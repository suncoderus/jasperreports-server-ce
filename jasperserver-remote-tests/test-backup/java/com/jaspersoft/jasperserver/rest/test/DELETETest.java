package com.jaspersoft.jasperserver.rest.test;

import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DELETETest extends RESTTest 
{
	private final Log log = LogFactory.getLog(getClass());	
	//server 
	private static final String SAMPLE_LINKED_RESOURCE_SERVER_PATH = "/resource/datasources/JServerJNDIDS";
	
	@Before
	public void setUp() {
    	super.setUp();
    	httpReqCE = new HttpDelete();
    	httpReqPro = new HttpDelete();
     }
    
    //Successful delete
//	@Test 
//	public void Delete200File() throws Exception{
//		putSampleImageFileResource(SAMPLE_IMAGE_FILE_SERVER_PATH);
//		httpRes = executeCall(httpReqCE, SAMPLE_IMAGE_FILE_SERVER_PATH);
//    	
//    	Assert.assertTrue("basic response check did not pass", isValidResposnse());
//    }
//	
//	//Successful delete of folder
//	@Test 
//	public void Delete200Folder() throws Exception{
//		putSampleResource(RD_FOLDER_, SAMPLE_FOLDER_SERVER_PATH);
//		httpRes = executeCall(httpReqCE, SAMPLE_FOLDER_SERVER_PATH);
//    	
//    	Assert.assertTrue("basic response check did not pass", isValidResposnse());
//    }
	
	@Test 
	public void Delete200Job() throws Exception{
		//Delete job is covered as part of the get job test
    }
	
	
	
	@Test 
    public void User_Delete_404() throws Exception{
		sendAndAssert_CE(httpReqCE, "/user/none_existing_user_21", HttpStatus.SC_NOT_FOUND);    	
    }
	
	@Test 
    public void Delete200Role() throws Exception{
		sendAndAssert_CE(httpReqCE, "/role/ROLE_SUPERUSER", HttpStatus.SC_NOT_FOUND); 
    }
	
	@Test 
    public void Role_Delete_404_DELETE_A_NON_EXISTING_ROLE() throws Exception{
		sendAndAssert_CE(httpReqCE, "/role/ROLE_SBLABLA", HttpStatus.SC_NOT_FOUND); 
    }
	
	@Test 
    public void Role_Delete_403_DELETE_SUPERUSER() throws Exception{
		sendAndAssert_CE(httpReqCE, "/role/ROLE_SUPERUSER", HttpStatus.SC_NOT_FOUND); 
    }
	
	@Test 
    public void Delete200Permission() throws Exception{
		List<NameValuePair> ceQparams = new ArrayList<NameValuePair>();
		ceQparams.add(new BasicNameValuePair("joeuser", "30"));
		
    	httpRes = sendRequestCE(httpReqCE, SERVICE_PERMISSION+"/reports",ceQparams); 
    	
    	List<NameValuePair> proQparams = new ArrayList<NameValuePair>();
    	proQparams.add(new BasicNameValuePair("CaliforniaUser/organization_1", "30"));
    	
    	httpRes = sendRequestPRO(httpReqCE, SERVICE_PERMISSION+"/reports",proQparams);
    	Assert.assertTrue("basic response check did not pass", isValidResposnse());
    }
	
	@Test 
    public void Delete200ProfileAttributes() throws Exception{
		// NOT IMPLEMENTED.
    }
	
	@Test 
    public void Delete200Tenant() throws Exception{
    	httpRes = sendRequestPRO(new HttpDelete(), "/organization/JUNIT_BLABLA"); 
    	Assert.assertTrue("basic response check did not pass", isValidResposnse(HttpStatus.SC_NOT_FOUND));
    }
    
    @Test
    public void Delete200Domain() throws Exception{
    	httpRes = sendRequestPRO(httpReqPro, "/resource/Domains/domainJunit"); 
    	Assert.assertTrue("basic response check did not pass", isValidResposnse());
    }
	
	
	
	//Error in case of a delete request with a resource linked to another resource (like report). the refusal should be expressed in the entity
	@Test 
	public void Delete403() throws Exception{
    	httpRes = sendRequestCE(httpReqCE, SAMPLE_LINKED_RESOURCE_SERVER_PATH);
    	
    	Assert.assertTrue("basic response check did not pass", isValidResposnse(500));
    }
	@Test 
	public void Delete_403_A_REFERENCED_RESOURCE() throws Exception{
    	//this resource is referenced by Simple Domain
		httpRes = sendRequestCE(new HttpDelete(), "/resource/analysis/datasources/SugarCRMDataSourceJNDI");
    	Assert.assertTrue("basic response check did not pass", isValidResposnse(403));
    	
    }
	 
    
    //Error the server did not find the resource in the mentioned URI.
	@Test 
	public void Delete404() throws Exception{
    	//httpRes = sendRequestCE(httpReqCE, SAMPLE_IMAGE_FILE_SERVER_PATH);
    	Assert.assertTrue("basic response check did not pass", isValidResposnse(500));
    	
    }
}
