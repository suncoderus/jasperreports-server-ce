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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;

import java.util.Map;

/**
 * Represents a persistent JasperServer repository data source associated with a custom data source 
 * defined in a CustomDataSourceDefinition.
 * Each CustomDataSourceDefinition corresponds to a particular implementation of ReportDataSourceService, and contains
 * metadata describing the properties that can be stored in the propertyMap of the persistent instances.
 * JasperServer just needs one implementation of this interface to support any number of CustomDataSourceDefinitions
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface CustomReportDataSource extends ReportDataSource {
	/**
	 * Get the persisted property values for this data source. 
	 * The keys of this map correspond to the names of the properties in CustomDataSourceDefinition.getPropertyDefinitions() 
	 * @return persisted property names and values
	 */
	public Map getPropertyMap();

	/**
	 * Set the persisted property values for this data source
	 * @param propertyMap persisted property names and values
	 */
	public void setPropertyMap(Map propertyMap);

	/**
	 * get the name of the class implementing ReportDataSourceService for this data source instance,
	 * which matches the value of getServiceClassName() on some instance of CustomDataSourceDefinition
	 * @return
	 */
	public String getServiceClass();

	/**
	 * set the name of the class implementing ReportDataSourceService for this data source instance.
	 * @param serviceClass class name of a ReportDataSourceService implementation
	 */
	public void setServiceClass(String serviceClass);

	/**
	 * @return
	 */
	public String getDataSourceName();
	
	public void setDataSourceName(String dataSourceName);

	Map<String, ResourceReference> getResources();
	void setResources(Map<String, ResourceReference> resources);
}
