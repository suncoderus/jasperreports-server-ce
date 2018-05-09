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
package com.jaspersoft.jasperserver.export.modules.logging.access;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class AccessEventsImportHandlerImpl implements AccessEventsImportHandler {

    private UserAuthorityService userAuthorityService;
    private HibernateRepositoryService hibernateRepositoryService;

    public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }

    public UserAuthorityService getUserAuthorityService() {
        return userAuthorityService;
    }

    public void setHibernateRepositoryService(HibernateRepositoryService hibernateRepositoryService) {
        this.hibernateRepositoryService = hibernateRepositoryService;
    }

    protected User getUserByTenantQualifiedName(TenantQualifiedName userName) {
        return userAuthorityService.getUser(null, userName.getName());
    }

    public User resolveUser(TenantQualifiedName userName) {
        return getUserByTenantQualifiedName(userName);
    }

    public Resource resolveResource(String uri) {
        return hibernateRepositoryService.getResource(null, uri);
    }
}
