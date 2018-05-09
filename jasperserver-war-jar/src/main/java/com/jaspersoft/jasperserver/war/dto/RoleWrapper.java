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
package com.jaspersoft.jasperserver.war.dto;

import java.util.Set;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class RoleWrapper extends BaseDTO
{
	private Role role;
	private List usersNotInRole;
	private List usersInRole;
	private Object selectedUsersNotInRole;
	private Object selectedUsersInRole;


	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public List getUsersNotInRole()
	{
		return usersNotInRole;
	}

	public void setUsersNotInRole(List usersNotInRole)
	{
		this.usersNotInRole = usersNotInRole;
	}

	public List getUsersInRole()
	{
		return usersInRole;
	}

	public void setUsersInRole(List usersInRole)
	{
		this.usersInRole = usersInRole;
	}

	public Object getSelectedUsersInRole()
	{
		return selectedUsersInRole;
	}

	public void setSelectedUsersInRole(Object selectedUsersInRole)
	{
		this.selectedUsersInRole = selectedUsersInRole;
	}

	public Object getSelectedUsersNotInRole()
	{
		return selectedUsersNotInRole;
	}

	public void setSelectedUsersNotInRole(Object selectedUsersNotInRole)
	{
		this.selectedUsersNotInRole = selectedUsersNotInRole;
	}
}
