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

import com.jaspersoft.jasperserver.api.security.internalAuth.InternalAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collection;

/**
 * Authentication object constructed by an internal JRS auth provider
 * Authentication provider returns this object upon successful authentication against
 * an internal database.  This object's {@link com.jaspersoft.jasperserver.api.security.internalAuth.InternalAuthenticationToken} interface
 * signals {@link com.jaspersoft.jasperserver.api.security.externalAuth.BaseAuthenticationProcessingFilter}
 * not to invoke {@link com.jaspersoft.jasperserver.api.security.externalAuth.ExternalDataSynchronizer}
 *
 * User: dlitvak
 * Date: 8/28/12
 */

public class InternalAuthenticationTokenImpl extends UsernamePasswordAuthenticationToken implements InternalAuthenticationToken {
	/**
	 * This constructor can be safely used by any code that wishes to create a
	 * <code>UsernamePasswordAuthenticationToken</code>, as the {@link
	 * #isAuthenticated()} will return <code>false</code>.
	 *
	 */
	public InternalAuthenticationTokenImpl(Object principal, Object credentials) {
		super(principal, credentials);
	}

	/**
	 * This constructor should only be used by <code>AuthenticationManager</code> or <code>AuthenticationProvider</code>
	 * implementations that are satisfied with producing a trusted (ie {@link #isAuthenticated()} = <code>true</code>)
	 * authentication token.
	 *
	 * @param principal
	 * @param credentials
	 * @param authorities
	 */
	public InternalAuthenticationTokenImpl(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}
}
