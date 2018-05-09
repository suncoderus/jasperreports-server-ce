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
import java.util.HashSet;
import java.util.Set;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceItem;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceItem;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class HiddenFilter extends BaseSearchFilter implements Serializable {
    private static final long serialVersionUID = 1L;
    // Set of lookup classes "HiddenFilter" is applicable for
    // i.e. it has "hidden" table column.
    private static final Set<String> HiddenFieldClass=new HashSet<String>(){
        private static final long serialVersionUID = 1L;
        {
            add(ResourceLookup.class.getName());
            add(RepoFolder.class.getName());
        }
    };

    private static final Log log = LogFactory.getLog(HiddenFilter.class);
    @Override
    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        RepositorySearchCriteria searchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);

        if (searchCriteria == null || !searchCriteria.isShowHidden())  {
            String p = criteria.getAlias("parent", "p");
            criteria.add(Restrictions.eq(p + ".hidden", Boolean.FALSE));



            if (Folder.class.getName().equals(type)){
                criteria.add(Restrictions.eq("hidden", Boolean.FALSE));
            }

            if( !context.getAttributes().contains(ResourceTypeSearchCriteriaFactory.NOFOLDERALIAS)
            	&& HiddenFieldClass.contains(type)){
                criteria.add(Restrictions.or(Restrictions.isNull("hidden"), Restrictions.eq("hidden", Boolean.FALSE)));
            }
        }
    }
}
