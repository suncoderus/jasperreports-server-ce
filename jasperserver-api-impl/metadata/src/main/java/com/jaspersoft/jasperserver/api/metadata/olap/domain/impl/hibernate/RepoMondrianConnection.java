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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;

/**
 * @author swood
 *
 * @hibernate.joined-subclass table="MondrianConnection"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoMondrianConnection extends RepoOlapClientConnection implements RepoReportDataSource {
	private RepoResource dataSource = null;
	private RepoFileResource schema = null;

	/**
	 * @hibernate.many-to-one
     *              column="mondrianSchema"
     *
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#getSchema()
	 *
     */
	public RepoFileResource getSchema() {
		return schema;
	}

	/**
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#setSchema(com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource)
	 */
	public void setSchema(RepoFileResource f) {
		schema = f;
	}

	/**
     * @hibernate.many-to-one
     *              column="dataSource"
     *
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#getDataSource()
     */
	public RepoResource getDataSource() {
		return dataSource;
	}

	/**
	 * @see com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection#setDataSource(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	public void setDataSource(RepoResource ds) {
		dataSource = ds;
	}

    protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
        super.copyTo(clientRes, resourceFactory);

        MondrianConnection conn = (MondrianConnection) clientRes;
        conn.setDataSource(getClientReference(getDataSource(), resourceFactory));
        conn.setSchema(getClientReference( getSchema(), resourceFactory));
    }

    protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
        super.copyFrom(clientRes, referenceResolver);

        MondrianConnection conn = (MondrianConnection) clientRes;
		copyDataSource(referenceResolver, conn);
		copySchema(referenceResolver, conn);
    }


	private void copyDataSource(ReferenceResolver referenceResolver, MondrianConnection conn) {
		ResourceReference ds = conn.getDataSource();
		RepoResource repoDS = getReference(ds, RepoReportDataSource.class, referenceResolver);
		if (repoDS != null && !(repoDS instanceof RepoReportDataSource)) {
			throw new JSException("jsexception.mondrian.jdbc.datasource.has.an.invalid.type", new Object[] {repoDS.getClass().getName()});
		}
		setDataSource(repoDS);
	}

	private void copySchema(ReferenceResolver referenceResolver, MondrianConnection conn) {
		ResourceReference report = conn.getSchema();
		RepoFileResource repoReport = (RepoFileResource) getReference(report, RepoFileResource.class, referenceResolver);
		setSchema(repoReport);
	}

	protected Class getClientItf() {
		return MondrianConnection.class;
	}

}
