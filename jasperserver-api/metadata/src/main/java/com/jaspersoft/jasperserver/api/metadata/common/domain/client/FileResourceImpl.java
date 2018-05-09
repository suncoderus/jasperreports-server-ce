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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FileResourceImpl extends ResourceImpl implements FileResource
{
	private String fileType;
	private byte[] data;
	private String referenceURI;

	public FileResourceImpl()
	{
		super();
	}

    public FileResourceImpl(FileResourceImpl another) {
        super(another);
        fileType = another.fileType;
        data = another.data != null ? another.data.clone() : null;
        referenceURI = another.referenceURI;
    }

    public byte[] getData()
	{
		return data;
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}

	public void readData(InputStream is)
	{
		setData(DataContainerStreamUtil.readData(is));
	}

	public InputStream getDataStream()
	{
		return data == null ? null : new ByteArrayInputStream(data);
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
		//empty array is considered no data
		return !isReference() && data != null && data.length > 0;
	}

	protected Class getImplementingItf() {
		return FileResource.class;
	}
	
	public boolean isSameType(Resource resource) {
		boolean same = super.isSameType(resource);
		if (same) {
			FileResource fileRes = (FileResource) resource;
			String resType = fileRes.getFileType();
			String type = getFileType();
			if (type != null && resType != null  && !type.equals(resType)) {
				same = false;
			}
		}
		return same;
	}
}
