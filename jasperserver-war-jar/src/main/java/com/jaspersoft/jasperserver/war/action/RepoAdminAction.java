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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.tags.PaginatorTag;

public class RepoAdminAction extends FormAction {

	protected final Log log = LogFactory.getLog(this.getClass());

	private Map resourceTypes;

	private RepositoryService repository;
	private String flowAttributeFolder;
	private MessageSource messages;//FIXME not used
	private ConfigurationBean configuration;
	private RepositorySecurityChecker repositoryServiceSecurityChecker;

	/*
	 * method to get the reposervice object arguments: none returns:
	 * RepositoryService
	 */
	public RepositoryService getRepository() {
		return repository;
	}

	/*
	 * method to set the reposervice object arguments: RepositoryService
	 * returns: void
	 */
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	/**
	 * @return Returns the resourceTypes.
	 */
	public Map getResourceTypes() {
		return resourceTypes;
	}

	/**
	 * @param resourceTypes The resourceTypes to set.
	 */
	public void setResourceTypes(Map resourceTypes) {
		this.resourceTypes = resourceTypes;
	}

	public MessageSource getMessages()
	{
		return messages;
	}

	public void setMessages(MessageSource messages)
	{
		this.messages = messages;
	}

	public ConfigurationBean getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(ConfigurationBean configuration)
	{
		this.configuration = configuration;
	}

	public RepoAdminAction()
	{
//		setFormObjectClass(CreateReportWizardDTO.class);
//		// custom form backing object class
//		setFormObjectName(FORM_OBJECT_KEY);
//		setFormObjectScope(ScopeType.FLOW); // this is a multi-page wizard!
//		setValidator(new CreateReportFlowValidator());
	}

	public Event initAction(RequestContext context)
	{
		String folderURI = context.getRequestParameters().get("folder");

		if (folderURI == null)
		{
			folderURI = context.getFlowScope().getString(getFlowAttributeFolder());
			if (folderURI == null)
			{
				folderURI = "/";
			}
		}
		else
		{
			context.getFlowScope().put(getFlowAttributeFolder(), folderURI);
		}

		List pathFolders = getPathFolders(folderURI);
		context.getRequestScope().put("pathFolders", pathFolders);

		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createParentFolderFilter(folderURI));

		List folders = repository.getSubFolders(JasperServerUtil.getExecutionContext(context), folderURI);

/*
		ResourceLookup[] resources = repository.findResource(null, criteria);
		context.getRequestScope().put("resources", Arrays.asList(resources));
*/
		List resources = repository.loadResourcesList(JasperServerUtil.getExecutionContext(context), criteria);

		List allResources = new ArrayList();
		allResources.addAll(folders);
		allResources.addAll(resources);
		
		context.getRequestScope().put("resources", allResources);
		context.getRequestScope().put("resourceTypes", getResourceTypes());
		
		Map removableResources = new HashMap();
		Map editableResources = new HashMap();
		repositoryServiceSecurityChecker.filterResources(allResources, removableResources, editableResources);
		context.getRequestScope().put("removableResources", removableResources);
		context.getRequestScope().put("editableResources", editableResources);
		return success();
	}

	
	public Event goToPage(RequestContext context)
	{
		context.getFlowScope().put(
			PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER, 
			context.getRequestParameters().get(PaginatorTag.CURRENT_PAGE_REQUEST_PARAMETER)
			);

		return success();
	}

	public Event remove(RequestContext context)
	{
		log.debug("RepositoryAdminAction.remove");
		
		String[] selectedFolders = context.getRequestParameters().getArray("selectedFolders");
		String[] selectedResources = context.getRequestParameters().getArray("selectedResources");
		List failedResources = new ArrayList();

		try {
			if (selectedResources != null && selectedResources.length > 0) {
				for (int i = 0; i < selectedResources.length; i++) {
					try {
						if (repository.resourceExists(null, selectedResources[i])) {
							repository.deleteResource(null, selectedResources[i]);
						}
					} 
					catch (JSExceptionWrapper ex) {
						if(ex.getOriginalException() instanceof org.springframework.dao.DataIntegrityViolationException) {
							log.error("Failed to remove resource: " + selectedResources[i], ex);
							failedResources.add(selectedResources[i]);
						}
						else {
							throw ex;
						}
					}
				}
			}

			if (selectedFolders != null && selectedFolders.length > 0) {
				for (int i = 0; i < selectedFolders.length; i++) {
					try {
						repository.deleteFolder(null, selectedFolders[i]);
					} 
					catch (JSExceptionWrapper ex) {
						if(ex.getOriginalException() instanceof org.springframework.dao.DataIntegrityViolationException) {
							log.error("Failed to remove folder: " + selectedFolders[i], ex);
							failedResources.add(selectedFolders[i]);
						}
						else {
							throw ex;
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("Unexpected error during removing resources. ", e);
			return error(e);
		}

		if (failedResources.size() > 0) {
			log.debug("There are resources that cannot be removed");
			context.getRequestScope().put("failedResources", failedResources);
			return no();
		}
		
		return success();
	}

	protected List getPathFolders(String uri)
	{
		List pathFolders = new ArrayList();

		Folder parentFolder = null;

		StringTokenizer tkzer = new StringTokenizer(uri, Folder.SEPARATOR, false);
		while(tkzer.hasMoreTokens())
		{
			String token = tkzer.nextToken();

			Folder folder = new FolderImpl();
			folder.setName(token);
			folder.setParentFolder(parentFolder);

			pathFolders.add(folder);

			parentFolder = folder;
		}

		return pathFolders;
	}

	public String getFlowAttributeFolder() {
		return flowAttributeFolder;
	}

	public void setFlowAttributeFolder(String flowAttributeFolder) {
		this.flowAttributeFolder = flowAttributeFolder;
	}

	public RepositorySecurityChecker getRepositoryServiceSecurityChecker() {
		return repositoryServiceSecurityChecker;
	}

	public void setRepositoryServiceSecurityChecker(
			RepositorySecurityChecker repositoryServiceSecurityChecker) {
		this.repositoryServiceSecurityChecker = repositoryServiceSecurityChecker;
	}

}
