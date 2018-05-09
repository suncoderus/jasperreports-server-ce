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

import mondrian.olap.MondrianException;
import mondrian.olap.MondrianServer;
import mondrian.rolap.RolapConnection;
import mondrian.rolap.RolapConnectionProperties;
import mondrian.xmla.DataSourcesConfig;
import mondrian.xmla.XmlaHandler;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.olap4j.OlapConnection;

import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class XmlaRepositoryImplTest {
    private final String TEST_ORGANIZATION_1 = "Test_Organization_1";
    private final String TEST_ORGANIZATION_2 = "Test_Organization_2";
    private final String catalogDelimiter = ";";
    private final String TEST_XMLA_DATA_SOURCE_NAME = "Provider=Mondrian;DataSource=JRS";

    private Properties connectionProperties;
    private DataSourcesConfig.DataSource dataSource;
    private Map<String, List<DataSourcesConfig.Catalog>> catalogsByTenantId;

    @Mock
    private XmlaContentFinder xmlaContentFinder;
    @Mock
    private OlapConnection olapConnection;
    @Mock
    private MondrianServer mondrianServer;
    @Spy
    private XmlaRepositoryImpl xmlaRepository;

    @Before
    public void initialize() {
        dataSource = new DataSourcesConfig.DataSource();
        dataSource.description = "JasperServer Mondrian XMLA Definition";
        dataSource.url = XmlaServletImpl.SERVER_URL;
        dataSource.providerName = "Mondrian";
        dataSource.providerType = DataSourcesConfig.DataSource.PROVIDER_TYPE_MDP;
        dataSource.name = TEST_XMLA_DATA_SOURCE_NAME;
        dataSource.dataSourceInfo = null;
        dataSource.authenticationMode = DataSourcesConfig.DataSource.AUTH_MODE_UNAUTHENTICATED;

        catalogsByTenantId = new HashMap<String, List<DataSourcesConfig.Catalog>>();

        DataSourcesConfig.Catalogs cs = new DataSourcesConfig.Catalogs();
        cs.catalogs = new DataSourcesConfig.Catalog[5];

        List<DataSourcesConfig.Catalog> catalogs = new ArrayList<DataSourcesConfig.Catalog>();
        catalogs.add(createCatalog("Catalog1", TEST_ORGANIZATION_1));
        catalogsByTenantId.put(TEST_ORGANIZATION_1, catalogs);

        catalogsByTenantId.get(TEST_ORGANIZATION_1).add(createCatalog("Catalog2", TEST_ORGANIZATION_1));
        catalogsByTenantId.get(TEST_ORGANIZATION_1).add(createCatalog("Catalog3", TEST_ORGANIZATION_1));

        catalogs = new ArrayList<DataSourcesConfig.Catalog>();
        catalogs.add(createCatalog("Catalog4", TEST_ORGANIZATION_2));
        catalogsByTenantId.put(TEST_ORGANIZATION_2, catalogs);

        catalogs = new ArrayList<DataSourcesConfig.Catalog>();
        catalogs.add(createCatalog("Catalog5", null));
        catalogsByTenantId.put(null, catalogs);

        dataSource.catalogs = cs;

        connectionProperties = new Properties();
        connectionProperties.put("urlPrefix", "jdbc:mondrian:");
        connectionProperties.put("DataSource", "java:/comp/env/jdbc/foodmart");
        connectionProperties.put("olap4jDriver", "mondrian.olap4j.MondrianOlap4jDriver");
        connectionProperties.put("UseContentChecksum", "false");
        connectionProperties.put("Catalog", "repoint:/organizations/organization_1/analysis/shemas/FoodmartSchema");
        connectionProperties.put("Provider", "mondrian");

        xmlaRepository.setXmlaContentFinder(xmlaContentFinder);
        xmlaRepository.setCatalogDelimiter(catalogDelimiter);

        doReturn("TestCurrentUserSessionId").when(xmlaRepository).getCurrentUserSessionId();
    }

    @Test
    public void getDatabaseNames_ServerContainsOneDataSource_ReturnOneDatabaseName() {
        doReturn(getDataSourcesByTenantId(TEST_ORGANIZATION_1)).when(xmlaContentFinder).getDataSources();
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();

        List<String> databaseNames = xmlaRepository.getDatabaseNames(any(RolapConnection.class));
        assertEquals(1, databaseNames.size());
    }

    @Test
    public void getDatabaseNames_ServerDoesNotContainsAnyDataSource_ReturnEmptyList() {
        doReturn(null).when(xmlaContentFinder).getDataSources();
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();

        List<String> databaseNames = xmlaRepository.getDatabaseNames(any(RolapConnection.class));
        assertEquals(0, databaseNames.size());
    }

    @Test
    public void getDatabases_ServerDoesNotContainsDataSource_ReturnEmptyList() {
        doReturn(null).when(xmlaContentFinder).getDataSources();
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();

        List<Map<String, Object>> databaseNames = xmlaRepository.getDatabases(any(RolapConnection.class));
        assertEquals(0, databaseNames.size());
    }

    @Test
    public void getDatabases_ServerContainsOneDataSource_ReturnOneDataSourceProperties() {
        doReturn(getDataSourcesByTenantId(TEST_ORGANIZATION_1)).when(xmlaContentFinder).getDataSources();
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();

        List<Map<String, Object>> dataBasePropsList = xmlaRepository.getDatabases(any(RolapConnection.class));
        assertEquals(1, dataBasePropsList.size());

        assertEquals(dataBasePropsList.get(0).get("DataSourceName"), dataSource.name);
        assertEquals(dataBasePropsList.get(0).get("URL"), dataSource.url);
        assertEquals(dataBasePropsList.get(0).get("DataSourceInfo"), dataSource.name);
        assertEquals(dataBasePropsList.get(0).get("ProviderType"), dataSource.providerType);
        assertEquals(dataBasePropsList.get(0).get("AuthenticationMode"), dataSource.authenticationMode);
    }

    @Test(expected = RuntimeException.class)
    public void getCatalogNames_ServerDoesNotContainsDataSource_ThrowException() {
        doReturn(null).when(xmlaContentFinder).getDataSources();
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();

        xmlaRepository.getCatalogNames(any(RolapConnection.class), any(String.class));
    }

    @Test(expected = RuntimeException.class)
    public void getCatalogNames_ServerDoesContainsOneDataSource_ThrowException() {
        doReturn(null).when(xmlaContentFinder).getDataSources();
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();

        xmlaRepository.getCatalogNames(null, "incorrectDataBaseName");
    }

    @Test
    public void getCatalogNames_ServerContains5CatalogsAndCurrentIsSuperUser_ReturnFiveCatalogNames() {
        DataSourcesConfig.DataSources datasources = getDataSourcesByTenantId(null);
        //Current user is super user
        doReturn(null).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(datasources).when(xmlaContentFinder).getDataSources();

        List<String> catalogNames = xmlaRepository.getCatalogNames(null, TEST_XMLA_DATA_SOURCE_NAME);
        assertEquals(5, catalogNames.size());
        for (DataSourcesConfig.Catalog catalog : datasources.dataSources[0].catalogs.catalogs) {
            if (!catalogNames.contains(generateCatalogNameForSuperUser(catalog))) {
                fail("Expected and actual catalogs don't match");
            }
        }
    }

    @Test
    public void getCatalogNames_ServerContains2CatalogsUnderOrg2AndCurrentUserFromOrg2_ReturnTwoCatalogNames() {
        doReturn(TEST_ORGANIZATION_2).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(getDataSourcesByTenantId(TEST_ORGANIZATION_2)).when(xmlaContentFinder).getDataSources();

        List<String> catalogNames = xmlaRepository.getCatalogNames(null, TEST_XMLA_DATA_SOURCE_NAME);
        assertEquals(2, catalogNames.size());
    }

    @Test
    public void getConnection_ServerContains2CatalogsUnderOrg1AndInputParamsAreValid_ReturnValidOlapConnection() throws SQLException{
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(getDataSourcesByTenantId(TEST_ORGANIZATION_1)).when(xmlaContentFinder).getDataSources();
        doReturn(connectionProperties).when(xmlaContentFinder).getMondrianConnectionProperties(any(Map.class), any(String.class));
        doReturn(this.olapConnection).when(xmlaRepository).getOlapConnection(connectionProperties);

        final String localeValue = "en_US";
        Properties externalProperties = new Properties();
        externalProperties.put(XmlaHandler.JDBC_LOCALE, localeValue);

        OlapConnection olapConnection = xmlaRepository.getConnection(mondrianServer, dataSource.name,
                catalogsByTenantId.get(TEST_ORGANIZATION_1).get(0).name, null, externalProperties);

        verify(xmlaContentFinder).getMondrianConnectionProperties(any(Map.class), any(String.class));
        verify(xmlaRepository).getOlapConnection(connectionProperties);
        assertEquals(this.olapConnection, olapConnection);
        //Check whether Instance name was added during logic.
        assertTrue(connectionProperties.getProperty(RolapConnectionProperties.Instance.name()) != null);
        assertTrue(connectionProperties.getProperty(XmlaHandler.JDBC_LOCALE).equals(localeValue));

        connectionProperties.remove(RolapConnectionProperties.Instance.name());
        connectionProperties.remove(XmlaHandler.JDBC_LOCALE);

        xmlaRepository.getConnection(mondrianServer, dataSource.name,
                catalogsByTenantId.get(TEST_ORGANIZATION_1).get(0).name, null, null);
        assertFalse(connectionProperties.containsKey(XmlaHandler.JDBC_LOCALE));
    }

    @Test(expected = MondrianException.class)
    public void getConnection_ServerDoesNotContainsDataSource_MondrianException() throws SQLException {
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(null).when(xmlaContentFinder).getDataSources();

        xmlaRepository.getConnection(mondrianServer, "Some Database name",
                "Some Catalog", null, null);
    }

    @Test(expected = SQLException.class)
    public void getConnection_ServerDoesNotContainsDataSource_ThrowSQLException() throws SQLException {
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(null).when(xmlaContentFinder).getDataSources();

        xmlaRepository.getConnection(mondrianServer, null, null, null, null);
    }

    @Test(expected = MondrianException.class)
    public void getConnection_ServerContainsOneDataSourceAndInputDatabaseNameIsIncorrect_ThrowException() throws SQLException {
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(getDataSourcesByTenantId(TEST_ORGANIZATION_1)).when(xmlaContentFinder).getDataSources();

        xmlaRepository.getConnection(mondrianServer, "IncorrectDataBaseName", "Some Catalog", null, null);
    }

    @Test(expected = MondrianException.class)
    public void getConnection_ServerContainsOneDataSourceWith5CatalogsInputCatalogNameIsIncorrect_ThrowException() throws SQLException {
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(getDataSourcesByTenantId(TEST_ORGANIZATION_1)).when(xmlaContentFinder).getDataSources();

        xmlaRepository.getConnection(mondrianServer, dataSource.name, "IncorrectCatalogName", null, null);
    }

    @Test(expected = MondrianException.class)
    public void getConnection_ServerContainsOneDataSourceWithoutCatalogs_ThrowException() throws SQLException {
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(getDataSourceWithoutCatalogs()).when(xmlaContentFinder).getDataSources();

        xmlaRepository.getConnection(mondrianServer, dataSource.name, "Some Catalog name", null, null);
    }

    @Test(expected = SQLException.class)
    public void getConnection_ServerContainsOneDataSourceWithoutCatalogs_ThrowOlapException() throws SQLException {
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(getDataSourceWithoutCatalogs()).when(xmlaContentFinder).getDataSources();

        xmlaRepository.getConnection(mondrianServer, dataSource.name, null, null, null);
    }

    @Test(expected = MondrianException.class)
    public void getConnection_ServerContainsOneDataSourceWithoutCatalogs_MondrianException() throws SQLException {
        doReturn(TEST_ORGANIZATION_1).when(xmlaRepository).getCurrentUserTenantId();
        doReturn(getDataSourcesByTenantId(TEST_ORGANIZATION_1)).when(xmlaContentFinder).getDataSources();

        xmlaRepository.getConnection(mondrianServer, dataSource.name, "Some catalog name", null, null);
    }

    //Creates specific catalog which belong to target tenant identifier.
    private DataSourcesConfig.Catalog createCatalog(String catalogName, String tenantId) {
        DataSourcesConfig.Catalog catalog = new DataSourcesConfig.Catalog();
        catalog.name = catalogName;
        catalog.dataSourceInfo = "Provider=Mondrian;DataSource=" + catalogName +";";
        if (tenantId != null) {
            catalog.dataSourceInfo += "TenantID=" + tenantId;
            catalog.definition = tenantId;
        }
        return catalog;
    }

    private DataSourcesConfig.DataSources getDataSourcesByTenantId(String tenantId) {
        DataSourcesConfig.DataSources datasources = new DataSourcesConfig.DataSources();
        datasources.dataSources = new DataSourcesConfig.DataSource[1];

        DataSourcesConfig.Catalogs cs = new DataSourcesConfig.Catalogs();
        List<DataSourcesConfig.Catalog> catalogs = new ArrayList<DataSourcesConfig.Catalog>();
        if (tenantId != null) {
            catalogs.addAll(catalogsByTenantId.get(tenantId));
            //Add catalogs from public folder
            catalogs.addAll(catalogsByTenantId.get(null));
        } else {
            for (String key : catalogsByTenantId.keySet()) {
                catalogs.addAll(catalogsByTenantId.get(key));
            }
        }
        cs.catalogs = catalogs.toArray(new DataSourcesConfig.Catalog[catalogs.size()]);

        dataSource.catalogs = cs;
        datasources.dataSources[0] = dataSource;

        return datasources;
    }

    private DataSourcesConfig.DataSources getDataSourceWithoutCatalogs() {
        DataSourcesConfig.DataSources datasources = new DataSourcesConfig.DataSources();
        datasources.dataSources = new DataSourcesConfig.DataSource[1];
        dataSource.catalogs = null;
        datasources.dataSources[0] = dataSource;

        return datasources;
    }

    private String generateCatalogNameForSuperUser(DataSourcesConfig.Catalog catalog) {
        String catalogName = catalog.name;
        if (catalog.definition != null) {
            catalogName += catalogDelimiter + catalog.definition;
        }
        return catalogName;
    }

}
