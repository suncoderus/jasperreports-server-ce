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

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.dto.resources.ClientXmlaConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class XmlaConnectionResourceConverter extends ResourceConverterImpl<XMLAConnection, ClientXmlaConnection> {
    @Override
    protected XMLAConnection resourceSpecificFieldsToServer(ClientXmlaConnection clientObject, XMLAConnection resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        resultToUpdate.setURI(clientObject.getUrl());
        resultToUpdate.setCatalog(clientObject.getCatalog());
        resultToUpdate.setDataSource(clientObject.getDataSource());
        final String password = clientObject.getPassword();
        if (password != null) {
            // update password if it is not null only
            // null password means "not changed"
            resultToUpdate.setPassword(password);
        }
        resultToUpdate.setUsername(clientObject.getUsername());
        return resultToUpdate;
    }

    @Override
    protected ClientXmlaConnection resourceSpecificFieldsToClient(ClientXmlaConnection client, XMLAConnection serverObject, ToClientConversionOptions options) {
        client.setUrl(serverObject.getURI());
        client.setCatalog(serverObject.getCatalog());
        client.setDataSource(serverObject.getDataSource());
        client.setUsername(serverObject.getUsername());
        return client;
    }
}
