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

import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.TenantAwareGrantedAuthority;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author bklawans from code written by swood
 * @version $Id$
 */
public class UserDetailsServiceImpl implements UserDetailsService {

	protected static final Log log = LogFactory.getLog(UserDetailsServiceImpl.class);

	private List defaultInternalRoles;
	private List defaultAdminRoles;
	private List adminUsers;

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		if (adminUsers.contains(username)) {
		    log.debug("User " + username + " is an admin");
		    return detailsFromList(defaultAdminRoles, username);
		}
		
		log.debug("User " + username + " is not an admin, getting default authorities");
		return detailsFromList(defaultInternalRoles, username);
	}

	public void setDefaultInternalRoles(List defaultInternalRoles) {
		this.defaultInternalRoles = defaultInternalRoles;
	}
	
    public void setDefaultAdminRoles(List defaultAdminRoles) {
        this.defaultAdminRoles = defaultAdminRoles;
    }
    
    public void setAdminUsers(List adminUsers) {
        this.adminUsers = adminUsers;
    }
    
    public List getDefaultInternalRoles() {
        return defaultInternalRoles;
    }
    
    public List getDefaultAdminRoles() {
        return defaultAdminRoles;
    }
    
    public List getAdminUsers() {
        return adminUsers;
    }
    
    private UserDetails detailsFromList(List roles, String username) {

        Collection<GrantedAuthority> authorities = roles == null ? new ArrayList<GrantedAuthority>(0) : new ArrayList<GrantedAuthority>(roles.size());
		
		if (roles == null) {
			return new UserDetailsImpl(authorities, username);
		}
		
		Iterator it = roles.iterator();
		int i = 0;
		while (it.hasNext()) {
			authorities.add(new TenantAwareGrantedAuthority((String) it.next()));
		}
		
		return new UserDetailsImpl(authorities, username);
       
    }
    
    public static class UserDetailsImpl implements UserDetails {
        private Collection<? extends GrantedAuthority> authorities;
        private String username;
        
        public UserDetailsImpl(Collection<? extends GrantedAuthority> authorities, String username) {
            this.authorities = authorities;
            this.username = username;
        }
        
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }
        
        public String getPassword() {
            return null;
        }
        
        public String getUsername() {
            return username;
        }
        
        public boolean isAccountNonExpired() {
            return true;
        }
        
        public boolean isAccountNonLocked() {
            return true;
        }
        
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        public boolean isEnabled() {
            return true;
        }
    }
}
