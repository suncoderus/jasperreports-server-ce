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

package com.jaspersoft.jasperserver.api.security.externalAuth.sso;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collection;

/**
 * SSO authentication token constructed and validated by SSO framework.
 *
 * @author Chaim Arbiv
 */
@JasperServerAPI
public class SsoAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private Object ticket;

	/**
	 * This constructor can be safely used by any code that wishes to create a
	 * <code>UsernamePasswordAuthenticationToken</code>, as the {@link
	 * #isAuthenticated()} will return <code>false</code>.
	 *
	 */
    public SsoAuthenticationToken(Object ticket, Object principal, Object credentials) {
		super(principal, credentials);
        this.ticket = ticket;
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
	public SsoAuthenticationToken(Object ticket, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
		this.ticket = ticket;
	}

	/**
	 *
	 * @return SSO token/ticket
	 */
	public Object getTicket() {
		return ticket;
	}
}
