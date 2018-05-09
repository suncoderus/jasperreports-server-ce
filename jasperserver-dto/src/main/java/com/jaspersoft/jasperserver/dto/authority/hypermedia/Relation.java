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

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id$
 */
public enum Relation {
    PERMISSION("permission"),

    RESOURCE("resource");

    private String relation;

    private static final Map<String, Relation> stringToEnum = new HashMap<String, Relation>();
    static { // Initialize map from constant name to enum constant
        for (Relation relation : values())
            stringToEnum.put(relation.toString(), relation);
    }

    // Returns Relation for string, or null if string is invalid
    public static Relation fromString(String symbol) {
        return stringToEnum.get(symbol);
    }


    Relation(String relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return this.relation;
    }
}
