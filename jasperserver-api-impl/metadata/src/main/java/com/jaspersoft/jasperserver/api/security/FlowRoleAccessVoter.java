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

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAclVoter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FlowRoleAccessVoter extends AbstractAclVoter {
	
    private FlowDefinitionSource flowDefinitionSource;
    private String flowAccessAttribute;
    
	public FlowRoleAccessVoter() {
		super();
		
		setProcessDomainObjectClass(String.class);
	}

	public boolean supports(ConfigAttribute attribute) {
		String attr = attribute.getAttribute();
		return attr != null && attr.equals(getFlowAccessAttribute());
	}

	public int vote(Authentication authentication, MethodInvocation object, Collection<ConfigAttribute> attributes) {
		int result = ACCESS_ABSTAIN;
		
		for (Iterator it = attributes.iterator(); it.hasNext(); ) {
			ConfigAttribute attribute = (ConfigAttribute) it.next();
			if (supports(attribute)) {
				result = voteFlowAccess(authentication, object);
			}
		}
		
		return result;
	}

	protected int voteFlowAccess(Authentication authentication, MethodInvocation object) {
		int result;
		
		String flowId = (String) getDomainObjectInstance(object);		
		if (flowId == null) {
			result = ACCESS_ABSTAIN;
		} else {
	        result = ACCESS_DENIED;

            final Collection<ConfigAttribute> attributes = getFlowDefinitionSource().getAttributes(flowId);
	        for (Iterator iter = attributes.iterator(); iter.hasNext(); ) {
	            ConfigAttribute attribute = (ConfigAttribute) iter.next();

				if (matchesAuthority(authentication, attribute.getAttribute())) {
					result = ACCESS_GRANTED;
					break;
				}
	        }
		}
        
		return result;
	}

	protected boolean matchesAuthority(Authentication authentication, String attribute) {
		boolean matches = false;
		for (GrantedAuthority authority : authentication.getAuthorities()) {
		    if (attribute.equals(authority.getAuthority())) {
		    	matches = true;
		    	break;
		    }
		}
		return matches;
	}

	public FlowDefinitionSource getFlowDefinitionSource() {
		return flowDefinitionSource;
	}

	public void setFlowDefinitionSource(FlowDefinitionSource flowDefinitionSource) {
		this.flowDefinitionSource = flowDefinitionSource;
	}

	public String getFlowAccessAttribute() {
		return flowAccessAttribute;
	}

	public void setFlowAccessAttribute(String flowAccessAttribute) {
		this.flowAccessAttribute = flowAccessAttribute;
	}

}
