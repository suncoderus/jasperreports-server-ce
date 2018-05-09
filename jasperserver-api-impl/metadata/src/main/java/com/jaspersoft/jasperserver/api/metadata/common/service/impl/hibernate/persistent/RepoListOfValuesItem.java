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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoQuery.java 2343 2006-03-10 14:54:32Z lucian $
 * 
 * @hibernate.joined-subclass table="ListOfValuesItem"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoListOfValuesItem
{
	/**
	 *
	 */
	private long id;
	private String label;
	private Object value;

	/**
	 * @hibernate.id generator-class="identity"
	 */
	public long getId() 
	{
		return id;
	}

	/**
	 *
	 */
	public void setId(long id) 
	{
		this.id = id;
	}

	/**
	 * @hibernate.property
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 *
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @hibernate.property type="serializable"
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 *
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

}
