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
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;

/**
 * 
 * OlapUnitWrapper provides the wrapper for the 
 * OlapUnitAction object
 *
 * @author jshih
 */
public class OlapUnitWrapper extends BaseDTO {
	private List existingResources;
	private OlapUnit olapUnit;
	private List suggestedResources;
	private List suggestedControls;
    @Deprecated
	private List reusableSchemas; // shared
	private String source;
	private byte[] schemaData;
	private String schemaUri;
	private String accessGrantResourceUri;
	private String datasourceUri;
	private String oldSchemaUri;
	private String originalSchemaUri;
	private String validationMessage;
	private boolean schemaLoaded;
	private boolean schemaLocated;
	private boolean accessGrantResourceLoaded;
	private boolean accessGrantResourceLocated;
	private boolean result;
	private boolean named;
	private boolean datasourceIdentified;
	private List olapViews;

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
	private String olapUnitName;
	private String olapUnitLabel;
	private String olapUnitDescription;
	private String olapUnitMdxQuery;
	private Object olapUnitOptions;
	private String xmlaCatalog;
	private String xmlaDatasource;
	private String xmlaConnectionUri;
	private List allTypes;
	private String type;
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
	
	private FileResource accessGrantResource;
	public void setAccessGrantResource(FileResource accessGrantResource) {
		this.accessGrantResource = accessGrantResource;
	}
	public FileResource getAccessGrantResource () {
		return this.accessGrantResource;
	}
	
	private ReportDataSource olapClientDatasource;
	public void setOlapClientDatasource(ReportDataSource olapClientDatasource) {
		this.olapClientDatasource = olapClientDatasource;
	}
	public ReportDataSource getOlapClientDatasource () {
		return this.olapClientDatasource;
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

	public boolean isAccessGrantResourceLoaded() {
		return accessGrantResourceLoaded;
	}
	public void setAccessGrantResourceLoaded(boolean accessGrantResourceLoaded) {
		this.accessGrantResourceLoaded = accessGrantResourceLoaded;
		setAccessGrantResourceLocated(true);
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
	public boolean isAccessGrantResourceLocated() {
		return accessGrantResourceLocated;
	}
	public void setAccessGrantResourceLocated(boolean accessGrantResourceLocated) {
		this.accessGrantResourceLocated = accessGrantResourceLocated;
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
	@Deprecated
    public List getExistingResources() {
		return existingResources;
	}
    @Deprecated
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
	public void setConnectionModified(boolean connectionCreated) {
		this.connectionModified = connectionCreated;
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
	
	public String getOlapUnitName() {
		return olapUnitName;
	}
	
	public void setOlapUnitName(String olapUnitName) {
		this.olapUnitName = olapUnitName;
	}
	
	public String getOlapUnitLabel() {
		return olapUnitLabel;
	}
	
	public void setOlapUnitLabel(String olapUnitLabel) {
		this.olapUnitLabel = olapUnitLabel;
	}
	
	public String getOlapUnitDescription() {
		return olapUnitDescription;
	}
	
	public void setOlapUnitDescription(String olapUnitDescription) {
		this.olapUnitDescription = olapUnitDescription;
	}
	
	public String getOlapUnitMdxQuery() {
		return olapUnitMdxQuery;
	}
	
	public void setOlapUnitMdxQuery(String olapUnitMdxQuery) {
		this.olapUnitMdxQuery = olapUnitMdxQuery;
	}
	
	public Object getOlapUnitOptions() {
		return olapUnitOptions;
	}
	
	public void setOlapUnitOptions(Object options) {
		this.olapUnitOptions = options;
	}
	
	
	public String getParentFolder() {
		return parentFolder;
	}
	
	public void setParentFolder(String parentFolder) {
		this.parentFolder = parentFolder;
	}
	public String getAccessGrantResourceUri() {
	    return accessGrantResourceUri;
	}
	public void setAccessGrantResourceUri(String accessGrantResourceUri) {
	    this.accessGrantResourceUri = accessGrantResourceUri;
	}
}
