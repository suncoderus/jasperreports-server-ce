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

package com.jaspersoft.jasperserver.search.strategy;

/**
 * Resource load strategy interface. The main goal of it is select a strategy for additional elements loading.
 * Additional elements loading is performed when some of the elements were filtered out according to the access
 * restrictions.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public interface ResourceLoadStrategy {

    /**
     * Returns count of items to be load.
     *
     * @param itemsPerPage items per page count.
     * @param currentResultsCount count of elements in the current result list.
     *
     * @return count of items to be load.
     */
    int getItemsToLoadCount(int itemsPerPage, int currentResultsCount);
}
