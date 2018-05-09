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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @author Volodya Sabadosh
 * @version $Id: Id$
 */
@Deprecated
@XmlRootElement(name = "attribute")
public class ClientUserAttribute {
    private String name;
    private String value;
    private Boolean secure = null;
    private Boolean inherited = null;
    private String description;
    private Integer permissionMask;
    private String holder;

    public ClientUserAttribute(ClientUserAttribute other) {
        this.name = other.getName();
        this.value = other.getValue();
        this.secure = other.isSecure();
        this.description = other.getDescription();
        this.permissionMask = other.getPermissionMask();
        this.inherited = other.isInherited();
        this.holder = other.getHolder();
    }

    public ClientUserAttribute() {
    }

    public String getName() {
        return name;
    }

    public ClientUserAttribute setName(String name) {
        this.name = name;
        return this;
    }

    public String getHolder() {
        return holder;
    }

    public ClientUserAttribute setHolder(String holder) {
        this.holder = holder;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ClientUserAttribute setValue(String value) {
        this.value = value;
        return this;
    }

    public ClientUserAttribute setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    @XmlElement(name = "secure")
    public Boolean isSecure() {
        return secure;
    }

    @XmlElement(name = "inherited")
    public Boolean isInherited() {
        return inherited;
    }

    public ClientUserAttribute setInherited(Boolean inherited) {
        this.inherited = inherited;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPermissionMask() {
        return permissionMask;
    }

    public ClientUserAttribute setPermissionMask(Integer permissionMask) {
        this.permissionMask = permissionMask;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientUserAttribute that = (ClientUserAttribute) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (holder != null ? !holder.equals(that.holder) : that.holder != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (holder != null ? holder.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientUserAttribute{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", secure='" + secure + '\'' +
                ", inherited='" + inherited + '\'' +
                ", description='" + description + '\'' +
                ", permissionMask='" + permissionMask + '\'' +
                ", holder='" + holder + '\'' +
                '}';
    }
}
