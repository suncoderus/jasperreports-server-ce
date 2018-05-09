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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributor;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoRepositoryService.java 45608 2014-05-12 09:49:27Z lchirita $
 */
public class DataSourceWrapperParameterContributor implements ParameterContributor
{
	/**
	 *
	 */
	public static final String PROPERTY_DATA_SOURCE_LOCATION = "com.jaspersoft.jrs.data.source";

	private ReportDataSourceService reportDataSourceService;
			

	public DataSourceWrapperParameterContributor(ReportDataSourceService reportDataSourceService) 
	{
		this.reportDataSourceService = reportDataSourceService;
	}

	public void contributeParameters(Map<String, Object> parameterValues) throws JRException 
	{
		reportDataSourceService.setReportParameterValues(parameterValues);
	}

	public void dispose() 
	{
		reportDataSourceService.closeConnection();
	}
}
