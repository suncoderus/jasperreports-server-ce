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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeCategory;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: nthapa
 * Date: 3/12/15
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileAttributeCacheKey {

    private static final long serialVersionUID = 20150318000000L;

    private String[] attributeName;
    private ProfileAttributeCategory profileAttributeCategory;
    private boolean required;
    private String currentLoggedInUser;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String[] getAttributeName() {
        return attributeName;
    }

    public ProfileAttributeCategory getProfileAttributeCategory() {
        return profileAttributeCategory;
    }

    public void setProfileAttributeCategory(ProfileAttributeCategory profileAttributeCategory) {
        this.profileAttributeCategory = profileAttributeCategory;
    }

    public void setAttributeName(String[] attributeName) {
        this.attributeName = attributeName;
    }

    public String getCurrentLoggedInUser() {
        return currentLoggedInUser;
    }

    public void setCurrentLoggedInUser(String currentLoggedInUser) {
        this.currentLoggedInUser = currentLoggedInUser;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof ProfileAttributeCacheKey))
            return false;
        if(obj == this)
            return true;

        ProfileAttributeCacheKey target = (ProfileAttributeCacheKey) obj;

        return new EqualsBuilder()
                .append(getAttributeName(), target.getAttributeName())
                .append(getProfileAttributeCategory(), target.getProfileAttributeCategory())
                .append(isRequired(), target.isRequired())
                .append(getCurrentLoggedInUser(), target.getCurrentLoggedInUser())
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(attributeName)
                .append(profileAttributeCategory)
                .append(required)
                .append(currentLoggedInUser)
                .toHashCode();
    }
}
