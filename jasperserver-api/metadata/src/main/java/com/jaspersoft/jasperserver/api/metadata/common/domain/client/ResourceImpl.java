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

import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public abstract class ResourceImpl implements Resource, Serializable
{
	private int version;
	private Date creationDate;
	private Date updateDate;
	private String name;
	private String label;
	private String description;
    private List attributes;
	private String folderUri;
	private String uri;

	private static String getParentFolderFromUri(String s) {
		if (s == null || s.length() == 0) {
			return null;
		}
		int lastSeparator = s.lastIndexOf(Folder.SEPARATOR);
		
		if (lastSeparator < 0) {
			return null;
		}
		
		if (lastSeparator == 0) {
			return Folder.SEPARATOR;
		}
			
		return s.substring(0, lastSeparator);
	}

	private static String getNameFromUri(String s) {
		if (s == null || s.length() == 0) {
			return null;
		}
		int lastSeparator = s.lastIndexOf(Folder.SEPARATOR);
		
		if (lastSeparator < 0 || lastSeparator == s.length() - 1) {
			return null;
		}
			
		return s.substring(lastSeparator + 1, s.length());
	}
	
	protected ResourceImpl() {
		version = VERSION_NEW;
	}

    protected ResourceImpl(ResourceImpl another) {
        if (another != null) {
            attributes = another.attributes != null ? Arrays.asList(another.attributes.toArray().clone()) : null;
            creationDate = another.creationDate != null ? (Date) another.creationDate.clone() : null;
            updateDate = another.updateDate != null ? (Date) another.updateDate.clone() : null;

            version = another.version;
            name = another.name;
            label = another.label;
            description = another.description;
            folderUri = another.folderUri;
            uri = another.uri;
        }
    }

    @Override
    public Serializable getIdentifier() {
        return getURIString();
    }

    @Override
    public String getType() {
        return getResourceType();
    }

    public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		uri = null;
		this.name = name;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getURIString() {
        if (uri == null) {
            StringBuffer sb = new StringBuffer();
            if (getParentFolder() != null && !getParentFolder().equals(Folder.SEPARATOR))
                sb.append(getParentFolder());
            sb.append(Folder.SEPARATOR);
            if (getName() != null && !getName().equals(Folder.SEPARATOR))
                sb.append(getName());
            uri = sb.toString();
        }
        return uri;
	}

	public void setURIString(String uri)
	{
		this.uri = uri;
		this.name = getNameFromUri(uri);
		this.folderUri = getParentFolderFromUri(uri);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getPath()
	 */
	public String getPath() {
		return getURIString();
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getProtocol()
	 */
	public String getProtocol() {
		return Resource.URI_PROTOCOL;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getURI()
	 */
	public String getURI() {
		return getProtocol() + ":" + getPath();
	}
	
	public String getParentURI() {
		return getParentFolder() == null ? null : getProtocol() + ":" + getParentFolder();
	}
	
	public String getParentPath() {
		return getParentFolder() == null ? null : getParentFolder();
	}

	public String getParentFolder() {
		return folderUri;
	}

	public void setParentFolder(Folder folder) {
		this.uri = null;
		folderUri = (folder == null ? null : folder.getURIString());
	}

	public void setParentFolder(String folderURI) {
		this.uri = null;
		folderUri = folderURI;
	}

	public List getAttributes()
	{
       	        return attributes;
	}

        public void setAttributes(List attrs) 
        {
	    attributes = attrs;
        }


	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getResourceType() {
		return getImplementingItf().getName();
	}
	
	public boolean isSameType(Resource resource) {
		return getResourceType().equals(resource.getResourceType());
	}

	protected abstract Class getImplementingItf();

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public boolean isNew() {
		return version == VERSION_NEW;
	}
}
