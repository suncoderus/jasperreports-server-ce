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

package com.jaspersoft.jasperserver.api.metadata.olap.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import java.io.Serializable;


/**
 * Parameters for defining a Mondrian Connection. Used for cache management
 *
 * @author swood
 */
@JasperServerAPI
public class MondrianConnectionSchemaParameters implements Serializable {
    public String repositoryUri;
    public String catalogUri;
    public String jdbcConnectionString;
    public String jdbcUser;
    public String jndiDataSource;

    /**
     * Constructor for a JDBC connection
     *
     * @param repositoryUri URI for the Mondrian Connection in the repository
     * @param catalogUri URI to the schema for the connection in the repository
     * @param jdbcConnectionString JDBC connection string
     * @param jdbcUser User for the JDBC connection
     */
    public MondrianConnectionSchemaParameters(String repositoryUri, String catalogUri, String jdbcConnectionString, String jdbcUser) {
        this.repositoryUri = repositoryUri;
        this.catalogUri = catalogUri;
        this.jdbcConnectionString = jdbcConnectionString;
        this.jdbcUser = jdbcUser;
    }

    /**
     * Constructor for a JNDI connection
     *
     * @param repositoryUri URI for the Mondrian Connection in the repository
     * @param catalogUri URI to the schema for the connection in the repository
     * @param jndiDataSource JNDI reference
     */
    public MondrianConnectionSchemaParameters(String repositoryUri, String catalogUri, String jndiDataSource) {
        this.repositoryUri = repositoryUri;
        this.catalogUri = catalogUri;
        this.jndiDataSource = jndiDataSource;
    }

    /**
     * Display string.
     *
     * @return toString
     */
    @Override
    public String toString() {
        return "Schema connection parameters: repositoryUri: " + repositoryUri + ", catalogUri: " + catalogUri +
               (jndiDataSource != null ? ", JNDI data source: " + jndiDataSource
                                        : ", JDBC connect string: " + jdbcConnectionString + ", jdbcUser: " + jdbcUser);
    }
}
