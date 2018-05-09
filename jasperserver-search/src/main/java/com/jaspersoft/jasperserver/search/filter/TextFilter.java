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

import org.hibernate.criterion.Criterion;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.ResourceCriterionUtils;
import com.jaspersoft.jasperserver.api.metadata.common.util.DatabaseCharactersEscapeResolver;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

/**
 * Text filter.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class TextFilter extends BaseSearchFilter implements Serializable {
    private DatabaseCharactersEscapeResolver databaseCharactersEscapeResolver;

    public void setDatabaseCharactersEscapeResolver(DatabaseCharactersEscapeResolver databaseCharactersEscapeResolver) {
        this.databaseCharactersEscapeResolver = databaseCharactersEscapeResolver;
    }

    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
    	Criterion criterion = ResourceCriterionUtils.getTextCriterion(getText(context));
    	if(criterion!=null){
    		criteria.add(criterion);
    	}
    }

    protected String getText(ExecutionContext context) {
        SearchAttributes searchAttributes = getSearchAttributes(context);
        RepositorySearchCriteria repositorySearchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);

        String text = searchAttributes != null && searchAttributes.getState() != null ?
                searchAttributes.getState().getText() :
                repositorySearchCriteria != null ? repositorySearchCriteria.getSearchText() : null;

        return text == null ? "" : databaseCharactersEscapeResolver.getEscapedText(text);
    }

}
