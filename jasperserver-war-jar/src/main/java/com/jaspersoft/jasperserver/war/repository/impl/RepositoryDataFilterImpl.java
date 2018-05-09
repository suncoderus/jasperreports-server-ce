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

package com.jaspersoft.jasperserver.war.repository.impl;

import com.jaspersoft.jasperserver.war.repository.RepositoryDataFilter;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import java.util.List;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Sergey Prilukin
 */
public class RepositoryDataFilterImpl implements RepositoryDataFilter {

    private List uriList;
    private UserAuthorityService userService;
    private String roleToShowFolders;

    public List getUriList() {
        return uriList;
    }

    public void setUriList(List uriList) {
        this.uriList = uriList;
    }

    public UserAuthorityService getUserService() {
        return userService;
    }

    public void setUserService(UserAuthorityService userService) {
        this.userService = userService;
    }

    public String getRoleToShowFolders() {
        return roleToShowFolders;
    }

    public void setRoleToShowFolders(String roleToShowFolders) {
        this.roleToShowFolders = roleToShowFolders;
    }

    public boolean filter(String uri) {
        if (uriList != null) {
            for (Iterator iter = uriList.iterator(); iter.hasNext(); ) {
                String uriReEx = (String) iter.next();
                Matcher matcher = Pattern.compile(uriReEx, Pattern.CASE_INSENSITIVE).matcher(uri);
                if (matcher.matches()) {
                	Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
                	String userName = existingAuth.getName();
                	List userList = userService.getUsersInRole(null, roleToShowFolders);
                	//if user has the role, in this case ROLE_ADMIN from the xml file, return true
                	for (Iterator userIter = userList.iterator(); userIter.hasNext();) {
                		String user = ((User)userIter.next()).getUsername();
                		if (user.equalsIgnoreCase(userName)) {
                			return true;
                		}
                	}
                    return false;
                }
            }
        }
        return true;
    }
}
