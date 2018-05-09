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

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;

/**
 * 
 * OlapClientConnectionWrapper provides the wrapper for the 
 * OlapClientConnectionAction object
 *
 * @author jshih
 */
public class OlapClientConnectionWrapper extends BaseDTO {
	private List existingResources; 
	private OlapUnit olapUnit;
	private ReportUnit reportUnit; // used to distinguish olap vs report webflow
	private boolean accessGrant; // used to distinguish olap schema vs access grant
	private List suggestedResources;
	private List suggestedControls;
    @Deprecated
	private List reusableSchemas;
	private String source;
	private byte[] schemaData;
	private String schemaUri;
	private String accessGrantUri;
	private String datasourceUri;
	private String oldSchemaUri;
	private String originalSchemaUri;
	private String originalAccessGrantResourceUri;
	private String validationMessage;
	private boolean schemaLoaded;
	private boolean schemaLocated;
	private boolean result;
	private boolean named;
	private boolean datasourceIdentified;
	private List olapViews;
	private boolean hasNonSuggestedResources;
	private boolean hasSuggestedResources;
	private String connectionUri;
	private String oldConnectionUri;
	private String originalConnectionUri;
    @Deprecated
	private List reusableMondrianConnections;
    @Deprecated
	private List reusableXmlaConnections;
    @Deprecated
	private List reusableXmlaDefinitions;
	private boolean connectionModified;
	private boolean datasourceAdded;
	private String connectionName;
	private String connectionLabel;
	private String connectionDescription;
	private String xmlaCatalog;
	private String xmlaDatasource;
	private String xmlaConnectionUri;
	private String username;
	private String password;
	private List allTypes;
	private String type;
	private Object parentFlowObject;
    @Deprecated
	private List allFolders;
	private String parentFolder;
	
	private String olapConnectionType;
	public void setOlapConnectionType(String type) {
		this.olapConnectionType = type;
	}
	public String getOlapConnectionType() {
		return this.olapConnectionType;
	}
	
	private OlapClientConnection olapClientConnection;
	public void setOlapClientConnection(OlapClientConnection olapClientConnection) {
		this.olapClientConnection = olapClientConnection;
	}
	public OlapClientConnection getOlapClientConnection () {
		return this.olapClientConnection;
	}
	
	private FileResource olapClientSchema;
	public void setOlapClientSchema(FileResource olapClientSchema) {
		this.olapClientSchema = olapClientSchema;
	}
	public FileResource getOlapClientSchema () {
		return this.olapClientSchema;
	}
	
	private ReportDataSource olapClientDatasource;
	public void setOlapClientDatasource(ReportDataSource olapClientDatasource) {
		this.olapClientDatasource = olapClientDatasource;
	}
	public ReportDataSource getOlapClientDatasource () {
		return this.olapClientDatasource;
	}
	
	public boolean isHasNonSuggestedResources() {
		return hasNonSuggestedResources;
	}
	public void setHasNonSuggestedResources(boolean hasNonSuggestedResources) {
		this.hasNonSuggestedResources = hasNonSuggestedResources;
	}
	public boolean isHasSuggestedResources() {
		return hasSuggestedResources;
	}
	public void setHasSuggestedResources(boolean hasSuggestedResources) {
		this.hasSuggestedResources = hasSuggestedResources;
	}
	public boolean isDatasourceIdentified() {
		return datasourceIdentified;
	}
	public void setDatasourceIdentified(boolean datasourceIdentified) {
		this.datasourceIdentified = datasourceIdentified;
	}
	public boolean isNamed() {
		return named;
	}
	public void setNamed(boolean named) {
		this.named = named;
	}
	public boolean isSchemaLoaded() {
		return schemaLoaded;
	}
	public void setSchemaLoaded(boolean schemaLoaded) {
		this.schemaLoaded = schemaLoaded;
		setSchemaLocated(true);
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
    @Deprecated
	public List getReusableSchemas() {
		return reusableSchemas;
	}
    @Deprecated
	public void setReusableSchemas(List reusableSchemas) {
		this.reusableSchemas = reusableSchemas;
	}
	public OlapUnit getOlapUnit() {
		return olapUnit;
	}
	public void setOlapUnit(OlapUnit olapUnit) {
		this.olapUnit = olapUnit;
	}
	public ReportUnit getReportUnit() {
		return reportUnit;
	}
	public void setReportUnit(ReportUnit reportUnit) {
		this.reportUnit = reportUnit;
	}
	public boolean getAccessGrant() {
		return accessGrant;
	}
	public void setAccessGrant(boolean accessGrant) {
		this.accessGrant = accessGrant;
	}
	public String getOldSchemaUri() {
		return oldSchemaUri;
	}
	public void setOldSchemaUri(String oldSchemaUri) {
		this.oldSchemaUri = oldSchemaUri;
	}
	public String getOriginalSchemaUri() {
		return originalSchemaUri;
	}
	public void setOriginalSchemaUri(String originalSchemaUri) {
		this.originalSchemaUri = originalSchemaUri;
	}
	public boolean isSchemaLocated() {
		return schemaLocated;
	}
	public void setSchemaLocated(boolean schemaLocated) {
		this.schemaLocated = schemaLocated;
	}
	public byte[] getSchemaData() {
		return schemaData;
	}
	public void setSchemaData(byte[] schemaData) {
		this.schemaData = schemaData;
	}
	public String getSchemaUri() {
		return schemaUri;
	}
	public void setSchemaUri(String schemaUri) {
		this.schemaUri = schemaUri;
	}
	public String getDatasourceUri() {
		return datasourceUri;
	}
	public void setDatasourceUri(String datasourceUri) {
		this.datasourceUri = datasourceUri;
	}
	public List getSuggestedControls() {
		return suggestedControls;
	}
	public void setSuggestedControls(List controlWrappers) {
		this.suggestedControls = controlWrappers;
	}
	public List getSuggestedResources() {
		return suggestedResources;
	}
	public void setSuggestedResources(List resourceWrappers) {
		this.suggestedResources = resourceWrappers;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public List getOlapViews() {
		return olapViews;
	}
	public void setOlapViews(List olapViews) {
		this.olapViews = olapViews;
	}
	public String getValidationMessage() {
		return validationMessage;
	}
	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}
	public List getExistingResources() {
		return existingResources;
	}
	public void setExistingResources(List existingResources) {
		this.existingResources = existingResources;
	}
	public String getXmlaCatalog() {
		return xmlaCatalog;
	}
	public void setXmlaCatalog(String xmlaCatalog) {
		this.xmlaCatalog = xmlaCatalog;
	}
	public String getXmlaConnectionUri() {
		return xmlaConnectionUri;
	}
	public void setXmlaConnectionUri(String xmlaConnectionUri) {
		this.xmlaConnectionUri = xmlaConnectionUri;
	}
	public String getXmlaDatasource() {
		return xmlaDatasource;
	}
	public void setXmlaDatasource(String xmlaDatasource) {
		this.xmlaDatasource = xmlaDatasource;
	}
	public String getConnectionUri() {
		return connectionUri;
	}
	public void setConnectionUri(String connectionUri) {
		this.connectionUri = connectionUri;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getOldConnectionUri() {
		return oldConnectionUri;
	}
	public void setOldConnectionUri(String oldConnectionUri) {
		this.oldConnectionUri = oldConnectionUri;
	}
	public String getOriginalConnectionUri() {
		return originalConnectionUri;
	}
	public void setOriginalConnectionUri(String originalConnectionUri) {
		this.originalConnectionUri = originalConnectionUri;
	}
    @Deprecated
	public List getReusableMondrianConnections() {
		return reusableMondrianConnections;
	}
    @Deprecated
	public void setReusableMondrianConnections(List reusableMondrianConnections) {
		this.reusableMondrianConnections = reusableMondrianConnections;
	}
    @Deprecated
	public List getReusableXmlaConnections() {
		return reusableXmlaConnections;
	}
    @Deprecated
	public void setReusableXmlaConnections(List reusableXmlaConnections) {
		this.reusableXmlaConnections = reusableXmlaConnections;
	}
    @Deprecated
	public List getReusableXmlaDefinitions() {
		return reusableXmlaDefinitions;
	}
    @Deprecated
	public void setReusableXmlaDefinitions(List reusableXmlaDefinitions) {
		this.reusableXmlaDefinitions = reusableXmlaDefinitions;
	}
	public boolean isConnectionModified() {
		return connectionModified;
	}
	public void setConnectionModified(boolean connectionChanged) {
		this.connectionModified = connectionChanged;
	}
	public boolean isDatasourceAdded() {
		return datasourceAdded;
	}
	public void setDatasourceAdded(boolean datasourceAdded) {
		this.datasourceAdded = datasourceAdded;
	}
	
	public List getAllTypes() {
		if(allTypes==null){
			allTypes=new ArrayList();
			allTypes.add(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION);
			allTypes.add(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
		}
		return allTypes;
	}
	
	public void setAllTypes(List allTypes) {
		this.allTypes = allTypes;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getConnectionName() {
		return connectionName;
	}
	
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	
	public String getConnectionLabel() {
		return connectionLabel;
	}
	
	public void setConnectionLabel(String connectionLabel) {
		this.connectionLabel = connectionLabel;
	}
	
	public String getConnectionDescription() {
		return connectionDescription;
	}
	
	public void setConnectionDescription(String connectionDescription) {
		this.connectionDescription = connectionDescription;
	}
	
	public Object getParentFlowObject() {
		return parentFlowObject;
	}

	public void setParentFlowObject(Object parentFlowObject) {
		this.parentFlowObject = parentFlowObject;
	}
    @Deprecated
	public List getAllFolders() {
		return allFolders;
	}

    @Deprecated
	public void setAllFolders(List allFolders) {
		this.allFolders = allFolders;
	}

	public String getParentFolder() {
		return parentFolder;
	}
	
	public void setParentFolder(String parentFolder) {
		this.parentFolder = parentFolder;
	}
	public String getAccessGrantUri() {
	    return accessGrantUri;
	}
	public void setAccessGrantUri(String accessGrantUri) {
	    this.accessGrantUri = accessGrantUri;
	}
	public String getOriginalAccessGrantResourceUri() {
	    return originalAccessGrantResourceUri;
	}
	public void setOriginalAccessGrantResourceUri(
		String originalAccessGrantResourceUri) {
	    this.originalAccessGrantResourceUri = originalAccessGrantResourceUri;
	}
}
