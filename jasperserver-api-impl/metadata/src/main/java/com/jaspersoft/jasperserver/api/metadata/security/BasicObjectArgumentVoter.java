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
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.PermissionOverride;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AfterInvocationProvider;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @author Volodya Sabadosh
 * @version $Id$
 */
public abstract class BasicObjectArgumentVoter<T> implements AccessDecisionVoter<Object>, AfterInvocationProvider {
    private Class<T> classParameterType;

    @Override
    public boolean supports(Class<?> clazz) {
        Class<T> type = getClassParameterType();

        return MethodInvocation.class.isAssignableFrom(clazz)
                || type.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;
        if (supports(object, attributes)) {
            if (isPrivileged(object)) {
                result = ACCESS_GRANTED;
            } else {
                Collection<T> filterObjects = getFilteredObjects(object);
                if (filterObjects != null && !filterObjects.isEmpty()) {
                    result = ACCESS_GRANTED;
                    for (T filterObject : filterObjects) {
                        if (!isPermitted(authentication, filterObject, object)) {
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
    @SuppressWarnings ("unchecked")
    public Object decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes,
                         Object returnedObject) throws AccessDeniedException {
        if (supports(object, attributes)) {
            Class<T> type = getClassParameterType();
            if (returnedObject == null || type.isAssignableFrom(returnedObject.getClass()) &&
                    !isPermitted(authentication, (T)returnedObject, object)) {
                returnedObject = null;
            } else if (returnedObject instanceof Collection) {
                Collection<T> returned = getFilteredObjects(returnedObject), res = new LinkedList<T>();
                for (T filteredObject : returned) {
                    if (isPermitted(authentication, filteredObject, object)) {
                        res.add(filteredObject);
                    }
                }
                returnedObject = res;
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

    @SuppressWarnings ("unchecked")
    protected Collection<T> getFilteredObjects(Object object) {
        Class<T> type = getClassParameterType();
        List<T> filteredObjects = new ArrayList<T>();
        if (type.isAssignableFrom(object.getClass())) {
            filteredObjects.add((T)object);
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            // check only first element for instanceof ObjectPermission because of performance reason
            if (!collection.isEmpty() && type.isAssignableFrom(collection.iterator().next().getClass())) {
                // if the first element is ObjectPermission, then let's assume, that the rest are also ;)
                filteredObjects.addAll(collection);
            }
        } else if (object instanceof MethodInvocation) {
            Object[] arguments = ((MethodInvocation) object).getArguments();
            if (arguments != null) {
                for (Object argument : arguments) {
                    // collect all arguments from invocation and check them all
                    if (argument != null) {
                        filteredObjects.addAll(getFilteredObjects(argument));
                    }
                }
            }
        }
        return filteredObjects;
    }

    protected abstract boolean isPermitted(Authentication authentication, T filteredObject, Object object);

    protected boolean isPrivileged(Object object){
        if (object instanceof MethodInvocation) {
            Object[] arguments = ((MethodInvocation) object).getArguments();
            if (arguments != null) {
                for (Object argument : arguments) {
                    if (argument instanceof ExecutionContext && ((ExecutionContext) argument).getAttributes().
                            contains(ObjectPermissionService.PRIVILEGED_OPERATION)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean isPrivilegedForExecuteOnly(Object object) {
        Object[] args;

        if (object instanceof MethodInvocation) {
            MethodInvocation invocation = (MethodInvocation) object;
            args = invocation.getArguments();
        } else {
            throw new AuthorizationServiceException("Secure object: " + object + " is not a MethodInvocation");
        }

        // ex context is always the first argument
        ExecutionContext ctx = null;
        PermissionOverride permOverride = null;

        for (Object argument : args) {
            if (argument instanceof ExecutionContext) {
                ctx = (ExecutionContext)argument;
            }
        }

        if (ctx != null && ctx.getAttributes() != null) {
            for (Object a : ctx.getAttributes()) {
                if (a instanceof PermissionOverride) {
                    permOverride = (PermissionOverride) a;
                }
            }
        }
        return permOverride != null && permOverride.getOverrideId().equals(ExecutionContextImpl.EXECUTE_OVERRIDE);
    }

    @SuppressWarnings ("unchecked")
    protected Class<T> getClassParameterType() {
        if (this.classParameterType == null) {
            Class specificVoterClass = getClass();
            while (specificVoterClass.getSuperclass() != BasicObjectArgumentVoter.class &&
                    specificVoterClass.getSuperclass() != null) {
                specificVoterClass = specificVoterClass.getSuperclass();
            }
            if (specificVoterClass != Object.class) {
                Type baseVoterInheritanceDeclaration = specificVoterClass.getGenericSuperclass();
                if (baseVoterInheritanceDeclaration instanceof ParameterizedType) {
                    Type[] actualTypeArguments = ((ParameterizedType) baseVoterInheritanceDeclaration).
                            getActualTypeArguments();
                    if (actualTypeArguments.length > 0) {
                        Type firstActualTypeArgument = actualTypeArguments[0];
                        if (firstActualTypeArgument instanceof Class) {
                            this.classParameterType = (Class) firstActualTypeArgument;
                        }
                    }
                }
            }
        }
        return this.classParameterType;
    }

}
