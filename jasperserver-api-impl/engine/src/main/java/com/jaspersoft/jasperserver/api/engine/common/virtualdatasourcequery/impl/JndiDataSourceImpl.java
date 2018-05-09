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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JndiDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;

import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class JndiDataSourceImpl extends DataSourceImpl implements JndiDataSource {

    private Set<String> schemas;

    public JndiDataSourceImpl(JndiJdbcReportDataSource jndiJdbcReportDataSource, Set<String> schemas, String dataSourceName, VirtualReportDataSource parentDataSource) {
        super(jndiJdbcReportDataSource, dataSourceName, parentDataSource);
        this.schemas = schemas;
    }

	/**
	 * Get the name used to obtain the javax.sql.DataSource associated with this ReportDataSource.
	 * This name is prepended with the string "java:comp/env/" and passed to javax.naming.Context.lookup(),
	 * which should return a DataSource.
	 * @return name of a JNDI data source registered with the application server
	 */
    public String getJndiName() {
        return ((JndiJdbcReportDataSource)reportDataSource).getJndiName();
    }

    public Set<String> getSchemas() {
        return schemas;
    }

}
