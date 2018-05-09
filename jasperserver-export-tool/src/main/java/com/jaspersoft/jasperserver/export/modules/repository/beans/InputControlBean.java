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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

/**
 * @author tkavanagh
 * @version $Id$
 */

public class InputControlBean extends ResourceBean {

	private byte type; 
	private boolean mandatory;
	private boolean readOnly;
	private boolean visible = true;
	private ResourceReferenceBean dataType;
	private ResourceReferenceBean listOfValues;
	private ResourceReferenceBean query;
	private String[] queryVisibleColumns;
	private String queryValueColumn;
	private Object defaultValue;
	
	protected void additionalCopyFrom(Resource res, ResourceExportHandler export) {
		InputControl ic = (InputControl) res;
		setType(ic.getInputControlType());
		setMandatory(ic.isMandatory());
		setReadOnly(ic.isReadOnly());
		setVisible(ic.isVisible());
		setDataType(export.handleReference(ic.getDataType()));
		setListOfValues(export.handleReference(ic.getListOfValues()));
		setQuery(export.handleReference(ic.getQuery()));
		setQueryVisibleColumns(ic.getQueryVisibleColumns());
		setQueryValueColumn(ic.getQueryValueColumn());
		setDefaultValue(ic.getDefaultValue());
	}

	protected void additionalCopyTo(Resource res, ResourceImportHandler importHandler) {
		InputControl ic = (InputControl) res;
		ic.setInputControlType(getType());
		ic.setMandatory(isMandatory());
		ic.setReadOnly(isReadOnly());
		ic.setVisible(isVisible());
		ic.setDataType(importHandler.handleReference(getDataType()));
		ic.setListOfValues(importHandler.handleReference(getListOfValues()));
		ic.setQuery(importHandler.handleReference(getQuery()));
		copyQueryColsTo(ic);
		ic.setQueryValueColumn(getQueryValueColumn());
		ic.setDefaultValue(getDefaultValue());
	}

	protected void copyQueryColsTo(InputControl ic) {
		if (queryVisibleColumns != null) {
			for (int i = 0; i < queryVisibleColumns.length; i++) {
				String column = queryVisibleColumns[i];
				ic.addQueryVisibleColumn(column);
			}
		}
	}

	public ResourceReferenceBean getDataType() {
		return dataType;
	}
	
	public void setDataType(ResourceReferenceBean dataType) {
		this.dataType = dataType;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public ResourceReferenceBean getListOfValues() {
		return listOfValues;
	}
	
	public void setListOfValues(ResourceReferenceBean listOfValues) {
		this.listOfValues = listOfValues;
	}
	
	public ResourceReferenceBean getQuery() {
		return query;
	}
	
	public void setQuery(ResourceReferenceBean query) {
		this.query = query;
	}
	
	public String getQueryValueColumn() {
		return queryValueColumn;
	}
	
	public void setQueryValueColumn(String queryValueColumn) {
		this.queryValueColumn = queryValueColumn;
	}
	
	public String[] getQueryVisibleColumns() {
		return queryVisibleColumns;
	}
	
	public void setQueryVisibleColumns(String[] queryVisibleColumns) {
		this.queryVisibleColumns = queryVisibleColumns;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible)	{
		this.visible = visible;
	}
}
