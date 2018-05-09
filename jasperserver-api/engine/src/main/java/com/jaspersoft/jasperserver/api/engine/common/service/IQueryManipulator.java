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

package com.jaspersoft.jasperserver.api.engine.common.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.Map;

/**
 * If the EngineService is provided with an instance of IQueryManipulator, it will call the
 * updateQuery() method to modify the query and/or its parameters before the query is actually run.
 * This is used by cascading input controls to modify filters whose values are provided by other input controls.
 * 
 * @author achan
 *
 */
@JasperServerAPI
public interface IQueryManipulator {

	/**
	 * Update the value list query and parameter map.
	 * 
	 * @param query Original value list query
	 * @param parameters Modifiable parameter map to be used by query
	 * @return modified query text
	 */
	public String updateQuery(String query, Map parameters);
}
