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
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.ObjectPoolFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DbcpDataSource implements PooledDataSource {
	
	private final static Log log = LogFactory.getLog(DbcpDataSource.class);

	private final ObjectPool connectionPool;
	private final PoolingDataSource dataSource;
	
	public DbcpDataSource(ObjectPoolFactory objectPoolFactory, 
			String url, String username, String password) {
		this(objectPoolFactory, url, username, password, true, false);
	}

	public DbcpDataSource(ObjectPoolFactory objectPoolFactory,
			String url, String username, String password, 
			boolean defaultReadOnly, boolean defaultAutoCommit) {
		connectionPool = createPool(objectPoolFactory);
		createPoolableConnectionFactory(url, username, password, defaultReadOnly, defaultAutoCommit);

		dataSource = new PoolingDataSource(connectionPool);
        dataSource.setAccessToUnderlyingConnectionAllowed(true);
	}

	protected ObjectPool createPool(ObjectPoolFactory objectPoolFactory) {
		ObjectPool objectPool = objectPoolFactory.createPool();
		return objectPool;
	}

	protected void createPoolableConnectionFactory(String url, String username, String password,
			boolean defaultReadOnly, boolean defaultAutoCommit) {
		ConnectionFactory connectionFactory = new JdbcDriverManagerConnectionFactory(url, username, password);
		new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, 
				defaultReadOnly, defaultAutoCommit);
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void release() {
		try {
			connectionPool.close();
		} catch (Exception e) {
			log.error("Error while closing DBCP connection pool", e);
			throw new JSExceptionWrapper(e);
		}
	}

	public boolean isActive() {
		return connectionPool.getNumActive() > 0;
	}

}
