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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.acls.afterinvocation.AbstractAclProvider;
import org.springframework.security.acls.afterinvocation.AclEntryAfterInvocationProvider;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;

import java.util.Collection;
import java.util.List;

public class DefaultValueAfterInvocationFilteringProvider extends AbstractAclProvider {
    protected static final Log logger = LogFactory.getLog(DefaultValueAfterInvocationFilteringProvider.class);
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private Object defaultValue=null;

    public DefaultValueAfterInvocationFilteringProvider(AclService aclService, String processConfigAttribute, List<Permission> requirePermission) {
        super(aclService, processConfigAttribute, requirePermission);
    }

    @Override
    public Object decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes, Object returnedObject) throws AccessDeniedException {
        if (returnedObject == null) {
            // AclManager interface contract prohibits nulls
            // As they have permission to null/nothing, grant access
            logger.debug("Return object is null, skipping");

            return null;
        }

        if (!getProcessDomainObjectClass().isAssignableFrom(returnedObject.getClass())) {
            logger.debug("Return object is not applicable for this provider, skipping");

            return returnedObject;
        }

        for (ConfigAttribute attr : attributes) {
            if (!this.supports(attr)) {
                continue;
            }
            // Need to make an access decision on this invocation

            if (hasPermission(authentication, returnedObject)) {
                return returnedObject;
            }

            logger.debug("Denying access and returning defaultValue");

            return defaultValue;
        }

        return returnedObject;

    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
