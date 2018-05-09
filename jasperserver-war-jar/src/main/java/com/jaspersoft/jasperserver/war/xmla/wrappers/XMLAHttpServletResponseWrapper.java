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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class XMLAHttpServletResponseWrapper extends HttpServletResponseWrapper {

	ServletOutputStreamWrapper outputStream = new ServletOutputStreamWrapper();

	public XMLAHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(outputStream);
	}

	public String getBytes() {
		return outputStream.getBytes();
	}

	public void updateBytes(String s) {
		outputStream.updateBytes(s);
	}
}
