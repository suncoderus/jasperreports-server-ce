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
package com.jaspersoft.jasperserver.api.security.externalAuth.cas;

import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalDataSynchronizer;
import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: dlitvak
 * Date: 10/20/12
 */
public class JSCasProcessingFilter extends CasAuthenticationFilter {
	private static final String CAS_TICKET_PARAM_NAME = "ticket";

	private String usernameParameter = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
	private String passwordParameter = UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY;

	@Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException, IOException {
            String password = obtainTicket(request);
		if (password != null && password.trim().length() > 0){
			final String username = CAS_STATEFUL_IDENTIFIER;
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
			authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

			return this.getAuthenticationManager().authenticate(authRequest);
		}
		else {
			String username = obtainUsername(request);
			password = obtainPassword(request);

			UsernamePasswordAuthenticationToken authRequest =
					new UsernamePasswordAuthenticationToken(username != null ? username.trim() : "", password != null ? password : "");

			// Allow subclasses to set the "details" property
			setDetails(request, authRequest);

			return this.getAuthenticationManager().authenticate(authRequest);
		}
	}

	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter(usernameParameter);
	}

    protected String obtainTicket(HttpServletRequest request) {
        return request.getParameter(CAS_TICKET_PARAM_NAME);
    }

	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter(passwordParameter);
	}

	String getUsernameParameter() {
		return usernameParameter;
	}

	/**
	 * Sets the parameter name which will be used to obtain the username from the login request.
	 *
	 * @param usernameParameter the parameter name. Defaults to "j_username".
	 */
	public void setUsernameParameter(String usernameParameter) {
		Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
		this.usernameParameter = usernameParameter;
	}

	String getPasswordParameter() {
		return passwordParameter;
	}

	/**
	 * Sets the parameter name which will be used to obtain the password from the login request..
	 *
	 * @param passwordParameter the parameter name. Defaults to "j_password".
	 */
	public void setPasswordParameter(String passwordParameter) {
		Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
		this.passwordParameter = passwordParameter;
	}

	/**
	 * Provided so that subclasses may configure what is put into the authentication request's details
	 * property.
	 *
	 * @param request that an authentication request is being created for
	 * @param authRequest the authentication request object that should have its details set
	 */
	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}
}
