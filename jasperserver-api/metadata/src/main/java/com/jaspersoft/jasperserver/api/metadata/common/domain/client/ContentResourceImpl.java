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
package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.MemoryDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;

public class ContentResourceImpl extends ResourceImpl implements ContentResource
{
	private String fileType;
	private DataContainer dataContainer;
	private String referenceURI;
	private List resources;

	public ContentResourceImpl()
	{
		super();
		resources = new ArrayList();
	}

	public byte[] getData()
	{
		return dataContainer == null ? null : dataContainer.getData();
	}

	public void setData(byte[] data)
	{
		setDataContainer(new MemoryDataContainer(data));
	}

        public int getSize()
        {
            return dataContainer == null || !hasData() ? 0 : dataContainer.dataSize(); 
        }
        
	public void setDataContainer(DataContainer dataContainer)
	{
		this.dataContainer = dataContainer;
	}

	public void readData(InputStream is)
	{
		setData(DataContainerStreamUtil.readData(is));
	}

	public InputStream getDataStream()
	{
		return dataContainer == null ? null : dataContainer.getInputStream();
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public boolean isReference() {
		return referenceURI != null && referenceURI.length() > 0;
	}

	public String getReferenceURI() {
		return referenceURI;
	}

	public void setReferenceURI(String referenceURI) {
		this.referenceURI = referenceURI;
	}

	public boolean hasData() {
		return !isReference() && dataContainer != null && dataContainer.hasData();
	}

	protected Class getImplementingItf() {
		return ContentResource.class;
	}

	public List getResources()
	{
		return resources;
	}

	public void setResources(List resources)
	{
		this.resources = resources;
	}

	public void addChildResource(ContentResource child)
	{
		resources.add(child);
	}
}
