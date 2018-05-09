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
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

/**
 * @author Oleg.Gavavka
 */

public class AclLookupStrategyImpl implements AclService {
    List<AclService> aclServices;
    NonMutableAclCache nonMutableAclCache;
    
    private boolean attemptToCache(final AclService service, final Acl result){
        if (!(service instanceof EhCacheAclServiceImpl)) {
            if (!isImportRunning() && nonMutableAclCache!=null) {
                nonMutableAclCache.putInCache(result);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ObjectIdentity> findChildren(ObjectIdentity parentIdentity) {
        List<ObjectIdentity> result=null;
        for(AclService service: aclServices){
            result = service.findChildren(parentIdentity);
            if (result!=null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        Acl result;
        for(AclService service: aclServices){
            result = service.readAclById(object);
            if (result!=null) {
            	attemptToCache(service, result);
            	return result;
            }
        }
        return null;
    }

    @Override
    public Acl readAclById(ObjectIdentity object, List<Sid> sids) throws NotFoundException {
        Acl result;
        for(AclService service: aclServices){
            result = service.readAclById(object,sids);
            if (result!=null) {
            	// attemptToCache(service, result);
                return result;
            }
        }
        return null;
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects) throws NotFoundException {
        Map<ObjectIdentity, Acl> result;
        for(AclService service: aclServices){
            result = service.readAclsById(objects);
            if (result!=null) {
//            	for(Acl acl:result.values()){
//            		attemptToCache(service, acl);
//            	}
                return result;
            }
        }
        return null;
    }

    @Override
    public Map<ObjectIdentity, Acl> readAclsById(List<ObjectIdentity> objects, List<Sid> sids) throws NotFoundException {
        Map<ObjectIdentity, Acl> result;
        for(AclService service: aclServices){
            result = service.readAclsById(objects,sids);
            if (result!=null) {
//            	for(Acl acl:result.values()){
//            		attemptToCache(service, acl);
//            	}
                return result;
            }
        }
        return null;
    }

    public void setAclServices(List<AclService> aclServices) {
        this.aclServices = aclServices;
    }

    public void setNonMutableAclCache(NonMutableAclCache nonMutableAclCache) {
        this.nonMutableAclCache = nonMutableAclCache;
    }
    private Boolean isImportRunning() {
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
