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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapManagementService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import mondrian.olap.Util;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.util.Pair;
import mondrian.xmla.DataSourcesConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author vsabadosh
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class XmlaContentFinderImplTest {
    @Mock
    private RepositoryService repository;
    @Mock
    private TenantService tenantService;
    @Mock
    private OlapConnectionService olapConnectionService;
    @Mock
    private OlapManagementService olapManagementService;
    @Spy
    private XmlaContentFinderImpl xmlaContentFinder;

    private List<String> resourceUris = Arrays.asList("/organizations/org1/resources/resource1",
            "/organizations/org2/resources/resource2", "/organizations/org3/resources/resource3");
    private List<String> catalogs = Arrays.asList("Catalog1", "Catalog2", "Catalog3");
    private List<String> tenants = Arrays.asList("org1", "org2", "org3");

    @Before
    public void initialize() {
        List<ResourceLookup> resourceLookups = new ArrayList<ResourceLookup>();
        int index = 0;
        for (String resourceUri : resourceUris) {
            resourceLookups.add(mockResourceLookup(resourceUri));
            mockTenantService(tenants.get(index), resourceUri);
            mockMondrianXMLADefinition(catalogs.get(index++), resourceUri);
        }
        doReturn(resourceLookups.toArray(new ResourceLookup[3])).when(repository).findResource(any(ExecutionContext.class), any(FilterCriteria.class));
        doReturn(resourceLookups).when(repository).loadResourcesList(any(ExecutionContext.class), any(FilterCriteria.class));

        doReturn(olapManagementService).when(xmlaContentFinder).getOlapManagementService();

        xmlaContentFinder.setOlapConnectionService(olapConnectionService);
        xmlaContentFinder.setRepository(repository);
        xmlaContentFinder.setTenantService(tenantService);
    }

    @Test
    public void getDataSources_ServerContainsOneDataSourceWithThreeCatalogs_ReturnOneDataSource() {
        DataSourcesConfig.DataSources actualDataSources = xmlaContentFinder.getDataSources();
        DataSourcesConfig.DataSource actualDataSource = actualDataSources.dataSources[0];
        DataSourcesConfig.Catalog[] actualCatalogs = actualDataSource.catalogs.catalogs;

        assertTrue(actualDataSources.dataSources.length == 1);

        assertEquals("Provider=" + XmlaContentFinderImpl.providerName + ";DataSource=" +
                XmlaContentFinderImpl.dataSourceName, actualDataSource.name);
        assertEquals(XmlaContentFinderImpl.dataSourceDescription, actualDataSource.description);
        assertEquals(null, actualDataSource.url);
        assertEquals(null, actualDataSource.dataSourceInfo);
        assertEquals(XmlaContentFinderImpl.providerName, actualDataSource.providerName);
        assertEquals(XmlaContentFinderImpl.providerName, actualDataSource.providerName);
        assertEquals(DataSourcesConfig.DataSource.PROVIDER_TYPE_MDP, actualDataSource.providerType);
        assertEquals(DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED, actualDataSource.authenticationMode);

        assertTrue(actualCatalogs.length == 3);

        Map<String, DataSourcesConfig.Catalog> catalogMap = new HashMap<String, DataSourcesConfig.Catalog>();
        for (DataSourcesConfig.Catalog catalog : actualCatalogs) {
            catalogMap.put(catalog.name, catalog);
        }

        int index = 0;
        for (String catalogName : catalogs) {
            assertCatalog(catalogMap.get(catalogName), catalogName, tenants.get(index++));
        }
    }

    @Test
    public void createCatalogConfigOutOfDefinition_ServerContainsRequiredCatalog_ReturnCatalog() {
        String catalogName = catalogs.get(0);
        String resourceUri = resourceUris.get(0);
        String tenantId = tenants.get(0);

        mockMondrianXMLADefinition(catalogName, resourceUri);
        mockTenantService(tenantId, resourceUri);

        DataSourcesConfig.Catalog catalog = xmlaContentFinder.
                createCatalogConfigOutOfDefinition(mockMondrianXMLADefinition(catalogName, resourceUri));

        assertCatalog(catalog, catalogName, tenantId);
    }

    @Test
    public void lookupXmlaConnection_ServerContainsRequiredCatalog_ReturnMondrianConnection() {
        int index = 0;
        List<MondrianConnection> mondrianConnections = new ArrayList<MondrianConnection>();
        for (String catalogName : catalogs) {
            MondrianXMLADefinition mondrianXMLADefinition = mockMondrianXMLADefinition(catalogName, resourceUris.get(index));
            MondrianConnection mondrianConnection = mock(MondrianConnection.class);
            ResourceReference resourceReference = mock(ResourceReference.class);
            mondrianConnections.add(mondrianConnection);
            doReturn(resourceReference).when(mondrianXMLADefinition).getMondrianConnection();
            doReturn(mondrianConnection).when(olapConnectionService).dereference(null, resourceReference);

            Tenant tenantMock = mock(Tenant.class);
            doReturn("TenantFolderUri").when(tenantMock).getTenantFolderUri();
            doReturn(tenantMock).when(tenantService).getTenant(null, tenants.get(index++));
        }

        MondrianConnection actualMondrianConnection = xmlaContentFinder.
                lookupXmlaConnection(null, "Provider=Mondrian;DataSource=" + catalogs.get(2) + ";TenantID=" + tenants.get(2));

        assertEquals(mondrianConnections.get(2), actualMondrianConnection);
    }

    @Test(expected = RuntimeException.class)
    public void lookupXmlaConnection_ServerDoesNotContainsRequiredCatalog_ThrowException() {
        int index = 0;
        List<MondrianConnection> mondrianConnections = new ArrayList<MondrianConnection>();
        for (String catalogName : catalogs) {
            MondrianXMLADefinition mondrianXMLADefinition = mockMondrianXMLADefinition(catalogName, resourceUris.get(index));
            MondrianConnection mondrianConnection = mock(MondrianConnection.class);
            ResourceReference resourceReference = mock(ResourceReference.class);
            mondrianConnections.add(mondrianConnection);
            doReturn(resourceReference).when(mondrianXMLADefinition).getMondrianConnection();
            doReturn(mondrianConnection).when(olapConnectionService).dereference(null, resourceReference);

            Tenant tenantMock = mock(Tenant.class);
            doReturn("TenantFolderUri").when(tenantMock).getTenantFolderUri();
            doReturn(tenantMock).when(tenantService).getTenant(null, tenants.get(index++));
        }

        xmlaContentFinder.lookupXmlaConnection(null, "Provider=Mondrian;DataSource=UNDEFINED_CATALOG;" + "TenantID=" + tenants.get(2));
    }

    @Test
    public void getMondrianConnectionProperties_ServerContainsRequiredCatalog_ReturnGeneratedMondrianConnProperties() {
        Map<String, Object> dataSourceProperties = new HashMap<String, Object>();
        String validDataSourceInfo = "Provider=Mondrian;DataSource=Foodmart;TenantID=organization_1";
        dataSourceProperties.put("AuthenticationMode", DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED);
        dataSourceProperties.put("ProviderName", XmlaContentFinderImpl.providerName);
        dataSourceProperties.put("ProviderType", DataSourcesConfig.DataSource.PROVIDER_TYPE_MDP);
        dataSourceProperties.put("URL", "http://localhost:8080/jasperserver-pro/xmla");
        dataSourceProperties.put("DataSourceInfo", validDataSourceInfo);
        dataSourceProperties.put("DataSourceDescription", XmlaContentFinderImpl.dataSourceDescription);

        Util.PropertyList connectionProperties = new Util.PropertyList();
        connectionProperties.put("Provider", "mondrian");
        connectionProperties.put("olap4jDriver", "mondrian.olap4j.MondrianOlap4jDriver");
        connectionProperties.put("urlPrefix", "jdbc:mondrian:");
        connectionProperties.put("Catalog", "repoint:/organizations/organization_1/analysis/schemas/FoodmartSchema");
        connectionProperties.put("UseContentChecksum", "false");
        connectionProperties.put("DataSource", "java:comp/env/jdbc/foodmart");

        MondrianConnection mondrianConnection = mock(MondrianConnection.class);

        doReturn(mondrianConnection).when(xmlaContentFinder).lookupXmlaConnection(null, validDataSourceInfo);
        doReturn(connectionProperties).when(olapConnectionService).getMondrianConnectProperties(null, mondrianConnection);

        Properties actualProperties = xmlaContentFinder.getMondrianConnectionProperties(dataSourceProperties, null);
        for (Pair pair : connectionProperties) {
            assertTrue(actualProperties.getProperty((String)pair.getKey()) != null);
            assertEquals(pair.getValue(), actualProperties.getProperty((String) pair.getKey()));
        }

        actualProperties = xmlaContentFinder.getMondrianConnectionProperties(dataSourceProperties, "SOME ROLE");
        for (Pair pair : connectionProperties) {
            assertTrue(actualProperties.getProperty((String)pair.getKey()) != null);
            assertEquals(pair.getValue(), actualProperties.getProperty((String) pair.getKey()));
        }
        assertEquals("SOME ROLE", actualProperties.getProperty(RolapConnectionProperties.Role.toString()));
    }

    private MondrianXMLADefinition mockMondrianXMLADefinition(String catalogName, String resourceUri) {
        MondrianXMLADefinition mondrianXMLADefinition = mock(MondrianXMLADefinition.class);
        doReturn(catalogName).when(mondrianXMLADefinition).getCatalog();
        doReturn(resourceUri).when(mondrianXMLADefinition).getURIString();
        doReturn(mondrianXMLADefinition).when(repository).getResource(null, resourceUri);

        return mondrianXMLADefinition;
    }

    private ResourceLookup mockResourceLookup(String resourceUri) {
        ResourceLookup resourceLookup = mock(ResourceLookup.class);
        doReturn(resourceUri).when(resourceLookup).getURIString();
        return resourceLookup;
    }

    private void mockTenantService(String tenantId, String resourceUri) {
        Tenant tenant = mock(Tenant.class);
        doReturn(tenantId).when(tenant).getId();
        doReturn(tenant).when(tenantService).getTenantBasedOnRepositoryUri(null, resourceUri);
    }

    private void assertCatalog(DataSourcesConfig.Catalog catalog, String catalogName, String tenantId) {
        assertEquals(catalogName, catalog.name);
        assertEquals("Provider=" + XmlaContentFinderImpl.providerName + ";DataSource=" + catalogName + ";TenantID="
                + tenantId + ";", catalog.dataSourceInfo);
        assertEquals(tenantId, catalog.definition);
    }

}