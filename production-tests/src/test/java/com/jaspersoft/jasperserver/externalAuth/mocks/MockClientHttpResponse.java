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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: dlitvak
 * Date: 9/21/12
 */
public class MockClientHttpResponse implements ClientHttpResponse {
	private static final Logger logger = LogManager.getLogger(MockClientHttpResponse.class);

	private String body = "";

	/**
	 * Return the HTTP status code of the response.
	 *
	 * @return the HTTP status as an HttpStatus enum value
	 * @throws java.io.IOException in case of I/O errors
	 */
	@Override
	public HttpStatus getStatusCode() throws IOException {
		return HttpStatus.OK;
	}

	/**
	 * Return the HTTP status text of the response.
	 *
	 * @return the HTTP status text
	 * @throws java.io.IOException in case of I/O errors
	 */
	@Override
	public String getStatusText() throws IOException {
		return "Status OK";
	}

	/**
	 * Closes this response, freeing any resources created.
	 */
	@Override
	public void close() {
		//Do nothing in the mock
	}

	/**
	 * Return the body of the message as an input stream.
	 *
	 * @return the input stream body
	 * @throws java.io.IOException in case of I/O Errors
	 */
	@Override
	public InputStream getBody() throws IOException {
		return new ByteArrayInputStream(this.body.getBytes());
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Return the headers of this message.
	 *
	 * @return a corresponding HttpHeaders object
	 */
	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.add("name", "value");
		return httpHeaders;
	}

	@Override
	public int getRawStatusCode() throws IOException
 	{	
 		return 200;
 	}
}
