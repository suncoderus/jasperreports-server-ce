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
package com.jaspersoft.jasperserver.remote.common;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;

import java.util.List;

/**
 * @author Volodya Sabadosh (vsabadosh@jaspersoft.com)
 * @version $Id $
 */
public class UserSearchCriteria {
    private java.lang.String name;

    private java.lang.String tenantId;

    private java.lang.Boolean includeSubOrgs;

    private java.lang.Boolean hasAllRequiredRoles = Boolean.TRUE;  //default

    private List<Role> requiredRoles;

    private int maxRecords;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getIncludeSubOrgs() {
        return includeSubOrgs;
    }

    public void setIncludeSubOrgs(Boolean includeSubOrgs) {
        this.includeSubOrgs = includeSubOrgs;
    }

    public List<Role> getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(List<Role> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    public Boolean getHasAllRequiredRoles() {
        return hasAllRequiredRoles;
    }

    public void setHasAllRequiredRoles(Boolean haveAllRequiredRoles) {
        this.hasAllRequiredRoles = haveAllRequiredRoles;
    }
}
