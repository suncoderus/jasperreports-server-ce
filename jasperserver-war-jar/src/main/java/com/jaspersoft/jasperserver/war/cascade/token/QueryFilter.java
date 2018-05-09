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
package com.jaspersoft.jasperserver.war.cascade.token;

import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.IDataSourceAwareQueryManipulator;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

/**
 * QueryFilter
 * @author jwhang
 * @version $Id$
 */
public class QueryFilter implements IDataSourceAwareQueryManipulator {
    private FilterCore filterResolver;

    public QueryFilter(){
    }

    public FilterCore getFilterResolver() {
        return filterResolver;
    }

    public void setFilterResolver(FilterCore filterResolver) {
        this.filterResolver = filterResolver;
    }


    /**
     * try to update parameter map with built-in params
     * if we have missing params, return empty query which will get bypassed
     */
    public String updateQuery(String queryString, Map parameters, ReportDataSource dataSource) {
    	/**
    	 * put the datasource URI into the param map to make these queries cacheable
    	 */
    	if (dataSource != null) {
    		parameters.put(EngineService.JS_DATASOURCE_URI, dataSource.getURIString());
    	}
        Map resolvedParams = getFilterResolver().resolveParameters(queryString, parameters);
        if (resolvedParams == null) {
            queryString = "";
        } else {
            parameters.putAll(resolvedParams);
        }
        return queryString;
    }

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.engine.common.service.IDataSourceAwareQueryManipulator#updateQuery(java.lang.String, java.util.Map, com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	@Override
	public String updateQuery(String query, Map parameters) {
		return updateQuery(query, parameters, null);
	}

}
