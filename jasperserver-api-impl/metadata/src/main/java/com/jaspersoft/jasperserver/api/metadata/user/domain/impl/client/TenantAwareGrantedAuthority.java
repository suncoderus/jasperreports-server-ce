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

package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public class TenantAwareGrantedAuthority implements GrantedAuthority, TenantQualified, Serializable {
    private String authority;
    private String tenantId;

    public TenantAwareGrantedAuthority(String authority, String tenantId) {
        this.authority = authority;
        this.tenantId = tenantId;
    }

    public TenantAwareGrantedAuthority(Role role) {
        this.authority = role.getRoleName();
        this.tenantId = (role.getTenantId() == TenantService.ORGANIZATIONS) ? null : role.getTenantId();
    }

    public TenantAwareGrantedAuthority(String authority) {
        this(authority, null);
    }

    public String getAuthority() {
        //return authority;
        // acegi does not recognize tenants
        // we need to encode it into authority
        return (tenantId == null) ? authority : authority + "|" + tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        //for compatibility
        if (obj instanceof String) {
            return obj.equals(this.authority);
        }

        //for compatibility
        if (obj instanceof GrantedAuthority && !(obj instanceof TenantQualified)) {
            return ((GrantedAuthority) obj).getAuthority().equals(authority);
        }

        if (obj instanceof GrantedAuthority && obj instanceof TenantQualified) {
            String authority = ((GrantedAuthority) obj).getAuthority();
            String tenantId = ((TenantQualified) obj).getTenantId();

            return authority.equals(this.authority) && (tenantId == null ? this.tenantId == null : tenantId.equals(this.tenantId));
        }

        return false;
    }

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}