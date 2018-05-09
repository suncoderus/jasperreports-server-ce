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

package com.jaspersoft.jasperserver.search.model;

import java.util.Date;


/**
 * Contains description of filter path item.
 *
 * @author Stas Chubar
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class PathItem {
    private int position;
    private Type type;
    private String label;

    public enum Type {
        TEXT,
        FILTER
    }

    /**
     * Returns the position of the current path item.
     *
     * @return the position.
     */
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Returns the type of path item.
     *
     * @return the type of path item.
     */
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the value of path item. The interpretation of the value dependents on the type of current path item.
     *
     * @return the value of path item.
     */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
