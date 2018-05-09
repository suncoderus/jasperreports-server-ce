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

import com.jaspersoft.jasperserver.api.common.crypto.PasswordCipherer;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.regex.Pattern;

/**
 * @author sbirney
 * @hibernate.class table="ProfileAttribute"
 */
public class RepoProfileAttribute implements IdedObject {

    private long id;
    private String attrName;
    private String attrValue;
    private Object principal;
    private String description;
    private String owner;

    protected static final Pattern PATTERN_RESOURCE_NAME_REPLACE = Pattern.compile("[/\\\\]");

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
     * @hibernate.property column="attrName" type="string" not-null="true" length="255"
     */
    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String s) {
        this.attrName = s;
    }

    /**
     * @hibernate.property column="attrValue" type="string" not-null="true" length="255"
     */
    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String s) {
        this.attrValue = s;
    }

    /**
     * This is a User, Tenant or a Role
     *
     * @hibernate.any id-type="long"
     * @hibernate.any-column name="principalobjectclass" length="100"
     * @hibernate.any-column name="principalobjectid"
     *
     * hibernate.meta-value class="RepoRole" value="RepoRole"
     * hibernate.meta-value class="RepoUser" value="RepoUser"
     * hibernate.meta-value class="RepoTenant" value="RepoTenant"
     */

    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(Object o) {
        this.principal = o;
    }

    /**
     * @hibernate.property column="description" type="string" length="255"
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @hibernate.property column="owner" type="string" length="255"
     */
    public String getOwner() {
        if (owner == null || owner.isEmpty()) {
            if (getPrincipal() instanceof RepoUser) {
                return ((RepoUser) getPrincipal()).getTenantId();
            } else if (getPrincipal() instanceof RepoTenant) {
                return ((RepoTenant) getPrincipal()).getTenantId();
            }
        }

        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Copy from a client object into this one
     *
     * @param obj
     * @param resolver
     */
    public void copyFromClient(Object obj, PersistentObjectResolver resolver) {
        ProfileAttribute other = (ProfileAttribute) obj;
        setAttrName(makeAttributeName(other.getAttrName()));
        String attrValue = other.getAttrValue();
        if (other.isSecure()) {
            attrValue =  PasswordCipherer.getInstance().encryptSecureAttribute(attrValue);
        }
        setAttrValue(attrValue);
        if (other.getPrincipal() != null) {
            setPrincipal(resolver.getPersistentObject(other.getPrincipal()));
        } else {
            setPrincipal(null);
        }
        setDescription(other.getDescription());
        setOwner(getAuthenticatedTenantId());
    }

    /**
     * Copy from this into a new client object
     *
     * @param clientMappingFactory
     */
    public Object toClient(ResourceFactory clientMappingFactory) {
        ProfileAttribute other = (ProfileAttribute) clientMappingFactory.newObject(ProfileAttribute.class);

        other.setAttrName(getAttrName());
        // Temp fix: Oracle database translates empty string as null value,
        // so return empty string to avoid possible NPE exceptions
        String attrValue = (getAttrValue() != null) ? getAttrValue() : "";
        if (PasswordCipherer.getInstance().isEncrypted(attrValue)) {
            attrValue = PasswordCipherer.getInstance().decryptSecureAttribute(attrValue);
            other.setSecure(true);
        }
        other.setAttrValue(attrValue);

        if (getPrincipal() != null) {
            IdedObject thisPrincipal = (IdedObject) getPrincipal();
            Object clientPrincipal = thisPrincipal.toClient(clientMappingFactory);
            other.setPrincipal(clientPrincipal);
            other.setUri(getAttrName(), getAttributeHolderUri(getPrincipal()));
        } else {
            other.setPrincipal(null);
        }
        other.setDescription(getDescription());

        return other;
    }

    public String getAttributeHolderUri(Object repoAttrHolder) {
        StringBuilder builder = new StringBuilder();
        String tenantFolderUri = "";
        if (repoAttrHolder instanceof RepoUser) {
            RepoUser user = (RepoUser)repoAttrHolder;
            builder.append("/users/").append(user.getUsername());
            tenantFolderUri = user.getTenant().getTenantFolderUri();
        } else if (repoAttrHolder instanceof RepoTenant) {
            tenantFolderUri = ((RepoTenant) repoAttrHolder).getTenantFolderUri();
        }
        String parentPath = tenantFolderUri.equals("/")? "" : tenantFolderUri;
        builder.insert(0, parentPath);

        return builder.toString();
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("profileAttributeId", getId())
                .append("attrName", getAttrName())
                .append("attrValue", getAttrValue())
                .append("principal", getPrincipal())
                .append("description", getDescription())
                .toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof RepoProfileAttribute)) return false;
        RepoProfileAttribute castOther = (RepoProfileAttribute) other;
        return new EqualsBuilder()
                .append(this.getId(), castOther.getId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .toHashCode();
    }

    protected String makeAttributeName(String attrName) {
        return PATTERN_RESOURCE_NAME_REPLACE.matcher(attrName).replaceAll("_");
    }

    private String getAuthenticatedTenantId() {
        String result = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            if (auth.getPrincipal() instanceof TenantQualified) {
                result = ((TenantQualified) auth.getPrincipal()).getTenantId();
            }
        }

        if (result == null) {
            return TenantService.ORGANIZATIONS;
        }

        return result;
    }
}
