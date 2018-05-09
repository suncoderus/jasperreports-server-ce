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

import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import org.hibernate.criterion.*;

/**
 * Access type filter.
 * 
 * @author Stas Chubar
 * @version $Id$
 */
public class AccessTypeFilter extends BaseSearchFilter implements Serializable {
    public static final String ACCESS_TYPE_FILTER_NAME = "accessTypeFilter";
    public static final String ACCESS_TYPE_FILTER_ALL_OPTION = "accessTypeFilter-all";
    public static final String ACCESS_TYPE_FILTER_CHANGED_BY_ME_OPTION = "accessTypeFilter-changedByMe";

    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        RepositorySearchCriteria searchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);

        if (searchCriteria == null) {
            SearchAttributes searchAttributes = getSearchAttributes(context);

            User user = null;

            if (context.getAttributes() != null) {
                for (Object o : context.getAttributes()) {
                    if (o instanceof User) {
                        user = (User) o;
                    }
                }
            }

            if (user != null && searchAttributes != null && searchAttributes.getState() != null) {
                String accessFilter = searchAttributes.getState().getCustomFiltersMap().get(ACCESS_TYPE_FILTER_NAME);

                if (accessFilter != null && !accessFilter.equals(ACCESS_TYPE_FILTER_ALL_OPTION)) {
                    processCriteria(criteria, user, accessFilter.equals(ACCESS_TYPE_FILTER_CHANGED_BY_ME_OPTION));
                }
            }
        } else {
            if (!searchCriteria.getAccessType().equals(AccessType.ALL)){
                processCriteria(criteria, searchCriteria.getUser(), AccessType.MODIFIED.equals(searchCriteria.getAccessType()));
            }

        }
    }

    private void processCriteria(SearchCriteria criteria, User user, boolean modified) {
        String alias = criteria.getAlias("accessEvents", "ae");
        criteria.add(Restrictions.eq(alias + ".updating", modified));

        String userAlias = criteria.getAlias(alias + ".user", "u");
        criteria.add(Restrictions.eq(userAlias + ".username", user.getUsername()));

        String tenantId = (user.getTenantId() == null) ? TenantService.ORGANIZATIONS : user.getTenantId();
        String tenantAlias = criteria.getAlias(userAlias + ".tenant", "t");
        criteria.add(Restrictions.eq(tenantAlias + ".tenantId", tenantId));
    }
}