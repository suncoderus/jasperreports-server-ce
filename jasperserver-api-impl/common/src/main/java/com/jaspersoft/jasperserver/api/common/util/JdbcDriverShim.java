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
package com.jaspersoft.jasperserver.api.common.util;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Shim class for java.sql.Driver.
 * It is used to load any jdbc driver in runtime.
 *
 * We could not register any jdbc driver which was loaded in runtime
 * in java.sql.DriverManager because it only accepts drivers which was loaded by system classloader.
 *
 * Thus we use this class (which is loaded with application, using system class loader)
 * to register it in DriverManager. Then instance of this class will redirect
 * all method calls to underlying real jdbc driver instance which was loaded in runtime (not by system class loader)
 *
 *
 * @author Sergey Prilukin (sprilukin@jaspersoft.com)
 * @version $Id$
 */
public class JdbcDriverShim implements Driver {
    private Driver driver;

    public JdbcDriverShim(Driver driver) {
        this.driver = driver;
    }

    /*
     * For tests purposes only
     */
    public Driver getDriver() {
        return driver;
    }

    public boolean acceptsURL(String url) throws SQLException {
        return this.driver.acceptsURL(url);
    }

    public Connection connect(String url, Properties info) throws SQLException {
        return this.driver.connect(url, info);
    }

    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return this.driver.getPropertyInfo(url, info);
    }

    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        try {
            //This will not compile under jdk version < 1.7 if we will try
            //to directly call this.driver.getParentLogger()
            Method method = this.driver.getClass().getMethod("getParentLogger");
            return (Logger)method.invoke(this.driver);
        } catch (Exception e) {
            throw new SQLFeatureNotSupportedException(e);
        }
    }
}
