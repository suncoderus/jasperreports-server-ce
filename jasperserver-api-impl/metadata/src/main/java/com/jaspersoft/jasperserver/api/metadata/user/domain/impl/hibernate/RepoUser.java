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
package com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoReportThumbnail;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.TenantAwareGrantedAuthority;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityPersistenceService;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

/**
 * @author swood
 * @version $Id$
 *
 * @hibernate.class table="JSUser"
 */
public class RepoUser implements User, IdedObject {
	
	private Set roleSet = new HashSet();
	private long id;
	private String username = null;
	private String fullName = null;
	private String password = null;
	private String emailAddress = null;
	private boolean externallyDefined = false;
	private boolean enabled = false;
	private Date previousPasswordChangeTime = null;
    private List attributes = null;
    private RepoTenant tenant = null;
    protected Set<RepoReportThumbnail> thumbnails = null;

	/**
	 * @return
	 * @hibernate.id type="long" column="id" generator-class="identity"
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @hibernate.property
	 * 		column="username" type="string" length="100" not-null="true" unique="true"
	 *
	 * @return Returns the username.
	 * 
	 * (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#getUsername()
	 */
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String newUsername) {
		if (newUsername == null || newUsername.trim().length() == 0) {
			throw new RuntimeException("No user name");
		}
		username = newUsername;
	}

	/**
	 * @hibernate.property
	 * 		column="fullname" type="string" length="100" not-null="true"
	 * 
	 * @return Returns the fullName.
	 * 
	 * (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#getFullName()
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName The fullName to set.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @hibernate.property
	 * 		column="emailAddress" type="string" length="100"
	 *
	 * @return Returns the emailAddress.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress The emailAddress to set.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 *   
     * @hibernate.set  table="UserRole" inverse="false" lazy="false"
     * 
     * @hibernate.key column="userId"
     * 
     * @hibernate.many-to-many  column="roleId" class="com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole"
	 * 
	 * @return Set
	 */
	public Set getRoles() {
		return roleSet;
	}
	
	public void setRoles(Set newRoleSet) {
		roleSet = newRoleSet;
	}

	public void addRole(final Role newRole) {
		if (! (newRole instanceof RepoRole)) {
			throw new IllegalArgumentException("can only add RepoRoles to a RepoUser");
		}
/*		Predicate findRolePredicate = new Predicate() {
			public boolean evaluate(Object o) {
				Role r = (Role) o;
				if (r == null || newRole == null || r.getRoleName() == null || newRole.getRoleName() == null) {
					return false;
				}
				return r.getRoleName().equalsIgnoreCase(newRole.getRoleName());
			}
		};
		Object found = CollectionUtils.find(getRoles(), findRolePredicate);
		if (found == null) {
*/		
		if (newRole != null && !getRoles().contains(newRole)) {
			getRoles().add(newRole);
			//newRole.getUsers().add(this);
		}
	}

	public void removeRole(final Role removedRole) {
		getRoles().remove(removedRole);
		//removedRole.getUsers().remove(this);
	}
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.common.domain.AttributedObject#getAttributes()
	 */
	public List getAttributes() {
		return attributes;
	}

        public void setAttributes(List attrs) {
           	attributes = attrs;
        }

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#getAuthorities()
	 */
	public GrantedAuthority[] getAuthorities() {
		Set currentRoles = getRoles();
		
		GrantedAuthority[] authorities = currentRoles == null ? new GrantedAuthority[0] : new GrantedAuthority[currentRoles.size()];
		
		if (currentRoles == null) {
			return authorities;
		}
		
		Iterator it = currentRoles.iterator();
		int i = 0;
		while (it.hasNext()) {
			Role aRole = (Role) it.next();
			authorities[i++] = new TenantAwareGrantedAuthority(aRole);
		}
		return authorities;
	}

	/**
	 * @hibernate.property
	 * 		column="password" type="string" length="250"
	 * 
	 * (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
	 */
	public boolean isAccountNonExpired() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isAccountNonLocked()
	 */
	public boolean isAccountNonLocked() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	public boolean isCredentialsNonExpired() {
		return enabled;
	}

	/** @hibernate.property
	 * 		column="externallyDefined" type="boolean"
	 * 
	 * (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#isExternallyDefined()
	 */
	public boolean isExternallyDefined() {
		return externallyDefined;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.User#setExternallyDefined(boolean)
	 */
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}

	/** @hibernate.property
	 * 		column="enabled" type="boolean"
	 * 
     *  (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled The enabled to set.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void copyFromClient(Object obj, PersistentObjectResolver resolver) {
		if (!(resolver instanceof UserAuthorityPersistenceService)) {
			throw new IllegalArgumentException(
					"This method requires an UserAuthorityPersistenceService resolver");
		}
		copyFromClient(obj, (UserAuthorityPersistenceService) resolver);
	}
	
	public void copyFromClient(Object obj, UserAuthorityPersistenceService resolver) {
		User u = (User) obj;
		
		// u -> this
		setUsername(u.getUsername());
		setPassword(PasswordCipherer.getInstance().encodePassword(u.getPassword()));
		setFullName(u.getFullName());
		setEmailAddress(u.getEmailAddress());
		setExternallyDefined(u.isExternallyDefined());
		setEnabled(u.isEnabled());
		setPreviousPasswordChangeTime(u.getPreviousPasswordChangeTime());
		
        String tenantId = (u.getTenantId() == null) ? TenantService.ORGANIZATIONS : u.getTenantId();
		RepoTenant pTenant = resolver.getPersistentTenant(tenantId, true);
		setTenant(pTenant);

		// The u is going to have Role DTOs - need to convert them
		// to RepoRoles
		Set clientRoles = u.getRoles();
		Set repoRoles = null;
		if (clientRoles != null) {
			repoRoles = new HashSet(clientRoles.size());
			if (clientRoles.size() > 0) {
				for (Iterator it = clientRoles.iterator(); it.hasNext(); ) {
					Role clientRole = (Role) it.next();
					RepoRole r = (RepoRole) resolver.getPersistentObject(clientRole);
					if (r == null) {
						r = new RepoRole();
					}
					r.copyFromClient(clientRole, resolver);
					repoRoles.add(r);
				}
			}
		}
		setRoles(repoRoles);
	}
	
	public Object toClient(ResourceFactory clientMappingFactory) {
		User u = (User) clientMappingFactory.newObject(User.class);
		// this -> u
		u.setUsername(getUsername());
		u.setPassword(PasswordCipherer.getInstance().decodePassword(getPassword()));
		u.setFullName(getFullName());
		u.setEmailAddress(getEmailAddress());
		u.setExternallyDefined(isExternallyDefined());
		u.setEnabled(isEnabled());
		u.setPreviousPasswordChangeTime(getPreviousPasswordChangeTime());
        if (TenantService.ORGANIZATIONS.equals(getTenantId())) {
            u.setTenantId(null);
        } else {
            u.setTenantId(getTenantId());
        }

		Set repoRoles = getRoles();
		Set clientRoles = null;
		if (repoRoles != null) {
			clientRoles = new HashSet(repoRoles.size());
			if (repoRoles.size() > 0) {
				for (Iterator it = repoRoles.iterator(); it.hasNext(); ) {
					RepoRole repoRole = (RepoRole) it.next();
					Role r = (Role) repoRole.toClient(clientMappingFactory);
					clientRoles.add(r);
				}
			}
		}
		u.setRoles(clientRoles);
		return u;
		
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append("userId", getId())
			.append("username", getUsername())
			.toString();
	}

    public boolean equals(Object other) {
        if ( !(other instanceof RepoUser) ) return false;
        RepoUser castOther = (RepoUser) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }

	public Date getPreviousPasswordChangeTime() {
		return previousPasswordChangeTime;
	}

	public void setPreviousPasswordChangeTime(Date previousPasswordChangeTime) {
		this.previousPasswordChangeTime = previousPasswordChangeTime;
	}
	
	public RepoTenant getTenant() {
		return tenant;
	}

	public void setTenant(RepoTenant tenant) {
		this.tenant = tenant;
	}

	public String getTenantId() {
		return tenant == null ? null : tenant.getTenantId();
	}

	public void setTenantId(String tenantId) {
		throw new UnsupportedOperationException("Cannot set tenant ID on persistent user");
	}

    public Set<RepoReportThumbnail> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Set<RepoReportThumbnail> thumbnails) {
        this.thumbnails = thumbnails;
    }
}
