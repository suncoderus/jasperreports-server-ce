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
package com.jaspersoft.jasperserver.war.common;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Role access resolver.
 *
 * @author Yuriy Plakosh
 */
public class RoleAccessUrisResolver implements Serializable {
    private List<RoleAccessUris> roleAccessUrisList;

    public void setRoleAccessUrisList(List<RoleAccessUris> roleAccessUrisList) {
        this.roleAccessUrisList = roleAccessUrisList;
    }

    public List<UriDescriptor> getRestrictedUris() {
        List<UriDescriptor> restrictedUris = new ArrayList<UriDescriptor>();

        for (RoleAccessUris roleAccessUris : roleAccessUrisList) {
            if (!hasRole(roleAccessUris)) {
                restrictedUris.addAll(roleAccessUris.getUris());
            }
        }

        return restrictedUris;
    }

    public String getAbsoluteUri(String path) {
        return path;
    }

    @SuppressWarnings({"unchecked"})
    protected boolean hasRole(RoleAccessUris roleAccessUris) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Set<Role> roles = ((User)authentication.getPrincipal()).getRoles();

        for (Role role : roles) {
            if (role.getRoleName().equals(roleAccessUris.getRoleName())) {
                return true;
            }
        }

        return false;
    }

    
}
