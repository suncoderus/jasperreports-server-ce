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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@link DataSource} decorator providing the ability to take corrective actions in case of failure
 * during {@link #getConnection()} or {@link #getConnection(String, String)} calls.
 * 
 * <p>If {@link SQLException} is thrown during any of the two getConnection calls
 * an attempt to make corrective actions is taken by calling the {@link #recover(SQLException)} method.
 * If the method returns <code>true</code> then another final attempt to obtain connection is made.
 * If the final attempt fails no corrective actions are taken and {@link SQLException} is propagated to the caller.
 * If the {@link #recover(SQLException)} method returns <code>false</code> then no other attempt to obtain a connection is made
 * and the exception is propagated to the caller.
 *
 * <p>Extend this class and override the {@link #recover(SQLException)} method to provide your own recovery behavior.
 * 
 * @see #recover(SQLException)
 */
public abstract class AutoRecoveringDataSourceDecorator implements DataSource {

    private static final Log log = LogFactory.getLog(AutoRecoveringDataSourceDecorator.class);

    private final DataSource target;

    public AutoRecoveringDataSourceDecorator(DataSource target) {
        this.target = target;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return target.getConnection();
        } catch (SQLException e) {
            if (recover(e)) {
                log.warn("Cannot create connection due to unreachable server, retrying after setting up firewall rules");
                return getConnection2();
            }
            throw new SQLException("Cannot create connection", e);
        }
    }

    private Connection getConnection2() throws SQLException {
        try {
            return target.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Cannot create connection after setting up firewall rules", e);
        }
    }

    /**
     * Override this method if you want to take corrective actions during failure to obtain a connection.
     * Return <code>true</code> if any corrective actions are executed or <code>false</code> otherwise.
     * Default implementation takes no corrective action and always returns false.
     * 
     * @param cause - the cause of the failure
     * @return <code>true</code> if corrective actions were executed during this call, <code>false</code> otherwise
     */
    protected boolean recover(SQLException cause) {
        return false; // no corrective actions are taken
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            return target.getConnection(username, password);
        } catch (SQLException e) {
            if (recover(e)) {
                log.warn("Cannot create connection due to unreachable server, retrying after setting up firewall rules");
                return getConnection2(username, password);
            }
            throw new SQLException("Cannot create connection", e);
        }
    }

    private Connection getConnection2(String username, String password) throws SQLException {
        try {
            return target.getConnection(username, password);
        } catch (SQLException e) {
            throw new SQLException("Cannot create connection after setting up firewall rules", e);
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return target.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        target.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        target.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return target.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return target.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return target.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return target.isWrapperFor(iface);
    }

}