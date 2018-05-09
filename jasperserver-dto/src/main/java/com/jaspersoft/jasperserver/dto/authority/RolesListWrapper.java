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
import java.util.*;

/**
 * @author: Zakhar.Tomchenco
 */

@XmlRootElement(name = "roles")
public class RolesListWrapper {
    private List<ClientRole> roleList;

    public RolesListWrapper(){}

    public RolesListWrapper(List<ClientRole> roles){
        roleList = new ArrayList<ClientRole>(roles.size());
        for (ClientRole r : roles){
            roleList.add(r);
        }
    }

    public RolesListWrapper(RolesListWrapper other) {
        final List<ClientRole> clientRoleList = other.getRoleList();
        if(clientRoleList != null){
            roleList = new ArrayList<ClientRole>(other.getRoleList().size());
            for(ClientRole role : clientRoleList){
                roleList.add(new ClientRole(role));
            }
        }
    }


    @XmlElement(name = "role")
    public List<ClientRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<ClientRole> roleList) {
        this.roleList = roleList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RolesListWrapper that = (RolesListWrapper) o;

        if (roleList != null ? !roleList.equals(that.roleList) : that.roleList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return roleList != null ? roleList.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RolesListWrapper{" +
                "roleList=" + roleList +
                '}';
    }
}
