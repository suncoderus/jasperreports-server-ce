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
package com.jaspersoft.jasperserver.api.security.internalAuth;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserIsNotInternalException;
import com.jaspersoft.jasperserver.api.security.SystemLoggedInUserStorage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *  InternalDaoAuthenticationProvider overrides successful authentication token creation to distinguish
 *  our internal authentication against jaspersoft database versus external authentication against any other
 *  source.
 *
 * User: dlitvak
 * Date: 11/14/12
 */
public class InternalDaoAuthenticationProvider extends DaoAuthenticationProvider {
	SystemLoggedInUserStorage systemLoggedInUserStorage;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
												  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (!(userDetails instanceof MetadataUserDetails))
			throw new UserIsNotInternalException("User needs to be internal to be authenticated by " + this.getClass());
		if (((MetadataUserDetails)userDetails).isExternallyDefined()) {
			User sysUser = loadUserFromSystemStorageByAuth(authentication);
			if (sysUser == null) {
				throw new UserIsNotInternalException("User needs to be internal to be authenticated by " + this.getClass());
			} else {
				((MetadataUserDetails) userDetails).setPassword(sysUser.getPassword());
			}
		}
		super.additionalAuthenticationChecks(userDetails, authentication);
	}

	/**
	 * Create InternalAuthenticationToken after successful authentication against JRS database
	 *
	 * @param principal that should be the principal in the returned object (defined by the {@link
	 *        #isForcePrincipalAsString()} method)
	 * @param authentication that was presented to the provider for validation
	 * @param user that was loaded by the implementation
	 *
	 * @return the successful authentication token
	 */
	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
														 UserDetails user) {
		InternalAuthenticationTokenImpl result = new InternalAuthenticationTokenImpl(principal,
				authentication.getCredentials(), user.getAuthorities());
		result.setDetails(authentication.getDetails());

		return result;
	}

	private User loadUserFromSystemStorageByAuth(UsernamePasswordAuthenticationToken authentication) {
		User sysUser = null;
		if (authentication.getPrincipal() instanceof String && authentication.getCredentials() instanceof String) {
			String userName = (String)authentication.getPrincipal();
			String password = (String)authentication.getCredentials();
			sysUser = systemLoggedInUserStorage != null ? systemLoggedInUserStorage.loadUserByNameAndPassword(userName, password) : null;
		}
		return sysUser;
	}

	public void setSystemLoggedInUserStorage(SystemLoggedInUserStorage systemLoggedInUserStorage) {
		this.systemLoggedInUserStorage = systemLoggedInUserStorage;
	}
}
