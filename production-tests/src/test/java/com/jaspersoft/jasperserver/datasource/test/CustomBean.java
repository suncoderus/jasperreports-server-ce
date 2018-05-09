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
package com.jaspersoft.jasperserver.datasource.test;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: CustomBean.java 21149 2011-10-04 22:21:02Z srosen $
 */
public class CustomBean
{


	/**
	 *
	 */
	private String city = null;
	private Integer id = null;
	private String name = null;
	private String street = null;


	/**
	 *
	 */
	public CustomBean(
		String pcity,
		Integer pid,
		String pname,
		String pstreet
		)
	{
		city = pcity;
		id = pid;
		name = pname;
		street = pstreet;
	}


	/**
	 *
	 */
	public CustomBean getMe()
	{
		return this;
	}


	/**
	 *
	 */
	public String getCity()
	{
		return city;
	}


	/**
	 *
	 */
	public Integer getId()
	{
		return id;
	}


	/**
	 *
	 */
	public String getName()
	{
		return name;
	}


	/**
	 *
	 */
	public String getStreet()
	{
		return street;
	}


}
