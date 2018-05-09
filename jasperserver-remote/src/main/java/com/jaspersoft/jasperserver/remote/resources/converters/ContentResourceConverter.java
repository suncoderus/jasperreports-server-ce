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
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
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
public class ContentResourceConverter extends ResourceConverterImpl<ContentResource, ClientFile>{

    @Override
    protected ContentResource resourceSpecificFieldsToServer(ClientFile clientObject, ContentResource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        if(resultToUpdate.getFileType() != null &&
                (!resultToUpdate.getFileType().equals(clientObject.getType().name()) &&
                        !(ContentResource.TYPE_UNSPECIFIED.equals(resultToUpdate.getFileType()) && clientObject.getType().equals(ClientFile.FileType.unspecified)))){
            // it's not allowed to change resource type
            throw new IllegalParameterValueException("type", clientObject.getType().name());
        }
        if (ClientFile.FileType.unspecified.equals(clientObject.getType())){
            resultToUpdate.setFileType(ContentResource.TYPE_UNSPECIFIED);
        } else {
            resultToUpdate.setFileType(clientObject.getType().name());
        }

        if (clientObject.getContent() != null && !"".equals(clientObject.getContent())) {
            try {
                resultToUpdate.setData(DatatypeConverter.parseBase64Binary(clientObject.getContent()));
            } catch (IllegalArgumentException e) {
                throw new IllegalParameterValueException("content", "");
            }
        }

        return resultToUpdate;
    }

    @Override
    protected ClientFile resourceSpecificFieldsToClient(ClientFile client, ContentResource serverObject, ToClientConversionOptions options) {
        if (ContentResource.TYPE_UNSPECIFIED.equals(serverObject.getFileType())){
            client.setType(ClientFile.FileType.unspecified);
        } else {
            client.setType(ClientFile.FileType.valueOf(serverObject.getFileType()));
        }
        return client;
    }
}
