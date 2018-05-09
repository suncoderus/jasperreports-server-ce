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

import java.io.Serializable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HibernateLoggingService.java 8408 2007-05-29 23:29:12Z melih $
 */
public abstract class HeartbeatInfo implements HeartbeatContributor, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int count = 0;

	/**
	 * 
	 */
	public int getCount() 
	{
		return count;
	}
	
	/**
	 * 
	 */
	public void incrementCount() {
		count++;
	}
	
	/**
	 * 
	 */
	public abstract String getKey();
	
	/**
	 * 
	 */
	public int hashCode()
	{
		return getKey().hashCode();
	}
	
	/**
	 * 
	 */
	public boolean equals(Object obj) 
	{
		HeartbeatInfo info = obj instanceof HeartbeatInfo ? (HeartbeatInfo)obj : null;
		return (info != null && getKey().equals(info.getKey()));
	}
}
