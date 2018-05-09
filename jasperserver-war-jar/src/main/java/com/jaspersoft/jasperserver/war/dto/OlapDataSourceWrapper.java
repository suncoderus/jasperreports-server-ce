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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;

/**
 * 
 * OlapDataSourceWrapper provides the wrapper for the 
 * OlapDataSourceAction object
 *
 * @author jshih
 */
public class OlapDataSourceWrapper extends BaseDTO implements Serializable {
	private ReportDataSource olapDataSource;
	private List allDatasources;
	private List allTypes;
	private boolean lookup;
	private String type;
	private String source;
	private String selectedUri;
	private byte parentMode;
	
	public boolean isLookup() {
		return lookup;
	}
	public void setLookup(boolean lookup) {
		this.lookup = lookup;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List getAllDatasources() {
		return allDatasources;
	}
	public void setAllDatasources(List allDatasources) {
		this.allDatasources = allDatasources;
	}
	public List getAllTypes() {
		if(allTypes==null){
			allTypes=new ArrayList();
			allTypes.add(JasperServerConstImpl.getJDBCDatasourceType());
			allTypes.add(JasperServerConstImpl.getJNDIDatasourceType());
		}
		return allTypes;
	}
	public void setAllTypes(List allTypes) {
		this.allTypes = allTypes;
	}
	public ReportDataSource getOlapDataSource() {
		return olapDataSource;
	}
	public void setOlapDataSource(ReportDataSource olapDataSource) {
		this.olapDataSource = olapDataSource;
	}
	public String getSelectedUri() {
		return selectedUri;
	}
	public void setSelectedUri(String selectedUri) {
		this.selectedUri = selectedUri;
	}

	public byte getParentMode() {
		return parentMode;
	}

	public void setParentMode(byte parentMode) {
		this.parentMode = parentMode;
	}
}
