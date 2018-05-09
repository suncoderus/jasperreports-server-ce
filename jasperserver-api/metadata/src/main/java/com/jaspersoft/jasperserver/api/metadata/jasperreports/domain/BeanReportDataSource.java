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

/**
 * A JasperServer repository data source which is associated with a JavaBean object 
 * defined in one of JasperServer's configuration file.
 * The bean can be a ReportDataSourceService itself, or it can have a method (designated by the beanMethod property)
 * which can be called to get a ReportDataSourceService.
 * 
 * @author swood
 *
 */
@JasperServerAPI
public interface BeanReportDataSource extends ReportDataSource {
	/**
	 * Gets the name or id identifying the bean which supplies the data source in any of JasperServer's Spring configuration files
	 * (WEB-INF/applicationContext*.xml)
	 * @return
	 */
	public String getBeanName();
	/**
	 * Set the name of the bean supplying the data source
	 * @param beanName
	 */
	public void setBeanName(String beanName);
	
	/**
	 * Get the name of the bean method which returns a ReportDataSourceService for this bean data source.
	 * If null, the bean itself is a ReportDataSourceService.
	 * @return
	 */
	public String getBeanMethod();
	/**
	 * Set the name of the bean method (if any) which returns a ReportDataSourceService for this data source.
	 * @return
	 */
	public void setBeanMethod(String beanMethod);
}
