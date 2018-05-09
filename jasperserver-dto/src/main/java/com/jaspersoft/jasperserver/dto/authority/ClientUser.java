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
package com.jaspersoft.jasperserver.dto.authority;

import com.jaspersoft.jasperserver.dto.resources.ClientProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

/**
 * @author: Zakhar.Tomchenco
 */

@XmlRootElement(name = "user")
public class ClientUser {
    private Set<ClientRole> roleSet;
    private String fullName;
    private String password;
    private String emailAddress;
    private Boolean externallyDefined;
    private Boolean enabled;
    private Date previousPasswordChangeTime;
    private String tenantId;
    private String username;

    public ClientUser() {
    }

    public ClientUser(ClientUser other) {
        final Set<ClientRole> clientRoleSet = other.getRoleSet();
        if(clientRoleSet != null){
            roleSet = new HashSet<ClientRole>(other.getRoleSet().size());
            for(ClientRole role : clientRoleSet){
                roleSet.add(new ClientRole(role));
            }
        }
        this.fullName = other.getFullName();
        this.password = other.getPassword();
        this.emailAddress = other.getEmailAddress();
        this.externallyDefined = other.isExternallyDefined();
        this.enabled = other.isEnabled();
        this.previousPasswordChangeTime = other.getPreviousPasswordChangeTime();
        this.tenantId = other.getTenantId();
        this.username = other.getUsername();
    }


    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    public Set<ClientRole> getRoleSet() {
        return roleSet;
    }

    public ClientUser setRoleSet(Set<ClientRole> roleSet) {
        this.roleSet = roleSet;
        return this;
    }

    @XmlElement(name = "fullName")
    public String getFullName() {
        return fullName;
    }

    public ClientUser setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    @XmlElement(name = "password")
    public String getPassword() {
        return password;
    }

    public ClientUser setPassword(String password) {
        this.password = password;
        return this;
    }

    @XmlElement(name = "emailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }

    public ClientUser setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    @XmlElement(name = "externallyDefined")
    public Boolean isExternallyDefined() {
        return externallyDefined;
    }

    public ClientUser setExternallyDefined(Boolean externallyDefined) {
        this.externallyDefined = externallyDefined;
        return this;
    }

    @XmlElement(name = "enabled")
    public Boolean isEnabled() {
        return enabled;
    }

    public ClientUser setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @XmlElement(name = "previousPasswordChangeTime")
    public Date getPreviousPasswordChangeTime() {
        return previousPasswordChangeTime;
    }

    public ClientUser setPreviousPasswordChangeTime(Date previousPasswordChangeTime) {
        this.previousPasswordChangeTime = previousPasswordChangeTime;
        return this;
    }

    @XmlElement(name = "tenantId")
    public String getTenantId() {
        return tenantId;
    }

    public ClientUser setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @XmlElement(name = "username")
    public String getUsername() {
        return username;
    }

    public ClientUser setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientUser that = (ClientUser) o;

        if (emailAddress != null ? !emailAddress.equals(that.emailAddress) : that.emailAddress != null) return false;
        if (enabled != null ? !enabled.equals(that.enabled) : that.enabled != null) return false;
        if (externallyDefined != null ? !externallyDefined.equals(that.externallyDefined) : that.externallyDefined != null)
            return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (previousPasswordChangeTime != null ? !previousPasswordChangeTime.equals(that.previousPasswordChangeTime) : that.previousPasswordChangeTime != null)
            return false;
        if (roleSet != null ? !roleSet.equals(that.roleSet) : that.roleSet != null) return false;
        if (tenantId != null ? !tenantId.equals(that.tenantId) : that.tenantId != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = roleSet != null ? roleSet.hashCode() : 0;
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
        result = 31 * result + (externallyDefined != null ? externallyDefined.hashCode() : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        result = 31 * result + (previousPasswordChangeTime != null ? previousPasswordChangeTime.hashCode() : 0);
        result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientUser{" +
                "roleSet=" + roleSet +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", externallyDefined=" + externallyDefined +
                ", enabled=" + enabled +
                ", previousPasswordChangeTime=" + previousPasswordChangeTime +
                ", tenantId='" + tenantId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
