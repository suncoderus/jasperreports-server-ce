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

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepoFont.java 2343 2006-03-10 14:54:32Z lucian $
 * 
 * @hibernate.joined-subclass table="DataType"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoDataType extends RepoResource
{

	/**
	 *
	 */
	private byte type = DataType.TYPE_TEXT;
	private Integer maxLength = null;
	private Integer decimals = null;
	private String regularExpr = null;
	private Comparable minValue = null;
	private Comparable maxValue = null;
	private boolean isStrictMin = false;
	private boolean isStrictMax = false;


	/**
	 * @hibernate.property
	 */
	public byte getType()
	{
		return type;
	}

	/**
	 * 
	 */
	public void setType(byte type)
	{
		this.type = type;
	}

	/**
	 * @hibernate.property
	 */
	public Integer getMaxLength()
	{
		return maxLength;
	}

	/**
	 * 
	 */
	public void setMaxLength(Integer maxLength)
	{
		this.maxLength = maxLength;
	}

	/**
	 * @hibernate.property
	 */
	public Integer getDecimals()
	{
		return decimals;
	}

	/**
	 * 
	 */
	public void setDecimals(Integer decimals)
	{
		this.decimals = decimals;
	}

	/**
	 * @hibernate.property
	 */
	public String getRegularExpr()
	{
		return regularExpr;
	}

	/**
	 * 
	 */
	public void setRegularExpr(String regularExpr)
	{
		this.regularExpr = regularExpr;
	}

	/**
	 * @hibernate.property type="serializable"
	 */
	public Comparable getMinValue()
	{
		return minValue;
	}

	/**
	 * 
	 */
	public void setMinValue(Comparable min)
	{
		this.minValue = min;
	}

	/**
	 * @hibernate.property type="serializable"
	 */
	public Comparable getMaxValue()
	{
		return maxValue;
	}

	/**
	 * 
	 */
	public void setMaxValue(Comparable max)
	{
		this.maxValue = max;
	}

	/**
	 * @hibernate.property
	 */
	public boolean isStrictMin()
	{
		return isStrictMin;
	}

	/**
	 * 
	 */
	public void setStrictMin(boolean isStrictMin)
	{
		this.isStrictMin = isStrictMin;
	}

	/**
	 * @hibernate.property
	 */
	public boolean isStrictMax()
	{
		return isStrictMax;
	}

	/**
	 * 
	 */
	public void setStrictMax(boolean isStrictMax)
	{
		this.isStrictMax = isStrictMax;
	}

	/**
	 *
	 */
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) 
	{
		super.copyFrom(clientRes, referenceResolver);
		
		DataType dataType = (DataType) clientRes;
		
		setType(dataType.getDataTypeType());
		setMaxLength(dataType.getMaxLength());
		setDecimals(dataType.getDecimals());
		setRegularExpr(dataType.getRegularExpr());
		setMinValue(dataType.getMinValue());
		setMaxValue(dataType.getMaxValue());
		setStrictMin(dataType.isStrictMin());
		setStrictMax(dataType.isStrictMax());
	}

	protected Class getClientItf() {
		return DataType.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory)
	{
		super.copyTo(clientRes, resourceFactory);

		DataType dataType = (DataType) clientRes;
		dataType.setDataTypeType(getType());
		dataType.setMaxLength(getMaxLength());
		dataType.setDecimals(getDecimals());
		dataType.setRegularExpr(getRegularExpr());
		dataType.setMinValue(getMinValue());
		dataType.setMaxValue(getMaxValue());
		dataType.setStrictMin(isStrictMin());
		dataType.setStrictMax(isStrictMax());
	}

}
