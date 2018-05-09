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

package com.jaspersoft.jasperserver.search.service;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.api.search.SearchSorter;
import com.jaspersoft.jasperserver.search.common.ResourceDetails;

import java.util.List;

/**
 * Repository search service.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public interface RepositorySearchService {
    public static final String PARAM_SORT_BY_POPULARITY = "popularity";
    public int getResultsCount(final ExecutionContext context, final SearchCriteriaFactory searchCriteriaFactory,
        final List<SearchFilter> filters, final SearchSorter sorter);

    public List<ResourceDetails> getResults(final ExecutionContext context,
            final SearchCriteriaFactory searchCriteriaFactory, final List<SearchFilter> filters,
            final SearchSorter sorter, final int current, final int max);

    public List<ResourceDetails> getResults(final ExecutionContext context, RepositorySearchCriteria criteria);

    public List<ResourceLookup> getLookups(final ExecutionContext context,
            final SearchCriteriaFactory searchCriteriaFactory, final List<SearchFilter> filters,
            final SearchSorter sorter, final int current, final int max);

    public List<ResourceLookup> getLookups(final ExecutionContext context, RepositorySearchCriteria criteria);

    public RepositorySearchResult<ResourceLookup> getLookupsForFullPage(final ExecutionContext context,
                                                                            RepositorySearchCriteria criteria);

    public int getResultsCount(final ExecutionContext context, RepositorySearchCriteria criteria);

    public List getResultsCountList(final ExecutionContext context, RepositorySearchCriteria criteria);

    
    public List<ResourceDetails> getResourceChildren(String type, String resourceUri);
}
