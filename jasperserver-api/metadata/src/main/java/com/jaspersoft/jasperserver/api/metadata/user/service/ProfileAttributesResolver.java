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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * Resolves profile attributes that used in parametrized repository resources like data sources.
 *
 * @author Volodya Sabadosh
 * @author Vlad Zavadskii
 * @version $Id$
 */
public interface ProfileAttributesResolver {

    /**
     * Substitutes attributes for parametrized resource, by:
     * <ul>
     * <li>serialization to String
     * <li>resolving attributes
     * <li>deserialization back to resource object.
     * </ul>
     *
     * Basic resource fields (i.e. name, label, description etc.) remain untouched.
     *
     * @param resource The parametrized resource
     * @param <T>      resource type
     * @return resource with attribute substitution.
     */
    <T extends Resource> T mergeResource(T resource);

    /**
     * Substitutes attributes for parametrized object, by:
     * <ul>
     * <li>serialization to String
     * <li>resolving attributes
     * <li>deserialization back to original object class instance.
     * </ul>
     *
     * @param object The parametrized object
     * @param <T>      resource type
     * @return resource with attribute substitution.
     */
    <T> T mergeObject(T object, String identifier);

    /**
     * Indicates if provided string contains any <code>attribute</code> placeholder
     *
     * @param str The string to be checked for an attribute
     * @return true if <code>str</code> contains an attribute
     */
    boolean containsAttribute(String str);

    /**
     * Substitutes attributes for parametrized string.
     *
     * @param templateString The parametrized string
     * @param identifier     A short brief what type of resource, field or other object <code>templateString</code> represents.
     *                       Can be <code>null</code>
     * @return string with attribute substitution.
     */
    String merge(String templateString, String identifier);

    /**
     * Substitutes attributes for parametrized string.
     *
     * @param templateString The parametrized string
     * @param identifier     A short brief what type of resource, field or other object <code>templateString</code> represents.
     *                       Can be <code>null</code>
     * @param escapeStrategy Used to escape special characters in attribute. Can be <code>null</code>
     * @return string with attribute substitution.
     */
    String merge(String templateString, String identifier, ProfileAttributeEscapeStrategy escapeStrategy);

    /**
     * Checks whether test resource parametrized ONLY with the profile attributes of given <code>categories</code>.
     * If categories is not specified check whether test resource is parametrized at all.
     *
     * @param resource   The parametrized resource
     * @param categories list of profile attributes. If it specified checks whether test resource parametrized ONLY with
     *                   the attributes of given categories. If categories is not specified check whether test resource is parametrized.
     * @return @return true if <code>resource</code> contains an attribute.
     */
    boolean isParametrizedResource(Object resource, ProfileAttributeCategory... categories);
}