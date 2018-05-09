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

import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalUserDetails;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalUserDetailsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.List;

/**
 * SSO authentication provider uses ticketValidator to validate an SSO token.
 *
 * User: dlitvak
 * Date: 8/16/12
 */
public class SsoAuthenticationProvider implements AuthenticationProvider, InitializingBean {
	private final static Logger logger = LogManager.getLogger(SsoAuthenticationProvider.class);

	private SsoTicketValidator ticketValidator;
	private ExternalUserDetailsService externalUserDetailsService;

	/**
	 * Method called by Spring's ProviderManager to initiate authentication.
	 *
	 * @param authentication
	 * @return
	 * @throws AuthenticationException if SSO token is not validated.
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final Object ssoToken = ((SsoAuthenticationToken) authentication).getTicket();
		logger.debug("Calling ticketValidator to authenticate token " + ssoToken);
		ExternalUserDetails userDetails = ticketValidator.validate(ssoToken);

		List<GrantedAuthority> authorities = externalUserDetailsService.loadAuthoritiesByUsername(userDetails.getUsername());
		return createSuccessAuthentication(authentication, userDetails, authorities);
	}

	/**
	 * Creates a successful {@link Authentication} object.<p>Protected so subclasses can override.</p>
	 *
	 * @param authentication that was presented to the provider for validation
	 * @param userDetails that were parsed from SSO server response to ticket validation request.
	 * @param authorities that were loaded from externalUserDetailsService
	 *
	 * @return the successful authentication token
	 */
	protected Authentication createSuccessAuthentication(Authentication authentication, ExternalUserDetails userDetails, List<GrantedAuthority> authorities) {
		SsoAuthenticationToken ssoAuthenticationToken = new SsoAuthenticationToken(null, userDetails, null, authorities);
		ssoAuthenticationToken.setDetails(authentication.getDetails());

		return ssoAuthenticationToken;
	}

	/**
	 * @param authentication
	 * @return true if the provider supports certain class of {@link Authentication}
	 */
	@Override
	public boolean supports(Class authentication) {
		final boolean supportsSsoAuthToken = SsoAuthenticationToken.class.isAssignableFrom(authentication);
		logger.debug("Provider " + (supportsSsoAuthToken ? "supports" : "does not support") + " authentication with " + authentication.getName());

		if (supportsSsoAuthToken) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(ticketValidator, "ticketValidator must not be null in SsoAuthenticationProvider.");
	}

	/**
	 * ticketValidator injected via Spring config.
	 * @param ticketValidator
	 */
	public void setTicketValidator(SsoTicketValidator ticketValidator) {
		this.ticketValidator = ticketValidator;
	}

	public ExternalUserDetailsService getExternalUserDetailsService() {
		return externalUserDetailsService;
	}

	/**
	 * externalAuthProperties injected via Spring config.
	 * @return
	 */
	public void setExternalUserDetailsService(ExternalUserDetailsService externalUserDetailsService) {
		this.externalUserDetailsService = externalUserDetailsService;
	}
}
