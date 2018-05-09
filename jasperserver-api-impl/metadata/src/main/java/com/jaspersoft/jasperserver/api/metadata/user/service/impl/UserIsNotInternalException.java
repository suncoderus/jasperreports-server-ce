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

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * This exception is thrown by the InternalDaoAuthenticationProvider when the authenticated user is not internal.
 *
 * User: dlitvak
 * Date: 8/3/12
 */
public class UserIsNotInternalException extends UsernameNotFoundException {
	public UserIsNotInternalException(String msg) {
		super(msg);
	}

	public UserIsNotInternalException(String msg, Object extraInformation) {
		super(msg, extraInformation);
	}

	public UserIsNotInternalException(String msg, Throwable t) {
		super(msg, t);
	}
}
