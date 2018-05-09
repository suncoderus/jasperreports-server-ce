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
package com.jaspersoft.jasperserver.api.metadata.user.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;


/**
 * Manage attributes for principals - Users, Roles
 *
 * @author sbirney
 * @version $Id$
 */
@JasperServerAPI
public interface ProfileAttributeService {

    /**
     * Gets an attribute with principal and attrName to find matching
     * persisted attribute
     *
     * @param context
     * @param attr with principal and name
     * @return Found ProfileAttribute or null
     */
    public ProfileAttribute getProfileAttribute(ExecutionContext context, ProfileAttribute attr);

    /**
     * Gets all profile attributes for current logged in user by specified category or all categories if
     * category argument is null
     *
     * @param category The category to find attributes or null to move trough all categories
     * @return The List of found profile attributes
     */
    public List<ProfileAttribute> getCurrentUserProfileAttributes(ExecutionContext executionContext, ProfileAttributeCategory category);

    /**
     * Find all matching attributes for the passed in principals
     *
     * @param context
     * @param principal
     * @return
     */
    public List getProfileAttributesForPrincipal(ExecutionContext context, Object principal);

    /**
     * Find all matching attributes for the passed in principal
     *
     * @param context
     * @param principal
     * @param effectiveAttributes indicates if attributes should be resolved hierarchically, from principal up to system level
     * @return List of found profile attributes
     */
    public AttributesSearchResult<ProfileAttribute> getProfileAttributesForPrincipal(ExecutionContext context, Object principal,
                                                                   AttributesSearchCriteria searchCriteria);

    /**
     * Find all matching attributes for the principal extracted from
	 * SecurityContextHolder.getContext().getAuthentication()
	 * token.
     *
     * @param context
     * @return
     */

    public List getProfileAttributesForPrincipal(ExecutionContext context);

    /**
     * Find all matching attributes for the principal extracted from
	 * SecurityContextHolder.getContext().getAuthentication() token.
	 * ExecutionContext is null.
     *
     * @return
     */
    public List getProfileAttributesForPrincipal();

    /**
     * Create an empty new attribute
     *
     * @param context
     * @return empty ProfileAttribute
     */
    public ProfileAttribute newProfileAttribute(ExecutionContext context);

    /**
     * Save or update the given attribute
     *
     * @param context
     * @param attr
     */
    public void putProfileAttribute(ExecutionContext context,
				    ProfileAttribute attr);

    /**
     * Delete the given attribute
     *
     * @param context
     * @param attr
     */
    public void deleteProfileAttribute(ExecutionContext context,
				    ProfileAttribute attr);

    /**
     * Convert attributesToDelete to a map and send it to #deleteProfileAttributes(Map<String, ProfileAttribute>)
     *
     * @param attributesToDelete - collection ProfileAttribute objects to delete
     */
    public void deleteProfileAttributes(Collection<ProfileAttribute> attributesToDelete);

    /**
     * Delete all the profile attributes in attributeMapToDelete
     *
     * @param attributeMapToDelete - a map of profile attribute names to ProfileAttribute objects
     */
    public void deleteProfileAttributes(Map<String, ProfileAttribute> attributeMapToDelete);

    /**
     * Get the given attribute for the current logged in user/principal
     *
     * @param attrName
     * @return String attributeValue
     */
    public String getCurrentUserPreferenceValue(String attrName);

    /**
     * Set/create the given attribute for the current logged in user/principal
     *
     * @param attrName
     * @param attrValue
     */
    public void setCurrentUserPreferenceValue(String attrName, String attrValue);

    /**
     * Get the UserAuthorityService used to interact with Users and Roles
     *
     * @param service UserAuthorityService
     */
    public void setUserAuthorityService(UserAuthorityService service);

    /**
     * Set the UserAuthorityService used to interact with Users and Roles
     *
     * @return UserAuthorityService
     */
    public UserAuthorityService getUserAuthorityService();

    /**
     * Applies SERVER profile attributes for all registered property changers
     */
    public void applyProfileAttributes();

    /**
     * Returns PropertyChanger name by a profile attribute name
     */
    public String getChangerName(String propertyName);

    public String generateAttributeHolderUri(Object holder);
}
