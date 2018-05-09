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

package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.FileBufferedOutputStream;

/**
 * @author Lucian Chirita
 * 
 */
public class FileBufferedDataContainer implements DataContainer {
	
	public static final int DEFAULT_MEMORY_THRESHOLD = 1 << 16;
	public static final int DEFAULT_INITIAL_MEMORY_BUFFER = 1 << 14;

	private int memoryThreshold;
	private transient FileBufferedOutputStream data;
	
	public FileBufferedDataContainer() {
		this(DEFAULT_MEMORY_THRESHOLD, DEFAULT_INITIAL_MEMORY_BUFFER);
	}
	
	public FileBufferedDataContainer(int memoryThreshold, int initialMemoryBuffer) {
		this.memoryThreshold = memoryThreshold;
		createDataBuffer(initialMemoryBuffer);
	}

	private void createDataBuffer(int initialMemoryBuffer) {
		data = new FileBufferedOutputStream(memoryThreshold, initialMemoryBuffer);
	}

	public OutputStream getOutputStream() {
		return data;
	}
	
	public byte[] getData() {
		return DataContainerStreamUtil.readDataAndClose(getInputStream());
	}

	public InputStream getInputStream() {
		try {
			return data.getDataInputStream();
		} catch (IOException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	public int dataSize() {
		return data.size();
	}

	public boolean hasData() {
		return true;
	}

	public void dispose() {
		data.dispose();
	}

	private void writeObject(java.io.ObjectOutputStream stream)
			throws IOException {
		stream.writeInt(memoryThreshold);
		
		stream.writeInt(data.size());
		DataContainerStreamUtil.writeObjectByteData(stream, data.getDataInputStream());
	}

	private void readObject(java.io.ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		memoryThreshold = stream.readInt();
		
		int size = stream.readInt();
		createDataBuffer(size <= memoryThreshold ? size : memoryThreshold);
		DataContainerStreamUtil.readObjectByteData(stream, size, data);
		data.close();
	}

}
