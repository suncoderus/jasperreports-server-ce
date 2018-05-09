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

import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

public class ResourceTypeMappingAction extends MultiAction implements MessageSourceAware {
	private static Logger logger = Logger.getLogger(ResourceTypeMappingAction.class);
	
	private static final String RESOLVED_TYPE_SEPARATOR = "!";
	
	private MessageSource messageSource;
	private RepositoryService repositoryService;
	private Map<String, Object> typeToActionBean;
	private String resourceUriAttributeName;
	private String resourceUriResolvedTypeAttributeName = "resourceUriResolvedType";//default value
    private EngineService engineService;
	
	public Event doPreExecute(RequestContext context) {
		String resourceUri = context.getFlowScope().getRequiredString(resourceUriAttributeName);
		
		// try to see if we have a cached resource type on the flow
		Object targetAction = getCachedType(context, resourceUri);

		// if not cached, go to the repository and find the type
		if (targetAction == null) {
			// can't use ResourceLookup here because security collection filter may filter out resources with execute-only permissions
            Resource resource = repositoryService.getResource(engineService.getRuntimeExecutionContext(), resourceUri);
            if (resource == null) {
                throw new JSException("jsexception.could.not.find.resource.with.uri", new Object[]{resourceUri});
            }

			String resourceType = resource.getResourceType();
			targetAction = typeToActionBean.get(resourceType);
			
			if (targetAction == null) {
				String typeLabel = messageSource.getMessage("resource." + resourceType + ".label", 
						null, LocaleContextHolder.getLocale());
				throw new JSException("jsexception.unexpected.resource.type.at.uri", new Object[]{typeLabel, resourceUri});
			}
			
			// caching the type in the flow so that we won't load the resource on each action
			cacheType(context, resourceUri, resourceType);
			
			if (logger.isDebugEnabled()) {
				logger.debug("found repository type " + resourceType + " for " + resourceUri);
			}
		}
		
		setTarget(targetAction);
		return null;
	}

	protected Object getCachedType(RequestContext context, String resourceUri) {
		Object targetAction = null;
		String resolvedTypeAttr = context.getFlowScope().getString(resourceUriResolvedTypeAttributeName);
		if (resolvedTypeAttr != null) {
			String resourceUriPrefix = resourceUri + RESOLVED_TYPE_SEPARATOR;
			if (resolvedTypeAttr.startsWith(resourceUriPrefix)) {
				String resolvedType = resolvedTypeAttr.substring(resourceUriPrefix.length());
				targetAction = typeToActionBean.get(resolvedType);
				
				if (targetAction != null) {//this should always be the case
					if (logger.isDebugEnabled()) {
						logger.debug("found cached resolved type " + resolvedType + " for " + resourceUri);
					}
				}
			}
		}
		return targetAction;
	}

	protected void cacheType(RequestContext context, String resourceUri, String resourceType) {
		String resolvedType = resourceUri + RESOLVED_TYPE_SEPARATOR + resourceType;
		context.getFlowScope().put(resourceUriResolvedTypeAttributeName, resolvedType);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public void setTypeToActionBean(Map<String, Object> typeToActionBean) {
		this.typeToActionBean = typeToActionBean;
	}

	public Map<String, Object> getTypeToActionBean() {
		return typeToActionBean;
	}

	public void setResourceUriAttributeName(String resourceUriAttributeName) {
		this.resourceUriAttributeName = resourceUriAttributeName;
	}

	public String getResourceUriAttributeName() {
		return resourceUriAttributeName;
	}

    public void setEngineService(EngineService engineService) {
        this.engineService = engineService;
    }

	public String getResourceUriResolvedTypeAttributeName() {
		return resourceUriResolvedTypeAttributeName;
	}

	public void setResourceUriResolvedTypeAttributeName(
			String resourceUriResolvedTypeAttributeName) {
		this.resourceUriResolvedTypeAttributeName = resourceUriResolvedTypeAttributeName;
	}
}
