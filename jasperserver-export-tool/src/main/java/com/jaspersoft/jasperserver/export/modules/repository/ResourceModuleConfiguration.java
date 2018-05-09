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

package com.jaspersoft.jasperserver.export.modules.repository;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementServiceImpl;
import com.jaspersoft.jasperserver.api.common.service.ImplementationObjectFactory;
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.modules.common.ReportParametersTranslator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ResourceModuleConfiguration implements ApplicationContextAware {
	
	private RepositoryService repository;
	private String indexFileName;
	private String resourcesDirName;
	private String folderDetailsFileName;
	private String folderIndexElement;
	private String resourceIndexElement;
    private String uriOfSettingsList = PropertiesManagementServiceImpl.RESOURCE_FULL_NAME; //default value
	private ImplementationObjectFactory castorBeanMappings;
	private ObjectSerializer serializer;
	private Map resourceDataProviders;
	private ObjectPermissionService permissionService;
	private UserAuthorityService authorityService;
	private JdbcDriverService jdbcDriverService;
	private String permissionRecipientRole;
	private String permissionRecipientUser;
	private ReportParametersTranslator reportParametersTranslator;
	private ApplicationContext applicationContext;

	public ImplementationObjectFactory getCastorBeanMappings() {
		return castorBeanMappings;
	}
	
	public void setCastorBeanMappings(ImplementationObjectFactory castorBeanMappings) {
		this.castorBeanMappings = castorBeanMappings;
	}
	
	public String getFolderDetailsFileName() {
		return folderDetailsFileName;
	}
	
	public void setFolderDetailsFileName(String folderDetailsFileName) {
		this.folderDetailsFileName = folderDetailsFileName;
	}
	
	public String getFolderIndexElement() {
		return folderIndexElement;
	}
	
	public void setFolderIndexElement(String folderIndexElement) {
		this.folderIndexElement = folderIndexElement;
	}
	
	public RepositoryService getRepository() {
		return repository;
	}
	
	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}
	
	public Map getResourceDataProviders() {
		return resourceDataProviders;
	}
	
	public void setResourceDataProviders(Map resourceDataProviders) {
		this.resourceDataProviders = resourceDataProviders;
	}
	
	public ResourceDataProvider getResourceDataProvider(String providerId) {
		ResourceDataProvider dataProvider = (ResourceDataProvider) resourceDataProviders.get(providerId);
		if (dataProvider == null) {
			throw new JSException("jsexception.no.resource.data.provider.found", new Object[] {providerId});
		}
		return dataProvider;
	}
	
	public String getResourceIndexElement() {
		return resourceIndexElement;
	}
	
	public void setResourceIndexElement(String resourceIndexElement) {
		this.resourceIndexElement = resourceIndexElement;
	}
	
	public String getResourcesDirName() {
		return resourcesDirName;
	}
	
	public void setResourcesDirName(String resourcesDirName) {
		this.resourcesDirName = resourcesDirName;
	}
	
	public ObjectSerializer getSerializer() {
		return serializer;
	}
	
	public void setSerializer(ObjectSerializer serializer) {
		this.serializer = serializer;
	}

	public String getIndexFileName() {
		return indexFileName;
	}

	public void setIndexFileName(String indexFileName) {
		this.indexFileName = indexFileName;
	}

	public UserAuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(UserAuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public ObjectPermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(ObjectPermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public String getPermissionRecipientRole() {
		return permissionRecipientRole;
	}

	public void setPermissionRecipientRole(String permissionRecipientRole) {
		this.permissionRecipientRole = permissionRecipientRole;
	}

	public String getPermissionRecipientUser() {
		return permissionRecipientUser;
	}

	public void setPermissionRecipientUser(String permissionRecipientUser) {
		this.permissionRecipientUser = permissionRecipientUser;
	}

	public ReportParametersTranslator getReportParametersTranslator() {
		return reportParametersTranslator;
	}

	public void setReportParametersTranslator(
			ReportParametersTranslator reportParametersTranslator) {
		this.reportParametersTranslator = reportParametersTranslator;
	}

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getUriOfSettingsList() {
        return uriOfSettingsList;
    }

    public void setUriOfSettingsList(String uriOfSettingsList) {
        this.uriOfSettingsList = uriOfSettingsList;
    }

    public JdbcDriverService getJdbcDriverService() {
        return jdbcDriverService;
    }

    public void setJdbcDriverService(JdbcDriverService jdbcDriverService) {
        this.jdbcDriverService = jdbcDriverService;
    }
}
