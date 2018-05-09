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

package com.jaspersoft.jasperserver.api.metadata.user.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.PaginatedOperationResult;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

import java.util.List;
import java.util.Set;

/**
 * RoleManagerService is the interface which is used to manage special in-memory role related UI functionality which
 * allows do cancellation of role related changes if the final save was not performed.
 *
 * @author schubar
 * @version $Id$
 * @since 3.5.0
 */
@JasperServerAPI
public interface RoleManagerService {

    /**
     * Returns a paginated result of {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} object
     * which do not have the {@link Role} with the specified role name assigned.
     *
     * @param context the execution context.
     * @param roleName the user name.
     * @param userName the role name.
     * @param assignedUserNameSet the set of usernames to which
     *          {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} objects {@link Role}
     *          with the specified role name was assigned.
     * @param unassignedUserNameSet the set of usernames from which
     *          {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} objects {@link Role}
     *          with the specified role name was unassigned.
     * @param firstResult the number of the first user.
     * @param maxResults the maximum number of users in the list.
     *
     * @return a paginated result of users which do not have the role with the specified role name assigned.
     */
    public PaginatedOperationResult getUsersWithoutRole(ExecutionContext context, String roleName, String userName,
            Set assignedUserNameSet, Set unassignedUserNameSet, int firstResult, int maxResults);

    /**
     * Returns a paginated result of {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} object
     * which have the {@link Role} with the specified role name assigned.
     *
     * @param context the execution context.
     * @param roleName the user name.
     * @param userName the role name.
     * @param assignedUserNameSet the set of usernames to which
     *          {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} objects {@link Role}
     *          with the specified role name was assigned.
     * @param unassignedUserNameSet the set of usernames from which
     *          {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} objects {@link Role}
     *          with the specified role name was unassigned.
     * @param firstResult the number of the first user.
     * @param maxResults the maximum number of users in the list.
     *
     * @return a paginated result of users which have the role with the specified role name assigned.
     */
    public PaginatedOperationResult getUsersWithRole(ExecutionContext context, String roleName, String userName,
            Set assignedUserNameSet, Set unassignedUserNameSet, int firstResult, int maxResults);

    /**
     * Performs in-memory role update.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param roleDetails the role.
     * @param assignedUserNameSet the set of usernames to which
     *          {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} objects {@link Role}
     *          with the specified role name was assigned.
     * @param unassignedUserNameSet the set of usernames from which
     *          {@link com.jaspersoft.jasperserver.api.metadata.user.domain.User User} objects {@link Role}
     *          with the specified role name was unassigned.
     */
    public void updateRole(ExecutionContext context, String roleName, Role roleDetails, Set<User> assignedUserNameSet,
            Set<User> unassignedUserNameSet);
			
	public void deleteAll(ExecutionContext context, List<String> roles);		
}
