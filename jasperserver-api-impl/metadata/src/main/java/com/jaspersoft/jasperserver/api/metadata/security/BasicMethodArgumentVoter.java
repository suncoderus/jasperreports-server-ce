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

import com.jaspersoft.jasperserver.api.common.util.Functor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Lucian Chirita
 *
 */
public class BasicMethodArgumentVoter implements MethodArgumentAclVoter {

	private static final Log log = LogFactory.getLog(BasicMethodArgumentVoter.class);

	private Class argumentType;
	private int argumentIndex = 1;
	private Permission[] accessPermissions;
	private Functor argumentFunctor;
    private SidRetrievalStrategy sidRetrievalStrategy;
    private AclService aclService;
    private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy;
	
	public boolean allow(MethodInvocation methodCall, Authentication authentication) {
		Object secureObject = getSecureObject(methodCall);
		return accessPermitted(authentication, secureObject);
	}

	protected Object getSecureObject(MethodInvocation methodCall) {
		Class[] types = methodCall.getMethod().getParameterTypes();
		int idx = -1;
		int cnt = 0;
		for (int i = 0; i < types.length; i++) {
			if (argumentType.isAssignableFrom(types[i])) {
				++cnt;
				if (cnt == argumentIndex) {
					idx = i;
					break;
				}
			}
		}
		
		if (idx == 0) {
			throw new AuthorizationServiceException("Argument #" + argumentIndex + " of type " + argumentType
					+ " not found in method " + methodCall.getMethod());
		}
		
		Object arg = methodCall.getArguments()[idx];
		
		if (log.isDebugEnabled()) {
			log.debug("Found argument #" + argumentIndex + " of type " + argumentType
					+ " in methdo " + methodCall.getMethod() + ": " + arg);
		}

		if (argumentFunctor != null) {
			arg = argumentFunctor.transform(arg);
			
			if (log.isDebugEnabled()) {
				log.debug("Argument transformed to " + arg);
			}
		}
		return arg;
	}

	protected boolean accessPermitted(Authentication authentication,Object secureObject) {
        ObjectIdentity objectIdentity = getObjectIdentityRetrievalStrategy().getObjectIdentity(secureObject);
        List<Sid> sidList = getSidRetrievalStrategy().getSids(authentication);
        Acl acl = getAclService().readAclById(objectIdentity, sidList);
        boolean permitted = acl.isGranted(new ArrayList<Permission>(Arrays.asList(accessPermissions)),sidList,false);
		if (log.isDebugEnabled()) {
			if (permitted) {
				log.debug("Access permitted on " + secureObject + " for " + authentication);
			} else {
				log.debug("Access denied on " + secureObject + " for " + authentication);
			}
		}
		return permitted;
	}


	public Class getArgumentType() {
		return argumentType;
	}

	public void setArgumentType(Class argumentType) {
		this.argumentType = argumentType;
	}

	public int getArgumentIndex() {
		return argumentIndex;
	}

	public void setArgumentIndex(int argumentIndex) {
		this.argumentIndex = argumentIndex;
	}

	public Permission[] getAccessPermissions() {
		return accessPermissions;
	}

	public void setAccessPermissions(Permission[] accessPermissions) {
		this.accessPermissions = accessPermissions;
	}

	public Functor getArgumentFunctor() {
		return argumentFunctor;
	}

	public void setArgumentFunctor(Functor argumentFunctor) {
		this.argumentFunctor = argumentFunctor;
	}
    @Override
    public void setSidRetrievalStrategy(SidRetrievalStrategy sidRetrievalStrategy) {
        this.sidRetrievalStrategy = sidRetrievalStrategy;
    }
    @Override
    public void setAclService(AclService aclService) {
        this.aclService = aclService;
    }
    @Override
    public void setObjectIdentityRetrievalStrategy(ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy) {
        this.objectIdentityRetrievalStrategy = objectIdentityRetrievalStrategy;
    }


    public SidRetrievalStrategy getSidRetrievalStrategy() {
        return sidRetrievalStrategy;
    }

    public AclService getAclService() {
        return aclService;
    }

    public ObjectIdentityRetrievalStrategy getObjectIdentityRetrievalStrategy() {
        return objectIdentityRetrievalStrategy;
    }
}
