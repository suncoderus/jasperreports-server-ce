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
package com.jaspersoft.jasperserver.datasource.test;

import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * @author swood
 *
 */
public class CustomDataSourceServiceFactory {

	public ReportDataSourceService plainDataSource() {
		return new CustomDataSourceService(new CustomDataSource());
	}
	public ReportDataSourceService tableModelDataSource() {
		return new CustomDataSourceService(new JRTableModelDataSource(new CustomTableModel()));
	}
	public ReportDataSourceService beanArrayDataSource() {
		return new CustomDataSourceService(new JRBeanArrayDataSource(CustomBeanFactory.getBeanArray()));
	}
	public ReportDataSourceService beanCollectionDataSource() {
		return new CustomDataSourceService(new JRBeanCollectionDataSource(CustomBeanFactory.getBeanCollection()));
	}
	
	
}
