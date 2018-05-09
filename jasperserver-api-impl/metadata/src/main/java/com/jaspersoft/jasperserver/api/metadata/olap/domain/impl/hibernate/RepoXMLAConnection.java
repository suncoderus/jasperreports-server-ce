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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.impl.hibernate;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;

/**
 * @author swood
 * 
 * @hibernate.joined-subclass table="XMLAConnection"
 * @hibernate.joined-subclass-key column="id"
 *
 */
public class RepoXMLAConnection extends RepoOlapClientConnection implements RepoReportDataSource {
	
	private String uri;
	private String dataSource;
	private String catalog;
	private String username;
	private String password;

	public RepoXMLAConnection() {
		super();
	}

	/**
	 * @hibernate.property column="catalog" type="string" length="100"
	 * 
	 * @return Returns the catalog.
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog The catalog to set.
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	/**
	 * @hibernate.property column="username" type="string" length="100"
	 * 
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @hibernate.property column="password" type="string" length="250"
	 * 
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @hibernate.property column="datasource" type="string" length="100"
	 * 
	 * @return Returns the dataSource.
	 */
	public String getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource The dataSource to set.
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @hibernate.property column="uri" type="string" length="100"
	 * 
	 * @return Returns the uri.
	 */
	public String getURI() {
		return uri;
	}

	/**
	 * @param uri The uri to set.
	 */
	public void setURI(String uri) {
		this.uri = uri;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource#copyTo(com.jaspersoft.jasperserver.api.metadata.common.domain.Resource, com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory)
	 */
	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		XMLAConnection conn = (XMLAConnection) clientRes;
		
		conn.setCatalog(getCatalog());
		conn.setUsername(getUsername());
		conn.setPassword(PasswordCipherer.getInstance().decodePassword(getPassword()));
		conn.setDataSource(getDataSource());
		conn.setURI(getURI());
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource#copyFrom(com.jaspersoft.jasperserver.api.metadata.common.domain.Resource, com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver)
	 */
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		
		XMLAConnection conn = (XMLAConnection) clientRes;
		setCatalog(conn.getCatalog());
		setUsername(conn.getUsername());
		setPassword(PasswordCipherer.getInstance().encodePassword(conn.getPassword()));
		setDataSource(conn.getDataSource());
		setURI(conn.getURI());
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase#getClientItf()
	 */
	protected Class getClientItf() {
		return XMLAConnection.class;
	}

}
