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
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class FileResourceConverter extends ResourceConverterImpl<FileResource, ClientFile>{
    @javax.annotation.Resource(name = "concreteRepository")
    protected RepositoryService repositoryService;

    @Override
    protected FileResource resourceSpecificFieldsToServer(ClientFile clientObject, FileResource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        if(resultToUpdate.getFileType() != null || resultToUpdate.isReference()){
            String type;
            if (resultToUpdate.isReference()){
                FileResource referenced = (FileResource)repositoryService.getResource(null, resultToUpdate.getReferenceURI());
                type = referenced.getFileType();
            } else {
                type = resultToUpdate.getFileType();
            }

            if (!type.equals(clientObject.getType().name())){
                // it's not allowed to change resource type
                throw new IllegalParameterValueException("type", clientObject.getType().name());
            }
        }

        if (!resultToUpdate.isReference()){
            resultToUpdate.setFileType(clientObject.getType().name());
        }

        if (clientObject.getContent() != null && !"".equals(clientObject.getContent())) {
            try {
                resultToUpdate.setData(DatatypeConverter.parseBase64Binary(clientObject.getContent()));
            } catch (IllegalArgumentException e) {
                throw new IllegalParameterValueException("content", "");
            }
            resultToUpdate.setReferenceURI(null);
            resultToUpdate.setFileType(clientObject.getType().name());
        }
        return resultToUpdate;
    }

    @Override
    protected ClientFile resourceSpecificFieldsToClient(ClientFile client, FileResource serverObject, ToClientConversionOptions options) {
        String type;
        if (serverObject.isReference()){
            FileResource referenced = (FileResource)repositoryService.getResource(null, serverObject.getReferenceURI());
            type = referenced.getFileType();
        } else {
            type = serverObject.getFileType();
        }
        ClientFile.FileType clientFileType;
        try{
            clientFileType = ClientFile.FileType.valueOf(type);
        }catch (Exception e){
            // if no appropriate client type in an enum, then let it be unspecified
            clientFileType  = ClientFile.FileType.unspecified;
        }
        client.setType(clientFileType);
        return client;
    }
}
