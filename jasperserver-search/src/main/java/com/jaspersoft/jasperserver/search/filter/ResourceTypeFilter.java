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
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFileResource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.RepoReportUnit;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import java.io.Serializable;
import java.util.*;

/**
 * <p>Filters resources by resourceType field.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
@SuppressWarnings("serial")
public class ResourceTypeFilter extends BaseSearchFilter implements Serializable {
    private Map<String, List<String>> filterOptionToResourceTypes;
	@SuppressWarnings("unused")
	private ResourceFactory persistentClassMappings;
    @SuppressWarnings("unused")
	private Map<String, List<String>> persistentResourceTypesCache =
            Collections.synchronizedMap(new HashMap<String, List<String>>());

    public void setFilterOptionToResourceTypes(Map<String, List<String>> filterOptionToResourceTypes) {
        this.filterOptionToResourceTypes = filterOptionToResourceTypes;
    }

    public void setPersistentClassMappings(ResourceFactory persistentClassMappings) {
        this.persistentClassMappings = persistentClassMappings;
    }

    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        SearchAttributes searchAttributes = getSearchAttributes(context);

        if (searchAttributes != null && searchAttributes.getState() != null) {
            String resourceTypeFilterOption =
                    searchAttributes.getState().getCustomFiltersMap().get("resourceTypeFilter");

            List<String> resourceTypes = null;
            if (resourceTypeFilterOption != null) {
                resourceTypes = filterOptionToResourceTypes.get(resourceTypeFilterOption);
            }

            if (resourceTypes != null) {
                criteria.add(Restrictions.in("resourceType", resourceTypes));
            }
        } else {
            final RepositorySearchCriteria repositorySearchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);
            if (repositorySearchCriteria != null) {
                if (!CollectionUtils.isEmpty(repositorySearchCriteria.getResourceTypes()) ||
                        !CollectionUtils.isEmpty(repositorySearchCriteria.getFileResourceTypes())) {

                    List<String> types = new ArrayList<String>(repositorySearchCriteria.getResourceTypes());
                    List<Criterion> includeTypes = new ArrayList<Criterion>();

                    boolean addFolders = types.remove(Folder.class.getName());
                    boolean extractTopics = types.remove("com.jaspersoft.commons.semantic.datasource.Topic");
                    boolean filterSecureFileType = types.remove(FileResource.TYPE_SECURE_FILE);

                    Criterion criterion = types.isEmpty()  ? null : Restrictions.in("resourceType", types);

                    // TODO: implement generic solution handling any fileType of FileResource
                    if (filterSecureFileType && types.contains(FileResource.class.getName())) {
                                               DetachedCriteria secureFileCriteria = DetachedCriteria.forClass(RepoFileResource.class, "U");

                        Criterion isSecureFile = Restrictions.eq("U.fileType", FileResource.TYPE_SECURE_FILE);;

                        secureFileCriteria
                                .add(isSecureFile)
                                .add(Property.forName("U.id").eqProperty(criteria.getAlias()+".id"))
                                .setProjection(Projections.property("U.id"));

                        criterion = Subqueries.exists(secureFileCriteria);

                    }

                    if (addFolders && (ResourceLookup.class.getName().equals(type) || (types.isEmpty() && Resource.class.getName().equals(type)))) {
                                               // folders only are requested
                        Criterion folderCriterion = Restrictions.isNull("resourceType");
                        criterion = criterion == null ? folderCriterion : Restrictions.or(folderCriterion, criterion);
                    }
                    if (extractTopics) {
                        extractTopics(includeTypes, criteria);
                    }
                    for (Criterion includeType : includeTypes) {
                        criterion = (criterion == null) ? includeType : Restrictions.or(criterion, includeType);
                    }

                    if (repositorySearchCriteria.getFileResourceTypes() != null &&
                            !repositorySearchCriteria.getFileResourceTypes().isEmpty()) {
                        Criterion fileTypeCriterion = Restrictions.or(
                                Restrictions.in("fileType", repositorySearchCriteria.getFileResourceTypes()),
                                Restrictions.in("contentFileType", repositorySearchCriteria.getFileResourceTypes())
                        );
                        criterion = criterion == null ? fileTypeCriterion : Restrictions.or(criterion, fileTypeCriterion);
                    }

                    criteria.add(criterion);

                } else if (!CollectionUtils.isEmpty(repositorySearchCriteria.getExcludeResourceTypes())) {
                    List<String> excludeTypes = new ArrayList<String>(repositorySearchCriteria.getExcludeResourceTypes());
                    // Additional types that require additional criterions to be search for.
                    // And only those types that are not present in excluded one
                    List<Criterion> includeTypes = new ArrayList<Criterion>();

                    Criterion criterion = Restrictions.not(Restrictions.in("resourceType", repositorySearchCriteria.getExcludeResourceTypes()));

                    boolean addFolders = !excludeTypes.remove(Folder.class.getName());
                    boolean extractTopics = !excludeTypes.remove("com.jaspersoft.commons.semantic.datasource.Topic");
                    boolean filterSecureFileType = !excludeTypes.remove(FileResource.TYPE_SECURE_FILE);

                    if (filterSecureFileType && !excludeTypes.contains(FileResource.class.getName())) {
                        criterion = filterSecureFileTypeCriterion(criteria);
                    }
                    if (addFolders && (ResourceLookup.class.getName().equals(type))) {
                        includeTypes.add(addFoldersCriterion());
                    }
                    if (extractTopics && !excludeTypes.contains(ReportUnit.class.getName())) {
                        extractTopics(includeTypes, criteria);
                    }
                    for (Criterion includeType : includeTypes) {
                        criterion = Restrictions.or(criterion, includeType);
                    }

                    criteria.add(criterion);
                }
            } else {
                throw new RuntimeException("Resource type filter not found in the custom filters map.");
            }
        }
    }

    protected void extractTopics(List<Criterion> includeTypes, SearchCriteria criteria) {
    }

    private Criterion addFoldersCriterion() {
        return Restrictions.isNull("resourceType");
    }

    private Criterion filterSecureFileTypeCriterion(SearchCriteria criteria) {
        DetachedCriteria secureFileCriteria = DetachedCriteria.forClass(RepoFileResource.class, "U");

        Criterion isSecureFile = Restrictions.eq("U.fileType", FileResource.TYPE_SECURE_FILE);

        secureFileCriteria
                .add(isSecureFile)
                .add(Property.forName("U.id").eqProperty(criteria.getAlias() + ".id"))
                .setProjection(Projections.property("U.id"));

        return Subqueries.exists(secureFileCriteria);
    }
}
