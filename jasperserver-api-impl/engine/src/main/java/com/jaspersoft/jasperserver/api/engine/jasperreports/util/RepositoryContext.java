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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CompiledReportProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceContainer;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepositoryContext {
	
	private RepositoryService repository;
	private ExecutionContext executionContext;
	private String contextURI;
	private ResourceContainer reportUnit;
	private ReportDataSource dataSource;
	private CompiledReportProvider compiledReportProvider;
	
	public RepositoryContext() {
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public String getContextURI() {
		return contextURI;
	}

	public void setContextURI(String contextURI) {
		this.contextURI = contextURI;
	}

	
	/**
	 * Sets the context URI as the context of local resource for a resource.
	 * <p>
	 * The repository service needs to be set before calling this method.
	 * 
	 * @param resourceURI the resource URI
	 * @see #setRepository(RepositoryService)
	 */
	public void setContextResourceURI(String resourceURI) {
		int lastSepIdx = resourceURI.lastIndexOf(Folder.SEPARATOR);
		String resourceName;
		String folder;
		if (lastSepIdx >= 0) {
			resourceName = resourceURI.substring(lastSepIdx + Folder.SEPARATOR_LENGTH);
			folder = resourceURI.substring(0, lastSepIdx + Folder.SEPARATOR_LENGTH);
		} else {
			resourceName = resourceURI;
			folder = "";
		}
		String childrenFolderName = getRepository().getChildrenFolderName(resourceName);
		String childrenFolderURI = folder + childrenFolderName;
		setContextURI(childrenFolderURI);
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public ResourceContainer getReportUnit() {
		return reportUnit;
	}

	public void setReportUnit(ResourceContainer reportUnit) {
		this.reportUnit = reportUnit;
	}

    public ReportDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(ReportDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CompiledReportProvider getCompiledReportProvider() {
		return compiledReportProvider;
	}

	public void setCompiledReportProvider(CompiledReportProvider compiledReportProvider) {
		this.compiledReportProvider = compiledReportProvider;
	}

}
