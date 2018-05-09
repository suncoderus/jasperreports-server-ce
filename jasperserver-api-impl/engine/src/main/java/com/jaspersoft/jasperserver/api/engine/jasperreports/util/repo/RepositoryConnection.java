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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util.repo;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.RepositoryContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceContainer;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepositoryConnection extends URLConnection
{
	private static final Log log = LogFactory.getLog(RepositoryConnection.class);
	
	private final RepositoryContext repositoryContext;
	
	public RepositoryConnection(RepositoryContext repository, URL url)
	{
		super(url);

		this.repositoryContext = repository;
	}

	public void connect() throws IOException
	{
		connected = true;
	}

    public InputStream getInputStream() throws IOException
    {
    	try {
            InputStream data = null;
            String path = url.getPath();
            if (!path.startsWith(Folder.SEPARATOR)) {
                ResourceContainer reportUnit = repositoryContext.getReportUnit();
                FileResource resource = null;
                if (reportUnit != null) {
                    resource = reportUnit.getResourceLocal(path);
                }

                if (resource == null || !resource.hasData()) {
                    path = repositoryContext.getContextURI() + Folder.SEPARATOR + path;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Loading resource \"" + resource.getName()
                                + "\" from in-memory report unit");
                    }
                    data = resource.getDataStream();
                    if (resource.getFileType().equals(FileResource.TYPE_JRXML)) {
                        data = repositoryContext.getCompiledReportProvider().getCompiledReport(
                                repositoryContext.getExecutionContext(),
                                data);//FIXME not currently used, but should do autoUpdateJRXMLResource
                    }
                }
            }

            if (data == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Loading resource \"" + path + "\" from repository");
                }

                RepositoryService repository = repositoryContext.getRepository();
                Resource resource = repository.getResource(repositoryContext.getExecutionContext(), path);
                if (resource == null) {
                    throw new IOException("Repository file resource " + path
                            + " could not be loaded");
                }
                if (resource instanceof FileResource) {
                    FileResource fileResource = (FileResource) repository.getResource(
                            repositoryContext.getExecutionContext(), path, FileResource.class);
                    while (fileResource != null && fileResource.isReference()) {
                        fileResource = (FileResource) repository.getResource(
                                repositoryContext.getExecutionContext(),
                                fileResource.getReferenceURI(), FileResource.class);
                    }

                    if (fileResource == null) {
                        throw new IOException("Repository file resource " + path
                                + " could not be loaded");
                    }

                    if (fileResource.getFileType().equals(FileResource.TYPE_JRXML)) {
                        data = repositoryContext.getCompiledReportProvider().getCompiledReport(
                                repositoryContext.getExecutionContext(),
                                path);
                    } else {
                        data = repository.getResourceData(repositoryContext.getExecutionContext(), path).getDataStream();
                    }
                } else if(resource instanceof ContentResource){
                    data = repository.getContentResourceData(repositoryContext.getExecutionContext(), path).getDataStream();
                }
            }

            return data;
        } catch (JSResourceNotFoundException e) {
			throw new IOException(e.getMessage());
		}
    }
}
