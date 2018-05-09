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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.ReferencedResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Component
public class ResourceLookupContextStrategy implements
        ContextManagementStrategy<ClientResourceLookup, ClientResourceLookup>,
        ContextQueryExecutor<Object, ClientResourceLookup>, GenericTypeMetadataBuilder<ClientResourceLookup>,
        ContextParametrizedMetadataBuilder<ClientResourceLookup, Object> {
    private static final String INNER_UUID = "innerUuid";
    @javax.annotation.Resource(name = "concreteRepository")
    private RepositoryService repository;
    @javax.annotation.Resource
    private ResourceConverterProvider resourceConverterProvider;
    @javax.annotation.Resource
    private ContextsManager contextsManager;
    @javax.annotation.Resource
    private ProfileAttributesResolver profileAttributesResolver;

    protected ClientResource getFullClientResource(ClientResourceLookup resourceLookup){
        ClientResource clientResource = null;
        if(resourceLookup != null){
            if (resourceLookup.getUri() == null) {
                throw new MandatoryParameterNotFoundException("uri");
            }
            if(profileAttributesResolver.containsAttribute(resourceLookup.getUri())){
                throw new IllegalParameterValueException("uri", resourceLookup.getUri());
            }
            final Resource repositoryResource = repository
                    .getResource(ExecutionContextImpl.getRuntimeExecutionContext(), resourceLookup.getUri());
            if(repositoryResource != null){
                String clientType = resourceLookup.getResourceType();
                ToClientConverter<? super Resource, ? extends ClientResource, ToClientConversionOptions> toClientConverter;
                if(clientType != null){
                    toClientConverter = resourceConverterProvider.getToClientConverter(repositoryResource.getResourceType(), clientType);
                } else {
                    toClientConverter = resourceConverterProvider.getToClientConverter(repositoryResource.getResourceType());
                    clientType = toClientConverter.getClientResourceType();
                }
                if(contextsManager.getConnectionDescriptionClass(clientType) == null){
                    // this client type isn't supported. Let's fail here
                    throw new RemoteException(new ErrorDescriptor().setMessage("Resource of type [" + clientType +
                            "] is not supported by this endpoint").setParameters(clientType).setErrorCode("not.supported.resource.type"));
                }
                clientResource = toClientConverter
                        .toClient(repositoryResource, ToClientConversionOptions.getDefault());
            } else {
                throw new ReferencedResourceNotFoundException(resourceLookup.getUri(), "uri");
            }
        }
        return clientResource;
    }

    @Override
    public boolean isMetadataSupported(ClientResourceLookup resourceLookup, String metadataClientType){
        return "repository.resourceLookup.metadata".equalsIgnoreCase(metadataClientType) ||
                contextsManager.isMetadataSupported(getFullClientResource(resourceLookup), metadataClientType);
    }

    @Override
    public String getMetadataClientResourceType(ClientResourceLookup connectionDescription) {
        return contextsManager.getMetadataClientResourceType(getFullClientResource(connectionDescription));
    }

    protected UUID getInnerUuid(Map<String, Object> data){
        final Object uuid = data.get(INNER_UUID);
        if(uuid instanceof UUID){
            return (UUID) uuid;
        } else {
            throw new ResourceNotFoundException();

        }
    }

    @Override
    public ClientResourceLookup createContext(ClientResourceLookup contextDescription, Map<String, Object> data) throws IllegalParameterValueException {
        final UUID uuid = contextsManager.createConnection(getFullClientResource(contextDescription));
        data.put(INNER_UUID, uuid);
        return contextDescription;
    }

    @Override
    public void deleteContext(ClientResourceLookup contextDescription, Map<String, Object> data) {
        contextsManager.removeConnection(getInnerUuid(data));
    }

    @Override
    public ClientResourceLookup getContextForClient(ClientResourceLookup contextDescription, Map<String, Object> data) {
        // nothing to secure in resource lookup
        return contextDescription;
    }

    @Override
    public Object build(ClientResourceLookup context, Map<String, String[]> options, Map<String, Object> contextData) {
        return contextsManager.getConnectionMetadata(getInnerUuid(contextData), options);
    }

    @Override
    public Object build(ClientResourceLookup connection, Object options, Map<String, Object> contextData) {
        return contextsManager.getConnectionMetadata(getInnerUuid(contextData), options);
    }

    @Override
    public Object executeQuery(Object query, ClientResourceLookup connection, Map<String, String[]> queryParameters, Map<String, Object> data) {
        return contextsManager.executeQuery(getInnerUuid(data), query, queryParameters);
    }

    @Override
    public Object executeQueryForMetadata(Object query, ClientResourceLookup connection, Map<String, Object> data) {
        return contextsManager.executeQueryForMetadata(getInnerUuid(data), query);
    }
}
