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
package com.jaspersoft.jasperserver.dto.authority.hypermedia;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id $
 */
@XmlRootElement(name="_links")
public class HypermediaAttributeLinks {
    private Link permission;

    public HypermediaAttributeLinks() {
    }

    public HypermediaAttributeLinks(HypermediaAttributeLinks other) {
        if (other.getPermission() != null) {
            this.permission = new Link(other.getPermission());
        }
    }

    @XmlElement(name = "permission")
    public Link getPermission() {
        return permission;
    }

    public HypermediaAttributeLinks setPermission(Link permission) {
        this.permission = permission;
        return this;
    }

    @Override
    public int hashCode() {
        return permission == null ? 0 : permission.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof HypermediaAttributeLinks) {
            HypermediaAttributeLinks other = (HypermediaAttributeLinks) obj;
            return permission == other.permission || (permission != null && permission.equals(other.permission));
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "permission=" + permission +
                '}';
    }
}
