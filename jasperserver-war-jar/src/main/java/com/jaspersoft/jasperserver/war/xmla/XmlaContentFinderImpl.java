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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapManagementService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import mondrian.olap.Util;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.util.Pair;
import mondrian.xmla.DataSourcesConfig;
import mondrian.xmla.XmlaConstants;
import mondrian.xmla.XmlaException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Implementation of
 * {@link com.jaspersoft.jasperserver.war.xmla.XmlaContentFinder} that
 * load content of the repository.
 *
 * @author vsabadosh
 * @version $Id$
 */
public class XmlaContentFinderImpl implements XmlaContentFinder {
    protected static final Log log = LogFactory.getLog(XmlaContentFinderImpl.class);
    protected final static String TENANT_ID = "TenantID=";
    protected static final String providerName = "Mondrian";
    protected static final String dataSourceName = "JRS";
    protected static final String dataSourceDescription = "JasperServer Mondrian XMLA Definition";

    private RepositoryService repository;
    private TenantService tenantService;
    private OlapConnectionService olapConnectionService;

    public void setRepository(RepositoryService repository) {
        this.repository = repository;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public void setOlapConnectionService(OlapConnectionService olapConnectionService) {
        this.olapConnectionService = olapConnectionService;
    }

    public OlapConnectionService getOlapConnectionService() {
        return olapConnectionService;
    }

    public OlapManagementService getOlapManagementService() {
        return StaticApplicationContext.getApplicationContext().getBean("olapManagementService", OlapManagementService.class);
    }

    public RepositoryService getRepository() {
        return repository;
    }

    public DataSourcesConfig.DataSources getDataSources() {
        DataSourcesConfig.DataSources datasources = new DataSourcesConfig.DataSources();

        // Use findResource to avoid filtering with security
        FilterCriteria f = FilterCriteria.createFilter(MondrianXMLADefinition.class);
        ResourceLookup[] lookups = repository.findResource(JasperServerUtil.getExecutionContext(), f);

        DataSourcesConfig.DataSource d = new DataSourcesConfig.DataSource();
        d.description = dataSourceDescription;
        d.url = XmlaServletImpl.SERVER_URL;
        d.providerName = providerName;
        d.providerType = DataSourcesConfig.DataSource.PROVIDER_TYPE_MDP;
        d.name = "Provider=" + d.providerName + ";DataSource=" + dataSourceName;
        d.dataSourceInfo = null;
        d.authenticationMode = DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED;


        Map<String, String> dsURIs = new LinkedHashMap<String, String>();
        List<DataSourcesConfig.Catalog> catalogList = new ArrayList<DataSourcesConfig.Catalog>();

        if (lookups != null && lookups.length > 0) {
            for (ResourceLookup r : lookups) {

                // disqualify org template resources
                if (r.getURIString().contains(TenantService.ORG_TEMPLATE)) {
                    continue;
                }

                MondrianXMLADefinition def = (MondrianXMLADefinition)repository.getResource(null, r.getURIString());

                DataSourcesConfig.Catalog catalog = createCatalogConfigOutOfDefinition(def);

                String alreadyUsed = dsURIs.get(catalog.dataSourceInfo);
                if (alreadyUsed != null) {
                    String errorMessage = "XML/A definition " + r.getURIString() + " has the name (" + d.dataSourceInfo + ")" +
                            " as another  XML/A definition (" + alreadyUsed + ")";
                    throw new JSException(errorMessage);
                }
                catalogList.add(catalog);
                dsURIs.put(catalog.dataSourceInfo, r.getURIString());
            }

            DataSourcesConfig.Catalogs cs = new DataSourcesConfig.Catalogs();
            cs.catalogs = catalogList.toArray(new DataSourcesConfig.Catalog[catalogList.size()]);
            d.catalogs = cs;
        }
        datasources.dataSources = new DataSourcesConfig.DataSource[1];
        datasources.dataSources[0] = d;

        return datasources;
    }

    public DataSourcesConfig.Catalog createCatalogConfigOutOfDefinition(MondrianXMLADefinition def) {
        DataSourcesConfig.Catalog catalog = new DataSourcesConfig.Catalog();

        catalog.dataSourceInfo = "Provider=" + providerName + ";DataSource=" + def.getCatalog() + ";";
        String tenantId = "";

        Tenant tenant = tenantService.getTenantBasedOnRepositoryUri(null, def.getURIString());
        if (tenant != null) {
            tenantId = tenant.getId();
            catalog.dataSourceInfo += TENANT_ID + tenantId + ";";
        }
        catalog.name = def.getCatalog();
        catalog.definition = tenantId;

        return catalog;
    }

    public Properties getMondrianConnectionProperties(Map<String, Object> dataSourceProperties, String role) {
        MondrianConnection monConn = lookupXmlaConnection(null, (String)dataSourceProperties.get("DataSourceInfo"));
        Util.PropertyList connectProperties = olapConnectionService.getMondrianConnectProperties(null, monConn);

        // Checking access
        if (!DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED.equalsIgnoreCase((String)dataSourceProperties.get("AuthenticationMode"))
                && null == role) {
            throw new XmlaException(XmlaConstants.CLIENT_FAULT_FC, XmlaConstants.HSB_ACCESS_DENIED_CODE,
                    XmlaConstants.HSB_ACCESS_DENIED_FAULT_FS,
                    new SecurityException("Access denied for data source needing authentication")
            );
        }

        if (role != null && role.trim().length() != 0) {
            connectProperties.put(RolapConnectionProperties.Role.toString(), role);
        }

        Properties properties = new Properties();
        for (Pair<String, String> pair : connectProperties) {
            properties.put(pair.getKey(), pair.getValue());
        }

        return properties;
    }

    public MondrianConnection lookupXmlaConnection(ExecutionContext context, String dataSourceInfo) {
        MondrianConnection result = null;
        // Use loadResourcesList to filter with security

        // TODO Make this more efficient: cache? use a URI to lookup?
        FilterCriteria f = FilterCriteria.createFilter(MondrianXMLADefinition.class);
        // search for the resource in a folder based on tenantID
        String[] tenantId = dataSourceInfo.split(TENANT_ID);
        if (tenantId.length < 2) { //this is a public data source
            f.addNegatedFilterElement(FilterCriteria.createAncestorFolderFilter("/" + TenantService.ORGANIZATIONS));
        } else { //this is a tenant data source
            tenantId = tenantId[1].split(";");
            Tenant t = tenantService.getTenant(null, tenantId[0]);
            f.addFilterElement(FilterCriteria.createAncestorFolderFilter(t.getTenantFolderUri()));
            log.debug("looking under "+t.getTenantFolderUri());
        }
        List lookups = repository.loadResourcesList(context, f);

        if (lookups == null || lookups.size() == 0) {
            log.error("No XMLA Definitions");
        } else {
            for (Iterator it = lookups.iterator(); it.hasNext(); ) {
                MondrianXMLADefinition xmlaDef = (MondrianXMLADefinition) repository.getResource(context, ((ResourceLookup) it.next()).getURIString());
                if (dataSourceInfo != null && dataSourceInfo.contains( "DataSource=" + xmlaDef.getCatalog() + ";" )) {
                    result = (MondrianConnection)olapConnectionService.dereference(context, xmlaDef.getMondrianConnection());
                    log.debug("Connection Found for catalog: " + xmlaDef.getCatalog());

                    getOlapManagementService().notifySchemaUse(null, result);
                    break;
                }
            }
        }
        if (result == null) {
            log.error("Mondrian XMLA Definition not found for name: " + dataSourceInfo);
            throw new RuntimeException("Mondrian XMLA Definition not found for name: " + dataSourceInfo);
        }

        return result;
    }

}
