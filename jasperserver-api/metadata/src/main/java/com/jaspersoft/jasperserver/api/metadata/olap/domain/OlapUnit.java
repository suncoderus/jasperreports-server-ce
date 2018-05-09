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
package com.jaspersoft.jasperserver.api.metadata.olap.domain;

/**
 * @author sbirney
 *
 */
import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

@JasperServerAPI
public interface OlapUnit extends Resource {

    /*
     * OlapConnection
     */
    public ResourceReference getOlapClientConnection();

    public void setOlapClientConnection(OlapClientConnection olapConnection);

    public void setOlapClientConnection(ResourceReference olapConnectionReference);
	
    public void setOlapClientConnectionReference(String referenceURI);

    /*
     * MdxQuery
     */
    public String getMdxQuery();
	
    public void setMdxQuery(String query);
    
    /*
     * OlapViewSaveOptions
     */
    public Object getOlapViewOptions();
    
    public void setOlapViewOptions(Object options);
    

    /*
     * For Resource management
     */
    
	/**
	 * Returns the reference to the
	 * {@link com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapDataSource data source}
	 * used by this olap unit.
	 * 
	 * @return a reference to the data source used by this olap unit
	 */
	public ResourceReference getDataSource();
	
	public void setDataSource(ResourceReference dataSourceReference);
	
	public void setDataSource(ReportDataSource dataSource);
	
	public void setDataSourceReference(String referenceURI);
}

