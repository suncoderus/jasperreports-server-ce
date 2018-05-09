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
package com.jaspersoft.jasperserver.api.engine.common.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.memory.UserAttribute;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class AcegiSecurityContextProvider implements SecurityContextProvider {
	
	private static final Log log = LogFactory.getLog(AcegiSecurityContextProvider.class); 
	
	private UserDetailsService userDetailsService;
	private UserAuthorityService userAuthorityService;
    private UserAttribute anonymousUserDescriptor;

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public UserAuthorityService getUserAuthorityService() {
		return userAuthorityService;
	}

	public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
		this.userAuthorityService = userAuthorityService;
	}

    public UserAttribute getAnonymousUserDescriptor() {
        return anonymousUserDescriptor;
    }

    public void setAnonymousUserDescriptor(UserAttribute anonymousUserDescriptor) {
        this.anonymousUserDescriptor = anonymousUserDescriptor;
    }


    public String getContextUsername() {
		Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
		if (authenticationToken == null) {
			return null;
		}
		
		if (authenticationToken.getPrincipal() instanceof UserDetails) {
			UserDetails contextUserDetails = (UserDetails) authenticationToken.getPrincipal();
			return contextUserDetails.getUsername();
		} else if (authenticationToken.getPrincipal() instanceof String) {
			return (String) authenticationToken.getPrincipal();
		} else {
			return null;
		}
	}
	
	public User getContextUser() {
		String username = getContextUsername();
		if (username == null) {
			return null;
		}

        User user = getUserAuthorityService().getUser(null, username);//TODO context
        if (user == null){
            user = createAnonymousUser();
        }

		return user;
	}

	public void setAuthenticatedUser(String username) {
		UserDetails userDetails = getUserDetailsService().loadUserByUsername(username);
		String quotedUsername = "\"" + username + "\"";
		if (userDetails == null) {
			throw new JSException("jsexception.user.not.found", new Object[] {quotedUsername});
		}
		
		if (!userDetails.isAccountNonExpired()) {
			throw new JSException("jsexception.user.expired", new Object[] {quotedUsername});
		}
		
		if (!userDetails.isAccountNonLocked()) {
			throw new JSException("jsexception.user.locked", new Object[] {quotedUsername});
		}
		
		if (!userDetails.isCredentialsNonExpired()) {
			throw new JSException("jsexception.user.credentials.are.expired", new Object[] {quotedUsername});
		}
		
		if (!userDetails.isEnabled()) {
			throw new JSException("jsexception.user.disabled", new Object[] {quotedUsername});
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Setting user " + username + " as authenticated");
		}
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	public void revertAuthenticatedUser() {
		// TODO revert to previous principal
		SecurityContextHolder.getContext().setAuthentication(null);
	}

    protected User createAnonymousUser() {
        User user = new UserImpl();

        user.setUsername(anonymousUserDescriptor.getPassword());

        Set<Role> roles = new HashSet<Role>();
        for (GrantedAuthority authority : anonymousUserDescriptor.getAuthorities()){
            Role role = new RoleImpl();
            role.setRoleName(authority.getAuthority());
            roles.add(role);
        }
        user.setRoles(roles);

        return user;
    }

}
