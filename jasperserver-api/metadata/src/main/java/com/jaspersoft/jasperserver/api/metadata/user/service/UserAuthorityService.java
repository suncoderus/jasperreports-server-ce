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

import java.util.List;
import java.util.Set;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import org.hibernate.criterion.DetachedCriteria;

/**
 * UserAuthorityService is the interface which is used to manage {@link User} and {@link Role} objects.
 *
 * @author swood
 * @version $Id$
 * @since 1.0.1
 */
@JasperServerAPI
public interface UserAuthorityService {
    /**
     * Returns the {@link User} object by its username.
     *
     * @param context the eUserAuthorityServiceUserAuthorityServicexecution context.
     * @param username the username.
     *
     * @return the user with specified username or <code>null</code> if the is no user with such username.
     */
	public User getUser(ExecutionContext context, String username);

    /**
     * Saves new or update existing {@link User} object.
     *
     * @param context the execution context.
     * @param aUser the user.
     */
	public void putUser(ExecutionContext context, User aUser);

    /**
     * Returns a list of {@link User} object that match filter criteria.
     *
     * @param context the execution context.
     * @param filterCriteria the filter criteria.
     *
     * @return a list of users.
     */
	public List getUsers(ExecutionContext context, FilterCriteria filterCriteria);

    /**
     * Returns a list of {@link User} object that match detached criteria.
     *
     * @param context the execution context.
     * @param detachedCriteria the filter criteria.
     *
     * @return a list of users.
     */
	public List getUsersByCriteria(ExecutionContext context, DetachedCriteria detachedCriteria);

    /**
     * Returns count of all {@link User} objects except excluded ones.
     *
     * @param executionContext the execution context.
     * @param excludedUserNames the excluded user names.
     * @param excludeDisabledUsers <code>true</code> if disabled users should be excluded.
     *
     * @return count of all {@link User} objects except excluded ones.
     */
    public int getUsersCountExceptExcluded(ExecutionContext executionContext, Set<String> excludedUserNames, boolean excludeDisabledUsers);

    /**
     * Returns a new instance of the {@link User} object.
     *
     * @param context the execution context.
     *
     * @return a new instance of user.
     */
	public User newUser(ExecutionContext context);

    /**
     * Deletes user by its username.
     *
     * @param context the execution context.
     * @param username the username.
     */
	public void deleteUser(ExecutionContext context, String username);

    /**
     * Checks if the user with the specified username exists.
     *
     * @param context the execution context.
     * @param username the username.
     *
     * @return <code>true</code> if user with specified username exists, <code>false</code> otherwise.
     */
	public boolean userExists(ExecutionContext context, String username);

    /**
     * Disables the user by the specified username.
     *
     * @param context the execution context.
     * @param username the username.
     *
     * @return <code>true</code> if user was disabled, <code>false</code> otherwise.
     */
	public boolean disableUser(ExecutionContext context, String username);

    /**
     * Enables the user by the specified username.
     *
     * @param context the execution context.
     * @param username the username.
     *
     * @return <code>true</code> if user was disabled, <code>false</code> otherwise.
     */
	public boolean enableUser(ExecutionContext context, String username);

    /**
     * Adds role to the specified user.
     *
     * @param context the execution context.
     * @param aUser the user.
     * @param role the role.
     */
	public void addRole(ExecutionContext context, User aUser, Role role);

    /**
     * Removes role from the specified user.
     *
     * @param context the execution context.
     * @param aUser the user.
     * @param role the role.
     */
	public void removeRole(ExecutionContext context, User aUser, Role role);

    /**
     * Removes all roles from the specified user.
     *
     * @param context the execution context.
     * @param aUser the user.
     */
	public void removeAllRoles(ExecutionContext context, User aUser);

    /**
     * Returns the role by the specified role name.
     *
     * @param context the execution context.
     * @param roleName the role name.
     *
     * @return the role by the specified role name if such role was found, <code>null</code> otherwise.
     */
	public Role getRole(ExecutionContext context, String roleName);

    /**
     * Saves new or update existing role.
     *
     * @param context the execution context.
     * @param aRole the role.
     */
	public void putRole(ExecutionContext context, Role aRole);

    /**
     * Returns a list of {@link Role} objects which match specified filter criteria.
     *
     * @param context the execution context.
     * @param filterCriteria the filter criteria.
     *
     * @return a list of roles.
     */
	public List getRoles(ExecutionContext context, FilterCriteria filterCriteria);

    /**
     * Returns a new instance of {@link Role} object.
     *
     * @param context the execution context.
     *
     * @return a new instance of role.
     */
	public Role newRole(ExecutionContext context);

    /**
     * Deletes the role by the specified role name.
     *
     * @param context the execution context.
     * @param roleName the role name.
     */
	public void deleteRole(ExecutionContext context, String roleName);

    /**
     * Returns a list of users which does not have the role with the specified role name.
     *
     * @param context the execution context.
     * @param roleName the role name.
     *
     * @return a list of users which does not have the role with the specified role name.
     */
	public List getUsersNotInRole(ExecutionContext context, String roleName);

    /**
     * Returns a list of users which have the role with the specified role name.
     *
     * @param context the execution context.
     * @param roleName the role name.
     *
     * @return a list of users which have the role with the specified role name.
     */
	public List getUsersInRole(ExecutionContext context, String roleName);

    /**
     * Returns a list of {@link Role} objects assigned to the user with the specified username.
     *
     * @param context the execution context.
     * @param userName the username.
     *
     * @return a list of roles assigned to the user with the specified username.
     */
	public List getAssignedRoles(ExecutionContext context, String userName);

    /**
     * Returns a list of {@link Role} objects which are not assigned but can be assigned to the user with the
     * specified username.
     *
     * @param context the execution context.
     * @param userName the username.
     *
     * @return a list of roles which can be assigned to the user with the specified username.
     */
	public List getAvailableRoles(ExecutionContext context, String userName);

    /**
     * Checks if the role with the specified role name exists.
     *
     * @param context the execution context.
     * @param roleName the role name.
     *
     * @return <code>true</code> if the role with the specified role name exists, <code>false</code> otherwise.
     */
	public boolean roleExists(ExecutionContext context, String roleName);

    /**
     * Checks if password expired for the specified username and the number of expiration days.
     *
     * @param context the execution context.
     * @param username the username.
     * @param nDate the number of days for the password to expire from last password change date.
     *
     * @return <code>true</code> if password expired for the specified user and the number expiration days,
     * <code>false</code> otherwise.
     */
	public boolean isPasswordExpired(ExecutionContext context, String username, int nDate);

    /**
     * Updates the last password change time with the current time for the user with the specified username.
     *
     * @param context the execution context.
     * @param username the username.
     */
	public void resetPasswordExpiration(ExecutionContext context, String username);

    /**
     * Updates the {@link User} object with the specified username. New values of user properties are taken from
     * <code>aUser</code> parameter.
     *
     * @param context the execution context.
     * @param userName the username.
     * @param aUser the user object.
     */
    public void updateUser(ExecutionContext context, String userName, User aUser);

    /**
     * Updates the {@link Role} object with the specified role name. New values of role properties are taken from
     * <code>roleDetails</code> parameter.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param roleDetails the role object.
     */
    public void updateRole(ExecutionContext context, String roleName, Role roleDetails);

    /**
     * Returns the tenant identifier of the user with the specified username.
     *
     * @param context the execution context.
     * @param userName the username.
     *
     * @return the tenant identifier of the user with the specified username.
     */
	public String getTenantId(ExecutionContext context, String userName);

    /**
     * Returns a list of {@link User} object for the specified set of tenant identifiers. Also the username or full
     * name of the user should match <code>name</code> parameter.
     *
     * @param context the execution context.
     * @param tenantIds the set of tenant identifier.
     * @param name the name of user.
     *
     * @return a list of users for the specified set of tenant identifiers and with the specified name.
     */
    public List getTenantUsers(ExecutionContext context, Set tenantIds, String name);

    /**
     * The same as
     * {@link #getTenantUsers(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.util.Set, String)}
     * but with additional parameters <code>firstResult</code> and <code>maxResults</code>. This method is used for the
     * functionality with paginated results.
     *
     * @param context the execution context.
     * @param tenantIds the set of tenant identifier.
     * @param name the name of user.
     * @param firstResult the number of the first user.
     * @param maxResults the maximum number of users in the list.
     *
     * @return a list of users for the specified set of tenant identifiers and with the specified name.
     */
    public List getTenantUsers(ExecutionContext context, Set tenantIds, String name, int firstResult, int maxResults);

    /**
     * Returns a list of {@link Role} object for the specified set of tenant identifiers. Also the role name
     * of the role should match <code>name</code> parameter.
     *
     * @param context the execution context.
     * @param tenantIds the set of tenant identifier.
     * @param name the name of role.
     *
     * @return a list of roles for the specified set of tenant identifiers and with the specified name.
     */
    public List getTenantRoles(ExecutionContext context, Set tenantIds, String name);

    /**
     * The same as
     * {@link #getTenantRoles(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.util.Set, String)}
     * but with additional parameters <code>firstResult</code> and <code>maxResults</code>. This method is used for the
     * functionality with paginated results.
     *
     * @param context the execution context.
     * @param tenantIds the set of tenant identifier.
     * @param name the name of role.
     * @param firstResult the number of the first role.
     * @param maxResults the maximum number of roles in the list.
     *
     * @return a list of roles for the specified set of tenant identifiers and with the specified name.
     */
    public List getTenantRoles(ExecutionContext context, Set tenantIds, String name, int firstResult, int maxResults);

    /**
     * Returns count of {@link User} object for the specified set of tenant identifiers. Also the username or full
     * name of the user should match <code>name</code> parameter.
     *
     * @param context the execution context.
     * @param tenantIds the set of tenant identifier.
     * @param name the name of user.
     *
     * @return count of users for the specified set of tenant identifiers and with the specified name.
     */
    public int getTenantUsersCount(ExecutionContext context, Set tenantIds, String name);

    /**
     * Returns count of {@link Role} object for the specified set of tenant identifiers. Also the role name
     * of the role should match <code>name</code> parameter.
     *
     * @param context the execution context.
     * @param tenantIds the set of tenant identifier.
     * @param name the name of role.
     *
     * @return count of roles for the specified set of tenant identifiers and with the specified name.
     */
    public int getTenantRolesCount(ExecutionContext context, Set tenantIds, String name);

    /**
     * Returns a list of visible {@link Role} object for the specified set of tenant identifiers (public roles of root
     * organization are included). Also the role name of the role should match <code>name</code> parameter. This method
     * is used for the functionality with paginated results.
     *
     * @param context the execution context.
     * @param tenantIds the set of tenant identifier.
     * @param name the name of role.
     * @param firstResult the number of the first role.
     * @param maxResults the maximum number of roles in the list.
     *
     * @return a list of visible roles for the specified set of tenant identifiers and with the specified name.
     */
    public List getTenantVisibleRoles(ExecutionContext context, Set tenantIds, String name, int firstResult, int maxResults);

    /**
     * Returns count of visible {@link Role} object for the specified set of tenant identifiers (public roles of root
     * organization are included). Also the role name of the role should match <code>name</code> parameter.
     *
     * @param context the execution context.
     * @param tenantIds the set of tenant identifier.
     * @param name the name of role.
     *
     * @return count of visible roles for the specified set of tenant identifiers and with the specified name.
     */
    public int getTenantVisibleRolesCount(ExecutionContext context, Set tenantIds, String name);

    /**
     * Returns a list of {@link Role} objects which are not in the specified set of user roles and can be assigned to
     * the user with the specified username. Also role name should match role name criteria. This method
     * is used for the functionality with paginated results.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param userRoles the set of user roles which should not be in the result list.
     * @param userName the username.
     * @param firstResult the number of the first role.
     * @param maxResults the maximum number of roles in the list.
     *
     * @return a list of roles which are not in the specified set of user roles and can be assigned to the user with
     * the specified username.
     */
    public List getAvailableRoles(ExecutionContext context, String roleName, Set userRoles, String userName,
                                  int firstResult, int maxResults);

    /**
     * Returns count of {@link Role} objects which are not in the specified set of user roles and can be assigned to
     * the user with the specified username. Also role name should match role name criteria.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param userRoles the set of user roles which should not be in the result list.
     * @param userName the username.
     *
     * @return count of roles which are not in the specified set of user roles and can be assigned to the user with
     * the specified username.
     */
    public int getAvailableRolesCount(ExecutionContext context, String roleName, Set userRoles, String userName);

    /**
     * Returns a list of {@link User} objects which do not have role with the specified role name assigned to them.
     * Also the username or full name of the user should match <code>userName</code> parameter. This method
     * is used for the functionality with paginated results.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param userName the user name.
     * @param firstResult the number of the first user.
     * @param maxResults the maximum number of users in the list.
     *
     * @return a list of users which do not have role with the specified role name assigned to them.
     */
    public List getUsersWithoutRole(ExecutionContext context, String roleName, String userName,
                                    int firstResult, int maxResults);

    /**
     * Returns count of {@link User} objects which do not have role with the specified role name assigned to them.
     * Also the username or full name of the user should match <code>userName</code> parameter.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param userName the user name.
     *
     * @return count of users which do not have role with the specified role name assigned to them.
     */
    public int getUsersCountWithoutRole(ExecutionContext context, String roleName, String userName);

    /**
     * Returns a list of {@link User} objects which have role with the specified role name assigned to them.
     * Also the username or full name of the user should match <code>userName</code> parameter. This method
     * is used for the functionality with paginated results.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param userName the user name.
     * @param firstResult the number of the first user.
     * @param maxResults the maximum number of users in the list.
     *
     * @return a list of users which have role with the specified role name assigned to them.
     */
    public List getUsersWithRole(ExecutionContext context, String roleName, String userName,
                                 int firstResult, int maxResults);

    /**
     * Returns count of {@link User} objects which have role with the specified role name assigned to them.
     * Also the username or full name of the user should match <code>userName</code> parameter.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param userName the user name.
     *
     * @return count of users which have role with the specified role name assigned to them.
     */
    public int getUsersCountWithRole(ExecutionContext context, String roleName, String userName);

    /**
     * Assigns the role with the specified role name to a set of users which usernames are in the specified set.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param userNames the set of usernames.
     */
    public void assignUsers(ExecutionContext context, String roleName, Set userNames);

    /**
     * Unassigns the role with the specified role name from a set of users which usernames are in the specified set.
     *
     * @param context the execution context.
     * @param roleName the role name.
     * @param userNames the set of usernames.
     */
    public void unassignUsers(ExecutionContext context, String roleName, Set userNames);
	
	public List getAvailableRoles(ExecutionContext context, String userName, String text, int firstResult,
            int maxResults);
    public int getAvailableRolesCount(ExecutionContext context, String userName, String text);
    public List getAssignedRoles(ExecutionContext context, String userName, String text, int firstResult,
            int maxResults);
    public int getAssignedRolesCount(ExecutionContext context, String userName, String text);

    public String getAllowedPasswordPattern();
}
