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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import net.sf.jasperreports.repo.PersistenceService;
import net.sf.jasperreports.repo.Resource;
import net.sf.jasperreports.repo.StreamRepositoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepoRepositoryService implements StreamRepositoryService {

	private static final Log log = LogFactory.getLog(RepoRepositoryService.class);
	
	private Map<Class<?>, PersistenceService> persistenceServices;
			
	@Override
	public Resource getResource(String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveResource(String uri, Resource resource) {
		throw new UnsupportedOperationException();
	}

	@Override
    public InputStream getInputStream(String uri) {
        InputStream data = null;
        RepositoryContext repositoryContext = RepositoryUtil.getThreadRepositoryContext();
        if (repositoryContext == null || !isReportContext(repositoryContext)) {
            if (log.isDebugEnabled()) {
                log.debug("No repository context for resource " + uri);
            }
            return null;
        }

        // filtering cases when the uri is (almost) obviously not a repository path.
        // we could use the repository resource names patters as validation but I'm not sure
        // that the validation is enforced everywhere.
        if (uri.startsWith("repo:")// for now we are not handling repo: URLs here, at some point we might want to get rid of RepositoryConnection.
                || uri.startsWith("file:") || uri.startsWith("http:") || uri.startsWith("https:")) {
            return null;
        }

        //FIXME should we look at repositoryContext.getReportUnit()?  it seems to be no longer used.
        String path = RepositoryUtils.resolveRelativePath(repositoryContext.getContextURI(), uri);
        ExecutionContext executionContext = repositoryContext.getExecutionContext();
        com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService repository =
                repositoryContext.getRepository();

        if (log.isDebugEnabled()) {
            log.debug("loading repository resource " + path
                    + ", context path " + repositoryContext.getContextURI()
                    + ", uri " + uri);
        }

        final com.jaspersoft.jasperserver.api.metadata.common.domain.Resource resource =
                repository.getResource(executionContext, path);
        if (resource == null) {
            if (log.isDebugEnabled()) {
                log.debug("Resource \"" + path + "\" not found in the repository");
            }
            return null;
        }

        if (resource instanceof FileResource) {
            FileResource fileResource = (FileResource) resource;
            while (fileResource != null && fileResource.isReference()) {
                if (log.isDebugEnabled()) {
                    log.debug("loading repository resource " + fileResource.getReferenceURI());
                }

                fileResource = (FileResource) repository.getResource(executionContext,
                        fileResource.getReferenceURI(), FileResource.class);
            }
            if (fileResource != null) {
                if (fileResource.getFileType().equals(FileResource.TYPE_JRXML)) {
                    // this would conceptually belong to the persistence service, but we don't have the context there
                    data = repositoryContext.getCompiledReportProvider().getCompiledReport(executionContext, path);
                } else {
                    FileResourceData resourceData = repository.getResourceData(executionContext, path);
                    data = resourceData.getDataStream();
                }

                if (log.isDebugEnabled()) {
                    log.debug("loaded resource of type " + fileResource.getFileType());
                }
            }

        } else if (resource instanceof ContentResource) {
            data = repository.getContentResourceData(executionContext, path).getDataStream();
        }
        return data;
    }

    protected boolean isReportContext(RepositoryContext repositoryContext) {
		// some places (e.g. MessageSourceLoader.setupThreadRepositoryContext()) 
		// set a thread repository context with no context URI, and do not clear the thread.
		// we can only use reports related repository contexts (as created by 
		// DefaultRepositoryContextManager.createRepositoryContext()).
		return repositoryContext.getContextURI() != null 
				&& repositoryContext.getCompiledReportProvider() != null;
	}

	@Override
	public <K extends Resource> K getResource(String uri, Class<K> resourceType) {
		PersistenceService persistenceService = persistenceServices.get(resourceType);
		if (persistenceService == null) {
			if (log.isDebugEnabled()) {
				log.debug("Unknown resource type " + resourceType.getClass().getName() + " requested");
			}
			return null;
		}
		
		@SuppressWarnings("unchecked")
		K resource = (K) persistenceService.load(uri, this);
		return resource;
	}

	@Override
	public OutputStream getOutputStream(String uri) {
		throw new UnsupportedOperationException();
	}

	public Map<Class<?>, PersistenceService> getPersistenceServices() {
		return persistenceServices;
	}

	public void setPersistenceServices(
			Map<Class<?>, PersistenceService> persistenceServices) {
		this.persistenceServices = persistenceServices;
	}

}
