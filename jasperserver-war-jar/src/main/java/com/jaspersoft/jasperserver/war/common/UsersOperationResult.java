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

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;

/**
 */
public class UsersOperationResult implements Serializable {

    private String roleName;

    private Set assignedUsers;

    private Set unassignedUsers;

    public UsersOperationResult(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set getAssignedUsers() {

        if (assignedUsers == null) {
            assignedUsers = new HashSet();
        }

        return assignedUsers;
    }

    public void setAssignedUsers(Set assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Set getUnassignedUsers() {

        if (unassignedUsers == null) {
            unassignedUsers = new HashSet();
        }

        return unassignedUsers;
    }

    public void setUnassignedUsers(Set unassignedUsers) {
        this.unassignedUsers = unassignedUsers;
    }

    public Object clone() throws CloneNotSupportedException {
        UsersOperationResult clone = new UsersOperationResult(this.roleName);

        clone.getAssignedUsers().addAll(this.getAssignedUsers());
        clone.getUnassignedUsers().addAll(this.getUnassignedUsers());

        return clone;
    }
}

