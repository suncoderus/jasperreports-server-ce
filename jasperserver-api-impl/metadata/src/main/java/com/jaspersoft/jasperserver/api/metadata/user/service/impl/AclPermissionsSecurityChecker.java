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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oleg Gavavka
 *         09.10.2014.
 */
public class AclPermissionsSecurityChecker {
    private AclService aclService;
    private SidRetrievalStrategy sidRetrievalStrategy;
    private PermissionGrantingStrategy permissionGrantingStrategy;
    private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy;


    public Boolean isPermitted(Permission permission, Object object) {
        List<Permission> permissions = new ArrayList<Permission>(1);
        permissions.add(permission);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Sid> sids = sidRetrievalStrategy.getSids(auth);
        ObjectIdentity oid = objectIdentityRetrievalStrategy.getObjectIdentity(object);
        Acl acl = aclService.readAclById(oid,sids);
        return permissionGrantingStrategy.isGranted(acl,permissions,sids,false);
    }

    public void setAclService(AclService aclService) {
        this.aclService = aclService;
    }

    public void setSidRetrievalStrategy(SidRetrievalStrategy sidRetrievalStrategy) {
        this.sidRetrievalStrategy = sidRetrievalStrategy;
    }

    public void setPermissionGrantingStrategy(PermissionGrantingStrategy permissionGrantingStrategy) {
        this.permissionGrantingStrategy = permissionGrantingStrategy;
    }

    public void setObjectIdentityRetrievalStrategy(ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy) {
        this.objectIdentityRetrievalStrategy = objectIdentityRetrievalStrategy;
    }
}
