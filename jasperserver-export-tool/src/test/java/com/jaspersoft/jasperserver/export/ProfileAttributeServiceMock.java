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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ProfileAttributeImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchCriteria;
import com.jaspersoft.jasperserver.api.metadata.user.service.AttributesSearchResult;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeCategory;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
    @author Zakhar.Tomchenco
*/
public class ProfileAttributeServiceMock implements ProfileAttributeService {
    private UserAuthorityService userAuthorityService;
    private ProfileAttribute attribute;

    @Override
    public ProfileAttribute getProfileAttribute(ExecutionContext executionContext, ProfileAttribute profileAttribute) {
        return attribute;
    }

    @Override
    public List getProfileAttributesForPrincipal(ExecutionContext executionContext, Object o) {
        return null;  
    }

    @Override
    public List<ProfileAttribute> getCurrentUserProfileAttributes(ExecutionContext executionContext, ProfileAttributeCategory category) {
        return null;
    }

    @Override
    public AttributesSearchResult<ProfileAttribute> getProfileAttributesForPrincipal(ExecutionContext context, Object principal, AttributesSearchCriteria searchCriteria) {
        return null;
    }

    /**
	 * Find all matching attributes for the principal extracted from
	 * SecurityContextHolder.getContext().getAuthentication()
	 * token.
	 *
	 * @param context
	 * @return
	 */
	@Override
	public List getProfileAttributesForPrincipal(ExecutionContext context) {
		return null;
	}

	/**
	 * Find all matching attributes for the principal extracted from
	 * SecurityContextHolder.getContext().getAuthentication() token.
	 * ExecutionContext is null.
	 *
	 * @return
	 */
	@Override
	public List getProfileAttributesForPrincipal() {
		return null;
	}

	public ProfileAttribute newProfileAttribute(ExecutionContext executionContext) {
        return new ProfileAttributeImpl();
    }

    public void putProfileAttribute(ExecutionContext executionContext, ProfileAttribute profileAttribute) {
        attribute = profileAttribute;
    }

    public String getCurrentUserPreferenceValue(String s) {
        return null;  
    }

    public void setCurrentUserPreferenceValue(String s, String s1) {
        
    }

    public UserAuthorityService getUserAuthorityService() {
        return userAuthorityService;
    }

    public void setUserAuthorityService(UserAuthorityService userAuthorityService) {
        this.userAuthorityService = userAuthorityService;
    }

    @Override
    public void deleteProfileAttribute(ExecutionContext context, ProfileAttribute attr) {
    }

	/**
	 * Convert attributesToDelete to a map and send it to #deleteProfileAttributes(Map<String, ProfileAttribute>)
	 *
	 * @param attributesToDelete - collection ProfileAttribute objects to delete
	 */
	@Override
	public void deleteProfileAttributes(Collection<ProfileAttribute> attributesToDelete) {
	}

	/**
	 * Delete all the profile attributes in attributeMapToDelete
	 *
	 * @param attributeMapToDelete - a map of profile attribute names to ProfileAttribute objects
	 */
	@Override
	public void deleteProfileAttributes(Map<String, ProfileAttribute> attributeMapToDelete) {
	}

    @Override
    public void applyProfileAttributes() {
    }

    @Override
    public String getChangerName(String propertyName) {
        return null;
    }

    @Override
    public String generateAttributeHolderUri(Object holder) {
        return null;
    }
}
