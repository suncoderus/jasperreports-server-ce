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
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceItem;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceItemBase;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.api.search.SearchSorter;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FIXME!!!!
 * It's configured as a singleton, and it's assumed that the resource type will always by Resource.
 * That means you can only create criteria expressions based on Resource--no subtypes.
 * The hardcoded resourceType is then passed to various other places, but no one ever does anything with it,
 * and I can't figure out why it's even there.
 * <p/>
 * Changes I'm making are really minimal:
 * - turn private hardcoded getResourceType() into a property
 * - add a newFactory() method so we can create copies on the fly that can actually do more than just query Resource props
 * <p/>
 * Other things that should be addressed are to make it MT-aware
 * (currently, you have to manually adjust any repo path you're using in a filter)
 * @version $Id$
 */
public class ResourceTypeSearchCriteriaFactory implements SearchCriteriaFactory, Cloneable {
    private ResourceFactory persistentClassMappings;
    private Map<String, SearchFilter> typeSpecificFilters;
    private String resourceType;
    private static final Log log = LogFactory.getLog(ResourceTypeSearchCriteriaFactory.class);
    public static String NOFOLDERALIAS="NoFolderAlias";

    public SearchCriteriaFactory newFactory(String type) {
	if(log.isDebugEnabled()){
    		log.debug("*** QUERY newFactory: new type:" + type + " : oldResourceType: " + resourceType);
	}
	try {
            ResourceTypeSearchCriteriaFactory newOne = (ResourceTypeSearchCriteriaFactory) clone();
            if (resourceType != null){
                Class classToReplace = Class.forName(type);
                Class oldClass = Class.forName(resourceType);

                if (!classToReplace.isAssignableFrom(oldClass) || (oldClass.equals(ResourceLookup.class) && classToReplace.equals(Resource.class))){
                    newOne.setResourceType(type);
                }
            } else {
                newOne.setResourceType(type);
            }

            return newOne;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("unexpected", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("unexpected", e);
        }
    }

    public SearchCriteria create(ExecutionContext context, List<SearchFilter> filters) {

        // Search criteria creation.
        Class clazz = resourceType == null ? RepoResourceItem.class : persistentClassMappings.getImplementationClass(resourceType);

        // <hack>
        // bug 45137 : performance hack
        // issue : union query on resources and folder slows down the query execution
        // (on 1M resources search for adhoc data sources takes 120s with union and 1.7s without union on MySQL)
        // If we know we are searching for resources, let's use RepoResourceItem to avoid union
        if (RepoResourceItemBase.class.equals(clazz)) {

            // get Search criteria
            RepositorySearchCriteria criteria = null;

            if (context != null && context.getAttributes() != null) {
                for (Object attr : context.getAttributes()) {
                    if (attr instanceof RepositorySearchCriteria) {
                        criteria = (RepositorySearchCriteria) attr;
                        break;
                    }
                }
            }
            if (criteria != null) {
                List<String> types = criteria.getResourceTypes();
                if (types != null && types.size() > 0) {
                    boolean noNeedFolders = true;
                    for (String type : types) {
                        if (Folder.class.getName().equals(type)) {
                            noNeedFolders = false;
                            break;
                        }
                    }
                    if (noNeedFolders) {
                        // set to Resources only
                        clazz = RepoResourceItem.class;
                        if(context != null && context.getAttributes()!=null){
                            context.getAttributes().add(NOFOLDERALIAS);
                        }
                    }
                }

            }
        }
        // </hack>

        SearchCriteria searchCriteria = SearchCriteria.forClass(clazz);

        // TODO: remove applying of typeSpecificFilters as redundant.        
        // Sub-classing of current factory can be used if required.
        // RepositoryService should be refactored to.
        if (typeSpecificFilters.get(getResourceType()) != null) {
            typeSpecificFilters.get(getResourceType()).applyRestrictions(getResourceType(), context, searchCriteria);
        }

        // Applying regular filters.
        if (filters != null) {
            for (SearchFilter filter : filters) {
                filter.applyRestrictions(getResourceType(), context, searchCriteria);
            }
        }
        if(context !=null && context.getAttributes()!=null){
        	context.getAttributes().remove(NOFOLDERALIAS);
        }

        return searchCriteria;
    }

    public void applySorter(ExecutionContext context, SearchCriteria searchCriteria, SearchSorter sorter) {
        String resourceType = getResourceType();
        // Applying sorters.
        if (sorter != null) {
            sorter.applyOrder(resourceType, context, searchCriteria);
        }
    }

    public void setPersistentClassMappings(ResourceFactory persistentClassMappings) {
        this.persistentClassMappings = persistentClassMappings;
    }

    public void setTypeSpecificFilters(Map<String, SearchFilter> typeSpecificFilters) {
        this.typeSpecificFilters = typeSpecificFilters;
    }

    public void setResourceType(String defaultResourceType) {
        this.resourceType = defaultResourceType;
    }

    public String getResourceType() {
        if (resourceType == null){
            return Resource.class.getName();
        }
        return resourceType;
    }

}
