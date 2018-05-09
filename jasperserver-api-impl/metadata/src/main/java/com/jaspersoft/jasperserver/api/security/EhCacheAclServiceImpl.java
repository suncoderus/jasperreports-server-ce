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
package com.jaspersoft.jasperserver.api.security;

import com.jaspersoft.jasperserver.api.common.util.ImportRunMonitor;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author Oleg.Gavavka
 */
public class EhCacheAclServiceImpl implements AclService {
    private final NonMutableAclCache cache;

    public EhCacheAclServiceImpl(NonMutableAclCache nonMutableAclCache) {
        Assert.notNull(nonMutableAclCache, "NonMutableAclCache required");
        this.cache = nonMutableAclCache;
    }

    @Override
    public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
        return null;
    }

    @Override
    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        if (isImportRunning()) {
            return null;
        }
        return cache.getFromCache(object);
    }

    @Override
    public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
        if (isImportRunning()) {
            return null;
        }

        return cache.getFromCache(object);
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
        return null;
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
        return null;
    }

    protected Boolean isImportRunning() {
//        Disable caching if Import is running
        if (ImportRunMonitor.isImportRun()){
            //Add special permission for current user
            //to allow updating special resources like themes etc
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            for (GrantedAuthority authority : authentication.getAuthorities()){
                if (authority.getAuthority().equals(ObjectPermissionService.PRIVILEGED_OPERATION)){
                    return true;
                }
            }
        }
        return false;
    }
}
