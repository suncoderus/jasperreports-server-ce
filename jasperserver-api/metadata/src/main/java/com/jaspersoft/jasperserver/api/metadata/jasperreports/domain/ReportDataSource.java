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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataSource;


/**
 * The base interface for any object stored in the JasperServer repository which can be 
 * used to provide a JRDataSource, either by itself or in conjunction with a JRQueryExecuter.
 * All 	{@link com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit ReportUnit} instances 
 * may have a ReportDataSource associated with them, although this is not their only use.
 * The steps that JasperServer takes to get a JRDataSource from a ReportDataSource are as follows:
 * <ul>
 * <li>JasperServer calls EngineService.createDataSourceService() with a ReportDataSource instance
 * <ul>
 * <li>EngineService looks up the ReportDataSourceServiceFactory associated with this ReportDataSource.
 * <li>{@link com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory#createService(ReportDataSource) ReportDataSourceServiceFactory.createService()} 
 * is called to return a {@link com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService ReportDataSourceService}
 * </ul>
 * <li> {@link com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService#setReportParameterValues(java.util.Map) ReportDataSourceService.setReportParameterValues()} 
 * is called, which can act in two different ways:
 * <ul>
 * <li> If the datasource does not support queries, it adds a JRDataSource directly to the map under the <code>REPORT_DATA_SOURCE</code> key. This JRDataSource is then used directly during the JasperReport fill process.
 * <li> If the datasource supports queries, it adds any parameters expected by the JRQueryExecuter. During the JasperReport fill process, the report parameters are passed to JRQueryExecuterFactory.createQueryExecuter().
 * </ul>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: AbstractAttributedObject.java 2140 2006-02-21 06:41:21Z tony $
 */
@JasperServerAPI
public interface ReportDataSource extends DataSource
{
	
}
