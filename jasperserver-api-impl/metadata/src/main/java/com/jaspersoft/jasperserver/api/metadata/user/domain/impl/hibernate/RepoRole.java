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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.UserAuthorityPersistenceService;

/**
 * @author swood
 * @version $Id$
 *
 * @hibernate.class table="Role"
 */
public class RepoRole implements Role, IdedObject {

	private long id;
	private String roleName;
	private boolean externallyDefined = false;
	private Set<User> users = new HashSet<User>();
    private List attributes = null;
    private RepoTenant tenant;

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
	 * 		column="rolename" type="string" length="100" not-null="true" unique="true"
	 *
     * (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.Role#getRoleName()
	 */
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String newRoleName) {
//		if (newRoleName == null || newRoleName.trim().length() == 0) {
//			throw new RuntimeException("No role name");
//		}
		roleName = newRoleName;

	}

	/**
	 * @hibernate.property
	 * 		column="externallyDefined" type="boolean"
	 *
	 * @return Returns the externallyDefined.
	 */
	public boolean isExternallyDefined() {
		return externallyDefined;
	}

	/**
	 * @param externallyDefined The externallyDefined to set.
	 */
	public void setExternallyDefined(boolean externallyDefined) {
		this.externallyDefined = externallyDefined;
	}

	public void addUser(User aUser)
	{
		// doesn't need implementing
	}

	public void removeUser(User aUser)
	{
		// doesn't need implementing
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


	public void copyFromClient(Object obj, PersistentObjectResolver resolver) {
		if (!(resolver instanceof UserAuthorityPersistenceService)) {
			throw new IllegalArgumentException(
					"This method requires an UserAuthorityPersistenceService resolver");
		}
		copyFromClient(obj, (UserAuthorityPersistenceService) resolver);
	}
	
	public void copyFromClient(Object obj, UserAuthorityPersistenceService resolver) {
		Role r = (Role) obj;
		// r -> this
		setRoleName(r.getRoleName());
		setExternallyDefined(r.isExternallyDefined());
		
        String tenantId = (r.getTenantId() == null) ? TenantService.ORGANIZATIONS : r.getTenantId();
		RepoTenant pTenant = resolver.getPersistentTenant(tenantId, true);
		setTenant(pTenant);
		
//		Set users = r.getUsers();
//		Set repoUsers = getUsers();
//		repoUsers.clear();
//		for (Iterator it = users.iterator(); it.hasNext();) {
//			RepoUser user = (RepoUser) resolver.getPersistentObject(it.next());
//			repoUsers.add(user);
//		}
	}

	public Object toClient(ResourceFactory clientMappingFactory) {

		Role r = (Role) clientMappingFactory.newObject(Role.class);
		// this -> r
		r.setRoleName(getRoleName());
		r.setExternallyDefined(isExternallyDefined());
        if (TenantService.ORGANIZATIONS.equals(getTenantId())) {
            r.setTenantId(null);
        } else {
            r.setTenantId(getTenantId());
        }
		return r;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("roleId", getId())
			.append("roleName", getRoleName())
			.toString();
	}

    public boolean equals(Object other) {
        if ( !(other instanceof RepoRole) ) return false;
        RepoRole castOther = (RepoRole) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
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
		throw new UnsupportedOperationException("Cannot set tenant ID on persistent role");
	}

}
