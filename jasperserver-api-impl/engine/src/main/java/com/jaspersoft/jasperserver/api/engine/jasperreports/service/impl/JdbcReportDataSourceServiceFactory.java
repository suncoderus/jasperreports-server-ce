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

import java.io.File;
import java.util.*;
import javax.sql.DataSource;

import com.jaspersoft.jasperserver.api.common.util.TibcoDriverManager;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectCache;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JdbcReportDataSourceServiceFactory implements ReportDataSourceServiceFactory {

    private static final Log log = LogFactory.getLog(JdbcReportDataSourceServiceFactory.class);


	protected static class PooledDataSourcesCache {

        protected static class DataSourceEntry extends PooledObjectEntry {
			final PooledDataSource ds;

			public DataSourceEntry(Object key, PooledDataSource ds) {
				super(key);
				this.ds = ds;
			}

            public boolean isActive() {
                return ds.isActive();
            }

		}

        static class PooledDataSourcesCacheLog implements PooledObjectCache.PooledObjectCacheLog {

            public void debug(Object key, DebugCode code) {
                if (!log.isDebugEnabled()) return;
                switch (code) {
                    case STILL_ACTIVE:
						log.debug("Connection pool for " + key + " is still active, not expiring");
                        break;
                    case EXPIRING:
                        log.debug("Expiring connection pool for " + key);
                        break;
                }

            }
        }

        protected PooledObjectCache  pooledObjectCache;

		public PooledDataSourcesCache() {
		    pooledObjectCache = new PooledObjectCache();
            pooledObjectCache.setLog(new PooledDataSourcesCacheLog());
		}

		public PooledDataSource get(Object key, long now) {
            PooledObjectEntry entry = pooledObjectCache.get(key, now);
			if ((entry != null) && (entry instanceof DataSourceEntry)) return ((DataSourceEntry)entry).ds;
		    return null;
		}

		public void put(Object key, PooledDataSource ds, long now) {
			DataSourceEntry entry = new DataSourceEntry(key, ds);
			pooledObjectCache.put(key, entry, now);
		}
		
		public List removeExpired(long now, int timeout) {
            List expired = new ArrayList();
            List<PooledObjectEntry> expiredEntries = pooledObjectCache.removeExpired(now, timeout);
            for (PooledObjectEntry objectEntry : expiredEntries) expired.add(((DataSourceEntry)objectEntry).ds);
			return expired;
		}

	}

	private PooledJdbcDataSourceFactory pooledJdbcDataSourceFactory;
	private PooledDataSourcesCache poolDataSources;
	private int poolTimeout;
    private Set<String> autoCommitUnsupportedDrivers = new HashSet<String>(); // protect from NPE
    private Map<String, String> driverAuthMethMap = new HashMap<String, String>(); // protect from NPE
    private Map<String, String> driverNoAuthMethMap = new HashMap<String, String>(); // protect from NPE
    private boolean defaultReadOnly = true;
	private boolean defaultAutoCommit = false;

	public JdbcReportDataSourceServiceFactory () {
		poolDataSources = new PooledDataSourcesCache();
	}

    protected TimeZone getTimeZoneByDataSourceTimeZone(String dataSourceTimeZone) {
        String timezoneId = dataSourceTimeZone == null ? "" : dataSourceTimeZone;
        return timezoneId.isEmpty() ? TimeZone.getDefault() : TimeZone.getTimeZone(timezoneId);
    }

	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof JdbcReportDataSource)) {
			throw new JSException("jsexception.invalid.jdbc.datasource", new Object[] {reportDataSource.getClass()});
		}
        JdbcReportDataSource jdbcDataSource = (JdbcReportDataSource) reportDataSource;
        String driverClass = jdbcDataSource.getDriverClass();
        String userName = jdbcDataSource.getUsername();
        String password = jdbcDataSource.getPassword();
        String connectionUrl = jdbcDataSource.getConnectionUrl();
        if (driverClass.startsWith("tibcosoftware.jdbc")) {
            userName = (( (userName != null) && (!userName.isEmpty())) ? userName : "dummy");
            password = (( (password != null) && (!password.isEmpty())) ? password : "dummy");
        } else if (driverAuthMethMap.get(driverClass) != null) {
            if (connectionUrl.toLowerCase().indexOf("authmech") < 0) {
                String authMech = "";
                if (( userName != null) && (!userName.isEmpty())) {
                    authMech = driverAuthMethMap.get(driverClass);
                } else {
                    authMech = driverNoAuthMethMap.get(driverClass);
                    if (!authMech.equals("0")) userName = "anonymous";
                }

                connectionUrl = connectionUrl + (connectionUrl.endsWith(";") ? "authmech=" : ";authmech=") + authMech;
            }
        }
        connectionUrl = getRuntimeConnectionURL(connectionUrl);
        DataSource dataSource = getPoolDataSource(driverClass, connectionUrl, userName, password);
		return new JdbcDataSourceService(dataSource, getTimeZoneByDataSourceTimeZone(jdbcDataSource.getTimezone()));
	}

	protected DataSource getPoolDataSource(String driverClass, String url, String username, String password) {
		long now = System.currentTimeMillis();
		releaseExpiredPools(now);
			
		Object poolKey = createJdbcPoolKey(driverClass, url, username, password);
		PooledDataSource dataSource;
		synchronized (poolDataSources.pooledObjectCache) {
			dataSource = poolDataSources.get(poolKey, now);
			if (dataSource == null) {
				if (log.isDebugEnabled()) {
					log.debug("Creating connection pool for " + poolKey + ".");
				}
                boolean isAutoCommit = (getAutoCommitUnsupportedDrivers().contains(driverClass) ? true : defaultAutoCommit);
				dataSource = pooledJdbcDataSourceFactory.createPooledDataSource(
						driverClass, url, username, password,
						defaultReadOnly, isAutoCommit);
				poolDataSources.put(poolKey, dataSource, now);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Using cached connection pool for " + poolKey + ".");
				}
			}
		}

		return dataSource.getDataSource();
	}

	protected void releaseExpiredPools(long now) {
		List expired = null;
		synchronized (poolDataSources.pooledObjectCache) {
			if (getPoolTimeout() > 0) {
				expired = poolDataSources.removeExpired(now, getPoolTimeout());
			}
		}

		if (expired != null && !expired.isEmpty()) {
			for (Iterator it = expired.iterator(); it.hasNext();) {
				PooledDataSource ds = (PooledDataSource) it.next();
				try {
					ds.release();
				} catch (Exception e) {
					log.error("Error while releasing connection pool.", e);
					// ignore
				}
			}
		}
	}

	public PooledJdbcDataSourceFactory getPooledJdbcDataSourceFactory() {
		return pooledJdbcDataSourceFactory;
	}

	public void setPooledJdbcDataSourceFactory(
			PooledJdbcDataSourceFactory jdbcDataSourceFactory) {
		this.pooledJdbcDataSourceFactory = jdbcDataSourceFactory;
	}

	protected Object createJdbcPoolKey(String driverClass,
			String url, String username, String password) {
		return new JdbcPoolKey(driverClass, url, username, password);
	}
	
	protected static class JdbcPoolKey {
		private final String driverClass;
		private final String url;
		private final String username;
		private final String password;
		private final int hash;

		public JdbcPoolKey(String driverClass, String url, String username,
				String password) {
			this.driverClass = driverClass;
			this.url = url;
			this.username = username;
			this.password = password;

			int hashCode = 559;
			if (driverClass != null) {
				hashCode += driverClass.hashCode();
			}
			hashCode *= 43;
			if (url != null) {
				hashCode += url.hashCode();
			}
			hashCode *= 43;
			if (username != null) {
				hashCode += username.hashCode();
			}
			hashCode *= 43;
			if (password != null) {
				hashCode += password.hashCode();
			}
			
			hash = hashCode;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof JdbcPoolKey)) {
				return false;
			}
			if (this == obj) {
				return true;
			}
			
			JdbcPoolKey key = (JdbcPoolKey) obj;
			return
				(driverClass == null ? key.driverClass == null : (key.driverClass != null && driverClass.equals(key.driverClass))) &&
				(url == null ? key.url == null : (key.url != null && url.equals(key.url))) &&
				(username == null ? key.username == null : (key.username != null && username.equals(key.username))) &&
				(password == null ? key.password == null : (key.password != null && password.equals(key.password)));
		}

		public int hashCode() {
			return hash;
		}

		public String toString() {
			return "driver=\"" + driverClass + "\", url=\"" 
					+ url + "\", username=\"" + username + "\"";
		}
	}

	public int getPoolTimeout() {
		return poolTimeout;
	}

	public void setPoolTimeout(int poolTimeout) {
		this.poolTimeout = poolTimeout;
	}
	
	public boolean getDefaultReadOnly() {
		return defaultReadOnly;
	}

	public void setDefaultReadOnly(boolean defaultReadOnly) {
		this.defaultReadOnly = defaultReadOnly;
	}

	public boolean getDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

    public Set<String> getAutoCommitUnsupportedDrivers() {
        return autoCommitUnsupportedDrivers;
    }

    public void setAutoCommitUnsupportedDrivers(Set<String> autoCommitUnsupportedDrivers) {
        this.autoCommitUnsupportedDrivers = autoCommitUnsupportedDrivers;
    }

    public Map<String, String> getDriverAuthMethMap() {
        return driverAuthMethMap;
    }

    public void setDriverAuthMethMap(Map<String, String> driverAuthMethMap) {
        this.driverAuthMethMap = driverAuthMethMap;
    }

    public Map<String, String> getDriverNoAuthMethMap() {
        return driverNoAuthMethMap;
    }

    public void setDriverNoAuthMethMap(Map<String, String> driverNoAuthMethMap) {
        this.driverNoAuthMethMap = driverNoAuthMethMap;
    }

    private String getRuntimeConnectionURL(String connectionUrl) {
        // return relative temp folder to full path
        int index = connectionUrl.indexOf("=[TMPDIR/]");
        if (index > 0) {
            return connectionUrl.substring(0, index) + "=" + System.getProperty("java.io.tmpdir") + File.separator + connectionUrl.substring(index + 10);
        }
        return connectionUrl;
    }
}
