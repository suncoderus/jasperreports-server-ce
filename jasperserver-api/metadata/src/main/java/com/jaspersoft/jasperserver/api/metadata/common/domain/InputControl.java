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
package com.jaspersoft.jasperserver.api.metadata.common.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.List;



/**
 * The interface for JasperServer input control.
 * It extends {@link com.jaspersoft.jasperserver.api.metadata.common.domain.Resource}
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl
 */
@JasperServerAPI
public interface InputControl extends Resource, ReferenceDescriptor
{

    public static final byte TYPE_BOOLEAN = 1;
	public static final byte TYPE_SINGLE_VALUE = 2;
	public static final byte TYPE_SINGLE_SELECT_LIST_OF_VALUES = 3;
	public static final byte TYPE_SINGLE_SELECT_QUERY = 4;
	public static final byte TYPE_MULTI_VALUE = 5;
	public static final byte TYPE_MULTI_SELECT_LIST_OF_VALUES = 6;
	public static final byte TYPE_MULTI_SELECT_QUERY = 7;
	public static final byte TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO = 8;
	public static final byte TYPE_SINGLE_SELECT_QUERY_RADIO = 9;
	public static final byte TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX = 10;
	public static final byte TYPE_MULTI_SELECT_QUERY_CHECKBOX = 11;


    /**
     * Returns the numeric type of the input control
     *
     * @return numeric type
     */
	public byte getInputControlType();

    /**
     * Sets the type for the input control
     *
     * @param type numeric type
     */
	public void setInputControlType(byte type);

    /**
     * Shows if the input control should have some value for report layout. It means
     * that the report can not be drawn if value of this input control is not set.
     *
     * @return  <code>true</code> if the input control is mandatory
     */
	public boolean isMandatory();

	/**
     * Sets the mandatory sign for input control. If set to  <code>true</code> then
     * input control should have some value for report layout
     *
	 * @param isMandatory
	 */
	public void setMandatory(boolean isMandatory);

	/**
     * Shows if the input control value can be changed when running the report
     *
     * @return  <code>true</code> if the input control value can be changed when running the report
	 */
	public boolean isReadOnly();

	/**
     * Makes the input control "read only" or not.
	 *
     * @param isReadOnly if set to <code>true</code> then the input control
     * value can not be changed when running the report
	 */
	public void setReadOnly(boolean isReadOnly);

    /**
     * If returns <code>true</code> then the input control will appear on report layout, otherwize
     * it will not be shown.
     *
     * @return  <code>true</code> if the input control is visible on report layout
     */
	public boolean isVisible();

    /**
     * Makes the input control visible on report layout or not.
     *
     * @param isVisible if set to <code>true</code> then the input control
     * will appear on report layout
     */
	public void setVisible(boolean isVisible);

	/**
	 * Returns the reference to the
	 * {@link DataType data type}
	 * of this input control.
	 *
	 * @return a reference to the data type used by this input control
	 */
	public ResourceReference getDataType();

    /**
     * Sets the reference to the
     * {@link DataType data type}
     * of this input control.
     *
     * @param dataTypeReference a reference to the data type used by this input control
     */
	public void setDataType(ResourceReference dataTypeReference);

    /**
     * Sets the {@link DataType data type}
     * of this input control.
     *
     * @param dataType a data type used by this input control
     */
	public void setDataType(DataType dataType);

    /**
     * Sets the reference URI to the
     * {@link DataType data type}
     * of this input control.
     *
     * @param referenceURI a reference URI to the data type used by this input control
     */
	public void setDataTypeReference(String referenceURI);

	/**
	 * Returns the reference to the
	 * {@link ListOfValues list of values}
	 * used by this input control.
	 *
	 * @return a reference to the list of values used by this input control
	 */
	public ResourceReference getListOfValues();

    /**
     * Sets the reference to the
     * {@link ListOfValues list of values}
     * used by this input control.
     *
     * @param listOfValuesReference a reference to the list of values used by this input control
     */
	public void setListOfValues(ResourceReference listOfValuesReference);

    /**
     * Sets the {@link ListOfValues list of values}
     * used by this input control.
     *
     * @param listOfValues a list of values used by this input control
     */
	public void setListOfValues(ListOfValues listOfValues);

    /**
     * Sets the reference URI to the
     * {@link ListOfValues list of values}
     * used by this input control.
     *
     * @param referenceURI a reference URI to the list of values used by this input control
     */
	public void setListOfValuesReference(String referenceURI);

	/**
	 * Returns the reference to the
	 * {@link Query query}
	 * used by this input control.
	 *
	 * @return a reference to the query used by this input control
	 */
	public ResourceReference getQuery();

    /**
     * Sets the reference to the
     * {@link Query query}
     * used by this input control.
     *
     * @param query a reference to the query used by this input control
     */
	public void setQuery(ResourceReference query);

    /**
     * Sets the {@link Query query}
     * used by this input control.
     *
     * @param query the query used by this input control
     */
	public void setQuery(Query query);

    /**
     * Sets the reference URI to the
     * {@link Query query}
     * used by this input control.
     *
     * @param referenceURI a reference URI to the query used by this input control
     */
	public void setQueryReference(String referenceURI);

    /**
     * Returns the array of table columns which will appear concatenated
     * in this input control (if it is a query type input control)
     * for each row returned from the database table.
     *
     * @return an array of table columns
     */
	public String[] getQueryVisibleColumns();

	/**
     * Adds one more item (table column) to the string which will appear
     * for each row returned from the database table.
     *
	 * @param column name of new item
	 */
	public void addQueryVisibleColumn(String column);

	/**
     * Removes a specified item (column) from the visible string
     * of input control list.
	 *
     * @param column name of removing column
	 */
	public void removeQueryVisibleColumn(String column);

	/**
     * Returns a column name which is used for input control value.
	 *
     * @return a column name
	 */
	public String getQueryValueColumn();

    /**
     * Sets a column name which is used for input control value.
     *
     * @param column a column name
     */
	public void setQueryValueColumn(String column);

	/**
     * Returns the default value of this input control.
	 *
     * @return default input control value
	 */
	public Object getDefaultValue();

    /**
     * Sets the default value for this input control.
     *
     * @param value default input control value
     */
	public void setDefaultValue(Object value);

    /**
     * Returns the default list of values of this input control
     * (if it is a multiple type input control).
     *
     * @return default list of input control values
     */
	public List getDefaultValues();

    /**
     * Sets the default list of values for this input control
     * (if it is a multiple type input control).
     *
     * @param values default list of input control values
     */
	public void setDefaultValues(List values);

    /**
     * Returns the list of table columns which will appear concatenated
     * in this input control (if it is a query type input control)
     * for each row returned from the database table.
     *
     * @return a list of table columns
     */
	public List getQueryVisibleColumnsAsList();
}
