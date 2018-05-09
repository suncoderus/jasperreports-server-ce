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
package com.jaspersoft.jasperserver.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

/**
 * @author srosen
 *
 * Deletes the full production data for CE using the TestNG framework
 * 
 * 2013-08-14 tkavanagh
 *   Sample report deletion has been removed from this file for the reports found 
 *   in /reports/samples. As of 5.5 release, these reports have been converted to 
 *   test resources (not part of standard sample resources). 
 */
public class FullDataDeleteTestNG extends BaseServiceSetupTestNG {

    protected final Log m_logger = LogFactory.getLog(FullDataDeleteTestNG.class);

	protected void onSetUp() throws Exception {
		m_logger.info("onSetUp() called");
	}

	public void onTearDown() {
		m_logger.info("onTearDown() called");
	}

    /*
    * This method is the starting point for deleting resources that comprise the Full production
    * data for the Community Edition (CE) product.
    *   Full Data == Sample Data
    */
    @Test()
	public void deleteFullDataResources() throws Exception {
        m_logger.info("deleteFullDataResources() called");

        // delete a interactive folder and its resources
        deleteInteractiveReportResources();
        deleteOlapConnectionResources();
        deleteUserAuthorityServiceTestResources();
        deleteContentRepositoryTestResources();
        deleteSchedulerResources();
        deleteHibernateRepositoryReportResources();
        deleteHibernateRepositoryDataSourceResources();
        deleteDefaultDomainWhitelist();
    }

    // move this to configuration file if people want to use
    // this for testing on a regular basis...
    private final boolean DO_PSEUDO_JAPANESE_FOODMART = false;

    private void deleteOlapConnectionResources() {
        m_logger.info("deleteOlapConnectionResources() called");

        // delete olap connection test metadata from the repository
        deleteMondrianFoodmartReport();
        deleteEmployees();
        deleteEmployeeAccounts();
        deleteAnalysisReportsFolder();

        if (DO_PSEUDO_JAPANESE_FOODMART) {
            deleteFoodmartJaOlapUnit();
            deleteFoodmartJaMondrianConnectionResource();
            deleteFoodmartJaDataSourceResource();
            deleteFoodmartJaSchemaResource();
        }

        deleteSugarCRMMondrianXMLADefinitionResource();
        deleteFoodmartMondrianXMLADefinitionResource();

        deleteSugarCRMXmlaOlapUnit();
        deleteSugarCRMOlapUnit();
        deleteFoodmartOlapUnit();

        deleteSugarCRMXMLAConnectionResource();
        deleteSugarCRMMondrianConnectionResource();

        deleteFoodmartXMLAConnectionResource();
        deleteFoodmartMondrianConnectionResource();
        deleteSugarFoodmartDataSourceResourceVirtual();

        deleteSugarCRMDataSourceResourceJNDI();
        deleteSugarCRMDataSourceResource();

        deleteFoodmartJNDIDataSourceResource();
        deleteFoodmartJDBCDataSourceResource();

        deleteSugarCRMSchemaUpperResource();
        deleteSugarCRMSchemaResource();

        deleteFoodmartSchemaUpperResource();
        deleteFoodmartSchemaResource();
    }

    protected void deleteMondrianFoodmartReport() {
        m_logger.info("deleteMondrianFoodmartReport() => deleting /analysis/reports/FoodmartSalesMondrianReport");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/reports/FoodmartSalesMondrianReport");
    }

    private void deleteEmployees() {
        m_logger.info("deleteEmployees() => deleting /analysis/reports/Employees");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/reports/Employees");
    }

    private void deleteEmployeeAccounts() {
        m_logger.info("deleteEmployeeAccounts() => deleting /analysis/reports/EmployeeAccounts");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/reports/EmployeeAccounts");
    }

    protected void deleteAnalysisReportsFolder() {
        m_logger.info("deleteAnalysisReportsFolder() => deleting /analysis/reports");
        getUnsecureRepositoryService().deleteFolder(null, "/analysis/reports");
    }

    protected void deleteFoodmartJaOlapUnit() {
        m_logger.info("deleteFoodmartJaOlapUnit() => deleting /analysis/views/FoodmartJa_sample_unit_1");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/views/FoodmartJa_sample_unit_1");
    }

    protected void deleteFoodmartJaMondrianConnectionResource() {
        m_logger.info("deleteFoodmartJaMondrianConnectionResource() => deleting /analysis/connections/FoodmartJa");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/connections/FoodmartJa");
    }

    protected void deleteFoodmartJaDataSourceResource() {
        m_logger.info("deleteFoodmartJaDataSourceResource() => deleting /analysis/datasources/FoodmartJaDataSource");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/datasources/FoodmartJaDataSource");
    }

    protected void deleteFoodmartJaSchemaResource() {
        m_logger.info("deleteFoodmartJaSchemaResource() => deleting /analysis/schemas/FoodmartJaSchema");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/schemas/FoodmartJaSchema");
    }

    protected void deleteSugarCRMMondrianXMLADefinitionResource() {
        m_logger.info("deleteSugarCRMMondrianXMLADefinitionResource() => deleting /analysis/xmla/definitions/SugarCRMXmlaDefinition");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/xmla/definitions/SugarCRMXmlaDefinition");
    }

    protected void deleteFoodmartMondrianXMLADefinitionResource() {
        m_logger.info("deleteFoodmartMondrianXMLADefinitionResource() => deleting /analysis/xmla/definitions/FoodmartXmlaDefinition");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/xmla/definitions/FoodmartXmlaDefinition");
    }

    protected void deleteSugarCRMXmlaOlapUnit() {
        m_logger.info("deleteSugarCRMXmlaOlapUnit() => deleting /analysis/views/SugarCRM_xmla_sample");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/views/SugarCRM_xmla_sample");
    }

    protected void deleteSugarCRMOlapUnit() {
        m_logger.info("deleteSugarCRMOlapUnit() => deleting /analysis/views/SugarCRM_sample");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/views/SugarCRM_sample");
    }

    protected void deleteFoodmartOlapUnit() {
        m_logger.info("deleteFoodmartOlapUnit() => deleting /analysis/views/Foodmart_sample");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/views/Foodmart_sample");
    }

    protected void deleteSugarCRMXMLAConnectionResource() {
        m_logger.info("deleteSugarCRMXMLAConnectionResource() => deleting /analysis/connections/SugarCRMXmlaConnection");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/connections/SugarCRMXmlaConnection");
    }

    protected void deleteSugarCRMMondrianConnectionResource() {
        m_logger.info("deleteSugarCRMMondrianConnectionResource() => deleting /analysis/connections/SugarCRM");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/connections/SugarCRM");
    }

    protected void deleteFoodmartXMLAConnectionResource() {
        m_logger.info("deleteFoodmartXMLAConnectionResource() => deleting /analysis/connections/FoodmartXmlaConnection");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/connections/FoodmartXmlaConnection");
    }

    protected void deleteFoodmartMondrianConnectionResource() {
        m_logger.info("FoodmartMondrianConnectionResource() => deleting /analysis/connections/Foodmart");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/connections/Foodmart");
    }

    protected void deleteSugarCRMDataSourceResourceJNDI() {
        m_logger.info("deleteSugarCRMDataSourceResourceJNDI() => deleting /analysis/datasources/SugarCRMDataSourceJNDI");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/datasources/SugarCRMDataSourceJNDI");
    }

    protected void deleteSugarCRMDataSourceResource() {
        m_logger.info("deleteSugarCRMDataSourceResource() => deleting /analysis/datasources/SugarCRMDataSource");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/datasources/SugarCRMDataSource");
    }

    protected void deleteFoodmartJNDIDataSourceResource() {
        m_logger.info("deleteFoodmartJNDIDataSourceResource() => deleting /analysis/datasources/FoodmartDataSourceJNDI");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/datasources/FoodmartDataSourceJNDI");
    }

    protected void deleteFoodmartJDBCDataSourceResource() {
        m_logger.info("deleteFoodmartJDBCDataSourceResource() => deleting /analysis/datasources/FoodmartDataSource");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/datasources/FoodmartDataSource");
    }

    protected void deleteSugarFoodmartDataSourceResourceVirtual() {
        m_logger.info("deleteSugarCRMDataSourceResourceVirtual() => deleting /datasources/SugarCRMDataSourceVirtual");
        getUnsecureRepositoryService().deleteResource(null, "/datasources/SugarFoodmartVDS");
    }

    protected void deleteSugarCRMSchemaUpperResource() {
        m_logger.info("deleteSugarCRMSchemaUpperResource() => deleting /analysis/schemas/SugarCRMSchemaUpper");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/schemas/SugarCRMSchemaUpper");
    }

    protected void deleteSugarCRMSchemaResource() {
        m_logger.info("deleteSugarCRMSchemaResource() => deleting /analysis/schemas/SugarCRMSchema");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/schemas/SugarCRMSchema");
    }

    protected void deleteFoodmartSchemaUpperResource() {
        m_logger.info("deleteFoodmartSchemaUpperResource() => deleting /analysis/schemas/FoodmartSchemaUpper");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/schemas/FoodmartSchemaUpper");
    }

    protected void deleteFoodmartSchemaResource() {
        m_logger.info("deleteFoodmartSchemaResource() => deleting /analysis/schemas/FoodmartSchema");
        getUnsecureRepositoryService().deleteResource(null, "/analysis/schemas/FoodmartSchema");
    }

    private void deleteUserAuthorityServiceTestResources() {
        m_logger.info("deleteUserAuthorityServiceTestResources() called");

        User theUser = getUser(BaseServiceSetupTestNG.USER_JOEUSER);
        removeRole(theUser, BaseServiceSetupTestNG.ROLE_USER);
        deleteUser(BaseServiceSetupTestNG.USER_JOEUSER);
    }

    private void deleteContentRepositoryTestResources() {
        m_logger.info("deleteContentRepositoryTestResources() called");

        m_logger.info("deleteContentRepositoryTestResources() => deleting /ContentFiles/xls");
        getUnsecureRepositoryService().deleteFolder(null, "/ContentFiles/xls");

        m_logger.info("deleteContentRepositoryTestResources() => deleting /ContentFiles/pdf");
        getUnsecureRepositoryService().deleteFolder(null, "/ContentFiles/pdf");

        m_logger.info("deleteContentRepositoryTestResources() => deleting /ContentFiles/html");
        getUnsecureRepositoryService().deleteFolder(null, "/ContentFiles/html");

        m_logger.info("deleteContentRepositoryTestResources() => deleting /ContentFiles");
        getUnsecureRepositoryService().deleteFolder(null, "/ContentFiles");
    }

    private void deleteHibernateRepositoryReportResources()  {
        m_logger.info("deleteHibernateRepositoryReportResources() called");

        // delete the OLAP analysis/views folder
        m_logger.info("deleteHibernateRepositoryReportResources() => deleting /analysis/views");
        getUnsecureRepositoryService().deleteFolder(null, "/analysis/views");

        // delete the OLAP analysis/datasources folder
        m_logger.info("deleteHibernateRepositoryReportResources() => deleting /analysis/datasources");
        getUnsecureRepositoryService().deleteFolder(null, "/analysis/datasources");

        // delete the OLAP analysis/schemas folder
        m_logger.info("deleteHibernateRepositoryReportResources() => deleting /analysis/schemas");
        getUnsecureRepositoryService().deleteFolder(null, "/analysis/schemas");

        // delete the OLAP analysis/connections folder
        m_logger.info("deleteHibernateRepositoryReportResources() => deleting /analysis/connections");
        getUnsecureRepositoryService().deleteFolder(null, "/analysis/connections");

        // delete the main OLAP analysis folder
        m_logger.info("deleteHibernateRepositoryReportResources() => deleting /analysis");
        getUnsecureRepositoryService().deleteFolder(null, "/analysis");

        // 2013-08-14: delete of /reports/samples reports removed 

        // delete a sample image and images folder
        deleteImagesFolderAndSampleImage();

        // delete the reports folder
        m_logger.info("deleteHibernateRepositoryReportResources() => deleting /reports");
        getUnsecureRepositoryService().deleteFolder(null, "/reports");
    }

    private void deleteHibernateRepositoryDataSourceResources()  {
        m_logger.info("deleteHibernateRepositoryDataSourceResources() called");

        // delete the JdbcDS, the RepoDS, and the JndiDS
        // finally, delete the main datasources folder
        deleteJdbcDS();
        deleteRepoDS();
        deleteJndiDS();

        m_logger.info("deleteHibernateRepositoryDataSourceResources() => deleting /datasources");
        getUnsecureRepositoryService().deleteFolder(null, "/datasources");
    }

    private void deleteJndiDS() {
        getUnsecureRepositoryService().deleteResource(null, "/datasources/JServerJNDIDS");
    }

    private void deleteRepoDS() {
        getUnsecureRepositoryService().deleteResource(null, "/datasources/repositoryDS");
    }

    private void deleteJdbcDS() {
        getUnsecureRepositoryService().deleteResource(null, "/datasources/JServerJdbcDS");
    }


    private void deleteImagesFolderAndSampleImage() {
        m_logger.info("deleteImagesFolderAndSampleImage() => deleting /images/JRLogo");
        getUnsecureRepositoryService().deleteResource(null, "/images/JRLogo");

        m_logger.info("deleteImagesFolderAndSampleImage() => deleting /images");
        getUnsecureRepositoryService().deleteFolder(null, "/images");
    }

    private void deleteInteractiveReportResources() {
        deleteCustomersReport();
        deleteCustomersData();
        deleteCustomersDataAdapter();

        deleteTableReport();
        deleteCsvData();
        deleteCsvDataAdapter();

        deleteMapReport();

        m_logger.info("deleteInteractiveFolderAndReportResources() => deleting /reports/interactive");
        getUnsecureRepositoryService().deleteFolder(null, "/reports/interactive");
    }

    private void deleteCustomersReport() {
        m_logger.info("deleteCustomersReport() => deleting /reports/interactive/CustomersReport");
        getUnsecureRepositoryService().deleteResource(null, "/reports/interactive/CustomersReport");
    }

    private void deleteCustomersData() {
        m_logger.info("deleteCustomersData() => deleting /reports/interactive/CustomersData");
        getUnsecureRepositoryService().deleteResource(null, "/reports/interactive/CustomersData");
    }

    private void deleteCustomersDataAdapter() {
        m_logger.info("deleteCustomersDataAdapter() => deleting /reports/interactive/CustomersDataAdapter");
        getUnsecureRepositoryService().deleteResource(null, "/reports/interactive/CustomersDataAdapter");
    }

    private void deleteTableReport() {
        m_logger.info("deleteTableReport() => deleting /reports/interactive/TableReport");
        getUnsecureRepositoryService().deleteResource(null, "/reports/interactive/TableReport");
    }

    private void deleteCsvData() {
        m_logger.info("deleteCsvData() => deleting /reports/interactive/CsvData");
        getUnsecureRepositoryService().deleteResource(null, "/reports/interactive/CsvData");
    }

    private void deleteCsvDataAdapter() {
        m_logger.info("deleteCsvDataAdapter() => deleting /reports/interactive/CsvDataAdapter");
        getUnsecureRepositoryService().deleteResource(null, "/reports/interactive/CsvDataAdapter");
    }

    private void deleteMapReport() {
        m_logger.info("deleteMapReport() => deleting /reports/interactive/MapReport");
        getUnsecureRepositoryService().deleteResource(null, "/reports/interactive/MapReport");
    }

    private void deleteTableModelDSReport() {
        m_logger.info("deleteTableModelDSReport() => deleting /reports/samples/DataSourceTableModel");
        getUnsecureRepositoryService().deleteResource(null, "/reports/samples/DataSourceTableModel");
    }

    private void deleteSchedulerResources() {
        getReportScheduler().deleteCalendar(HOLIDAY_CALENDAR_NAME);
    }
}
