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

package com.jaspersoft.jasperserver.search.filter;

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

/**
 * Sorter filter.
 *
 * @author Stas Chubar
 * @version $Id$
 */
public class SorterFilter extends BaseSearchFilter implements Serializable {

    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        SearchAttributes searchAttributes = getSearchAttributes(context);
        RepositorySearchCriteria repositorySearchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);
        String searchBy = searchAttributes != null && searchAttributes.getState() != null ? searchAttributes.getState().getSortBy() :
                repositorySearchCriteria != null ? repositorySearchCriteria.getSortBy() : null;

        if ("popularity".equals(searchBy)) {
            criteria.getAlias("accessEvents", "ae");
        }
    }
}