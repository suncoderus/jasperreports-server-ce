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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.common.util.TibcoDriverManager;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import java.sql.Connection;

public class JdbcDriverManagerConnectionFactory extends DriverManagerConnectionFactory {

    public JdbcDriverManagerConnectionFactory(java.lang.String connectUri, java.lang.String uname, java.lang.String passwd) {
        super(connectUri, uname, passwd);
    }

    public Connection createConnection() throws java.sql.SQLException {
        TibcoDriverManager tibcoDriverManager = TibcoDriverManagerImpl.getInstance();
        Connection connection = super.createConnection();
        tibcoDriverManager.unlockConnection(connection);
        return connection;

    }


}
