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

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 *
 * @hibernate.joined-subclass table="InputControl"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoInputControl extends RepoResource
{

	/**
	 * 
	 */
	private byte type = InputControl.TYPE_SINGLE_VALUE;
	private boolean isMandatory = false;
	private boolean isReadOnly = false;
	private boolean isVisible = true;
	private RepoDataType dataType = null;
	private RepoListOfValues listOfValues = null;
	private RepoQuery query = null;
	private List queryVisibleColumns = new ArrayList();
	private String queryValueColumn = null;
	private Object defaultValue = null;
	private List defaultValues = null;


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
	public boolean isMandatory()
	{
		return isMandatory;
	}

	/**
	 * 
	 */
	public void setMandatory(boolean isMandatory)
	{
		this.isMandatory = isMandatory;
	}

	/**
	 * @hibernate.property
	 */
	public boolean isReadOnly()
	{
		return isReadOnly;
	}

	/**
	 * 
	 */
	public void setReadOnly(boolean isReadOnly)
	{
		this.isReadOnly = isReadOnly;
	}

	/**
	 *
	 */
	public boolean isVisible()
	{
		return isVisible;
	}

	/**
	 *
	 */
	public void setVisible(boolean visible)
	{
		isVisible = visible;
	}

	/**
	 * @hibernate.many-to-one
	 * 		column="data_type"
	 */
	public RepoDataType getDataType()
	{
		return dataType;
	}

	/**
	 * 
	 */
	public void setDataType(RepoDataType dataType)
	{
		this.dataType = dataType;
	}

	/**
	 * @hibernate.many-to-one
	 * 		column="list_of_values"
	 */
	public RepoListOfValues getListOfValues()
	{
		return listOfValues;
	}

	/**
	 * 
	 */
	public void setListOfValues(RepoListOfValues values)
	{
		this.listOfValues = values;
	}

	/**
	 * @hibernate.many-to-one
	 * 		column="list_query"
	 */
	public RepoQuery getQuery()
	{
		return query;
	}

	/**
	 * 
	 */
	public void setQuery(RepoQuery query)
	{
		this.query = query;
	}

	/**
	 * @hibernate.list table="InputControlQueryColumn"
	 * @hibernate.key column="input_control_id"
	 * @hibernate.element column="query_column" type="string" length="40" not-null="true"
	 * @hibernate.list-index column="column_index"
	 */
	public List getQueryVisibleColumns()
	{
		return queryVisibleColumns;
	}

	/**
	 * 
	 */
	public void setQueryVisibleColumns(List queryVisibleColumns)
	{
		this.queryVisibleColumns = queryVisibleColumns;
	}

	/**
	 * @hibernate.property
	 * 		column="query_value_column" type="string" length="40"
	 */
	public String getQueryValueColumn()
	{
		return queryValueColumn;
	}

	/**
	 * 
	 */
	public void setQueryValueColumn(String column)
	{
		this.queryValueColumn = column;
	}

	/**
	 * @hibernate.property type="serializable"
	 */
	public Object getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * 
	 */
	public void setDefaultValue(Object value)
	{
		this.defaultValue = value;
	}

	/**
	 *
	 */
	public List getDefaultValues() //FIXME persist this
	{
		return defaultValues;
	}

	/**
	 * 
	 */
	public void setDefaultValues(List values)
	{
		this.defaultValues = values;
	}

	protected Class getClientItf() {
		return InputControl.class;
	}

	protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
		super.copyTo(clientRes, resourceFactory);

		InputControl control = (InputControl) clientRes;
		
		control.setInputControlType(getType());
		control.setMandatory(isMandatory());
		control.setReadOnly(isReadOnly());
		control.setVisible(isVisible());
		
		control.setDataType(getClientReference(getDataType(), resourceFactory));
		control.setListOfValues(getClientReference(getListOfValues(), resourceFactory));
		control.setQuery(getClientReference(getQuery(), resourceFactory));

		control.setQueryValueColumn(getQueryValueColumn());
		for (int i = 0; i < queryVisibleColumns.size(); i++)
			control.addQueryVisibleColumn((String) queryVisibleColumns.get(i));

		control.setDefaultValue(getDefaultValue());
		//FIXME defaultValues
	}

	/**
	 * 
	 */
	protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) 
	{
		super.copyFrom(clientRes, referenceResolver);
		
		InputControl control = (InputControl) clientRes;
		
		setType(control.getInputControlType());
		setMandatory(control.isMandatory());
		setReadOnly(control.isReadOnly());
		setVisible(control.isVisible());

		switch(control.getInputControlType()) {
			case InputControl.TYPE_BOOLEAN:
				setDataType(null);
				setListOfValues(null);
				setQuery(null);
				break;
			case InputControl.TYPE_SINGLE_VALUE:
			case InputControl.TYPE_MULTI_VALUE:
				setDataType((RepoDataType) getReference(control.getDataType(), RepoDataType.class, referenceResolver));
				setListOfValues(null);
				setQuery(null);
				break;
			case InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES:
			case InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES:
			case InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO:
			case InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX:
				setListOfValues((RepoListOfValues) getReference(control.getListOfValues(), RepoListOfValues.class, referenceResolver));
				setDataType(null);
				setQuery(null);
				break;
			case InputControl.TYPE_SINGLE_SELECT_QUERY:
			case InputControl.TYPE_MULTI_SELECT_QUERY:
			case InputControl.TYPE_SINGLE_SELECT_QUERY_RADIO:
			case InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX:
				setQuery((RepoQuery) getReference(control.getQuery(), RepoQuery.class, referenceResolver));
				setDataType(null);
				setListOfValues(null);
				break;
		}

		setQueryValueColumn(control.getQueryValueColumn());
		setQueryVisibleColumns(control.getQueryVisibleColumnsAsList());
		setDefaultValue(control.getDefaultValue());
		//FIXME defaultValues
	}

}
