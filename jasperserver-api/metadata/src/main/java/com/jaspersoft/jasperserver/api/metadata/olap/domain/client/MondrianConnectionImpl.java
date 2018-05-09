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

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;

/**
 * @author swood
 *
 */
public class MondrianConnectionImpl extends OlapClientConnectionImpl implements MondrianConnection {

	private ResourceReference schema = null;
	private ResourceReference dataSource = null;

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#getDataSource()
	 */
	public ResourceReference getDataSource() {
		return dataSource;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#getSchema()
	 */
	public ResourceReference getSchema() {
		return schema;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#setDataSource(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */

	public void setDataSource(ReportDataSource dataSource) {
		setDataSource(new ResourceReference(dataSource));
	}


	public void setDataSourceReference(String referenceURI) {
		setDataSource(new ResourceReference(referenceURI));
	}

	public void setDataSource(ResourceReference dataSource) {
		this.dataSource = dataSource;
	}
	/**
	 * 
	 */
	public void setSchema(ResourceReference schema)	{
		this.schema = schema;
	}


	public void setSchema(FileResource schema) {
		setSchema(new ResourceReference(schema));
	}


	public void setSchemaReference(String referenceURI) {
		setSchema(new ResourceReference(referenceURI));
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl#getImplementingItf()
	 */
	protected Class getImplementingItf() {
		return MondrianConnection.class;
	}

}
