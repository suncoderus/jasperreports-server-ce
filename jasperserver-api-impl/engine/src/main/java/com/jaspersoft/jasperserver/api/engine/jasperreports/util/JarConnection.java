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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JarConnection extends URLConnection {

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEE, dd MMM yyyy hh:mm:ss 'GMT'");

	private final JarFileEntry entry;

	public JarConnection(URL url, JarFileEntry entry) {
		super(url);

		this.entry = entry;
	}

	public void connect() throws IOException {
		connected = true;
	}

	public InputStream getInputStream() throws IOException {
		return entry.getInputStream();
	}

	public int getContentLength() {
		return (int) entry.getSize();
	}

	public long getLastModified() {
		return entry.getTime();
	}

	public String getHeaderField(String field) {
		String header = null;
		if (field.equals("content-length"))
			header = Long.toString(entry.getSize());
		else if (field.equals("last-modified")) {
			synchronized (DATE_FORMAT) {
				header = DATE_FORMAT.format(new Date(entry.getTime()));
			}
		}
		return header;
	}
}
