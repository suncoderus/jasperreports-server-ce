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

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * @author Lucian Chirita
 *
 */
public class MultiAclEntryVoter implements AccessDecisionVoter<Object> {

	private ConfigAttribute configAttribute;
	private AclService aclService;
    private SidRetrievalStrategy sidRetrievalStrategy;
    private ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy;
	private MethodArgumentAclVoter[] argumentVoters;
    private Boolean overrideVoterProperties=true;
	
	public boolean supports(ConfigAttribute attribute) {
		return configAttribute.equals(attribute);
	}

	public boolean supports(Class clazz) {
		return MethodInvocation.class.isAssignableFrom(clazz);
	}

	public int vote(Authentication authentication, Object object,
            Collection<ConfigAttribute> attributes) {
		int access;
		if (attributes.contains(configAttribute)) {
			MethodInvocation call = (MethodInvocation) object;
			access = ACCESS_GRANTED;
			for (int i = 0; i < argumentVoters.length; i++) {
				MethodArgumentAclVoter voter = argumentVoters[i];
                if (overrideVoterProperties) {
                    setPropertiesForVoter(voter);
                }
				if (!voter.allow(call, authentication)) {
					access = ACCESS_DENIED;
					break;
				}
			}
		} else {
			access = ACCESS_ABSTAIN;
		}
		return access;
	}

    private void setPropertiesForVoter(MethodArgumentAclVoter voter) {
        voter.setAclService(aclService);
        voter.setSidRetrievalStrategy(sidRetrievalStrategy);
        voter.setObjectIdentityRetrievalStrategy(objectIdentityRetrievalStrategy);
    }

	public ConfigAttribute getConfigAttribute() {
		return configAttribute;
	}

	public void setConfigAttribute(ConfigAttribute configAttribute) {
		this.configAttribute = configAttribute;
	}

	/**
	 * @return Returns the argumentVoters.
	 */
	public MethodArgumentAclVoter[] getArgumentVoters() {
		return argumentVoters;
	}

	/**
	 * @param argumentVoters The argumentVoters to set.
	 */
	public void setArgumentVoters(MethodArgumentAclVoter[] argumentVoters) {
		this.argumentVoters = argumentVoters;
	}

	public void setAclService(AclService aclService) {
		this.aclService = aclService;
	}

    public void setSidRetrievalStrategy(SidRetrievalStrategy sidRetrievalStrategy) {
        this.sidRetrievalStrategy = sidRetrievalStrategy;
    }

    public void setObjectIdentityRetrievalStrategy(ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy) {
        this.objectIdentityRetrievalStrategy = objectIdentityRetrievalStrategy;
    }

    public void setOverrideVoterProperties(Boolean overrideVoterProperties) {
        this.overrideVoterProperties = overrideVoterProperties;
    }
}
