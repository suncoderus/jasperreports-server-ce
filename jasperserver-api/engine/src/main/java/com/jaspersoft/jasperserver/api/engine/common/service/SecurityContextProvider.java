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
package com.jaspersoft.jasperserver.api.engine.common.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

/**
 * An interface for viewing and modifying user information maintained by Spring Security (formerly Acegi)
 * in the current thread context.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface SecurityContextProvider {

	/**
	 * Get the username value for the user currently in context (this is the same string used to identify the user on login)
	 * @return value of username
	 */
	String getContextUsername();

	/**
	 * Get the User object for the user currently in context
	 * @return
	 */
	User getContextUser();
	
	/**
	 * Set the user in the current thread context to the user identified by name
	 * @param username value of username for an existing user
	 */
	void setAuthenticatedUser(String username);
	
	/**
	 * set the user in the current thread context to the user previously in context (if any)
	 */
	void revertAuthenticatedUser();

    /**
	 * Get a handle of UserAuthorityService which is used to manage {@link User} and {@link com.jaspersoft.jasperserver.api.metadata.user.domain.Role} objects
	 * @return  UserAuthorityService interface
     * @see UserAuthorityService
	 */
    UserAuthorityService getUserAuthorityService();

}
