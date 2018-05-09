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

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

/**
 * @author swood
 *
 */
@JasperServerAPI
public interface XMLAConnection extends OlapClientConnection, ReportDataSource {
	
    public static final String TENANT_ID_EQ = "TenantID=";

    /**
     * URI for XML/A service, like "http://localhost:8080/jpivot/xmla"
     */
    public String getURI();
    public void setURI(String uri);
    
    /**
     * XML/A Data Source, like "Provider=Mondrian;DataSource=MondrianFoodMart;"
     * 
     */
    public String getDataSource();
    public void setDataSource(String xmlaDataSource);
    
    /**
     * Catalog within the Data Source, like "FoodMart"
     * 
     */
    public String getCatalog();
    public void setCatalog(String catalog);

    /**
     * username for HTTP authentication of XMLA
     * 
     */
    public String getUsername();
    public void setUsername(String username);

    /**
     * password for HTTP authentication of XMLA
     * 
     */
    public String getPassword();
    public void setPassword(String password);


}
