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

package com.jaspersoft.jasperserver.war.ftpserver;

import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author asokolnikov
 */
public class JSFtpUserManager implements UserManager {

    private AuthenticationManager authenticationManager;

    public User getUserByName(String s) throws FtpException {
        return new JSFtpUser(s, null, null, 0, true, "/");
    }

    public String[] getAllUserNames() throws FtpException {
        throw new UnsupportedOperationException();
    }

    public void delete(String s) throws FtpException {
        throw new UnsupportedOperationException();
    }

    public void save(User user) throws FtpException {
        throw new UnsupportedOperationException();
    }

    public boolean doesExist(String s) throws FtpException {
        throw new UnsupportedOperationException();
    }

    public User authenticate(Authentication authentication) throws AuthenticationFailedException {

        try {
            if (authentication instanceof UsernamePasswordAuthentication) {
                UsernamePasswordAuthentication upa = (UsernamePasswordAuthentication) authentication;
                UsernamePasswordAuthenticationToken authRequest =
                        new UsernamePasswordAuthenticationToken(upa.getUsername(), upa.getPassword());
                org.springframework.security.core.Authentication auth = this.getAuthenticationManager().authenticate(authRequest);

                SecurityContextHolder.getContext().setAuthentication(auth);

                UserDetails ud = (UserDetails) auth.getPrincipal();
                return new JSFtpUser(ud.getUsername(), ud.getPassword(), null, 0, true, "/");
            }

            throw new AuthenticationFailedException("Anonymous authentication is not allowed");

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            throw new AuthenticationFailedException("Authentication failed", ex);
        }
    }

    public String getAdminName() throws FtpException {
        throw new UnsupportedOperationException();
    }

    public boolean isAdmin(String s) throws FtpException {
        return false;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
