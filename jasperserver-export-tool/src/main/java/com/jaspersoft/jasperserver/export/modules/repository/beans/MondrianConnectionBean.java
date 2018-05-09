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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id$
 */
public class MondrianConnectionBean extends ResourceBean {

	private ResourceReferenceBean schema;
	private ResourceReferenceBean dataSource;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler) {
		MondrianConnection conn = (MondrianConnection) res;
		setSchema(exportHandler.handleReference(conn.getSchema()));
		setDataSource(exportHandler.handleReference(conn.getDataSource()));
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		MondrianConnection conn = (MondrianConnection) res;
		conn.setSchema(importHandler.handleReference(getSchema()));
		conn.setDataSource(importHandler.handleReference(getDataSource()));
	}

	public ResourceReferenceBean getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(ResourceReferenceBean dataSource) {
		this.dataSource = dataSource;
	}
	
	public ResourceReferenceBean getSchema() {
		return schema;
	}
	
	public void setSchema(ResourceReferenceBean schema) {
		this.schema = schema;
	}

}
