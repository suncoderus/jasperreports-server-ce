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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.TeiidDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class VirtualDataSourceConfig {

    private String dataSourceURI;
    private Set<String> excludedSchemaSet;
    private List<TeiidDataSource> additionalDataSourceList = new ArrayList<TeiidDataSource>();

    public String getDataSourceURI() {
        return dataSourceURI;
    }

    public void setDataSourceURI(String dataSourceURI) {
        this.dataSourceURI = dataSourceURI;
    }

    public Set<String> getExcludedSchemaSet() {
        return excludedSchemaSet;
    }

    /*
     * exclude certain schemas from the data source
     */
    public void setExcludedSchemaSet(Set<String> excludedSchemaSet) {
        this.excludedSchemaSet = excludedSchemaSet;
    }

    public List<TeiidDataSource> getAdditionalDataSourceList() {
        return additionalDataSourceList;
    }

    /*
     * set additional sub data sources for the virtual data source
     * these additional data sources would be stored in repository
      * it will gets constructed in runtime.
     */
    public void setAdditionalDataSourceList(List<TeiidDataSource> additionalDataSourceList) {
        this.additionalDataSourceList = additionalDataSourceList;
    }

    public boolean isSchemaExcluded(String schemaName) {
        if ((excludedSchemaSet != null) && excludedSchemaSet.contains(schemaName)) return true;
        return false;
    }
}
