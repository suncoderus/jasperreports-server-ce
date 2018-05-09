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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

/**
 * A factory associated with a specific implementation class of {@link ReportDataSource}. 
 * It is responsible for allocating the appropriate {@link ReportDataSourceService) based on 
 * the contents of the ReportDataSource.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface ReportDataSourceServiceFactory {
	
	/**
	 * Create a ReportDataSourceService based on the properties of the data source.
	 * @param dataSource a repository data source
	 * @return
	 */
	ReportDataSourceService createService(ReportDataSource dataSource);
}
