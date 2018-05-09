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

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.MondrianConnectionImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerConstImpl;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.OlapClientConnectionWrapper;
import com.jaspersoft.jasperserver.war.dto.OlapUnitWrapper;
import com.jaspersoft.jasperserver.war.model.impl.TypedTreeDataProvider;
import com.jaspersoft.jasperserver.war.validation.OlapUnitValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * OlapUnitAction provides the actions for the olap view web flow
 *
 * @author jshih
 * @revision $Id$
 */
public class OlapUnitAction extends FormAction {
    private static final String ATTRIBUTE_RESOURCE_ID_NOT_SUPPORTED_SYMBOLS = "resourceIdNotSupportedSymbols";

    private static final String OU_URI_PARAM = "resource";
    private static final String FORM_OBJECT_KEY = "wrapper";
    private static final String OLAP_UNIT_SUFFIX = "_unit";
    private static final String OLAP_UNIT_KEY = "unit";
    private static final String CONTROL_OBJECT_KEY = "control";
    private static final String RESOURCE_OBJECT_KEY = "resource";
    private static final String DATASOURCE_OBJECT_KEY = "dataResource";
    private static final String CONNECTION_OBJECT_KEY = "connectionWrapper";
    private static final String PARENT_FOLDER_ATTR = "parentFolder";
    private static final String PARAM_FROM_PAGE = "frompage";
    private static final String PARAM_RESOURCE_NAME = "resourceName";
    private static final String LOCAL_SCHEMA_SUFFIX = "_schema";
    private static final String UNPARSABLE_SCHEMA_ATTR = "schemaUnparsable";
    private static final String INVALID_XMLA_CONNECTION_ATTR = "xmlaConnectionInvalid";
    private static final String UNPARSABLE_XMLA_ATTR = "xmlaUnparsable";
    private static final String UNPARSABLE_CONNECTION_ATTR = "connectionUnparsable";
    private static final String OLAP_TREE_DATA_PROVIDER = "OLAPTreeDataProvider";
    private static final String MONDRIAN_TREE_DATA_PROVIDER = "MondrianTreeDataProvider";
    private static final String XMLA_TREE_DATA_PROVIDER = "XMLATreeDataProvider";
    protected final Log log = LogFactory.getLog(this.getClass());
    protected RepositoryService repository;
    protected OlapConnectionService olapConnection;
    private EngineService engine;
    protected JasperServerConstImpl constants = new JasperServerConstImpl();
    private TypedTreeDataProvider oLAPTreeDataProvider;
    private TypedTreeDataProvider mondrianTreeDataProvider;
    private TypedTreeDataProvider xMLATreeDataProvider;

    private ConfigurationBean configuration;

    /**
     * initialize OlapUnitAction.class object
     */
    public OlapUnitAction() {
	setFormObjectClass(OlapUnitWrapper.class);
	setFormObjectName(FORM_OBJECT_KEY);
	setFormObjectScope(ScopeType.FLOW);
    }

    /**
     * initAction performs the initialization for the olap view (unit) web flow
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public Event initAction(RequestContext context) throws Exception {
	((OlapUnitValidator)getValidator()).setConnectionService(olapConnection);
	OlapUnitWrapper wrapper = (OlapUnitWrapper) getFormObject(context);
    context.getExternalContext().getSessionMap().put(OLAP_TREE_DATA_PROVIDER, oLAPTreeDataProvider);
    context.getExternalContext().getSessionMap().put(XMLA_TREE_DATA_PROVIDER, xMLATreeDataProvider);
    context.getExternalContext().getSessionMap().put(MONDRIAN_TREE_DATA_PROVIDER, mondrianTreeDataProvider);
	if (wrapper.isNewMode()) {
	    // create olap view
	    // this entire if case seems unnecessary --
	    // there are no resources, and null is the default for "/"
	} else {
	    // modify olap view
	    // this case seems to be initializing the FormObject wrapper
	    // from the state of the existing olap unit... good.
	    OlapUnit ou = wrapper.getOlapUnit();
	    wrapper.setOlapUnitName(ou.getName());
	    wrapper.setOlapUnitLabel(ou.getLabel());
	    wrapper.setOlapUnitDescription(ou.getDescription());
        final ResourceReference olapClientConnectionRef = ou.getOlapClientConnection();
        final OlapClientConnection resource = (OlapClientConnection) (olapClientConnectionRef.isLocal() ? olapClientConnectionRef.getLocalResource() : repository
                .getResource(null, olapClientConnectionRef
                        .getReferenceURI()));
        wrapper.setOlapClientConnection(resource);
	    wrapper.setOlapUnitMdxQuery(ou.getMdxQuery());
	    wrapper.setOlapUnitOptions(ou.getOlapViewOptions());
	    // set the connectin type to enable testConnectionType in webflow
	    if (wrapper.getOlapClientConnection() instanceof MondrianConnection) {
		wrapper.setType(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION);
	    } else if (wrapper.getOlapClientConnection() instanceof XMLAConnection) {
		wrapper.setType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
	    } else {
		log("Unknown connection type.");
	    }
	}
	/**
	 * TODO(stas): Remove next lines. I didn't find any usage of results
	 */
//	getAllConnections(context, wrapper);
//	findAllSchemas(context, wrapper);
//	getAllXmlaSources(context, wrapper);
	wrapper.setParentFolder((String) context.getFlowScope().get(
								    PARENT_FOLDER_ATTR));
	context.getFlowScope().put(FORM_OBJECT_KEY, wrapper);
	context.getFlowScope().put("constants", constants);

        context.getFlowScope().put(ATTRIBUTE_RESOURCE_ID_NOT_SUPPORTED_SYMBOLS,
                configuration.getResourceIdNotSupportedSymbols());

	return success();
    }
	
    /**
     * findAllSchemas finds all mondrian olap schema
     *
     * @param context
     * @param wrapper
     */
    @Deprecated
    private void findAllSchemas(RequestContext context, OlapUnitWrapper wrapper)
    {
	FilterCriteria filterCriteria = FilterCriteria
	    .createFilter(FileResource.class);
	filterCriteria.addFilterElement(FilterCriteria.createPropertyEqualsFilter(
										  "fileType", ResourceDescriptor.TYPE_MONDRIAN_SCHEMA));
	filterCriteria.addFilterElement(FilterCriteria.createPropertyEqualsFilter(
		  "fileType", ResourceDescriptor.TYPE_ACCESS_GRANT_SCHEMA));
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
			if (!allSources.contains(((FileResource) resourceObj).getURIString())) {
				allSources.add(((FileResource) resourceObj).getURIString());
			}
			log("added uri=" + resource.getURIString());
	    }
	    wrapper.setReusableSchemas(allSources);
	}
    }

    /**
     * getAllConnections finds all olap client connections
     *
     * @param context
     * @param wrapper
     */
    @Deprecated
    private void getAllConnections(RequestContext context, OlapUnitWrapper wrapper) {
	// this seems fine, but i wish we could do away with all
	// non-reusable connections -- the inline connection makes
	// the number of cases more difficult.
	FilterCriteria filterCriteria = FilterCriteria
	    .createFilter(OlapClientConnection.class);
	ResourceLookup[] resourceLookup = repository.findResource(JasperServerUtil.getExecutionContext(context),
								  filterCriteria);
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
				if (!allMondrianConnections
				.contains(((OlapClientConnection) resourceObj)
						.getURIString())) {
				allMondrianConnections
					.add(((OlapClientConnection) resourceObj)
							.getURIString());
				}
			} else if (resourceObj instanceof XMLAConnection) {
				if (!allXmlaConnections
				.contains(((OlapClientConnection) resourceObj)
						.getURIString())) {
				allXmlaConnections
					.add(((OlapClientConnection) resourceObj)
							.getURIString());
				}
			} else {
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
    private void getAllXmlaSources(RequestContext context, OlapUnitWrapper wrapper) {
	FilterCriteria filterCriteria =
	    FilterCriteria.createFilter(MondrianXMLADefinition.class);
	ResourceLookup[] resourceLookup = repository.findResource(JasperServerUtil.getExecutionContext(context), filterCriteria);
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

			if (!allXmlaDefinitions.contains(((MondrianXMLADefinition) resourceObj).getURIString())) {
				allXmlaDefinitions.add(((MondrianXMLADefinition) resourceObj).getCatalog());
			}
	    }
	    wrapper.setReusableXmlaDefinitions(allXmlaDefinitions);
	}
    }

    /**
     * handleTypeSelection receives input from mouse click and 
     * converts to the connection type.
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public Event handleTypeSelection(RequestContext context) throws Exception {
	OlapUnitWrapper wrapper = (OlapUnitWrapper) getFormObject(context);
	String strType = wrapper.getType();
	log("Type" + strType);

	if (strType != null) {
	    if (strType.equals(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION)) {
		wrapper.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION);
	    } else if (strType.equals(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION)) {
		wrapper.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
	    } else {
		throw new JSException("jsexception.connection.type.not.suported");
	    }
	} else {
	    throw new JSException("jsexception.no.connection.selected");
	}

	return success();
    }

    /**
     * locateOlapConnection creates or modify olap client connection
     * 
     * @param context
     * @return
     * @throws Exception
     */

    public Event locateOlapConnection(RequestContext context) throws Exception {
	//     new OU?  |  new OCC? |  test
	//     ----------------------------------------------------------
	//         no   |     no    |  (!wrapper.isNew() && cw.mode==SUB_FLOW_EDIT)
	//         no   |    yes    |  create connection, save local
	//        yes   |     no    |  n2
	//        yes   |    yes    |  new inline local connection
	log("In locate olap connection");
	OlapUnitWrapper wrapper = (OlapUnitWrapper) getFormObject(context);
	OlapClientConnection connection = wrapper.getOlapClientConnection();

	OlapClientConnectionWrapper connectionWrapper;
	try {	    
	    connectionWrapper = (OlapClientConnectionWrapper)
		Class.forName(getConnectionWrapperClass()).newInstance();
	} catch (Exception e) {
	    log.error("error creating connection wrapper class: " + e);
	    connectionWrapper = new OlapClientConnectionWrapper();	
	}

	if (connection == null) {
	    // new connection
	    log("Found no previous OlapClientConnection, creating new");
	    connectionWrapper.setMode(BaseDTO.MODE_SUB_FLOW_NEW);
	    connectionWrapper.setParentFolder(wrapper.getParentFolder());
	} else {
	    // edit connection
		log("Found previous OlapClientConnection");
	    connectionWrapper.setOlapConnectionType(wrapper
						    .getOlapConnectionType()); //.TYPE_MONDRIAN_SCHEMA or .TYPE_XMLA_SCHEMA
	    connectionWrapper.setSource(wrapper.getSource());
	    connectionWrapper.setType(wrapper.getType());
	    connectionWrapper.setConnectionUri(wrapper.getOlapUnit()
					       .getOlapClientConnection().getTargetURI());
        connectionWrapper.setParentFolder(connection.getParentFolder());
	    connectionWrapper.setMode(BaseDTO.MODE_SUB_FLOW_EDIT);
	}
	connectionWrapper.setParentFlowObject(wrapper);
	context.getFlowScope().put(CONNECTION_OBJECT_KEY, connectionWrapper);
	context.getFlowScope().put("constants", constants);
	return success();
    }
	
    /**
     * saveOlapClientConnection saves changes to olap client connection
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public Event saveOlapClientConnection(RequestContext context) throws Exception {
        // TODO remove
        OlapUnitWrapper wrapper = (OlapUnitWrapper) getFormObject(context);

        OlapClientConnectionWrapper connectionWrapper = (OlapClientConnectionWrapper) context
                .getFlowScope().get(CONNECTION_OBJECT_KEY);

        OlapClientConnection connection = connectionWrapper.getOlapClientConnection();

        if (connection == null) {
            throw new JSException("jsexception.got.null.connection.back");
        } else
            log("type was " + connectionWrapper.getOlapConnectionType());

        // update olapUnitWrapper with connection info from olapClientConnectionWrapper
        wrapper.setOlapClientConnection(connectionWrapper.getOlapClientConnection());
        wrapper.setOlapConnectionType(connectionWrapper.getOlapConnectionType());
        wrapper.setSchemaUri(connectionWrapper.getSchemaUri());
        wrapper.setType(connectionWrapper.getType());
            wrapper.getOlapUnit().setOlapClientConnectionReference(connectionWrapper.getConnectionUri());
        wrapper.setConnectionModified(connectionWrapper.isConnectionModified());

        return success();
    }

    /**
     * saveOlapUnit saves changes to olap view (unit)
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public Event saveOlapUnit(RequestContext context) throws Exception {
	log("In saveOlapUnit");
	OlapUnitWrapper wrapper = (OlapUnitWrapper) getFormObject(context);
	OlapUnit ou = wrapper.getOlapUnit();
	ou.setName(wrapper.getOlapUnitName()); 
	ou.setLabel(wrapper.getOlapUnitLabel()); 
	ou.setDescription(wrapper.getOlapUnitDescription()); 

	if (wrapper.getOlapClientConnection() != null) {
		ou.setOlapClientConnection(wrapper.getOlapClientConnection());
		if (wrapper.getOlapClientConnection().getURIString() != null) {
			ou.setOlapClientConnectionReference(wrapper.getOlapClientConnection().getURIString());
		}
		else {
			throw new JSException("jsexception.missing.olap.connection.reference");
		}
	}
	else {
		throw new JSException("jsexception.missing.olap.connection");
	}
	ou.setMdxQuery(wrapper.getOlapUnitMdxQuery());
	ou.setOlapViewOptions(wrapper.getOlapUnitOptions());
	
	OlapClientConnection connection = wrapper.getOlapClientConnection();
	OlapClientConnectionWrapper connectionWrapper = 
	    (OlapClientConnectionWrapper) context.getFlowScope().get(CONNECTION_OBJECT_KEY);
	wrapper.setConnectionModified(connectionWrapper.isConnectionModified());
	//ExecutionContextImpl executionContext = new ExecutionContextImpl();
	if (wrapper.isConnectionModified()) {
	    if (connection instanceof MondrianConnection) {
		// schema
		FileResource schema = wrapper.getOlapClientSchema();
		if (wrapper.isSchemaLoaded()) {
		    schema = wrapper.getOlapClientSchema();
		    try {
			repository.saveResource(null, schema);
		    } catch (Exception e) {
			throw new JSException(e);
		    }
		    schema.setReferenceURI(schema.getURIString());
		    ((MondrianConnection) connection).setSchemaReference(
									 schema.getReferenceURI());
		} else {
		    ((MondrianConnection) connection).setSchemaReference(
									 connectionWrapper.getSchemaUri());
		}
		// data source
		ReportDataSource datasource = wrapper.getOlapClientDatasource();
		if (wrapper.isDatasourceAdded()) {
		    try {
			repository.saveResource(null, datasource);
		    } catch (Exception e) {
			throw new JSException(e);
		    }

		    ((MondrianConnection) connection).setDataSourceReference(
									     datasource.getURIString());
		} else {
		    ((MondrianConnection) connection).setDataSourceReference(
									     connectionWrapper.getDatasourceUri());
		}
	    } else if (connection instanceof XMLAConnection) {
		connection.setName(connectionWrapper.getConnectionName());
		connection.setLabel(connectionWrapper.getConnectionLabel());
		connection.setDescription(connectionWrapper
					  .getConnectionDescription());

		((XMLAConnection) connection).setCatalog(
							 connectionWrapper.getXmlaCatalog());
		((XMLAConnection) connection).setURI(
						     connectionWrapper.getXmlaConnectionUri());
		((XMLAConnection) connection).setDataSource(
							    connectionWrapper.getXmlaDatasource());
		((XMLAConnection) connection).setUsername(
							  connectionWrapper.getUsername());
		((XMLAConnection) connection).setPassword(
							  connectionWrapper.getPassword());
	    } else {
		throw new JSException("jsexception.unknown.connection.type");
	    }
			
	    if (connectionWrapper.getMode() == BaseDTO.MODE_SUB_FLOW_EDIT) {
			if (connection.getParentFolder() == null) 
				connection.setParentFolder(wrapper.getParentFolder());
		} else {
			connection.setParentFolder(connectionWrapper.getParentFolder());
		try {
		    repository.saveResource(null, connection);
		} catch (Exception e) {
		    throw new JSException(e);
		}
	    }
	}
        // set local resource or resource reference checking parent folder URI
        String localResourcesFolderUri = ou.getURIString() + "_files";
        if(localResourcesFolderUri.equals(connection.getParentFolder())){
            ou.setOlapClientConnection(connection);
        } else {
            ou.setOlapClientConnectionReference(connection.getURIString());
        }
	try {
		repository.saveResource(null, ou);
	} catch (JSDuplicateResourceException e) {
		getFormErrors(context).rejectValue("olapUnitName", "OlapUnitValidator.error.duplicate");
		return error();
	}
	return success();
    }
	
    /**
     * saveXmlaConnection stores XML/A connection information and 
     * places its resource reference in olap unit
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public Event saveXmlaConnection(RequestContext context) throws Exception {
	OlapUnitWrapper wrapper = (OlapUnitWrapper) getFormObject(context);
	OlapUnit ou = wrapper.getOlapUnit();

	XMLAConnection xmlaConnection = 
	    (XMLAConnection) repository.newResource(null, XMLAConnection.class);

	xmlaConnection.setName(wrapper.getOlapUnitName());
	xmlaConnection.setLabel(wrapper.getOlapUnitLabel());
	xmlaConnection.setDescription(wrapper.getOlapUnitDescription());

	xmlaConnection.setCatalog(wrapper.getXmlaCatalog());
	xmlaConnection.setDataSource(wrapper.getXmlaDatasource());
	xmlaConnection.setURI(wrapper.getXmlaConnectionUri());

	ou.setOlapClientConnection(xmlaConnection);
	ou.setOlapClientConnectionReference(xmlaConnection.getURIString());
	wrapper.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
	wrapper.setOlapClientConnection(xmlaConnection);

	wrapper.setConnectionModified(true);
	return success();
    }
	
    /**
     * createFormObject loads form object
     */
    public Object createFormObject(RequestContext context) {
	OlapUnitWrapper formObject = null;
	String resourceUri = context.getRequestParameters().get(OU_URI_PARAM);
	if (resourceUri != null && resourceUri.trim().length() != 0) {
	    OlapUnit ou = (OlapUnit) repository.getResource(null,
							    resourceUri);
	    if (ou == null)
		throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {resourceUri});
	    log("Found resource with uri=" + resourceUri);
	    formObject = new OlapUnitWrapper();
	    formObject.setOlapUnit(ou); 
	    ResourceReference olapClientConnectionRef = ou.getOlapClientConnection();
	    OlapClientConnection occ =  
		(OlapClientConnection) (olapClientConnectionRef.isLocal() ? olapClientConnectionRef.getLocalResource() :
                repository.getResource(null, olapClientConnectionRef.getReferenceURI()));
	    formObject.setOlapClientConnection(occ);
	    if (occ instanceof MondrianConnection) {
		formObject.getOlapUnit().setDataSource(((MondrianConnection) occ).getDataSource()); //?
		formObject.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION);
	    }
	    else if (occ instanceof XMLAConnection) {
		formObject.setOlapConnectionType(ResourceDescriptor.TYPE_OLAP_XMLA_CONNECTION);
	    }
	    else {
		throw new JSException("jsexception.unknown.connection.type");
	    }
			
	    String uri = null;
	    if (!olapClientConnectionRef.isLocal()) {
		formObject.setSource(constants.FIELD_CHOICE_CONT_REPO);
		if (formObject.getOlapConnectionType().equals(ResourceDescriptor.TYPE_OLAP_MONDRIAN_CONNECTION)) {
		    uri = ((MondrianConnectionImpl) occ).getSchema().getReferenceURI();					
		}
		formObject.setSchemaUri(uri);
	    } else {
		formObject.setSource(constants.FIELD_CHOICE_FILE_SYSTEM);
		uri = olapClientConnectionRef.getLocalResource().getURIString();
	    }
	    formObject.setOldSchemaUri(uri);
	    formObject.setOriginalSchemaUri(uri);
	    formObject.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
	    formObject.setDatasourceIdentified(true);
	    formObject.setNamed(true);
	    formObject.setSchemaLocated(true);
	}
		
	if (formObject == null) {
	    log("OlapUnitAction: Stand alone new mode");
	    formObject = new OlapUnitWrapper();
	    OlapUnit ou = (OlapUnit) repository.newResource(null,
							    OlapUnit.class);
	    FileResource schema = (FileResource) repository.newResource(null,
									FileResource.class);
	    schema.setFileType(ResourceDescriptor.TYPE_MONDRIAN_SCHEMA);
	    ou.setOlapClientConnectionReference((String) schema.getReferenceURI());
	    formObject.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
	    String parentFolder = (String) context.getFlowScope().get(
								      PARENT_FOLDER_ATTR);
	    if (parentFolder == null) {
	    	parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
			context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);	    	
	    }
	    ou.setParentFolder(parentFolder);
	    formObject.setOlapUnit(ou);
	    formObject.setSource(constants.FIELD_CHOICE_FILE_SYSTEM);
	}
	return formObject;
    }

	// FIXME this method is deprecated (see validateMdxQuery() in OlapUnitvalidator)
    public Event validateOlapUnit(RequestContext context) throws Exception {
	// there are a lot of side effects here for validating
	log("In Validate OLAP Unit");
	OlapUnitWrapper wrapper = (OlapUnitWrapper) getFormObject(context);
	OlapUnit ou = wrapper.getOlapUnit(); 
	ou.setName(wrapper.getOlapUnitName()); 
	ou.setLabel(wrapper.getOlapUnitLabel()); 
	ou.setDescription(wrapper.getOlapUnitDescription()); 

	ou.setOlapClientConnectionReference(wrapper.getConnectionUri()); // ???
	ou.setOlapClientConnection(wrapper.getOlapClientConnection());
	ou.setMdxQuery(wrapper.getOlapUnitMdxQuery());
	ExecutionContextImpl executionContext = new ExecutionContextImpl();
	FileResource schema = wrapper.getOlapClientSchema();
	OlapClientConnection conn = wrapper.getOlapClientConnection();
	ReportDataSource datasource = wrapper.getOlapClientDatasource();
	ValidationResult result = null;
	try {
	    result = olapConnection.validate(executionContext, 
					     ou,
					     schema,
					     conn,
					     datasource);
	    wrapper.setResult(result.getValidationState().
			      equals(ValidationResult.STATE_VALID));
	    if (result.getValidationState().equals(ValidationResult.STATE_ERROR)) {
		return error();
	    }
	}
	catch (Exception e) {
	    // TODO fix
	    wrapper.setResult(result.getValidationState().equals(
								 ValidationResult.STATE_ERROR));
	    log.error(e.getStackTrace());
	}
	wrapper.setOlapUnit(ou);
	return success();
    }

    private String connectionWrapperClass;
    public String getConnectionWrapperClass() {
	return connectionWrapperClass;
    }
    public void setConnectionWrapperClass(String s) {
	connectionWrapperClass = s;
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

    public TypedTreeDataProvider getOLAPTreeDataProvider() {
        return oLAPTreeDataProvider;
    }

    public void setOLAPTreeDataProvider(TypedTreeDataProvider oLAPTreeDataProvider) {
        this.oLAPTreeDataProvider = oLAPTreeDataProvider;
    }

    public TypedTreeDataProvider getXMLATreeDataProvider() {
        return xMLATreeDataProvider;
    }

    public void setXMLATreeDataProvider(TypedTreeDataProvider xMLATreeDataProvider) {
        this.xMLATreeDataProvider = xMLATreeDataProvider;
    }

    public TypedTreeDataProvider getMondrianTreeDataProvider() {
        return mondrianTreeDataProvider;
    }

    public void setMondrianTreeDataProvider(TypedTreeDataProvider mondrianTreeDataProvider) {
        this.mondrianTreeDataProvider = mondrianTreeDataProvider;
    }

    public void setConfiguration(ConfigurationBean configuration) {
        this.configuration = configuration;
    }
}
