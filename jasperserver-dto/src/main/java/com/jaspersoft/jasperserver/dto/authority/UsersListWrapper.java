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
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Zakhar.Tomchenco
 */

@XmlRootElement(name = "users")
public class UsersListWrapper {
    private List<ClientUser> userList;

    public UsersListWrapper(){}

    public UsersListWrapper(List<ClientUser> users){
        userList = new ArrayList<ClientUser>(users.size());
        for (ClientUser r : users){
            userList.add(r);
        }
    }

    public UsersListWrapper(UsersListWrapper other) {
        final List<ClientUser> clientUsers = other.getUserList();
        if(clientUsers != null){
            userList = new ArrayList<ClientUser>(other.getUserList().size());
            for(ClientUser user : clientUsers){
                userList.add(new ClientUser(user));
            }
        }
    }


    @XmlElement(name = "user")
    public List<ClientUser> getUserList() {
        return userList;
    }

    public void setUserList(List<ClientUser> users) {
        this.userList = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersListWrapper that = (UsersListWrapper) o;

        if (userList != null ? !userList.equals(that.userList) : that.userList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return userList != null ? userList.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UsersListWrapper{" +
                "userList=" + userList +
                '}';
    }
}
