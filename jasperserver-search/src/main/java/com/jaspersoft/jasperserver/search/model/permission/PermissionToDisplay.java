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

package com.jaspersoft.jasperserver.search.model.permission;

/**
 * Permission to display.
 *
 * @author Yuriy Plakosh
 */
public class PermissionToDisplay {
    private Permission permission;
    private Permission inheritedPermission;
    private boolean inherited = false;
    private Permission newPermission;
    private boolean disabled = false;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Permission getInheritedPermission() {
        return inheritedPermission;
    }

    public void setInheritedPermission(Permission inheritedPermission) {
        this.inheritedPermission = inheritedPermission;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public Permission getNewPermission() {
        return newPermission;
    }

    public void setNewPermission(Permission newPermission) {
        this.newPermission = newPermission;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
