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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * 
 * @hibernate.joined-subclass table="JdbcDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoJdbcDataSource extends RepoDataSource implements RepoReportDataSource {
	
	private String driverClass;
	private String connectionUrl;
	private String username;
	private String password;
	private String timezone;

	public RepoJdbcDataSource() {
	}

	/**
	 * @hibernate.property column="driver" type="string" length="100" not-null="true"
	 */
	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @hibernate.property column="password" type="string" length="250"
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @hibernate.property column="connectionUrl" type="string" length="200"
	 */
	public String getConnectionUrl() {
		return connectionUrl;
	}

	public void setConnectionUrl(String url) {
		this.connectionUrl = url;
	}

	/**
	 * @hibernate.property column="username" type="string" length="100"
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTimezone()
	{
		return timezone;
	}

	public void setTimezone(String timezone)
	{
		this.timezone = timezone;
	}

	protected Class getClientItf() {
		return JdbcReportDataSource.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		JdbcReportDataSource ds = (JdbcReportDataSource) clientRes;
		ds.setDriverClass(getDriverClass());
		ds.setConnectionUrl(getConnectionUrl());
		ds.setUsername(getUsername());
		ds.setTimezone(getTimezone());
		ds.setPassword(PasswordCipherer.getInstance().decodePassword(getPassword()));
		
	}

	protected void copyFrom(Resource clientRes,
			ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		JdbcReportDataSource ds = (JdbcReportDataSource) clientRes;
		setDriverClass(ds.getDriverClass());
		setConnectionUrl(ds.getConnectionUrl());
		setUsername(ds.getUsername());
		setTimezone(ds.getTimezone());
		setPassword(PasswordCipherer.getInstance().encodePassword(ds.getPassword()));
		
	}
}
