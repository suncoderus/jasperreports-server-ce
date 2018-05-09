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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import com.jaspersoft.jasperserver.api.common.util.TibcoDriverManager;
import org.apache.commons.dbcp.BasicDataSource;

public class BasicDataSourceWrapper extends BasicDataSource {

	private TibcoDriverManager tibcoDriverManager;
	
	public BasicDataSourceWrapper() {
        super();
        tibcoDriverManager = TibcoDriverManagerImpl.getInstance();
    }

    public Connection getConnection() throws java.sql.SQLException {
        Connection connection = super.getConnection();
        if (connection == null) {
            return connection;
        }
        tibcoDriverManager.unlockDSConnection(connection);
        return connection;
    }

    public java.sql.Connection getConnection(java.lang.String username, java.lang.String password) throws java.sql.SQLException {
        Connection connection = super.getConnection(username, password);
        if (connection == null) {
            return connection;
        }
        tibcoDriverManager.unlockDSConnection(connection);
        return connection;
    }

    @SuppressWarnings("unchecked")
	public <T> T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException {
    //    try { return (T) invokeMethod(basicDataSource != null?  basicDataSource : this, "unwrap", iface);
        try { return (T) invokeMethod(this, "unwrap", iface);
        } catch (Exception ex) { throw new SQLException(ex); }
    }

    public boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException {
    //    try { return (Boolean) invokeMethod(basicDataSource != null?  basicDataSource : this, "isWrapperFor", iface);
        try { return (Boolean) invokeMethod(this, "isWrapperFor", iface);
        } catch (Exception ex) { throw new SQLException(ex); }
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    //    try { return (Logger) invokeMethod(basicDataSource != null?  basicDataSource : this, "getParentLogger", null);
        try { return (Logger) invokeMethod(this, "getParentLogger", null);
        } catch (Exception ex) { throw new SQLFeatureNotSupportedException(ex); }
    }
    private static Object invokeMethod(Object object, String methodName, Object arg1) throws Exception {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (arg1 == null) return method.invoke(object);
                else return method.invoke(object, arg1);
            }
        }
        throw new Exception("Class " + object.getClass().getName() + " Invalid Method: " + methodName);
    }


}
