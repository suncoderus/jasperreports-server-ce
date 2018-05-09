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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;

/**
 * @author swood
 *
 * @hibernate.joined-subclass table="MondrianXMLADefinition"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoMondrianXMLADefinition extends RepoResource {

	private RepoMondrianConnection connection = null;
	private String catalog;

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
     * @hibernate.many-to-one
     *              column="mondrianConnection"
     *
	 * @return Returns the connection.
	 */
	public RepoMondrianConnection getMondrianConnection() {
		return connection;
	}

	/**
	 * @param connection The connection to set.
	 */
	public void setMondrianConnection(RepoMondrianConnection connection) {
		this.connection = connection;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase#copyTo(com.jaspersoft.jasperserver.api.metadata.common.domain.Resource)
	 */
    protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
        super.copyTo(clientRes, resourceFactory);

		MondrianXMLADefinition def = (MondrianXMLADefinition) clientRes;

		def.setCatalog(getCatalog());
		def.setMondrianConnection(getClientReference(getMondrianConnection(), resourceFactory));

	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase#copyFrom(com.jaspersoft.jasperserver.api.metadata.common.domain.Resource)
	 */
    protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
        super.copyFrom(clientRes, referenceResolver);
		MondrianXMLADefinition def = (MondrianXMLADefinition) clientRes;

		setCatalog(def.getCatalog());
		ResourceReference ds = def.getMondrianConnection();
		RepoMondrianConnection repoConn = (RepoMondrianConnection) getReference(ds, RepoMondrianConnection.class, referenceResolver);
		setMondrianConnection(repoConn);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceBase#getClientItf()
	 */
	protected Class getClientItf() {
		return MondrianXMLADefinition.class;
	}

}
