package com.jaspersoft.jasperserver.rest.test.helper;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.rest.test.RESTTest;
import com.jaspersoft.jasperserver.rest.test.RESTTestUtils;
import com.jaspersoft.jasperserver.rest.test.ReportDescriptor;
import com.jaspersoft.jasperserver.rest.test.RESTTestUtils.ReportOutputFormat;
import com.jaspersoft.jasperserver.ws.xml.Marshaller;
import com.jaspersoft.jasperserver.ws.xml.Unmarshaller;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.*;

import junit.framework.Assert;

public abstract class Common extends RESTTest {

    public static Log logger = LogFactory.getLog(Common.class);

    public static String getResponseBody(final HttpEntity entity) throws IOException, ParseException {

        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }

        InputStream instream = entity.getContent();

        return inputStreamToString(instream);
    }

    public static String inputStreamToString(InputStream instream) throws IOException, ParseException {

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

        if (logger.isInfoEnabled()) {
            Common.logger.info(buffer.toString());
        }
        return buffer.toString();
    }

    public static String getResourceDescriptorFromFile(File f) throws Exception {
        return IOUtils.toString(new FileInputStream(f));
    }

    public static ResourceDescriptor getResourceDescriptorFromResponse(HttpEntity entity) throws IOException, ParseException, SAXException, ParserConfigurationException {
        String respBody = getResponseBody(entity);
        Common.logger.info(respBody);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        Document doc = domFactory.newDocumentBuilder().parse(new InputSource(new StringReader(respBody)));
        return (ResourceDescriptor) Unmarshaller.unmarshal(ResourceDescriptor.class, doc.getDocumentElement());
    }

    public static HttpRequestBase assembleRequest(ResourceDescriptor rd, HttpRequestBase httpReq) {
        Marshaller m = new Marshaller();
        String rdXml = m.writeResourceDescriptor(rd);

        BasicHttpEntity reqEntity = new BasicHttpEntity();
        reqEntity.setContent(new ByteArrayInputStream(rdXml.getBytes()));
        ((HttpEntityEnclosingRequestBase) httpReq).setEntity(reqEntity);

        return httpReq;
    }


    /**
     * Reading and saving the response to file
     *
     * @param httpRes    - Http Response
     * @param fileName   - output file name
     * @param fileFormat - com.jaspersoft.qa.rest.helper.ReportOutputFormat
     * @throws Exception -
     */
    public static void saveReportToFile(HttpResponse httpRes, String fileName, ReportOutputFormat fileFormat) throws Exception {

        InputStream is = httpRes.getEntity().getContent();

        OutputStream os = new FileOutputStream(REST_RESULTS_FOLDER + "/"
                + fileName + "." + fileFormat);
        try {
            byte[] buffer = new byte[4096];
            for (int n; (n = is.read(buffer)) != -1; ) {
                os.write(buffer, 0, n);
            }
        } finally {
            os.close();
        }

    }

    /**
     * Reading and saving the response to file
     *
     * @param httpRes  Http Response
     * @param fileName - output File name
     * @param fileExt  - File extension
     * @throws Exception -
     */
    public static void saveResourceToFile(HttpResponse httpRes, String fileName, String fileExt) throws Exception {

        saveResourceToFile(httpRes, fileName, fileExt, REST_RESULTS_FOLDER);

    }

    /**
     * Reading and saving the response to file
     *
     * @param httpRes  Http Response
     * @param fileName - output File name
     * @param fileExt  - File extension
     * @param path     - relative path to the file, e.g.: /images
     * @throws Exception -
     */
    public static void saveResourceToFile(HttpResponse httpRes, String fileName, String fileExt, String path) throws Exception {

        InputStream is = httpRes.getEntity().getContent();

        OutputStream os = new FileOutputStream(path + "/"
                + fileName + (fileExt==null || fileExt.equals("") ? "" : "." + fileExt));
        try {
            byte[] buffer = new byte[4096];
            for (int n; (n = is.read(buffer)) != -1; ) {
                os.write(buffer, 0, n);
            }
        } finally {
            os.close();
        }
    }

    public static String getTagValue(String jobSummaryXML, String tagName) {
        int jobIDStartIndex = jobSummaryXML.indexOf("<" + tagName + ">") + ("<" + tagName + ">").length();
        int jobIDEndIndex = jobSummaryXML.indexOf("</" + tagName + ">");

        return jobSummaryXML.substring(jobIDStartIndex, jobIDEndIndex);
    }


    /**
     * Use to clean connection Streams
     *
     * @throws IOException -
     */
    public void cleanConnections() throws IOException {
        if (httpRes != null) {
            try {
                httpRes.getEntity().getContent().close();
            } catch (IllegalStateException ex) {
                //nothing to do, the response is empty
            }
        }
    }


    public static void notImplementedHelperMethod() {
        fail("This helper method: \"" + "\" is NOT implemented yet");
    }

    public static void fail(String message) {
        Assert.fail(message);
    }
}
