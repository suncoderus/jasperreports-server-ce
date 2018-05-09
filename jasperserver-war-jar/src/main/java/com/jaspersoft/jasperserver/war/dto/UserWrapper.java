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

import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class UserWrapper extends BaseDTO
{
	private User user;
	private String confirmedPassword;
	private List assignedRoles;
	private List availableRoles;
	private Object selectedAssignedRoles;
	private Object selectedAvailableRoles;


	public List getAssignedRoles()
	{
		return assignedRoles;
	}

	public void setAssignedRoles(List assignedRoles)
	{
		this.assignedRoles = assignedRoles;
	}

	public List getAvailableRoles()
	{
		return availableRoles;
	}

	public void setAvailableRoles(List availableRoles)
	{
		this.availableRoles = availableRoles;
	}

	public Object getSelectedAssignedRoles()
	{
		return selectedAssignedRoles;
	}

	public void setSelectedAssignedRoles(Object selectedAssignedRoles)
	{
		this.selectedAssignedRoles = selectedAssignedRoles;
	}

	public Object getSelectedAvailableRoles()
	{
		return selectedAvailableRoles;
	}

	public void setSelectedAvailableRoles(Object selectedAvailableRoles)
	{
		this.selectedAvailableRoles = selectedAvailableRoles;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
		
		setConfirmedPassword(this.user == null ? null : this.user.getPassword());
	}

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}
}
