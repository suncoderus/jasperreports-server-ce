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
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

import java.util.*;

import com.jaspersoft.jasperserver.search.common.ItemsExistException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for repository search services.
 *
 * @author Stas Chubar
 */
public class BaseService {
    private static final Log log = LogFactory.getLog(BaseService.class);

    protected RepositoryService repositoryService;

    //What is folderUri vs parentFolderUri? Should be eventually documented.
    protected boolean isObjectsLabelsExist(String parentFolderUri, Set<String> objectsLabels, String folderUri) {

        return !getExistingObjectLabels(parentFolderUri, objectsLabels, folderUri).isEmpty();
    }

    protected Set<String> getExistingObjectLabels(String parentFolderUri, Set<String> objectsLabels, String folderUri) {
        Set<String> existingLabels = new HashSet<String>();
        if (objectsLabels.isEmpty()) {
            return existingLabels;
        }
        try {
            List repoFolderList = repositoryService.getSubFolders(null, parentFolderUri);

            FilterCriteria criteria = FilterCriteria.createFilter();
            criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentFolderUri));
            criteria.addFilterElement(FilterCriteria.createPropertyInFilter("label", objectsLabels.toArray(), true));

            List resources = repositoryService.loadResourcesList(null, criteria);
            repoFolderList.addAll(resources);

            for (String objectLabel : objectsLabels) {
                if (isLabelExist(objectLabel, repoFolderList, folderUri)) {
                    existingLabels.add(objectLabel);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return existingLabels;
    }


        private boolean isLabelExist(String objectLabel, List repoFolderList, String folderUri) {
        for (int i=0; i<repoFolderList.size(); i++) {
            if (repoFolderList.get(i) instanceof FolderImpl) {
                FolderImpl repoFolder = (FolderImpl)repoFolderList.get(i);

                if (folderUri != null && folderUri.equals(repoFolder.getURIString())) {
                    continue;
                }

                if (objectLabel.equalsIgnoreCase(repoFolder.getLabel())) {
                    return true;
                }
            } else if (repoFolderList.get(i) instanceof ResourceLookupImpl) {
                ResourceLookupImpl res = (ResourceLookupImpl)repoFolderList.get(i);

                if (folderUri != null && folderUri.equals(res.getURIString())) {
                    continue;
                }

                if (objectLabel.equalsIgnoreCase(res.getLabel())) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void ensureObjectLabelsNew(String parentFolderUri, Set<String> objectLabel) throws JSException, ItemsExistException {
        Set<String> existingLabels = getExistingObjectLabels(parentFolderUri, objectLabel, null);
        if(existingLabels.isEmpty()) {
            return;
        }
        throw new ItemsExistException(existingLabels);
    }

    protected boolean isObjectsLabelsExist(String parentFolderUri, Set<String> objectLabel) {
        return isObjectsLabelsExist(parentFolderUri, objectLabel, null);
    }

    protected boolean isObjectLabelExist(String parentFolderUri, String objectLabel, String folderUri) {
        Set<String> set = new HashSet<String>();
        set.add(objectLabel);

        return isObjectsLabelsExist(parentFolderUri, set, folderUri);
    }

    protected boolean isObjectLabelExist(String parentFolderUri, String objectLabel) {
        return isObjectLabelExist(parentFolderUri, objectLabel, null);
    }

    protected boolean isLabelsUnique(Map<String, Resource> resourceMap) {
        Set<String> labels = new HashSet<String>();

        for (Resource resource : resourceMap.values()) {
            labels.add(resource.getLabel().toLowerCase());
        }

        return resourceMap.size() == labels.size();
    }

    protected Map<String, Resource> getResourcesWithUniqueName(Map<String, Resource> resourceMap) {
        Map<String, Resource> resultMap = new HashMap<String, Resource>();
        Set<String> selected = new HashSet<String>();

        for (Map.Entry<String, Resource> entry : resourceMap.entrySet()) {
            String name = entry.getValue().getName();

            if (selected.contains(name)) {
                continue;
            }

            selected.add(name);
            resultMap.put(entry.getKey(), entry.getValue());
        }

        return resultMap;
    }

    protected Map<String, Resource> getResourceMap(Set<String> resourceUris) {
        Map<String, Resource> resourceMap = new HashMap<String, Resource>();

        for (String uri : resourceUris) {
            resourceMap.put(uri, repositoryService.getResource(null, uri));
        }

        return resourceMap;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
}
