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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ContentResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class BinaryDataResourceConverter implements ResourceConverter<Resource, ClientFile> {
    @javax.annotation.Resource
    private Set<String> fileResourceTypes;
    @javax.annotation.Resource
    private ResourceConverter<Resource, ClientFile> fileResourceConverter;
    @javax.annotation.Resource
    private ResourceConverter<Resource, ClientFile> contentResourceConverter;

    @Override
    public Class<ClientFile> getClientTypeClass() {
        return ClientFile.class;
    }

    @Override
    public ClientFile toClient(Resource serverObject, ToClientConversionOptions options) {
        if (serverObject instanceof FileResource){
            return fileResourceConverter.toClient(serverObject, options);
        } else if(serverObject instanceof ContentResource) {
            return contentResourceConverter.toClient(serverObject, options);
        } else {
            throw new IllegalStateException(getClass().getName() + " can't process server object of type " + serverObject.getResourceType());
        }
    }

    @Override
    public String getClientResourceType() {
        return ClientTypeHelper.extractClientType(getClientTypeClass());
    }

    @Override
    public Resource toServer(ClientFile clientObject, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        return toServer(clientObject, fileResourceTypes.contains(clientObject.getType().toString()) ? new FileResourceImpl() : new ContentResourceImpl(), options);
    }

    @Override
    public Resource toServer(ClientFile clientObject, Resource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        if (resultToUpdate instanceof FileResource || (resultToUpdate == null && fileResourceTypes.contains(clientObject.getType().name()))){
            return fileResourceConverter.toServer(clientObject, resultToUpdate, options);
        } else {
            return contentResourceConverter.toServer(clientObject, resultToUpdate, options);
        }
    }

    @Override
    public String getServerResourceType() {
        return null;
    }
}
