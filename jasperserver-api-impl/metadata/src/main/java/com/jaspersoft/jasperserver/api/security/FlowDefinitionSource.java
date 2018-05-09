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
import org.springframework.security.access.SecurityMetadataSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class FlowDefinitionSource implements SecurityMetadataSource {

	protected static final String DEFAULT_FLOW = "*";
	
	private Map<Object, Collection<ConfigAttribute>> flowAttributes;
	
	public FlowDefinitionSource() {
		flowAttributes = new HashMap();
	}
	
	public void addFlow(String flowId, Collection<ConfigAttribute> attributes) {
		flowAttributes.put(flowId, attributes);
	}
	
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        Collection<ConfigAttribute> attributes =  flowAttributes.get(object);
		if (attributes == null) {
			attributes = flowAttributes.get(DEFAULT_FLOW);
		}
		return attributes;
	}

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();

        for (Map.Entry<Object, Collection<ConfigAttribute>> entry : flowAttributes.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }

        return allAttributes;
    }

    public Collection getConfigAttributeDefinitions() {
		return flowAttributes.values();
	}

	public boolean supports(Class clazz) {
        return MethodInvocation.class.isAssignableFrom(clazz);
	}

}
