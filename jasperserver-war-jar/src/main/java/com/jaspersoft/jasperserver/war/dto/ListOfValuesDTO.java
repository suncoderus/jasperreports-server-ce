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
package com.jaspersoft.jasperserver.war.dto;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class ListOfValuesDTO extends BaseDTO
{
	private ListOfValues listOfValues;
	private String newLabel;
	private String newValue;

	public ListOfValuesDTO(ListOfValues listOfValues)
	{
		this.listOfValues = listOfValues;
	}

	public String getNewLabel()
	{
		return newLabel;
	}

	public void setNewLabel(String newLabel)
	{
		this.newLabel = newLabel;
	}

	public String getNewValue()
	{
		return newValue;
	}

	public void setNewValue(String newValue)
	{
		this.newValue = newValue;
	}

	public ListOfValues getListOfValues()
	{
		return listOfValues;
	}

	public void setListOfValues(ListOfValues listOfValues)
	{
		this.listOfValues = listOfValues;
	}
}
