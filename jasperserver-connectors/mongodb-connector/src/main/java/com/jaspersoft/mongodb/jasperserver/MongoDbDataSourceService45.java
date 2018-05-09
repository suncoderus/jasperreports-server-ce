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

package com.jaspersoft.mongodb.jasperserver;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.mongodb.connection.MongoDbConnection;
import com.jaspersoft.mongodb.connection.MongoDbConnectionManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class MongoDbDataSourceService45 implements ReportDataSourceService {
    private final static Logger logger = Logger.getLogger(MongoDbDataSourceService45.class);

    protected MongoDbConnection connection;

    private String mongoURI;

    private String username;

    private String password;

    private MongoDbConnectionManager connectionManager;

    /**
     * Returns the active connection to the pool
     */
    @Override
    public void closeConnection() {
        if (logger.isDebugEnabled()) {
            logger.debug("MongoDB connection close requested");
        }
        if (connectionManager != null && connection != null) {
            connectionManager.returnConnection(connection);
            connection = null;
            if (logger.isDebugEnabled()) {
                logger.debug("MongoDB connection returned");
            }
        }
    }

    /**
     * Creates a new connection base on the parameters set
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setReportParameterValues(Map parameters) {
        try {
            createConnection();
            parameters.put(JRParameter.REPORT_CONNECTION, connection);
        } catch (Exception e) {
            logger.error("Cannot set report parameter values for mongo URI " + mongoURI + "; Caused by: " + e.getMessage(), e);
        }
    }

    protected void createConnection() throws JRException {
        if (connection != null) {
            closeConnection();
        }
        connectionManager.setMongoURI(mongoURI);
        connectionManager.setUsername(username);
        connectionManager.setPassword(password);
        try {
            connection = connectionManager.borrowConnection();
            if (logger.isDebugEnabled()) {
                logger.debug("MongoDB connection created");
            }
        } catch (Exception e) {
            logger.error("Cannot create or borrow a connection for mongo URI " + mongoURI + "; Caused by: " + e.getMessage(), e);
            throw new JRException(e.getMessage());
        }
    }

    public void setMongoURI(String mongoURI) {
        this.mongoURI = mongoURI;
    }

    public String getMongoURI() {
        return mongoURI;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setConnectionManager(MongoDbConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public MongoDbConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
