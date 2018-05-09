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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author bob
 *
 */
public interface FilterResolver {

	/**
	 * does the query have params?
	 * @param queryString
	 * @param parameters
	 * @return
	 */
	boolean hasParameters(String queryString, Map parameters);



    /**
     * Resolves the parameters which are used in query
     * @param queryString
     * @return list of parameters
     */
    public Set<String> getParameterNames(String queryString, Map<String, Object> providedParameters);


	/**
	 * return string with parameters substituted for displayable values.
	 * Update given parameters with LoggedInUser values as requested
	 * 
	 * @param queryString
	 * @param providedParameters
	 * @return map of params used to resolve query
	 */
	Map<String, Object> resolveParameters(String queryString, Map<String, Object> providedParameters);

	/**
	 * Get a key to use for caching the query results 
	 * @param rawQueryString
	 * @param parameterValues
	 * @return
	 */
	Object getCacheKey(String rawQueryString, Map<String, Object> parameterValues);

	/**
	 * Get a key to use for caching the query results
	 * @param rawQueryString
	 * @param parameterValues
	 * @param keyColumn
	 * @param resultColumns
	 * @return
	 */
	Object getCacheKey(String rawQueryString, Map<String, Object> parameterValues, String keyColumn, String[] resultColumns);

	/**
	 * This tells caller that we need the ReportDataSourceService.setReportParameterValues()
	 * run before we can look at parameters. This is only required for domain data sources.
	 * @param dataSource 
	 * @return true to call setReportParameterValues() before calling any other methods
	 */
	boolean paramTestNeedsDataSourceInit(ReportDataSource dataSource);

    /**
     * Returns a set with names ordered regarding supplied dependencies.
     * Used for Input Controls and JRParameters.
     * @param masterDependencies A map which contains names and their master dependencies.
     * @return Ordered set of names
     */
    public LinkedHashSet<String> resolveCascadingOrder(Map<String, Set<String>> masterDependencies);

}