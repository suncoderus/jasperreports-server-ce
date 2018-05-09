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
 * Represents a persistent JasperServer repository data source which corresponds with a
 * JDBC data source that is registered with the JNDI service
 * of the application server in which JasperServer is run.
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface JndiJdbcReportDataSource extends ReportDataSource {
	
	/**
	 * Get the name used to obtain the javax.sql.DataSource associated with this ReportDataSource.
	 * This name is prepended with the string "java:comp/env/" and passed to javax.naming.Context.lookup(),
	 * which should return a DataSource.
	 * @return name of a JNDI data source registered with the application server
	 */
	String getJndiName();
	
	/**
	 * Set the name used to obtain the javax.sql.DataSource associated with this ReportDataSource.
	 * @param jndiName name of a JNDI data source registered with the application server
	 */
	void setJndiName(String jndiName);

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
