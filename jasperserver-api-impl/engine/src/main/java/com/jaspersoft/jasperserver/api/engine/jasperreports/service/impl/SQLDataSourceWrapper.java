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

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class SQLDataSourceWrapper implements DataSource {

    private DataSource basicDataSource;

    public SQLDataSourceWrapper(DataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }

    public Connection getConnection() throws SQLException {
        Connection connection =  basicDataSource.getConnection();
        TibcoDriverManagerImpl.getInstance().unlockDSConnection(connection);
        return connection;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = basicDataSource.getConnection(username, password);
        TibcoDriverManagerImpl.getInstance().unlockDSConnection(connection);
        return connection;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try { return (T) invokeMethod(basicDataSource, "unwrap", iface);
        } catch (Exception ex) { throw new SQLException(ex); }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        try { return (Boolean) invokeMethod(basicDataSource, "isWrapperFor", iface);
        } catch (Exception ex) { throw new SQLException(ex); }
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        try { return (Logger) invokeMethod(basicDataSource, "getParentLogger", null);
        } catch (Exception ex) { throw new SQLFeatureNotSupportedException(ex); }
    }

    public int getLoginTimeout() throws SQLException {
        return basicDataSource.getLoginTimeout();
    }

    public java.io.PrintWriter getLogWriter() throws SQLException {
        return basicDataSource.getLogWriter();
    }

    public void setLoginTimeout(int loginTimeout) throws SQLException {
        basicDataSource.setLoginTimeout(loginTimeout);
    }

    public void setLogWriter(java.io.PrintWriter logWriter) throws SQLException {
        basicDataSource.setLogWriter(logWriter);
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
