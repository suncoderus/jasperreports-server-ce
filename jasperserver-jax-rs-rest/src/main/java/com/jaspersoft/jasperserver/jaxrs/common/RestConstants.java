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

package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.api.common.util.PaginationConstants;
import com.jaspersoft.jasperserver.war.common.JasperServerHttpConstants;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public interface RestConstants {
    String HEADER_START_INDEX = "Start-Index";
    String HEADER_NEXT_OFFSET = "Next-Offset";
    String HEADER_RESULT_COUNT = "Result-Count";
    String HEADER_TOTAL_COUNT = "Total-Count";

    String HEADER_ACCEPT_TIMEZONE = JasperServerHttpConstants.HEADER_ACCEPT_TIMEZONE;

    String QUERY_PARAM_SEARCH_QUERY = "q";
    String QUERY_PARAM_OFFSET = PaginationConstants.PARAM_OFFSET;
    String QUERY_PARAM_LIMIT = PaginationConstants.PARAM_LIMIT;
    String QUERY_PARAM_SORT_BY = "sortBy";
    String QUERY_PARAM_EXPANDED = "expanded";
    String QUERY_PARAM_DRY_RUN = "dry-run";
    String QUERY_PARAM_INCLUDE = "include";

    String QUERY_PARAM_CREATE_FOLDERS = "createFolders";
}
