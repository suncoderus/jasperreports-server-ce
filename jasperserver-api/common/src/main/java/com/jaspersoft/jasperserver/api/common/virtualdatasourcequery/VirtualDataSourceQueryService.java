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
package com.jaspersoft.jasperserver.api.common.virtualdatasourcequery;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.Collection;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
@JasperServerAPI
public interface VirtualDataSourceQueryService {

    /*
     * a hook to replace teiid with other virtual data source service (like JDBCUnity)
     * jasperserver engine is going to provide a list of sub data sources and connection information
     * virtualDataSourceQueryService is supposed to build a virtual data source and return a connection factory
     * @param dataSources a collection of sub data sources
     * @return connection factory for virtual data source
     */
    public ConnectionFactory getConnectionFactory(Collection<DataSource> dataSources) throws Exception;

        /*
     * a hook to replace teiid with other virtual data source service (like JDBCUnity)
     * jasperserver engine is going to provide a list of sub data sources and connection information
     * virtualDataSourceQueryService is supposed to build a virtual data source and return a connection factory
     * @param dataSources a collection of sub data sources
     * @param virtualDataSourceUri uri for virtual data source object  (use it for picking up additional data source if dataSources list is empty)
     * @return connection factory for virtual data source
     */
    public ConnectionFactory getConnectionFactory(Collection<DataSource> dataSources, String virtualDataSourceUri) throws Exception;
}
