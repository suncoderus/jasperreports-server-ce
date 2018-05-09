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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;

/**
 * @author sbirney
 *
 */
//public class OlapUnitImpl extends ResourceImpl implements OlapUnit {
public class OlapUnitImpl extends ResourceImpl implements OlapUnit {

    private String mdxQuery;
    private ResourceReference olapClientConnection;
    private Object olapViewOptions;
    

    /*
     * OlapConnection
     */
    public ResourceReference getOlapClientConnection() {
	return olapClientConnection;
    }

    public void setOlapClientConnection(OlapClientConnection olapConnection) {
	setOlapClientConnection(new ResourceReference(olapConnection));
    }

    public void setOlapClientConnection(ResourceReference olapConnectionReference) {
	olapClientConnection = olapConnectionReference;
    }
	
    public void setOlapClientConnectionReference(String referenceURI) {
	setOlapClientConnection(new ResourceReference(referenceURI));
    }

    /*
     * MdxQuery
     */
    public String getMdxQuery() {
	return mdxQuery;
    }
	
    public void setMdxQuery(String query) {
	mdxQuery = query;
    }
    
    /*
     * OlapViewSaveOptions
     */
    public Object getOlapViewOptions() {
    	return olapViewOptions;
    }
    
    public void setOlapViewOptions(Object options) {
    	olapViewOptions = options;
    }


    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceImpl#getImplementingItf()
     */
    protected Class getImplementingItf() {
	return OlapUnit.class;
    }

    /*
     * For Resource maintenance 
     */
    
	private ResourceReference dataSource = null;
	
	public ResourceReference getDataSource()
	{
		return dataSource;
	}
	
	/**
	 * 
	 */
	public void setDataSource(ResourceReference dataSource)
	{
		this.dataSource = dataSource;
	}

	public void setDataSource(ReportDataSource dataSource) {
		setDataSource(new ResourceReference(dataSource));
	}

	
	public void setDataSourceReference(String referenceURI) {
		setDataSource(new ResourceReference(referenceURI));
	}
}
