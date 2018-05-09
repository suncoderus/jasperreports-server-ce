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
package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class GzipDataContainer implements DataContainer {

	private static final Log log = LogFactory.getLog(GzipDataContainer.class);
	
	private final DataContainer decorated;
	
	private DataContainerStreamUtil.CompressedOutputStream compressedOut;
	private int uncompressedSize;
	
	public GzipDataContainer(DataContainer decorated) {
		this.decorated = decorated;
	}
	
	public GzipDataContainer(DataContainer decorated, int uncompressedSize) {
		this(decorated);
		this.uncompressedSize = uncompressedSize;
	}
	
	public boolean hasData() {
		return decorated.hasData();
	}

	public OutputStream getOutputStream() {
		if (compressedOut == null) {
			OutputStream out = decorated.getOutputStream();
			try {
				compressedOut = new DataContainerStreamUtil.CompressedOutputStream(out) {
					@Override
					public void close() throws IOException {
						super.close();

						// set the final uncompressed size
						GzipDataContainer.this.uncompressedSize = getUncompressedSize();
					}
				};
				return compressedOut;
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			}
		}
		
		return compressedOut;
	}

	public int dataSize() {
		return uncompressedSize;
	}

	public InputStream getInputStream() {
		InputStream in = decorated.getInputStream();
		if (in == null) {
			return null;
		}
		
		try {
			return new GZIPInputStream(in);
		} catch (IOException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	public byte[] getData() {
		return DataContainerStreamUtil.readDataAndClose(getInputStream());
	}

	public DataContainer getDecorated() {
		return decorated;
	}

	public void dispose() {
		if (compressedOut != null) {
			try {
				compressedOut.close();
			} catch (IOException e) {
				if (log.isDebugEnabled()) {
					log.debug("Failed to close output stream", e);
				}
			}
		}
		
		decorated.dispose();
	}
}
