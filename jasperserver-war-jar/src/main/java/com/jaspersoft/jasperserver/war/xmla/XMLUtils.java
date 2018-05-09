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
package com.jaspersoft.jasperserver.war.xmla;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

/**
 * This file provides XSLT services to XMLAServletImpl 
 * @author udavidovich
 *
 */
public class XMLUtils {
	
		/**
		 * This method will apply an XSLT transformation
		 * @param source the source reader
		 * @param result the target writter
		 * @param style the stylesheet to be applied
		 * @throws TransformerException
		 */
		static void transform(Reader source, Writer result, String style) throws TransformerException {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(new StringReader(style)));
			transformer.transform(new StreamSource(source), new StreamResult(result));
		}

		/**
		 * read a file from classpath into a string
		 * @param uri the path to the file (e.g. com/jaspersoft/foo/bar.xsl)
		 * @return the content of the file
		 * @throws IOException
		 */
		static String loadStyle(String uri) throws IOException {
			InputStream xslt = XMLUtils.class.getClassLoader().getResourceAsStream(uri);
			
			StringWriter xsltwriter = new StringWriter();
			IOUtils.copy(xslt, xsltwriter);

			return xsltwriter.toString();
		}
}
