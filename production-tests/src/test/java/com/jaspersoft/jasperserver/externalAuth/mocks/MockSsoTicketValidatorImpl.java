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
package com.jaspersoft.jasperserver.externalAuth.mocks;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.security.externalAuth.sso.SsoTicketValidatorImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

/**
 * User: dlitvak
 * Date: 9/21/12
 */
public class MockSsoTicketValidatorImpl extends SsoTicketValidatorImpl {
	private static final Logger logger = LogManager.getLogger(MockSsoTicketValidatorImpl.class);

	private String testValidatedPrincipal;
	private URI ticketValidationUrl;

	@Override
	protected final URI constructValidationUrl(final String ticket) throws AuthenticationServiceException {
		ticketValidationUrl = super.constructValidationUrl(ticket);
		return ticketValidationUrl;
	}

	@Override
	protected ClientHttpResponse requestTicketValidationFromSsoServer(URI validationUrl) throws AuthenticationServiceException {
		MockClientHttpResponse response = new MockClientHttpResponse();
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			//create the root element and add it to the document
			Element root = doc.createElement("response");
			root.setAttribute("xmlns:cas", "http://www.yale.edu/tp/cas");
			Element rootChild = doc.createElement(getExternalAuthProperties().getCustomSsoProperty(PRINCIPAL_XML_TAG_NAME));
			root.appendChild(rootChild);

			Text text = doc.createTextNode(testValidatedPrincipal);
			rootChild.appendChild(text);
			doc.appendChild(root);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));

			response.setBody(writer.getBuffer().toString());
		} catch (ParserConfigurationException e) {
			logger.error(e);
			throw new JSException(e);
		}
		catch (TransformerException te) {
			logger.error(te);
			throw new JSException(te);
		}

		return response;
	}

	public URI getTicketValidationUrl() {
		return ticketValidationUrl;
	}

	public String getTestValidatedPrincipal() {
		return testValidatedPrincipal;
	}

	public void setTestValidatedPrincipal(String testValidatedPrincipal) {
		this.testValidatedPrincipal = testValidatedPrincipal;
	}

	public void cleanup() {
		this.ticketValidationUrl = null;
	}
}
