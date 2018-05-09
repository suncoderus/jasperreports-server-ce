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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.CustomDataSourceDefinition;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

public class ReportDataSourceWrapper extends BaseDTO implements Serializable {
    public static final String ATTRIBUTE_DATA_SOURCE_JSON = "dataSourceJson";
	private ReportDataSource reportDataSource;
	private List allDatasources;
	private boolean lookup;
	private String type;
	private String source;
	private String selectedUri;
	private List allFolders;
	private Object parentFlowObject;
	private String parentType;
    private String dataSourceUri;
    private String dataSourceJson;
	private ResourceLookup[] existingResources;
	private List customProperties;
    private Map<String, Object> namedProperties = new HashMap<String, Object>();
	private String customDatasourceLabel;
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
	public ReportDataSource getReportDataSource() {
		return reportDataSource;
	}
	public void setReportDataSource(ReportDataSource reportDataSource) {
		this.reportDataSource = reportDataSource;
	}
	public String getSelectedUri() {
		return selectedUri;
	}
	public void setSelectedUri(String selectedUri) {
		this.selectedUri = selectedUri;
	}

    public String getDataSourceUri() {
        return dataSourceUri;
    }

    public void setDataSourceUri(String dataSourceUri) {
        this.dataSourceUri = dataSourceUri;
    }

    public String getDataSourceJson() {
        return dataSourceJson;
    }

    public void setDataSourceJson(String dataSourceJson) {
        this.dataSourceJson = dataSourceJson;
    }

    public List getAllFolders() {
		return allFolders;
	}
	public void setAllFolders(List allFolders) {
		this.allFolders = allFolders;
	}
	
	public Object getParentFlowObject() {
		return parentFlowObject;
	}

	public void setParentFlowObject(Object parentFlowObject) {
		this.parentFlowObject = parentFlowObject;
	}
	public String getParentType() {
		return parentType;
	}
	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public ResourceLookup[] getExistingResources()
	{
		return existingResources;
	}

	public void setExistingResources(ResourceLookup[] existingResources)
	{
		this.existingResources = existingResources;
	}

	public List getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(List customProperties) {
		this.customProperties = customProperties;
	}

    public Map<String, Object> getNamedProperties() {
        return namedProperties;
    }

    public void setNamedProperties(Map<String, Object> namedProperties) {
        this.namedProperties = namedProperties;
    }

    public String getCustomDatasourceLabel() {
		return customDatasourceLabel;
	}
	public void setCustomDatasourceLabel(String customDatasourceLabel) {
		this.customDatasourceLabel = customDatasourceLabel;
	}
}
