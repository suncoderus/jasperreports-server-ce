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

import com.jaspersoft.jasperserver.api.security.externalAuth.db.ExternalJDBCUserDetailsService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/**
 * User: dlitvak
 * Date: 10/19/12
 */
public class CasJDBCUserDetailsService extends ExternalJDBCUserDetailsService {
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		try {
			return super.loadUserByUsername(username);
		} catch (UsernameNotFoundException unfe) {
			logger.warn("Could not find " + username + " in external DB.");  //To change body of catch statement use File | Settings | File Templates.
		}
		return createUserDetails(username, new User(username, "", true, true, true, true, new ArrayList<GrantedAuthority>()), new ArrayList<GrantedAuthority>());
	}
}
