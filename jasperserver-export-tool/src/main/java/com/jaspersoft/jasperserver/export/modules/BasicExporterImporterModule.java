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
package com.jaspersoft.jasperserver.export.modules;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.AclPermissionsSecurityChecker;
import com.jaspersoft.jasperserver.export.modules.repository.beans.PermissionRecipient;

/**
 * @author Volodya Sabadosh
 * @version $Id: $
 */
public abstract class BasicExporterImporterModule {

    protected String permissionRecipientRole;
    protected String permissionRecipientUser;

    protected ObjectPermissionService permissionService;
    protected AclPermissionsSecurityChecker aclSecurityChecker;

    public AclPermissionsSecurityChecker getAclSecurityChecker() {
        return aclSecurityChecker;
    }

    public void setAclSecurityChecker(AclPermissionsSecurityChecker aclSecurityChecker) {
        this.aclSecurityChecker = aclSecurityChecker;
    }

    public String getPermissionRecipientRole() {
        return permissionRecipientRole;
    }

    public void setPermissionRecipientRole(String permissionRecipientRole) {
        this.permissionRecipientRole = permissionRecipientRole;
    }

    public String getPermissionRecipientUser() {
        return permissionRecipientUser;
    }

    public void setPermissionRecipientUser(String permissionRecipientUser) {
        this.permissionRecipientUser = permissionRecipientUser;
    }

    public ObjectPermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(ObjectPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    protected PermissionRecipient toPermissionRecipient(Object permissionRecipient) {
        if (permissionRecipient instanceof Role) {
            Role role = (Role) permissionRecipient;
            return new PermissionRecipient(getPermissionRecipientRole(),
                    role.getTenantId(), role.getRoleName());
        } else if (permissionRecipient instanceof User) {
            User user = (User) permissionRecipient;
            return new PermissionRecipient(getPermissionRecipientUser(),
                    user.getTenantId(), user.getUsername());
        } else {
            // Adding non localized message cause import-export tool does not support localization.
            StringBuilder message = new StringBuilder("Permission recipient type ");
            message.append(permissionRecipient.getClass().getName());
            message.append(" is not recognized.");
            throw new JSException(message.toString());
        }
    }

}
