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

package com.jaspersoft.jasperserver.api.metadata.user.service;

import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Represents a category for profile attribute
 *
 * @author Vlad Zavadskii
 * @version $Id$
 */
public enum ProfileAttributeCategory {
    USER("User Profile") {
        @Override
        public Object getPrincipal(ResourceFactory resourceFactory) {
            return getCurrentUser();
        }
    },

    TENANT("Tenant Profile") {
        @Override
        public Object getPrincipal(ResourceFactory resourceFactory) {
            User user = getCurrentUser();
            Tenant tenant = null;

            if (user != null && user.getTenantId() != null) {
                tenant = (Tenant) resourceFactory.newObject(Tenant.class);
                tenant.setId(user.getTenantId());
            }

            return tenant;
        }
    },

    SERVER("Server Profile") {
        @Override
        public Object getPrincipal(ResourceFactory resourceFactory) {
            Tenant tenant = (Tenant) resourceFactory.newObject(Tenant.class);
            tenant.setId(TenantService.ORGANIZATIONS);
            tenant.setAlias(TenantService.ORGANIZATIONS);
            tenant.setTenantName("root");
            tenant.setTenantDesc(TenantService.ORGANIZATIONS);
            tenant.setTenantNote(" ");
            tenant.setTenantUri("/");
            tenant.setTenantFolderUri("/");
            tenant.setTheme("default");

            return tenant;
        }
    },

    HIERARCHICAL("Entire hierarchy") {
        @Override
        public Object getPrincipal(ResourceFactory resourceFactory) {
            return null;
        }
    };

    ProfileAttributeCategory(String label) {
        this.label = label;
    }

    private String label;

    private static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        } else {
            return null;
        }
    }

    public String getLabel() {
        return label;
    }

    public abstract Object getPrincipal(ResourceFactory resourceFactory);
}
