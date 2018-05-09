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
import java.util.List;

/**
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */

@XmlRootElement(name = "organizations")
public class OrganizationsListWrapper {

    private List<ClientTenant> list;

    public OrganizationsListWrapper(){}

    public OrganizationsListWrapper(List<ClientTenant> roles){
       list = roles;
    }

    @XmlElement(name = "organization")
    public List<ClientTenant> getList() {
        return list;
    }

    public void setList(List<ClientTenant> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganizationsListWrapper that = (OrganizationsListWrapper) o;

        if (list != null ? !list.equals(that.list) : that.list != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return list != null ? list.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "OrganizationsListWrapper{" +
                "list=" + list +
                '}';
    }
}
