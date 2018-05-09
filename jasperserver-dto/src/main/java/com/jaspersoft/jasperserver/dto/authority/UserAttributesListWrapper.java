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
@XmlRootElement(name = "attributes")
public class UserAttributesListWrapper {
    private List<ClientAttribute> profileAttributes;

    public UserAttributesListWrapper(){}

    public UserAttributesListWrapper(List<ClientAttribute> attributes){
        profileAttributes = new ArrayList<ClientAttribute>(attributes.size());
        for (ClientAttribute r : attributes){
            profileAttributes.add((ClientAttribute)r);
        }
    }

    public UserAttributesListWrapper(UserAttributesListWrapper other) {
        final List<ClientAttribute> clientAttributes = other.getProfileAttributes();
        if(clientAttributes != null){
            profileAttributes = new ArrayList<ClientAttribute>(other.getProfileAttributes().size());
            for(ClientAttribute attribute : clientAttributes){
                profileAttributes.add(new ClientAttribute(attribute));
            }
        }
    }


    @XmlElement(name = "attribute")
    public List<ClientAttribute> getProfileAttributes() {
        return profileAttributes;
    }

    public void setProfileAttributes(List<ClientAttribute> profileAttributes) {
        this.profileAttributes = profileAttributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAttributesListWrapper that = (UserAttributesListWrapper) o;

        if (profileAttributes != null ? !profileAttributes.equals(that.profileAttributes) : that.profileAttributes != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return profileAttributes != null ? profileAttributes.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserAttributesListWrapper{" +
                "profileAttributes=" + profileAttributes +
                '}';
    }
}
