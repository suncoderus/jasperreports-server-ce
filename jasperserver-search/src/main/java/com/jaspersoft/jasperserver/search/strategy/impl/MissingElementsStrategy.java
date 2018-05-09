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

package com.jaspersoft.jasperserver.search.strategy.impl;

import com.jaspersoft.jasperserver.search.strategy.ResourceLoadStrategy;

/**
 * Missing elements strategy is used to load only missing elements.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class MissingElementsStrategy implements ResourceLoadStrategy {

    /**
     * @see ResourceLoadStrategy#getItemsToLoadCount(int, int)
     */
    public int getItemsToLoadCount(int itemsPerPage, int currentResultsCount) {
        return itemsPerPage - currentResultsCount;
    }
}
