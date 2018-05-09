package com.jaspersoft.jasperserver.rest.test;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.ADMIN_PASS_CE;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.ADMIN_PASS_PRO;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.ADMIN_USER_NAME_CE;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.ADMIN_USER_NAME_PRO;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.DEMO_PASSWORD;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.DEMO_USER_NAME;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.JOEUSER_PASSWORD;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.JOEUSER_USER_NAME;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.ORG;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.PIPE;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.RESOURCE;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.RESOURCE_LIST;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.SAMPLE_REPORT_URL;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.SERVICE_ATTRIBUTE;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.SERVICE_JOB;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.SERVICE_JOB_SUMMARY;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.SERVICE_ORGANIZATION;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.SERVICE_PERMISSION;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.SERVICE_ROLE;
import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.SERVICE_USER;

public class GETTest extends RESTTest
{
	private final Log log = LogFactory.getLog(getClass());
    private static ObjectMapper jsonMapper = new ObjectMapper();
    
	@Before
	public void setUp() {
    	super.setUp(); 
    	httpReqCE = new HttpGet();
    	httpReqPro = new HttpGet();
     }
	
//	/* ENTITIES-RESOURCE */

    @Test
    public void testCeLoginUsingKeyStore() throws Exception
    {
        PublicKey publicKey = getPublicKey();
        String encryptedUtfCePass = getEncryptedPassword(publicKey, ADMIN_PASS_CE);

        loginToServer(ADMIN_USER_NAME_CE, encryptedUtfCePass , ADMIN_USER_NAME_CE + PIPE + ORG, null);
        sendAndAssert_CE(httpReqCE, SERVICE_USER+"/" + ADMIN_USER_NAME_CE);
    }

    /**
     * Ignored: жасперадмин must be an admin user to be authorized to use  SERVICE_USER+"/" + russianUserName
     * This needs a better test..
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testLoginUsingKeyStoreWithForeignChars() throws Exception
    {
        String russianUserName = "жасперадмин", russianPassword = "жасперадмин";

        PublicKey publicKey = getPublicKey();
        String encryptedUtfProPass = getEncryptedPassword(publicKey, russianPassword);

        loginToServer(null, null, russianUserName, encryptedUtfProPass);
        sendAndAssert_PRO(httpReqPro, SERVICE_USER+"/" + russianUserName);
    }

    @Test
    public void testProLoginUsingKeyStore() throws Exception
    {
        PublicKey publicKey = getPublicKey();
        String encryptedUtfProPass = getEncryptedPassword(publicKey, ADMIN_PASS_PRO);

        loginToServer(null, null, ADMIN_USER_NAME_PRO, encryptedUtfProPass);
        sendAndAssert_PRO(httpReqPro, SERVICE_USER + "/" + ADMIN_USER_NAME_PRO);
    }


    @Test
    public void JobSummary_Get_200() throws Exception{
		sendAndAssert_CE(httpReqCE, SERVICE_JOB_SUMMARY+SAMPLE_REPORT_URL);
		sendAndAssert_PRO(httpReqPro, SERVICE_JOB_SUMMARY+SAMPLE_REPORT_URL);
    }
	
	@Test 
	public void Job_Get_200() throws Exception{
		putSampleJob_CE();
		int ceJobIndex = getJobIndexFromJobSummary(getSampleJobSummary_CE());
		sendAndAssert_CE(httpReqCE, SERVICE_JOB+"/"+ceJobIndex);
		deleteJob(ceJobIndex, false);
		

		putSampleJob_PRO();
		getSampleJobSummary_PRO();
		int proJobIndex = getJobIndexFromJobSummary(getSampleJobSummary_PRO());
		sendAndAssert_PRO(httpReqPro, SERVICE_JOB+"/"+proJobIndex);
		deleteJob(proJobIndex, true);
	}
	
	@Test 
    public void User_Get_200() throws Exception{
		sendAndAssert_CE(httpReqCE, SERVICE_USER+"/jasperadmin");
		sendAndAssert_PRO(httpReqPro, SERVICE_USER+"/jasperadmin"+PIPE+ORG);
    }
	
	
	
	@Test 
    public void Role_Get_200_PUBLIC_ROLES() throws Exception{
		sendAndAssert_PRO(httpReqPro, SERVICE_ROLE+"/ROLE_USER");
    }
	
	@Test 
    public void Permission_Get_200() throws Exception{
		sendAndAssert_CE(httpReqCE, SERVICE_PERMISSION+"/reports");
		sendAndAssert_PRO(httpReqPro, SERVICE_PERMISSION+"/public");
    }
	
	@Test 
    public void Attributes_Get_200() throws Exception{
		sendAndAssert_CE(httpReqCE, SERVICE_ATTRIBUTE+"/jasperadmin");
		sendAndAssert_PRO(httpReqPro, SERVICE_ATTRIBUTE+"/CaliforniaUser"+PIPE+ORG);
    }
	
	@Test 
    public void Organization_Get_200() throws Exception{
		sendAndAssert_PRO(httpReqPro, SERVICE_ORGANIZATION+"/organization_1");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("includeSubTenants", "true"));
		HttpResponse res = sendRequestPRO(httpReqPro, SERVICE_ORGANIZATION+"/organization_1", params);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(res, HttpStatus.SC_OK));
    }
	

	

	/* SERVER RESOURCES */
	@Test 
    public void Resources_Folder_GET_200() throws Exception{
		sendAndAssert_CE(httpReqCE, RESOURCE_LIST+"/ContentFiles");
		sendAndAssert_PRO(httpReqPro, RESOURCE_LIST+"/");
    }
	
	@Test 
    public void Resources_Type_Recursive_GET_200() throws Exception{
		sendAndAssert_CE(httpReqCE, RESOURCE_LIST+"/?type=reportUnit&recursive=true");
		sendAndAssert_PRO(httpReqPro, RESOURCE_LIST+"/?type=reportUnit&recursive=true");
    }
	
	@Test 
    public void Resource_Image_File_GET_200() throws Exception{
		putSampleFolderAndFile();
		
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    	qparams.add(new BasicNameValuePair("fileData", "true"));
    	httpRes = sendRequestCE(httpReqCE, RESOURCE+"/JUNIT_NEW_FOLDER/JUNIT_IMAGE_FILE", qparams);
    	Assert.assertTrue("wrong file size: ", isValidResposnse()&& httpRes.getEntity().getContentLength() == 4788);
    	
    	httpRes = sendRequestPRO(httpReqPro, RESOURCE+"/JUNIT_NEW_FOLDER/JUNIT_IMAGE_FILE", qparams);
    	Assert.assertTrue("wrong file size: ", isValidResposnse()&& httpRes.getEntity().getContentLength() == 4788);
    	
    	deleteSampleFolder();
	}
	
	// returns a report xml
	@Test 
    public void Resources_Report_GET_200() throws Exception{
		sendAndAssert_CE(httpReqCE, RESOURCE+"/reports/samples/AllAccounts");
		sendAndAssert_PRO(httpReqPro, RESOURCE+"/reports/samples/AllAccounts");
	}
	
	//return the RD of a local resource
	@Test  
	public void Resources_LocalResource_GET_200() throws Exception{
		sendAndAssert_CE(httpReqCE, RESOURCE+"/reports/samples/AllAccounts_files/AllAccounts_Res2");
		sendAndAssert_PRO(httpReqPro, RESOURCE+"/reports/samples/AllAccounts_files/AllAccounts_Res2");
	}

	//return the binary content of a local resource
	@Test  
	public void Resources_LocalResource_BIN_GET_200() throws Exception{
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    	qparams.add(new BasicNameValuePair("fileData", "true"));
    	httpRes = sendRequestCE(httpReqCE, RESOURCE+"/reports/samples/AllAccounts_files/AllAccounts_Res2", qparams);
    	Assert.assertTrue("wrong file size: ", httpRes.getEntity().getContentLength() == 5181 && isValidResposnse());
    	
    	httpRes = sendRequestPRO(httpReqPro, RESOURCE+"/reports/samples/AllAccounts_files/AllAccounts_Res2", qparams);
    	Assert.assertTrue("wrong file size: ", httpRes.getEntity().getContentLength() == 5181 && isValidResposnse());
	}
	
	@Test  
	public void Resources_InputControl_GET_200() throws Exception{
		putSampleInputControlInSampleFolder();
		
		sendAndAssert_CE(httpReqCE, RESOURCE+"/JUNIT_NEW_FOLDER/JUNIT_INPUT_CONTROL_CONTAINS_LOCAL_RESOURCE");
		sendAndAssert_PRO(httpReqPro, RESOURCE+"/JUNIT_NEW_FOLDER/JUNIT_INPUT_CONTROL_CONTAINS_LOCAL_RESOURCE");
    	
    	deleteSampleInputControl();
    	deleteSampleFolder();
	}
	
	@Test  
	public void Resources_JRXML_GET_200() throws Exception{
		sendAndAssert_CE(httpReqCE, RESOURCE+"/reports/samples/AllAccounts_files/AllAccountsReport");
		sendAndAssert_PRO(httpReqPro, RESOURCE+"/reports/samples/AllAccounts_files/AllAccountsReport");
	}
	
	@Test  
	public void Resource_DomainDataSource_GET_200() throws Exception{
		sendAndAssert_PRO(httpReqPro, RESOURCE+"/Domains/Simple_Domain");
	}
	
	@Test  
	public void Resource_Query_GET_200() throws Exception{
		sendAndAssert_PRO(httpReqPro, RESOURCE+"/supermart/Common/CustomerCityQuery");
	}
	
	@Test  
	public void Resource_DomainTopic_GET_200() throws Exception{
		sendAndAssert_PRO(httpReqPro, RESOURCE+"/adhoc/topics/Simple_Domain_Topic");
	}
	
	@Test
	public void Resource_Dashboard_GET_200() throws Exception{
		sendAndAssert_PRO(httpReqPro, RESOURCE+"/supermart/SupermartDashboard30");
	}

    @Test
    /**
     * @throws Exception
     * @description 4.7
     * Get public key
     * log in as jasperadmin - OK
     * log in as joeuser - should fail
     * log in as demouser - should fail
     */
    public void testLoginWithDynamicKeyHappensOncePerRequest() throws Exception {
        PublicKey publicKey = getPublicKey();
        String jasperadminPasswd = getEncryptedPassword(publicKey, ADMIN_PASS_PRO);
        String joeuserPasswd = getEncryptedPassword(publicKey, JOEUSER_PASSWORD);
        String demoPasswd = getEncryptedPassword(publicKey, DEMO_PASSWORD);

        loginToServer(null, null, ADMIN_USER_NAME_PRO, jasperadminPasswd);
        final String httpResStatusLine = httpRes.getStatusLine().toString();
        Assert.assertTrue("Login failed", httpResStatusLine.indexOf("OK") >= 0);

        loginToServer(null, null, JOEUSER_USER_NAME, joeuserPasswd);
        final String httpResStatusLine2 = httpRes.getStatusLine().toString();
        Assert.assertFalse("Logged in successfully 2, failure expected", httpResStatusLine2.indexOf("OK") >= 0);

        loginToServer(null, null, DEMO_USER_NAME, demoPasswd);
        final String httpResStatusLine3 = httpRes.getStatusLine().toString();
        Assert.assertFalse("Logged in successfully 3, failure expected", httpResStatusLine3.indexOf("OK") >= 0);
    }

    /**
     * Convert byteArr to hex sting.
     * @param byteArr
     * @return
     */
    private static String byteArrayToHexString(byte[] byteArr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteArr.length; i++) {
            byte b = byteArr[i];
            int high = (b & 0xF0) >> 4;
            int low = b & 0x0F;

            sb.append(Character.forDigit(high, 16));
            sb.append(Character.forDigit(low, 16));
        }
        return sb.toString();
    }

    private String getEncryptedPassword(PublicKey publicKey, String pwd) throws Exception{
        Cipher enc = Cipher.getInstance("RSA/NONE/NoPadding", new BouncyCastleProvider());
        enc.init(Cipher.ENCRYPT_MODE, publicKey);

        String utfPass = URLEncoder.encode(pwd, CharEncoding.UTF_8);
        byte[] encryptedUtfPass = enc.doFinal(utfPass.getBytes());

        return byteArrayToHexString(encryptedUtfPass);
    }

    private PublicKey getPublicKey() throws Exception{
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        HttpResponse httpRes = sendRequestProFroNonRestService(httpReqPro, "/GetEncryptionKey", null);

        String entityStr = IOUtils.toString(httpRes.getEntity().getContent());
        JSONObject jpk = new JSONObject(entityStr);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(jpk.getString("n"), 16), new BigInteger(jpk.getString("e"), 16));
        return keyFactory.generatePublic(publicKeySpec);
    }
}

