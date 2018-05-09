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

package com.jaspersoft.jasperserver.api.search;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

import java.util.List;

/**
 * Search criteria factory.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public interface SearchCriteriaFactory {

    /**
     * Creates {@link SearchCriteria} instance.
     *
     * @param context the execution context.
     * @param filters the filters.
     * @return {@link SearchCriteria} instance.
     */
    SearchCriteria create(ExecutionContext context, List<SearchFilter> filters);

    /**
     * Applies sorters to search criteria.
     *
     * @param context the execution context.
     * @param searchCriteria the search criteria.
     * @param sorter the sorter.
     */
    void applySorter(ExecutionContext context, SearchCriteria searchCriteria, SearchSorter sorter);
    
    /**
     * get a factory that queries on the type you want, not the hardcoded Resource type
     * @param type
     * @return
     */
    SearchCriteriaFactory newFactory(String type);
}
