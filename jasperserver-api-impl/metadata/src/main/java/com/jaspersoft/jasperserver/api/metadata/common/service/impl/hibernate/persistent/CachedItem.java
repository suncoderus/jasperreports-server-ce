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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.lob.SerializableBlob;

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * 
 * @hibernate.class table="repository_cache"
 */
public class CachedItem implements Serializable {
	private long id;
	private String uri;
	private String cacheName;
	private int version;
	//private SerializableBlob data;
	private CachedItem reference;
	private Date versionDate;
	private Set referrers;
    private byte[] dataBytes;
	
	/**
	 * @hibernate.property column="data" type="blob"
	 */
	public SerializableBlob getData() {
        if (getDataBytes() != null) {
            return (SerializableBlob) DataContainerStreamUtil.createComparableBlob(getDataBytes());
        } else {
            return null;
        }
	}

    /**
     * Because of different Blob implementations across databases,
     * we need to pull the complete blob bytes into the object,
     * so that we don't re-read the blob outside a database transaction
     */
	public void setData(SerializableBlob data) {
        // make sure you get the latest data
        if (data != null) {
            dataBytes = DataContainerStreamUtil.readData(data);
        } else {
            dataBytes = null;
        }
	}
	
	public byte[] getDataBytes() {
        return dataBytes;
	}

	public void setDataBytes(byte[] bytes) {
        this.dataBytes = bytes;
	}
	
	/**
	 * @hibernate.id generator-class="identity"
	 */
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @hibernate.property column="uri" type="string" length="200" not-null="true"
	 */
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * @hibernate.property column="version" not-null="true"
	 */
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	/**
	 * @hibernate.property column="cache_name" type="string" length="20" not-null="true"
	 */
	public String getCacheName() {
		return cacheName;
	}
	
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	
	/**
	 * @hibernate.many-to-one column="reference"
	 */
	public CachedItem getReference() {
		return reference;
	}
	
	public void setReference(CachedItem reference) {
		this.reference = reference;
	}

	public boolean isItemReference() {
		return getReference() != null;
	}

	/**
	 * @hibernate.property
	 * 		column="version_date" type="timestamp" not-null="true"

	 * @return Returns the versionDate.
	 */
	public Date getVersionDate() {
		return versionDate;
	}

	/**
	 * @param versionDate The versionDate to set.
	 */
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public Set getReferrers() {
		return referrers;
	}

	public void setReferrers(Set referrers) {
		this.referrers = referrers;
	}
}
