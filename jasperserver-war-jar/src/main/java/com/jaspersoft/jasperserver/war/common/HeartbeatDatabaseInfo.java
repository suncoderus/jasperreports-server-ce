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
package com.jaspersoft.jasperserver.war.common;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HibernateLoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public class HeartbeatDatabaseInfo extends HeartbeatInfo
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String databaseName = null;
	private String databaseVersion = null;

	/**
	 * @return Returns the databaseName.
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * @param databaseName The databaseName to set.
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * @return Returns the databaseVersion.
	 */
	public String getDatabaseVersion() {
		return databaseVersion;
	}

	/**
	 * @param databaseVersion The databaseVersion to set.
	 */
	public void setDatabaseVersion(String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public void contributeToHttpCall(HeartbeatCall call)
	{
		call.addParameter("repoDbName[]", getDatabaseName() == null ? "" : getDatabaseName());
		call.addParameter("repoDbVersion[]", getDatabaseVersion() == null ? "" : getDatabaseVersion());
		call.addParameter("repoDbCount[]", String.valueOf(getCount()));
	}

	public String getKey()
	{
		return 
			getDatabaseName() 
			+ "|" + getDatabaseName();
	}

}
