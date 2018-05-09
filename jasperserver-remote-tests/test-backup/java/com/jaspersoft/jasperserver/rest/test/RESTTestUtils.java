package com.jaspersoft.jasperserver.rest.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.ws.xml.Marshaller;

import static com.jaspersoft.jasperserver.rest.test.RESTTestUtilsAndConsts.*;

public class RESTTestUtils {
	
	static final String LOG4J_PATH = "src/main/java/com/jaspersoft/jasperserver/rest/test/resources/log4j.properties";
	
	static String getResourceDescriptor(File f) throws Exception
    {
    	return IOUtils.toString(new FileInputStream(f));
    }
	
	
	
	public static void saveReportToFile(HttpResponse httpRes, String fileName, ReportOutputFormat fileFormat) throws Exception {

        InputStream is = httpRes.getEntity().getContent();

        OutputStream os = new FileOutputStream(REST_RESULTS_FOLDER + "/" + fileName + "." + fileFormat);
        try {
            byte[] buffer = new byte[4096];
            for (int n; (n = is.read(buffer)) != -1; ) {
                os.write(buffer, 0, n);
            }
        } finally {
            os.close();
        }

    }
	
	public static enum ReportOutputFormat {
        html("HTML"),
        pdf("PDF"),
        rtf("RTF"),
        xls("XLS"),
        csv("CSV"),
        xml("XML"),
        jrprint("JRPRINT");

        private String format;

        private ReportOutputFormat(String format) {
            this.format = format;
        }

        @Override
        public String toString() {
            return format;
        }

        public boolean equals(String format) {
            return (this.toString().equals(format));
        }

    }
	
	public static HttpRequestBase assembleRequest(ResourceDescriptor rd, HttpRequestBase httpReq) {
        Marshaller m = new Marshaller();
        String rdXml = m.writeResourceDescriptor(rd);

        BasicHttpEntity reqEntity = new BasicHttpEntity();
        reqEntity.setContent(new ByteArrayInputStream(rdXml.getBytes()));
        ((HttpEntityEnclosingRequestBase) httpReq).setEntity(reqEntity);

        return httpReq;
    }
	
	public static void assertCorrectFileSize(String fileName, Integer minimumSize) {
        File exportedFile = new File(fileName);

        if (minimumSize == null) {
            minimumSize = 0;
        }
        Assert.assertTrue(exportedFile.length() >= minimumSize);
    }

	public static void assertValidResponseCode(int expectedCode, int actualCode) throws Exception 
	{
		Assert.assertTrue(	"Basic response check did not pass; Expected code = " + expectedCode + "; Actual code = " + actualCode, 
							isValidResponse(expectedCode, actualCode));
	}
	
	public static boolean isValidResponse(int expected_Code, int actualCode) throws Exception {
        return actualCode == expected_Code;
    }

	public static void assertValid404ResponseCode(int actualCode) throws Exception {
		assertValidResponseCode(HttpStatus.SC_NOT_FOUND, actualCode);
	}
		
}
