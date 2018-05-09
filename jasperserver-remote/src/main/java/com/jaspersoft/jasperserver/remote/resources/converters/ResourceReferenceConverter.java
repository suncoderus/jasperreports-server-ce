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

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceable;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientUriHolder;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ResourceReferenceConverter.java 28864 2013-02-21 11:27:18Z ykovalchyk $
 */
public class ResourceReferenceConverter<T extends ClientReferenceable> {
    protected final ResourceConverterProvider resourceConverterProvider;
    protected final RepositoryService repositoryService;
    protected final List<ClientReferenceRestriction> restrictions = new ArrayList<ClientReferenceRestriction>();
    protected ConfigurationBean configurationBean;
    protected PermissionsService permissionsService;

    public ResourceReferenceConverter(ResourceConverterProvider resourceConverterProvider,
            RepositoryService repositoryService, PermissionsService permissionsService, ConfigurationBean configurationBean, ClientReferenceRestriction... restriction) {
        this.repositoryService = repositoryService;
        this.permissionsService = permissionsService;
        this.resourceConverterProvider = resourceConverterProvider;
        if (restriction != null) {
            restrictions.addAll(Arrays.asList(restriction));
        }
        this.configurationBean = configurationBean;
    }

    public ResourceReferenceConverter<T> addReferenceRestriction(ClientReferenceRestriction restriction) {
        restrictions.add(restriction);
        return this;
    }

    /**
     * Converts ResoruceReferrence to client type object.
     *
     *
     * @param serverObject ResourceReference instance
     * @param options - to client conversion options
     * @return client referenceable object
     * @throws ClassCastException if server side local resource of given ResourceReference is of wrong type.
     */
    @SuppressWarnings("unchecked")
    public T toClient(ResourceReference serverObject, ToClientConversionOptions options) throws ClassCastException {
        ClientUriHolder result = null;
        if (serverObject != null) {
            if ((options != null && options.isExpanded())) {
                try {
                    Resource localResource;

                    if (serverObject.isLocal()) {
                        localResource = serverObject.getLocalResource();
                    } else {
                        localResource = repositoryService.getResource(ExecutionContextImpl.getRuntimeExecutionContext(), serverObject.getReferenceURI());
                    }

                    result = resourceConverterProvider.getToClientConverter(localResource).toClient(localResource, options);
                } catch (AccessDeniedException e) {
                    result = new ClientReference(serverObject.getTargetURI());
                }
            } else {
                result = new ClientReference(serverObject.getTargetURI());
            }
        }
        return (T) result;
    }

    public ResourceReference toServer(T clientObject,ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        return toServer(clientObject, null, options);
    }

    public ResourceReference toServer(T clientObject, ResourceReference resultToUpdate, ToServerConversionOptions options)
            throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final ResourceReference resourceReference;
        if (clientObject == null) {
            resourceReference = null;
        } else if (clientObject.getClass() == ClientReference.class) {
            resourceReference = toServerReference(clientObject, resultToUpdate, options);
        } else if (clientObject instanceof ClientResource) {
            if (options == null || !options.isAllowReferencesOnly()) {
                if (restrictions != null){
                    for (ClientReferenceRestriction restriction : restrictions) {
                        restriction.validateReference((ClientResource)clientObject);
                    }
                }
                resourceReference = toServerLocalResource((ClientResource) clientObject, resultToUpdate, options);
            } else {
                throw new IllegalParameterValueException("reference", clientObject.toString());
            }
        } else {
            // shouldn't happen
            throw new IllegalParameterValueException("References of type "
                    + ClientTypeHelper.extractClientType(clientObject.getClass()) + " are not supported");
        }
        return resourceReference;
    }

    protected ResourceReference toServerReference(T clientObject, ResourceReference resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        ResourceReference result;
        final String uriFromClient = clientObject.getUri();
        if (resultToUpdate != null && resultToUpdate.getTargetURI().equals(uriFromClient)) {
            // no update is needed. Existing ResourceReference points to the same URI
            result = resultToUpdate;
        } else {
            // if it is local resource, reference with local resource must be returned or update will fail
            // NOTE: it is always preferable to pass resultToUpdate to get actual resource from there to avoid rereading and possible errors.
            String ownersUri = options == null ? null : options.getOwnersUri();
            Resource referencedResource = validateAndGetReference(uriFromClient, ownersUri);
            final boolean local = ownersUri != null && uriFromClient.startsWith(ownersUri + "_files" + Folder.SEPARATOR);
            result = resultToUpdate == null ? new ResourceReference(referencedResource) : resultToUpdate;
            if (!local) {
                result.setReference(uriFromClient);
            }
        }
        return result;
    }

    protected Resource validateAndGetReference(String referenceUri, String ownersUri) throws IllegalParameterValueException {
        if (referenceUri == null) {
            throw new IllegalParameterValueException("resourceReference.uri", "null");
        }

        if (!isAssignable(ownersUri, referenceUri)){
            throw new IllegalParameterValueException("resourceReference.uri", referenceUri);
        }
        // we need to update reference
        final Resource resource = repositoryService.getResource(ExecutionContextImpl.getRuntimeExecutionContext(), referenceUri);
        if (resource == null) {
            // resource with such URI doesn't exist
            throw new IllegalParameterValueException("Referenced resource doesn't exist", "resourceReference.uri", referenceUri);
        } else if (!restrictions.isEmpty()) {
            final ClientResource clientTargetObject = resourceConverterProvider.getToClientConverter(resource).toClient(resource, null);
            for (ClientReferenceRestriction restriction : restrictions) {
                restriction.validateReference(clientTargetObject);
            }
        }
        return resource;
    }

    protected ResourceReference toServerLocalResource(ClientResource clientObject, ResourceReference resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        ResourceReference result;
        Resource localResource = resourceConverterProvider.getToServerConverter(clientObject).toServer(clientObject, options);
        if (localResource.getName() == null && clientObject.getLabel() != null) {
            localResource.setName(clientObject.getLabel().replaceAll(configurationBean.getResourceIdNotSupportedSymbols(), "_"));
        }
        if (options != null && options.isResetVersion()){
            localResource.setVersion(Resource.VERSION_NEW);
        }
        if (resultToUpdate != null) {
            result = resultToUpdate;
            result.setLocalResource(localResource);
        } else {
            result = new ResourceReference(localResource);
        }
        return result;
    }

    protected final boolean isAssignable(String parent, String child){
        boolean res = true;
        if (parent != null){
            int suffixIndex = child.lastIndexOf("_files");
            if (suffixIndex > 0){
                String possibleParentUri = child.substring(0, suffixIndex);
                if (!possibleParentUri.equals(parent)){
                    res = repositoryService.getResource(null, possibleParentUri) == null;
                }
            }
        }
        return res;
    }

    public static class ReferenceClassRestriction implements ClientReferenceRestriction {
        protected final Class<? extends ClientReferenceable> targetClientClass;

        public ReferenceClassRestriction(Class<? extends ClientReferenceable> targetClientClass) {
            this.targetClientClass = targetClientClass;
        }

        @Override
        public void validateReference(ClientResource clientResource) throws IllegalParameterValueException {
            if (!targetClientClass.isAssignableFrom(clientResource.getClass())) {
                throw new IllegalParameterValueException("Reference target is of wrong type",
                        "resourceReference.uri",
                        clientResource.getUri(),
                        ClientTypeHelper.extractClientType(clientResource.getClass()));
            }
        }
    }

    public static class FileTypeRestriction implements ClientReferenceRestriction{
        private final List<ClientFile.FileType> fileTypes;
        private final String fieldName;

        public FileTypeRestriction(ClientFile.FileType fileType){
            this(fileType, null);
        }

        public FileTypeRestriction(ClientFile.FileType fileType, String fieldName){
            this(fieldName, fileType);
        }

        public FileTypeRestriction(String fieldName, ClientFile.FileType ... fileTypes){
            this.fileTypes = Arrays.asList(fileTypes);
            this.fieldName = fieldName;
        }

        @Override
        public void validateReference(ClientResource clientResource) throws IllegalParameterValueException {
            if(clientResource instanceof ClientFile && !fileTypes.contains(((ClientFile)clientResource).getType())){
                final IllegalParameterValueException illegalParameterValueException =
                        new IllegalParameterValueException("Referenced " + (fieldName != null ? fieldName : "file") +
                                " is of wrong type. File type is expected to be " + fileTypes + " but is ["
                                + ((ClientFile)clientResource).getType() + "]",
                        fieldName != null ? fieldName : "file.type",
                        ((ClientFile) clientResource).getType().name(),
                                fileTypes.size() > 1 ? fileTypes.toString() : fileTypes.get(0).toString());
                illegalParameterValueException.getErrorDescriptor().setErrorCode("incompatible.file.type");
                throw illegalParameterValueException;
            }
        }
    }
}
