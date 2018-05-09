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

package com.jaspersoft.jasperserver.api.security.externalAuth.processors;


import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Holds all the necessary services to be used by the processors
 *
 * created by chaim
 * creation date: 8/23/12
 */
@JasperServerAPI
public abstract class AbstractExternalUserProcessor implements ExternalUserProcessor, InitializingBean {
    private final static Logger logger = LogManager.getLogger(AbstractExternalUserProcessor.class);

    private RepositoryService repositoryService;
    private UserAuthorityService userAuthorityService;
    private TenantService tenantService;
    private ProfileAttributeService profileAttributeService;
    private ObjectPermissionService objectPermissionService;

	/**
	 * Main processor method.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
    public abstract void process();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.repositoryService, "repositoryService can not be null");
        Assert.notNull(this.userAuthorityService, "userAuthorityService can not be null");
        Assert.notNull(this.tenantService, "tenantService can not be null");
        Assert.notNull(this.profileAttributeService, "profileAttributeService can not be null");
        Assert.notNull(this.objectPermissionService, "objectPermissionService can not be null");
    }

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public UserAuthorityService getUserAuthorityService() {
        return userAuthorityService;
    }

    public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public ProfileAttributeService getProfileAttributeService() {
        return profileAttributeService;
    }

    public void setProfileAttributeService(ProfileAttributeService profileAttributeService) {
        this.profileAttributeService = profileAttributeService;
    }

    public ObjectPermissionService getObjectPermissionService() {
        return objectPermissionService;
    }

    public void setObjectPermissionService(ObjectPermissionService objectPermissionService) {
        this.objectPermissionService = objectPermissionService;
    }

}
