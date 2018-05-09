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

package com.jaspersoft.cassandra.jasperserver;

import java.util.Map;

import com.jaspersoft.connectors.cassandra.connection.JSCassandraConnection;
import com.jaspersoft.connectors.cassandra.connection.CassandraConnectionManager;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.CustomReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;

import org.apache.log4j.Logger;

/**
 * 
 * @author Eric Diaz
 * 
 */
public class CassandraDataSourceService implements ReportDataSourceService, CustomReportDataSourceService {
    private final static Logger logger = Logger.getLogger(CassandraDataSourceService.class);

    public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}
	private String hostname;
    private Integer port;
    private String keyspace;
    private String username;
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	private String password;
    
    
    
    
    @Override
    public boolean testConnection() throws JRException {
        if (logger.isDebugEnabled()) {
            logger.debug("Testing connection");
        }
        try {
            createConnection();
            if (connection == null) {
                return false;
            }
            return connection.test();
        } finally {
            closeConnection();
        }
    }
        protected JSCassandraConnection connection;

        private CassandraConnectionManager connectionManager;

        /**
         * Returns the active connection to the pool
         */
        @Override
        public void closeConnection() {
            if (logger.isDebugEnabled()) {
                logger.debug("Cassandra connection close requested");
            }
            if (connectionManager != null && connection != null) {
                //connectionManager.returnConnection(connection);
                connection = null;
                if (logger.isDebugEnabled()) {
                    logger.debug("Cassandra connection returned");
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
                logger.error(e);
            }
        }

        protected void createConnection() throws JRException {
            if (connection != null) {
                closeConnection();
            }
            
            try {
            	if(connectionManager==null){
            		connectionManager = new CassandraConnectionManager(hostname, port, keyspace);
            	} else {
            		connectionManager.setHostname(hostname);
            		connectionManager.setPort(port);
            		connectionManager.setKeyspace(keyspace);
            	}
            	connectionManager.setUsername(username);
            	connectionManager.setPassword(password);
            	if(connectionManager!=null){
            			connection = connectionManager.borrowConnection();
        		}
            } catch (Exception e) {
                logger.error(e);
                throw new JRException(e.getMessage());
            }
        }

        public void setConnectionManager(CassandraConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }

        public CassandraConnectionManager getConnectionManager() {
            return connectionManager;
        }

}
