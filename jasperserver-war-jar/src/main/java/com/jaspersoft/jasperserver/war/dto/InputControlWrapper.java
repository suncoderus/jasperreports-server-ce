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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id$
 */
public class InputControlWrapper extends BaseDTO
{
	
	public static final String TYPE_DESCRIPTION_BOOLEAN = "input.control.type.boolean";
	public static final String TYPE_DESCRIPTION_SINGLE_VALUE = "input.control.type.single.value";
	public static final String TYPE_DESCRIPTION_SINGLE_SELECT_LOV = "input.control.type.single.select.lov";
	public static final String TYPE_DESCRIPTION_SINGLE_SELECT_LOV_RADIO = "input.control.type.single.select.lov.radio";
	public static final String TYPE_DESCRIPTION_SINGLE_SELECT_QUERY = "input.control.type.single.select.query";
	public static final String TYPE_DESCRIPTION_SINGLE_SELECT_QUERY_RADIO = "input.control.type.single.select.query.radio";
	public static final String TYPE_DESCRIPTION_MULTI_VALUE = "input.control.type.multi.value";
	public static final String TYPE_DESCRIPTION_MULTI_SELECT_LOV = "input.control.type.multi.select.lov";
	public static final String TYPE_DESCRIPTION_MULTI_SELECT_LOV_CHECKBOX = "input.control.type.multi.select.lov.checkbox";
	public static final String TYPE_DESCRIPTION_MULTI_SELECT_QUERY = "input.control.type.multi.select.query";
	public static final String TYPE_DESCRIPTION_MULTI_SELECT_QUERY_CHECKBOX = "input.control.type.multi.select.query.checkbox";

	
	private InputControl inputControl;
	private ResourceReference inputControlURI; // non-null if an input control was selected from repository
	private String oldInputControlURI;
	private String source;
	private List existingPathList;
	private String existingPath;
	private String listItemLabel;
	private String listItemValue;
	private String [] itemsToDelete;
	private static Map supportedControlTypes;
	private static Map supportedDataTypes;
	private String visibleColumns;
	private String dtMaxLength;
	private String dtDecimals;
	private boolean located;
	private boolean suggested;
	private Object parentFlowObject;
	private String newVisibleColumn;
	private List allResources;

	public List getAllResources() {
		return allResources;
	}

	public void setAllResources(List allResources) {
		this.allResources = allResources;
	}

	public Object getParentFlowObject() {
		return parentFlowObject;
	}

	public void setParentFlowObject(Object parentFlowObject) {
		this.parentFlowObject = parentFlowObject;
	}

	public boolean isLocated() {
		return located;
	}

	public void setLocated(boolean located) {
		this.located = located;
	}

	public boolean isSuggested() {
		return suggested;
	}

	public void setSuggested(boolean suggested) {
		this.suggested = suggested;
	}

	public InputControlWrapper(){
	}
	
	public InputControlWrapper(InputControl inputControl)
	{
		this.inputControl = inputControl;
	}

	public Map getSupportedControlTypes(){
		if(supportedControlTypes==null){
			supportedControlTypes=new LinkedHashMap();
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_BOOLEAN), TYPE_DESCRIPTION_BOOLEAN);
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_SINGLE_VALUE), TYPE_DESCRIPTION_SINGLE_VALUE);
			//supportedControlTypes.put(String.valueOf(InputControl.TYPE_MULTI_VALUE),JasperServerConstImpl.getMultiValueCtrlType());
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES), TYPE_DESCRIPTION_SINGLE_SELECT_LOV);
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO), TYPE_DESCRIPTION_SINGLE_SELECT_LOV_RADIO);
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES), TYPE_DESCRIPTION_MULTI_SELECT_LOV);
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX), TYPE_DESCRIPTION_MULTI_SELECT_LOV_CHECKBOX);
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_SINGLE_SELECT_QUERY), TYPE_DESCRIPTION_SINGLE_SELECT_QUERY);
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_SINGLE_SELECT_QUERY_RADIO), TYPE_DESCRIPTION_SINGLE_SELECT_QUERY_RADIO);
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_MULTI_SELECT_QUERY), TYPE_DESCRIPTION_MULTI_SELECT_QUERY);
			supportedControlTypes.put(String.valueOf(InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX), TYPE_DESCRIPTION_MULTI_SELECT_QUERY_CHECKBOX);
		}
		return supportedControlTypes;
	}

	public Map getSupportedDataTypes(){
		if(supportedDataTypes==null){
			supportedDataTypes=new LinkedHashMap();
			supportedDataTypes.put(String.valueOf(DataType.TYPE_TEXT),JasperServerConstImpl.getTypeText());
			supportedDataTypes.put(String.valueOf(DataType.TYPE_NUMBER),JasperServerConstImpl.getTypeNumber());
			supportedDataTypes.put(String.valueOf(DataType.TYPE_DATE),JasperServerConstImpl.getTypeDate());
			supportedDataTypes.put(String.valueOf(DataType.TYPE_DATE_TIME),JasperServerConstImpl.getTypeDateTime());
		}
		return supportedDataTypes;
	}

	public InputControl getInputControl()
	{
		return inputControl;
	}

	public void setInputControl(InputControl inputControl)
	{
		this.inputControl = inputControl;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public List getExistingPathList()
	{
		return existingPathList;
	}

	public void setExistingPathList(List existingPathList)
	{
		this.existingPathList = existingPathList;
	}

	public String getExistingPath()
	{
		return existingPath;
	}

	public void setExistingPath(String existingPath)
	{
		this.existingPath = existingPath;
	}

	public String getListItemLabel()
	{
		return listItemLabel;
	}

	public void setListItemLabel(String listItemLabel)
	{
		this.listItemLabel = listItemLabel;
	}

	public String getListItemValue()
	{
		return listItemValue;
	}

	public void setListItemValue(String listItemValue)
	{
		this.listItemValue = listItemValue;
	}

	public String[] getItemsToDelete()
	{
		return itemsToDelete;
	}

	public void setItemsToDelete(String[] itemsToDelete)
	{
		this.itemsToDelete = itemsToDelete;
	}

	public String getVisibleColumns()
	{
		return visibleColumns;
	}

	public void setVisibleColumns(String visibleColumns)
	{
		this.visibleColumns = visibleColumns;
	}

	public String getDtDecimals()
	{
		return dtDecimals;
	}

	public void setDtDecimals(String dtDecimals)
	{
		this.dtDecimals = dtDecimals;
		int decimals=0;
		try {
			decimals = Integer.parseInt(dtMaxLength);
		} catch (NumberFormatException e) {
			//When validated, no parsing errors would come
			}
		((DataType) inputControl.getDataType().getLocalResource()).setDecimals(new Integer(decimals));
	}

	public String getDtMaxLength()
	{
		return dtMaxLength;
	}

	public void setDtMaxLength(String dtMaxLength)
	{	
		this.dtMaxLength = dtMaxLength;
		int maxLength=0;
		try {
			maxLength = Integer.parseInt(dtMaxLength);
		} catch (NumberFormatException e) {
			//When validated, no parsing errors would come
			}
		((DataType) inputControl.getDataType().getLocalResource()).setMaxLength(new Integer(maxLength));
	}
	
	public boolean isLovType(){
		return isMultiSelectLovType() || isSingleSelectLovType();
	}
	public boolean isQueryType(){
		return isSingleSelectQueryType() || isMultiSelectQueryType();
	}
	public boolean isDataType(){
		return isSingleValueType() || isMultiValueType();
	}
	public boolean isBooleanType(){
		return InputControl.TYPE_BOOLEAN==inputControl.getInputControlType();
	}
	public boolean isMultiSelectLovType(){
		return InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES==inputControl.getInputControlType()
			|| inputControl.getInputControlType() == InputControl.TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX;
	}
	
	public boolean isSingleSelectLovType(){
		return InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES == inputControl.getInputControlType()
				|| InputControl.TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO == inputControl.getInputControlType();
	}
	
	public boolean isMultiSelectQueryType(){
		return InputControl.TYPE_MULTI_SELECT_QUERY==inputControl.getInputControlType()
			|| inputControl.getInputControlType() == InputControl.TYPE_MULTI_SELECT_QUERY_CHECKBOX;
	}
	public boolean isMultiValueType(){
		return InputControl.TYPE_MULTI_VALUE==inputControl.getInputControlType();
	}
	
	public boolean isMulti() {
		return isMultiValueType() || isMultiSelectLovType() || isMultiSelectQueryType();
	}
	
	public boolean isSingleSelectQueryType() {
		return InputControl.TYPE_SINGLE_SELECT_QUERY == inputControl.getInputControlType()
				|| InputControl.TYPE_SINGLE_SELECT_QUERY_RADIO == inputControl.getInputControlType();
	}
	
	public boolean isSingleValueType(){
		return InputControl.TYPE_SINGLE_VALUE==inputControl.getInputControlType();
	}

	public ResourceReference getInputControlURI()
	{
		return inputControlURI;
	}

	public void setInputControlURI(ResourceReference inputControlURI)
	{
		this.inputControlURI = inputControlURI;
	}

	public String getOldInputControlURI()
	{
		return oldInputControlURI;
	}

	public void setOldInputControlURI(String oldInputControlURI)
	{
		this.oldInputControlURI = oldInputControlURI;
	}

	public String getNewVisibleColumn()
	{
		return newVisibleColumn;
	}

	public void setNewVisibleColumn(String newVisibleColumn)
	{
		this.newVisibleColumn = newVisibleColumn;
	}
}
