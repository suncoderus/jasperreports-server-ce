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
package com.jaspersoft.jasperserver.war.validation;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.dto.RoleWrapper;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class RoleValidator implements Validator
{
	private UserAuthorityService userService;

	public UserAuthorityService getUserService()
	{
		return userService;
	}

	public void setUserService(UserAuthorityService userService)
	{
		this.userService = userService;
	}

	public boolean supports(Class klass)
	{
		return RoleWrapper.class.isAssignableFrom(klass);
	}

	public void validate(Object object, Errors errors)
	{

	}


	public void validateName(RoleWrapper wrapper, Errors errors)
	{
		Role role = wrapper.getRole();
		if (role.getRoleName() == null || role.getRoleName().length() == 0) {
			errors
					.rejectValue("role.roleName",
							"RoleValidator.error.not.empty");
		} 
		else {
			if (!JasperServerUtil.regExValidateName(role.getRoleName())) {
				errors.rejectValue("role.roleName",
						"RoleValidator.error.invalid.chars");
			}
			if (wrapper.isNewMode()
					&& userService.roleExists(null, role.getRoleName())) {
				errors.rejectValue("role.roleName",
						"RoleValidator.error.duplicate");
			}
		}
		
	}
}
