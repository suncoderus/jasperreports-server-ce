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
package com.jaspersoft.jasperserver.api.security.externalAuth.wrappers.spring.ldap;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

/**
 * Wrapper class for org.springframework.security.ldap.authentication.LdapAuthenticationProvider
 * @author dlitvak
 * @version $Id$
 * @since 6.0
 */
@JasperServerAPI
public class JSLdapAuthenticationProvider extends LdapAuthenticationProvider {
    private static final Logger logger = LogManager.getLogger(JSLdapAuthenticationProvider.class);

	public JSLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
		super(authenticator, authoritiesPopulator);
	}

	public JSLdapAuthenticationProvider(LdapAuthenticator authenticator) {
		super(authenticator);
	}

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            return super.authenticate(authentication);
        }
        catch (IncorrectResultSizeDataAccessException irsdae) {
            logger.error(irsdae.getMessage());
            throw new BadCredentialsException("Found more than 1 user for the supplied credentials!", irsdae);
        }
        catch (AuthenticationException e) {
            logger.warn(e.getMessage());
            throw e;
        }
    }
}
