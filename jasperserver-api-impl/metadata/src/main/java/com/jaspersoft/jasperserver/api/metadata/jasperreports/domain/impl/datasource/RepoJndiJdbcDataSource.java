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
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;



/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: DataSourceImpl.java 2281 2006-03-02 18:05:23Z lucian $
 *
 * @hibernate.joined-subclass table="JNDIJdbcDatasource"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoJndiJdbcDataSource extends RepoDataSource implements RepoReportDataSource
{
	private String jndiName;
	private String timezone;

	
	public RepoJndiJdbcDataSource()
	{
	}

	/**
	 * @hibernate.property
	 * 		column="jndiName" type="string" length="100" not-null="true"
	 */
	public String getJndiName()
	{
		return jndiName;
	}

	public void setJndiName(String jndiName)
	{
		this.jndiName = jndiName;
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
		return JndiJdbcReportDataSource.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);
		
		JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) clientRes;
		ds.setJndiName(getJndiName());
		ds.setTimezone(getTimezone());
	}
	
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
		super.copyFrom(clientRes, referenceResolver);
		JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) clientRes;
		setJndiName(ds.getJndiName());
		setTimezone(ds.getTimezone());
	}
}
