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

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;

import java.util.List;

/**
 * @author asokolnikov
 */
public class JSFtpUser implements User {

    private String name;
    private String password;
    private List<Authority> authorities;
    private int maxIdleTime;
    private boolean enabled;
    private String homeDirectory;

    public JSFtpUser(String name, String password, List<Authority> authorities,
            int maxIdleTime,  boolean enabled, String homeDirectory) {
        this.name = name;
        this.password = password;
        this.authorities = authorities;
        this.maxIdleTime = maxIdleTime;
        this.enabled = enabled;
        this.homeDirectory = homeDirectory;
    }


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public List<Authority> getAuthorities(Class<? extends Authority> aClass) {
        return null;
    }

    public AuthorizationRequest authorize(AuthorizationRequest authorizationRequest) {
        return authorizationRequest;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }
}
