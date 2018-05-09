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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import org.springframework.security.core.Authentication;

/**
 * @author Volodya Sabadosh
 * @version $Id: $
 */
public class AttributePathTransformer {
    public static final String TENANT_MARKER = "/".concat(TenantService.ORGANIZATIONS).concat("/");

    //TODO: Add more details...
     /* Transforms a given profile attribute URI according to an authenticated user.
     *
     * @param   uri a profile attribute absolute URI
     * @param   authentication an <code>Authentication</code> object.
     *
     * @return  The profile attribute URI according to an authenticated user.
     */
    public String transformPath(String uri, Authentication authentication) {
        String tenantId = ((User) authentication.getPrincipal()).getTenantId();

        if (tenantId != null && !TenantService.ORGANIZATIONS.equals(tenantId)) {
            // Not a superuser.
            final int tenantPos = uri.indexOf(TENANT_MARKER.concat(tenantId));
            if (tenantPos == -1) {
                return uri;
            } else {
                String result = uri.substring(0, tenantPos + 1);
                int tailPos = uri.indexOf('/', uri.lastIndexOf(TENANT_MARKER) + TENANT_MARKER.length());
                if (tailPos != -1) {
                    result = result.concat(uri.substring(tailPos + 1, uri.length()));
                }
                return result;
            }
        } else {
            // A superuser
            return "/";
        }
    }

}
