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

package example.cds;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

/**
 * @author bob
 * 
 */
public class WebScraperDataSource implements JRDataSource {
	public static final String WEBSCRAPE_PATH = "webscrape.path";
	private NodeList nodes;
	private int index = -1;
	private String urlValue;
	private String path;
	private Map params = new HashMap();
	private XPath xpath;

	/**
	 * @param url
	 * @param path
	 * @param parameterValues 
	 */
	public WebScraperDataSource(String urlValue, String path, Map parameterValues) {
		this.urlValue = urlValue;
		this.path = path;
		Iterator pvi = parameterValues.entrySet().iterator();
		while (pvi.hasNext()) {
			Map.Entry pv = (Entry) pvi.next();
			String key = (String) pv.getKey();
			if (key.startsWith("ws_p_")) {
				params.put(key.substring(5), pv.getValue());
			}
		}
	}

	private void init() throws JRException {
		if (nodes != null) {
			return;
		}
		try {
			URL url = new URL(urlValue);
			InputStream is = url.openStream();
			Tidy tidy = new Tidy();
			OutputStream es = new ByteArrayOutputStream();
			tidy.setErrout(new PrintWriter(es));
			Document doc = tidy.parseDOM(is, null);
			is.close();

			xpath = XPathFactory.newInstance().newXPath();
			nodes = (NodeList) xpath.evaluate(path, doc, XPathConstants.NODESET);
		} catch (Exception e) {
			throw new JRException("Exception getting web page " + urlValue+ " with node path " + path, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
	 */
	public Object getFieldValue(JRField jrField) throws JRException {
		init();
		String itemPath = jrField.getPropertiesMap().getProperty(WEBSCRAPE_PATH);
		if (itemPath == null) {
			throw new JRException("Missing " + WEBSCRAPE_PATH + " property on field " + jrField.getName());
		}
		try {
			return xpath.evaluate(itemPath, nodes.item(index), XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new JRException("Exception getting field with path " + itemPath, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	public boolean next() throws JRException {
		init();
		index++;
		return nodes != null && index < nodes.getLength();
	}

}
