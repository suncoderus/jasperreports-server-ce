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

package com.jaspersoft.jasperserver.war.xmla;

import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.service.UpdatableXMLAContainer;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import mondrian.olap.DriverManager;
import mondrian.olap.MondrianServer;
import mondrian.olap.Util;
import mondrian.olap4j.MondrianOlap4jDriver;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.rolap.RolapSchema;
import mondrian.server.JsMondrianServerRegistry;
import mondrian.server.Repository;
import mondrian.spi.CatalogLocator;
import mondrian.util.ClassResolver;
import mondrian.util.LockBox;
import mondrian.xmla.DataSourcesConfig;
import mondrian.xmla.XmlaHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.impl.Olap4jUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Implementation of {@link mondrian.server.Repository} that reads from JRS databases.
 *
 * @author vsabadosh
 * @version $Id$
 */
public class XmlaRepositoryImpl implements Repository, UpdatableXMLAContainer {

    protected static final Log log = LogFactory.getLog(XmlaRepositoryImpl.class);
    protected XmlaContentFinder xmlaContentFinder;
    private CatalogLocator locator;
    private long cacheTimeout;
    private String catalogDelimiter;

    private final Map<String, CacheElement<Properties>> olapConnectionPropertiesCache =
            new ConcurrentHashMap<String, CacheElement<Properties>>();

    private final Map<String, CacheElement<ServerInfo>> serverInfoCache =
            new ConcurrentHashMap<String, CacheElement<ServerInfo>>();

    public void setXmlaContentFinder(XmlaContentFinder xmlaContentFinder) {
        this.xmlaContentFinder = xmlaContentFinder;
    }

    public void setLocator(CatalogLocator locator) {
        this.locator = locator;
    }

    public void setCacheTimeout(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    public void setCatalogDelimiter(String catalogDelimiter) {
        this.catalogDelimiter = catalogDelimiter;
    }

    @Override
    public List<String> getDatabaseNames(RolapConnection connection) {
        return new ArrayList<String>(getServerInfo().datasourceMap.keySet());
    }

    @Override
    public List<String> getCatalogNames(RolapConnection connection, String databaseName) {
        return new ArrayList<String>(getServerInfo().getDatabaseInfo(databaseName).getCatalogAsMap().keySet());
    }

    @Override
    public Map<String, RolapSchema> getRolapSchemas(RolapConnection connection, String databaseName, String catalogName) {
        final RolapSchema schema = connection.getSchema();
        return Collections.singletonMap(schema.getName(), schema);
    }

    @Override
    public List<Map<String, Object>> getDatabases(RolapConnection connection) {
        final List<Map<String, Object>> propsList = new ArrayList<Map<String, Object>>();
        for (DatabaseInfo dsInfo : getServerInfo().datasourceMap.values()) {
            propsList.add(dsInfo.properties);
        }
        return propsList;
    }

    @Override
    public void clearCache() {
        synchronized (this.serverInfoCache) {
            serverInfoCache.clear();
        }
    }

    @Override
    public void shutdown() {
        // nothing to do
    }

    @Override
    public void updateXMLAConnection(MondrianXMLADefinition oldDef, MondrianXMLADefinition newDef) {
        clearCache();
    }

    @Override
    public OlapConnection getConnection(MondrianServer server, String databaseName, String catalogName, String roleName,
            Properties props) throws SQLException {
        final ServerInfo serverInfo = getServerInfo();
        final DatabaseInfo databaseInfo;

        if (databaseName == null) {
            if (serverInfo.datasourceMap.size() == 0) {
                throw new OlapException("No databases configured on this server");
            }
            databaseInfo = serverInfo.datasourceMap.values().iterator().next();
        } else {
            databaseInfo = serverInfo.datasourceMap.get(databaseName);
        }

        if (databaseInfo == null) {
            throw Util.newError("Unknown database '" + databaseName + "'");
        }

        CatalogInfo catalogInfo = null;
        Map<String, CatalogInfo> catalogsMap = databaseInfo.getCatalogAsMap();

        Iterator<CatalogInfo> catalogInfoIterator = catalogsMap.values().iterator();

        if (isEmpty(catalogName)) {
            if (catalogsMap.size() == 0) {
                throw new OlapException("No catalogs in the database named " + databaseInfo.name);
            }
        } else {
            catalogInfo = databaseInfo.getCatalogByName(catalogName, catalogsMap);
            if (catalogInfo == null) {
                throw Util.newError("Unknown catalog '" + catalogName + "'");
            }
        }

        /* Strong reference to the LockBox.Entry instance
           We must keep this strong reference here to prevent its removal from WeakHashMap inside LockBox
           See bug http://jira.jaspersoft.com/browse/JRS-15076 */
        LockBox.Entry lockBoxEntry = JsMondrianServerRegistry.INSTANCE.getLockBox().register(server);

        //Loop because in the case of catalogName == null we need find the default valid catalog.
        do {
            try {
                if (isEmpty(catalogName)) {
                    catalogInfo = catalogInfoIterator.next();
                }

                // Now create the connection
                OlapConnection olapConnection = getOlapConnection(getMondrianConnectionProperties(
                        server, databaseInfo, catalogInfo, roleName, props, lockBoxEntry));

                /* Following check is required.
                   We should twitch lockBoxEntry reference to keep it from JVM optimization removal.
                   See bug http://jira.jaspersoft.com/browse/JRS-15076 */
                if (lockBoxEntry == null || lockBoxEntry.getMoniker() == null) {
                    throw new IllegalStateException("Lack of strong reference to mondrian.util.LockBox.Entry");
                } else {
                    if (log.isDebugEnabled()) {
                        LockBox.Entry entry = JsMondrianServerRegistry.INSTANCE.getLockBox().get(lockBoxEntry.getMoniker());
                        if (entry == null || !StringUtils.equals(lockBoxEntry.getMoniker(), entry.getMoniker())) {
                            log.error("LockBox.Entry was lost");
                        }
                    }
                }

                return olapConnection;
            } catch (Exception ex) {
                if ((isNotEmpty(catalogName)) || !catalogInfoIterator.hasNext()) {
                    throw new RuntimeException(ex.getMessage(), ex);
                }
            }
        } while (isEmpty(catalogName) && catalogInfoIterator.hasNext());

        // Is not reachable.
        throw new OlapException("All XML/A catalogs are not valid.");
    }

    protected OlapConnection getOlapConnection(Properties properties) throws SQLException {
        final java.sql.Connection connection = java.sql.DriverManager.getConnection("jdbc:mondrian:", properties);
        return connection.unwrap(OlapConnection.class);
    }

    private Properties getMondrianConnectionProperties(MondrianServer server, DatabaseInfo databaseInfo,
            CatalogInfo catalogInfo, String roleName,
            Properties props, LockBox.Entry lockBoxEntry) throws SQLException {
        //Get current session id
        String currentSessionId = getCurrentUserSessionId();
        //Generated cached key based on authorized session id, datasource, catalog name and tenant id.
        String cacheKey = currentSessionId + "_" + databaseInfo.name + "_" + catalogInfo.name + "_" + catalogInfo.tenantId;

        Map<String, Object> connectProperties = new HashMap<String, Object>();
        connectProperties.putAll(databaseInfo.properties);
        connectProperties.put("DataSourceInfo", catalogInfo.connectString);

        synchronized (this.olapConnectionPropertiesCache) {
            cleanExpired(this.olapConnectionPropertiesCache);

            Properties properties;
            if (olapConnectionPropertiesCache.get(cacheKey) != null) {
                properties = olapConnectionPropertiesCache.get(cacheKey).getValue();
            } else {
                properties = xmlaContentFinder.getMondrianConnectionProperties(connectProperties, roleName);
                olapConnectionPropertiesCache.put(cacheKey, new CacheElement<Properties>(properties));
            }

            // Save the server for the duration of the call to 'getConnection'.
            properties.setProperty(RolapConnectionProperties.Instance.name(), lockBoxEntry.getMoniker());

            // Make sure we load the Mondrian driver into the ClassLoader.
            try {
                ClassResolver.INSTANCE.forName(MondrianOlap4jDriver.class.getName(), true);
            } catch (ClassNotFoundException e) {
                throw new OlapException("Cannot find mondrian olap4j driver.");
            }

            if (props != null && props.containsKey(XmlaHandler.JDBC_LOCALE)) {
                properties.put(XmlaHandler.JDBC_LOCALE, props.get(XmlaHandler.JDBC_LOCALE));
            }

            return properties;
        }
    }

    private ServerInfo getServerInfo() {
        synchronized (this.serverInfoCache) {
            String currentSessionId = getCurrentUserSessionId();

            cleanExpired(serverInfoCache);
            if (serverInfoCache.get(currentSessionId) == null) {
                DataSourcesConfig.DataSources xmlDataSources = xmlaContentFinder.getDataSources();
                ServerInfo serverInfo = new ServerInfo();

                if (xmlDataSources != null && xmlDataSources.dataSources != null) {
                    for (DataSourcesConfig.DataSource xmlDataSource : xmlDataSources.dataSources) {
                        final Map<String, Object> dsPropsMap =
                                Olap4jUtil.<String, Object>mapOf(
                                        "DataSourceName", xmlDataSource.getDataSourceName(),
                                        "DataSourceDescription", xmlDataSource.getDataSourceDescription(),
                                        "URL", xmlDataSource.getURL(),
                                        "DataSourceInfo", xmlDataSource.getDataSourceName(),
                                        "ProviderName", xmlDataSource.getProviderName(),
                                        "ProviderType", xmlDataSource.providerType,
                                        "AuthenticationMode", xmlDataSource.authenticationMode);
                        final DatabaseInfo databaseInfo = new DatabaseInfo(xmlDataSource.name, dsPropsMap);
                        serverInfo.datasourceMap.put(xmlDataSource.name, databaseInfo);
                        if (xmlDataSource.catalogs != null && xmlDataSource.catalogs.catalogs != null) {
                            for (DataSourcesConfig.Catalog xmlCatalog : xmlDataSource.catalogs.catalogs) {
                                final CatalogInfo catalogInfo = new CatalogInfo(xmlCatalog, xmlDataSource.dataSourceInfo, locator);
                                if (databaseInfo.catalogsByTenantIdMap.get(catalogInfo.tenantId) != null) {
                                    databaseInfo.catalogsByTenantIdMap.get(catalogInfo.tenantId).add(catalogInfo);
                                } else {
                                    List<CatalogInfo> catalogInfos = new ArrayList<CatalogInfo>();
                                    catalogInfos.add(catalogInfo);
                                    databaseInfo.catalogsByTenantIdMap.put(catalogInfo.tenantId, catalogInfos);
                                }
                            }
                        }
                    }
                }
                serverInfoCache.put(currentSessionId, new CacheElement<ServerInfo>(serverInfo));

                return serverInfo;
            } else {
                return serverInfoCache.get(currentSessionId).getValue();
            }
        }
    }

    private class ServerInfo {
        private Map<String, DatabaseInfo> datasourceMap = new HashMap<String, DatabaseInfo>();

        private DatabaseInfo getDatabaseInfo(String databaseName) {
            DatabaseInfo databaseInfo = datasourceMap.get(databaseName);
            if (databaseInfo != null) {
                return databaseInfo;
            } else {
                throw Util.newError("Unknown database '" + databaseName + "'");
            }
        }
    }

    private class DatabaseInfo {
        private final String name;
        private final Map<String, Object> properties;
        private Map<String, List<CatalogInfo>> catalogsByTenantIdMap = new HashMap<String, List<CatalogInfo>>();

        DatabaseInfo(String name, Map<String, Object> properties) {
            this.name = name;
            this.properties = properties;
        }

        Map<String, CatalogInfo> getCatalogAsMap() {
            Map<String, CatalogInfo> catalogsMap = new HashMap<String, CatalogInfo>();

            for (String tenantId : catalogsByTenantIdMap.keySet()) {
                catalogsMap.putAll(getCatalogsMapForId(tenantId));
            }
            return catalogsMap;
        }

        private Map<String, CatalogInfo> getCatalogsMapForId(String tenantId) {
            Map<String, CatalogInfo> catalogsMap = new HashMap<String, CatalogInfo>();

            if (catalogsByTenantIdMap.get(tenantId) == null) {
                return catalogsMap;
            }

            for (CatalogInfo catalogInfo : catalogsByTenantIdMap.get(tenantId)) {
                String catalogKey;

                if (isNotEmpty(tenantId) && !tenantId.equals(getCurrentUserTenantId())) {
                    catalogKey = catalogInfo.name + catalogDelimiter + tenantId;
                } else {
                    catalogKey = catalogInfo.name;
                }

                if (catalogsMap.containsKey(catalogKey)) {
                    CatalogInfo catalogInfoDuplicate = catalogsMap.get(catalogKey);
                    if (catalogInfo.tenantId.equals(catalogInfoDuplicate.tenantId)) {
                        throw Util.newError("More than one Catalog object has name '" + catalogKey + "'");
                    } else if (isNotEmpty(catalogInfo.tenantId)) {
                        catalogsMap.put(catalogKey, catalogInfo);
                    }
                }
                catalogsMap.put(catalogKey, catalogInfo);
            }

            return catalogsMap;
        }

        CatalogInfo getCatalogByName(String name, Map<String, CatalogInfo> catalogsMap) {
            CatalogInfo catalogInfo = null;
            if (catalogsMap.containsKey(name)) {
                catalogInfo = catalogsMap.get(name);
            } else {
                for (String key : catalogsMap.keySet()) {
                    //Searching over another organization catalogs.
                    if (key.startsWith(name + catalogDelimiter)) {
                        catalogInfo = catalogsMap.get(key);
                        break;
                    }
                }
            }
            return catalogInfo;
        }

    }

    private class CatalogInfo {
        private final String connectString;
        private RolapSchema rolapSchema; // populated on demand
        private final String olap4jConnectString;
        private final CatalogLocator locator;
        private final String tenantId;
        private final String name;

        CatalogInfo(DataSourcesConfig.Catalog catalog, String dataSourceInfo, CatalogLocator locator) {
            this.name = catalog.name;
            this.locator = locator;
            this.tenantId = catalog.definition;
            this.connectString = catalog.dataSourceInfo != null ? catalog.dataSourceInfo : dataSourceInfo;

            this.olap4jConnectString = connectString.startsWith("jdbc:") ? connectString : "jdbc:mondrian:"
                    + connectString;
        }

        private RolapSchema getRolapSchema() {
            if (rolapSchema == null) {
                RolapConnection rolapConnection = null;
                try {
                    rolapConnection = (RolapConnection)DriverManager.getConnection(connectString, this.locator);
                    rolapSchema = rolapConnection.getSchema();
                } finally {
                    if (rolapConnection != null) {
                        rolapConnection.close();
                    }
                }
            }
            return rolapSchema;
        }
    }

    protected String getCurrentUserTenantId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            MetadataUserDetails userDetails = (MetadataUserDetails) auth.getPrincipal();
            if (userDetails != null) {
                return userDetails.getTenantId();
            }
        }
        return null;
    }

    protected String getCurrentUserSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    private class CacheElement<T> {
        private T value;
        private AtomicLong timestamp = new AtomicLong(Calendar.getInstance().getTimeInMillis());

        public CacheElement(T value) {
            this.value = value;
        }

        private T getValue() {
            return value;
        }

        private AtomicLong getTimestamp() {
            return timestamp;
        }
    }

    private <T> void cleanExpired(Map<String, CacheElement<T>> cacheMap) {
        for (Map.Entry<String, CacheElement<T>> entry : cacheMap.entrySet()) {
            // Check if not expired
            if (Calendar.getInstance().getTimeInMillis() > (entry.getValue().getTimestamp().longValue() +
                    (cacheTimeout * 1000))) {
                // Evicts it.
                cacheMap.remove(entry.getKey());
            }
        }
    }

}
