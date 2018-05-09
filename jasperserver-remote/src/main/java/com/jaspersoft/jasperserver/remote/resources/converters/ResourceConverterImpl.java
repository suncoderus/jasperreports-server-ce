/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.attachments.AttachmentsProcessor;
import com.jaspersoft.jasperserver.remote.resources.validation.ResourceValidator;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CalendarFormatProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.text.DateFormat;
import java.util.List;
import java.util.Set;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public abstract class ResourceConverterImpl<ResourceType extends Resource, ClientType extends ClientResource<ClientType>> implements ResourceConverter<ResourceType, ClientType> {
    @javax.annotation.Resource(name = "mappingResourceFactory")
    protected ResourceFactory objectFactory;

    @javax.annotation.Resource(name = "isoCalendarFormatProvider")
    protected CalendarFormatProvider calendarFormatProvider;

    @javax.annotation.Resource(name = "concretePermissionsService")
    private PermissionsService permissionsService;

    @javax.annotation.Resource
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;

    private ServerResourceTypeExtractor serverResourceTypeExtractor;

    private ClientTypeHelper<ClientType> clientTypeHelper;

    @javax.annotation.Resource(name = "basicResourceValidator")
    private ResourceValidator defaultValidator;
    @javax.annotation.Resource(name = "beanValidator")
    private Validator validator;

    public ResourceConverterImpl(){
        clientTypeHelper = new ClientTypeHelper(this.getClass());
        serverResourceTypeExtractor = new ServerResourceTypeExtractor(this.getClass());
    }

    // object factory returns correct type of resource. So, cast below is safe
    @SuppressWarnings("unchecked")
    protected ResourceType getNewResourceInstance() {
        return (ResourceType) objectFactory.newResource(null, getServerResourceType());
    }

    protected DateFormat getDateTimeFormat() {
        return calendarFormatProvider.getDatetimeFormat();
    }

    public String getServerResourceType() {
        return serverResourceTypeExtractor.getServerResourceType();
    }

    @Override
    public ResourceType toServer(ClientType clientObject, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        return toServer(clientObject, null, options);
    }

    @Override
    public ResourceType toServer(ClientType clientObject, ResourceType resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final Set<ConstraintViolation<ClientType>> constraintViolations = validator.validate(clientObject);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException("Resource validation failed.", constraintViolations);
        }
        ResourceType resource = genericFieldsToServer(clientObject, resultToUpdate, options);
        resource = resourceSpecificFieldsToServer(clientObject, resource, options);
        if (options != null && options.getAttachments() != null) {
            // impossible to build
            @SuppressWarnings("unchecked")
            AttachmentsProcessor<ResourceType> attachmentsProcessor = genericTypeProcessorRegistry.getTypeProcessor(getServerResourceType(), AttachmentsProcessor.class, false);
            if (attachmentsProcessor != null) {
                resource = attachmentsProcessor.processAttachments(resource, options.getAttachments());
            }
        }
        if(options == null || !options.isSuppressValidation()){
            validateResource(resource, options != null && options.isSkipRepoFieldsValidation());
        }
        return resource;
    }

    protected void validateResource(ResourceType resource, boolean skipRepoFieldsValidation) {
        ResourceValidator<ResourceType> validator = genericTypeProcessorRegistry.getTypeProcessor(resource.getResourceType(), ResourceValidator.class, false);
        (validator != null ? validator : defaultValidator).validate(resource, skipRepoFieldsValidation);
    }

    protected ResourceType genericFieldsToServer(ClientType clientObject, ResourceType resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        if (resultToUpdate == null) {
            resultToUpdate = getNewResourceInstance();
            resultToUpdate.setVersion(Resource.VERSION_NEW);
        } else {
            resultToUpdate.setVersion(clientObject.getVersion() == null || (options != null && options.isResetVersion()) ?
                    Resource.VERSION_NEW : clientObject.getVersion());
        }
        resultToUpdate.setURIString(clientObject.getUri());
        resultToUpdate.setDescription(clientObject.getDescription());
        resultToUpdate.setLabel(clientObject.getLabel());
        return resultToUpdate;
    }

    protected abstract ResourceType resourceSpecificFieldsToServer(ClientType clientObject, ResourceType resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException;

    @Override
    public ClientType toClient(ResourceType serverObject, ToClientConversionOptions options) {
        final ClientType client = genericFieldsToClient(getNewClientObjectInstance(), serverObject, options);
        return resourceSpecificFieldsToClient(client, serverObject, options);
    }

    protected ClientType genericFieldsToClient(ClientType client, ResourceType serverObject, ToClientConversionOptions options) {
        final DateFormat dateTimeFormatter = getDateTimeFormat();
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        dateTimeFormatter.setTimeZone(TimeZoneContextHolder.getTimeZone());
        if (serverObject.getCreationDate() != null) {
            client.setCreationDate(dateTimeFormatter.format(serverObject.getCreationDate()));
        }
        client.setDescription(serverObject.getDescription());
        client.setLabel(serverObject.getLabel());
        if (serverObject.getUpdateDate() != null) {
            client.setUpdateDate(dateTimeFormatter.format(serverObject.getUpdateDate()));
        }
        client.setUri(serverObject.getURIString());
        client.setVersion(serverObject.getVersion());
        client.setPermissionMask(permissionsService.getEffectivePermission(serverObject, authentication).getPermissionMask());
        return client;
    }

    protected abstract ClientType resourceSpecificFieldsToClient(ClientType client, ResourceType serverObject, ToClientConversionOptions options);

    protected ClientType getNewClientObjectInstance() {
        return clientTypeHelper.getNewClientObjectInstance();
    }

    // Client object class is extracted from real implementation class by reflection. So, cast is safe.
    @SuppressWarnings("unchecked")
    public Class<ClientType> getClientTypeClass() {
        return clientTypeHelper.getClientClass();
    }

    protected final ResourceReference findReference(List<ResourceReference> references, String uri) {
        ResourceReference result = null;
        if (references != null) {
            for (ResourceReference reference : references) {
                if (reference.getTargetURI().equals(uri)) {
                    result = reference;
                }
            }
        }
        return result;
    }

    public String getClientResourceType() {
        return clientTypeHelper.getClientResourceType();
    }
}
