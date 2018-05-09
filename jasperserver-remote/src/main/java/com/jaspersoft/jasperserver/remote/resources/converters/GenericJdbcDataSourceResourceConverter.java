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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.dto.resources.AbstractClientJdbcDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class GenericJdbcDataSourceResourceConverter<JdbcDataSourceType extends JdbcReportDataSource, ClientJdbcDataSourceType extends AbstractClientJdbcDataSource<ClientJdbcDataSourceType>> extends ResourceConverterImpl<JdbcDataSourceType, ClientJdbcDataSourceType> {
    @Override
    protected JdbcDataSourceType resourceSpecificFieldsToServer(ClientJdbcDataSourceType clientObject, JdbcDataSourceType resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException {
        resultToUpdate.setConnectionUrl(clientObject.getConnectionUrl());
        resultToUpdate.setDriverClass(clientObject.getDriverClass());
        resultToUpdate.setUsername(clientObject.getUsername());
        resultToUpdate.setTimezone(clientObject.getTimezone());

        if (clientObject.getPassword() != null){
            resultToUpdate.setPassword(clientObject.getPassword());
        }
        return resultToUpdate;
    }

    @Override
    protected ClientJdbcDataSourceType resourceSpecificFieldsToClient(ClientJdbcDataSourceType client, JdbcDataSourceType serverObject, ToClientConversionOptions options) {
        client.setConnectionUrl(serverObject.getConnectionUrl());
        client.setDriverClass(serverObject.getDriverClass());
        client.setUsername(serverObject.getUsername());
        client.setTimezone(serverObject.getTimezone());
        return client;
    }
}
