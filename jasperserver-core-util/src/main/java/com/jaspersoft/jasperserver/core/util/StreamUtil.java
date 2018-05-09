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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;


/**
 * This class was modified and moved from the StreamUtils class in the jaspserver.api package
 */
/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class StreamUtil {
	private static final Log log = LogFactory.getLog(StreamUtil.class);
	private static final int READ_STREAM_BUFFER_SIZE = 10000;

	public static byte[] readData(InputStream is) {
		if (is == null) {
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] bytes = new byte[READ_STREAM_BUFFER_SIZE];
		int ln = 0;
		try {
			while ((ln = is.read(bytes)) > 0) {
				baos.write(bytes, 0, ln);
			}
		} catch (IOException e) {
			log.error("Error while reading data.", e);
			throw new JSExceptionWrapper(e);
		}

		return baos.toByteArray();
	}

	public static byte[] readData(InputStream is, int size) {
		if (is == null) {
			return null;
		}

		try {
			byte[] data = new byte[size];
			int offset = 0;
			while (size > 0) {
				int read = is.read(data, offset, size);
				if (read < 0) {
					throw new JSException("jsexception.input.stream.exhausted", new Object[]{new Integer(size)});
				}
				offset += read;
				size -= read;
			}
			return data;
		} catch (IOException e) {
			log.error("Error while reading data.", e);
			throw new JSExceptionWrapper(e);
		}
	}

	public static byte[] readData(Blob blob) {
		if (blob == null) {
			return null;
		}

		try {
			return readData(blob.getBinaryStream());
		} catch (SQLException e) {
			log.error("Error while reading blob data", e);
			throw new JSExceptionWrapper(e);
		}
	}


	public static void pipeData(InputStream is, OutputStream os) throws IOException {
		if (is == null) {
			return;
		}

		byte[] bytes = new byte[READ_STREAM_BUFFER_SIZE];
		int ln = 0;
		while ((ln = is.read(bytes)) > 0) {
			os.write(bytes, 0, ln);
		}
	}

	public static void writeObjectByteData(ObjectOutputStream objectStream,
			InputStream data) throws IOException {
		pipeData(data, objectStream);
	}

	public static void readObjectByteData(ObjectInputStream objectStream,
			int size, OutputStream outStream) throws IOException {
		byte[] buffer = new byte[READ_STREAM_BUFFER_SIZE];

		while (size > 0) {
			int read = buffer.length;
			if (read > size)
			{
				read = size;
			}
			objectStream.readFully(buffer, 0, read);

			outStream.write(buffer, 0, read);

			size -= read;
		}
	}

	public static String uncompressToString(byte[] compressed) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
		GZIPInputStream gzis = new GZIPInputStream(bais);
		BufferedReader br = new BufferedReader(new InputStreamReader(gzis));
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}

	public static byte[] compress(String string) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		OutputStreamWriter osw = new OutputStreamWriter(gzos);
		osw.write(string);
		osw.flush();
		osw.close();
		return baos.toByteArray();
	}

	public static Object uncompressObject(byte[] compressed) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
		GZIPInputStream gzis = new GZIPInputStream(bais);
		ObjectInputStream ois = new ObjectInputStream(gzis);
		return ois.readObject();
	}

	public static byte[] compress(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		ObjectOutputStream oos = new ObjectOutputStream(gzos);
		oos.writeObject(object);
		oos.flush();
		oos.close();
		return baos.toByteArray();
	}

}

