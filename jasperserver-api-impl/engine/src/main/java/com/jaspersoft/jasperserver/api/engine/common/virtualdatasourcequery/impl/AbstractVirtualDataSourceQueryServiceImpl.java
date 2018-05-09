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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.ConnectionFactory;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.CustomDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.VirtualDataSourceException;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.VirtualDataSourceQueryService;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JdbcDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JndiDataSource;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectCache;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectEntry;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public abstract class AbstractVirtualDataSourceQueryServiceImpl implements VirtualDataSourceQueryService {

    private PooledObjectCache dataSourceCache = new PooledObjectCache();
    private int poolTimeoutInMinute;
    protected static final Log log = LogFactory.getLog(VirtualDataSourceQueryService.class);
    protected enum DataSourceType {
        SUB_DATASOURCE,
        MASTER_DATASOURCE;
    }

    public int getPoolTimeoutInMinute() {
        return poolTimeoutInMinute;
    }

    public void setPoolTimeoutInMinute(int poolTimeoutInMinute) {
        this.poolTimeoutInMinute = poolTimeoutInMinute;
    }

    // get connection factory from a list of sub data sources
    public ConnectionFactory getConnectionFactory(Collection<DataSource> dataSourceCollection) throws Exception {
        return getConnectionFactory(dataSourceCollection, null);
    }

    public ConnectionFactory getConnectionFactory(Collection<DataSource> dataSourceCollection, String virtualReportDataSourceUri) throws Exception {
        debug("********* getConnectionFactory [BEGIN] *********************");
        try {
        init();
        StringBuffer virtualDSKey = new StringBuffer("|");
        List<String> dataSourceNames = new ArrayList<String>();
        List<String> dataSourceIDs = new ArrayList<String>();
        List<DataSource> reportDataSources = new ArrayList<DataSource>();
        long now = System.currentTimeMillis();
        DataSource firstSubDataSource = null;
        for (DataSource subDataSource: dataSourceCollection) {
                if (firstSubDataSource == null) firstSubDataSource = subDataSource;
                // construct sub data source id
                String dataSourceID = getDataSourceID(subDataSource);
                // and and mark the sub data source is in used
                addOrMarkSubDataSource(dataSourceID, subDataSource, now);
                // construct virtual data source key
                virtualDSKey.append(dataSourceID + getSubDataSourceInfo(subDataSource) + "|");
                dataSourceIDs.add(dataSourceID);
                dataSourceNames.add(subDataSource.getDataSourceName());
                reportDataSources.add(subDataSource);
        }
        if (virtualReportDataSourceUri != null) {
            virtualDSKey.append(getAdditionalInformation(virtualReportDataSourceUri));
        }  else if (firstSubDataSource != null) {
            virtualDSKey.append(getAdditionalInformation(firstSubDataSource));
        }
        debug("Virtual DS Key = " + virtualDSKey.toString());
        // convert virtual data source key to ID
        String virtualDSID = virtualDSKey.toString().hashCode() + "";

        // create connection factory
        ConnectionFactory connectionFactory = createConnectionFactory(virtualDSID, dataSourceNames, dataSourceIDs, reportDataSources, virtualReportDataSourceUri);

            // remove expired sub data source and virtual data source from the pool and embedded server
        releaseExpiredPools(now);
        debug("********* getConnectionFactory [END] *********************");
        return connectionFactory;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public abstract void init();

    public abstract ConnectionFactory createConnectionFactory(String virtualDSName, List<String> subDataSourceNames, List<String> subDataSourceIDs, List<DataSource> reportDataSource, String virtualDataSourceUri) throws Exception;

    public abstract void undeployVirtualDataSource(String virtualDSName) throws Exception;

    public abstract boolean isSubDataSourceExisted(String subDataSourceID);

    public abstract void addSubDataSource(String subDataSourceID, DataSource dataSource) throws Exception;

    public abstract void removeSubDataSource(String subDataSourceID) throws Exception;

    public abstract String getAdditionalInformation(String virtualDataSourceUri) throws VirtualDataSourceException;

    public abstract String getAdditionalInformation(DataSource dataSource) throws VirtualDataSourceException;

    // get sub data source id
    protected String getDataSourceID(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource dataSource) {
        if (dataSource instanceof JdbcDataSource) return getJdbcDataSourceID((JdbcDataSource) dataSource);
        else if (dataSource instanceof JndiDataSource) return getJndiDataSourceID((JndiDataSource) dataSource);
        else if (dataSource instanceof CustomDataSource) return getCustomDataSourceID((CustomDataSource) dataSource);
        return dataSource.hashCode() + "";
    }

    // get id for JDBC data source
    private String getJdbcDataSourceID(JdbcDataSource jdbcDataSource) {
        String key = jdbcDataSource.getConnectionUrl() + "|" + jdbcDataSource.getDriverClass() + "|" +
                jdbcDataSource.getUsername() + "|" + jdbcDataSource.getPassword();
        return key.hashCode() + "";
    }

    // get id for JNDI data source
    private String getJndiDataSourceID(JndiDataSource jndiDataSource) {
        String key = jndiDataSource.getJndiName();
        return key.hashCode() + "";
    }

    // get id for Custom data source
    private String getCustomDataSourceID(CustomDataSource customDataSource) {
        String key = customDataSource.getPropertyMap().toString();
        return key.hashCode() + "";
    }

    // add sub data source if it doesn't exist
    // and mark it used with current server time
    private synchronized void addOrMarkSubDataSource(String subDataSourceID, DataSource subDataSource, long currentTime) throws Exception {
        if (!isSubDataSourceExisted(subDataSourceID)) {
            addSubDataSource(subDataSourceID, subDataSource);
        }
        markDataSourceUsed(subDataSourceID, DataSourceType.SUB_DATASOURCE, currentTime);
    }

    protected Set<String> getSchemaSet(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource dataSource) {
        if (dataSource instanceof JdbcDataSource) return (((JdbcDataSource) dataSource).getSchemas());
        else if (dataSource instanceof JndiDataSource) return (((JndiDataSource) dataSource).getSchemas());
        else if (dataSource instanceof CustomDataSource) return (((CustomDataSource) dataSource).getSchemas());
        return null;
    }

    // get the data source name and selected schema in string
    private String getSubDataSourceInfo(com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.DataSource dataSource) {
        StringBuffer schemaString = new StringBuffer("[" + dataSource.getDataSourceName() + "]");
        Set<String> schemaList = getSchemaSet(dataSource);
        if (schemaList == null) {
            schemaString.append("[*]");
            return schemaString.toString();
        }
        schemaString.append("[");
        for (String schema : schemaList) schemaString.append(schema + ";");
        return schemaString.append("]").toString();
    }

    // mark data source is in used and update last used time
    protected void markDataSourceUsed(String poolKey, DataSourceType dataSourceType, long now) {
        if (getPoolTimeoutInMinute() <= 0) return;
        synchronized (dataSourceCache) {
            PooledObjectEntry pooledObjectEntry = dataSourceCache.get(poolKey, now);
            if (pooledObjectEntry == null) {
                dataSourceCache.put(poolKey, new PooledVirtualDataSourceEntry(poolKey, dataSourceType), now);
                if (dataSourceType == DataSourceType.MASTER_DATASOURCE) debug("Acquire Virtual Data Source Pool Key: " + poolKey + ".");
                else debug("Acquire Sub Data Source Pool Key: " + poolKey + ".");
            } else {
                if (dataSourceType == DataSourceType.MASTER_DATASOURCE) debug("Update Virtual Data Source Pool Key: " + poolKey + ".");
                else debug("Update Sub Data Source Pool Key: " + poolKey + ".");
            }
        }
    }

    protected boolean isVirtualDataSourceExistedInPool(String poolKey) {
        synchronized (dataSourceCache) {
            PooledObjectEntry pooledObjectEntry = dataSourceCache.get(poolKey, System.currentTimeMillis());
            boolean isVirtualDataSourceExistedInPool = (pooledObjectEntry != null);
            debug("VDB EXISTS IN POOL = " + isVirtualDataSourceExistedInPool);
            return isVirtualDataSourceExistedInPool;
        }
    }

    // release expired data source and remove from server
    protected void releaseExpiredPools(long now) {
        if (getPoolTimeoutInMinute() <= 0) return;
		List expired = null;
		synchronized (dataSourceCache) {
		    expired = dataSourceCache.removeExpired(now, getPoolTimeoutInMinute() * 60);
		}

		if (expired != null && !expired.isEmpty()) {
			for (Iterator it = expired.iterator(); it.hasNext();) {
				PooledVirtualDataSourceEntry ds = (PooledVirtualDataSourceEntry) it.next();
				try {
                    // remove data source
					ds.release();
				} catch (Exception e) {
					log.error("Error while releasing Virtual Pool Key.", e);
					// ignore
				}
			}
		}
	}

    class PooledVirtualDataSourceEntry extends PooledObjectEntry {

        private DataSourceType dataSourceType;

        public PooledVirtualDataSourceEntry(Object key, DataSourceType dataSourceType) {
            super(key);
            this.dataSourceType = dataSourceType;
        }

        public void release() throws Exception {
            switch (dataSourceType) {
                case MASTER_DATASOURCE:
                    undeployVirtualDataSource((String) getKey());
                    break;
                case SUB_DATASOURCE:
                    removeSubDataSource((String)getKey());
            }
        }
    }

    protected boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    protected void debug(Object debugMessage) {
        if (log.isDebugEnabled()) log.debug(debugMessage);
    //    System.out.println(debugMessage);
    }

    protected void debug(Object debugMessage, Throwable throwable) {
        if (log.isDebugEnabled()) log.debug(debugMessage, throwable);
    //    System.out.println(debugMessage);
    }

}
