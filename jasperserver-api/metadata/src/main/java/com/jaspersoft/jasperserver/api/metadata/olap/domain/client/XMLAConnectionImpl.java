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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;

/**
 * @author swood
 *
 */
public class XMLAConnectionImpl extends OlapClientConnectionImpl implements XMLAConnection {

	private String uri;
	private String dataSource;
	private String catalog;
        private String username;
        private String password;
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#getURI()
	 */
	public String getURI() {
		return uri;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#setURI(java.lang.String)
	 */
	public void setURI(String uri) {
		this.uri = uri;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#getDataSource()
	 */
	public String getDataSource() {
		return dataSource;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#setDataSource(java.lang.String)
	 */
	public void setDataSource(String xmlaDataSource) {
		this.dataSource = xmlaDataSource;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#getCatalog()
	 */
	public String getCatalog() {
		return catalog;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#setUsername(java.lang.String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#getUsername()
	 */
	public String getUsername() {
		return username;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection#getPassword()
	 */
	public String getPassword() {
		return password;
	}

	
	protected Class getImplementingItf() {
		return XMLAConnection.class;
	}

}
