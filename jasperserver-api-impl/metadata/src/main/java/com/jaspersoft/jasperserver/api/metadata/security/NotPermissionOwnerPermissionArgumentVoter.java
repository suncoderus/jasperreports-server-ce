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

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Component;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class NotPermissionOwnerPermissionArgumentVoter extends BasicObjectPermissionArgumentVoter {
    private static final String ATTRIBUTE = "NOT_PERMISSION_OWNER";

    @Override
    protected boolean isPermitted(Authentication authentication, ObjectPermission objectPermission, Object object) {
        User user = (User)authentication.getPrincipal();
        boolean res = true;

        if (objectPermission.getPermissionRecipient() instanceof User){
            User recipient = (User)objectPermission.getPermissionRecipient();
            res = !((recipient.getUsername() == user.getUsername() || recipient.getUsername().equals(user.getUsername())) &&
                   (recipient.getTenantId() == user.getTenantId() || recipient.getTenantId().equals(user.getTenantId())));
        }

        return res;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return ATTRIBUTE.equals(attribute.getAttribute());
    }
}
