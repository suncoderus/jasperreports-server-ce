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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.VirtualReportDataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Chna (ichan@jaspersoft.com)
 * @version $Id$
 */
public class VirtualReportDataSourceImpl extends ReportDataSourceImpl implements VirtualReportDataSource {

    private Set<String> schemas;
	private String timezone;
    private Map<String, ResourceReference> dataSourceUriMap = new HashMap<String, ResourceReference>();

    /**
     * returns a Map<String,ResourceReference> of data sources used by this virtual data source
     * @return a map of sub data sources
     */
    public Map<String, ResourceReference> getDataSourceUriMap() {
        return dataSourceUriMap;
    }

    /**
     * Sets a Map<String,ResourceReference> of data sources used by this virtual data source
     * @param dataSourceUriMap
     */
    public void setDataSourceUriMap(Map<String, ResourceReference> dataSourceUriMap) {
        this.dataSourceUriMap = dataSourceUriMap;
    }

    /*
     * Returns a set of selected schema for this virtual data sources
     * this information is not saved in repo, retrieve it in runtime when reading domain XML schema
     */
    public Set<String> getSchemas() {
        return schemas;
    }

     /*
     * set schema for virtual report data source in order to create smaller virtual data source in teiid
     * get the schema from SchemaImpl in runtime. do not save it in RepoVirtualDataSource
     */
    public void setSchemas(Set<String> schemas) {
        this.schemas = schemas;
    }

	/**
	 * Get a timezone associated with date and time values returned by the JDBC connection.
	 * This timezone should be one understood by java.util.Timezone.getTimezone();
	 * If the timezone is not null, values in result sets produced by this datasource
	 * will be adjusted by the offset between GMT and the corresponding Timezone.
	 * @return A string representing a timezone, or null if no time adjustment is desired
	 */
    public String getTimezone()
	{
		return timezone;
	}

	/**
	 * Set a timezone associated with date and time values returned by the JDBC connection.
	 * @param timezone A string representing a timezone, or null if no time adjustment is desired
	 */
	public void setTimezone(String timezone)
	{
		this.timezone = timezone;
	}

	protected Class getImplementingItf() {
		return VirtualReportDataSource.class;
	}

}
