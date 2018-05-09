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
package com.jaspersoft.jasperserver.api.metadata.olap.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.MondrianConnectionSchemaParameters;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapManagementService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import mondrian.olap.CacheControl;
import mondrian.rolap.agg.AggregationManager;
import mondrian.spi.CatalogLocator;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.ehcache.EhCacheFactoryBean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Iterator;
import java.util.List;


/**
 * @author sbirney
 *
 */
public class OlapManagementServiceImpl implements  OlapManagementService {

    private static final Log log = LogFactory.getLog(OlapManagementServiceImpl.class);

    RepositoryService repositoryService;
    OlapConnectionService olapConnectionService;

    private EhCacheFactoryBean mondrianConnectionCacheFactory;
    private EhCacheFactoryBean sharedMondrianConnectionCacheFactory;

    private Cache mondrianConnectionCache;
    private Cache sharedMondrianConnectionCache;
    private Cache olapFiltersCache;

    public void setOlapFiltersCache(Cache olapFiltersCache) {
        this.olapFiltersCache = olapFiltersCache;
    }

    private CatalogLocator repositoryCatalogLocator = new RepositoryCatalogLocator();
    private ExecutionContext runtimeContext = ExecutionContextImpl.getRuntimeExecutionContext();

    protected interface MondrianConnectionAction {
        public String getName();
        public void doIt(MondrianConnection o);
    }

    /**
     * Flush all loaded schemas
     */
    public void flushOlapCache() {
        //TODO: Mondrian upgrade QA have to test occurate mondrian schema cache
        AggregationManager.instance().getCacheControl(null, null).flushSchemaCache();
        olapFiltersCache.removeAll();
        log.debug("All schemas flushed");
    }

    /**
     * Called from cache event notification. Resource will be an internal representation,
     * so it does not need to be transformed
     *
     * @param resource Internal representation of Resource
     */
    public void flushConnection(Resource resource) {

        if (resource == null) {
            throw new JSException("Null resource");
        }
        flushIfRelatedToMondrianConnection(resource);
    }

    /**
     * Given internal representation of uri of Mondrian Connection - flush
     *
     * @param uri
     */
    public void flushConnection(String uri) {

        Resource resource = getRepositoryService().getResource(runtimeContext, uri);

        if (resource == null) {
            throw new JSException("No such resource: " + uri);
        }

        flushConnection(resource);
    }

    private MondrianConnectionSchemaParameters getConnectionParameters(MondrianConnection monConn, boolean transform) {
        MondrianConnectionSchemaParameters parameters = null;

        String monConnUri = transform ? transformUri(monConn.getURIString()) : monConn.getURIString();

        String dsUri = null;
        ReportDataSource dataSource = null;

        ResourceReference ref = monConn.getDataSource();
        if (ref.isLocal()) {
            dataSource = (ReportDataSource) ref.getLocalResource();
        } else {
            dsUri = transform ? transformUri(ref.getReferenceURI()) : ref.getReferenceURI();
            dataSource = (ReportDataSource) getRepositoryService().getResource(runtimeContext, dsUri);
        }

        if (dataSource == null) {
            throw new JSException("null data source on dereference of mondrian connection " + monConnUri + " for " +
                    (monConn.getDataSource().isLocal() ? "local: " + ref.getLocalResource().getURIString()
                                                    : dsUri));
        }

        // Define values for schema cache key
        // This uri will be transformed as part of the CatalogLocator
        String catalogUrl = monConn.getSchema().getReferenceURI();

        if (transform) {
            catalogUrl = transformUri(catalogUrl);
        }

       catalogUrl = repositoryCatalogLocator.locate(catalogUrl);

        log.debug("catalogUrl: " + catalogUrl + ", original URI: " + monConn.getSchema().getReferenceURI() + ", transform: " + transform);

        if (dataSource instanceof JdbcReportDataSource) {
            JdbcReportDataSource jdbcDs = (JdbcReportDataSource) dataSource;
            String jdbcConnectString = jdbcDs.getConnectionUrl();
            String jdbcUser = jdbcDs.getUsername();
            parameters = new MondrianConnectionSchemaParameters(monConnUri, catalogUrl, jdbcConnectString, jdbcUser);
        } else {
            JndiJdbcReportDataSource jndiDs = (JndiJdbcReportDataSource) dataSource;

            String strDataSource = "";

            if ((jndiDs.getJndiName() != null && !jndiDs.getJndiName().startsWith("java:"))) {
                try {
                    Context ctx = new InitialContext();
                    ctx.lookup("java:comp/env/" + jndiDs.getJndiName());
                    strDataSource = "java:comp/env/";
                }  catch (NamingException e) {
                    //Added as short time solution due of http://bugzilla.jaspersoft.com/show_bug.cgi?id=26570.
                    //The main problem - this code executes in separate tread (non http).
                    //Jboss 7 support team recommend that you use the non-component environment namespace for such situations.
                    try {
                        Context ctx = new InitialContext();
                        ctx.lookup(jndiDs.getJndiName());
                        strDataSource = "";
                    } catch (NamingException ex) {

                    }
                }
            }

            strDataSource = strDataSource + jndiDs.getJndiName();
            parameters = new MondrianConnectionSchemaParameters(monConnUri, catalogUrl, strDataSource);
        }

        return parameters;
    }

    /**
     * Flush cache on this server instance for given internal resource
     *
     * @param monConnSchemaParameters internal representation
     */
    public void flushConnection(MondrianConnectionSchemaParameters monConnSchemaParameters) {

        log.debug("flushing connection " + monConnSchemaParameters);

        String connectionKey = monConnSchemaParameters.jdbcConnectionString;
        // we don't add extra Jdbc parameters
        //    + getJdbcPropertiesFromConnectionProperties(connectInfo).toString();

        CacheControl cacheControl = AggregationManager.instance().getCacheControl(null, null);

        cacheControl.flushSchema(
            monConnSchemaParameters.catalogUri,
            connectionKey,
            monConnSchemaParameters.jdbcUser,
            monConnSchemaParameters.jndiDataSource);
    }

    /**
     * Flush Mondrian connection if the given resource is related to a
     * Mondrian connection.
     * 
     * @param resource Resource possibly related to a Mondrian Connection
     */
    public void flushIfRelatedToMondrianConnection(Resource resource) {
        actIfRelatedToMondrianConnection(resource, flushConnection);
    }

    private MondrianConnectionAction flushConnection = new MondrianConnectionAction() {
        public String getName() {
            return "flush connection";
        }
        public void doIt(MondrianConnection monConn) {
            flushConnection(monConn);
        }
    };

    protected void actIfRelatedToMondrianConnection(Resource resource, MondrianConnectionAction act) {
        if (resource instanceof JdbcReportDataSource ||
                resource instanceof JndiJdbcReportDataSource) {
            actIfRelatedToMondrianConnection("dataSource", resource, act);
        } else if (resource instanceof MondrianConnection) {
            act.doIt((MondrianConnection) resource);
        } else if (resource instanceof FileResource) {
            actIfRelatedToMondrianConnection("schema", resource, act);
        }
    }

    /**
     * Flush if the given object is related to any Mondrian Connections
     *
     * @param accessorName
     * @param resource
     * @param act
     */
    protected void actIfRelatedToMondrianConnection(String accessorName, Resource resource, MondrianConnectionAction act) {
        log.debug("checking for " + act.getName() +  ": " + resource.getClass().getName() +
                ", uri: " + resource.getURIString() +
                " via accessor: " + accessorName);
        FilterCriteria criteria = new FilterCriteria(getMondrianConnectionClass());
        criteria.addFilterElement(FilterCriteria.createReferenceFilter(accessorName, resource.getClass(), resource.getURIString()));
        List mondrianConnectionLookups = getRepositoryService().loadResourcesList(runtimeContext, criteria);
        if (mondrianConnectionLookups != null && !mondrianConnectionLookups.isEmpty()) {
            for (Iterator it = mondrianConnectionLookups.iterator(); it.hasNext();) {
                ResourceLookup lookup = (ResourceLookup) it.next();
                MondrianConnection monConn = (MondrianConnection) getRepositoryService().getResource(runtimeContext, lookup.getURI());
                act.doIt(monConn);
            }
        }
    }

    /**
     * 
     * @return Mondrian Connection class
     */
    protected Class getMondrianConnectionClass() {
        return MondrianConnection.class;
    }

    /**
     * Called when an object related to a Mondrian Connection, or the Connection
     * itself, is changed. Resource is an internal representation, as it comes from
     * Hibernate saveOrUpdate event notification.
     *
     * @param context
     * @param resource
     */
    public void notifySchemaChange(ExecutionContext context, Resource resource) {
        actIfRelatedToMondrianConnection(resource, notifySchemaChange);
    }

    private MondrianConnectionAction notifySchemaChange = new MondrianConnectionAction() {
        public String getName() {
            return "notify schema change";
        }

        public void doIt(MondrianConnection monConn) {

            String uri = monConn.getURIString();

            Cache aConnectionCache = getMondrianConnectionCache();

            log.debug("schema change: " + uri);

            Element element = new Element(uri, getConnectionParameters(monConn, false));

            // Remove the old element. This actually causes the flush
            aConnectionCache.remove(uri);

            aConnectionCache.put(element);
        }
    };

    /**
     * Called when a process uses a Mondrian Connection. Keeps the cache fresh.
     * Given connection is an external representation, so it needs to be transformed,
     * though this implementation of the OlapManagementService does not
     *
     * @param context
     * @param resource
     */
    public void notifySchemaUse(ExecutionContext context, Resource resource) {
        actIfRelatedToMondrianConnection(resource, notifySchemaUse);
    }

    private MondrianConnectionAction notifySchemaUse = new MondrianConnectionAction() {
        public String getName() {
            return "notify schema use";
        }

        public void doIt(MondrianConnection monConn) {

            String uri = transformUri(monConn.getURIString());

            log.debug("schema use: " + uri);

            Cache aConnectionCache = getMondrianConnectionCache();

            Element schemaReference = aConnectionCache.get(uri);

            if (schemaReference == null) {
                MondrianConnectionSchemaParameters parameters = getConnectionParameters(monConn, true);
                schemaReference = new Element(parameters.repositoryUri, parameters);
                aConnectionCache.put(schemaReference);
            }

        }
    };

    /**
     * We want Connections to be consistent across servers, so provide a hook
     * to transform the resource uri if needed
     *
     * @param uri
     * @return unchanged URI
     */
    protected String transformUri(String uri) {
        return uri;
    }

    /**
     * Extract JDBC connection specific info from the connect string
     *
     * Since we don't add extra Jdbc settings, not used
     *
     * @param connectInfo
     * @return Properties only JDBC properties
     */
    /*
    private static Properties getJdbcPropertiesFromConnectionProperties(Util.PropertyList connectInfo) {
        Properties onlyJdbcProperties = new Properties();
        for (Pair<String, String> aPropertyPair : connectInfo) {
            if (aPropertyPair.left.startsWith(
                RolapConnectionProperties.JdbcPropertyPrefix))
            {
                onlyJdbcProperties.put(
                    aPropertyPair.left.substring(
                        RolapConnectionProperties.JdbcPropertyPrefix.length()),
                    aPropertyPair.right);
            }
        }
        return onlyJdbcProperties;
    }
    */

    /**
     * expects transformed resources and URIs
     *
     * @return
     */
    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public OlapConnectionService getOlapConnectionService() {
        return olapConnectionService;
    }

    public void setOlapConnectionService(OlapConnectionService olapConnectionService) {
        this.olapConnectionService = olapConnectionService;
    }

    public EhCacheFactoryBean getMondrianConnectionCacheFactory() {
        return mondrianConnectionCacheFactory;
    }

    public void setMondrianConnectionCacheFactory(EhCacheFactoryBean mondrianConnectionCacheFactory) {
        this.mondrianConnectionCacheFactory = mondrianConnectionCacheFactory;
    }

    public EhCacheFactoryBean getSharedMondrianConnectionCacheFactory() {
        return sharedMondrianConnectionCacheFactory;
    }

    public void setSharedMondrianConnectionCacheFactory(EhCacheFactoryBean sharedMondrianConnectionCacheFactory) {
        this.sharedMondrianConnectionCacheFactory = sharedMondrianConnectionCacheFactory;
    }

    public Cache getMondrianConnectionCache() {
        if (mondrianConnectionCache == null) {
            mondrianConnectionCache = (Cache) getMondrianConnectionCacheFactory().getObject();
        }
        return mondrianConnectionCache;
    }

    public void setMondrianConnectionCache(Cache mondrianConnectionCache) {
        this.mondrianConnectionCache = mondrianConnectionCache;
    }

    public Cache getSharedMondrianConnectionCache() {
        if (sharedMondrianConnectionCache == null) {
            sharedMondrianConnectionCache = (Cache) getSharedMondrianConnectionCacheFactory().getObject();
        }
        return sharedMondrianConnectionCache;
    }
}
