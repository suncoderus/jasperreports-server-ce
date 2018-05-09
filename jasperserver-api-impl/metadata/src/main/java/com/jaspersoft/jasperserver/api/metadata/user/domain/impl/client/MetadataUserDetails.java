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
package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User object for security in JasperServer with Spring Security.
 *
 * @author swood
 * @version $Id$
 */
@JasperServerAPI
public class MetadataUserDetails implements UserDetails, User {

    private Set roleSet;
    private String username = null;
    private String password = null;
    private boolean enabled = false;
    private String fullName = null;
    private String emailAddress = null;
    private boolean externallyDefined = false;
    private Authentication originalAuthentication = null;
    private Date previousPasswordChangeTime = null;
    private List attributes = null;
    private String tenantId = null;

    /**
     * Constructor
     *
     * @param u JasperServer User object
     */
    public MetadataUserDetails(User u) {
        super();

        setUsername(u.getUsername());
        String password = u.isExternallyDefined() ? null : u.getPassword();
        setPassword(password);
        setFullName(u.getFullName());
        setEmailAddress(u.getEmailAddress());
        setExternallyDefined(u.isExternallyDefined());
        setEnabled(u.isEnabled());
        setRoles(u.getRoles());
        setAttributes(u.getAttributes());
        setTenantId(u.getTenantId());
    }

    /**
     * Get Spring security GrantedAuthoritys from Jasperserver Roles
     *
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     * @see com.jaspersoft.jasperserver.api.metadata.user.domain.Role
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
            Set currentRoles = getRoles();

            List<GrantedAuthority> authorities = currentRoles == null ? new ArrayList<GrantedAuthority>() : new ArrayList<GrantedAuthority>(currentRoles.size());

            if (currentRoles == null) {
                    return authorities;
            }

            Iterator it = currentRoles.iterator();
            int i = 0;
            while (it.hasNext()) {
                    Role aRole = (Role) it.next();
                    authorities.add(new TenantAwareGrantedAuthority(aRole.getRoleName(), aRole.getTenantId()));
            }
            return authorities;
    }

    /**
     * Get password for user
     *
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    public String getPassword() {
            return password;
    }

    /**
     * Set password for user
     *
     * @param password The password to set.
     */
    public void setPassword(String password) {
            this.password = password;
    }

    /**
     * Get user name
     *
     * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
     */
    public String getUsername() {
            return username;
    }

    /**
     * Set user name
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
            this.username = username;
    }

    /**
     * Get user email address
     *
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {
            return emailAddress;
    }

    /**
     * Set user email address
     *
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
    }

    /**
     * Is the user externally defined? ie. are they defined in a security
     * service external to JasperServer, like LDAP
     *
     * @return externallyDefined
     */
    public boolean isExternallyDefined() {
            return externallyDefined;
    }

    /**
     * @param externallyDefined
     */
    public void setExternallyDefined(boolean externallyDefined) {
            this.externallyDefined = externallyDefined;
    }

    /**
     * Get full name of user
     *
     * @return Returns the fullName.
     */
    public String getFullName() {
            return fullName;
    }

    /**
     * Set full name of user
     *
     * @param fullName The fullName to set.
     */
    public void setFullName(String fullName) {
            this.fullName = fullName;
    }

    /**
     * Is the user account active? True if user is "enabled"
     *
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    public boolean isAccountNonExpired() {
            return enabled;
    }

    /**
     * Can the user login?  True if user is "enabled"
     *
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    public boolean isAccountNonLocked() {
            return enabled;
    }

    /**
     * Is the user not expired?   True if user is "enabled"
     *
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    public boolean isCredentialsNonExpired() {
            return enabled;
    }

    /**
     * Is the user enabled? (active, allowed to login)
     *
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    public boolean isEnabled() {
            return enabled;
    }
    /**
     * Set that user is enabled or disabled.
     *
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
            this.enabled = enabled;
    }

    /**
     * Get JasperServer roles of the user. Collection is not protected - changes
     * will need to be persisted
     *
     * @return roleSet
     */
    public Set getRoles() {
            return roleSet;
    }

    /**
     * Set the set of roles for the user.
     *
     * @param newRoleSet
     */
    public void setRoles(Set newRoleSet) {
            roleSet = newRoleSet;
    }

    /**
     * Get the Spring Security authentication object for this user. Not persisted.
     * This value is available while the user thread is active in the secure
     * application.
     *
     * @return Returns the originalAuthentication.
     */
    public Authentication getOriginalAuthentication() {
            return originalAuthentication;
    }

    /**
     * Set the Spring Security authentication object for this user. Not persisted.
     * This method is performed while the user thread is active in the secure
     * application.
     *
     * @param auth The originalAuthentication to set.
     */
    public void setOriginalAuthentication(Authentication auth) {
            this.originalAuthentication = auth;
    }

    /**
     * Get the current user attributes. This list is not safe.
     *
     * @see com.jaspersoft.jasperserver.api.common.domain.AttributedObject#getAttributes()
     */
    public List getAttributes() {
        return attributes;
    }

    /**
     * Set the user attributes
     *
     * @param attrs new list of attributes
     * @see com.jaspersoft.jasperserver.api.common.domain.AttributedObject#setAttributes(java.util.List)
     */
    public void setAttributes(List attrs) {
        attributes = attrs;
    }

    /**
     * Does nothing. Use {@link #setRoles(java.util.Set)}
     *
     * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#addRole(com.jaspersoft.jasperserver.api.metadata.user.domain.Role)
     */
    public void addRole(Role aRole) {
    }

    /**
     * Does nothing. Use {@link #setRoles(java.util.Set)}
     * 
     * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#removeRole(com.jaspersoft.jasperserver.api.metadata.user.domain.Role)
     */
    public void removeRole(Role aRole) {
    }

    /**
     * Display string
     *
     * @return display string
     */
    @Override
    public String toString() {
            return "MetadataUserDetails: " + getUsername();
    }

    /**
     * Get previous password change date. Can be null.
     *
     * @return date of previous password cahnge
     */
    public Date getPreviousPasswordChangeTime() {
            return previousPasswordChangeTime;
    }

    /**
     * Set previous password change date. Can be null.
     *
     * @param previousPasswordChangeTime
     */
    public void setPreviousPasswordChangeTime(Date previousPasswordChangeTime) {
            this.previousPasswordChangeTime = previousPasswordChangeTime;
    }

    /**
     * Get the tenantId of the user. Can be null.
     *
     * @return tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Set the tenantId of the user. Can be null.
     *
     * @param tid
     */
    public void setTenantId(String tid) {
        tenantId = tid;
    }

    /**
     * True if obj is a TenantQualifiedPrincipal, and user name and tenantId match
     *
     * @see com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.TenantQualifiedPrincipal
     * @param obj to test
     * @return true if equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserImpl) {
        	UserImpl principal = (UserImpl) obj;
            return username.equals(principal.getUsername())
                    && (tenantId == null ? principal.getTenantId() == null
                                    : tenantId.equals(principal.getTenantId()));
        }

        if (obj instanceof TenantQualifiedPrincipal) {
                TenantQualifiedPrincipal principal = (TenantQualifiedPrincipal) obj;
                return username.equals(principal.getName())
                        && (tenantId == null ? principal.getTenantId() == null
                                        : tenantId.equals(principal.getTenantId()));
        }
        
        if (obj instanceof User) {
            return username.equals(((User)obj).getUsername());
        }

        return super.equals(obj);
    }
}
