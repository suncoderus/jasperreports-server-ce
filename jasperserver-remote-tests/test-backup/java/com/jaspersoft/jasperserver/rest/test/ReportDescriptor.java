package com.jaspersoft.jasperserver.rest.test;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

import static org.springframework.util.xml.DomUtils.getTextValue;

/**
 * Author: Sasha Stoykovich
 * Date: 8/23/2011
 */

public class ReportDescriptor extends RESTTest {

    public String uuid = null;
    public String originalUri = null;
    public String totalPages = null;
    public String startPage = null;
    public String endPage = null;
    /**
     * Array contains values in sequence: file_is, type, extension
     */
    public String[][] file_id = null;

    public static final String UUID_TAG_NAME = "uuid";
    public static final String TAG_NAME_ORIGINAL_URI = "originalUri";
    public static final String TAG_NAME_TOTAL_PAGES = "totalPages";
    public static final String TAG_NAME_START_PAGE = "startPage";
    public static final String TAG_NAME_END_PAGE = "endPage";
    public static final String TAG_NAME_FILE = "file";

    public static final String TAG_VALUE_JASPERPRINT = "jasperPrint";
    public static final String TAG_VALUE_REPORT = "report";

    public boolean equals(ReportDescriptor reportDescriptor) {
        return (this.uuid.equals(reportDescriptor.uuid) &&
                this.originalUri.equals(reportDescriptor.originalUri) &&
                this.totalPages.equals(reportDescriptor.totalPages) &&
                this.startPage.equals(reportDescriptor.startPage) &&
                this.endPage.equals(reportDescriptor.endPage));
    }

    public void parseXMLResponse(HttpResponse httpRes) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStream is = httpRes.getEntity().getContent();
        //Show the response xml in log

        Document dom = db.parse(is);

        
        NodeList uuidNode = dom.getElementsByTagName(UUID_TAG_NAME);
        uuid = getTextValue((Element) uuidNode.item(0));

        NodeList originalUriNode = dom.getElementsByTagName(TAG_NAME_ORIGINAL_URI);
        originalUri = getTextValue((Element) originalUriNode.item(0));

        NodeList totalPagesNode = dom.getElementsByTagName(TAG_NAME_TOTAL_PAGES);
        totalPages = getTextValue((Element) totalPagesNode.item(0));

        NodeList startPageNode = dom.getElementsByTagName(TAG_NAME_START_PAGE);
        startPage = getTextValue((Element) startPageNode.item(0));

        NodeList endPageNode = dom.getElementsByTagName(TAG_NAME_END_PAGE);
        endPage = getTextValue((Element) endPageNode.item(0));

        NodeList files = dom.getElementsByTagName(TAG_NAME_FILE);
        file_id = new String[files.getLength()][3];
        for (int i = 0; i < files.getLength(); ++i) {
            file_id[i][0] = getTextValue((Element) files.item(i));
            file_id[i][1] = ((Element) files.item(i)).getAttribute("type");
            file_id[i][2] = file_id[i][1].split("[/]")[1];
        }
    }

    public String getFileExtension() {
        String fileExt = null;

        for (String[] aFile_id : file_id) {
            if (aFile_id[0].equals("report")) {
                if (aFile_id[2].contains("vnd.ms-excel")) {
                    fileExt = "csv";
                } else {
                    fileExt = aFile_id[2];
                }
            }
            if (aFile_id[0].equals("jasperPrint")) {
                fileExt = "jasperPrint";
            }
        }
        return fileExt;
    }

    public boolean isJasperPrint() {
        for (String[] aFile_id : file_id) {
            if (aFile_id[0].equals("jasperPrint")) {
                return true;
            }
        }
        return false;
    }

}