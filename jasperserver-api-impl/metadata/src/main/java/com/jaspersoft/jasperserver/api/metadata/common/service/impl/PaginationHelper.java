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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.metadata.common.service.PaginatedOperationResult;

import java.util.List;

/**
 *
 */
public class PaginationHelper {

    static public PaginatedOperationResult paginatedGetOperationResult(int firstResult, int maxResults, JasperService service) {

        int resultCount = service.getResultCount();
        int currentFirstResult = firstResult;

        if (resultCount > 0 && firstResult >= resultCount) {

            currentFirstResult = resultCount - maxResults;
            currentFirstResult = (currentFirstResult > -1) ? currentFirstResult : 0;
        }

        List result = service.getResultList(currentFirstResult, maxResults);

        return new PaginatedOperationResult(result, currentFirstResult, maxResults, resultCount);
    }

    public interface JasperService {

        public List getResultList(int firstResult, int maxResults);

        public int getResultCount();
    }

}
