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

/**
 * Represents a category for profile attribute
 *
 * @author Volodya Sabadosh
 * @version $Id: $
 */
public enum ProfileAttributeGroup {
    ADHOC("adhoc"),
    AWS("aws"),
    LOG4j("log4j"),
    MONDRIAN("mondrian"),
    JDBC("jdbc"),
    JI("ji"),
    CUSTOM("custom"),
    CUSTOM_SERVER_SETTINGS("customServerSettings");

    private String groupName;

    ProfileAttributeGroup(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return groupName;
    }

}
