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
package com.jaspersoft.jasperserver.api.metadata.security;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public abstract class BasicObjectPermissionArgumentVoter implements AccessDecisionVoter<Object>, AfterInvocationProvider {

    @Override
    public boolean supports(Class clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz)
                || ObjectPermission.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;
        if (supports(object, attributes)) {
            if (isPrivileged(object)) {
                result = ACCESS_GRANTED;
            } else {
                Collection<ObjectPermission> permissions = getObjectPermissions(object);
                if (permissions != null && !permissions.isEmpty()) {
                    result = ACCESS_GRANTED;
                    for (ObjectPermission permission : permissions) {
                        if (!isPermitted(authentication, permission, object)) {
                            result = ACCESS_DENIED;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Object decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes,
            Object returnedObject) throws AccessDeniedException {
        if (supports(object, attributes)) {
            if (returnedObject instanceof Collection) {
                Collection<ObjectPermission> returned = getObjectPermissions(returnedObject), res = new LinkedList<ObjectPermission>();
                for (ObjectPermission permission : returned) {
                    if (isPermitted(authentication, permission, object)
                            || isPrivileged(object)) {
                        res.add(permission);
                    }
                }
                returnedObject = res;
            } else if (returnedObject instanceof ObjectPermission
                    && !(isPermitted(authentication, (ObjectPermission) returnedObject, object)
                    || isPrivileged(object))) {
                returnedObject = null;
            }
        }
        return returnedObject;
    }

    protected boolean supports(Object object, Collection<ConfigAttribute> attributes) {
        boolean supports = false;
        if (object != null && supports(object.getClass()) && attributes != null) {
            for (ConfigAttribute configAttribute : attributes) {
                if (supports(configAttribute)) {
                    supports = true;
                    break;
                }
            }
        }
        return supports;
    }

    protected Collection<ObjectPermission> getObjectPermissions(Object object) {
        List<ObjectPermission> permissions = new ArrayList<ObjectPermission>();
        if (object instanceof ObjectPermission) {
            permissions.add((ObjectPermission) object);
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            // check only first element for instanceof ObjectPermission because of performance reason
            if (!collection.isEmpty() && collection.iterator().next() instanceof ObjectPermission) {
                // if the first element is ObjectPermission, then let's assume, that the rest are also ;)
                permissions.addAll(collection);
            }
        } else if (object instanceof MethodInvocation) {
            Object[] arguments = ((MethodInvocation) object).getArguments();
            if (arguments != null) {
                for (Object argument : arguments) {
                    // collect all arguments from invocation and check them all
                    permissions.addAll(getObjectPermissions(argument));
                }
            }
        }
        return permissions;
    }

    protected abstract boolean isPermitted(Authentication authentication, ObjectPermission objectPermission, Object object);

    private boolean isPrivileged(Object object){
        if (object instanceof MethodInvocation) {
            Object[] arguments = ((MethodInvocation) object).getArguments();
            if (arguments != null) {
                for (Object argument : arguments) {
                    if (argument instanceof ExecutionContext && ((ExecutionContext) argument).getAttributes().contains(ObjectPermissionService.PRIVILEGED_OPERATION)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
