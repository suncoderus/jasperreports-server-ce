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

/**
 * @author tkavanagh
 * @version $id $
 */

import java.io.InputStream;
import java.util.Properties;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.crypto.EncryptionEngine;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.export.Parameters;

import com.jaspersoft.jasperserver.test.BaseExportTestCaseTestNG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public class ExportOlapTestTestNG extends BaseExportTestCaseTestNG {

    private static final Log m_logger = LogFactory.getLog(ExportOlapTestTestNG.class);

	static final String PATH_SEP = "/";
	static final String LABEL = "_label";
	static final String DESC = "_description";
	static final String DESC_TEXT = " for export-import testing";

	protected final String foodMartSchemaURI = "/queries/Foodmart.xml";
	protected final String sugarCRMSchemaURI = "/reports/jasper/SugarCRMOpps.xml";

	final String OLAP_FOLDER = "test_olap_05";
	final String OLAP_REPORT_NAME = "OlapSugarMondrianView";

    private OlapConnectionService olapConnectionService;

    private String SAMPLE_SUGAR_CRM_MDX_QUERY =
    	"select {[Measures].[Total Sale Amount], [Measures].[Number of Sales], [Measures].[Avg Sale Amount], [Measures].[Avg Time To Close (Days)], [Measures].[Avg Close Probablility]} ON COLUMNS, " +
    	" NON EMPTY {([Account Categorization].[All Accounts], [Close Period].[All Periods])} ON ROWS " +
    	" from [SalesAnalysis] " +
    	" where [Sale State].[All Types].[Closed Won]";

    public ExportOlapTestTestNG(){
        m_logger.info("ExportOlapTestTestNG => constructor() called");
    }

    @BeforeClass()
    public void onSetUp() throws Exception {
        m_logger.info("ExportOlapTestTestNG => onSetUp() called");
		super.onSetUp();
	}

    @AfterClass()
    public void onTearDown() throws Exception {
        m_logger.info("ExportOlapTestTestNG => onTearDown() called");
		super.onTearDown();
	}

	/*
	 * This test will create a few olap resources and then
	 * create an olap unit that uses a MondrianConnection
	 * against the SugarCRM database.
	 */
    @Test()
	public void doOlapViewTest() throws Exception {
        m_logger.info("ExportOlapTestTestNG => doOlapViewTest() called");

		createOlapConnectionSimpleResources();

		String exportDir = createExportDir();
		Parameters exportParams = createParameters()
			.addParameterValue(PARAM_EXPORT_DIR, exportDir)
			.addParameterValue(PARAM_EXPORT_URIS, PATH_SEP + OLAP_FOLDER + PATH_SEP + OLAP_REPORT_NAME);
		performExport(exportParams);


		deleteOlapConnectionSimpleResources();

		// asserts
		OlapUnit ou_null = (OlapUnit) getRepositoryService().getResource(null,
				PATH_SEP + OLAP_FOLDER + PATH_SEP + OLAP_REPORT_NAME);
		assertNull(ou_null);

		Parameters importParams = createParameters()
				.addParameterValue(PARAM_IMPORT_DIR, exportDir);
		performImport(importParams);

		// junit asserts
		OlapUnit unit = (OlapUnit) getRepositoryService().getResource(null,
				PATH_SEP + OLAP_FOLDER + PATH_SEP + OLAP_REPORT_NAME);
		assertNotNull(unit);

		deleteOlapConnectionSimpleResources();
	}

	protected void createOlapConnectionSimpleResources() throws Exception {

        // clobber olap folder if it's there
		Folder folder = getRepositoryService().getFolder(null, "/" + OLAP_FOLDER);
        if (folder != null) {
            getRepositoryService().deleteFolder(null, "/" + OLAP_FOLDER);
        }
		folder = new FolderImpl();
		folder.setName(OLAP_FOLDER);
		folder.setLabel(OLAP_FOLDER + LABEL);
		folder.setDescription(OLAP_FOLDER + DESC);
		folder.setParentFolder("/");
		getRepositoryService().saveFolder(null, folder);

		FileResource schema = (FileResource) getRepositoryService().newResource( null, FileResource.class );
        schema.setName("SugarCRMSchema");
        schema.setLabel("SugarCRM Opportunities DataMart");
        schema.setDescription("SugarCRM Opportunities Data Mart Schema");
        schema.setFileType(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA);
        InputStream in = getClass().getResourceAsStream(sugarCRMSchemaURI);
        schema.readData(in);
        getOlapConnectionService().saveResource( null, PATH_SEP + OLAP_FOLDER + PATH_SEP, schema );

        JdbcReportDataSource ds =
            (JdbcReportDataSource) getRepositoryService().newResource( null, JdbcReportDataSource.class );
        ds.setName("SugarCRMDataSource");
        ds.setLabel("SugarCRM Data Source");
        ds.setDescription("SugarCRM Data Source");
        ds.setDriverClass(getJdbcProps().getProperty("test.jdbc.driverClassName"));
        ds.setConnectionUrl(getJdbcProps().getProperty("test.jdbc.url"));
        ds.setUsername(getJdbcProps().getProperty("test.jdbc.username"));

		String passwd = getJdbcProps().getProperty("test.jdbc.password");
		if (EncryptionEngine.isEncrypted(passwd)) {
			KeystoreManager ksManager = KeystoreManager.getInstance();
			passwd = EncryptionEngine.decrypt(ksManager.getBuildKey(), passwd);
		}
		ds.setPassword(passwd);

        getOlapConnectionService().saveResource( null, PATH_SEP + OLAP_FOLDER + PATH_SEP, ds );

        MondrianConnection mondConn =
        	(MondrianConnection) getRepositoryService().newResource( null, MondrianConnection.class );
        mondConn.setName("SugarCRM");
        mondConn.setLabel("SugarCRM OLAP Connection");
        mondConn.setDescription("SugarCRM OLAP Connection: only opportunities");
        mondConn.setSchemaReference(PATH_SEP + OLAP_FOLDER + PATH_SEP + "SugarCRMSchema");
        mondConn.setDataSourceReference(PATH_SEP + OLAP_FOLDER + PATH_SEP + "SugarCRMDataSource");
        getOlapConnectionService().saveResource(null, PATH_SEP + OLAP_FOLDER + PATH_SEP, mondConn );

        XMLAConnection xmlaConn = (XMLAConnection) getRepositoryService().newResource( null, XMLAConnection.class );
        xmlaConn.setName("SugarCRMXmlaConnection");
        xmlaConn.setLabel("SugarCRM XML/A Connection");
        xmlaConn.setDescription("SugarCRM XML/A Connection");
        xmlaConn.setCatalog("SugarCRM");
        xmlaConn.setDataSource("Provider=Mondrian;DataSource=SugarCRM;");
        xmlaConn.setURI("http://localhost:8080/jasperserver/xmla");
        xmlaConn.setUsername("jasperadmin");
        xmlaConn.setPassword("jasperadmin");
        getOlapConnectionService().saveResource(null,  PATH_SEP + OLAP_FOLDER + PATH_SEP, xmlaConn );

        MondrianXMLADefinition def =
        	(MondrianXMLADefinition) getRepositoryService().newResource( null, MondrianXMLADefinition.class );
        def.setName("SugarCRMXmlaDefinition");
        def.setLabel("SugarCRM XMLA Connection");
        def.setDescription("SugarCRM XMLA Connection");
        def.setCatalog("SugarCRM");
        def.setMondrianConnectionReference(PATH_SEP + OLAP_FOLDER + PATH_SEP + "SugarCRM");

        getOlapConnectionService().saveResource( null, PATH_SEP + OLAP_FOLDER + PATH_SEP, def );

        // OlapUnit
    	OlapUnit view = (OlapUnit) getRepositoryService().newResource( null, OlapUnit.class );
    	view.setName(OLAP_REPORT_NAME);
    	view.setLabel("SugarCRM sample unit 1");
    	view.setDescription("SugarCRM Sample Olap Unit 1: Sales Performance by Industry/Account");
    	view.setOlapClientConnectionReference(PATH_SEP + OLAP_FOLDER + PATH_SEP + "SugarCRM");
    	view.setMdxQuery(SAMPLE_SUGAR_CRM_MDX_QUERY);
    	getOlapConnectionService().saveResource(null, PATH_SEP + OLAP_FOLDER + PATH_SEP, view );
	}

	protected void deleteOlapConnectionSimpleResources() {

		getRepositoryService().deleteFolder(null, PATH_SEP + OLAP_FOLDER);
	}

	public OlapConnectionService getOlapConnectionService() {
		return olapConnectionService;
	}

    @javax.annotation.Resource(name = "olapConnectionService")
	public void setOlapConnectionService(OlapConnectionService olapConnectionService) {
        m_logger.info("ExportOlapTestTestNG => setOlapConnectionService() called");
		this.olapConnectionService = olapConnectionService;
	}

}
