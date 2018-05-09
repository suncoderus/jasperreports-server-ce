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
package com.jaspersoft.jasperserver.war.xmla.wrappers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


public class XMLAHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private byte[] contentData;

	public XMLAHttpServletRequestWrapper(HttpServletRequest request,
                                         byte[] contentData) {
        super(request);
		setContentData(contentData);
	}

	public XMLAHttpServletRequestWrapper(HttpServletRequest request) {
		this(request, new byte[0]);
	}

	public void setContentData(byte[] contentData) {
		this.contentData=contentData;
	}


	/**
	 * This method is safe to use multiple times. Changing the returned array
	 * will not interfere with this class operation.
	 * 
	 * @return The cloned content data.
	 */
	public byte[] getContentData() {
		return contentData.clone();
	}
	
	/**
	 * This method is safe to call multiple times. Calling it will not interfere
	 * with getParameterXXX() or getReader(). Every time a new
	 * ServletInputStream is returned that reads data from the begining.
	 * 
	 * @return A new ServletInputStream.
	 */
	public ServletInputStream getInputStream() throws IOException {
		return new ServletInputStreamWrapper(contentData);
	}

	/**
	 * This method is safe to call multiple times. Calling it will not interfere
	 * with getParameterXXX() or getInputStream(). Every time a new
	 * BufferedReader is returned that reads data from the begining.
	 * 
	 * @return A new BufferedReader with the wrapped request's character
	 *         encoding (or UTF-8 if null).
	 */
	public BufferedReader getReader() throws IOException {
		String enc = getRequest().getCharacterEncoding();
		if (enc == null)
			enc = "UTF-8";
		return new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(contentData), enc));
	}
}


