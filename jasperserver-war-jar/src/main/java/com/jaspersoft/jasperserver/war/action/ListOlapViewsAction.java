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

import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;

/**
 * ListOlapViewsAction provides the action to display olap views
 *
 * @author jshih
 */
public class ListOlapViewsAction extends FormAction {
	private RepositoryService repository;

	/**
	 * listOlapViews performs the list olap views action
	 * 
	 * @param context
	 * @return
	 */
	public Event listOlapViews(RequestContext context) {
		ExecutionContext executionContext = JasperServerUtil.getExecutionContext(context);
		ResourceLookup[] olapUnits = repository.findResource(executionContext,
				FilterCriteria.createFilter(OlapUnit.class));
		context.getRequestScope().put("olapUnits", olapUnits);
		return success();
	}

	/**
	 * getRepository returns the repository service property
	 * 
	 * @return
	 */
	public RepositoryService getRepository() {
		return repository;
	}

	/**
	 * setRepository sets the repository service property
	 * @param repository
	 */
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}
}
