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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryUtil;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DefaultRepositoryContextManager implements
		RepositoryContextManager {

	private RepositoryService repository;
	private CompiledReportProvider compiledReportProvider;
	
	public DefaultRepositoryContextManager() {
	}
	
	public DefaultRepositoryContextManager(RepositoryService repository, 
			CompiledReportProvider compiledReportProvider) {
		this.repository = repository;
		this.compiledReportProvider = compiledReportProvider;
	}
	
	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

	public CompiledReportProvider getCompiledReportProvider() {
		return compiledReportProvider;
	}

	public void setCompiledReportProvider(
			CompiledReportProvider compiledReportProvider) {
		this.compiledReportProvider = compiledReportProvider;
	}

	@Override
	public RepositoryContextHandle setRepositoryContext(ExecutionContext context,
			String reportUnitURI, ResourceContainer inMemoryReportUnit) {
		RepositoryContext repositoryContext = createRepositoryContext(context,
				reportUnitURI, inMemoryReportUnit, repository);
		return setRepositoryContext(repositoryContext);
	}

	protected RepositoryContextHandle setRepositoryContext(
			RepositoryContext repositoryContext) {
		RepositoryContext originalContext = RepositoryUtil.getThreadRepositoryContext();
		RepositoryUtil.setThreadRepositoryContext(repositoryContext);
		return new Handle(originalContext, repositoryContext);
	}

	protected RepositoryContext createRepositoryContext(
			ExecutionContext context,
			String reportUnitURI, ResourceContainer inMemoryReportUnit,
			RepositoryService repositoryService) {
		RepositoryContext repositoryContext = new RepositoryContext();
		repositoryContext.setRepository(repositoryService);
		repositoryContext.setContextResourceURI(reportUnitURI);
		repositoryContext.setReportUnit(inMemoryReportUnit);
		repositoryContext.setDataSource(getDataSource(inMemoryReportUnit));

		repositoryContext.setExecutionContext(context);
		repositoryContext.setCompiledReportProvider(compiledReportProvider);
		return repositoryContext;
	}

    protected ReportDataSource getDataSource(ResourceContainer inMemoryReportUnit) {
        if (inMemoryReportUnit != null && inMemoryReportUnit instanceof ReportUnit) {
            ReportUnit reportUnit = (ReportUnit) inMemoryReportUnit;
            ResourceReference reference = reportUnit.getDataSource();

            if(reference == null) {
                return null;
            }

            Resource resource;
            if(reference.isLocal()) {
                resource = reference.getLocalResource();
            } else {
                resource = getDirectRepository().getResource(ExecutionContextImpl.getRuntimeExecutionContext(), 
                		reference.getReferenceURI());
            }

            if (resource != null && resource instanceof ReportDataSource) {
                return (ReportDataSource) resource;
            }
        }

        return null;
    }

    protected RepositoryService getDirectRepository() {
    	return repository;
    }

	public String getRepositoryPathKey(String path) {
		return path;
	}

	public String getRepositoryUriForKey(String pathKey) {
		return pathKey;
	}

	protected class Handle implements RepositoryContextHandle {
		private final RepositoryContext originalContext;
		private final RepositoryContext repositoryContext;

		public Handle(RepositoryContext originalContext, RepositoryContext repositoryContext) {
			this.originalContext = originalContext;
			this.repositoryContext = repositoryContext;
		}

		public void unset() {
			// restore the original context
			RepositoryUtil.setThreadRepositoryContext(originalContext);
		}

		@Override
		public RepositoryContext getRepositoryContext() {
			return repositoryContext;
		}
	}
	
}
