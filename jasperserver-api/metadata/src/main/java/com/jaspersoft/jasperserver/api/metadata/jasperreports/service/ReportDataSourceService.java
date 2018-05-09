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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.Map;


/**
 * A service associated with a ReportDataSource which allocates and disposes of any 
 * resources needed to generate the JRDataSource. See {@link ReportDataSource} for details.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface ReportDataSourceService {
	
	/**
	 * Initialize resources needed by the JRQueryExecuter to create a JRDataSource, and
	 * put them in the map. The keys can be any string value except for predefined JR parameter values
	 * such as REPORT_MAX_COUNT.
     * If the datasource does not support queries, it adds a JRDataSource directly to the map under the <code>REPORT_DATA_SOURCE</code> key.
	 * @param parameterValues map of parameters to be used for running a JasperReport
	 */
	void setReportParameterValues(Map parameterValues);
	
	/**
	 * Clean up any resources allocated by {{@link #setReportParameterValues(Map)}
	 */
	void closeConnection();

}
