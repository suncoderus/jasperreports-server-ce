/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.buildomatic.sql.ant;

import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.EncryptionProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.JDBCTask;
import org.apache.tools.ant.taskdefs.SQLExec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Vladimir Tsukur
 */
public class AdvancedSQLExec extends SQLExec {
	public static final String DEFAULT_ENC_BLOCK_SIZE = "16";
	public static final String DEFAULT_ENC_MODE = "AES/CBC/PKCS5Padding";

	/** Whether the task should cause the build to fail if it cannot connect to the database. */
    private boolean failOnConnectionError = true;

    /** The name of the property that receives <code>true</code> if there was a connection error. */
    private String connectionErrorOccurredProperty;

    /** The name of the property that receives SQL state error code. */
    private String errorSqlStateCodeProperty;

    /** The name of the property that receives native driver error code. */
    private String errorNativeCodeProperty;

    /** The name of the property that receives error message. */
    private String errorMessageProperty;

    /** Database connection. */
    private Connection connection;

    /**
     * Default constructor.
     */
    public AdvancedSQLExec() {
        // No operations.
    }

    /**
     * Overrides execution of task by testing connection before execution.
     *
     * @throws BuildException
     */
    @Override
    public void execute() throws BuildException {
        if (getConnection() == null) {
            return;
        }

        super.execute();
    }

    /**
     * Whether the task should cause the build to fail if it cannot connect to the database.
     *
     * @return <code>true</code> if task should cause the build to fail
     * if it cannot connect to the database; <code>false</code> otherwise.
     */
    public boolean isFailOnConnectionError() {
        return failOnConnectionError;
    }

    /**
     * Sets whether the task should cause the build to fail if it cannot connect to the database.
     *
     * @param failOnConnectionError <code>true</code> if task should cause the build to fail
     * if it cannot connect to the database; <code>false</code> otherwise.
     */
    public void setFailOnConnectionError(boolean failOnConnectionError) {
        this.failOnConnectionError = failOnConnectionError;
    }

    /**
     * Sets the name of the property that receives <code>true</code> if there was a connection error.
     *
     * @param connectionErrorOccurredProperty the name of the property that
     * receives <code>true</code> if there was a connection error.
     */
    public void setConnectionErrorOccurredProperty(String connectionErrorOccurredProperty) {
        this.connectionErrorOccurredProperty = connectionErrorOccurredProperty;
    }

    /**
     * Sets the name of the property that receives SQL state error code.
     *
     * @param errorSqlStateCodeProperty the name of the property that receives SQL state error code.
     */
    public void setErrorSqlStateCodeProperty(String errorSqlStateCodeProperty) {
        this.errorSqlStateCodeProperty = errorSqlStateCodeProperty;
    }

    /**
     * Sets the name of the property that receives native driver error code.
     *
     * @param errorNativeCodeProperty the name of the property that receives native driver error code.
     */
    public void setErrorNativeCodeProperty(String errorNativeCodeProperty) {
        this.errorNativeCodeProperty = errorNativeCodeProperty;
    }

    /**
     * Sets the name of the property that receives error message.
     *
     * @param errorMessageProperty the name of the property that receives error message.
     */
    public void setErrorMessageProperty(String errorMessageProperty) {
        this.errorMessageProperty = errorMessageProperty;
    }

    @Override
    protected Connection getConnection() {
        if (connection == null) {
            connection = establishConnection();
            if (!isValidRdbms(connection)) {
                connection = null;
            }
        }
        return connection;
    }

    private Connection establishConnection() throws BuildException {
        if (getUserId() == null) {
            throw new BuildException("UserId attribute must be set!", getLocation());
        }
        if (getPassword() == null) {
            throw new BuildException("Password attribute must be set!", getLocation());
        }
        if (getUrl() == null) {
            throw new BuildException("Url attribute must be set!", getLocation());
        }

        try {
            log("connecting to " + getUrl(), Project.MSG_VERBOSE);
            Properties info = new Properties();
            info.put("user", getUserId());

			String passwd = getPassword();
			if (EncryptionEngine.isEncrypted(passwd)) {
				//decrypt password
				KeystoreManager ksManager = KeystoreManager.getInstance();
				passwd = EncryptionEngine.decrypt(ksManager.getBuildKey(), passwd);
			}
            info.put("password", passwd);

            Connection conn = callGetDriver().connect(getUrl(), info);

            if (conn == null) {
                // Driver doesn't understand the URL
                throw new SQLException("No suitable Driver for " + getUrl());
            }

            conn.setAutoCommit(isAutocommit());
            return conn;
        }
        catch (SQLException e) {
            // failed to connect
            if (!isFailOnConnectionError()) {
                log("Failed to connect: " + e.getMessage(), Project.MSG_WARN);
                updatePropertiesFromSQLException(e);
                return null;
            }
            else {
                throw new BuildException(e, getLocation());
            }
        }
		catch (Exception e) {
			throw new BuildException(e, getLocation());
		}
    }

    private void updatePropertiesFromSQLException(SQLException exception) {
        setProperty(connectionErrorOccurredProperty, "true");
        setProperty(errorSqlStateCodeProperty, exception.getSQLState());
        setProperty(errorNativeCodeProperty, String.valueOf(exception.getErrorCode()));
        setProperty(errorMessageProperty, exception.getMessage());
    }

    protected void setProperty(String name, String value) {
        if (name != null) {
            getProject().setNewProperty(name, value);
        }
    }

    private Driver callGetDriver() {
        try {
            Method getDriverMethod = JDBCTask.class.getDeclaredMethod("getDriver");
            getDriverMethod.setAccessible(true);
            return (Driver) getDriverMethod.invoke(this);
        }
        catch (NoSuchMethodException e) {
            throw new BuildException("Method getDriver() not found in Ant task class hierarchy", e, getLocation());
        }
        catch (IllegalAccessException e) {
            throw new BuildException("Illegal access of getDriver() method", e, getLocation());
        }
        catch (InvocationTargetException e) {
            throw new BuildException("Error invoking getDriver() method", e, getLocation());
        }
    }

}
