
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
package com.jaspersoft.jasperserver.war.wizard;

import com.jaspersoft.jasperserver.war.wizard.TestAttribute.DataSourceAttrb;
import com.jaspersoft.jasperserver.war.JasperServerConstants;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * The test cases are for: -
 * testing the wizard flow for the AllAccounts Jrxml file
 * 
 **/
public class WizardFlowTest
	extends AbstractHttpWizardBaseFlow {
	
	/**
	 * Creates a new JDBCWizardFlowTest object.
	 *
	 * @param s 
	 **/
	public WizardFlowTest(String s) {
		super(s);
	}
	
	
	
	//****--------------------------------------------------------------------------*****/
	//*								HttpUnit test cases		                            */
	//****--------------------------------------------------------------------------*****/	

	

	/**
	 * This test case method is for testing Wizard flow with respect to JDBC, for JS application 
	 * This Page checks for the cancel and back buttons on different pages within the folw,
	 * also checks for the next button functionality with and without proer response.
	 * And finally publish the report on Report Browser page.
	 *
	 * @throws Exception if fails
	 **/
	public void testWizardFlowWithJDBC()
	  throws Exception {
		TestAttribute attrbs = new TestAttribute();
		attrbs.setReportName("TestJDBC"+String.valueOf((int)(Math.random()*1000)));
		attrbs.setLabel("Test report by http unit test for JDBC");
		attrbs.setJrxml("AllAccounts.jrxml");
		attrbs.setJrmlResources(resourceMap);
		DataSourceAttrb dsattrbs = attrbs.new DataSourceAttrb();
		dsattrbs.setDataSourceType(TestAttribute.DSTYPE_JDBC);
		dsattrbs.setName("jdbc");
		dsattrbs.setLabel("Using JDBC DS");
		dsattrbs.setDriver("com.mysql.jdbc.Driver");
		dsattrbs.setUrl("jdbc:mysql://localhost:3306/jasperserver");
		dsattrbs.setUsername("root");
		dsattrbs.setPassword("root");
		attrbs.setDataSourceAttrb(dsattrbs);
		this.runWizardFlow(attrbs);
	}
	
	
	/**
	 * This test case method is for testing Wizard flow with respect to JNDI, for JS application 
	 * This Page checks for the cancel and back buttons on different pages within the folw,
	 * also checks for the next button functionality with and without proer response.
	 * And finally publish the report on Report Browser page.
	 *
	 * @throws Exception if fails
	 **/
	public void testWizardFlowWithJNDI()
	  throws Exception {
		TestAttribute attrbs = new TestAttribute();
		attrbs.setReportName("TestJNDI"+String.valueOf((int)(Math.random()*1000)));
		attrbs.setLabel("Test report by http unit test for JNDI");
		attrbs.setJrxml("AllAccounts.jrxml");
		attrbs.setJrmlResources(resourceMap);
		DataSourceAttrb dsattrbs = attrbs.new DataSourceAttrb();
		dsattrbs.setDataSourceType(TestAttribute.DSTYPE_JNDI);
		dsattrbs.setName("jndi");
		dsattrbs.setLabel("Using jndi ds");
		dsattrbs.setServiceName("jdbc/jserver");
		attrbs.setDataSourceAttrb(dsattrbs);
		this.runWizardFlow(attrbs);
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
	//*				main method to run the test                                         */
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

		TestCase  test1 = new WizardFlowTest("testWizardFlowWithJDBC");
		TestCase  test2 = new WizardFlowTest("testWizardFlowWithJNDI");
		suite.addTest(test1);
		suite.addTest(test2);
		return suite;
	}
}
