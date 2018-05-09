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

package com.jaspersoft.jasperserver.search.state;

import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.model.FilterPath;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.Map;
import java.util.Set;

/**
 * Keeps the state of the search request.
 *
 * @author Stas Chubar
 * @author Yuriy Plakosh
 * @version $Id$
 */
public interface State {
    String getText();

    String getSortBy();

    String getFolderUri();

    /**
     * @deprecated customFiltersMap is partially moved to *ModeRepositorySearchConfiguration. This duplication should be removed or changed
     */
    Map<String, String> getCustomFiltersMap();

    int getResultIndex();

    int getResultsCount();

    void updateText(String text);

    void updateSorter(String sortBy);

    void updateFolder(String folderUri);

    void updateFilter(String filterId, String optionId, boolean isDefault);

    void updateResultState(int resultIndex, int resultsCount);
    
    void rollback(int position, RepositorySearchConfiguration configuration);

    FilterPath getFilterPath(RepositorySearchConfiguration configuration, MessageSource messageSource);

    JSONObject toJson() throws JSONException;

    Set<String> getServedUri();
}
