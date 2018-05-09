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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.filter.BaseSearchFilter;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id: }
 */

public class ExcludeFolderFilter extends BaseSearchFilter {

    @Override
    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {

        String alias = criteria.getAlias("parent", "p");

        for (String uri : toListOfUrisToExclude(context, criteria)) {

            // Filter uri
            criteria.add(Restrictions.ne(alias + ".URI", uri));

            // Filter children uris
            String childrenUrlsExp = (uri.endsWith(Folder.SEPARATOR)) ? uri + "%" : uri + Folder.SEPARATOR + "%";
            criteria.add(Restrictions.not(Restrictions.like(alias + ".URI", childrenUrlsExp)));
        }
    }

    public List<String> toListOfUrisToExclude(ExecutionContext context, SearchCriteria criteria) {
        RepositorySearchCriteria repositorySearchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);

        if (repositorySearchCriteria != null && repositorySearchCriteria.getExcludeRelativePaths() != null){
        	// check and fix duplicates
        	List<String> result = repositorySearchCriteria.getExcludeRelativePaths();
        	Set<String> processed = new HashSet<String>(result.size());
        	processed.addAll(result);
        	if(result.size()>processed.size()){ // there was reduction to the number of results = there were duplicates
        		result.clear();
        		result.addAll(processed);
        		repositorySearchCriteria.setExcludeRelativePaths(result);
        	}
        	// return duplicate-free result
        	return result;
        }

        return Collections.emptyList();
    }
}
