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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;

import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CustomReportDataSourceImpl extends ReportDataSourceImpl implements CustomReportDataSource {

	private String serviceClass;
	private String dataSourceName;
	private Map propertyMap;
	private Map<String, ResourceReference> resources;

	public Map<String, ResourceReference> getResources() {
		return resources;
	}

	public void setResources(Map<String, ResourceReference> resources) {
		this.resources = resources;
	}

	public Map getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map propertyMap) {
		this.propertyMap = propertyMap;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	public String getDataSourceName() {
		return dataSourceName;
	}

	protected Class getImplementingItf() {
		return CustomReportDataSource.class;
	}
}
