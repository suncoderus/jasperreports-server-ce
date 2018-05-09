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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;

/**
 * @author swood
 *
 * @hibernate.class table="ObjectPermission"
 */
public class RepoObjectPermission implements ObjectPermission, IdedObject {

	private long id;
	private String uri;
	private Object permissionRecipient;
	private int permissionMask;
	
	/**
	 * @return
	 * @hibernate.id generator-class="identity"
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @hibernate.property column="uri" type="string" not-null="true" length="255"
	 * 
	 *  (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#getURI()
	 */
	public String getURI() {
		return uri;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#setObjectIdentity(java.lang.String)
	 */
	public void setURI(String URI) {
		this.uri = URI;
	}
	
	/**
	 * This is a User or a Role
	 * 
	 * @hibernate.any id-type="long"
	 * 
	 * @hibernate.any-column name="recipientobjectclass" length="100"
	 * @hibernate.any-column name="recipientobjectid"
	 * 
	 * hibernate.meta-value class="RepoRole" value="RepoRole"
	 * hibernate.meta-value class="RepoUser" value="RepoUser"
	 */

	public Object getPermissionRecipient() {
		return permissionRecipient;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission#setPermissionRecipient(java.lang.Object)
	 */
	public void setPermissionRecipient(Object permissionRecipient) {
		this.permissionRecipient = permissionRecipient;
	}

	/**
	 * 
	 * @hibernate.property
	 * 		column="permissionMask" type="integer" not-null="true"
	 * 
	 * @return
	 */
	public int getPermissionMask() {
		return permissionMask;
	}

	public void setPermissionMask(int permissionMask) {
		this.permissionMask = permissionMask;
	}
	
	/**
	 * Copy from a client object into this one
	 * 
	 * @param objIdent
	 * @param resolver
	 */
	public void copyFromClient(Object obj, PersistentObjectResolver resolver) {
		// objIdent -> this
		ObjectPermission objPermission = (ObjectPermission) obj;
		setURI(objPermission.getURI());
		if (objPermission.getPermissionRecipient() != null) {
			setPermissionRecipient(resolver.getPersistentObject(objPermission.getPermissionRecipient()));
		} else {
			setPermissionRecipient(null);
		}
		setPermissionMask(objPermission.getPermissionMask());
	}
	
	/**
	 * Copy from this into a new client object
	 * 
	 * @param clientMappingFactory
	 */
	public Object toClient(ResourceFactory clientMappingFactory) {
		// this -> objIdent
		
		ObjectPermission objPermission = (ObjectPermission) clientMappingFactory.newObject(ObjectPermission.class);

		objPermission.setURI(getURI());
		
		if (getPermissionRecipient() != null) {
			IdedObject recipient = (IdedObject) getPermissionRecipient();
			Object clientPermissionRecipient = recipient.toClient(clientMappingFactory);
			objPermission.setPermissionRecipient(clientPermissionRecipient);
		} else {
			objPermission.setPermissionRecipient(null);
		}
		objPermission.setPermissionMask(getPermissionMask());
		return objPermission;
	}
	
	public String toString() {
		return new ToStringBuilder(this)
			.append("objectPermissionId", getId())
			.append("objectIdentity", getURI())
			.append("recipient", getPermissionRecipient())
			.toString();
	}

    public boolean equals(Object other) {
        if ( !(other instanceof RepoObjectPermission) ) return false;
        RepoObjectPermission castOther = (RepoObjectPermission) other;
        return new EqualsBuilder()
            .append(this.getId(), castOther.getId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }

}
