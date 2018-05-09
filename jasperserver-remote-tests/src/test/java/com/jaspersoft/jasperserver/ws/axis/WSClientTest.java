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

package com.jaspersoft.jasperserver.ws.axis;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.FileDataSource;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.axis.AxisFault;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.InputControlQueryDataRow;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ListItem;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.irplugin.JServer;
import com.jaspersoft.jasperserver.irplugin.wsclient.FileContent;
import com.jaspersoft.jasperserver.irplugin.wsclient.RequestAttachment;
import com.jaspersoft.jasperserver.irplugin.wsclient.WSClient;
import com.jaspersoft.jasperserver.war.JasperServerConstants;

/**
 *
 * @author gtoffoli
 */
public class WSClientTest extends TestCase {

	private static final String FIELD_USERNAME = "username";
	private static final String FIELD_PASSWORD = "password";
	private String targetUrl = JasperServerConstants.instance().WS_END_POINT_URL;
	private static String reportUnitURI = "/reports/samples/AllAccounts";

	private static String newRUURI = "/reports/testRU";

	private static String reportUnitDataSourceURI = "/datasources/JServerJdbcDS";

        private static WSClient wsclnt = null;
	private static JServer jserver = null;

	private boolean errorAllowed = false;

        /**
	 * default constructor
	 */
	public WSClientTest(String method) {
		super(method);
	}
	public WSClientTest(String method, boolean errorAllowed) {
		super(method);
		this.errorAllowed = errorAllowed;
	}

	/*
	 * setUp method
	 */
	public void setUp() throws Exception, AxisFault {

		if (wsclnt == null) {
			jserver = new JServer();
			jserver.setUrl(targetUrl);
			jserver.setUsername(JasperServerConstants.instance().USERNAME);
			jserver.setPassword(JasperServerConstants.instance().PASSWORD);
			wsclnt = new WSClient(jserver);
		}
	}

	/*
	 * tearDown method
	 */
	public void tearDown() {
		//Dont tear down the logged in client
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



		//TestCase test1 = new WSClientTest("testValidLogin");
  		TestCase testInValidLogin = new WSClientTest("testInValidLogin", true);
  		TestCase testListDataSources = new WSClientTest("testListDataSources");
  		TestCase testListRoot = new WSClientTest("testListRoot");
  		TestCase testGet = new WSClientTest("testGet");
  		TestCase testPut = new WSClientTest("testPut");
  		TestCase testDelete = new WSClientTest("testDelete");
  		TestCase testDeleteWithPossError = new WSClientTest("testDelete", false);
  		TestCase testTraffic = new WSClientTest("testTraffic");
  		TestCase testGetJasperPrint = new WSClientTest("testGetJasperPrint");
  		TestCase testGetJasperPrintWithParameters = new WSClientTest("testGetJasperPrintWithParameters");
  		TestCase testGetJasperPrintWithInvalidParameters = new WSClientTest("testGetJasperPrintWithInvalidParameters");
  		TestCase testGetHTML = new WSClientTest("testGetHTML");
  		TestCase testGetPDF = new WSClientTest("testGetPDF");
  		TestCase testPutRU = new WSClientTest("testPutRU");
  		TestCase testRunRU = new WSClientTest("testRunRU");
  		TestCase testCIC = new WSClientTest("testCascadingInputControl");


                suite.addTest(testListRoot);
  		suite.addTest(testListDataSources);
  		suite.addTest(testGet);
  		suite.addTest(testDeleteWithPossError);

                suite.addTest(testPut);
  		suite.addTest(testDelete);

                //suite.addTest(testTraffic);
  		suite.addTest(testGetJasperPrint);

                suite.addTest(testGetJasperPrintWithParameters);
  		suite.addTest(testGetJasperPrintWithInvalidParameters);

  		suite.addTest(testGetHTML);
                suite.addTest(testGetPDF);


  		suite.addTest(testPutRU);
                suite.addTest(testRunRU);
                suite.addTest(testCIC);

                suite.addTest(testInValidLogin);



                suite.addTest(new WSClientTest("testPutPdfResource"));
                suite.addTest(new WSClientTest("testGetPdfResource"));
                suite.addTest(new WSClientTest("testDeletePdfResource"));

                suite.addTest(new WSClientTest("testPutHtmlResource"));
                suite.addTest(new WSClientTest("testGetHtmlResource"));
                suite.addTest(new WSClientTest("testDeleteHtmlResource"));

                suite.addTest(new WSClientTest("testMoveResource"));
                suite.addTest(new WSClientTest("testMoveFolder"));
                suite.addTest(new WSClientTest("testCopyResource"));
                suite.addTest(new WSClientTest("testCopyFolder"));

                suite.addTest(new WSClientTest("testListReportUnitsUnderFolder"));
                suite.addTest(new WSClientTest("testListReportUnitsInFolder"));
                suite.addTest(new WSClientTest("testListImages"));
                suite.addTest(new WSClientTest("testListJars"));

                return suite;
        }

        /**
	 * testInValidLogin - method
	 */
	public void testInValidLogin() {

		System.out.println("testInValidLogin");
		try {
			JServer invalidJserver = new JServer();
			invalidJserver.setUrl(targetUrl);
			invalidJserver.setUsername(JasperServerConstants.instance().USERNAME2);
			invalidJserver.setPassword(JasperServerConstants.instance().BAD_PASSWORD2);
			WSClient invalidWsclnt = new WSClient(invalidJserver);
			invalidWsclnt.getVersion();
                        fail("No exception thrown at invalid Login");
                } catch (AxisFault ex)
                {
                    boolean isError = true;
                    if (errorAllowed) {
                            if (ex.getFaultString().indexOf("401") >= 0) {
                                    isError = false;
                            }
                    }
                    if (isError) {
                            fail("Unexpected error");
                    }
		} catch (Exception _ex) {
		    fail("Unexpected error");
		}

                System.out.println("end testInValidLogin");

	}

	/*
	 * testListDataSources method
	 * @param args
	 * @return
	 */
	public void testListDataSources() throws Exception {
		System.out.println("testListDataSources");

		List lst = wsclnt.listDatasources();
		assertNotNull("Datasources is null", lst);
		assertTrue("No datasources returned", lst.size()>0);
		System.out.println("end testListDataSources");

	}

	/*
	 * testListRoot method
	 *
	 * @return
	 */
	public void testListRoot() throws Exception {
		System.out.println("testListRoot");


		ResourceDescriptor rd = new ResourceDescriptor();
                rd.setWsType( ResourceDescriptor.TYPE_FOLDER );
                rd.setUriString("/");
		List lst = wsclnt.list(rd);
		assertNotNull("Null LIST returned", lst);
		assertFalse("List has no valid content", lst.isEmpty());

		System.out.println("end testListRoot");
	}

	/*
	 * testGet method
	 * @param args
	 * @return
	 */
	public void testGet() throws Exception {
		System.out.println("testGet");

		String imgUri = "/images/JRLogo";
		ResourceDescriptor rdis = new ResourceDescriptor();
		rdis.setParentFolder("/images");
		rdis.setUriString(imgUri);

		ResourceDescriptor result = wsclnt.get(rdis, null);
		assertNotNull("Object returned is NULL", result);
		String imgName = result.getName();
		assertNotNull("Image Name is NULL", imgName);
		System.out.println("end testGet");

	}

	/*
	 * testPut method
	 * @param args
	 * @return
	 */
	public void testPut() throws Exception {
		System.out.println("testPut");

		ResourceDescriptor rdis = new ResourceDescriptor();
		rdis.setResourceType(ResourceDescriptor.TYPE_IMAGE);
		rdis.setName("testImageName");
		rdis.setLabel("TestImageLabel");
		rdis.setDescription("Test Image Description");
		rdis.setParentFolder("/images");

		rdis.setUriString(rdis.getParentFolder() + "/" + rdis.getName());
		rdis.setWsType(ResourceDescriptor.TYPE_IMAGE);
		File img = getResourceAsFile("logo.jpg");
		rdis.setHasData(true);
		rdis.setIsNew(true);
		ResourceDescriptor result = wsclnt.addOrModifyResource(rdis, img);
		assertNotNull("Upload image is Un-Successful", result);
		System.out.println("end testPut");

	}

	/*
	 * testDelete method
	 * @param args
	 * @return
	 */
	public void testDelete() {
		System.out.println("testDelete");

		try {
    	   String imgUri = "/images/testImageName";
           ResourceDescriptor rdes = uriDescriptor(imgUri);
           wsclnt.delete(rdes);
        } catch (Exception ex) {
        	boolean isError = true;
        	if (errorAllowed) {
        		if (ex.getMessage().indexOf("Resource not found") >= 0) {
        			isError = false;
        		}
        	}
        	if (isError) {
        		fail("Image Delete Unsuccessful");
        	}
        }
		System.out.println("end testDelete");
	}

	public void testTraffic() throws Exception {
		System.out.println("testTraffic");

		for (int i = 0; i < 10; i++) {
			testGet();
			Thread.sleep(1000);
			testPut();
			Thread.sleep(1000);
			testDelete();
			Thread.sleep(1000);
		}
		System.out.println("end testTraffic");

	}

	public void testGetJasperPrint() throws Exception {
		System.out.println("testGetJasperPrint");

       ResourceDescriptor rdes = new ResourceDescriptor();
       rdes.setUriString(reportUnitURI);
       JasperPrint js = wsclnt.runReport(rdes, new HashMap());
       assertNotNull("No JasperPrint", js);
	   System.out.println("end testGetJasperPrint");
	}



	public void testGetJasperPrintWithParameters() throws Exception {
		System.out.println("testGetJasperPrintWithParameters");

 	   String imgUri = "/reports/samples/SalesByMonth";
       ResourceDescriptor rdes = uriDescriptor(imgUri);

       Map args = new HashMap();
       args.put("TextInputControl", "5");


       JasperPrint js = wsclnt.runReport(rdes, args);

       assertNotNull("No JasperPrint", js);
	   System.out.println("end testGetJasperPrintWithParameters");
	}

	public void testGetJasperPrintWithInvalidParameters() throws Exception {
		System.out.println("testGetJasperPrintWithInvalidParameters");

 	   String imgUri = "/reports/samples/SalesByMonth";
       ResourceDescriptor rdes = uriDescriptor(imgUri);

       Map args = new HashMap();
       args.put("TextInputControl", "BAD-HAS TO BE A NUMBER");


       JasperPrint js = wsclnt.runReport(rdes, args);

       assertNotNull("No JasperPrint", js);
	   System.out.println("end testGetJasperPrintWithInvalidParameters");
	}








	/*
	 * Disabled: only gets one attachment back, when it is sent 4!
	 */
	public void testGetHTML() throws Exception {
		System.out.println("testGetHTML");

       ResourceDescriptor rdes = new ResourceDescriptor();
       rdes.setUriString(reportUnitURI);

       List args = new ArrayList();
   	   args.add(new Argument(Argument.RUN_OUTPUT_FORMAT, Argument.RUN_OUTPUT_FORMAT_HTML));

       Map attachments = wsclnt.runReport(rdes, new HashMap(), args);

       assertNotNull("No result", attachments);

       Iterator it = attachments.entrySet().iterator();
       List mimeTypes = new java.util.ArrayList();

       FileContent content = null;
       while (it.hasNext()) {
    	   Map.Entry entry = (Map.Entry) it.next();
    	   content = (FileContent)entry.getValue();
    	   System.out.println("Got " + entry.getKey() + " " + content.getName() +" " + content.getMimeType());
    	   mimeTypes.add(content.getMimeType());
    	}


	if (attachments != null && !attachments.isEmpty()) {
		   content = (FileContent) attachments.get("report");
	}

        assertNotNull("No report", content);
        assertTrue("not html. was: " + content.getMimeType(), content.getMimeType().equals("text/html"));

	//content = (FileContent) attachments.get("px");
	//assertNotNull("No spacer image", content);
	//assertTrue("not gif. was: " + content.getMimeType(), content.getMimeType().equals("image/gif"));

	assertTrue("jpeg image not found: ", mimeTypes.contains("image/jpeg"));
	//assertTrue("gif image not found: ", mimeTypes.contains("image/gif"));

	System.out.println("end testGetHTML");
	}

	public void testGetPDF() throws Exception {
		System.out.println("testGetPDF");

       ResourceDescriptor rdes = new ResourceDescriptor();
       rdes.setUriString(reportUnitURI);

       List args = new ArrayList();
   	   args.add(new Argument(Argument.RUN_OUTPUT_FORMAT, Argument.RUN_OUTPUT_FORMAT_PDF));


       Map attachments = wsclnt.runReport(rdes, new HashMap(), args);

       assertNotNull("No result", attachments);

	   FileContent content = null;
           // get the first key....


	   if (attachments != null && !attachments.isEmpty()) {
                   Object key = attachments.keySet().iterator().next();
                   content = (FileContent) attachments.get(key);
	   }
       assertNotNull("No PDF report", content);

       assertTrue("not pdf. was: " + content.getMimeType(), content.getMimeType().equals("application/pdf"));

	   System.out.println("end testGetPDF");
	}

	/** Fetches the URL of the Files in the classpath
	 * @return file path
	 **/
	private File getResourceAsFile(String name) throws UnsupportedEncodingException{
		    return  new File(URLDecoder.decode(getClass().getClassLoader().getResource(name).getFile(), "UTF-8"));
    }

	/*
	 * testPutRU method
	 * @param args
	 * @return
	 */
	public void testPutRU() throws Exception {
		System.out.println("testPutRU");

		// Delete if exists:

		try {
    	           ResourceDescriptor rdes = new ResourceDescriptor();
                   rdes.setUriString(newRUURI);
                   wsclnt.delete(rdes);
                } catch (Exception ex) {
        	}



		ResourceDescriptor rd = new ResourceDescriptor();
     		File resourceFile = null;

        	rd.setWsType( ResourceDescriptor.TYPE_REPORTUNIT );
        	rd.setName("testRU");
		rd.setLabel("Test RU");
		rd.setDescription("Test RU Description");
		rd.setParentFolder("/reports");

        	rd.setUriString(newRUURI);
        	rd.setIsNew( true );

        	rd.setResourceProperty(ResourceDescriptor.PROP_RU_ALWAYS_PROPMT_CONTROLS, true);
        	rd.setResourceProperty(ResourceDescriptor.PROP_RU_CONTROLS_LAYOUT,
        			ResourceDescriptor.RU_CONTROLS_LAYOUT_TOP_OF_PAGE);

        	ResourceDescriptor tmpDataSourceDescriptor = new ResourceDescriptor();
                tmpDataSourceDescriptor.setWsType( ResourceDescriptor.TYPE_DATASOURCE );
                tmpDataSourceDescriptor.setReferenceUri( reportUnitDataSourceURI );
                tmpDataSourceDescriptor.setIsReference(true);
             	rd.getChildren().add( tmpDataSourceDescriptor );

        	ResourceDescriptor jrxmlDescriptor = new ResourceDescriptor();
        	jrxmlDescriptor.setWsType( ResourceDescriptor.TYPE_JRXML );
        	jrxmlDescriptor.setName( "test_jrxml");
                jrxmlDescriptor.setLabel("Main jrxml"); //getResource().getDescriptor().getLabel()  );
                jrxmlDescriptor.setDescription("Main jrxml"); //getResource().getDescriptor().getDescription()
                jrxmlDescriptor.setIsNew(true);
                jrxmlDescriptor.setHasData(true);
                jrxmlDescriptor.setMainReport(true);

                resourceFile = getResourceAsFile("test.jrxml");
                System.out.println(resourceFile + " " + resourceFile.length());

                assertTrue("Test jrxml file not found!", resourceFile.exists());

                rd.getChildren().add( jrxmlDescriptor );

                ResourceDescriptor result = wsclnt.addOrModifyResource(rd, resourceFile);
        	assertNotNull("Object returned is NULL", result);
		String ruName = result.getName();
		assertNotNull("RU Name is NULL", ruName);

		ResourceDescriptor uriDescriptor = new ResourceDescriptor();
		uriDescriptor.setUriString(newRUURI);
		ResourceDescriptor ru = wsclnt.get(uriDescriptor, null);
		assertNotNull(ru);
		assertEquals("testRU", ru.getName());
		Boolean alwaysPrompt = ru.getResourcePropertyValueAsBoolean(
				ResourceDescriptor.PROP_RU_ALWAYS_PROPMT_CONTROLS);
		assertNotNull(alwaysPrompt);
		assertTrue(alwaysPrompt.booleanValue());
		Integer controlsLayout = ru.getResourcePropertyValueAsInteger(
				ResourceDescriptor.PROP_RU_CONTROLS_LAYOUT);
		assertNotNull(controlsLayout);
		assertEquals(ResourceDescriptor.RU_CONTROLS_LAYOUT_TOP_OF_PAGE, controlsLayout.byteValue());

		System.out.println("end testPutRU");

        }


        /*
	 * testRunRU method
	 * @param args
	 * @return
	 */
        public void testRunRU() throws Exception {
		System.out.println("testRunRU");

	       ResourceDescriptor rdes = new ResourceDescriptor();
	       rdes.setUriString(newRUURI);

	       List args = new ArrayList();
	   	   args.add(new Argument(Argument.RUN_OUTPUT_FORMAT, Argument.RUN_OUTPUT_FORMAT_PDF));


	       HashMap params = new HashMap();
	       params.put("TEST_PARAMETER","test string");

	       Map attachments = wsclnt.runReport(rdes, params, args);

	       assertNotNull("No result", attachments);

	       FileContent content = null;
	           // get the first key....


	       if (attachments != null && !attachments.isEmpty()) {
	                   Object key = attachments.keySet().iterator().next();
	                   content = (FileContent) attachments.get(key);

	       }
	       assertNotNull("No PDF report", content);
	       assertTrue("not pdf. was: " + content.getMimeType(), content.getMimeType().equals("application/pdf"));

	       File f = new File(content.getName());
	       /*
               File f2 = new File("./test.pdf");
               if (f2.exists()) {
           	  f2.delete();
               }
               f.renameTo(f2);
               */
	       System.out.println("File created: " + f);

	   System.out.println("end testRunRU");
	}

	public void testPutPdfResource() throws Exception {
		System.out.println("testPutPdfResource");

		ResourceDescriptor resource = new ResourceDescriptor();
		resource.setWsType(ResourceDescriptor.TYPE_CONTENT_RESOURCE);
		resource.setIsNew(true);
		resource.setName("Test_Report_Pdf");
		resource.setLabel("PDF file");
		resource.setParentFolder("/ContentFiles/pdf");
		resource.setUriString("/ContentFiles/pdf/Test_Report_Pdf");
		resource.setResourceProperty(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE,
				ResourceDescriptor.CONTENT_TYPE_PDF);

		resource.setHasData(true);
		FileDataSource fileDataSource = new FileDataSource(getResourceAsFile("report.pdf"));
		RequestAttachment attachment = new RequestAttachment(fileDataSource, "Test_Report_Pdf");
		wsclnt.putResource(resource, new RequestAttachment[]{attachment});

		System.out.println("end testPutPdfResource");
	}

	public void testGetPdfResource() throws Exception {
		System.out.println("testGetPdfResource");

		File tmpFile = File.createTempFile("jasperserver", "");
		tmpFile.deleteOnExit();

		ResourceDescriptor uriDescriptor = new ResourceDescriptor();
		uriDescriptor.setUriString("/ContentFiles/pdf/Test_Report_Pdf");
		ResourceDescriptor resource = wsclnt.get(uriDescriptor, tmpFile);

		assertEquals(ResourceDescriptor.TYPE_CONTENT_RESOURCE, resource.getWsType());
		assertEquals("/ContentFiles/pdf/Test_Report_Pdf", resource.getUriString());
		assertEquals(ResourceDescriptor.CONTENT_TYPE_PDF,
				resource.getResourcePropertyValue(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE));

		assertTrue(tmpFile.exists());

		byte[] originalData = readFile(getResourceAsFile("report.pdf"));
		byte[] returnedData = readFile(tmpFile);
		assertTrue(Arrays.equals(originalData, returnedData));

		System.out.println("end testGetPdfResource");
	}

	public void testDeletePdfResource() throws Exception {
		System.out.println("testDeletePdfResource");

		ResourceDescriptor uriDescriptor = new ResourceDescriptor();
		uriDescriptor.setUriString("/ContentFiles/pdf/Test_Report_Pdf");
		wsclnt.delete(uriDescriptor);

		System.out.println("end testDeletePdfResource");
	}

	public void testPutHtmlResource() throws Exception {
		System.out.println("testPutHtmlResource");

		ResourceDescriptor resource = new ResourceDescriptor();
		resource.setWsType(ResourceDescriptor.TYPE_CONTENT_RESOURCE);
		resource.setIsNew(true);
		resource.setName("HtmlResourceTest");
		resource.setLabel("HTML file");
		resource.setParentFolder("/ContentFiles/html");
		resource.setUriString("/ContentFiles/html/HtmlResourceTest");
		resource.setResourceProperty(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE,
				ResourceDescriptor.CONTENT_TYPE_HTML);
		resource.setHasData(true);

		ResourceDescriptor image = new ResourceDescriptor();
		image.setWsType(ResourceDescriptor.TYPE_CONTENT_RESOURCE);
		image.setIsNew(true);
		image.setName("image0");
		image.setLabel("Image");
		image.setResourceProperty(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE,
				ResourceDescriptor.CONTENT_TYPE_IMAGE);
		image.setHasData(true);

		List children = new ArrayList(1);
		children.add(image);
		resource.setChildren(children);

		FileDataSource htmlDataSource = new FileDataSource(getResourceAsFile("HtmlResourceTest.html"));
		RequestAttachment htmlAttachment = new RequestAttachment(htmlDataSource, "HtmlResourceTest");

		FileDataSource imageDataSource = new FileDataSource(getResourceAsFile("HtmlResourceTest_files/image0"));
		RequestAttachment imageAttachment = new RequestAttachment(imageDataSource, "image0");

		wsclnt.putResource(resource, new RequestAttachment[]{htmlAttachment, imageAttachment});

		System.out.println("end testPutHtmlResource");
	}

	public void testGetHtmlResource() throws Exception {
		System.out.println("testGetHtmlResource");

		File htmlTmpFile = File.createTempFile("jasperserver", "");
		htmlTmpFile.deleteOnExit();

		ResourceDescriptor htmlURIDescriptor = new ResourceDescriptor();
		htmlURIDescriptor.setUriString("/ContentFiles/html/HtmlResourceTest");

        List args = new ArrayList(1);
        args.add(new Argument(Argument.NO_SUBRESOURCE_DATA_ATTACHMENTS, null));
		ResourceDescriptor htmlResource = wsclnt.get(htmlURIDescriptor, htmlTmpFile, args);

		assertEquals(ResourceDescriptor.TYPE_CONTENT_RESOURCE, htmlResource.getWsType());
		assertEquals("/ContentFiles/html/HtmlResourceTest", htmlResource.getUriString());
		assertEquals(ResourceDescriptor.CONTENT_TYPE_HTML,
				htmlResource.getResourcePropertyValue(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE));

		assertTrue(htmlTmpFile.exists());

		byte[] originalHtmlData = readFile(getResourceAsFile("HtmlResourceTest.html"));
		byte[] returnedHtmlData = readFile(htmlTmpFile);
		assertTrue(Arrays.equals(originalHtmlData, returnedHtmlData));

		File imageTmpFile = File.createTempFile("jasperserver", "");
		imageTmpFile.deleteOnExit();

		ResourceDescriptor imageURIDescriptor = new ResourceDescriptor();
		imageURIDescriptor.setUriString("/ContentFiles/html/HtmlResourceTest_files/image0");

		ResourceDescriptor imageResource = wsclnt.get(imageURIDescriptor, imageTmpFile);

		assertEquals(ResourceDescriptor.TYPE_CONTENT_RESOURCE, imageResource.getWsType());
		assertEquals("/ContentFiles/html/HtmlResourceTest_files/image0", imageResource.getUriString());
		assertEquals(ResourceDescriptor.CONTENT_TYPE_IMAGE,
				imageResource.getResourcePropertyValue(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE));

		assertTrue(imageTmpFile.exists());

		byte[] originalImageData = readFile(getResourceAsFile("HtmlResourceTest_files/image0"));
		byte[] returnedImageData = readFile(imageTmpFile);
		assertTrue(Arrays.equals(originalImageData, returnedImageData));

		System.out.println("end testGetHtmlResource");
	}

	public void testDeleteHtmlResource() throws Exception {
		System.out.println("testDeleteHtmlResource");

		ResourceDescriptor uriDescriptor = new ResourceDescriptor();
		uriDescriptor.setUriString("/ContentFiles/html/HtmlResourceTest");
		wsclnt.delete(uriDescriptor);

		System.out.println("end testDeleteHtmlResource");
	}

	protected byte[] readFile(File file) throws IOException {
		int length = (int) file.length();
		byte[] data = new byte[length];

		FileInputStream fileInput = new FileInputStream(file);
		try {
			int read = 0;
			do {
				read = fileInput.read(data, read, length - read);
			} while (read > 0);
			return data;
		} finally {
			try {
				fileInput.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	protected void delete(String uri) throws Exception {
        ResourceDescriptor rdes = uriDescriptor(uri);
        wsclnt.delete(rdes);
	}
	protected ResourceDescriptor uriDescriptor(String uri) {
		ResourceDescriptor rdes = new ResourceDescriptor();
        rdes.setUriString(uri);
		return rdes;
	}

	public void testMoveResource() throws Exception {
		ResourceDescriptor res = new ResourceDescriptor();
		res.setWsType(ResourceDescriptor.TYPE_DATASOURCE_JNDI);
		res.setResourceType(ResourceDescriptor.TYPE_DATASOURCE_JNDI);
		res.setName("testJndiDS");
		res.setLabel("testJndiDS");
		res.setParentFolder("/datasources");
		res.setResourceProperty(ResourceDescriptor.PROP_DATASOURCE_JNDI_NAME, "jdbc/testDS");
		String oldURI = res.getParentFolder() + "/" + res.getName();
		res.setUriString(oldURI);
		res.setIsNew(true);

		wsclnt.addOrModifyResource(res, null);
		boolean deleteOld = true;
		boolean deleteNew = false;
		String newURI = "/images/" + res.getName();
		try {
			wsclnt.move(res, "/images");
			deleteOld = false;
			deleteNew = true;

			assertNotExisting(oldURI);
			ResourceDescriptor movedRes = assertExisting(newURI, ResourceDescriptor.TYPE_DATASOURCE_JNDI);
			assertEquals("jdbc/testDS", movedRes.getResourcePropertyValue(ResourceDescriptor.PROP_DATASOURCE_JNDI_NAME));

			deleteNew = false;
			delete(newURI);
		} finally {
			if (deleteOld) {
				try {
					delete(oldURI);
				} catch (Exception e) {
					System.err.println("Unable to delete test resource");
					e.printStackTrace();
				}
			}

			if (deleteNew) {
				try {
					delete(newURI);
				} catch (Exception e) {
					System.err.println("Unable to delete test resource");
					e.printStackTrace();
				}
			}
		}
	}

	public void testMoveFolder() throws Exception {
		ResourceDescriptor folder = new ResourceDescriptor();
		folder.setWsType(ResourceDescriptor.TYPE_FOLDER);
		folder.setResourceType(ResourceDescriptor.TYPE_FOLDER);
		folder.setName("testWSFolder");
		folder.setLabel("testWSFolder");
		folder.setParentFolder("/");
		String oldURI = "/" + folder.getName();
		folder.setUriString(oldURI);
		folder.setIsNew(true);

		wsclnt.addOrModifyResource(folder, null);
		boolean deleteOld = true;
		boolean deleteNew = false;
		String newURI = "/images/" + folder.getName();
		try {
			wsclnt.move(folder, "/images");
			deleteOld = false;
			deleteNew = true;

			assertNotExisting(oldURI);
			assertExisting(newURI, ResourceDescriptor.TYPE_FOLDER);

			deleteNew = false;
			delete(newURI);
		} finally {
			if (deleteOld) {
				try {
					delete(oldURI);
				} catch (Exception e) {
					System.err.println("Unable to delete test folder");
					e.printStackTrace();
				}
			}

			if (deleteNew) {
				try {
					delete(newURI);
				} catch (Exception e) {
					System.err.println("Unable to delete test folder");
					e.printStackTrace();
				}
			}
		}
	}

	public void testCopyResource() throws Exception {
		ResourceDescriptor res = new ResourceDescriptor();
		res.setWsType(ResourceDescriptor.TYPE_DATASOURCE_JNDI);
		res.setResourceType(ResourceDescriptor.TYPE_DATASOURCE_JNDI);
		res.setName("testJndiDS");
		res.setLabel("testJndiDS");
		res.setParentFolder("/datasources");
		res.setResourceProperty(ResourceDescriptor.PROP_DATASOURCE_JNDI_NAME, "jdbc/testDS");
		String oldURI = res.getParentFolder() + "/" + res.getName();
		res.setUriString(oldURI);
		res.setIsNew(true);

		wsclnt.addOrModifyResource(res, null);
		boolean deleteOld = true;
		boolean deleteNew = false;
		boolean deleteNew2 = false;
		String newURI = "/images/testJndiDSCopy";
		String new2URI = "/images/testJndiDSCopy_1";
		try {
			ResourceDescriptor copyDescriptor = wsclnt.copy(res, newURI);
			deleteNew = true;
			assertNotNull(copyDescriptor);
			assertEquals(newURI, copyDescriptor.getUriString());
			assertEquals("jdbc/testDS", copyDescriptor.getResourcePropertyValue(
					ResourceDescriptor.PROP_DATASOURCE_JNDI_NAME));
			assertEquals(ResourceDescriptor.TYPE_DATASOURCE_JNDI, copyDescriptor.getWsType());

			assertExisting(oldURI, ResourceDescriptor.TYPE_DATASOURCE_JNDI);
			ResourceDescriptor movedRes = assertExisting(newURI, ResourceDescriptor.TYPE_DATASOURCE_JNDI);
			assertEquals("jdbc/testDS", movedRes.getResourcePropertyValue(ResourceDescriptor.PROP_DATASOURCE_JNDI_NAME));

			ResourceDescriptor copy2Descriptor = wsclnt.copy(res, newURI);
			deleteNew2 = true;
			assertNotNull(copy2Descriptor);
			assertEquals(new2URI, copy2Descriptor.getUriString());
			assertEquals("jdbc/testDS", copy2Descriptor.getResourcePropertyValue(
					ResourceDescriptor.PROP_DATASOURCE_JNDI_NAME));
			assertEquals(ResourceDescriptor.TYPE_DATASOURCE_JNDI, copy2Descriptor.getWsType());

			deleteOld = false;
			delete(oldURI);

			deleteNew = false;
			delete(newURI);

			deleteNew2 = false;
			delete(new2URI);
		} finally {
			if (deleteOld) {
				try {
					delete(oldURI);
				} catch (Exception e) {
					System.err.println("Unable to delete test resource");
					e.printStackTrace();
				}
			}

			if (deleteNew) {
				try {
					delete(newURI);
				} catch (Exception e) {
					System.err.println("Unable to delete test resource");
					e.printStackTrace();
				}
			}

			if (deleteNew2) {
				try {
					delete(new2URI);
				} catch (Exception e) {
					System.err.println("Unable to delete test resource");
					e.printStackTrace();
				}
			}
		}
	}

	public void testCopyFolder() throws Exception {
		ResourceDescriptor folder = new ResourceDescriptor();
		folder.setWsType(ResourceDescriptor.TYPE_FOLDER);
		folder.setResourceType(ResourceDescriptor.TYPE_FOLDER);
		folder.setName("testWSFolder");
		folder.setLabel("testWSFolder");
		folder.setParentFolder("/images");
		String oldURI = "/images/" + folder.getName();
		folder.setUriString(oldURI);
		folder.setIsNew(true);

		wsclnt.addOrModifyResource(folder, null);
		boolean deleteOld = true;
		boolean deleteNew = false;
		String newURI = "/testWSFolderCopy";
		try {
			ResourceDescriptor copyDescriptor = wsclnt.copy(folder, newURI);
			deleteNew = true;
			assertNotNull(copyDescriptor);
			assertEquals(newURI, copyDescriptor.getUriString());
			assertEquals(ResourceDescriptor.TYPE_FOLDER, copyDescriptor.getWsType());

			assertExisting(oldURI, ResourceDescriptor.TYPE_FOLDER);
			assertExisting(newURI, ResourceDescriptor.TYPE_FOLDER);

			deleteOld = false;
			delete(oldURI);

			deleteNew = false;
			delete(newURI);
		} finally {
			if (deleteOld) {
				try {
					delete(oldURI);
				} catch (Exception e) {
					System.err.println("Unable to delete test folder");
					e.printStackTrace();
				}
			}

			if (deleteNew) {
				try {
					delete(newURI);
				} catch (Exception e) {
					System.err.println("Unable to delete test folder");
					e.printStackTrace();
				}
			}
		}
	}

	protected ResourceDescriptor assertExisting(String uri, String wsType) {
		try {
			ResourceDescriptor resource = wsclnt.get(uriDescriptor(uri), null);
			assertNotNull(resource);
			assertEquals(uri, resource.getUriString());
			assertEquals(wsType, resource.getWsType());
			return resource;
		} catch (Exception e) {
			assertTrue("URI " + uri + " doesn't exist", false);
			return null;
		}
	}

	protected void assertNotExisting(String uri) {
		try {
			wsclnt.get(uriDescriptor(uri), null);
			assertTrue("URI " + uri + " exists", false);
		} catch (Exception e) {
			// expected
		}
	}

	public void testListReportUnitsUnderFolder() throws Exception {
		System.out.println("testListReportUnitsUnderFolder");

		String parentFolder = "/reports";
		List reports = wsclnt.listResourcesUnderFolder(ResourceDescriptor.TYPE_REPORTUNIT, parentFolder);

		assertTrue(reports != null && !reports.isEmpty());

		for (Iterator it = reports.iterator(); it.hasNext();) {
			Object item = (Object) it.next();
			assertTrue(item instanceof ResourceDescriptor);
			ResourceDescriptor resource = (ResourceDescriptor) item;
			System.out.println("listed " + resource.getUriString());
			assertTrue(resource.getUriString().startsWith(parentFolder));
		}

		System.out.println("end testListReportUnitsUnderFolder");
	}

	public void testListReportUnitsInFolder() throws Exception {
		System.out.println("testListReportUnitsInFolder");

		String parentFolder = "/reports";
		List resources = wsclnt.listResourcesInFolder(ResourceDescriptor.TYPE_REPORTUNIT, parentFolder);

		if (resources != null) {
			for (Iterator it = resources.iterator(); it.hasNext();) {
				Object item = (Object) it.next();
				assertTrue(item instanceof ResourceDescriptor);
				ResourceDescriptor resource = (ResourceDescriptor) item;
				System.out.println("listed " + resource.getUriString());
				assertTrue(resource.getParentFolder().equals(parentFolder));
			}
		}

		System.out.println("end testListReportUnitsInFolder");
	}

	public void testListImages() throws Exception {
		System.out.println("testListImages");

		List resources = wsclnt.listResources(ResourceDescriptor.TYPE_IMAGE);

		assertTrue(resources != null && !resources.isEmpty());

		for (Iterator it = resources.iterator(); it.hasNext();) {
			Object item = (Object) it.next();
			assertTrue(item instanceof ResourceDescriptor);
			ResourceDescriptor resource = (ResourceDescriptor) item;
			System.out.println("listed " + resource.getUriString());
			assertEquals(ResourceDescriptor.TYPE_IMAGE, resource.getWsType());
		}

		System.out.println("end testListImages");
	}

	public void testListJars() throws Exception {
		System.out.println("testListJars");

		List resources = wsclnt.listResources(ResourceDescriptor.TYPE_CLASS_JAR);

		if (resources != null) {
			for (Iterator it = resources.iterator(); it.hasNext();) {
				Object item = (Object) it.next();
				assertTrue(item instanceof ResourceDescriptor);
				ResourceDescriptor resource = (ResourceDescriptor) item;
				System.out.println("listed " + resource.getUriString());
				assertEquals(ResourceDescriptor.TYPE_CLASS_JAR, resource.getWsType());
			}
		}

		System.out.println("end testListJars");
	}

	public void testCascadingInputControl() throws Exception {
		System.out.println("testCascadingInputControl");

		ResourceDescriptor rd = new ResourceDescriptor();
		rd.setUriString("/reports/samples/Cascading_multi_select_report_files/Cascading_state_multi_select");
		rd.setResourceProperty(rd.PROP_QUERY_DATA, null);
		ListItem li1 = new ListItem("Country_multi_select", "USA");
		li1.setIsListItem(true);
		rd.getParameters().add(li1);
		ListItem li2 = new ListItem("Country_multi_select", "Mexico");
		li2.setIsListItem(true);
		rd.getParameters().add(li2);
		java.util.List args = new java.util.ArrayList();
		args.add(new Argument( Argument.IC_GET_QUERY_DATA, ""));
		args.add(new Argument( Argument.RU_REF_URI, "/reports/samples/Cascading_multi_select_report"));
		ResourceDescriptor rd2 = wsclnt.get(rd, null, args);

		if (rd2.getQueryData() != null) {
		    List l = (List) rd2.getQueryData();
			for (Iterator it = l.iterator(); it.hasNext();) {
		        InputControlQueryDataRow icdr = (InputControlQueryDataRow) it.next();
		        for (Iterator it2 = icdr.getColumnValues().iterator(); it2.hasNext(); ) {
		            System.out.print(it2.next() + " | ");
		        }
		        System.out.println();
		    }
		}
	}
}
