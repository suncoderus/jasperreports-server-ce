/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.ws.xmla;

import java.net.URL;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.tonbeller.jpivot.core.Model;
import com.tonbeller.jpivot.core.ModelFactory;

import com.tonbeller.jpivot.xmla.XMLA_SOAP;
import com.tonbeller.jpivot.xmla.XMLA_Model;
import com.tonbeller.jpivot.xmla.XMLA_Result;
import com.tonbeller.jpivot.xmla.XMLA_OlapModelTag;

import com.jaspersoft.jasperserver.war.JasperServerConstants;


/**
 * @author sbirney
 */


public class XmlaTest extends TestCase {

    private static boolean ENABLED = true;

    private static XMLA_SOAP XMLA_CLIENT = null;

    /**
     * default constructor
     */
    public XmlaTest(String method) {
	super(method);
    }

    /*
     * setUp method
     */
    public void setUp() throws Exception {
	if (!ENABLED) return;
	XMLA_CLIENT = new XMLA_SOAP( //"http://localhost:8080/mondrian-embedded/xmla",
				     JasperServerConstants.instance().XMLA_URL,
				     JasperServerConstants.instance().USERNAME,
				     JasperServerConstants.instance().PASSWORD );
    }

    /*
     * tearDown method
     */
    public void tearDown() {
	//no tearDown
    }

    /**
     * main method defined here
     * @param args
     */
    public static void main(String[] args) {
	try {
	    junit.textui.TestRunner.run(suite());
	} catch (Exception _ex) {
	    _ex.printStackTrace();
	}
    }

    /**
     * this method is for adding which all test case/s method/s need to be
     * @return Test
     * @throws Exception if fails
     */
    public static Test suite() throws Exception {
	TestSuite suite = new TestSuite();

	TestCase test1 = new XmlaTest("testDiscoverCatalogs");
	TestCase test2 = new XmlaTest("testDiscoverDSProperties");
	TestCase test3 = new XmlaTest("testDiscoverSugarCRMCubes");
	// disabled - failing 07-19-06
	TestCase test4 = new XmlaTest("testXmlaQuery");
	//TestCase test5 = new XmlaTest("testInvalidCredentials");
	//TestCase test6 = new XmlaTest("testNoCredentials");

	suite.addTest(test1);
	suite.addTest(test2);
	suite.addTest(test3);
	suite.addTest(test4); // disabled 07-19-06
	//suite.addTest(test5);
	//suite.addTest(test6);

	return suite;
    }

    /*
     * test no credentials
     */
    public void testNoCredentials() throws Exception {
	if (!ENABLED) return;
	System.out.println("testNoCredentials");
	boolean success = true;
	try {
	    XMLA_SOAP noCredClient = new XMLA_SOAP( JasperServerConstants.instance().XMLA_URL,
						    null, null ); // null or "" is empty
	    success = false;
	} catch (Throwable t) {
	    // for some reason this seems to be throwing up some text/html about:
	    //org.acegisecurity.AuthenticationCredentialsNotFoundException: An Authentication object was not found in the SecurityContext
	    //org.acegisecurity.intercept.AbstractSecurityInterceptor.credentialsNotFound(AbstractSecurityInterceptor.java:414)
	    //org.acegisecurity.intercept.AbstractSecurityInterceptor.beforeInvocation(AbstractSecurityInterceptor.java:308)
	    System.out.println("we should have just seen an exception printed but not thrown");
	}
	if (!success) {
	    throw new SecurityException("should have thrown a Bad credentials exception");
	}
    }

    /*
     * test invalid credentials
     */
    public void testInvalidCredentials() throws Exception {
	if (!ENABLED) return;
	System.out.println("testInvalidCredentials");
	try {
	    XMLA_SOAP badCredClient = new XMLA_SOAP( JasperServerConstants.instance().XMLA_URL,
						     "wrong",
						     "bad" );
	    throw new SecurityException("should have thrown a Bad credentials exception");
	} catch (Throwable t) {
	    // message should look like this:
	    // javax.xml.soap.SOAPException: java.security.PrivilegedActionException:
	    // javax.xml.soap.SOAPException: Bad response: (401Bad credentials"
	    if (!t.getMessage().endsWith("Bad credentials")) {
		//something other than we expected went wrong
		throw new SecurityException("Unexpected error while testing credentials", t);
	    }
	    System.out.println("we should have just seen an exception printed but not thrown");
	}
    }

    private String SAMPLE_SUGAR_CRM_MDX_QUERY =
	"select {[Measures].[Total Sale Amount], [Measures].[Number of Sales], [Measures].[Avg Sale Amount], [Measures].[Avg Time To Close (Days)], [Measures].[Avg Close Probablility]} ON COLUMNS, " +
	" NON EMPTY {([Account Categorization].[All Accounts], [Close Period].[All Periods])} ON ROWS " +
	" from [SalesAnalysis] " +
	" where [Sale State].[All Types].[Closed Won]";

    /*
     * test xmla query
     */
    public void testXmlaQuery() throws Exception {
	if (!ENABLED) return;

	URL configUrl = XMLA_OlapModelTag.class.getResource("config.xml");

	// let Digester create a model from config input
	// the config input stream MUST refer to the XMLA_Model class
	// <model class="com.tonbeller.bii.xmla.XMLA_Model"> is required
	Model model;
	model = ModelFactory.instance(configUrl);

	XMLA_Model xmlaModel = (XMLA_Model) model;

	xmlaModel.setCatalog("SugarCRM");
	xmlaModel.setDataSource("Provider=Mondrian;DataSource=SugarCRM;");
	xmlaModel.setMdxQuery(SAMPLE_SUGAR_CRM_MDX_QUERY);
	xmlaModel.setID("SugarCRM-1");
	xmlaModel.setUri(JasperServerConstants.instance().XMLA_URL);
	xmlaModel.setUser(JasperServerConstants.instance().USERNAME);
	xmlaModel.setPassword(JasperServerConstants.instance().PASSWORD);

	/*
	XMLA_SOAP xmlaClient = new XMLA_SOAP( JasperServerConstants.instance().XMLA_URL,
					      JasperServerConstants.instance().USERNAME,
					      JasperServerConstants.instance().PASSWORD,
					      xmlaModel.getDataSource() );

	// this is how jpivot executes the remote query
	XMLA_Result result = new XMLA_Result(xmlaModel,
					     xmlaClient,
					     "SugarCRM",
					     SAMPLE_SUGAR_CRM_MDX_QUERY,
					     false);
	*/
	xmlaModel.initialize();
	xmlaModel.getResult();
    }

    /*
     * test discover method
     */
    public void testDiscoverCatalogs() throws Exception {
	if (!ENABLED) return;
	System.out.println("testDiscoverCatalogs");
	List cats = XMLA_CLIENT.discoverCat();
	if (cats == null) {
	    fail("no catalogs available");
	    return;
	}
	System.out.println("number of catalogs: " + cats.size());
    }

    /*
     * test discover method
     */
    public void testDiscoverSugarCRMCubes() throws Exception {
	System.out.println("testDiscoverSugarCRMCubes");
    }

    /*
     * test discover method
     */
    public void testDiscoverDSProperties() throws Exception {
	if (!ENABLED) return;
	System.out.println("testDiscoverDSProperties");
	List props = XMLA_CLIENT.discoverDSProps();
	if (props == null) {
	    fail("no DSProperties available");
	    return;
	}
	System.out.println("XmlaTest::discoverDSProps props length " + props.size());
    }

}
