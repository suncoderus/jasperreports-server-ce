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
package com.jaspersoft.jasperserver.war.action;

import java.util.ArrayList;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.XMLAConnectionImpl;
import com.jaspersoft.jasperserver.core.util.XMLUtil;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.model.impl.BaseTreeDataProvider;
import com.jaspersoft.jasperserver.war.model.impl.TypedTreeDataProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.driver.xmla.cache.XmlaOlap4jCache;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.JdbcOlapDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.JndiJdbcOlapDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.XMLATestResult;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.FileResourceWrapper;
import com.jaspersoft.jasperserver.war.dto.OlapClientConnectionWrapper;
import com.jaspersoft.jasperserver.war.dto.OlapUnitWrapper;
import com.jaspersoft.jasperserver.war.dto.ReportDataSourceWrapper;

/**
 * 
 * OlapClientConnectionAction provides the actions to olap client connection views
 *
 * @author jshih
 */
public class OlapClientConnectionAction extends FormAction {
    protected static final String ATTRIBUTE_RESOURCE_ID_NOT_SUPPORTED_SYMBOLS = "resourceIdNotSupportedSymbols";

	private static final String OU_URI_PARAM = "resource";
	private static final String FORM_OBJECT_KEY = "connectionWrapper";
	private static final String OLAP_UNIT_SUFFIX = "_unit";
	private static final String OLAP_UNIT_KEY = "unit";
	private static final String CONTROL_OBJECT_KEY = "control";
	private static final String RESOURCE_OBJECT_KEY = "resource";
	private static final String DATASOURCE_OBJECT_KEY = "dataResource";
	private static final String SCHEMA_OBJECT_KEY = "fileResource";
	private static final String PARENT_FOLDER_ATTR = "parentFolder";
	private static final String PARAM_FROM_PAGE = "frompage";
	private static final String PARAM_RESOURCE_NAME = "resourceName";
	private static final String LOCAL_SCHEMA_SUFFIX = "_schema";
	private static final String UNPARSABLE_SCHEMA_ATTR = "schemaUnparsable";
	private static final String INVALID_XMLA_CONNECTION_ATTR = "xmlaConnectionInvalid";
    protected static final String PASSWORD_SUBSTITUTION_KEY="passwordSubstitution";
	private static final String UNPARSABLE_XMLA_ATTR = "xmlaUnparsable";
	private static final String UNPARSABLE_CONNECTION_ATTR = "connectionUnparsable";
    private static final String DATASOURCE_TREE_DATA_PROVIDER = "dsTreeDataProvider";
	private static final String JRXML_TREE_DATA_PROVIDER = "jrxmlTreeDataProvider";
    private static final String AJAX_RESPONSE_MODEL = "ajaxResponseModel";

	protected final Log log = LogFactory.getLog(this.getClass());
	protected RepositoryService repository;
	protected OlapConnectionService olapConnection;
	private EngineService engine;
	protected JasperServerConstImpl constants = new JasperServerConstImpl();
    protected MessageSource messages;
    private TypedTreeDataProvider typedTreeDataProvider;
    private String classOlap4jCache;

    private BaseTreeDataProvider jrxmlTreeDataProvider;

    protected ConfigurationBean configuration;

	/**
	 * initialize OlapClientConnectionAction.class object
	 */
	public OlapClientConnectionAction() {
		setFormObjectClass(OlapClientConnectionWrapper.class);
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW);
	}

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    public void setConfiguration(ConfigurationBean configuration) {
        this.configuration = configuration;
    }

    public void setClassOlap4jCache(String classOlap4jCache) {
        this.classOlap4jCache = classOlap4jCache;
    }

    /**
	 * initAction performs the initialization for the olap client connection web flow
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event initAction(RequestContext context) throws Exception {
		OlapClientConnectionWrapper wrapper = 
			(OlapClientConnectionWrapper) getFormObject(context);
		OlapUnitWrapper parentWrapper = (OlapUnitWrapper) wrapper
				.getParentFlowObject();
		OlapClientConnection connection = null;
		if (wrapper.isNewMode()) {
			// create new olap client connection
			String parentFolder = (String)context.getFlowScope().get(PARENT_FOLDER_ATTR);
			if (parentFolder == null) {
				parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
				context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
			}
			String folderURI = parentFolder;
			if (folderURI == null) {
				folderURI = "/";
			}
			FilterCriteria resourcesInFolder = FilterCriteria.createFilter();
			resourcesInFolder.addFilterElement(FilterCriteria
					.createParentFolderFilter(folderURI));
			log("Searching for resources in the chosen folder:" + folderURI);
			ResourceLookup[] existingResources = repository.findResource(JasperServerUtil.getExecutionContext(context),
					resourcesInFolder);

			if (existingResources != null && existingResources.length != 0) {
				log("res lookup size=" + existingResources.length);
				List allResources = new ArrayList();
				for (int i = 0; i < existingResources.length; i++) {
					ResourceLookup rLookup = existingResources[i];
					allResources.add(rLookup.getName());
					log("adding resource: " + rLookup.getName()
							+ " to the list");
				}
				wrapper.setExistingResources(allResources);
			}
		} else {
			// modify existing olap client connection
			if (parentWrapper == null) {
				// main flow
				connection = wrapper.getOlapClientConnection();
			} else {
				// subflow
				connection = parentWrapper.getOlapClientConnection();
			}

			if (connection == null) {
				throw new JSException("jsexception.no.olap.client.connection");
			}

			wrapper.setOlapClientConnection(connection);
			wrapper.setConnectionName(connection.getName());
			wrapper.setConnectionLabel(connection.getLabel());
			wrapper.setConnectionDescription(connection.getDescription());

			// set the connection type to enable testConnectionType in webflow
			if (wrapper.getOlapClientConnection() instanceof MondrianConnection) {
				// for mondrian connection
				wrapper.setOlapClientSchema((FileResource) repository
						.getResource(null, ((MondrianConnection) connection)
								.getSchema().getReferenceURI()));
				wrapper.setOlapClientDatasource((ReportDataSource) repository
						.getResource(null, ((MondrianConnection) connection)
								.getDataSource().getReferenceURI()));
				// TODO subflow
				if (parentWrapper != null) {
					wrapper.setSchemaUri(parentWrapper.getSchemaUri());

                    MondrianConnection monConnInRepo = (MondrianConnection) parentWrapper.getOlapClientConnection();
					wrapper.setDatasourceUri(monConnInRepo.getDataSource().getReferenceURI());
				}
				wrapper.setType(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION);
			} else if (wrapper.getOlapClientConnection() instanceof XMLAConnection) {
				// for xmla connection
				wrapper.setXmlaCatalog(((XMLAConnection) connection)
						.getCatalog());
				wrapper.setXmlaDatasource(((XMLAConnection) connection)
						.getDataSource());
				wrapper.setXmlaConnectionUri(((XMLAConnection) connection)
						.getURI());
				wrapper.setUsername(((XMLAConnection) connection)
						.getUsername());
				wrapper.setPassword(((XMLAConnection) connection)
						.getPassword());
				wrapper.setType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
			} else {
				throw new JSException("jsexception.unknown.connection.type");
			}
			wrapper.setSource(constants.FIELD_CHOICE_CONT_REPO);
		}
		/**
		 * TODO(stas): Remove next block. I didn't find any usage of it
		 */
		// get reusable resources
		if (!wrapper.isSubflowMode()) {
//			getAllConnections(context, wrapper);
//			findAllSchemas(context, wrapper);
//			getAllXmlaSources(context, wrapper);
		} else {
			// get resource from main flow
//			wrapper.setReusableMondrianConnections(parentWrapper
//					.getReusableMondrianConnections());
//			wrapper.setReusableXmlaConnections(parentWrapper
//					.getReusableXmlaConnections());
//			wrapper.setReusableSchemas(parentWrapper.getReusableSchemas());
//			wrapper.setReusableXmlaDefinitions(parentWrapper
//					.getReusableXmlaDefinitions());
		}
//		getAllFolders(wrapper);
		String parentFolder = (String)context.getFlowScope().get(PARENT_FOLDER_ATTR);
		if (parentFolder == null) {
			parentFolder = context.getRequestParameters().get("ParentFolderUri");
			context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
		}
		wrapper.setParentFolder(parentFolder); // for olap conneciton main flow
		context.getFlowScope().put(FORM_OBJECT_KEY, wrapper);

        context.getFlowScope().put(PASSWORD_SUBSTITUTION_KEY, messages.getMessage("input.password.substitution", null, LocaleContextHolder.getLocale()));
		context.getFlowScope().put("constants", constants);
		if (wrapper.getType() == null) { // default
			wrapper.setType(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION);
		}

        context.getFlowScope().put(ATTRIBUTE_RESOURCE_ID_NOT_SUPPORTED_SYMBOLS,
                configuration.getResourceIdNotSupportedSymbols());

        context.getExternalContext().getSessionMap().put(DATASOURCE_TREE_DATA_PROVIDER, typedTreeDataProvider);
        context.getExternalContext().getSessionMap().put(JRXML_TREE_DATA_PROVIDER, jrxmlTreeDataProvider);
		return success();
	}

	/**
	 * findAllSchemas finds all mondrian olap schema
	 * 
	 * @param context 
	 * @param wrapper
	 */
    @Deprecated
    protected void findAllSchemas(RequestContext context,
			OlapClientConnectionWrapper wrapper) {
		FilterCriteria filterCriteria = FilterCriteria
				.createFilter(FileResource.class);
		filterCriteria.addFilterElement(FilterCriteria
				.createPropertyEqualsFilter("fileType",
						ResourceDescriptor.TYPE_MONDRIAN_SCHEMA));
		ResourceLookup[] resourceLookup = repository.findResource(JasperServerUtil.getExecutionContext(context),
				filterCriteria);
		List allSources = null;
		if (resourceLookup != null && resourceLookup.length != 0) {
			log("Found source lookups size=" + resourceLookup.length);
			allSources = new ArrayList(resourceLookup.length);
			for (int i = 0; i < resourceLookup.length; i++) {
				Resource resource = (Resource) resourceLookup[i];
				Object resourceObj;
				try {
					resourceObj = repository.getResource(null, resource.getURIString());
				} catch (JSException ex) {
					continue;
				}
				if (!allSources.contains(((FileResource) resourceObj)
						.getURIString())) {
					allSources.add(((FileResource) resourceObj).getURIString());
				}
				log("added uri=" + resource.getURIString());
			}
			wrapper.setReusableSchemas(allSources);
		}
	}
	
	/**
	 * getAllConnections finds all olap client connections
	 * @param context 
	 * 
	 * @param wrapper
	 */
    @Deprecated
	protected void getAllConnections(RequestContext context,
			OlapClientConnectionWrapper wrapper) {
		FilterCriteria filterCriteria = FilterCriteria
				.createFilter(OlapClientConnection.class);
		ResourceLookup[] resourceLookup = repository.findResource(
				JasperServerUtil.getExecutionContext(context), filterCriteria);
		List allMondrianConnections = null;
		List allXmlaConnections = null;
		if (resourceLookup != null && resourceLookup.length != 0) {
			log("Found conneciton lookups size=" + resourceLookup.length);
			allMondrianConnections = new ArrayList(resourceLookup.length);
			allXmlaConnections = new ArrayList(resourceLookup.length);
			for (int i = 0; i < resourceLookup.length; i++) {
				Resource resource = (Resource) resourceLookup[i];
                                
				Object resourceObj;
				try {
					resourceObj = repository.getResource(null, resource.getURIString());
				} catch (JSException ex) {
					continue;
				}
				if (resourceObj instanceof MondrianConnection) {
					if (!allMondrianConnections.contains(
							((OlapClientConnection) resourceObj).getURIString())) {
						allMondrianConnections.add(
								((OlapClientConnection) resourceObj).getURIString());
					}
				}
				else if (resourceObj instanceof XMLAConnection) {
					if (!allXmlaConnections.contains(
							((OlapClientConnection) resourceObj).getURIString())) {
						allXmlaConnections.add(
								((OlapClientConnection) resourceObj).getURIString());
					}
				}
				else {
					throw new JSException("jsexception.unknown.connection.type");
				}
			}
			wrapper.setReusableMondrianConnections(allMondrianConnections);
			wrapper.setReusableXmlaConnections(allXmlaConnections);
		}
	}
	
	/**
	 * getAllXmlaConnections finds all xmla mondrian sources
	 * @param context 
	 * 
	 * @param wrapper
	 */
    @Deprecated
	protected void getAllXmlaSources(RequestContext context,
			OlapClientConnectionWrapper wrapper) {
		FilterCriteria filterCriteria = FilterCriteria
				.createFilter(MondrianXMLADefinition.class);
		ResourceLookup[] resourceLookup = repository.findResource(
				JasperServerUtil.getExecutionContext(context), filterCriteria);
		List allXmlaDefinitions = null;
		if (resourceLookup != null && resourceLookup.length != 0) {
			log("Found xmla definition lookups size=" + resourceLookup.length);
			allXmlaDefinitions = new ArrayList(resourceLookup.length);
			for (int i = 0; i < resourceLookup.length; i++) {
				Resource resource = (Resource) resourceLookup[i];

				Object resourceObj;
				try {
					resourceObj = repository.getResource(null, resource.getURIString());
				} catch (JSException ex) {
					continue;
				}

				if (!allXmlaDefinitions.contains(
						((MondrianXMLADefinition) resourceObj).getURIString())) {
					allXmlaDefinitions.add(
							((MondrianXMLADefinition) resourceObj).getCatalog());
				}
			}
			wrapper.setReusableXmlaDefinitions(allXmlaDefinitions);
		}
	}

	/**
	 * getAllFolders finds all folder resources
	 * 
	 * @param wrapper
	 */
    @Deprecated
	protected void getAllFolders(OlapClientConnectionWrapper wrapper) {
		List allFolders = repository.getAllFolders(null);
		wrapper.setAllFolders(new ArrayList());
		for (int i = 0; i < allFolders.size(); i++) {
			String folderUri = ((Folder) allFolders.get(i)).getURIString();
			wrapper.getAllFolders().add(folderUri);
		}
	}

	/**
	 * handleTypeSelection receives input from mouse click and 
	 * converts to the connection type
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event handleTypeSelection(RequestContext context) throws Exception {
		OlapClientConnectionWrapper wrapper = 
			(OlapClientConnectionWrapper) getFormObject(context);
		String strType = wrapper.getType();
		log("Type=" + strType);

		if (strType != null) {
			if (strType.equals(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION)) {
				wrapper
						.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION); //.TYPE_MONDRIAN_SCHEMA
			} else if (strType.equals(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION)) {
				wrapper
						.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
			} else {
				throw new JSException("jsexception.connection.type.not.suported");
			}
		} else {
			throw new JSException("jsexception.no.connection.selected");
		}

		return success();
	}

	/**
	 * locateSchemaResource performs the action to load or create schema resource
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event locateSchemaResource(RequestContext context) throws Exception {
		log("In locateSchemaResource");
		OlapClientConnectionWrapper occWrapper = 
			(OlapClientConnectionWrapper) getFormObject(context);
		if (occWrapper == null) {
			throw new JSException("jsexception.no.olap.client.connection");
		} else {
			log("Olap client connection name=" + occWrapper.getConnectionName());
		}
		FileResourceWrapper frW = new FileResourceWrapper();
		FileResource fileR = (FileResource) repository.newResource(null,
				FileResource.class);
		String parentFolder = (String) context.getFlowScope().get(PARENT_FOLDER_ATTR);
		if (parentFolder == null) {
			parentFolder = context.getRequestParameters().get("ParentFolderUri");
			context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
		}
		fileR.setParentFolder(parentFolder);
		frW.setMode(BaseDTO.MODE_SUB_FLOW_NEW);
        fileR.setFileType(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA);
		frW.setFileResource(fileR);
		// Set current form object to validate duplicate resource names
		frW.setParentFlowObject(occWrapper);
		// Set the FileResourceWrapper object into scope for the subflow
		frW.setNewUri(occWrapper.getSchemaUri());
		frW.setSource(occWrapper.getSource());
		//context.getFlowScope().put(PARENT_FOLDER_ATTR,
		//		(String) context.getFlowScope().get(PARENT_FOLDER_ATTR));
		context.getFlowScope()
				.put(FileResourceAction.getFORM_OBJECT_KEY(), frW);
		occWrapper.setAccessGrant(false);
		return success();
	}

	/**
	 * locateDataSource performs the action to load or create data source
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event locateDataSource(RequestContext context) throws Exception {
		log("In locate data source");
		OlapClientConnectionWrapper wrapper = 
			(OlapClientConnectionWrapper) getFormObject(context);
		ResourceReference dsRef = new ResourceReference(wrapper
				.getDatasourceUri());
		ReportDataSourceWrapper odWrapper = new ReportDataSourceWrapper();
		odWrapper.setMode(BaseDTO.MODE_SUB_FLOW_NEW);
		odWrapper.setParentFlowObject(wrapper);
		odWrapper.setParentType(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA);
		if (dsRef.getReferenceURI() == null && dsRef.getLocalResource() == null) {
			log("Found no previous ReportDataSource, creating new");
			ReportDataSource ods = (ReportDataSource) repository.newResource(
					null, JdbcOlapDataSource.class);
			String parentFolder = (String)context.getFlowScope().get(PARENT_FOLDER_ATTR);
			if (parentFolder == null) {
				parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
				context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
			}
			ods.setParentFolder(parentFolder);
			dsRef = new ResourceReference(ods);
			odWrapper.setSource(constants.getFieldChoiceLocal());
			odWrapper.setType(constants.getJDBCDatasourceType());
			odWrapper.setReportDataSource(ods);
		} else {
			// if the dataSource exists decide source and type and set in
			// wrapper
			if (dsRef.isLocal()) { // [OK]
                odWrapper.setSource(constants.getFieldChoiceLocal());
                ReportDataSource ods = (ReportDataSource) dsRef
                        .getLocalResource();
                if (JdbcOlapDataSource.class.isAssignableFrom(ods.getClass())) {
                    log("Found JDBCOlapDataSource");
                    odWrapper.setType(constants.getJDBCDatasourceType());
                } else {
                    log("Found JndiJdbcOlapDataSourceLookup");
                    if (JndiJdbcOlapDataSource.class.isAssignableFrom(ods
                            .getClass()))
                        odWrapper.setType(constants.getJNDIDatasourceType());
                }
                odWrapper.setMode(BaseDTO.MODE_SUB_FLOW_EDIT);
                odWrapper.setReportDataSource(ods);
			} else {
				// DataSource object is a lookup
				log("Found ReportDataSourceLookup");
				odWrapper.setSource(constants.getFieldChoiceRepo());
			}
			// set the current selection
			odWrapper.setSelectedUri(dsRef.getReferenceURI());
		}
		// Set the object into scope with the name that the reportDataSourceFlow
		// can pickup
		//context.getFlowScope().put(PARENT_FOLDER_ATTR,
		//		(String) context.getFlowScope().get(PARENT_FOLDER_ATTR));
		context.getFlowScope().put(DATASOURCE_OBJECT_KEY,
				odWrapper);
		return success();
	}
	
	/**
	 * saveSchemaResource performs the action to save schema resource
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event saveSchemaResource(RequestContext context) throws Exception {
		log("In save schema resource");
		OlapClientConnectionWrapper wrapper = 
			(OlapClientConnectionWrapper) getFormObject(context);
		FileResourceWrapper frWrapper = (FileResourceWrapper) context
				.getFlowScope().get(SCHEMA_OBJECT_KEY);
		if (frWrapper == null) {
			throw new JSException("jsexception.got.null.schema.source.wrapper");
		} else
			log("type was " + frWrapper.getClass().getName());

		FileResource fileResource = frWrapper.getFileResource();
		if (fileResource == null) {
			throw new JSException("jsexception.got.null.schema.source.wrapper");
		} else
			log("type was " + frWrapper.getFileResource().getFileType());

		wrapper.setOlapClientSchema(fileResource);
		// bug 8707: missing schema uri
		wrapper.setSchemaUri(fileResource.getPath());
		if (frWrapper.getSource().equals(constants.getFieldChoiceFile())) {
			wrapper.setSchemaLoaded(true);
		} else {
			wrapper.setSchemaUri(frWrapper.getFileResource().getReferenceURI());
		}
		wrapper.setConnectionModified(true);
		return success();
	}
	
	/**
	 * saveDatasource performs the action to save data source
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */	
	public Event saveDatasource(RequestContext context) throws Exception {
		// Save the returned datasource info
        ReportDataSourceWrapper resource = (ReportDataSourceWrapper) context
                .getFlowScope().get(DATASOURCE_OBJECT_KEY);
        OlapClientConnectionWrapper wrapper = (OlapClientConnectionWrapper) getFormObject(context);
        if (resource == null) {
			throw new JSException("jsexception.got.null.schema.source.wrapper");
		} else {
			log("type was " + resource.getType());
        }
		if (resource.getSource().equals(constants.getFieldChoiceRepo())) {
			//if (wrapper.getOlapClientDatasource() == null) {
			// new olap unit using existing datasource			
			ReportDataSource datasource = (ReportDataSource) repository
					.getResource(JasperServerUtil.getExecutionContext(context), resource.getSelectedUri());
			resource.setReportDataSource(datasource);
			wrapper.setOlapClientDatasource(datasource);
			//}
			wrapper.setDatasourceUri(resource.getSelectedUri());
		} else {
			ReportDataSource datasource = resource.getReportDataSource();
			// bug 8325: missing datasource, i.e., none
			if (datasource == null || datasource.getName() == null) {
				log("No datasource specified for OLAP connection.");
                return no();
            }
			wrapper.setOlapClientDatasource(datasource);
            // bug 8707: missing datasource uri
			wrapper.setDatasourceUri(datasource.getPath());
            wrapper.setDatasourceAdded(true);
        }
		wrapper.setConnectionModified(true);
		return success();
	}

	/**
	 * saveMondrianConnection performs the action to save mondrian connection resource
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event saveMondrianConnection(RequestContext context)
			throws Exception {
		log("In saveMondrianConnection");
		OlapClientConnectionWrapper wrapper = (OlapClientConnectionWrapper) getFormObject(context);
		OlapUnitWrapper parentWrapper = (OlapUnitWrapper) wrapper
				.getParentFlowObject();
		MondrianConnection mondrianConnection = null;
		if (wrapper.getSource().equals(constants.FIELD_CHOICE_CONT_REPO)) {
			if (wrapper.getMode() == BaseDTO.MODE_SUB_FLOW_NEW) {
				// modify existing connection
				if (wrapper.getConnectionUri() != null) {
					mondrianConnection = (MondrianConnection) repository
							.getResource(null, wrapper.getConnectionUri());
				} else {
					throw new JSException("jsexception.connection.uri.not.specified");
				}
				parentWrapper.setOlapClientConnection(mondrianConnection);
				parentWrapper.setConnectionUri(wrapper.getConnectionUri());
				wrapper.setSchemaUri(mondrianConnection.getSchema()
						.getReferenceURI());
				wrapper.setDatasourceUri(mondrianConnection.getDataSource()
						.getReferenceURI());
				wrapper.setConnectionModified(false);
			} else {
				// change CONTENT_REPOSITORY to FILE_SYSTEM during update
				if (wrapper.isSchemaLoaded()) {
					repository.saveResource(null, wrapper.getOlapClientSchema());
				}
				if (wrapper.isDatasourceAdded()) {
					repository.saveResource(null, wrapper.getOlapClientDatasource());
				}
				if (wrapper.getMode() == BaseDTO.MODE_STAND_ALONE_EDIT) {
					mondrianConnection = (MondrianConnection) wrapper
							.getOlapClientConnection();
					setMondrianConnectionDetails(wrapper, mondrianConnection);
					getRepository().saveResource(null, mondrianConnection);
					return yes();
				} else {
					mondrianConnection = (MondrianConnection) repository
							.getResource(null, wrapper.getConnectionUri());
					getMondrianConnectionDetails(wrapper, mondrianConnection);
					wrapper.setConnectionModified(true);

				}
			}
		} else {
			// create new connection
			try {
				mondrianConnection = (MondrianConnection) repository
						.newResource(null, MondrianConnection.class);
			} catch (Exception e) {
				throw new JSException("jsexception.failed.to.create.connection");
			}
			// save schema resource
			if (wrapper.isSchemaLoaded()) {
				FileResource schema = wrapper.getOlapClientSchema();

				// check for XXE vulnerability
				XMLUtil.checkForXXE(schema.getData());

				if (!wrapper.isSubflowMode()) {
					schema.setURIString(wrapper.getSchemaUri());
					getRepository().saveResource(null, schema);
				} else {
					parentWrapper.setOlapClientSchema(schema);
					parentWrapper.setSchemaLoaded(true);
				}
			}
			// save data source
			if (wrapper.isDatasourceAdded()) {
				ReportDataSource datasource = wrapper.getOlapClientDatasource();
				if (!wrapper.isSubflowMode()) {
					datasource.setURIString(wrapper.getDatasourceUri());
					getRepository().saveResource(null, datasource);
				} else {
					parentWrapper.setOlapClientDatasource(datasource);
					parentWrapper.setDatasourceAdded(true);
				}
			}
			// persist connection, if new or changed
			if (wrapper.isConnectionModified()) {
				setMondrianConnectionDetails(wrapper, mondrianConnection);
			}
			// save conneciton depending on main flow vs. subflow
			if (!wrapper.isSubflowMode()) {
				// main flow
				mondrianConnection.setParentFolder((String) context
						.getFlowScope().get("parentFolder"));
				try {
					getRepository().saveResource(null, mondrianConnection);

                    if (!wrapper.isEditMode()) {
                        context.getExternalContext().getSessionMap().put("repositorySystemConfirm",
                                messages.getMessage("resource.analysisConnection.analysisConnectionAdded",
                                        new String[] {wrapper.getConnectionName(),
                                        wrapper.getParentFolder()},
                                        LocaleContextHolder.getLocale()));
                    }

					return yes();
				} catch (Exception e) {
					throw new JSException("jsexception.failed.to.save.connection.changes");
				}
			} else {
				// subflow
				mondrianConnection
						.setParentFolder(((OlapUnitWrapper) wrapper
								.getParentFlowObject()).getOlapUnit()
								.getParentFolder());
				parentWrapper.setOlapClientConnection(mondrianConnection);
				parentWrapper.setConnectionModified(true);
			}
		}
		wrapper.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION); //.TYPE_MONDRIAN_SCHEMA
		wrapper.setOlapClientConnection(mondrianConnection);
		return success();
	}	

	/**
	 * setMondrianConnectionDetails saves the changes to mondrian connection
	 * 
	 * @param wrapper
	 * @param mondrianConnection
	 */
	protected void setMondrianConnectionDetails(
			OlapClientConnectionWrapper wrapper,
			MondrianConnection mondrianConnection) {
		mondrianConnection.setName(wrapper.getConnectionName());
		mondrianConnection.setLabel(wrapper.getConnectionLabel());
		mondrianConnection.setDescription(wrapper.getConnectionDescription());
		mondrianConnection.setURIString(
				mondrianConnection.getParentFolder() + "/" + mondrianConnection.getName());
		// schema
		((MondrianConnection) mondrianConnection)
				.setSchemaReference(wrapper.getOlapClientSchema().getURIString());
		if (wrapper.getMode() == BaseDTO.MODE_SUB_FLOW_EDIT) {
			try {
				repository.getResource(
						null, wrapper.getOlapClientSchema().getReferenceURI());
			}
			catch (Exception e) {
				// TODO check exception type
				repository.saveResource(null, wrapper.getOlapClientSchema());
			}
		}
		// datasource
		((MondrianConnection) mondrianConnection)
				.setDataSourceReference(wrapper.getOlapClientDatasource().getURIString()); 
		if (wrapper.getMode() == BaseDTO.MODE_SUB_FLOW_EDIT) {
			try {
				repository.getResource(
						null, wrapper.getOlapClientDatasource().getURIString());
			}
			catch (Exception e) {
				// TODO check exception type
				repository.saveResource(null, wrapper.getOlapClientDatasource());
			}
		}		
	}
	
	/**
	 * getMondrianConnectionDetails buffers the changes to mondrian connection
	 * 
	 * @param wrapper
	 * @param mondrianConnection
	 */
	protected void getMondrianConnectionDetails(
			OlapClientConnectionWrapper wrapper,
			MondrianConnection mondrianConnection) {
		wrapper.setConnectionName(mondrianConnection.getName());
		wrapper.setConnectionLabel(mondrianConnection.getLabel());
		wrapper.setConnectionDescription(mondrianConnection.getDescription());
		// schema
		wrapper.setSchemaUri(
				((MondrianConnection) mondrianConnection).getSchema().getReferenceURI());
		// data source
		wrapper.setDatasourceUri(
				((MondrianConnection) mondrianConnection).getDataSource().getReferenceURI()); 
	}
	
	/**
	 * saveXmlaConnection stores XML/A connection information and 
	 * places its resource reference in olap unit.
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event saveXmlaConnection(RequestContext context) throws Exception { 
		log("In saveXmlaConnection");

        flushXmlaCache();

        OlapClientConnectionWrapper wrapper =
			(OlapClientConnectionWrapper) getFormObject(context);
		OlapUnitWrapper parentWrapper = (OlapUnitWrapper) wrapper
				.getParentFlowObject();
		XMLAConnection xmlaConnection = (XMLAConnection) wrapper
				.getOlapClientConnection();
		if (wrapper.getSource().equals(constants.FIELD_CHOICE_CONT_REPO)) {
			if (wrapper.getMode() == BaseDTO.MODE_SUB_FLOW_NEW) {
				if (wrapper.getConnectionUri() != null) {
					xmlaConnection = (XMLAConnection) repository.getResource(
							null, wrapper.getConnectionUri());
				} else {
					throw new JSException("jsexception.connection.uri.not.specified");
				}
				parentWrapper.setOlapClientConnection(xmlaConnection);
				parentWrapper.setConnectionUri(wrapper.getConnectionUri());
				wrapper.setConnectionModified(false);
			} else {
				if (wrapper.getMode() == BaseDTO.MODE_STAND_ALONE_EDIT) {
					setXmlaConnectionDetails(wrapper, xmlaConnection);
					repository.saveResource(null, xmlaConnection);
					return yes();
				} else {
					xmlaConnection = (XMLAConnection) repository.getResource(
							null, wrapper.getConnectionUri());
					getXmlaConnectionDetails(wrapper, xmlaConnection);
					wrapper.setConnectionModified(true);
				}
			}
		} else {
			xmlaConnection = (XMLAConnection) repository.newResource(null,
					XMLAConnection.class);
			xmlaConnection.setParentFolder(wrapper.getParentFolder());
			setXmlaConnectionDetails(wrapper, xmlaConnection);
			// new xmla connection
			if (!wrapper.isSubflowMode()) {
				try {
					repository.saveResource(null, xmlaConnection); // wrapper.setConnectionChanged(true);

                    if (!wrapper.isEditMode()) {
                        context.getExternalContext().getSessionMap().put("repositorySystemConfirm",
                                messages.getMessage("resource.analysisConnection.analysisConnectionAdded",
                                        new String[] {wrapper.getConnectionName(),
                                        wrapper.getParentFolder()},
                                        LocaleContextHolder.getLocale()));
                    }

					return yes();
				} catch (Exception e) {
					throw new JSException("jsexception.failed.to.save.connection");
				}				
			} else {
				xmlaConnection
						.setParentFolder(((OlapUnitWrapper) wrapper
								.getParentFlowObject()).getOlapUnit()
								.getParentFolder());
				parentWrapper.setOlapClientConnection(xmlaConnection);
				parentWrapper.setConnectionModified(true);
				wrapper.setConnectionModified(true); // bug #9015: can now save locally defined xml/a connection as subflow
			}
		}
		wrapper.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
		wrapper.setOlapClientConnection(xmlaConnection);
		return success();
	}
	

	/**
	 * setXmlaConnectionDetails saves changes to xmla connection
	 * 
	 * @param wrapper
	 * @param xmlaConnection
	 */
	private void setXmlaConnectionDetails(
			OlapClientConnectionWrapper wrapper, XMLAConnection xmlaConnection) 
	{
        xmlaConnection.setName(wrapper.getConnectionName());
        xmlaConnection.setLabel(wrapper.getConnectionLabel());
        xmlaConnection.setDescription(wrapper.getConnectionDescription());
        
		xmlaConnection.setCatalog(wrapper.getXmlaCatalog());
        xmlaConnection.setDataSource(wrapper.getXmlaDatasource()); 
        xmlaConnection.setURI(wrapper.getXmlaConnectionUri());
        
        xmlaConnection.setUsername(wrapper.getUsername());

        // Ignore password update if substitution is set (~value-subst~)
        if (!wrapper.getPassword().equals(messages.getMessage("input.password.substitution", null, LocaleContextHolder.getLocale()))) {
            xmlaConnection.setPassword(wrapper.getPassword());
        }

	}

	/**
	 * getXmlaConnectionDetails buffers changes to xmla connection
	 * @param wrapper
	 * @param xmlaConnection
	 */
	private void getXmlaConnectionDetails(
			OlapClientConnectionWrapper wrapper, XMLAConnection xmlaConnection) 
	{
		wrapper.setConnectionName(xmlaConnection.getName());
		wrapper.setConnectionLabel(xmlaConnection.getLabel());
		wrapper.setConnectionDescription(xmlaConnection.getDescription());
        
		wrapper.setXmlaCatalog(xmlaConnection.getCatalog());
		wrapper.setXmlaDatasource(xmlaConnection.getDataSource());
		wrapper.setXmlaConnectionUri(xmlaConnection.getURI());
        
		wrapper.setUsername(xmlaConnection.getUsername());
		wrapper.setPassword(xmlaConnection.getPassword());
	}
	
	/**
	 * createFormObject set the form object 
	 * @param context
	 * @return Object
	 */
	public Object createFormObject(RequestContext context) {
		OlapClientConnectionWrapper formObject = null;
		String resourceUri = context.getRequestParameters().get(OU_URI_PARAM);
		if (resourceUri == null) {
			resourceUri = context.getRequestParameters().get("selectedResource");
		}
		if (resourceUri != null && resourceUri.trim().length() != 0) {
			try {
			    formObject = (OlapClientConnectionWrapper)
				getFormObjectClass().newInstance();
			} catch (Exception e) {
			    log.error("FormObjectClass invalid: " + getFormObjectClass());
			}
			
			OlapClientConnection occ =  
				(OlapClientConnection) repository.getResource(null, resourceUri);
			
			// connection info
			formObject.setOlapClientConnection(occ); 
			formObject.setConnectionName(occ.getName());
			formObject.setConnectionLabel(occ.getLabel());
			formObject.setConnectionDescription(occ.getDescription());
			
			// connection-specifics
			if (occ instanceof MondrianConnection) {
				getMondrianConnectionInfo(occ, formObject);
			} else if (occ instanceof XMLAConnection) {
				XMLAConnection xmlac = (XMLAConnection) occ;
				formObject.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION); 
				formObject.setXmlaCatalog(xmlac.getCatalog());
				formObject.setXmlaDatasource(xmlac.getDataSource());
				formObject.setXmlaConnectionUri(xmlac.getURI());
				formObject.setUsername(xmlac.getUsername());
				formObject.setPassword(xmlac.getPassword());
			}
			else {
				log("Unknown connection type");
			}
			
			formObject.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
			formObject.setDatasourceIdentified(true);
			formObject.setNamed(true);
		}
		
		if (formObject == null) {
			log("OlapUnitAction: Stand alone new mode");
			try {
			    formObject = (OlapClientConnectionWrapper)
				getFormObjectClass().newInstance();
			} catch (Exception e) {
			    log.error("FormObjectClass invalid: " + getFormObjectClass());
			}
			formObject.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
			//String parentFolder = (String) context.getFlowScope().get(
			//		PARENT_FOLDER_ATTR);
			formObject.setSource(constants.FIELD_CHOICE_FILE_SYSTEM);
		}
		return formObject;
	}

	protected void getMondrianConnectionInfo(OlapClientConnection occ,
			OlapClientConnectionWrapper formObject) {
		MondrianConnection mc = (MondrianConnection) occ;
		formObject
				.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION); //.TYPE_MONDRIAN_SCHEMA
		String uri = mc.getSchema().getReferenceURI();
		formObject.setOriginalSchemaUri(uri);
		formObject.setSchemaUri(uri);
		formObject.setDatasourceUri(mc.getDataSource().getReferenceURI());
		formObject.setSchemaLocated(true);
	}

	/**
	 * validateOlapConnection performs validate for olap view
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event validateOlapConnection(RequestContext context) throws Exception {
		log("In Validate OLAP Connection");
		// TODO 
		OlapClientConnectionWrapper wrapper = 
			(OlapClientConnectionWrapper) getFormObject(context);
		OlapClientConnection connection = wrapper.getOlapClientConnection();
		if (wrapper.getOlapConnectionType().equals(
				ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION)) {
			// validate mondrian connection
		}
		else if (wrapper.getOlapConnectionType().equals(
				ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION)) {
			// validate xmla connection
		}
		else {
			throw new JSException("jsexception.unknown.connection.type");
		}
		// TODO activate
//		ValidationResult result = olapConnection.validate(null, conection);
//		wrapper.setResult(result.getValidationState().equals(
//				ValidationResult.STATE_VALID));
		return success();
	}

    /**
     * testXMLAConnection performs validate for XML/A connection
     *
     * @param context
     * @return
     * @throws Exception
     */
    public Event testXMLAConnection(RequestContext context) throws Exception
    {
        flushXmlaCache();

        OlapClientConnectionWrapper wrapper = (OlapClientConnectionWrapper) getFormObject(context);

        OlapConnectionService service = getOlapConnection();
        XMLAConnection connection = (XMLAConnection) wrapper.getOlapClientConnection();

        // If null then connection is new. Simply creating a new object for test
        if (connection == null) {
            connection = new XMLAConnectionImpl();
        }
        setXmlaConnectionDetails(wrapper, connection);

        XMLATestResult result = service.testConnection(null, connection);

        context.getRequestScope().put(AJAX_RESPONSE_MODEL, result.buildJson());
        return success();
    }


    /**
	 * getRepository returns repository service property
	 * 
	 * @return
	 */
	public RepositoryService getRepository() {
		return repository;
	}
	
	/**
	 * setRepository sets repository service property 
	 * 
	 * @param repository
	 */
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

    public void setDataSourceTreeDataProvider(TypedTreeDataProvider typedTreeDataProvider) {
        this.typedTreeDataProvider = typedTreeDataProvider;
    }

	public void setJrxmlTreeDataProvider(BaseTreeDataProvider typedTreeDataProvider) {
        this.jrxmlTreeDataProvider = typedTreeDataProvider;
    }

	/**
	 * method to get the olap connection service object
	 * 
	 * @return
	 */
	public OlapConnectionService getOlapConnection() {
		return this.olapConnection;
	}
	
	/**
	 * method to set the olap connection service object
	 * 
	 * @param olapConnection
	 */
	public void setOlapConnection(OlapConnectionService olapConnection) {
		this.olapConnection = olapConnection;
	}

	/**
	 * method to get the engine service object
	 * 
	 * @return
	 */
	public EngineService getEngine() {
		return engine;
	}

	/**
	 * method to set the engine service object
	 * 
	 * @param engine
	 */
	public void setEngine(EngineService engine) {
		this.engine = engine;
	}

	/**
	 * Registers a byte array editor to allow spring handle File uploads as byte
	 * arrays
	 * 
	 * @param context
	 * @param binder
	 */
	protected void initBinder(RequestContext context, DataBinder binder) {
		binder.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
	}

	/**
	 * log logs debug message
	 * 
	 * @param text
	 */
	protected void log(String text) {
		log.debug(text);
	}

    private void flushXmlaCache() {
        try {
            Class clazz = Class.forName(classOlap4jCache);

            // Instantiates it
            XmlaOlap4jCache cache = (XmlaOlap4jCache) clazz.newInstance();

            cache.flushCache();
        } catch (Exception ex) {
            log.error("Xmla cache can not be flushed due of " + ex.getMessage());
        }
    }

}
