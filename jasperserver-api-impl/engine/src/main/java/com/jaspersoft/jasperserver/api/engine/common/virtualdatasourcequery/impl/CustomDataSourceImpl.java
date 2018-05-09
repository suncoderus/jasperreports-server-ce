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

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.CustomDataSource;
import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.JdbcDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;

import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class CustomDataSourceImpl extends DataSourceImpl implements CustomDataSource {

    private Set<String> schemas;

    public CustomDataSourceImpl(CustomReportDataSource customReportDataSource, Set<String> schemas, String dataSourceName, VirtualReportDataSource parentDataSource) {
        super(customReportDataSource, dataSourceName, parentDataSource);
        this.schemas = schemas;
    }

    /**
	 * Get all the selected schemas
	 * @return a set of schema that is going to use to build virtual data source
	 */
    public Set<String> getSchemas() {
        return schemas;
    }

	/**
	 * Get the persisted property values for this data source.
	 * The keys of this map correspond to the names of the properties in CustomDataSourceDefinition.getPropertyDefinitions()
	 * @return persisted property names and values
	 */
	public Map getPropertyMap() {
        return ((CustomReportDataSource)reportDataSource).getPropertyMap();
    }

	/**
	 * get the name of the class implementing ReportDataSourceService for this data source instance,
	 * which matches the value of getServiceClassName() on some instance of CustomDataSourceDefinition
	 * @return
	 */
	public String getServiceClass() {
         return ((CustomReportDataSource)reportDataSource).getServiceClass();
    }

}
