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

import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPoolFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DbcpDataSourceFactory implements PooledJdbcDataSourceFactory {
	private static final Log log = LogFactory.getLog(DbcpDataSourceFactory.class);

	private ObjectPoolFactory objectPoolFactory;
    private JdbcDriverService jdbcDriverService;

	public PooledDataSource createPooledDataSource(String driverClass, String url, 
			String username, String password) {
		registerDriver(driverClass);

		return new DbcpDataSource(objectPoolFactory, url, username, password);
	}

	public PooledDataSource createPooledDataSource(String driverClass,
			String url, String username, String password,
			boolean defaultReadOnly, boolean defaultAutoCommit) {
		registerDriver(driverClass);
		return new DbcpDataSource(objectPoolFactory, url, username, password, 
				defaultReadOnly, defaultAutoCommit);
	}

	protected void registerDriver(String driverClass) {
		try {
            jdbcDriverService.register(driverClass);
		} catch (Exception e) {
			log.error(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

	public ObjectPoolFactory getObjectPoolFactory() {
		return objectPoolFactory;
	}

	public void setObjectPoolFactory(ObjectPoolFactory genericObjectPoolFactory) {
		this.objectPoolFactory = genericObjectPoolFactory;
	}

    public void setJdbcDriverService(JdbcDriverService jdbcDriverService) {
        this.jdbcDriverService = jdbcDriverService;
    }
}
