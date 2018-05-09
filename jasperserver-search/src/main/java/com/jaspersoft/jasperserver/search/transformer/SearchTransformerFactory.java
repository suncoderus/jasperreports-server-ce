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

package com.jaspersoft.jasperserver.search.transformer;

import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.api.search.SearchSorter;
import com.jaspersoft.jasperserver.api.search.ResultTransformer;
import com.jaspersoft.jasperserver.api.search.TransformerFactory;
import com.jaspersoft.jasperserver.api.search.BasicTransformer;
import com.jaspersoft.jasperserver.search.sorter.ByPopularitySorter;

import java.util.List;

/**
 */
public class SearchTransformerFactory extends TransformerFactory {
    public ResultTransformer createTransformer(List<SearchFilter> filters, SearchSorter sorter) {
        if (sorter instanceof ByPopularitySorter) {
            return new SortByPopularityTransformer();
        } else {
            return new BasicTransformer();
        }
    }
}
