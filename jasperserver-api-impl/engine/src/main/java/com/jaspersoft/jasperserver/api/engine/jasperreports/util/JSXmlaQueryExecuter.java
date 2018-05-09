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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import org.fusesource.hawtbuf.ByteArrayInputStream;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuter;
import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory;
import net.sf.jasperreports.util.Base64Util;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JSXmlaQueryExecuter extends JRXmlaQueryExecuter {

	public JSXmlaQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset, Map parametersMap) {
		super(jasperReportsContext, dataset, parametersMap);
	}

	@Override
	protected SOAPMessage createQueryMessage() {
		SOAPMessage message = super.createQueryMessage();
		
		String user = (String) getParameterValue(
				JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER, true);
		if (user != null && user.length() > 0) {
			// set the Basic Auth header
			String auth = user;
			String password = (String) getParameterValue(
					JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD, true);
			if (password != null && password.length() > 0) {
				auth += ":" + password;
			}
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try
			{
				Base64Util.encode(new ByteArrayInputStream(auth.getBytes("ISO-8859-1")), os);//this was the hardcoded encoding used in the old Base64 encoder; keeping just for backward compatibility
			}
			catch (IOException ex)
			{
				throw new JRRuntimeException(ex);
			}
			String encodedAuth = os.toString().trim();

			MimeHeaders headers = message.getMimeHeaders();
			headers.addHeader("Authorization", "Basic " + encodedAuth);
		}
		
		return message;
	}

	@Override
	protected String getSoapUrl() throws MalformedURLException {
		// do not include user & password in the URL
		return (String) getParameterValue(
				JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL);
	}
	
	

}
