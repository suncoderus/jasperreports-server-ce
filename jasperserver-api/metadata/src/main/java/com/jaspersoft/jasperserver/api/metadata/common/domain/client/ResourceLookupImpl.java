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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ResourceLookupImpl implements ResourceLookup,Serializable {
	
	private int version;
	private Date creationDate;
	private Date updateDate;
	private String name;
	private String label;
	private String description;
	private String folderUri;
	private String uri;
	private String resourceType;
    private List attributes = null;

	public ResourceLookupImpl() {
	}

	private static String getParentFolderFromUri(String s) {
		if (s == null || s.length() == 0) {
			return null;
		}
		int lastSeparator = s.lastIndexOf(Folder.SEPARATOR);
		
		if (lastSeparator < 0) {
			return null;
		} else if (lastSeparator == 0) {
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

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List getAttributes() {
	    return attributes;
	}

        public void setAttributes(List attrs) {
	    attributes = attrs;
        }

	public void setLabel(String label) {
		this.label = label;
	}

	public void setName(String name) {
		this.uri = null;
		this.name = name;
	}

	public String getURIString()
	{
		if (uri == null) {
            StringBuilder sb = new StringBuilder();
			if (getParentFolder() != null && !getParentFolder().equals(Folder.SEPARATOR))
				sb.append(getParentFolder());
			if(getName() != null) {
				sb.append(Folder.SEPARATOR);
				if (!getName().equals(Folder.SEPARATOR))
					sb.append(getName());
			}
			uri = sb.length() > 0 ? sb.toString() : null;
		}
		return uri;
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

	public String getParentFolder() {
		return folderUri;
	}

	public void setParentFolder(Folder folder) {
		this.uri = null;
		folderUri = folder.getURIString();
	}

	public void setParentFolder(String folderURI) {
		this.uri = null;
		folderUri = folderURI;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	protected final JSException lookupUnsupportedException() {
		return new JSException("jsexception.call.not.supported");
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

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
		return false;
	}

	public void setURIString(String uri) {
		this.uri = null;
		this.name = getNameFromUri(uri);
		this.folderUri = getParentFolderFromUri(uri);
	}

	public boolean isSameType(Resource resource) {
		return getResourceType().equals(resource.getResourceType());
	}

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResourceLookup)) {
            return false;
        }
        if (o == this) {
            return true;
        }

        String uri = this.getURIString();
        ResourceLookup resourceLookup = (ResourceLookup) o;
        return (uri != null && uri.equals(resourceLookup.getURIString()) && resourceType != null &&
                resourceType.equals(resourceLookup.getResourceType()));
    }

    @Override
    public Serializable getIdentifier() {
        return getURIString();
    }

    @Override
    public String getType() {
        return getResourceType();
    }

    @Override
    public int hashCode() {
        return getURIString().hashCode();
    }

}
