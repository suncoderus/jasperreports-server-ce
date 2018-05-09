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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author swood
 *
 */
public interface ExternalUserService extends UserAuthorityService {
	/**
	 *
	 * @return
	 * @deprecated deprecated per emerald SSO work
	 */
	@Deprecated
	public User maintainInternalUser();
	public void makeUserLoggedIn(User user);
}
