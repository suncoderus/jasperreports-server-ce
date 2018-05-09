package com.jaspersoft.jasperserver.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.InputSource;

//TODO: change the file name to RESTClientTest

public class RESTClient extends TestCase
{
	private static String USERNAME_PARAM = "j_username";
	private static String PASS_PARAM = "j_password";
	private static String ADMIN_USER_NAME = "jasperadmin";
	private static String ADMIN_PASS = "jasperadmin";
	private static String PROTOCOL = "http";
	private static String BASE_REST_URL = "localhost:8080/jasperserver/rest";
	private static String XPATH_OPERATION_RETURN_CODE = "/operationResult/returnCode";
	private static String LOGIN_CALL = "login";

	private static int RETURN_CODE_OK = 0;
	private static final int JR_LOGO_FILE_SIZE = 1491;

	private final Log log = LogFactory.getLog(getClass());

	private HttpGet httpGet;
	private HttpClient httpClient;
	private	CookieStore cookieStore;
	private HttpContext httpContext;

	private HttpResponse httpResponse;
	private XPath xpath;



    protected void setUp() {
    	xpath = XPathFactory.newInstance().newXPath();
    	httpClient = new DefaultHttpClient();
    	cookieStore = new BasicCookieStore();
    	httpContext = new BasicHttpContext();
    	httpGet = new HttpGet();
    	httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

    	//create login call to get credentials
    	loginToServer();
     }

    public void testlistRoot() throws Exception{
    	//building the request parameters
    	List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    	qparams.add(new BasicNameValuePair("uri", "/"));

    	httpResponse = executeCall("list", qparams);

    	assertTrue("basic response check did not pass", isValidResposnse(httpResponse));
    }

    /**
     * tests the list REST call NOTE: if case there is no sessionID a login call will be made
     * @throws Exception
     */
    public void testGet() throws Exception{
    	//building the request parameters
    	List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    	qparams.add(new BasicNameValuePair("uri", "/images/JRLogo"));

    	//executing the request
    	httpResponse = executeCall("get", qparams);


    	assertTrue("basic response check did not pass", isValidResposnse(httpResponse));
    }

    public void testFile() throws Exception{
    	testGet();// to add the resource to the session

    	//building the request parameters
    	List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    	qparams.add(new BasicNameValuePair("uri", "/images/JRLogo"));
    	qparams.add(new BasicNameValuePair("id", "attachment"));

    	//executing the request
    	httpResponse = executeCall("file", qparams);


    	assertTrue("basic response check did not pass", isValidFileResposnse(httpResponse));

    }

    private boolean isValidFileResposnse(HttpResponse httpResponse2) {
		// TODO Auto-generated method stub
		if (httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			//making sure that the file is being returned.
			return Integer.parseInt(httpResponse.getFirstHeader("Content-Length").getValue())==(JR_LOGO_FILE_SIZE);
		}
		return false;


	}

	protected void tearDown() throws Exception{
    	//releasing the related streams
    	if (httpResponse.getEntity().getContent().available()!=0){
    		httpResponse.getEntity().getContent();
    	}
    	httpClient.getConnectionManager().closeExpiredConnections();

	}

    private void loginToServer() {
    	//building the request parameters
    	List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    	qparams.add(new BasicNameValuePair(USERNAME_PARAM, ADMIN_USER_NAME));
    	qparams.add(new BasicNameValuePair(PASS_PARAM, ADMIN_PASS));

    	try {
			executeCall(LOGIN_CALL, qparams);
			//consuming the content to close the stream
			IOUtils.toString(httpResponse.getEntity().getContent());


    	} catch (Exception e) {

			e.printStackTrace();
		}

	}

    private HttpResponse executeCall(String action, List<NameValuePair> qparams) throws Exception
    {
    	URI uri= URIUtils.createURI(PROTOCOL, BASE_REST_URL, -1, "/"+action, URLEncodedUtils.format(qparams, "UTF-8"), null);
    	httpGet.setURI(uri);

    	httpResponse = httpClient.execute(httpGet, httpContext);

    	return httpResponse;
    }

    /**
     * @param httpResponse
     * @return true if the HTTP status code is 200 and if the REST set 0 as operation code. does not check the content.
     * @throws Exception
     */
    private boolean isValidResposnse(HttpResponse httpResponse) throws Exception
    {
    	if (httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
		{
			//getting the operation result return code
			InputSource is = new InputSource(httpResponse.getEntity().getContent());
			Double returnCode = (Double) xpath.evaluate(XPATH_OPERATION_RETURN_CODE, is, XPathConstants.NUMBER);

			return returnCode.intValue()==RETURN_CODE_OK;
		}

    	return false;
    }




//    private Document getDom(HttpEntity entity) throws Exception
//    {
//    	encoding = httpResponse.getEntity().getContentEncoding() == null ? "UTF-8" : httpResponse.getEntity().getContentEncoding().toString();
//    	String body = IOUtils.toString(httpResponse.getEntity().getContent(),encoding);
//
//    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//    	InputSource source = new InputSource( body );
//    	return factory.newDocumentBuilder().parse(source);
//    }


}