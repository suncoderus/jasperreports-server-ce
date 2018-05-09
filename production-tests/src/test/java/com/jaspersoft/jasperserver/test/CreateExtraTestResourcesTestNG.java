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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

/**
 * @author tkavanagh
 *
 * Create test resources used for the integration-tests. 
 * The reports used to be part of our sample data given to end users in the 
 * released product. Now the reports are converted to just being test resources.  
 * A number of integration tests expect these reports to exist. These reports
 * will be deleted at the end of the integration-tests run.  
 *
 */
public class CreateExtraTestResourcesTestNG extends BaseServiceSetupTestNG {

    protected final Log m_logger = LogFactory.getLog(CreateExtraTestResourcesTestNG.class);

    @BeforeClass()
	protected void onSetUp() throws Exception {
		m_logger.info("onSetUp() called");
	}

    @AfterClass()
	public void onTearDown() {
		m_logger.info("onTearDown() called");
	}


    /*
     * This method is the starting point for adding extra test resources to be used by 
     * the integration-tests logic. Each resource created should be deleted in the 
     * DeleteExtraTestResourcesTestNG class.
     * 
     */
    @Test()
    public void createExtraTestResources() throws Exception {
        m_logger.info("createExtraTestResources() called");


        Folder reportFolder = getUnsecureRepositoryService().getFolder(null, "/reports");

        // create /reports/samples folder
        Folder samplesFolder = new FolderImpl();
        samplesFolder.setName("samples");
        samplesFolder.setLabel("Samples");
        samplesFolder.setDescription("Samples");
        samplesFolder.setParentFolder(reportFolder);
        getUnsecureRepositoryService().saveFolder(null, samplesFolder);

        // create reports 
        createAllAccounts(samplesFolder);
        createSalesByMonth(samplesFolder);
        createEmployeeAccounts(samplesFolder);
        createEmployees(samplesFolder);
        createFreightReport(samplesFolder); 
        createAllCharts(samplesFolder); 
        createAllCascading(samplesFolder);
        createDepartmentReport(samplesFolder);
    }


    private void createAllAccounts(Folder folder) {
        m_logger.info("createAllAccounts() => creating /reports/samples/AllAccounts");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("AllAccountsReport");
        reportRes.setLabel("All Accounts Jasper Report");
        reportRes.setDescription("All Accounts Jasper Report");
        reportRes.setParentFolder(folder);

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/AllAccounts.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("AllAccounts");
        unit.setLabel("Accounts Report");
        unit.setDescription("All Accounts Report");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        FileResource res2 = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        res2.setFileType(FileResource.TYPE_IMAGE);
        res2.readData(getClass().getResourceAsStream("/images/logo.jpg"));
        res2.setName("AllAccounts_Res2");
        res2.setLabel("AllAccounts_Res2");
        res2.setDescription("AllAccounts_Res2");
        unit.addResource(res2);

        FileResource res3 = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        res3.setFileType(FileResource.TYPE_IMAGE);
        res3.readData(getClass().getResourceAsStream("/images/jasperreports.png"));
        res3.setName("AllAccounts_Res3");
        res3.setLabel("AllAccounts_Res3");
        res3.setDescription("AllAccounts_Res3");
        unit.addResource(res3);

        FileResource res4 = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        res4.setFileType(FileResource.TYPE_IMAGE);
        res4.setReferenceURI("/images/JRLogo");
        setCommon(res4, "LogoLink");
        unit.addResource(res4);

        getUnsecureRepositoryService().saveResource(null, unit);
    }

    private void createSalesByMonth(Folder folder) {
        m_logger.info("createSalesByMonth() => creating /reports/samples/SalesByMonth");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("SalesByMonthReport");
        reportRes.setLabel("Sales By Month Jasper Report");
        reportRes.setDescription("Sales By Month Jasper Report");

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/SalesByMonth.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("SalesByMonth");
        unit.setLabel("Sales By Month Report");
        unit.setDescription("Sales By Month Report");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        FileResource jar = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        jar.setFileType(FileResource.TYPE_JAR);
        jar.readData(getClass().getResourceAsStream("/jars/scriptlet.jar"));
        setCommon(jar, "Scriptlet");
        unit.addResource(jar);

        FileResource img = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        img.setFileType(FileResource.TYPE_IMAGE);
        img.readData(getClass().getResourceAsStream("/images/jasperreports.png"));
        setCommon(img, "Logo");
        unit.addResource(img);

        FileResource subrep = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        subrep.setFileType(FileResource.TYPE_JRXML);
        subrep.readData(getClass().getResourceAsStream("/reports/jasper/SalesByMonthDetail.jrxml"));
        setCommon(subrep, "SalesByMonthDetail");
        unit.addResource(subrep);

        FileResource resBdl = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        resBdl.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl.readData(getClass().getResourceAsStream("/resource_bundles/sales.properties"));
        setCommon(resBdl, "sales.properties");
        unit.addResource(resBdl);

        FileResource resBdl_ro = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        resBdl_ro.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl_ro.readData(getClass().getResourceAsStream("/resource_bundles/sales_ro.properties"));
        setCommon(resBdl_ro, "sales_ro.properties");
        unit.addResource(resBdl_ro);

        InputControl textInputCtrl = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        setCommon(textInputCtrl, "TextInputControl");
        textInputCtrl.setInputControlType(InputControl.TYPE_SINGLE_VALUE);
        textInputCtrl.setMandatory(false);
        textInputCtrl.setReadOnly(false);
        textInputCtrl.setVisible(true);
        //FIXME textInputCtrl.setSize(new Integer(30));
        textInputCtrl.setLabel("Text Input Control");
        textInputCtrl.setName("TextInput");


        DataType dataType = new DataTypeImpl();
        dataType.setName("test");
        dataType.setLabel("test");
        dataType.setDataTypeType(DataType.TYPE_NUMBER);
        textInputCtrl.setDataType(dataType);
        unit.addInputControl(textInputCtrl);

        InputControl checkboxInputControl = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        setCommon(checkboxInputControl, "CheckboxInputControl");
        checkboxInputControl.setInputControlType(InputControl.TYPE_BOOLEAN);
        checkboxInputControl.setMandatory(true);
        checkboxInputControl.setReadOnly(false);
        checkboxInputControl.setVisible(true);
        checkboxInputControl.setLabel("Checkbox Input Control");
        checkboxInputControl.setName("CheckboxInput");
        unit.addInputControl(checkboxInputControl);

        InputControl listInputControl = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        setCommon(listInputControl, "ListInputControl");
        listInputControl.setInputControlType(InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES);
        listInputControl.setMandatory(true);
        listInputControl.setReadOnly(false);
        listInputControl.setVisible(true);
        listInputControl.setLabel("List Input Control");
        listInputControl.setName("ListInput");

        ListOfValues values = (ListOfValues) getUnsecureRepositoryService().newResource(null, ListOfValues.class);
        values.setName("List_of_values");
        values.setLabel("List of values label");
        values.setDescription("List of values description");
        ListOfValuesItem item = new ListOfValuesItemImpl();
        item.setLabel("An item");
        item.setValue("1");
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("Another item");
        item.setValue("2");
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("Yet another item");
        item.setValue("3");
        values.addValue(item);
        listInputControl.setListOfValues(values);

        dataType = new DataTypeImpl();
        dataType.setName("test");
        dataType.setLabel("test");
        dataType.setDataTypeType(DataType.TYPE_TEXT);
        listInputControl.setDataType(dataType);

        unit.addInputControl(listInputControl);

        createDateDatatype();

        InputControl dateInputCtrl = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        setCommon(dateInputCtrl, "DateInput");
        dateInputCtrl.setInputControlType(InputControl.TYPE_SINGLE_VALUE);
        dateInputCtrl.setMandatory(false);
        dateInputCtrl.setReadOnly(false);
        dateInputCtrl.setVisible(true);
        dateInputCtrl.setDataTypeReference("/datatypes/date");
        unit.addInputControl(dateInputCtrl);


        InputControl queryInputCtrl = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        setCommon(queryInputCtrl, "QueryInput");
        queryInputCtrl.setInputControlType(InputControl.TYPE_SINGLE_SELECT_QUERY);
        queryInputCtrl.setMandatory(false);
        queryInputCtrl.setReadOnly(false);
        queryInputCtrl.setVisible(true);
        queryInputCtrl.setQueryValueColumn("user_name");
        queryInputCtrl.addQueryVisibleColumn("first_name");
        queryInputCtrl.addQueryVisibleColumn("last_name");
        Query query = (Query) getUnsecureRepositoryService().newResource(null, Query.class);
        setCommon(query, "testQuery");
        query.setLanguage("sql");
        query.setSql("select user_name, first_name, last_name from users");
        queryInputCtrl.setQuery(query);
        unit.addInputControl(queryInputCtrl);

        unit.setAlwaysPromptControls(false);

        getUnsecureRepositoryService().saveResource(null, unit);
    }

    private void createEmployeeAccounts(Folder folder) {
        m_logger.info("createEmployeeAccounts() => creating /reports/samples/EmployeeAccounts");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        setCommon(reportRes, "EmployeeAccountsJRXML");

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/EmployeeAccounts.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("EmployeeAccounts");
        unit.setLabel("Employee Accounts");
        unit.setDescription("List of Accounts per Employee");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        InputControl empIC = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        empIC.setName("EmployeeID");
        empIC.setLabel("Employee");
        empIC.setMandatory(true);

        Query empQuery = (Query) getUnsecureRepositoryService().newResource(null, Query.class);
        empQuery.setName("EmployeeQuery");
        empQuery.setLabel("Employee Query");
        empQuery.setLanguage("sql");
        empQuery.setSql("SELECT id, user_name FROM users WHERE employee_status = 'Active'");

        empIC.setInputControlType(InputControl.TYPE_SINGLE_SELECT_QUERY);
        empIC.setQuery(empQuery);
        empIC.setQueryValueColumn("id");
        empIC.addQueryVisibleColumn("user_name");

        unit.addInputControl(empIC);
        unit.setAlwaysPromptControls(false);

        getUnsecureRepositoryService().saveResource(null, unit);
    } 

    private void createEmployees(Folder folder) {
        m_logger.info("createEmployees() => creating /reports/samples/Employees");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        setCommon(reportRes, "EmployeesJRXML");

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/Employees.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("Employees");
        unit.setLabel("Employee List");
        unit.setDescription("Employee List");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        getUnsecureRepositoryService().saveResource(null, unit);
    }

    private void createFreightReport(Folder folder) {
        m_logger.info("createFreightReport() => creating /reports/samples/Freight");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        setCommon(reportRes, "ParametersJRXML");

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/ParamMany.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("Freight");
        unit.setLabel("Freight Report");
        unit.setDescription("Freight Report with Saved Parameters");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        // Country
        InputControl countryIC = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        countryIC.setName("Country");
        countryIC.setLabel("Country");
        countryIC.setDescription("Country");
        countryIC.setMandatory(true);
        countryIC.setInputControlType(InputControl.TYPE_SINGLE_SELECT_QUERY);

        Query countryQ = (Query) getUnsecureRepositoryService().newResource(null, Query.class);
        countryQ.setName("CountryQuery");
        countryQ.setLabel("CountryQuery");
        countryQ.setLanguage("sql");
        countryQ.setDataSourceReference("/datasources/JServerJNDIDS");
        countryQ.setSql("select distinct SHIPCOUNTRY from ORDERS");
        countryIC.setQuery(countryQ);

        countryIC.setQueryValueColumn("SHIPCOUNTRY");
        countryIC.addQueryVisibleColumn("SHIPCOUNTRY");

        unit.addInputControl(countryIC);

        // Request Date
        InputControl requestDateIC = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        requestDateIC.setName("RequestDate");
        requestDateIC.setLabel("RequestDate");
        requestDateIC.setDescription("RequestDate");
        requestDateIC.setMandatory(false);
        requestDateIC.setInputControlType(InputControl.TYPE_SINGLE_VALUE);

        DataType dateDT = (DataType) getUnsecureRepositoryService().newResource(null, DataType.class);
        setCommon(dateDT, "Date");
        dateDT.setDataTypeType(DataType.TYPE_DATE);
        requestDateIC.setDataType(dateDT);

        unit.addInputControl(requestDateIC);

        // Order Id
        InputControl orderIdIC = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        orderIdIC.setName("OrderId");
        orderIdIC.setLabel("OrderId");
        orderIdIC.setDescription("OrderId");
        orderIdIC.setMandatory(false);
        orderIdIC.setInputControlType(InputControl.TYPE_SINGLE_VALUE);

        DataType numberDT = (DataType) getUnsecureRepositoryService().newResource(null, DataType.class);
        setCommon(numberDT, "Number");
        numberDT.setDataTypeType(DataType.TYPE_NUMBER);
        orderIdIC.setDataType(numberDT);

        unit.addInputControl(orderIdIC);
        unit.setAlwaysPromptControls(false);

        getUnsecureRepositoryService().saveResource(null, unit);

    }

    private void createAllCharts(Folder folder) {
        // create the StandardChartsReport
        m_logger.info("createAllCharts() => creating /reports/samples/StandardChartsReport");

        FileResource reportRes;
        ReportUnit unit;
        InputStream jrxml;
        FileResource resBdl;
        FileResource resBdl_ro;

        reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("StandardChartsReport");
        reportRes.setLabel("Standard Charts Report");
        reportRes.setDescription("Standard Charts Report");
        reportRes.setParentFolder(folder);

        jrxml = getClass().getResourceAsStream("/reports/jasper/StandardChartsReport.jrxml");
        reportRes.readData(jrxml);

        unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("StandardChartsReport");
        unit.setLabel("Standard Charts Report");
        unit.setDescription("Standard Charts Report");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        resBdl = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        resBdl.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl.readData(getClass().getResourceAsStream("/resource_bundles/standardCharts.properties"));
        setCommon(resBdl, "standardCharts.properties");
        unit.addResource(resBdl);

        resBdl_ro = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        resBdl_ro.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl_ro.readData(getClass().getResourceAsStream("/resource_bundles/standardCharts_ro.properties"));
        setCommon(resBdl_ro, "standardCharts_ro.properties");
        unit.addResource(resBdl_ro);

        getUnsecureRepositoryService().saveResource(null, unit);

        // create the StandardChartsAegeanReport
        m_logger.info("createAllCharts() => creating /reports/samples/StandardChartsAegeanReport");

        reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("StandardChartsAegeanReport");
        reportRes.setLabel("Standard Charts Aegean Report");
        reportRes.setDescription("Standard Charts Aegean Report");
        reportRes.setParentFolder(folder);

        jrxml = getClass().getResourceAsStream("/reports/jasper/StandardChartsAegeanReport.jrxml");
        reportRes.readData(jrxml);

        unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("StandardChartsAegeanReport");
        unit.setLabel("Standard Charts Aegean Report");
        unit.setDescription("Standard Charts Aegean Report");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        resBdl = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        resBdl.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl.readData(getClass().getResourceAsStream("/resource_bundles/standardCharts.properties"));
        setCommon(resBdl, "standardCharts.properties");
        unit.addResource(resBdl);

        resBdl_ro = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        resBdl_ro.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl_ro.readData(getClass().getResourceAsStream("/resource_bundles/standardCharts_ro.properties"));
        setCommon(resBdl_ro, "standardCharts_ro.properties");
        unit.addResource(resBdl_ro);

        getUnsecureRepositoryService().saveResource(null, unit);

        // create the StandardChartsEyeCandyReport
        m_logger.info("createAllCharts() => creating /reports/samples/StandardChartsEyeCandyReport");

        reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("StandardChartsEyeCandyReport");
        reportRes.setLabel("Standard Charts Eye Candy Report");
        reportRes.setDescription("Standard Charts Eye Candy Report");
        reportRes.setParentFolder(folder);

        jrxml = getClass().getResourceAsStream("/reports/jasper/StandardChartsEyeCandyReport.jrxml");
        reportRes.readData(jrxml);

        unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("StandardChartsEyeCandyReport");
        unit.setLabel("Standard Charts Eye Candy Report");
        unit.setDescription("Standard Charts Eye Candy Report");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        resBdl = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        resBdl.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl.readData(getClass().getResourceAsStream("/resource_bundles/standardCharts.properties"));
        setCommon(resBdl, "standardCharts.properties");
        unit.addResource(resBdl);

        resBdl_ro = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        resBdl_ro.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl_ro.readData(getClass().getResourceAsStream("/resource_bundles/standardCharts_ro.properties"));
        setCommon(resBdl_ro, "standardCharts_ro.properties");
        unit.addResource(resBdl_ro);

        getUnsecureRepositoryService().saveResource(null, unit);
    } 


    private void createAllCascading(Folder folder) {
        // create the Cascading_multi_select_report
        m_logger.info("createAllCascading() => creating /reports/samples/Cascading_multi_select_report");

        FileResource reportRes;
        InputStream jrxml;
        ReportUnit unit;
        InputControl countryIC;
        Query countryQ;
        InputControl stateIC;
        Query stateQ;
        InputControl nameIC;
        Query nameQ;
        InputControl rolesIC;
        Query rolesQ;
        FileResource img;

        reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("Cascading_multi_select_report");
        reportRes.setLabel("Cascading multi select example report");
        reportRes.setDescription("Shows cascading input controls. Multi-select and single select queries");
        reportRes.setParentFolder(folder);

        jrxml = getClass().getResourceAsStream("/reports/jasper/CascadingMultiSelectReport.jrxml");
        reportRes.readData(jrxml);

        unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("Cascading_multi_select_report");
        unit.setLabel("Cascading multi select example report");
        unit.setDescription("Example report with Cascading multi select input controls");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/JServerJNDIDS");
        unit.setMainReport(reportRes);

        img = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        img.setFileType(FileResource.TYPE_IMAGE);
        img.readData(getClass().getResourceAsStream("/images/RightArrow.png"));
        setCommon(img, "RightArrow.png");
        unit.addResource(img);

        img = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        img.setFileType(FileResource.TYPE_IMAGE);
        img.readData(getClass().getResourceAsStream("/images/js_samples.png"));
        setCommon(img, "js_samples.png");
        unit.addResource(img);

        unit.setControlsLayout(ReportUnit.LAYOUT_IN_PAGE);  // set input control layout as in page

        // Country IC
        countryIC = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        countryIC.setName("Country_multi_select");
        countryIC.setLabel("Country multi select");
        countryIC.setDescription("Country multi select");
        countryIC.setMandatory(true);
        countryIC.setInputControlType(InputControl.TYPE_MULTI_SELECT_QUERY);

        countryQ = (Query) getUnsecureRepositoryService().newResource(null, Query.class);
        countryQ.setName("country_query");
        countryQ.setLabel("Country Query");
        countryQ.setLanguage("sql");
        countryQ.setDataSourceReference("/datasources/JServerJNDIDS");
        countryQ.setSql("select distinct billing_address_country from accounts order by billing_address_country");
        countryIC.setQuery(countryQ);

        countryIC.setQueryValueColumn("billing_address_country");
        countryIC.addQueryVisibleColumn("billing_address_country");

        unit.addInputControl(countryIC);

        // State IC
        stateIC = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        stateIC.setName("Cascading_state_multi_select");
        stateIC.setLabel("Cascading state multi select control");
        stateIC.setDescription("Cascading state multi select control");
        stateIC.setMandatory(true);
        stateIC.setInputControlType(InputControl.TYPE_MULTI_SELECT_QUERY);

        stateQ = (Query) getUnsecureRepositoryService().newResource(null, Query.class);
        stateQ.setName("Cascading_state_query");
        stateQ.setLabel("Cascading state query");
        stateQ.setLanguage("sql");
        stateQ.setDataSourceReference("/datasources/JServerJNDIDS");
        stateQ.setSql("select distinct billing_address_state, billing_address_country from accounts where $X{IN, billing_address_country, Co untry_multi_select} order by billing_address_country, billing_address_state");
        stateIC.setQuery(stateQ);

        stateIC.setQueryValueColumn("billing_address_state");
        stateIC.addQueryVisibleColumn("billing_address_country");
        stateIC.addQueryVisibleColumn("billing_address_state");

        unit.addInputControl(stateIC);

        // Name IC
        nameIC = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        nameIC.setName("Cascading_name_single_select");
        nameIC.setLabel("Cascading name single select");
        nameIC.setDescription("Cascading name single select");
        nameIC.setMandatory(true);
        nameIC.setInputControlType(InputControl.TYPE_SINGLE_SELECT_QUERY);

        nameQ = (Query) getUnsecureRepositoryService().newResource(null, Query.class);
        nameQ.setName("country_state_to_name");
        nameQ.setLabel("Country State to Name");
        nameQ.setLanguage("sql");
        nameQ.setDataSourceReference("/datasources/JServerJNDIDS");
        nameQ.setSql("select name from accounts where $X{IN, billing_address_country, Country_multi_select} and $X{IN, billing_address_state , Cascading_state_multi_select} order by name");
        nameIC.setQuery(nameQ);

        nameIC.setQueryValueColumn("name");
        nameIC.addQueryVisibleColumn("name");

        unit.addInputControl(nameIC);
        unit.setAlwaysPromptControls(false);

        getUnsecureRepositoryService().saveResource(null, unit);

        // create the PermissionsOfUsersWithARoleSharedByLoggedIn
        m_logger.info("createAllCascading() => creating /reports/samples/PermissionsOfUsersWithARoleSharedByLoggedIn");

        reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        reportRes.setName("PermissionsOfUsersWithARoleSharedByLoggedIn");
        reportRes.setLabel("Permissions Of Users with a role shared by LoggedIn");
        reportRes.setDescription("Input control controlled by user profile");
        reportRes.setParentFolder(folder);

        String dbName = getJdbcProps().getProperty("test.databaseFlavor");
        if (dbName != null && (dbName.startsWith("postgre"))) {                      // using || for string concatenation
            jrxml = getClass().getResourceAsStream("/reports/jasper/PermissionsOfUsersWithARoleSharedByLoggedIn_postgres.jrxml");
        } else if (dbName != null && dbName.startsWith("db2")) {                     // using || for string concatenation and 'cast(n ull as varchar(1))' for null column
            jrxml = getClass().getResourceAsStream("/reports/jasper/PermissionsOfUsersWithARoleSharedByLoggedIn_db2.jrxml");
        } else if (dbName != null && dbName.startsWith("sqlserver")) {               // using + for string concatenation
            jrxml = getClass().getResourceAsStream("/reports/jasper/PermissionsOfUsersWithARoleSharedByLoggedIn_mssql.jrxml");
        } else {                                                                     // using 'concat' function for string concatenation
            jrxml = getClass().getResourceAsStream("/reports/jasper/PermissionsOfUsersWithARoleSharedByLoggedIn.jrxml");
        }
        reportRes.readData(jrxml);

        unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("PermissionsOfUsersWithARoleSharedByLoggedIn");
        unit.setLabel("Permissions Of Users with a role shared by LoggedIn");
        unit.setDescription("Input control controlled by user profile");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/datasources/repositoryDS");
        unit.setMainReport(reportRes);

        // RolesOfLoggedInUser IC
        rolesIC = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        rolesIC.setName("RolesOfLoggedInUser");
        rolesIC.setLabel("Roles of LoggedIn User");
        rolesIC.setDescription("Roles of LoggedIn User");
        rolesIC.setMandatory(true);
        rolesIC.setInputControlType(InputControl.TYPE_MULTI_SELECT_QUERY);

        rolesQ = (Query) getUnsecureRepositoryService().newResource(null, Query.class);
        rolesQ.setName("RolesOfUserQuery");
        rolesQ.setLabel("RolesOfUserQuery");
        rolesQ.setLanguage("sql");
        rolesQ.setDataSourceReference("/datasources/repositoryDS");
        rolesQ.setSql("select rolename from JIRole where $X{IN, rolename, LoggedInUserRoles}");
        rolesIC.setQuery(rolesQ);

        rolesIC.setQueryValueColumn("rolename");
        rolesIC.addQueryVisibleColumn("rolename");

        unit.addInputControl(rolesIC);
        unit.setAlwaysPromptControls(true);

        getUnsecureRepositoryService().saveResource(null, unit);
    }


    private void createDepartmentReport(Folder folder) {
        m_logger.info("createDepartmentReport() => creating /reports/samples/Department");

        FileResource reportRes = (FileResource) getUnsecureRepositoryService().newResource(null, FileResource.class);
        reportRes.setFileType(FileResource.TYPE_JRXML);
        setCommon(reportRes, "DepartmentJRXML");

        InputStream jrxml = getClass().getResourceAsStream("/reports/jasper/Department.jrxml");
        reportRes.readData(jrxml);

        ReportUnit unit = (ReportUnit) getUnsecureRepositoryService().newResource(null, ReportUnit.class);
        unit.setName("Department");
        unit.setLabel("Department");
        unit.setDescription("A report that displays multi-lingual input controls");
        unit.setParentFolder(folder);

        unit.setDataSourceReference("/analysis/datasources/FoodmartDataSourceJNDI");
        unit.setMainReport(reportRes);

        // Gender.
        InputControl listInputControl = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        setCommon(listInputControl, "Gender");
        listInputControl.setInputControlType(InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES);
        listInputControl.setMandatory(true);
        listInputControl.setReadOnly(false);
        listInputControl.setVisible(true);
        listInputControl.setLabel("$R{Gender}");
        listInputControl.setName("gender");

        ListOfValues values = (ListOfValues) getUnsecureRepositoryService().newResource(null, ListOfValues.class);
        values.setName("Gender_list_of_values");
        values.setLabel("Gender list of values label");
        values.setDescription("Gender list of values description");
        ListOfValuesItem item = new ListOfValuesItemImpl();
        item.setLabel("$R{Male}");
        item.setValue("M");
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Female}");
        item.setValue("F");
        values.addValue(item);
        listInputControl.setListOfValues(values);

        DataType dataType = new DataTypeImpl();
        dataType.setName("gender_type");
        dataType.setLabel("gender_type");
        dataType.setDataTypeType(DataType.TYPE_TEXT);
        listInputControl.setDataType(dataType);

        unit.addInputControl(listInputControl);

        // Marital status.
        listInputControl = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        setCommon(listInputControl, "Marital Status");
        listInputControl.setInputControlType(InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX);
        listInputControl.setMandatory(true);
        listInputControl.setReadOnly(false);
        listInputControl.setVisible(true);
        listInputControl.setLabel("$R{MaritalStatus}");
        listInputControl.setName("maritalStatus");

        values = (ListOfValues) getUnsecureRepositoryService().newResource(null, ListOfValues.class);
        values.setName("Marital_status_list_of_values");
        values.setLabel("Marital status list of values");
        values.setDescription("Marital status list of values description");
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Married}");
        item.setValue("M");
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Single}");
        item.setValue("S");
        values.addValue(item);
        listInputControl.setListOfValues(values);

        dataType = new DataTypeImpl();
        dataType.setName("maritalStatus_type");
        dataType.setLabel("maritalStatus_type");
        dataType.setDataTypeType(DataType.TYPE_TEXT);
        listInputControl.setDataType(dataType);

        unit.addInputControl(listInputControl);

        // Department.
        listInputControl = (InputControl) getUnsecureRepositoryService().newResource(null, InputControl.class);
        setCommon(listInputControl, "Department");
        listInputControl.setInputControlType(InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES);
        listInputControl.setMandatory(true);
        listInputControl.setReadOnly(false);
        listInputControl.setVisible(true);
        listInputControl.setLabel("$R{Department}");
        listInputControl.setName("department");

        values = (ListOfValues) getUnsecureRepositoryService().newResource(null, ListOfValues.class);
        values.setName("Department_list_of_values");
        values.setLabel("Department list of values label");
        values.setDescription("Department list of values description");
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep1}");
        item.setValue(1);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep2}");
        item.setValue(2);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep3}");
        item.setValue(3);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep4}");
        item.setValue(4);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep5}");
        item.setValue(5);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep11}");
        item.setValue(11);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep14}");
        item.setValue(14);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep15}");
        item.setValue(15);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep16}");
        item.setValue(16);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep17}");
        item.setValue(17);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep18}");
        item.setValue(18);
        values.addValue(item);
        item = new ListOfValuesItemImpl();
        item.setLabel("$R{Dep19}");
        item.setValue(19);
        values.addValue(item);
        listInputControl.setListOfValues(values);

        dataType = new DataTypeImpl();
        dataType.setName("department_type");
        dataType.setLabel("department_type");
        dataType.setDataTypeType(DataType.TYPE_NUMBER);
        listInputControl.setDataType(dataType);

        unit.addInputControl(listInputControl);

        FileResource resBdl = (FileResource) getRepositoryService().newResource(null, FileResource.class);
        resBdl.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl.readData(getClass().getResourceAsStream("/resource_bundles/department.properties"));
        setCommon(resBdl, "department.properties");
        unit.addResource(resBdl);

        resBdl = (FileResource) getRepositoryService().newResource(null, FileResource.class);
        resBdl.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl.readData(getClass().getResourceAsStream("/resource_bundles/department_fr.properties"));
        setCommon(resBdl, "department_fr.properties");
        unit.addResource(resBdl);

        resBdl = (FileResource) getRepositoryService().newResource(null, FileResource.class);
        resBdl.setFileType(FileResource.TYPE_RESOURCE_BUNDLE);
        resBdl.readData(getClass().getResourceAsStream("/resource_bundles/department_es.properties"));
        setCommon(resBdl, "department_es.properties");
        unit.addResource(resBdl);

        getUnsecureRepositoryService().saveResource(null, unit);
    }

    private void createDateDatatype() {
        Folder folder = new FolderImpl();
        folder.setName("datatypes");
        folder.setLabel("Input data Types");
        getUnsecureRepositoryService().saveFolder(null, folder);

        DataType dateDataType = new DataTypeImpl();
        setCommon(dateDataType, "date");
        dateDataType.setDataTypeType(DataType.TYPE_DATE);
        dateDataType.setParentFolder(folder);
        getUnsecureRepositoryService().saveResource(null, dateDataType);
    }

    private void setCommon(Resource res, String id) {
        res.setName(id);
        res.setLabel(id + " Label");
        res.setDescription(id + " description");
    }

}
