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
 * Represents a persistent JasperServer repository data source accessible through JDBC.
 * Implementations are responsible for storing all the information needed to create a 
 * JDBC connection.
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface JdbcReportDataSource extends ReportDataSource {
	
	/**
	 * Get the name of the JDBC driver required to create this JDBC connection
	 * @return name of a JDBC class loadable by JasperServer
	 */
	String getDriverClass();
	
	/**
	 * Get the URL specifying the JDBC connection
	 * @return URL string compatible with the JDBC driver
	 */
	String getConnectionUrl();
	
	/**
	 * Get the username used to authenticate the JDBC connection
	 * @return username for JDBC connection
	 */
	String getUsername();
	
	/**
	 * Get the password used to authenticate the JDBC connection
	 * @return password for JDBC connection
	 */
	String getPassword();
	
	/**
	 * Set the name of the JDBC driver required to create this JDBC connection
	 * @param driverClass name of a JDBC class loadable by JasperServer
	 */
	void setDriverClass(String driverClass);
	
	/**
	 * Set the URL specifying the JDBC connection
	 * param url URL string compatible with the JDBC driver
	 */
	void setConnectionUrl(String url);
	
	/**
	 * Set the username used to authenticate the JDBC connection
	 * @param name username for JDBC connection
	 */
	void setUsername(String name);
	
	/**
	 * Set the password used to authenticate the JDBC connection
	 * @param password password for JDBC connection
	 */
	void setPassword(String password);

	/**
	 * Get a timezone associated with date and time values returned by the JDBC connection.
	 * This timezone should be one understood by java.util.Timezone.getTimezone();
	 * If the timezone is not null, values in result sets produced by this datasource
	 * will be adjusted by the offset between GMT and the corresponding Timezone.
	 * @return A string representing a timezone, or null if no time adjustment is desired
	 */
	String getTimezone();

	/**
	 * Set a timezone associated with date and time values returned by the JDBC connection.
	 * @param timezone A string representing a timezone, or null if no time adjustment is desired
	 */
	void setTimezone(String timezone);
}
