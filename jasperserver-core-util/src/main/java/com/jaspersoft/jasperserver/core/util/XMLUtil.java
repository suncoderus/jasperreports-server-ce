/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.core.util;

import com.jaspersoft.jasperserver.api.JSException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

public class XMLUtil {
    private static DocumentBuilderFactory domFactory;
    private static XPathFactory xPathFactory;

    private static Boolean skipXXECheck;

    private static final Log log = LogFactory.getLog(XMLUtil.class);

    static{
        domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);

        xPathFactory = XPathFactory.newInstance();

    }

    public static Document toDocument(InputStream is)throws Exception {
        SAXBuilder builder = new SAXBuilder();
		return builder.build(is);
    }

    public static Document toDocument(String xmlString)throws Exception {
        InputStream is = new ByteArrayInputStream(xmlString.getBytes());
    	return toDocument(is);
    }


    /**
     * Add Quotes if not a valid JRXML expression (eg $R())
     * @param expr
     * @return
     */
    public static String getAsJRXMLTextFieldExpression(String expr, boolean renderBlank) {
        //should use regex - and will if necessary
        if (expr == null || renderBlank) {
            return "\"\"";
        }
        if (!expr.startsWith("$")) {
            return "\"" + expr + "\"";
        }
        return expr;

    }

    public static Date getUniqueDate(int counter) {
    	return new Date((new Date()).getTime()+counter);
    }

    public static DocumentBuilder getNewDocumentBuilder() throws ParserConfigurationException{
          return domFactory.newDocumentBuilder();
    }

    public static XPath getNewXPath(){
        return xPathFactory.newXPath();
    }

    /**
     * Sets the skipXXECheck. The method allows to set it only once, to prevent
     * disabling the check by static call from a client code
     * @param bSkipXXECheck true to disable XXE check
     */
    public static void setSkipXXECheck(boolean bSkipXXECheck) {
        if (skipXXECheck == null) {
            skipXXECheck = bSkipXXECheck;
        } else if(!skipXXECheck.equals(bSkipXXECheck)){
            //throw new JSException("XMLUtil.skipXXECheck is set already, rewrites are not allowed!");
            log.error("XMLUtil.skipXXECheck is set already, rewrites are not allowed!");
        }
    }

    public static void checkForXXE(byte[] xmlData) throws Exception {
        checkForXXE(new String(xmlData, Charset.forName("UTF-8")));
    }

    public static void checkForXXE(String xmlString) throws Exception {
        if (skipXXECheck == null || !skipXXECheck) {
            InputStream is = new ByteArrayInputStream(xmlString.getBytes(Charset.forName("UTF-8")));
            SAXBuilder builder = new SAXBuilder();
            builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            builder.build(is);
        }
    }

    /**
     * Checks for XML XXE exploit. Throws exception if DOCTYPE declaration is found.
     * Returns a new InputStream ready to read the content by a subsequent code fragment.
     * @param xmlStream
     * @return resets the input stream
     * @throws Exception if XML has DOCTYPE declaration
     */
    public static InputStream checkForXXE(InputStream xmlStream) throws Exception {
        if (skipXXECheck == null || !skipXXECheck) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            org.apache.commons.io.IOUtils.copy(xmlStream, baos);
            byte[] bytes = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            SAXBuilder builder = new SAXBuilder();
            builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            builder.build(bais);
            bais.reset();
            return bais;
        }
        return xmlStream;
    }


}
