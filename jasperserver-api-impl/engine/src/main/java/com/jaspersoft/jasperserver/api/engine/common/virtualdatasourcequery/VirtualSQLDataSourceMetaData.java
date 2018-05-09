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
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery;


import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.ConnectionFactory;

import java.sql.SQLException;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ichan
 * Date: 7/19/12
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VirtualSQLDataSourceMetaData extends ConnectionFactory {

    // return schemas for this virtual data source
    public Set<String> getSchemas() throws SQLException;

    // return catalogs for this virtual data source
    public Set<String> getCatalogs() throws SQLException;

    // return database product name for this virtual data source
    public String getDatabaseProductName() throws SQLException;

    // return identifier quote string for this virtual data source
    public String getIdentifierQuoteString() throws SQLException;

    /*
    *  Teiid creates temp tables in the VDS and we should not display those temp tables in domain designer
    *  This method will go to each sub-data source and retrieve the original table list
    */
    public Set<String> getSubDSTableList(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException;

}
