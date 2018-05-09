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

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepositoryResourceKey {
	private final String uriKey;
	private final String uri;
	private final int version;
	private final Date creationDate;
	private final int hash;
	
	public RepositoryResourceKey(String uri, int version, Date creationDate) {
		this(uri, uri, version, creationDate);
	}	
	
	public RepositoryResourceKey(String uriKey, String uri, 
			int version, Date creationDate) {
		this.uriKey = uriKey;
		this.uri = uri;
		this.version = version;
		this.creationDate = creationDate;
		
		this.hash = new HashCodeBuilder().append(this.uriKey).append(this.uri)
				.append(this.version).append(this.creationDate).toHashCode();
	}
	
	public RepositoryResourceKey(Resource res) {
		this(res.getURIString(), res.getVersion(), res.getCreationDate());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof RepositoryResourceKey)) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		RepositoryResourceKey key = (RepositoryResourceKey) obj;
		if (hash != key.hash) {
			return false;
		}
		return new EqualsBuilder().append(uriKey, key.uriKey).append(uri, key.uri)
				.append(version, key.version).append(creationDate, key.creationDate)
				.isEquals();
	}

	public int hashCode() {
		return hash;
	}

	public String getUriKey() {
		return uriKey;
	}

	public String getUri() {
		return uri;
	}

	public int getVersion() {
		return version;
	}
	
	public Date getCreatDate() {
		return creationDate;
	}
}
