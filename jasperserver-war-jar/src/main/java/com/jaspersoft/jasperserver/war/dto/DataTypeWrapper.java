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

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;

public class DataTypeWrapper extends BaseDTO
{

	private DataType dataType;
	private ResourceLookup[] allDataTypes;
	private String minValueText;
	private String maxValueText;
	
	public DataTypeWrapper(DataType dataType)
	{
		this.dataType = dataType;
	}


	public DataType getDataType()
	{
		return dataType;
	}

	public void setDataType(DataType dataType)
	{
		this.dataType = dataType;
	}

	public ResourceLookup[] getAllDataTypes()
	{
		return allDataTypes;
	}

	public void setAllDataTypes(ResourceLookup[] allDataTypes)
	{
		this.allDataTypes = allDataTypes;
	}


	/**
	 * @return Returns the minValueText.
	 */
	public String getMinValueText() {
		return minValueText;
	}


	/**
	 * @param minValueText The minValueText to set.
	 */
	public void setMinValueText(String minValueText) {
		this.minValueText = minValueText;
	}


	/**
	 * @return Returns the maxValueText.
	 */
	public String getMaxValueText() {
		return maxValueText;
	}


	/**
	 * @param maxValueText The maxValueText to set.
	 */
	public void setMaxValueText(String maxValueText) {
		this.maxValueText = maxValueText;
	}
}
