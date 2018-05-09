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

package com.jaspersoft.jasperserver.search.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElementDisjunction;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.search.common.ResourceDetails;
import com.jaspersoft.jasperserver.search.common.SchedulingChecker;
import com.jaspersoft.jasperserver.search.service.ChildrenLoaderService;
import com.jaspersoft.jasperserver.search.service.ResourceService;
import com.jaspersoft.jasperserver.search.service.ResourceTypeResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.LikeEscapeAwareExpression.ESCAPE_CHAR;
import static com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.LikeEscapeAwareExpression.escape;
import static com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria.createPropertyEqualsFilter;
import static com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria.createPropertyLikeFilter;
import static org.hibernate.criterion.MatchMode.START;

/**
 * Resources management service.
 *
 * @author Stas Chubar
 * @author Yuriy Plakosh
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ResourceServiceImpl extends BaseService implements ResourceService {
    private static final Pattern INDEXED_LABEL_PATTERN = Pattern.compile("(.*)\\((\\d+)\\)");
    private static final Pattern INDEXED_NAME_PATTERN = Pattern.compile("(.*)_(\\d+)$");

    private static final Log log = LogFactory.getLog(ResourceServiceImpl.class);

    private static final String LABEL_DELIMITER = " (";
    private static final String NAME_DELIMITER = "_";

    private SessionFactory sessionFactory;

    private RepositorySecurityChecker securityChecker;
    private SchedulingChecker schedulingChecker;
    private ResourceTypeResolver typeResolver;
    private Map<String, ChildrenLoaderService> childrenLoaders;
    private List<String> deleteOrder;
    private List<String> checkDependentResourcesFor;
    private SearchCriteriaFactory searchCriteriaFactory;
    private int maxDependentReports = 20;
    private String tempFolderName;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public ResourceDetails update(String uri, String label, String description) {
        String parentUri = uri.substring(0, uri.lastIndexOf("/"));

        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentUri));

        Resource resource = repositoryService.getResource(null, uri);
        if (!label.equals(resource.getLabel()) || !description.equals(resource.getDescription())) {
            resource.setLabel(label);
            resource.setDescription(description);

            repositoryService.saveResource(null, resource);
        }

        return getResourceDetails(repositoryService.getResource(null, uri));
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(final List<Resource> resources) {
        Collections.sort(resources, new DeleteOrderComparator());

        for (Resource resource : resources) {
            deleteTempDependencies(resource);
            repositoryService.deleteResource(null, resource.getURIString());
        }
    }

    /**
     * Delete dependencies from temporary folder
     *
     * @param resource
     */
    private void deleteTempDependencies(Resource resource) {
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext();
        List<ResourceLookup> dependentResources = repositoryService.getDependentResources(context, resource.getURIString(), searchCriteriaFactory, 0, 0); //from zero index, unlimited count
        if (dependentResources == null) {
            return;
        }
        for (ResourceLookup depResource : dependentResources) {
            if (depResource.getURIString().startsWith(tempFolderName)) {
                deleteTempDependencies(depResource);
                repositoryService.deleteResource(context, depResource.getURIString());
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<ResourceDetails> check(ExecutionContext context, final List<Resource> resources) {
        Collections.sort(resources, new DeleteOrderComparator());

        List<ResourceDetails> result = new ArrayList<ResourceDetails>();
        for (Resource resource : resources) {
            List<ResourceDetails> dependentResources = getDependentResources(context, resource.getURIString());
            if (dependentResources != null) {
                result.addAll(dependentResources);
            }

            if (result.size() >= maxDependentReports) {
                break;
            }
        }

        if (!result.isEmpty()) {
            if (result.size() > maxDependentReports) {
                result = result.subList(0, maxDependentReports);
            }

            Collections.sort(result, new Comparator<ResourceLookup>() {
                @Override
                public int compare(ResourceLookup o1, ResourceLookup o2) {
                    return o1.getURIString().compareTo(o2.getURIString());
                }
            });
        }

        return result;
    }

    /**
     * Get list of dependent non-temporary resources
     *
     * @param context
     * @param resourceUri
     * @return
     */
    public List<ResourceDetails> getDependentResources(ExecutionContext context, String resourceUri) {
        List<ResourceDetails> result = null;
        final Resource resource = repositoryService.getResource(context, resourceUri);

        if (resource != null && CollectionUtils.isNotEmpty(checkDependentResourcesFor) &&
                checkDependentResourcesFor.contains(resource.getResourceType())) {

            List<ResourceLookup> dependentResources = repositoryService.getDependentResources(context, resourceUri, searchCriteriaFactory, 0, maxDependentReports);
            if (dependentResources == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Can't check for dependent reports of resource type!");
                }
                return result;
            }
            result = new ArrayList<ResourceDetails>();
            for (ResourceLookup lookup : dependentResources) {
                if (!lookup.getParentFolder().equals(tempFolderName)) {
                    result.add(new ResourceDetails(lookup));
                }
            }
        }

        return result;
    }

    protected Map<String, String> getExistingNamesAndLabels(String parentFolderUri, Collection<Resource> copiedResources) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            List<Folder> repoFolderList = repositoryService.getSubFolders(null, parentFolderUri);
            for (Folder folder : repoFolderList) {
                result.put(folder.getName(), folder.getLabel());
            }
            FilterCriteria criteria = FilterCriteria.createFilter();
            criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentFolderUri));
            FilterElementDisjunction disjunction = criteria.addDisjunction();
            for (Resource resource : copiedResources) {
                String pattern;
                disjunction.addFilterElement(createPropertyEqualsFilter("name", resource.getName()));
                pattern = escape(resource.getName() + NAME_DELIMITER, ESCAPE_CHAR);
                disjunction.addFilterElement(createPropertyLikeFilter("name", pattern, START, ESCAPE_CHAR));
                disjunction.addFilterElement(createPropertyEqualsFilter("label", resource.getLabel()));
                pattern = escape(resource.getLabel() + LABEL_DELIMITER, ESCAPE_CHAR);
                disjunction.addFilterElement(createPropertyLikeFilter("label", pattern, START, ESCAPE_CHAR));
            }

            List<Resource> resources = repositoryService.loadResourcesList(null, criteria);
            for (Resource resource : resources) {
                result.put(resource.getName(), resource.getLabel());
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return result;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void copy(Set<String> resourceUris, String destinationFolderUri) {
        Map<String, Resource> resourceMap = getResourceMap(resourceUris);
        if (!isLabelsUnique(resourceMap)) {
            throw new JSException("jsexception.search.duplicate.label", new Object[]{"", destinationFolderUri});
        }
        // here are all names and labels, that exist in target folder
        final Map<String, String> existingNamesAndLabels = getExistingNamesAndLabels(destinationFolderUri, resourceMap.values());
        Set<String> resourceUrisToCopy = new HashSet<String>();
        // now we are able to paste resources to the same folder, where they are placed. To process this situation correctly
        // we have to change names/labels of resources. So, let's go over resources to copy list and process all resources
        // with already existing labels/name in a separate way.
        for (Resource resource : resourceMap.values()) {
            String label = resource.getLabel();
            String name = resource.getName();
            final Collection<String> labels = existingNamesAndLabels.values();
            if (labels.contains(label) || existingNamesAndLabels.containsKey(name)) {
                // such label or name already exist in target folder. We have to make it's indexed copy.
                // For instance if original resource label is "Some Resource", then indexed copy will have label "Some Resource (1)"
                int index = 1;
                String labelPrefix = label;
                final Matcher initialLabelIndexMatcher = INDEXED_LABEL_PATTERN.matcher(label);
                if (initialLabelIndexMatcher.find()) {
                    // current label already indexed. Let's extract it's index and prefix to generate next index label
                    labelPrefix = initialLabelIndexMatcher.group(1).trim();
                    index = Integer.valueOf(initialLabelIndexMatcher.group(2));
                }
                // let's find index, that doesn't exist yet
                label = buildIndexedLabel(labelPrefix, index);
                while (labels.contains(label)) {
                    index++;
                    label = buildIndexedLabel(labelPrefix, index);
                }
                //let's process name in this indexed manner
                final Matcher initialNameIndex = INDEXED_NAME_PATTERN.matcher(name);
                if (initialNameIndex.find()) {
                    // this name is indexed. Let's try to change index keeping the same prefix.
                    final String updatedIndexName = buildIndexedName(initialNameIndex.group(1), index);
                    if (!existingNamesAndLabels.containsKey(updatedIndexName)) {
                        // no name with such index. Let's use indexed
                        name = updatedIndexName;
                    } else {
                        // name with such index already exist. Let's use original indexed name as prefix
                        do {
                            // keep appending index till such name doesn't exist
                            name = buildIndexedName(name, index);
                        } while (existingNamesAndLabels.containsKey(name));
                    }

                }
                final Resource copied = repositoryService.copyRenameResource(null, resource.getURIString(),
                        destinationFolderUri + "/" + name, label);
                // add copied resource name and label to existing names and labels map
                existingNamesAndLabels.put(copied.getName(), copied.getLabel());

            } else {
                resourceUrisToCopy.add(resource.getURIString());
            }
        }
        repositoryService.copyResources(null, resourceUrisToCopy.toArray(new String[resourceUrisToCopy.size()]),
                destinationFolderUri);
    }

    protected String buildIndexedLabel(String labelPrefix, int index) {
        return labelPrefix + LABEL_DELIMITER + index + ")";
    }

    protected String buildIndexedName(String namePrefix, int index) {
        return namePrefix + NAME_DELIMITER + index;
    }

    private Set<String> getLabels(Map<String, Resource> resourceMap) {
        Set<String> result = new HashSet<String>();

        for (Resource resource : resourceMap.values()) {
            result.add(resource.getLabel());
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void move(Set<String> resourceUris, String destinationFolderUri) {
        Map<String, Resource> resourceMap = getResourceMap(resourceUris);

        if (!isLabelsUnique(resourceMap) || isObjectsLabelsExist(destinationFolderUri, getLabels(resourceMap))) {
            throw new JSException("jsexception.search.duplicate.label", new Object[]{"", destinationFolderUri});
        }

        if (resourceMap.size() != getResourcesWithUniqueName(resourceMap).size()) {
            throw new JSException("jsexception.search.duplicate.name", new Object[]{"", destinationFolderUri});
        }

        for (String uri : resourceMap.keySet()) {
            repositoryService.moveResource(null, uri, destinationFolderUri);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceDetails getResourceDetails(Resource resource) {
        ResourceDetails resourceDetails = new ResourceDetails(resource);

        if (securityChecker != null) {
//            resourceDetails.setReadable(securityChecker.isResourceReadable(resource.getURIString()));
            resourceDetails.setReadable(true);
            resourceDetails.setEditable(securityChecker.isEditable(resource));
            resourceDetails.setRemovable(securityChecker.isRemovable(resource));
            resourceDetails.setAdministrable(securityChecker.isAdministrable(resource));
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Repository security checker is null!");
            }
        }

        if (schedulingChecker != null) {
            resourceDetails.setScheduled(schedulingChecker.isScheduled(null, resource));
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Scheduling checker is null!");
            }
        }

        ChildrenLoaderService childrenLoaderService = childrenLoaders.get(resource.getResourceType());

        if (childrenLoaderService != null) {
            resourceDetails.setHasChildren(childrenLoaderService.hasChildren(resourceDetails.getURIString()));
        } else {
            resourceDetails.setHasChildren(false);
        }

        if (typeResolver != null) {
            resourceDetails.setResourceType(typeResolver.getResourceType(resource));
        }

        return resourceDetails;
    }

    public void setSecurityChecker(RepositorySecurityChecker securityChecker) {
        this.securityChecker = securityChecker;
    }

    public void setDeleteOrder(List<String> deleteOrder) {
        this.deleteOrder = deleteOrder;
    }

    public void setSchedulingChecker(SchedulingChecker schedulingChecker) {
        this.schedulingChecker = schedulingChecker;
    }

    public void setChildrenLoaders(Map<String, ChildrenLoaderService> childrenLoaders) {
        this.childrenLoaders = childrenLoaders;
    }

    public void setSearchCriteriaFactory(SearchCriteriaFactory searchCriteriaFactory) {
        this.searchCriteriaFactory = searchCriteriaFactory;
    }

    public void setMaxDependentReports(int maxDependentReports) {
        this.maxDependentReports = maxDependentReports;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setCheckDependentResourcesFor(List<String> checkDependentResourcesFor) {
        this.checkDependentResourcesFor = checkDependentResourcesFor;
    }

    public void setTempFolderName(String tempFolderName) {
        this.tempFolderName = tempFolderName;
    }

    public void setResourceTypeResolver(ResourceTypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    class DeleteOrderComparator implements Comparator<Resource> {
        public int compare(Resource o1, Resource o2) {
            int index1 = deleteOrder.indexOf(o1.getResourceType());
            int index2 = deleteOrder.indexOf(o2.getResourceType());
            return (index1 < index2 ? -1 : (index1 == index2 ? 0 : 1));
        }
    }
}
