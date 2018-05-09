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

package com.jaspersoft.jasperserver.api.security.externalAuth.processors;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import org.hibernate.criterion.DetachedCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserAuthorityServiceMock implements UserAuthorityService {

    private Map<String, Role> roles = new HashMap<String, Role>();
    private Map<String,List<User>> usersWithRoles = new HashMap<String, List<User>>();
    private Map<String, User> users = new HashMap<String, User>();


    
    public User getUser(ExecutionContext executionContext, String s) {
        return users.get(s);
    }

    
    public void putUser(ExecutionContext executionContext, User user) {
        users.put(user.getUsername(), user);
    }

    
    public List getUsers(ExecutionContext executionContext, FilterCriteria filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getUsersByCriteria(ExecutionContext executionContext, DetachedCriteria detachedCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getUsersCountExceptExcluded(ExecutionContext executionContext, Set<String> strings, boolean b) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public User newUser(ExecutionContext executionContext) {
        return new UserImpl();
    }

    
    public void deleteUser(ExecutionContext executionContext, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public boolean userExists(ExecutionContext executionContext, String s) {
        return users.containsKey(s);
    }

    
    public boolean disableUser(ExecutionContext executionContext, String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public boolean enableUser(ExecutionContext executionContext, String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public void addRole(ExecutionContext executionContext, User user, Role role) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public void removeRole(ExecutionContext executionContext, User user, Role role) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public void removeAllRoles(ExecutionContext executionContext, User user) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public Role getRole(ExecutionContext executionContext, String s) {
        return roles.get(s);
    }

    
    public void putRole(ExecutionContext executionContext, Role role) {
        roles.put(role.getRoleName(),role);
    }

    
    public List getRoles(ExecutionContext executionContext, FilterCriteria filterCriteria) {
        return new ArrayList(roles.values());
    }

    
    public Role newRole(ExecutionContext executionContext) {
        return new RoleImpl();
    }

    
    public void deleteRole(ExecutionContext executionContext, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getUsersNotInRole(ExecutionContext executionContext, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getUsersInRole(ExecutionContext executionContext, String s) {
        List res = usersWithRoles.get(s);
        return res == null ? new ArrayList() : res;
    }

    
    public List getAssignedRoles(ExecutionContext executionContext, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getAvailableRoles(ExecutionContext executionContext, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public boolean roleExists(ExecutionContext executionContext, String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public boolean isPasswordExpired(ExecutionContext executionContext, String s, int i) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public void resetPasswordExpiration(ExecutionContext executionContext, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public void updateUser(ExecutionContext executionContext, String s, User user) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public void updateRole(ExecutionContext executionContext, String s, Role role) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public String getTenantId(ExecutionContext executionContext, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getTenantUsers(ExecutionContext executionContext, Set set, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getTenantUsers(ExecutionContext executionContext, Set set, String s, int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getTenantRoles(ExecutionContext executionContext, Set set, String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getTenantRoles(ExecutionContext executionContext, Set set, String s, int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getTenantUsersCount(ExecutionContext executionContext, Set set, String s) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getTenantRolesCount(ExecutionContext executionContext, Set set, String s) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getTenantVisibleRoles(ExecutionContext executionContext, Set set, String s, int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getTenantVisibleRolesCount(ExecutionContext executionContext, Set set, String s) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getAvailableRoles(ExecutionContext executionContext, String s, Set set, String s1, int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getAvailableRolesCount(ExecutionContext executionContext, String s, Set set, String s1) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getUsersWithoutRole(ExecutionContext executionContext, String s, String s1, int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getUsersCountWithoutRole(ExecutionContext executionContext, String s, String s1) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getUsersWithRole(ExecutionContext executionContext, String s, String s1, int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getUsersCountWithRole(ExecutionContext executionContext, String s, String s1) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public void assignUsers(ExecutionContext executionContext, String s, Set set) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public void unassignUsers(ExecutionContext executionContext, String s, Set set) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getAvailableRoles(ExecutionContext executionContext, String s, String s1, int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getAvailableRolesCount(ExecutionContext executionContext, String s, String s1) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public List getAssignedRoles(ExecutionContext executionContext, String s, String s1, int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    
    public int getAssignedRolesCount(ExecutionContext executionContext, String s, String s1) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getAllowedPasswordPattern() {
        return null; // TODO: implement.
    }

    public Map<String, Role> getRolesMap() {
        return roles;
    }

    public void setRolesMap(Map<String, Role> roles) {
        this.roles = roles;
    }

    public Map<String, List<User>> getUsersWithRolesMap() {
        return usersWithRoles;
    }

    public void setUsersWithRolesMap(Map<String, List<User>> usersWithRoles) {
        this.usersWithRoles = usersWithRoles;
    }

    public Map<String, User> getUsersMap() {
        return users;
    }

    public void setUsersMap(Map<String, User> users) {
        this.users = users;
    }
}
