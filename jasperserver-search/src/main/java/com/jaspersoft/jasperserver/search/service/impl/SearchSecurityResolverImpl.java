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

package com.jaspersoft.jasperserver.search.service.impl;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.search.common.RoleAccess;
import com.jaspersoft.jasperserver.search.service.SearchSecurityResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class SearchSecurityResolverImpl implements SearchSecurityResolver, Serializable {

    /**
     * @see SearchSecurityResolver#hasAccess(com.jaspersoft.jasperserver.search.common.RoleAccess) 
     */
    public boolean hasAccess(RoleAccess roleAccess) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Set<Role> roles = ((User)authentication.getPrincipal()).getRoles();

        for (Role role : roles) {
            if (hasAccess(role, roleAccess)) {
                return true;
            }
        }

        return false;
    }

    protected boolean hasAccess(Role role, RoleAccess roleAccess) {
        return role.getRoleName().equals(roleAccess.getRoleName());
    }
}
