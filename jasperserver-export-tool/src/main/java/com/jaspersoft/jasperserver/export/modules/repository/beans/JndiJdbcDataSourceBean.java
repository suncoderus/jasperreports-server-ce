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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id$
 */
public class JndiJdbcDataSourceBean extends ResourceBean {

	private String jndiName;
	private String timezone;

	protected void additionalCopyFrom(Resource res, ResourceExportHandler referenceHandler) {
		JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) res;
		setJndiName(ds.getJndiName());
		setTimezone(ds.getTimezone());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		JndiJdbcReportDataSource ds = (JndiJdbcReportDataSource) res;
		ds.setJndiName(getJndiName());
		ds.setTimezone(getTimezone());
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}	

}
