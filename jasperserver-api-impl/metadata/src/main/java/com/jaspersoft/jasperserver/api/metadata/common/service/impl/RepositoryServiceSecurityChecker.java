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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.acls.AclEntryVoter;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.util.SimpleMethodInvocation;

import java.lang.reflect.Method;
import java.util.Collection;


/**
 * This class adds security/permission checking utilities for RepositoryService
 */
public class RepositoryServiceSecurityChecker extends BaseRepositorySecurityChecker {

    private static final Log log = LogFactory.getLog(RepositoryServiceSecurityChecker.class);
    private AclEntryVoter adminVoter, updateVoter, deleteVoter, readVoter;
    private AclEntryVoter folderReadVoter;
    private MethodSecurityInterceptor interceptor;

    public RepositoryServiceSecurityChecker() {

    }

    // Previous implementation was using special Interceptor - CheckMethodSecurityInterceptor
    // which was just invoking chain of security interceptors which are called before execution of method.
    // Then was catching Error in case access is not permitted, and this way it was deciding
    // if you have desired access or not - this kind of Error throws - consume much of CPU power ....
    private boolean getResult(AclEntryVoter voter, String methodName, Class arg1, Class arg2, Object argValue1, Object argValue2) throws NoSuchMethodException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Method method = RepositoryService.class.getMethod(methodName, arg1, arg2);
        MethodInvocation mi = new SimpleMethodInvocation(RepositoryService.class, method, argValue1, argValue2);
        Collection<ConfigAttribute> configs = interceptor.getSecurityMetadataSource().getAttributes(method, RepositoryService.class);
        return voter.vote(auth, mi, configs) == AclEntryVoter.ACCESS_GRANTED;
    }

    /**
     * Checks whether the given resource can be edited
     */
    public boolean isEditable(Resource resource) {
        try {
            return getResult(updateVoter, "saveResource", ExecutionContext.class, Resource.class, null, resource);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("No UPDATE permission for < " + resource.getURIString() + ">:" + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Checks whether the given resource can be deleted
     */
    public boolean isRemovable(Resource resource) {
        try {
            return getResult(deleteVoter, "deleteResource", ExecutionContext.class, String.class, null, resource.getURI());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("No DELETE permission for < " + resource.getURIString() + ">:" + e.getMessage());
            }
            return false;
        }
    }

    public boolean isResourceReadable(String uri) {
        try {
            return getResult(readVoter, "getResource", ExecutionContext.class, String.class, null, uri);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFolderReadable(String uri) {
        try {
            return getResult(folderReadVoter, "getFolder", ExecutionContext.class, String.class, null, uri);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdministrable(String uri) {
        try {
            return getResult(adminVoter, "saveResource", ExecutionContext.class, String.class, null, uri);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdministrable(Resource resource) {
        try {
            return getResult(adminVoter, "saveResource", ExecutionContext.class, Resource.class, null, resource);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExecutable(Resource resource) {
        throw new NotFoundException("isExecutable is not supported yet");
    }

    public void setAdminVoter(AclEntryVoter adminVoter) {
        this.adminVoter = adminVoter;
    }

    public void setUpdateVoter(AclEntryVoter updateVoter) {
        this.updateVoter = updateVoter;
    }

    public void setDeleteVoter(AclEntryVoter deleteVoter) {
        this.deleteVoter = deleteVoter;
    }

    public void setReadVoter(AclEntryVoter readVoter) {
        this.readVoter = readVoter;
    }

    public void setFolderReadVoter(AclEntryVoter folderReadVoter) {
        this.folderReadVoter = folderReadVoter;
    }

    public void setInterceptor(MethodSecurityInterceptor interceptor) {
        this.interceptor = interceptor;
    }

}
