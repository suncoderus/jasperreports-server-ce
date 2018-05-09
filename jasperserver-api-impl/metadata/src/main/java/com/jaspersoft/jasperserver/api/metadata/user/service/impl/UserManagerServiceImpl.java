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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.PaginatedOperationResult;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.PaginationHelper;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.RoleManagerService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserManagerService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.PropertyFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link UserManagerService}.
 *
 * @author Stas Chubar
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserManagerServiceImpl extends HibernateDaoImpl implements UserManagerService {

    protected static final Log log = LogFactory.getLog(UserManagerServiceImpl.class);

    protected UserAuthorityService userService;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void updateUser(ExecutionContext context, String userName, User userDetails,
                                 Set<Role> assignedRoleSet, Set<Role> unassignedRoleSet) {

        User oldUser = this.userService.getUser(null, userName);

        String newPassword = userDetails.getPassword();
        String oldPassword = oldUser.getPassword();

        if (newPassword == null) {
            userDetails.setPassword(oldPassword);
        } else if (!newPassword.equals(oldPassword)) {
            userDetails.setPreviousPasswordChangeTime(new Date());
        }

        Set roles = oldUser.getRoles();

        FilterCriteria filter = FilterCriteria.createFilter(Role.class);
        List<Role> allRoles = userService.getRoles(null, filter);

        if (allRoles != null) {
            for (Role assignedRole : unassignedRoleSet) {
                roles.remove(getRole(assignedRole, allRoles));
            }

            for (Role assignedRole : assignedRoleSet) {
                roles.add(getRole(assignedRole, allRoles));
            }
        }

        userDetails.setRoles(roles);

        userService.updateUser(null, userName, userDetails);
    }

    private Role getRole(Role roleDetails, List<Role> list) {
        for (Role role : list) {
            if (role.equals(roleDetails)) {
                return role;
            }
        }

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void enableAll(ExecutionContext context, List<String> users) {
        if (users != null) {

            for(String userName : users) {
                if (userName != null && userName.length() > 0) {

                    enableOrDisableUser(context, userName, true);
                }
            }

        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void disableAll(ExecutionContext context, List<String> users) {
        if (users != null) {

            for(String userName : users) {
                if (userName != null && userName.length() > 0) {

                    enableOrDisableUser(context, userName, false);
                }
            }

        }
    }

    protected void enableOrDisableUser(ExecutionContext context, String userName, boolean enable) {
        User user = getUser(context, userName);

        if (user != null && user.isEnabled() != enable) {
            user.setEnabled(enable);
            userService.updateUser(context, userName, user);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteAll(ExecutionContext context, List<String> users) {
        if (users != null) {

            for(String userName : users) {
                if (userName != null && userName.length() > 0) {
                    userService.deleteUser(context, userName);
                }
            }

        }
    }

    protected User getUser(ExecutionContext context, String userName) {
        return userService.getUser(context, userName);
    }

    public void setUserService(UserAuthorityService userService) {
        this.userService = userService;
    }
}