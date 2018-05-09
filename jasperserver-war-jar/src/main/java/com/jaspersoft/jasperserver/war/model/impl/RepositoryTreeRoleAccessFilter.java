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
package com.jaspersoft.jasperserver.war.model.impl;

import com.jaspersoft.jasperserver.war.common.RoleAccessUrisResolver;
import com.jaspersoft.jasperserver.war.common.UriDescriptor;
import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * Repository tree role access filter.
 *
 * @author Yuriy Plakosh
 */
public class RepositoryTreeRoleAccessFilter implements TreeDataFilter {
    private RoleAccessUrisResolver roleAccessUrisResolver;

    public void setRoleAccessUrisResolver(RoleAccessUrisResolver roleAccessUrisResolver) {
        this.roleAccessUrisResolver = roleAccessUrisResolver;
    }

    public boolean filter(TreeNode node) {
        for (UriDescriptor uriDescriptor : roleAccessUrisResolver.getRestrictedUris()) {
            String uri = (uriDescriptor.isAbsolute()) ? uriDescriptor.getUri() :
                   roleAccessUrisResolver.getAbsoluteUri(uriDescriptor.getUri());

            if (node != null && node.getUriString() != null &&
                    roleAccessUrisResolver.getAbsoluteUri(node.getUriString()).endsWith(uri)) {
                return false;
            }
        }

        return true;
    }
}
