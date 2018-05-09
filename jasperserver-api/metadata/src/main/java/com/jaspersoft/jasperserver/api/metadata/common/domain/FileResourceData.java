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

import java.io.InputStream;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;

/**
 * The class represents the container for file resource data in JasperServer
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public class FileResourceData {
	private final DataContainer dataContainer;

    protected FileResourceData(FileResourceData parent){
        this.dataContainer = parent.dataContainer;
    }

    /**
     * Creates a new FileResourceData object from byte array data.
     *
     * @param data data byte array
     * @return FileResourceData object
     */
	public FileResourceData(byte[] data) {
		this.dataContainer = new MemoryDataContainer(data);
	}

    /**
     * Creates a new FileResourceData object from the input stream.
     *
     * @param is data input stream
     * @return FileResourceData object
     */
	public FileResourceData(InputStream is) {
		this.dataContainer = new FileBufferedDataContainer();
		DataContainerStreamUtil.pipeData(is, this.dataContainer);
	}

	public FileResourceData(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}

    /**
     * Returns <code>true</code> if the container has some data.
     *
     * @return <code>true</code> if the container has some data.
     */    
	public boolean hasData() {
		return dataContainer.hasData();
	}

    /**
     * Returns size of data contained in this file resource
     *
     * @return data size
     */    
	public int dataSize() {
		return dataContainer.dataSize();
	}

    /**
     * Returns the data from the data container of resource as a byte array
     *
     * @return data
     */    
	public byte[] getData() {
		return dataContainer.getData();
	}

    /**
     * Returns the data stream from the data container of resource
     *
     * @return data stream of the resource
     */    
	public InputStream getDataStream() {
		return dataContainer.getInputStream();
	}
	
	public void dispose() {
		dataContainer.dispose();
	}
}
