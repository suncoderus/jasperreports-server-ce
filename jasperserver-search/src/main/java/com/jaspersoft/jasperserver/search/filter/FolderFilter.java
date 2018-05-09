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
import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceItem;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.war.common.RoleAccessUrisResolver;
import com.jaspersoft.jasperserver.war.common.UriDescriptor;

import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

/**
 * Folder filter.
 *
 * @author Stas Shubar
 * @version $Id$
 */
public class FolderFilter extends BaseSearchFilter implements Serializable {
    private RoleAccessUrisResolver roleAccessUrisResolver;

    public void setRoleAccessUrisResolver(RoleAccessUrisResolver roleAccessUrisResolver) {
        this.roleAccessUrisResolver = roleAccessUrisResolver;
    }

    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        SearchMode searchMode = null;
        String folderUri = null;
        List<ClientResourceLookup> resources = null;
        SearchAttributes searchAttributes = getSearchAttributes(context);
        RepositorySearchCriteria searchCriteria = null;
        if(searchAttributes != null){
            searchMode = searchAttributes.getMode();
            folderUri = searchAttributes.getState() != null ? searchAttributes.getState().getFolderUri() : null;
        }else{
            searchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);
            if(searchCriteria != null){
                searchMode = searchCriteria.getSearchMode();
                folderUri = searchCriteria.getFolderUri();
                resources = searchCriteria.getResources();
            }
        }

        boolean useStrict = SearchMode.BROWSE == searchMode;
//        &&
//        		(	searchCriteria==null 
//        			|| !RepoResourceItem.class.getName().equals(searchCriteria.getLookupClass())
//        );
        
        if(resources!=null && !resources.isEmpty()){
            String alias = criteria.getAlias("parent", "p");
            Disjunction uriDisjunction = Restrictions.disjunction();
            boolean addPublic = true;
            for(ClientResourceLookup resource:resources){
            	
	            String absoluteUri = roleAccessUrisResolver.getAbsoluteUri(resource.getUri());
	
	            if (useStrict ) {
	                uriDisjunction.add(Restrictions.eq(alias + ".URI", absoluteUri));
	            } else { // Other modes are search like at the moment.
	                addPublic = addSearchModeFolderRestrictions(criteria, uriDisjunction, absoluteUri, alias, addPublic);
	            }
            }
            criteria.add(uriDisjunction);
            addRoleAccessUrlsRestrictions(criteria);
        	
        } else if (folderUri != null) {
            String absoluteUri = roleAccessUrisResolver.getAbsoluteUri(folderUri);

            String alias = criteria.getAlias("parent", "p");
            if (useStrict) {
                criteria.add(Restrictions.eq(alias + ".URI", absoluteUri));
            } else { // Other modes are search like at the moment.
                addSearchModeFolderRestrictions(criteria, null, absoluteUri, alias, true);
            }

            addRoleAccessUrlsRestrictions(criteria);
        }
    }

    protected String createSubFoldersLikeUri(String folderUri) {
        return (folderUri.endsWith(Folder.SEPARATOR)) ? folderUri + "%" : folderUri + Folder.SEPARATOR + "%";
    }

    protected boolean addSearchModeFolderRestrictions(SearchCriteria criteria, Disjunction disjunction, String folderUri, String alias, boolean addPublic) {
    	if(disjunction!=null){
    		disjunction.add(Restrictions.or(Restrictions.like(alias + ".URI", createSubFoldersLikeUri(folderUri)),
                Restrictions.eq(alias + ".URI", folderUri)));
    	} else {
    		criteria.add(Restrictions.or(Restrictions.like(alias + ".URI", createSubFoldersLikeUri(folderUri)),
                Restrictions.eq(alias + ".URI", folderUri)));
    	}
    	return addPublic;
    }

    private void addRoleAccessUrlsRestrictions(SearchCriteria criteria) {
        String alias = criteria.getAlias("parent", "p");

        for (UriDescriptor uriDescriptor : roleAccessUrisResolver.getRestrictedUris()) {
            String uri = (uriDescriptor.isAbsolute()) ? uriDescriptor.getUri() :
                   roleAccessUrisResolver.getAbsoluteUri(uriDescriptor.getUri());
            // Filter uri
            criteria.add(Restrictions.ne(alias + ".URI", uri));

            // Filter children uris
            String childrenUrlsExp = (uri.endsWith(Folder.SEPARATOR)) ? uri + "%" : uri + Folder.SEPARATOR + "%";
            criteria.add(Restrictions.not(Restrictions.like(alias + ".URI", childrenUrlsExp)));
        }
    }
}
