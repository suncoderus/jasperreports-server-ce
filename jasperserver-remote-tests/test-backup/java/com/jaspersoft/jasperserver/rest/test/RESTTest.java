package com.jaspersoft.jasperserver.rest.test;

import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.TenantImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.rest.RESTUtils;
import com.jaspersoft.jasperserver.rest.test.RESTTestUtils.ReportOutputFormat;
import com.jaspersoft.jasperserver.rest.test.helper.Common;
import com.jaspersoft.jasperserver.rest.utils.JAXBList;
import com.jaspersoft.jasperserver.ws.authority.WSRole;
import com.jaspersoft.jasperserver.ws.authority.WSUser;


public class RESTTest{
	
	//Different consts
	protected static final String REQUEST_PARAMENTER_RD = "ResourceDescriptor";
	protected static final String REQUEST_PARAMENTER_RESOURCE_BIN = "ResourceBinaries";
	protected static final String REQUEST_SAMPLE_IMAGE_PATH = "/ContentFiles/JUNIT_IMAGE_FILE";
	
	private HttpClient httpClient;
	private	CookieStore cookieStore;
	private HttpContext httpContext;
	
	
	protected HttpRequestBase httpReqCE;
	protected HttpRequestBase httpReqPro;
	protected HttpRequestBase httpReq;
	
	protected HttpRequestBase tempHttpReq;
	protected HttpResponse httpRes;
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Before
	public void setUp() {
    	httpClient = new DefaultHttpClient();
    	cookieStore = new BasicCookieStore();
    	httpContext = new BasicHttpContext();
    	httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    	
//    	loginToServer();
     }
	
	@After
    	//releasing the related streams
	public void tearDown() throws Exception{
    	if (httpRes.getEntity().getContent().available()!=0){
    		httpRes.getEntity().getContent();
    	}
    	httpClient.getConnectionManager().closeExpiredConnections();
	}


	public void createRESTOrganization(Tenant tenant, int expectedStatsCode) throws Exception {

        httpReq = new HttpPut();

        StringWriter sw = new StringWriter();

        RESTUtils.getMarshaller(TenantImpl.class).marshal(tenant, sw);

        BasicHttpEntity reqEntity = new BasicHttpEntity();
        reqEntity.setContent(new ByteArrayInputStream(sw.toString().getBytes()));

        ((HttpEntityEnclosingRequestBase) httpReq).setEntity(reqEntity);

        httpRes = sendRequestPRO(httpReq, TENANT_BASE_URL);
        try {
            assertValidResponseCode(expectedStatsCode, httpRes.getStatusLine().getStatusCode());
        } finally {
            getResponseBody(httpRes.getEntity());
        }
    }
	
	public static String getResponseBody(final HttpEntity entity) throws IOException, java.text.ParseException{

        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }

        InputStream instream = entity.getContent();

        return inputStreamToString(instream);
    }
	
	public static String inputStreamToString(InputStream instream) throws IOException, java.text.ParseException{

        if (instream == null) {
            throw new IllegalArgumentException("InputStream can not be null");
        }

        Reader reader = new InputStreamReader(instream);
        StringBuilder buffer = new StringBuilder();

        try {
            char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }

        } finally {
            reader.close();
        }

        
        return buffer.toString();
    }
	
	public static void assertValid404ResponseCode(int actualCode) throws Exception {
		assertValidResponseCode(HttpStatus.SC_NOT_FOUND, actualCode);
	}
	
	public static void assertValidResponseCode(int expectedCode, int actualCode) throws Exception 
	{
		Assert.assertTrue(	"Basic response check did not pass; Expected code = " + expectedCode + "; Actual code = " + actualCode, 
							isValidResponse(expectedCode, actualCode));
	}
	
	public static boolean isValidResponse(int expected_Code, int actualCode) throws Exception {
        return actualCode == expected_Code;
    }
	
	//Successful delete with a path
    public void deleteResource(String uri) throws Exception
    {
    	deleteResource_CE(uri);
    	deleteResource_PRO(uri);
    }
    
	//Successful delete with a path
    public void deleteResource_CE(String uri) throws Exception
    {
    	httpRes = sendRequestCE(new HttpDelete(), uri);
    	Assert.assertTrue("basic response check did not pass", isValidResposnse());
    }
    
    //Successful delete with a path
    public void deleteResource_PRO(String uri) throws Exception
    {
    	httpRes = sendRequestPRO(new HttpDelete(), uri);
    	Assert.assertTrue("basic response check did not pass", isValidResposnse());
    }
    
//    protected void putSampleImageFileResource(String serverPath) throws Exception 
//    {
//    	tempHttpReq = new HttpPut();
//    	
//    	
//    	//building a multipart request
//		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//		
//		//appending the file descriptor from a file 
//		String rdFileName = LOCAL_NEW_RDS+RD_IMAGE_FILE;
//		FileBody bin = new FileBody(new File(LOCAL_RESOURCE_DESCRIPTOR_PATH+SAMPLE_IMAGE_FILE_BIN));
//		
//		reqEntity.addPart(REQUEST_PARAMENTER_RD, new StringBody(RESTTestUtilsAndConsts.getResourceDescriptor(new File(rdFileName))));
//		reqEntity.addPart(REQUEST_SAMPLE_IMAGE_PATH, bin );
//		
//		((HttpEntityEnclosingRequestBase)tempHttpReq).setEntity(reqEntity);
//		
//		//executing the request
//		httpRes = executeCall(tempHttpReq, serverPath);
//		Assert.assertTrue("basic response check did not pass", isValidResposnse(201));
//    }
    
    // puts a sample image file in CE and PRO at /JUNIT_NEW_FOLDER/JUNIT_IMAGE_FILE 
    
    protected void putSampleFolderAndFile() throws Exception{
    	putSampleFolderAndFile(	new HttpPut(), 
    					LOCAL_NEW_RDS+SAMPLE_IMAGE_FILE_RD, 
    					LOCAL_RESOURCE_DESCRIPTOR_PATH+SAMPLE_IMAGE_FILE_BIN);
    }
    protected void putSampleFolderAndFile(HttpRequestBase req, String fileRDPath, String fileBinPath) throws Exception 
    {
		putSampleFolder();
		putSampleFileInSampleFolder(req, fileRDPath, fileBinPath, "");
    }
    
    public void putSampleFileInSampleFolder(HttpRequestBase req, String fileRDPath, String fileBinPath, String path) throws Exception{
    	tempHttpReq = req;
    	MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart(REQUEST_PARAMENTER_RD, new StringBody(RESTTestUtilsAndConsts.getResourceDescriptor(new File(fileRDPath))));
		FileBody bin = new FileBody(new File(fileBinPath));
		reqEntity.addPart("/JUNIT_NEW_FOLDER/JUNIT_IMAGE_FILE", bin );
		((HttpEntityEnclosingRequestBase)tempHttpReq).setEntity(reqEntity);
		
		//executing the request
		httpRes = sendRequestCE(tempHttpReq, "/resource/JUNIT_NEW_FOLDER"+path);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(httpRes, HttpStatus.SC_CREATED)||httpRes.getStatusLine().getStatusCode()==HttpStatus.SC_OK);
		
		//executing the request
		httpRes = sendRequestPRO(tempHttpReq, "/resource/JUNIT_NEW_FOLDER"+path);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(HttpStatus.SC_CREATED)||httpRes.getStatusLine().getStatusCode()==HttpStatus.SC_OK);
	}

	protected void putSampleFolder() throws Exception{
    	tempHttpReq = new HttpPut();
    	sendAndAssert_CE(tempHttpReq, RESOURCE+"/JUNIT_NEW_FOLDER", LOCAL_NEW_RDS + SAMPLE_FOLDER_RD, HttpStatus.SC_CREATED);
    	sendAndAssert_PRO(tempHttpReq, RESOURCE+"/JUNIT_NEW_FOLDER", LOCAL_NEW_RDS + SAMPLE_FOLDER_RD, HttpStatus.SC_CREATED);
    }
	
	protected void putSampleReport() throws Exception{
		putSampleFolder();
		putSampleReport(new HttpPut(), LOCAL_RESOURCE_DESCRIPTOR_PATH+SAMPLE_IMAGE_FILE_BIN, "");
		
	}
	
	protected void putSampleReport(HttpRequestBase req, String fileBinPath, String path) throws Exception
	{
		String SalesByMonthReportURI="/JUNIT_NEW_FOLDER/JUNIT_REPORT_UNIT_SALES_BY_MONTH_files/SalesByMonthReport";
		String ScriptletURI="/JUNIT_NEW_FOLDER/JUNIT_REPORT_UNIT_SALES_BY_MONTH_files/Scriptlet";
		String LogoURI="/JUNIT_NEW_FOLDER/JUNIT_REPORT_UNIT_SALES_BY_MONTH_files/Logo";
		String SalesByMonthDetailURI="/JUNIT_NEW_FOLDER/JUNIT_REPORT_UNIT_SALES_BY_MONTH_files/SalesByMonthDetail";
		String sales_propertiesURI="/JUNIT_NEW_FOLDER/JUNIT_REPORT_UNIT_SALES_BY_MONTH_files/sales.properties";
		String sales_ro_propertiesURI="/JUNIT_NEW_FOLDER/JUNIT_REPORT_UNIT_SALES_BY_MONTH_files/sales_ro.properties";
		
		
		String SalesByMonthReportLocalPath = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/SalesByMonthReport.jrxml";
		String ScriptletLocalPath = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/scriptlet.jar";
		String LogoLocalPath = LOCAL_RESOURCE_DESCRIPTOR_PATH+SAMPLE_IMAGE_FILE_BIN;
		String SalesByMonthDetailLocalPath = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/jasperReport_AllAccounts.jrxml";
		String sales_propertiesLocalPath = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/sales.properties";
		String sales_ro_propertiesLocalPath = LOCAL_RESOURCE_DESCRIPTOR_PATH+"/sales_ro.properties"; 
		
		String fileRd = LOCAL_NEW_RDS+SAMPLE_COMPOLEX_REPORT_UNIT_RD;
		
		FileBody bin;
		
    	//building the body
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		//appending the file descriptor from a file 
		reqEntity.addPart(REQUEST_PARAMENTER_RD, new StringBody(RESTTestUtilsAndConsts.getResourceDescriptor(new File(fileRd))));
		
		//appending the binaries to the request body
		bin = new FileBody(new File(SalesByMonthReportLocalPath));
		reqEntity.addPart(SalesByMonthReportURI, bin );
		
		//appending the binaries to the request body
		bin = new FileBody(new File(ScriptletLocalPath));
		reqEntity.addPart(ScriptletURI, bin );
		
		//appending the binaries to the request body
		bin = new FileBody(new File(LogoLocalPath));
		reqEntity.addPart(LogoURI, bin );
		
		//appending the binaries to the request body
		bin = new FileBody(new File(SalesByMonthDetailLocalPath));
		reqEntity.addPart(SalesByMonthDetailURI, bin );
		
		//appending the binaries to the request body
		bin = new FileBody(new File(sales_propertiesLocalPath));
		reqEntity.addPart(sales_propertiesURI, bin );
		
		//appending the binaries to the request body
		bin = new FileBody(new File(sales_ro_propertiesLocalPath));
		reqEntity.addPart(sales_ro_propertiesURI, bin );
		
		((HttpEntityEnclosingRequestBase)req).setEntity(reqEntity);
		
		//executing the request
		httpRes = sendRequestCE(req, 	RESOURCE+"/JUNIT_NEW_FOLDER"+path);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(201));
		
		//executing the request
		httpRes = sendRequestPRO(req, RESOURCE+"/JUNIT_NEW_FOLDER"+path);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(201));
	}
	
	protected void deleteSampleReport()throws Exception{
		sendAndAssert_CE(new HttpDelete(), RESOURCE+"/JUNIT_NEW_FOLDER/JUNIT_REPORT_UNIT_SALES_BY_MONTH");
    	sendAndAssert_PRO(new HttpDelete(), RESOURCE+"/JUNIT_NEW_FOLDER/JUNIT_REPORT_UNIT_SALES_BY_MONTH");
	}
    
    
    
    protected void deleteSampleFolder() throws Exception{
		sendAndAssert_CE(new HttpDelete(), RESOURCE+"/JUNIT_NEW_FOLDER");
		sendAndAssert_PRO(new HttpDelete(), RESOURCE+"/JUNIT_NEW_FOLDER");
    	
    }
    
	public void Resource_LocalResource_of_ReportUnit_PUT_200(HttpRequestBase req, String fileRDPath, String fileBinPath, String entityBinKey, String resourceURI) throws Exception 
	{
		putSampleReport();
		
		tempHttpReq = req;
		
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart(REQUEST_PARAMENTER_RD, new StringBody(RESTTestUtilsAndConsts.getResourceDescriptor(new File(fileRDPath))));
		FileBody bin = new FileBody(new File(fileBinPath));
		reqEntity.addPart(entityBinKey, bin );
		((HttpEntityEnclosingRequestBase)tempHttpReq).setEntity(reqEntity); 
		
		//executing the request
		httpRes = sendRequestCE(tempHttpReq, RESOURCE+resourceURI);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(httpRes, HttpStatus.SC_CREATED)||httpRes.getStatusLine().getStatusCode()==HttpStatus.SC_OK);
		
		//executing the request
		httpRes = sendRequestPRO(tempHttpReq, RESOURCE+resourceURI);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(HttpStatus.SC_CREATED)||httpRes.getStatusLine().getStatusCode()==HttpStatus.SC_OK);
	}
	
	void putSampleRole_CE() throws Exception{
		putSampleResource_CE(LOCAL_RESOURCE_DESCRIPTOR_PATH + SAMPLE_ROLE_CE_RD, SERVICE_ROLE);
	}
	
	void putSampleRole_PRO() throws Exception{
		putSampleResource_PRO(LOCAL_RESOURCE_DESCRIPTOR_PATH + SAMPLE_ROLE_PRO_RD, SERVICE_ROLE);
	}
	
		
	
	protected void putSampleResource(String rd_path, String call_path) throws Exception
    {
		putSampleResource_CE(rd_path, call_path);
		putSampleResource_PRO(rd_path, call_path);
    }
    
    protected void putSampleResource_CE(String rd_path, String call_path) throws Exception
    {
    	tempHttpReq = new HttpPut();

    	//building the body
		BasicHttpEntity reqEntity = new BasicHttpEntity();
		
		//appending the file descriptor from a file 
		reqEntity.setContent(new FileInputStream(new File(rd_path)));
		((HttpEntityEnclosingRequestBase)tempHttpReq).setEntity(reqEntity);
		
		//executing the request
		httpRes = sendRequestCE(tempHttpReq, call_path);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(201));
    }
    
    protected void putSampleResource_PRO(String rd_path, String call_path) throws Exception
    {
    	tempHttpReq = new HttpPut();

    	//building the body
		BasicHttpEntity reqEntity = new BasicHttpEntity();
		
		//appending the file descriptor from a file 
		reqEntity.setContent(new FileInputStream(new File(rd_path)));
		((HttpEntityEnclosingRequestBase)tempHttpReq).setEntity(reqEntity);
		
		//executing the request
		httpRes = sendRequestPRO(tempHttpReq, call_path);
		Assert.assertTrue("basic response check did not pass", isValidResposnse(201));
    }
    
    protected void putSampleInputControlInSampleFolder() throws Exception{
    	putSampleInputControlInSampleFolder(	new HttpPut(), LOCAL_NEW_RDS+SAMPLE_INPUT_CONTROL_WITH_LOCAL_RESOURCE_RD);
    }
    protected void putSampleInputControlInSampleFolder(HttpRequestBase req, String fileRDPath) throws Exception 
    {
		putSampleFolder();
		putSampleInputControlInSampleFolder(req, fileRDPath, "", HttpStatus.SC_CREATED);
    }
    
    public void putSampleInputControlInSampleFolder(HttpRequestBase req, String fileRDPath, String path, int expectedStatus) throws Exception{
    	sendAndAssert_CE(req, 	RESOURCE+"/JUNIT_NEW_FOLDER"+path, fileRDPath, expectedStatus);
    	sendAndAssert_PRO(req, 	RESOURCE+"/JUNIT_NEW_FOLDER"+path, fileRDPath, expectedStatus);
		
    }
    
    public void deleteSampleInputControl() throws Exception{
    	sendAndAssert_CE(new HttpDelete(), RESOURCE+"/JUNIT_NEW_FOLDER/JUNIT_INPUT_CONTROL_CONTAINS_LOCAL_RESOURCE");
		sendAndAssert_PRO(new HttpDelete(), RESOURCE+"/JUNIT_NEW_FOLDER/JUNIT_INPUT_CONTROL_CONTAINS_LOCAL_RESOURCE");
    }
    
    //Job
    public void putSampleJob_CE() throws Exception{
    	putJob(false);
    	
    }
    
    // adds a basic scheduling job in PRO for the AllAccount report
    public void putSampleJob_PRO() throws Exception{
    	putJob(true);
    }
    
    private void putJob(boolean isPro) throws Exception
    {
    	tempHttpReq = new HttpPut();

    	BasicHttpEntity reqEntity = new BasicHttpEntity();
		reqEntity.setContent(new FileInputStream(LOCAL_NEW_RDS + SAMPLE_JOB_RD));
		
		((HttpEntityEnclosingRequestBase)tempHttpReq).setEntity(reqEntity);
		
		
		//executing the request
		if (!isPro)
			sendAndAssert_CE(tempHttpReq, SERVICE_JOB, HttpStatus.SC_CREATED);
		else 
			sendAndAssert_PRO(tempHttpReq, SERVICE_JOB, HttpStatus.SC_CREATED);
	}
    
    protected void deleteJob(int jobId, boolean isPro) throws Exception{
    	tempHttpReq = new HttpDelete();
    	if (!isPro)
			sendAndAssert_CE(tempHttpReq, SERVICE_JOB+"/"+jobId);
		else 
			sendAndAssert_PRO(tempHttpReq, SERVICE_JOB+"/"+jobId);
    }
    
    //Job summary
    public String getSampleJobSummary_CE()throws Exception{
		return getJobSummeryForReport(false);
    }
    
    public String getSampleJobSummary_PRO()throws Exception{
		return getJobSummeryForReport(true);
    }
    
    private String getJobSummeryForReport(boolean isPro) throws Exception{
    	tempHttpReq = new HttpGet();
		if (!isPro)
			httpRes = sendRequestCE(tempHttpReq, SERVICE_JOB_SUMMARY+SAMPLE_REPORT_URL);
		else 
			httpRes = sendRequestPRO(tempHttpReq, SERVICE_JOB_SUMMARY+SAMPLE_REPORT_URL);
    	Assert.assertTrue("basic response check did not pass", httpRes.getStatusLine().getStatusCode()==HttpStatus.SC_OK);
    	return IOUtils.toString(httpRes.getEntity().getContent());
    }
    
    //this function relays on the structure of jobSummary XML.
    protected int getJobIndexFromJobSummary(String jobSummaryXML){
    	int jobIDStartIndex = jobSummaryXML.indexOf("<id>")+"<id>".length();
    	int jobIDEndIndex = jobSummaryXML.indexOf("</id>");
    	
    	return Integer.parseInt(jobSummaryXML.substring(jobIDStartIndex, jobIDEndIndex));
    }
    
    public void loginToServer() {
    	loginToServer(ADMIN_USER_NAME_CE, ADMIN_PASS_CE, ADMIN_USER_NAME_PRO + PIPE + ORG, ADMIN_PASS_PRO);
    }

    public void loginToPROServer(String userName, String passoword) {
    	loginToServer(ADMIN_USER_NAME_CE, ADMIN_PASS_CE, userName, passoword);
    }
    
    public void loginToPROServer(String userName, String passoword, String organization) {
    	loginToServer(ADMIN_USER_NAME_CE, ADMIN_PASS_CE, userName + PIPE + organization, passoword);
    }
    
    public void loginToServer(String ceUserName, String cePassword, String proUserName, String proPassword) {
    	//building the request parameters
    	List<NameValuePair> ce_qparams = new ArrayList<NameValuePair>();
    	ce_qparams.add(new BasicNameValuePair(PARAMETER_USERNAME, ceUserName));
    	ce_qparams.add(new BasicNameValuePair(PARAM_PASSWORD, cePassword));
    	
    	List<NameValuePair> pro_qparams = new ArrayList<NameValuePair>();
    	pro_qparams.add(new BasicNameValuePair(PARAMETER_USERNAME, proUserName));
    	pro_qparams.add(new BasicNameValuePair(PARAM_PASSWORD, proPassword));
    	
    	try {
    		httpRes = sendRequestCE(new HttpPost(), SERVICE_LOGIN, ce_qparams);
			
    		//consuming the content to close the stream
			IOUtils.toString(httpRes.getEntity().getContent());
			
			httpRes = sendRequestPRO(new HttpPost(), SERVICE_LOGIN, pro_qparams);

			IOUtils.toString(httpRes.getEntity().getContent());
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /* SERVICE FUNCTION */	
    protected void sendAndAssert_CE(HttpRequestBase req, String service, String rdPath, int expectedStatus) throws Exception{
    	//building the body
		BasicHttpEntity reqEntity = new BasicHttpEntity();
		
		//appending the file descriptor from a file 
		reqEntity.setContent(new FileInputStream(rdPath));
		
		((HttpEntityEnclosingRequestBase)req).setEntity(reqEntity);
		
		
		//executing the request
		sendAndAssert_CE(req, service, expectedStatus);
	}
    
    protected void sendAndAssert_PRO(HttpRequestBase req, String service, String rdPath, int expectedStatus) throws Exception{
    	//building the body
		BasicHttpEntity reqEntity = new BasicHttpEntity();
		
		//appending the file descriptor from a file 
		reqEntity.setContent(new FileInputStream(rdPath));
		
		((HttpEntityEnclosingRequestBase)req).setEntity(reqEntity);
		
		
		//executing the request
		sendAndAssert_PRO(req, service, expectedStatus);
    }

    protected void sendAndAssert_CE(HttpRequestBase req, String service, String rdPath) throws Exception{
    	sendAndAssert_CE(req, service, rdPath, HttpStatus.SC_OK);
	}
    
    protected void sendAndAssert_PRO(HttpRequestBase req, String service, String rdPath) throws Exception{
    	sendAndAssert_PRO(req, service, rdPath, HttpStatus.SC_OK);
    }
    
    protected void sendAndAssert_CE(HttpRequestBase req, String service, int expectedStatus) throws Exception{
    	httpRes = sendRequestCE(req, service); 
    	Assert.assertTrue("basic response check did not pass", isValidResposnse(expectedStatus));
    }
    
    protected void sendAndAssert_PRO(HttpRequestBase req, String service, int expectedStatus) throws Exception{
    	httpRes = sendRequestPRO(req, service); 
    	Assert.assertTrue("basic response check did not pass", isValidResposnse(expectedStatus));
    }
    
    protected void sendAndAssert_CE(HttpRequestBase req, String service) throws Exception{
    	httpRes = sendRequestCE(req, service); 
    	Assert.assertTrue("basic response check did not pass", isValidResposnse(HttpStatus.SC_OK));
    }
    
    protected void sendAndAssert_PRO(HttpRequestBase req, String service) throws Exception{
    	httpRes = sendRequestPRO(req, service); 
    	Assert.assertTrue("basic response check did not pass", isValidResposnse(HttpStatus.SC_OK));
    }
    
    // send a request to the CE server
	protected HttpResponse sendRequestCE(HttpRequestBase req, String service) throws Exception{
		return sendRequestCE(req, service, null);
	}
    
    protected HttpResponse sendRequestCE(HttpRequestBase req, String service, List<NameValuePair> qparams) throws Exception
    {
    	URI uri = createURI(service, qparams, false);
    	req.setURI(uri);
    	StringBuffer sb;
    	
    	return httpClient.execute(req, httpContext);
    }

    protected HttpResponse sendRequestCeFroNonRestService(HttpRequestBase req, String service, List<NameValuePair> qparams) throws Exception
    {
        URI uri = createURIForNoneRestService(service, qparams, false);
        req.setURI(uri);
        StringBuffer sb;

        return httpClient.execute(req, httpContext);
    }

    protected HttpResponse sendRequestProFroNonRestService(HttpRequestBase req, String service, List<NameValuePair> qparams) throws Exception
    {
        URI uri = createURIForNoneRestService(service, qparams, true);
        req.setURI(uri);
        StringBuffer sb;

        return httpClient.execute(req, httpContext);
    }


    // send a request to the PRO server
    protected HttpResponse sendRequestPRO(HttpRequestBase req, String service) throws Exception{
		return sendRequestPRO(req, service, null);
	}
    
    protected HttpResponse sendRequestPRO(HttpRequestBase req, String service, List<NameValuePair> qparams) throws Exception
    {
    	URI uri = createURI(service, qparams, true);
    	req.setURI(uri);
    	
    	return httpClient.execute(req, httpContext);
    }

    private URI createURIForNoneRestService(String servicePath, List<NameValuePair> qparams, boolean isPro) throws Exception{
        if (isPro){
            return createURI(JASPERSERVER_PRO_URL+servicePath, qparams);
        }
        else {
            return createURI(JASPERSERVER_CE_URL+servicePath, qparams);
        }
    }

    private URI createURI(String servicePath, List<NameValuePair> qparams, boolean isPro) throws Exception{
    	if (isPro){
	    	return createURI(BASE_REST_PRO_URL+servicePath, qparams);
		}
		else {
			return createURI(BASE_REST_URL+servicePath, qparams);
		}
    }
    
    private URI createURI(String servicePath, List<NameValuePair> qparams) throws Exception{
    	URI uri;
    	
    	
    	if (qparams!=null)
	    	uri = URIUtils.createURI(SCHEME, HOST, PORT, servicePath, URLEncodedUtils.format(qparams, "UTF-8"), null);
	    
	    else
	    	uri = (new URL(SCHEME, HOST, PORT, servicePath)).toURI(); 
	    return uri;
    }
    
    protected boolean isValidBinaryResposnse() throws Exception{
    	return isValidResposnse() && httpRes.getHeaders("Content-Type")!=null && httpRes.getHeaders("Content-Length")!=null; 
    }
    
    protected boolean isValidResposnse() throws Exception{
    	return isValidResposnse(HttpStatus.SC_OK);
    }
    
    protected boolean isValidResposnse(int expected_respose_code) throws Exception{
    	String body = EntityUtils.toString(httpRes.getEntity());
    	if (SHOW_SPEC_MODE)
    		System.out.println(body);

    	return 	httpRes.getStatusLine().getStatusCode()==expected_respose_code;
    }
    
    protected boolean isValidResposnse(HttpResponse res, int expected_respose_code) throws Exception{
    	InputStream is = res.getEntity().getContent();
    	is.close();
    	return 	httpRes.getStatusLine().getStatusCode()==expected_respose_code;
    }
    
    public ReportDescriptor runReport(String reportName, String reportUrl, ReportOutputFormat outputFormat) throws Exception {

        ReportDescriptor reportDescriptor = new ReportDescriptor();

        ResourceDescriptor rd = new ResourceDescriptor();
        rd.setWsType(ResourceDescriptor.TYPE_REPORTUNIT);
        rd.setName(reportName);
        rd.setUriString(reportUrl + "/" + reportName);
        rd.setIsNew(false);

        //assembling the request
        httpReq = RESTTestUtils.assembleRequest(rd, new HttpPut());

        //executing the request
        httpRes = sendRequestPRO(httpReq, REPORT_BASE_URL + ((outputFormat != null) ? "/?" + RUN_OUTPUT_FORMAT + "=" + outputFormat : ""));

        //parsing response
        reportDescriptor.parseXMLResponse(httpRes);

        return reportDescriptor;
    }
    
    /**
     * Saves report output in REST_RESULTS_FOLDER
     *
     * @param reportDescriptor - Report Descriptor
     * @param expectedFileSize - expected file size in bytes, may be "null"
     * @throws Exception -
     */
    public void saveReport(ReportDescriptor reportDescriptor, Integer expectedFileSize) throws Exception {

        String fileExt = reportDescriptor.getFileExtension();
        String[] tmp = reportDescriptor.originalUri.split("[/]");
        String reportName = tmp[tmp.length - 1];
        //Creating an other request to get the report data
        tempHttpReq = new HttpGet();

        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair(ReportDescriptor.TAG_NAME_FILE, (reportDescriptor.isJasperPrint()) ? TAG_VALUE_JASPERPRINT : TAG_VALUE_REPORT));

        //Getting a response
        httpRes = sendRequestCE(tempHttpReq, REPORT_BASE_URL + "/" + reportDescriptor.uuid, qparams);

        //Saving report to an output file
        Common.saveResourceToFile(httpRes, reportName, fileExt);

        RESTTestUtils.assertCorrectFileSize(REST_RESULTS_FOLDER + "/" + reportName + "." + fileExt, (expectedFileSize != null) ? expectedFileSize : 0);
    }
    
    public ReportDescriptor changeReportParametersInSession(ReportDescriptor reportDescriptor, List<NameValuePair> qparams) throws Exception {

        //Creating an other request to change the report data
        tempHttpReq = new HttpPost();

        //Getting a response, the output format changes in session
        httpRes = executeCall(tempHttpReq, REPORT_BASE_URL + "/" + reportDescriptor.uuid, qparams);

        //parsing response
        reportDescriptor.parseXMLResponse(httpRes);

        return reportDescriptor;
    }
    
    protected HttpResponse executeCall(HttpRequestBase req, String action_path, List<NameValuePair> qparams) throws Exception {
        req.setURI(createURI(action_path, qparams));
        httpRes = httpClient.execute(req, httpContext);

        return httpRes;
    }
    
    protected HttpResponse executeCall(HttpRequestBase req, String action_path) throws Exception {
        return executeCall(req, action_path, null);
    }

    public WSRole[] getRESTRoles(String role) throws Exception {

        List<WSRole> wsRoles = getRESTRoles("/" + role, HttpStatus.SC_OK);
        WSRole[] roles = new WSRole[wsRoles.size()];

        return wsRoles.toArray(roles);
    }
    
    public List<WSRole> getRESTRoles(String role, int expectedStatsCode) throws Exception {
        httpReq = new HttpGet();
        httpRes = sendRequestPRO(httpReq, ROLE_BASE_URL + role);
        assertValidResponseCode(expectedStatsCode, httpRes.getStatusLine().getStatusCode());

        InputStream getUsersResp = httpRes.getEntity().getContent();
        String usersRespString = IOUtils.toString(getUsersResp);

        List<WSRole> foundRoles = new ArrayList<WSRole>();

        String[] foundRolesParts = usersRespString.split("<role>");
        String foundRolesPart;

        for (int i = 1; i < foundRolesParts.length; i++) {
            foundRolesPart = "<role>" + foundRolesParts[i].replace("</roles>", "");
            foundRoles.add(RESTUtils.unmarshal(WSRole.class, new ByteArrayInputStream(foundRolesPart.getBytes())));
        }

        return foundRoles;
    }
    
    public void createRESTUser(WSUser user, int expectedStatsCode) throws Exception {

        httpReq = new HttpPut();

        StringWriter sw = new StringWriter();

        RESTUtils.getMarshaller(WSUser.class).marshal(user, sw);

        BasicHttpEntity reqEntity = new BasicHttpEntity();
        reqEntity.setContent(new ByteArrayInputStream(sw.toString().getBytes()));

        ((HttpEntityEnclosingRequestBase) httpReq).setEntity(reqEntity);

        httpRes = sendRequestPRO(httpReq, USER_BASE_URL);
        try {
            assertValidResponseCode(expectedStatsCode, httpRes.getStatusLine().getStatusCode());
        } finally {
            getResponseBody(httpRes.getEntity());
        }

    }
    
    public List<WSUser> getRESTUsers(String username) throws Exception {
        return getRESTUsers("/" + username, HttpStatus.SC_OK);
    }
    
    public void verifyUser(WSUser user_to_check, boolean expected_exists) throws Exception {
        List<WSUser> wsUsers = getRESTUsers(user_to_check.getUsername());

        boolean actual_exists = false;
        String user_to_check_id = (user_to_check.getTenantId() != null) ? user_to_check.getTenantId() : "";
        String wsUser_id;
        for (WSUser wsUser : wsUsers) {
            wsUser_id = (wsUser.getTenantId() != null) ? wsUser.getTenantId() : "";

            if (user_to_check.getUsername().equals(wsUser.getUsername())
                    && user_to_check.getPassword().equals(wsUser.getPassword())
                    && user_to_check_id.equals(wsUser_id)) {
                actual_exists = true;
            }
        }

        if (expected_exists) {
            Assert.assertEquals("Such user should exist, but it doesn't exist!", actual_exists, true);
        } else {
            Assert.assertEquals("Such user should not exist, but it was found!", actual_exists, false);
        }
    }
    
    public List<WSUser> getRESTUsers(String username, int expectedStatsCode) throws Exception {
        httpReq = new HttpGet();
        httpRes = sendRequestPRO(httpReq, USER_BASE_URL + username);
        assertValidResponseCode(expectedStatsCode, httpRes.getStatusLine().getStatusCode());

        InputStream getUsersResp = httpRes.getEntity().getContent();
        String usersRespString = IOUtils.toString(getUsersResp);

        List<WSUser> foundUsers = new ArrayList<WSUser>();

        String[] foundUsersParts = usersRespString.split("<user>");
        String foundUsersPart;

        for (int i = 1; i < foundUsersParts.length; i++) {
            foundUsersPart = "<user>" + foundUsersParts[i].replace("</users>", "");
            foundUsers.add(RESTUtils.unmarshal(WSUser.class, new ByteArrayInputStream(foundUsersPart.getBytes())));
        }

        return foundUsers;
    }
    
    public WSUser sampleUserDescriptor() {
        WSUser wsUser = new WSUser();
        wsUser.setEnabled(true);
        wsUser.setExternallyDefined(false);
        wsUser.setFullName("test");
        wsUser.setPassword("test");
        wsUser.setUsername("test");
        wsUser.setTenantId(DEFAULT_ORGANIZATION);

        return wsUser;
    }
	
	public Tenant sampleOrganizationDescriptor() {
        Tenant tenant = new TenantImpl();
        tenant.setParentId(DEFAULT_ORGANIZATION);
        tenant.setId("REST_TEST_ORGANIZATION");
        tenant.setTenantName("REST_TEST_ORGANIZATION");
        tenant.setAlias("REST_TEST_ORGANIZATION");
        tenant.setTheme("default");
        return tenant;
    }
	
	public RoleImpl convertWSRole(WSRole wsRole) {
        RoleImpl roleToReturn = new RoleImpl();
        roleToReturn.setRoleName(wsRole.getRoleName());
        roleToReturn.setTenantId(wsRole.getTenantId());
        roleToReturn.setExternallyDefined(wsRole.getExternallyDefined());

        return roleToReturn;
    }
	
	public void createRESTRole(WSRole role, int expectedStatsCode) throws Exception {

        httpReq = new HttpPut();

        StringWriter sw = new StringWriter();

        RESTUtils.getMarshaller(WSRole.class).marshal(role, sw);

        BasicHttpEntity reqEntity = new BasicHttpEntity();
        reqEntity.setContent(new ByteArrayInputStream(sw.toString().getBytes()));

        ((HttpEntityEnclosingRequestBase) httpReq).setEntity(reqEntity);

        httpRes = sendRequestPRO(httpReq, ROLE_BASE_URL);
        try {
            assertValidResponseCode(expectedStatsCode, httpRes.getStatusLine().getStatusCode());
        } finally {
            getResponseBody(httpRes.getEntity());
        }
    }
	
	public ObjectPermission getSampleObjectPermission(String url, Object recipient, int mask) {
        ObjectPermission o = new ObjectPermissionImpl();
        o.setPermissionMask(mask);
        o.setPermissionRecipient(recipient);
        o.setURI(url);

        return o;
	 }
	 
	 public void putRESTPermissions(JAXBList permission, int expectedStatsCode) throws Exception {
		 put_postRESTPermissions(permission, expectedStatsCode, new HttpPut());
	 }
	 
	 /**
	     * Put-Post Permissions
	     *
	     * @param permission        - permission Descriptor
	     * @param expectedStatsCode - HttpStatus
	     * @throws Exception - exception
	     */
	    public void put_postRESTPermissions(JAXBList permission, int expectedStatsCode, HttpRequestBase request) throws Exception {

	        httpReq = request;

	        StringWriter sw = new StringWriter();

	        RESTUtils.getMarshaller(JAXBList.class, ObjectPermissionImpl.class, UserImpl.class, RoleImpl.class)
	                .marshal(permission, sw);

	        BasicHttpEntity reqEntity = new BasicHttpEntity();
	        reqEntity.setContent(new ByteArrayInputStream(sw.toString().getBytes()));

	        ((HttpEntityEnclosingRequestBase) httpReq).setEntity(reqEntity);

	        httpRes = sendRequestPRO(httpReq, PERMISSION_BASE_URL);
	        try {
	            assertValidResponseCode(expectedStatsCode, httpRes.getStatusLine().getStatusCode());
	        } finally {
	            getResponseBody(httpRes.getEntity());
	        }
	    }
	    
	    public void deleteRESTRole(String rolename, int expectedStatsCode) throws Exception {
	        httpReq = new HttpDelete();
	        httpRes = sendRequestPRO(httpReq, ROLE_BASE_URL + rolename);
	        try {
	            assertValidResponseCode(expectedStatsCode, httpRes.getStatusLine().getStatusCode());
	        } finally {
	            getResponseBody(httpRes.getEntity());
	        }
	    }

	    public void deleteRESTRole(String rolename) throws Exception {
	        deleteRESTRole("/" + rolename, HttpStatus.SC_OK);
	    }

	    public void deleteRESTRole(WSRole wsRole) throws Exception {
	        deleteRESTRole(wsRole.getRoleName()
	                + ((wsRole.getTenantId() != null) ? PIPE + wsRole.getTenantId() : ""));
	    }

//	public String getJobIDOutOfSummery(String reports) {
//		String openIDTag = "<id>";
//		String closeIDTag = "</id>";
//		int s = reports.indexOf(openIDTag) + openIDTag.length();
//		int e = reports.indexOf(closeIDTag);
//		
//		return reports.substring(s, e);
//	}
//
//	public void putSampleUser() throws Exception{
//		tempHttpReq = new HttpPut();
//		
//		BasicHttpEntity reqEntity = new BasicHttpEntity();
//		
//		//appending the file descriptor from a file 
//		reqEntity.setContent(new FileInputStream(LOCAL_NEW_RDS + SAMPLE_USER_RD));
//		
//		((HttpEntityEnclosingRequestBase)httpReq).setEntity(reqEntity);
//		
//		
//		//executing the request
//		httpRes = sendRequestCE(tempHttpReq, SERVICE_USER);
//		Assert.assertTrue("basic response check did not pass", isValidResposnse());
//		
//	}
//
//	public void putSampleRole() throws Exception {
//		//building the body
//		BasicHttpEntity reqEntity = new BasicHttpEntity();
//		
//		//appending the file descriptor from a file 
//		reqEntity.setContent(new FileInputStream(LOCAL_NEW_RDS + SAMPLE_ROLE_RD));
//		
//		((HttpEntityEnclosingRequestBase)httpReq).setEntity(reqEntity);
//		
//		
//		//executing the request
//		httpRes = sendRequestCE(httpReq, SERVICE_ROLE);
//		Assert.assertTrue("basic response check did not pass", isValidResposnse());
//    }
}
