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

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.SelfCleaningFileResourceDataWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.resources.ClientPropertiesFile;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.NotAcceptableException;
import com.jaspersoft.jasperserver.remote.services.SingleRepositoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import static com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceBase.TYPE_RESOURCE_BUNDLE;

/**
 * @author askorodumov
 * @version $Id$
 */
@Service
@VirtualResourceConverter
public class PropertiesFileConverter extends ResourceConverterImpl<FileResource, ClientPropertiesFile> {
    @javax.annotation.Resource(name = "concreteRepository")
    protected RepositoryService repositoryService;
    @Resource
    protected FileResourceConverter fileResourceConverter;
    @javax.annotation.Resource
    private SingleRepositoryService singleRepositoryService;

    @Override
    protected FileResource resourceSpecificFieldsToServer(ClientPropertiesFile clientObject, FileResource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ClientPropertiesFile resourceSpecificFieldsToClient(ClientPropertiesFile client, FileResource serverObject, ToClientConversionOptions options) {
        FileResource resource;
        if (serverObject.isReference()){
            resource = (FileResource)repositoryService.getResource(null, serverObject.getReferenceURI());
        } else {
            resource = serverObject;
        }

        String type = resource.getFileType();

        if (!TYPE_RESOURCE_BUNDLE.equals(type)) {
            throw new NotAcceptableException(
                    ResourceMediaType.RESOURCE_MEDIA_TYPE_PREFIX + ResourceMediaType.PROPERTIES_FILE_CLIENT_TYPE);
        }

        FileResourceData data = singleRepositoryService.getFileResourceData(resource);
        FileResourceData wrapper = new SelfCleaningFileResourceDataWrapper(data);

        Properties properties = new Properties();
        try {
            properties.load(wrapper.getDataStream());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        LinkedList<ClientProperty> propertiesList = new LinkedList<ClientProperty>();
        for (String key : properties.stringPropertyNames()) {
            ClientProperty property = new ClientProperty();
            property.setKey(key);
            property.setValue(properties.getProperty(key));
            propertiesList.add(property);
        }

        client.setProperties(propertiesList);

        return client;
    }
}
