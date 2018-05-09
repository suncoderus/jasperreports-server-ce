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

import com.jaspersoft.jasperserver.war.common.ConfigurationBean;

import com.jaspersoft.jasperserver.war.model.impl.TypedTreeDataProvider;
import com.jaspersoft.jasperserver.api.metadata.olap.service.UpdatableXMLAContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.BaseDTO;
import com.jaspersoft.jasperserver.war.dto.MondrianXmlaSourceWrapper;

/**
 * The EditMondrianXmlaSourceAction class provides action methods for 
 * the mondrianXmlaSourceFlow web flow
 * 
 * @author jshih
 */
public class EditMondrianXmlaSourceAction extends FormAction {
    private static final String ATTRIBUTE_RESOURCE_ID_NOT_SUPPORTED_SYMBOLS = "resourceIdNotSupportedSymbols";

	public final Log log = LogFactory.getLog(this.getClass());
	private static final String FORM_OBJECT_KEY = "mondrianXmlaSource";
	private static final String PARENT_FOLDER_ATTR = "parentFolder";
	private static final String CURRENT_MONDRIAN_XMLA_DEFINITION_ATTR = "currentMondrianXmlaDefinition";
	private static final String IS_EDIT = "isEdit";//FIXME use wrapper to disable name in UI
    private static final String MONDRIAN_TREE_DATA_PROVIDER = "mondrianTreeDataProvider";
	private RepositoryService repository;
	private OlapConnectionService connectionService;
    private UpdatableXMLAContainer updatableXMLAContainer;

    private ConfigurationBean configuration;
    private TypedTreeDataProvider typedTreeDataProvider;

	/**
	 * initialize EditMondrianXmlaSourceAction.class object
	 */
	public EditMondrianXmlaSourceAction() {
		setFormObjectClass(MondrianXmlaSourceWrapper.class);
		setFormObjectName(FORM_OBJECT_KEY);
		setFormObjectScope(ScopeType.FLOW);
	}
	
	/**
	 * createFormObject initializes form object
	 * 
	 * @param context 
	 * @return wrapper
	 */
	public Object createFormObject(RequestContext context) {
		MondrianXMLADefinition mondrianXmlaDefinition;
		MondrianXmlaSourceWrapper wrapper;
		ExecutionContextImpl executionContext = new ExecutionContextImpl();
		
		String isEdit = (String)context.getFlowScope().get(IS_EDIT);
		if (isEdit == null) {
			isEdit = (String)context.getRequestParameters().get("isEdit");
			context.getFlowScope().put(IS_EDIT, isEdit);
		}
		
		if (isEdit != null) {
			String currentMondrianXmlaDefinition = (String) context
					.getFlowScope().get(CURRENT_MONDRIAN_XMLA_DEFINITION_ATTR);
			if (currentMondrianXmlaDefinition == null) {
				currentMondrianXmlaDefinition = (String)context.getRequestParameters().get("selectedResource");
			    context.getFlowScope().put(CURRENT_MONDRIAN_XMLA_DEFINITION_ATTR, currentMondrianXmlaDefinition);
			}
			mondrianXmlaDefinition = (MondrianXMLADefinition) repository
					.getResource(executionContext,
							currentMondrianXmlaDefinition);
			if(mondrianXmlaDefinition == null){
				context.getFlowScope().remove("prevForm");
				throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[] {currentMondrianXmlaDefinition});
			}
			wrapper = new MondrianXmlaSourceWrapper(mondrianXmlaDefinition);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_EDIT);
		} else {
			mondrianXmlaDefinition = (MondrianXMLADefinition) repository
					.newResource(executionContext, MondrianXMLADefinition.class);
			String parentFolder = (String) context.getFlowScope().get(
					PARENT_FOLDER_ATTR);
			if (parentFolder == null) {
				parentFolder = (String)context.getRequestParameters().get("ParentFolderUri");
				context.getFlowScope().put(PARENT_FOLDER_ATTR, parentFolder);
			}
			if (parentFolder == null || parentFolder.trim().length() == 0)
				parentFolder = "/";
			mondrianXmlaDefinition.setParentFolder(parentFolder);
			wrapper = new MondrianXmlaSourceWrapper(mondrianXmlaDefinition);
			wrapper.setMode(BaseDTO.MODE_STAND_ALONE_NEW);
		}
        context.getExternalContext().getSessionMap().put(MONDRIAN_TREE_DATA_PROVIDER, typedTreeDataProvider);
		getAllMondrianConnections(context, wrapper);
		return wrapper;
	}
	
	/**
	 * getAllMondrianConnections retrieves all Mondrian connections
	 * 
	 * @param context 
	 * @param wrapper
	 */
	private void getAllMondrianConnections(RequestContext context, MondrianXmlaSourceWrapper wrapper) {
		FilterCriteria filterCriteria = FilterCriteria
				.createFilter(MondrianConnection.class);
		ResourceLookup[] resourceLookup = repository.findResource(JasperServerUtil.getExecutionContext(context),
				filterCriteria);
		List allMondrianConnections = null;
		if (resourceLookup != null && resourceLookup.length != 0) {
			log("Found Mondrian conneciton lookups size="
					+ resourceLookup.length);
			allMondrianConnections = new ArrayList(resourceLookup.length);
			for (int i = 0; i < resourceLookup.length; i++) {
				Resource resource = resourceLookup[i];
				Object resourceObj;
				try {
					resourceObj = repository.getResource(null, resource.getURIString());
				} catch (JSException ex) {
					continue;
				}

				if (!allMondrianConnections
						.contains(((OlapClientConnection) resourceObj)
								.getURIString())) {
					allMondrianConnections
							.add(((OlapClientConnection) resourceObj)
									.getURIString());
				}
			}
			wrapper.setAllMondrianConnections(allMondrianConnections);
		}
	}
	
	/**
	 * saveMondrianXmlaSource saves mondrian xmla source definitions.
	 * 
	 * @param context
	 * @return success() if valid, otherwise error()
	 * @throws Exception
	 */
	public Event saveMondrianXmlaSource(RequestContext context)
			throws Exception {
		MondrianXmlaSourceWrapper wrapper = (MondrianXmlaSourceWrapper) getFormObject(context);
		try {
			if (wrapper.isStandAloneMode()) {
                MondrianXMLADefinition oldDef = (MondrianXMLADefinition) repository.getResource(null, wrapper.getMondrianXmlaDefinition().getURIString());
                
				MondrianConnection mondrianConnection = (MondrianConnection) repository.getResource(null, wrapper.getConnectionUri());
				wrapper.getMondrianXmlaDefinition().setMondrianConnection(mondrianConnection);
				wrapper.getMondrianXmlaDefinition().setMondrianConnectionReference(mondrianConnection.getURIString());
				repository.saveResource(null, wrapper.getMondrianXmlaDefinition());
				wrapper.setConnectionInvalid(false);

                MondrianXMLADefinition newDef = (MondrianXMLADefinition) repository.getResource(null, wrapper.getMondrianXmlaDefinition().getURIString());
                // Update the XMLA deployment catalog
                updatableXMLAContainer.updateXMLAConnection(oldDef, newDef);
			}
		} catch (JSDuplicateResourceException e) {
			getFormErrors(context).rejectValue("mondrianXmlaDefinition.name", "MondrianXmlaSourceValidator.error.duplicate");
			return error();
		} catch (Exception e) {
			wrapper.setConnectionInvalid(true);
		}
        
		return success();
	}

	/**
	 * getRepository returns repository service property
	 * 
	 * @return repository
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
	 * getConnectionService returns connection service
	 * 
	 * @return connectionService
	 */
	public OlapConnectionService getConnectionService() {
		return connectionService;
	}

	/**
	 * setConnectionService sets connection service property
	 * 
	 * @param connectionService
	 */
	public void setConnectionService(OlapConnectionService connectionService) {
		this.connectionService = connectionService;
	}
	
	/**
	 * initBinder initializes binder object
	 * 
	 * @param context
	 * @param binder
	 */
	public void initBinder(RequestContext context, DataBinder binder) {
		binder.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
	}
	
	/**
	 * setupEditForm set the form object
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event setupEditForm(RequestContext context) throws Exception {
		MutableAttributeMap rs = context.getRequestScope();
		rs.put(FORM_OBJECT_KEY, getFormObject(context));

        context.getFlowScope().put(ATTRIBUTE_RESOURCE_ID_NOT_SUPPORTED_SYMBOLS,
                configuration.getResourceIdNotSupportedSymbols());

		return success();
	}
	
	/**
	 * log logs debug message
	 * 
	 * @param text
	 */
	private void log(String text) {
		log.debug(text);
	}

    public void setUpdatableXMLAContainer(UpdatableXMLAContainer updatableXMLAContainer) {
        this.updatableXMLAContainer = updatableXMLAContainer;
    }

    public void setConfiguration(ConfigurationBean configuration) {
        this.configuration = configuration;
    }

    public void setMondrianTreeDataProvider(TypedTreeDataProvider typedTreeDataProvider) {
        this.typedTreeDataProvider = typedTreeDataProvider;
    }

}

