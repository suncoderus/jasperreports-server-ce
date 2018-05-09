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


import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author achan
 *
 */
public class RepositoryExplorerTreeDataFilterImpl implements TreeDataFilter {

    
    private List uriList;
    private UserAuthorityService userService;
    private String roleToShowTempFolder;

    /**
     * Returns false if node is a resource or a child of a resource with uri 
     * which is in uriList.
     * Returns true otherwise.
     * Returns true if no uriList configured.
     */
    public boolean filter(TreeNode node) {

        if (uriList != null) {
            String nodeUri = node.getUriString();
            for (Iterator iter = uriList.iterator(); iter.hasNext(); ) {
                String uri = (String) iter.next();
                if ((nodeUri.equalsIgnoreCase(uri)) || (nodeUri.indexOf(uri + "/") == 0)) {
                    Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                    final Collection<? extends GrantedAuthority> grantedAuthorities = existingAuth.getAuthorities();
                    if (grantedAuthorities != null) {
                        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
                            if (roleToShowTempFolder.equals(grantedAuthority.getAuthority())) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public List getUriList() {
        return uriList;
    }

    public void setUriList(List uriList) {
        this.uriList = uriList;
    }
    
    public UserAuthorityService getUserService() {
        return userService;
    }

    public void setUserService(UserAuthorityService service) {
        this.userService = service;
    }  
    
    public String getRoleToShowTempFolder() {
        return roleToShowTempFolder;
    }

    public void setRoleToShowTempFolder(String folder) {
        this.roleToShowTempFolder = folder;
    } 
    
}
