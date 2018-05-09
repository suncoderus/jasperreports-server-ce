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
package com.jaspersoft.jasperserver.remote.connection.datadiscovery;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ConnectionOperationTemplate<ConnectionDescriptorType, ConnectionType> {
    private final Connector<ConnectionType, ConnectionDescriptorType> connector;

    public ConnectionOperationTemplate(Connector<ConnectionType, ConnectionDescriptorType> connector) {
        this.connector = connector;
    }

    public Connector<ConnectionType, ConnectionDescriptorType> getConnector() {
        return connector;
    }


    public <R> R operateConnection(ConnectionDescriptorType connectionDescriptor, ConnectionOperator<R, ConnectionType> operator) {
        R result;
        ConnectionType connection = null;
        try {
            connection = connector.openConnection(connectionDescriptor);
            result = operator.operate(connection);
        } finally {
            if (connection != null) {
                connector.closeConnection(connection);
            }
        }
        return result;
    }

    public interface ConnectionOperator<ResultType, ConcreteConnectionType> {
        ResultType operate(ConcreteConnectionType connection);
    }
}
