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
package com.jaspersoft.jasperserver.dto.adhoc.query.group.axis;

/**
 * Enum of possible axis names used in groupBy query clause.
 * Currently supported two axes: columns, rows.
 *
 * @author Andriy Godovanets
 */
public enum ClientGroupAxisEnum {
    COLUMNS(0, "columns"),
    ROWS(1, "rows"),
    GROUPS(2, "groups");

    private String name;
    private int index;

    ClientGroupAxisEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public static ClientGroupAxisEnum fromString(String axis) {
        if (axis == null) {
            return null;
        }

        axis = axis.toLowerCase();

        if (axis.equals(COLUMNS.name)) {
            return COLUMNS;
        }
        if (axis.equals(ROWS.name)) {
            return ROWS;
        }
        if (axis.equals(GROUPS.name)) {
            return GROUPS;
        }

        throw new IllegalArgumentException(String.format("Unsupported axis name: %s", axis));
    }

    public static ClientGroupAxisEnum fromIndex(int index) {
        if (COLUMNS.index == index) {
            return COLUMNS;
        }
        if (ROWS.index == index) {
            return ROWS;
        }
        if (GROUPS.index == index) {
            return GROUPS;
        }

        throw new IllegalArgumentException(String.format("Unsupported axis index: %s", index));
    }

    @Override
    public String toString() {
        return name;
    }
}
