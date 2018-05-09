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

package com.jaspersoft.jasperserver.search.mode;

/**
 * <p>SearchMode which indicates the page presentation and functionality which is accessed.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public enum SearchMode {
    BROWSE,
    SEARCH,
    LIBRARY;

    public static SearchMode getMode(String modeStr) {
        SearchMode mode = SearchMode.BROWSE; // Default mode.
        
        if (modeStr != null) {
            try {
                mode = SearchMode.valueOf(modeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // No op. Use default mode.
            }
        }

        return mode;
    }
    
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
