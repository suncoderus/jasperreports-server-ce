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
package com.jaspersoft.jasperserver.war;

import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebResponse;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class HttpUnitCreateRoleTest	extends HttpUnitBaseTestCase
{
	private static String roleMgmtPageUrl = JasperServerConstants.instance().BASE_URL +
											"/jasperserver/flow.html";
	private static String roleMgmtPageString = "?_flowId=roleListFlow";
	private static String roleSearchFormText = "frm";
	private static String editRoleFormText  = "fmCreEdUsr";
	private static String newRoleButtonText  = "_eventId_add";
	private static String submitButtonText  = "_eventId_save";
	private static String cancelButtonText  = "_eventId_cancel";
	private static String roleNameText = "role.roleName";
	private static String rolesText = "Users in this role";
	private static String errMsgText = "Name cannot be empty";

	private static SubmitButton cancelButton;
	private static WebForm createRoleForm;
	private static SubmitButton submitButton;
	private static WebForm roleSearchForm;
	private static SubmitButton newRoleButton;

	/**
	 * Creates a new HttpUnitCreateUserTest object.
	 **/
	public HttpUnitCreateRoleTest() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param s
	 **/
	public HttpUnitCreateRoleTest(String s) {
		super(s);
	}

	/**
	 * Checks through commonLoginFunction for logging in in at first time
	 *
	 * @throws Exception if fails
	 **/
	public void setUp()
	  throws Exception {
		  commonLoginFunction(roleMgmtPageUrl, roleMgmtPageString);
	}


	//****--------------------------------------------------------------------------*****/
	//*						HttpUnit test cases		                                    */
	//****--------------------------------------------------------------------------*****/


	/**
	 * This test case method is for Create/Edit User page of JS application.
	 * This page take User information as input parameter and on Submit goes to User Search Page
	 * on proper response test success else fail
	 *
	 * @throws Exception if fails
	 **/
	public void testCreateRolePage()
	  throws Exception {

		this.createNewRole("Test_HttpUnit");
		WebResponse roleSearchPage = this.getWebConversation().getCurrentPage();

		String searchPage = roleSearchPage.getText();
		assertNotNull("Role search page is null", searchPage);

		if ((searchPage == null) || (searchPage.trim().length() == 0)) {
			fail("No text found on response");
		}

	}

	/**
	 * This test case method is for Create/Edit User page of JS application.
	 * This page take empty user info as parameter and on Submit remains on same Page with Error message
	 * On proper response test success else failure
	 *
	 * @throws Exception if fails
	 **/
	public void testCreateNoRolePage()
	  throws Exception {
		 this.createNewRole(null);

		WebResponse resultPage = this.getWebConversation().getCurrentPage();
		assertNotNull("Result page is null", resultPage);
		String createUserPage = resultPage.getText();

		if ((createUserPage == null) || (createUserPage.trim().length() == 0)) {
		fail("No text found on response");
		}

		assertTrue(createUserPage.indexOf(errMsgText) != -1);
	}

	/**
	 * This test case method is for Create/Edit User page of JS application.
	 * On click to Cancel Button it goes to User Search Page
	 * On proper response test success else failure
	 *
	 * @throws Exception if fails
	 **/
	public void testRoleCancelPage()
	  throws Exception {

		WebResponse roleSearchPage = this.getWebConversation().getCurrentPage();
		assertNotNull("User searsh page is null", roleSearchPage);

		roleSearchForm = roleSearchPage.getFormWithName(roleSearchFormText);
		assertNotNull("Form is null", roleSearchForm);

		newRoleButton = roleSearchForm.getSubmitButton(newRoleButtonText);
		assertNotNull("Button is null", newRoleButton);

		roleSearchForm.submit(newRoleButton);

		WebResponse createRolePage = this.getWebConversation().getCurrentPage();
		createRoleForm = createRolePage.getFormWithName(editRoleFormText);
		assertNotNull("Form is null", createRoleForm);

		cancelButton = createRoleForm.getSubmitButton(cancelButtonText);
		assertNotNull("Button is null", cancelButton);

		createRolePage = createRoleForm.submit(cancelButton);

		//check if in response it come back to the User Search page
		WebResponse bckUsrSearchPage = this.getWebConversation().getCurrentPage();

		String str = bckUsrSearchPage.getText();
		assertNotNull("Text is null", str);
		if(str == null || str.trim().length()== 0)
			fail("There was no text in response");

	}

	/**
	 * This method checks for the User Search Page and New User Button on the page
	 * also checks for the text field, text and link on the page.
	 *
	 * @param roleName name of role to be created
	 *
	 * @throws Exception if fails
	 **/
	protected WebResponse createNewRole(String roleName) throws Exception
	{
		WebResponse roleSearchPage = this.getWebConversation().getCurrentPage();
		roleSearchForm = roleSearchPage.getFormWithName(roleSearchFormText);
		newRoleButton = roleSearchForm.getSubmitButton(newRoleButtonText);
		roleSearchForm.submit(newRoleButton);

		WebResponse createSearchPage = this.getWebConversation().getCurrentPage();
		assertNotNull("Text Field Element on Create/Edit Page is not present",
					  createSearchPage.getElementsWithName(roleNameText));

		assertNotNull("Text Field Element on Create/Edit Page is not present",
					  createSearchPage.getElementsWithName(rolesText));

		WebResponse createRolePage = this.getWebConversation().getCurrentPage();

		WebResponse resultPage = this.saveRole(createRolePage, roleName);

		return resultPage;

	}

	/**
	 * Saves a User .
	 *
	 * @param webResponse  page reference
	 * @param roleName  to be added
	 *
	 * @return  Result response
	 *
	 * @throws Exception if fails
	 **/
	protected WebResponse saveRole(WebResponse webResponse, String roleName)
	  throws Exception {

		createRoleForm  = webResponse.getFormWithName(editRoleFormText);
		submitButton = createRoleForm.getSubmitButton(submitButtonText);
		createRoleForm.setParameter("role.roleName", roleName);

		WebResponse wbResponse = createRoleForm.submit(submitButton);
		assertNotNull("The response is Null", wbResponse);

		return wbResponse;
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
	 * this method is for adding which all test case/s method/s need to be tested
	 *
	 * @return Test
	 *
	 * @throws Exception if fails
	 **/
	public static Test suite()
	  throws Exception {
		TestSuite suite = new TestSuite();

		TestCase test1 = new HttpUnitCreateRoleTest("testCreateRolePage");
		TestCase test2 = new HttpUnitCreateRoleTest("testCreateNoRolePage");
		TestCase test3 = new HttpUnitCreateRoleTest("testRoleCancelPage");

		suite.addTest(test1);
		suite.addTest(test2);
		suite.addTest(test3);
		return suite;
	}

}
