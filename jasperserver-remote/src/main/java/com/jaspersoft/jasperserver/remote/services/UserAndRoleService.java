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
package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.remote.common.RoleSearchCriteria;
import com.jaspersoft.jasperserver.remote.common.UserSearchCriteria;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;

import java.util.List;

/**
 * @author Volodya Sabadosh (vsabadosh@jaspersoft.com)
 * @version $Id $
 */
public interface UserAndRoleService {

    public List<User> findUsers(UserSearchCriteria criteria) throws RemoteException;

    public User putUser(User user) throws RemoteException;

    public void deleteUser(User user) throws RemoteException;

    public List<Role> findRoles(RoleSearchCriteria criteria) throws RemoteException;

    public Role putRole(Role role) throws RemoteException;

    public Role updateRoleName(Role oldRole, String newName) throws RemoteException;

    public void deleteRole(Role role) throws RemoteException;

}
