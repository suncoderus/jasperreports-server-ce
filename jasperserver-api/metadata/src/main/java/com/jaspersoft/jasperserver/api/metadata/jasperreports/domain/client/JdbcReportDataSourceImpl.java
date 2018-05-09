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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JdbcReportDataSourceImpl extends ReportDataSourceImpl implements JdbcReportDataSource {

	private String driverClass;
	private String url;
	private String username;
	private String password;
	private String timezone;

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConnectionUrl() {
		return url;
	}

	public void setConnectionUrl(String url) {
		this.url = url;
	}

	public String getUsername() { return username; }

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTimezone()
	{
		return timezone;
	}

	public void setTimezone(String timezone)
	{
		this.timezone = timezone;
	}

	protected Class getImplementingItf() {
		return JdbcReportDataSource.class;
	}

}
