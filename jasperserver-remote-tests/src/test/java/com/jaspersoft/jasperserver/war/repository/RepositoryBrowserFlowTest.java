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
package com.jaspersoft.jasperserver.war.repository;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.jaspersoft.jasperserver.war.JasperServerConstants;
import com.jaspersoft.jasperserver.war.wizard.TestAttribute;
import com.jaspersoft.jasperserver.war.wizard.TestAttribute.DataSourceAttrb;


/**
 * The test cases are for: -
 * testing the Repository Browser flow for the different files.
 * 
 **/
public class RepositoryBrowserFlowTest 
	extends AbstractHttpRepositeryBaseFlow {

	/**
	 * Constructor
	 * 
	 * @param s
	 */
	public RepositoryBrowserFlowTest(String s) {
		super(s);
	}
	
	
	
	//****--------------------------------------------------------------------------*****/
	//*						HttpUnit test cases		                                    */
	//****--------------------------------------------------------------------------*****/	

	
	/**
	 * This test case method is for testing Repository Browser flow with respect to DataSource,
	 *  DataType & JRXML files 
	 * 
	 * @throws Exception if fails.
	 */
	public void testRepositoryBrowser() throws Exception {
		
		TestAttribute attrib = new TestAttribute();
		attrib.setDataSourceReportName(String.valueOf("TestJNDI"+String.valueOf((int)(Math.random()*1000))));
		attrib.setDataTypeReportName("DataType"+String.valueOf((int)(Math.random()*1000)));
		attrib.setInputControlReportName("InputControl"+String.valueOf((int)(Math.random()*1000)));
		attrib.setJRXMLReportName("JRXMLFile"+String.valueOf((int)(Math.random()*1000)));
		attrib.setLabel("Test report by http unit test");
		attrib.setServiceName("jndi/jserver");
		attrib.setJrxml("AllAccounts.jrxml");
		attrib.setJrmlResources(resourceMap);
		DataSourceAttrb dsattrb = attrib.new DataSourceAttrb();
		dsattrb.setDataSourceType(TestAttribute.DSTYPE_JNDI);
		dsattrb.setName("jndi");
		dsattrb.setLabel("Using JNDI DS");
		dsattrb.setDriver("com.mysql.jdbc.Driver");
		dsattrb.setUrl("jndi:mysql://localhost:3306/jasperserver");
		dsattrb.setUsername("root");
		dsattrb.setPassword("root");
		attrib.setDataSourceAttrb(dsattrb);
		this.repositoryFlow(attrib);
	}
	
	/**
	 * This test case method is for testing Repository Browser flow with respect to Image File
	 * 
	 * @throws Exception if fails.
	 */
	public void testImageFileAddition() throws Exception {		
		
		TestAttribute attrib = new TestAttribute();
		attrib.setDataSourceReportName("TestJNDI"+String.valueOf((int)(Math.random()*1000)));
		attrib.setDataTypeReportName("DataType"+String.valueOf((int)(Math.random()*1000)));
		attrib.setInputControlReportName("InputControl"+String.valueOf((int)(Math.random()*1000)));
		attrib.setJRXMLReportName("JRXMLFile"+String.valueOf((int)(Math.random()*1000)));
		attrib.setLabel("Test report by http unit test for Image File");
		attrib.setServiceName("jndi/jserver");
		attrib.setJrxml("logo.jpg");
		attrib.setJrmlResources(resourceMap);
		DataSourceAttrb dsattrb = attrib.new DataSourceAttrb();
		dsattrb.setDataSourceType(TestAttribute.DSTYPE_JNDI);
		dsattrb.setName("jndi");
		dsattrb.setLabel("Using JNDI DS");
		dsattrb.setDriver("com.mysql.jdbc.Driver");
		dsattrb.setUrl("jndi:mysql://localhost:3306/jasperserver");
		dsattrb.setUsername("root");
		dsattrb.setPassword("root");
		attrib.setDataSourceAttrb(dsattrb);
		this.repositoryFlow(attrib);		
	}
	
	/**
	 * This test case method is for testing Repository Browser flow with respect to Jar File
	 * 
	 * @throws Exception if fails.
	 */
	public void testJarFileAddition() throws Exception {		
		
		TestAttribute attrib = new TestAttribute();
		attrib.setDataSourceReportName("TestJNDI"+String.valueOf((int)(Math.random()*1000)));
		attrib.setDataTypeReportName("DataType"+String.valueOf((int)(Math.random()*1000)));
		attrib.setInputControlReportName("InputControl"+String.valueOf((int)(Math.random()*1000)));
		attrib.setJRXMLReportName("JRXMLFile"+String.valueOf((int)(Math.random()*1000)));
		attrib.setLabel("Test report by http unit test for Jar file");
		attrib.setServiceName("jndi/jserver");
		attrib.setJrxml("httpunit.jar");
		attrib.setJrmlResources(resourceMap);
		DataSourceAttrb dsattrb = attrib.new DataSourceAttrb();
		dsattrb.setDataSourceType(TestAttribute.DSTYPE_JNDI);
		dsattrb.setName("jndi");
		dsattrb.setLabel("Using JNDI DS");
		dsattrb.setDriver("com.mysql.jdbc.Driver");
		dsattrb.setUrl("jndi:mysql://localhost:3306/jasperserver");
		dsattrb.setUsername("root");
		dsattrb.setPassword("root");
		attrib.setDataSourceAttrb(dsattrb);
		this.repositoryFlow(attrib);		
	}
	
	
	/**
	 * This test case method is for testing Repository Browser flow with respect to Resource Bundle
	 *  
	 * @throws Exception if fails.
	 */
	public void testResourceBundleAddition() throws Exception {		
		
		TestAttribute attrib = new TestAttribute();
		attrib.setDataSourceReportName("TestJNDI"+String.valueOf((int)(Math.random()*1000)));
		attrib.setDataTypeReportName("DataType"+String.valueOf((int)(Math.random()*1000)));
		attrib.setInputControlReportName("InputControl"+String.valueOf((int)(Math.random()*1000)));
		attrib.setJRXMLReportName("JRXMLFile"+String.valueOf((int)(Math.random()*1000)));
		attrib.setLabel("Test report by http unit test for Resource Bundle");
		attrib.setServiceName("jndi/jserver");
		attrib.setJrxml("jndi.properties");
		attrib.setJrmlResources(resourceMap);
		DataSourceAttrb dsattrb = attrib.new DataSourceAttrb();
		dsattrb.setDataSourceType(TestAttribute.DSTYPE_JNDI);
		dsattrb.setName("jndi");
		dsattrb.setLabel("Using JNDI DS");
		dsattrb.setDriver("com.mysql.jdbc.Driver");
		dsattrb.setUrl("jndi:mysql://localhost:3306/jasperserver");
		dsattrb.setUsername("root");
		dsattrb.setPassword("root");
		attrib.setDataSourceAttrb(dsattrb);
		this.repositoryFlow(attrib);		
	}
	
	
	
	/**
	 * This test case method is for testing Repository Browser flow with respect to Font File
	 * 
	 * @throws Exception if fails.
	 */
	public void testFontFileAddition() throws Exception {		
		
		TestAttribute attrib = new TestAttribute();
		attrib.setDataSourceReportName("TestJNDI"+String.valueOf((int)(Math.random()*1000)));
		attrib.setDataTypeReportName("DataType"+String.valueOf((int)(Math.random()*1000)));
		attrib.setInputControlReportName("InputControl"+String.valueOf((int)(Math.random()*1000)));
		attrib.setJRXMLReportName("JRXMLFile"+String.valueOf((int)(Math.random()*1000)));
		attrib.setLabel("Test report by http unit test for Font file");
		attrib.setServiceName("jndi/jserver");
		attrib.setJrxml("arial.ttf");
		attrib.setJrmlResources(resourceMap);
		DataSourceAttrb dsattrb = attrib.new DataSourceAttrb();
		dsattrb.setDataSourceType(TestAttribute.DSTYPE_JNDI);
		dsattrb.setName("jndi");
		dsattrb.setLabel("Using JNDI DS");
		dsattrb.setDriver("com.mysql.jdbc.Driver");
		dsattrb.setUrl("jndi:mysql://localhost:3306/jasperserver");
		dsattrb.setUsername("root");
		dsattrb.setPassword("root");
		attrib.setDataSourceAttrb(dsattrb);
		this.repositoryFlow(attrib);		
	}
	
	
	/**
	 * This test case method is for testing Repository Browser flow with respect to Text File
	 * 
	 * @throws Exception if fails.
	 */
	public void testTextFileAddition() throws Exception {		
		
		TestAttribute attrib = new TestAttribute();
		attrib.setDataSourceReportName("TestJNDI"+String.valueOf((int)(Math.random()*1000)));
		attrib.setDataTypeReportName("DataType"+String.valueOf((int)(Math.random()*1000)));
		attrib.setInputControlReportName("InputControl"+String.valueOf((int)(Math.random()*1000)));
		attrib.setJRXMLReportName("JRXMLFile"+String.valueOf((int)(Math.random()*1000)));
		attrib.setLabel("Test report by http unit test for Text file");
		attrib.setServiceName("jndi/jserver");
		attrib.setJrxml("test.txt");
		attrib.setJrmlResources(resourceMap);
		DataSourceAttrb dsattrb = attrib.new DataSourceAttrb();
		dsattrb.setDataSourceType(TestAttribute.DSTYPE_JNDI);
		dsattrb.setName("jndi");
		dsattrb.setLabel("Using JNDI DS");
		dsattrb.setDriver("com.mysql.jdbc.Driver");
		dsattrb.setUrl("jndi:mysql://localhost:3306/jasperserver");
		dsattrb.setUsername("root");
		dsattrb.setPassword("root");
		attrib.setDataSourceAttrb(dsattrb);
		this.repositoryFlow(attrib);		
	}

	
	//****--------------------------------------------------------------------------*****/
	//*				Base class method implementaion                                     */
	//****--------------------------------------------------------------------------*****/	

	
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.war.HttpUnitBaseTestCase#getloginCredentials()
	 */
	protected String[] getloginCredentials() {
		return new String[] { JasperServerConstants.instance().USERNAME, 
				      JasperServerConstants.instance().PASSWORD };
	}
	
	//****--------------------------------------------------------------------------*****/
	//*				HttpUnit framework methods                                          */
	//****--------------------------------------------------------------------------*****/		
		
	
	/**
	 * the main method for calling all the test cases whichever is being added into the suite.
	 *
	 * @param args
	 **/
	public static void main(String[] args) {
		try {
			junit.textui.TestRunner.run(suite());
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}

	/**
	 * this method is for adding which all test case/s method/s need to be
	 *
	 * @return Test
	 *
	 * @throws Exception if fails
	 **/
	public static Test suite()
	  throws Exception {
		TestSuite suite = new TestSuite();

		TestCase  test1 = new RepositoryBrowserFlowTest("testRepositoryBrowser");
		TestCase  test2 = new RepositoryBrowserFlowTest("testImageFileAddition");
		TestCase  test3 = new RepositoryBrowserFlowTest("testJarFileAddition");
		TestCase  test4 = new RepositoryBrowserFlowTest("testResourceBundleAddition");
		TestCase  test5 = new RepositoryBrowserFlowTest("testFontFileAddition");
		TestCase  test6 = new RepositoryBrowserFlowTest("testTextFileAddition");
		
		suite.addTest(test1);
		suite.addTest(test2);
		suite.addTest(test3);
		suite.addTest(test4);
		suite.addTest(test5);
		suite.addTest(test6);
		
		return suite;
	}
} 