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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.PermissionUriProtocol;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

import java.io.Serializable;

/**
 * @author swood
 *
 */
public class InternalURIDefinition implements InternalURI {

	private String uri;
	private PermissionUriProtocol protocol;

	public InternalURIDefinition(String uri) {
		uri = PermissionUriProtocol.cleanUri(uri);
		this.uri = PermissionUriProtocol.removePrefix(uri);
		this.protocol = PermissionUriProtocol.getProtocol(uri);
	}

	public InternalURIDefinition(String uri, PermissionUriProtocol protocol) {
		this.uri = PermissionUriProtocol.removePrefix(uri);
		this.protocol = protocol;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getParentPath()
	 */
	public String getParentPath() {
		return protocol.getParentUri(uri);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getParentURI()
	 */
	public String getParentURI() {
		return getParentPath() == null ? null : protocol.addPrefix(getParentPath());
	}

	public String getParentFolder() {
		return protocol.getParentUri(uri);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getPath()
	 */
	public String getPath() {
		return uri;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getProtocol()
	 */
	public String getProtocol() {
		return protocol.toString();
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI#getURI()
	 */
	public String getURI() {
		return protocol.addPrefix(getPath());
	}

	@Override
	public Serializable getIdentifier() {
		return getURI();
	}

	@Override
	public String getType() {
		return Folder.class.getName();
	}

	@Override
	public String toString() {
		return getURI();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InternalURIDefinition that = (InternalURIDefinition) o;

		if (protocol != that.protocol) return false;
		if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = uri != null ? uri.hashCode() : 0;
		result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
		return result;
	}
}
