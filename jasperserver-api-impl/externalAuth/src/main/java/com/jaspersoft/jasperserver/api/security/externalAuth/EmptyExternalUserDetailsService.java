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

package com.jaspersoft.jasperserver.api.security.externalAuth;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class a place-holder for a real implementation of ExternalUserDetailsService
 * It returns empty user details list, empty authorities for user name, etc.
 *
 * @author Dmitriy Litvak
 * Extends {@link org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl}
 */
public class EmptyExternalUserDetailsService implements ExternalUserDetailsService {
	public static final Logger logger = LogManager.getLogger(EmptyExternalUserDetailsService.class);

    /**
     * {@link org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl}
     */
	@Override
	public List<GrantedAuthority> loadAuthoritiesByUsername(String username) throws DataAccessException {
		logger.debug("Loading external roles via JDBC.");
		return Collections.emptyList();
	}

	@Override
	public List<Map<String, Object>> loadDetails(String... params) throws DataAccessException {
		return Collections.emptyList();
	}

	/**
	 * Locates the user based on the username. In the actual implementation, the search may possibly be case
	 * insensitive, or case insensitive depending on how the implementaion instance is configured. In this case, the
	 * <code>UserDetails</code> object that comes back may have a username that is of a different case than what was
	 * actually requested..
	 *
	 * @param username the username presented to the {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider}
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
	 *          if the user could not be found or the user has no GrantedAuthority
	 * @throws org.springframework.dao.DataAccessException
	 *          if user could not be found for a repository-specific reason
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		return null;
	}
}
