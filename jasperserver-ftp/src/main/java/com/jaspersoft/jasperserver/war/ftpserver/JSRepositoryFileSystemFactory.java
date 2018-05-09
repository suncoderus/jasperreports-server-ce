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

package com.jaspersoft.jasperserver.war.ftpserver;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

import java.util.Map;

/**
 * @author asokolnikov
 */
public class JSRepositoryFileSystemFactory implements FileSystemFactory {

    private RepositoryService repositoryService;
    private RepositorySecurityChecker repositorySecurityChecker;
    private ResourceFactory resourceFactory;
    private ConfigurationBean configurationBean;
    private boolean loadContent = false;
    private Map<String, String> typeMapping;

    public FileSystemView createFileSystemView(User user) throws FtpException {
        return new JSRepositoryFileSystemView(user, this);
    }

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public RepositorySecurityChecker getRepositorySecurityChecker() {
        return repositorySecurityChecker;
    }

    public void setRepositorySecurityChecker(RepositorySecurityChecker repositorySecurityChecker) {
        this.repositorySecurityChecker = repositorySecurityChecker;
    }

    public ResourceFactory getResourceFactory() {
        return resourceFactory;
    }

    public void setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public boolean isLoadContent() {
        return loadContent;
    }

    public void setLoadContent(boolean loadContent) {
        this.loadContent = loadContent;
    }

    public Map<String, String> getTypeMapping() {
        return typeMapping;
    }

    public void setTypeMapping(Map<String, String> typeMapping) {
        this.typeMapping = typeMapping;
    }

    public ConfigurationBean getConfigurationBean() {
        return configurationBean;
    }

    public void setConfigurationBean(ConfigurationBean configurationBean) {
        this.configurationBean = configurationBean;
    }
}
