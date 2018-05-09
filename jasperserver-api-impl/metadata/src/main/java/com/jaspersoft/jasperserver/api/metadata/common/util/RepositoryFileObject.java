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
package com.jaspersoft.jasperserver.api.metadata.common.util;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.jaspersoft.jasperserver.core.util.XMLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Implementation of Apache VFS FileObject to access the JasperServer repository
 *
 * @author swood
 */
public class RepositoryFileObject extends AbstractFileObject implements FileObject {

    private static final Log log = LogFactory.getLog(RepositoryFileObject.class);

    RepositoryService repositoryService;

    FileResource fileResource = null;
    FileResourceData resourceData = null;
    Folder folder = null;

    String scheme;

    protected RepositoryFileObject(final RepositoryFileSystem fs, final String scheme, final FileName fileName) {
        super(fileName, fs);
        this.scheme = scheme;
    }

    @Override
    protected FileType doGetType() throws Exception {

        if (folder != null) {
            return FileType.FOLDER;
        } else if (fileResource != null) {
            return FileType.FILE;
        } else {
            return FileType.IMAGINARY;
        }
    }

    @Override
    protected String[] doListChildren() throws Exception {

        if (doGetType() != FileType.FOLDER) {
            return new String[0];
        }
        FilterCriteria filter = FilterCriteria.createFilter();
        filter.addFilterElement(FilterCriteria.createParentFolderFilter(getName().getPath()));

        ResourceLookup[] resourceResults = getRepositoryService().findResource(null, filter);

        List subFolders = getRepositoryService().getSubFolders(null, getName().getPath());

        String[] results = new String[resourceResults.length + subFolders.size()];
        int pos = 0;
        for (ResourceLookup lookup : resourceResults) {
            results[pos++] = lookup.getName();
        }
        for (Object aFolder : subFolders) {
            results[pos++] = ((Folder) aFolder).getName();
        }
        return results;
    }

    @Override
    protected long doGetContentSize() throws Exception {

        if (doGetType() != FileType.FILE) {
            return 0;
        }

        if (fileResource == null) {
            throw new JSException("no resource. URI: " + getName());
        }

        if (resourceData != null) {
            return resourceData.dataSize();
        } else if (fileResource.hasData()) {
            return fileResource.getData().length;
        } else {
            return 0;
        }
    }

    /**
     * Attaches this file object to its file resource.  This method is called
     * before any of the doBlah() or onBlah() methods.  Sub-classes can use
     * this method to perform lazy initialisation.
     *
     * @throws Exception
     */
    @Override
    protected void doAttach() throws Exception {

        String resourceName = getName().getPath();
        log.debug("Loading resource: " + resourceName);
        ExecutionContext executionContext = ExecutionContextImpl.getRuntimeExecutionContext(null);
        fileResource = (FileResource) getRepositoryService().getResource(executionContext, resourceName);

        // could be folder
        if (fileResource == null) {
            folder = getRepositoryService().getFolder(executionContext, resourceName);
        } else {
            log.debug("has data: " + ((fileResource == null) ? "null" : "" + fileResource.hasData()));
            log.debug("isReference: " + ((fileResource == null) ? "null" : "" + fileResource.isReference()));

            if (fileResource != null && !fileResource.hasData()) {
                resourceData = getRepositoryService().getResourceData(executionContext, fileResource.getURIString());
            }
        }
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {

        if (fileResource == null) {
            throw new JSException("no resource. URI: " + getName());
        }

        InputStream data = resourceData != null ? resourceData.getDataStream(): fileResource.getDataStream();
        // check for XXE vulnerability
        data = XMLUtil.checkForXXE(data);

        // TODO: Should this be cached?

        InputStream in = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOut = new BufferedOutputStream(out);
        try {
            in = new BufferedInputStream(data);
            byte[] buf = new byte[10000];
            int numRead = 0;
            while ((numRead = in.read(buf)) != -1) {
                bufferedOut.write(buf, 0, numRead);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            bufferedOut.flush();
        }

        return new BufferedInputStream(new ByteArrayInputStream(out.toByteArray()));
    }

    public RepositoryService getRepositoryService() {
        if (repositoryService != null) {
            return repositoryService;
        }

        ApplicationContext ctx = StaticApplicationContext.getApplicationContext();

        if (ctx == null) {
            throw new JSException("StaticApplicationContext not configured in Spring");
        }

        Properties springConfiguration;
        try {
            springConfiguration = ((Properties) ctx.getBean("springConfiguration"));
        } catch (NoSuchBeanDefinitionException e) {
            springConfiguration = new Properties();
            log.debug("RepositoryFileObject#doAttach : no spring configuration properties");
        }

        /*
         * For the "repo" scheme, we expect an "external" representation of the URI
         * that has to be transformed.
         * Otherwise we use the "repoint" scheme, which expects an "internal" representation of the URI
         * that does not have to be transformed so that the same file is used across tenants.
         */
        String repositoryServiceName = "repositoryService";

        if (scheme.equals(RepositoryFileProvider.REPOSITORY_SCHEME)) {
            if (springConfiguration.containsKey("bean.repositoryService")) {
                repositoryServiceName = springConfiguration.getProperty("bean.repositoryService");
            }
        } else {
            if (springConfiguration.containsKey("bean.internalRepositoryService")) {
                repositoryServiceName = springConfiguration.getProperty("bean.internalRepositoryService");
            }
        }

        repositoryService = (RepositoryService) ctx.getBean(repositoryServiceName);
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

}
