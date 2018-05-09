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
package com.jaspersoft.jasperserver.remote.connection.jdbc;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.connection.datadiscovery.Connector;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import org.springframework.stereotype.Service;

import java.sql.Connection;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class ClientJdbcConnector<ConnectionDescriptionType extends ClientResource<ConnectionDescriptionType>> implements Connector<Connection, ConnectionDescriptionType> {

    @javax.annotation.Resource
    private ResourceConverterProvider resourceConverterProvider;
    @javax.annotation.Resource
    private JdbcConnector jdbcConnector;

    @Override
    public Connection openConnection(ConnectionDescriptionType connectionDescriptor) {
        return jdbcConnector.openConnection(convert(connectionDescriptor));
    }

    public void testConnection(ConnectionDescriptionType connectionDescriptor){
        jdbcConnector.testConnection(convert(connectionDescriptor));
    }

    protected Resource convert(ConnectionDescriptionType connectionDescriptor){
        return resourceConverterProvider
                .getToServerConverter(connectionDescriptor)
                .toServer(connectionDescriptor, ToServerConversionOptions.getDefault().setSuppressValidation(true));
    }

    @Override
    public void closeConnection(Connection connection) {
        jdbcConnector.closeConnection(connection);
    }
}
