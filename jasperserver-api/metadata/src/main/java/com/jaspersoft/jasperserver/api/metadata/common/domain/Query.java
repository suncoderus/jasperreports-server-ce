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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

import java.util.List;


/**
 * The interface represents the property of
 * {@link com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl}
 * which type is query.
 * It extends {@link com.jaspersoft.jasperserver.api.metadata.common.domain.Resource}
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface Query extends Resource
{

    /**
     * Returns the reference to the data source where the query is being executed
     *
     * @return data source reference
     */    
	public ResourceReference getDataSource();
    
    /**
     * Sets the reference to the data source where the query is being executed
     *
     * @param dataSourceReference
     */	
	public void setDataSource(ResourceReference dataSourceReference);

        /**
     * Sets the data source where the query is being executed
     *
     * @param dataSource
     */
	public void setDataSource(ReportDataSource dataSource);

        /**
     * Sets the URI address to the data source where the query is being executed
     *
     * @param referenceURI
     */
	public void setDataSourceReference(String referenceURI);

	/**
     * Returns the SQL text of this query
     *
	 * @return SQL text
	 */
	public String getSql();

    /**
     * Sets the SQL text to this query
     *
     * @param sql SQL text
     */
	public void setSql(String sql);

    /**
     * Returns the SQL language of this query
     *
     * @return SQL language name
     */    
	public String getLanguage();

    /**
     * Sets the SQL language of this query
     *
     * @param language SQL language name
     */    
	public void setLanguage(String language);

    public void setParameters(List<QueryParameterDescriptor> descriptors);

    public List<QueryParameterDescriptor> getParameters();
}
