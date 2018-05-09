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

package com.jaspersoft.jasperserver.war.model.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Retrieves File Resources by given file types.
 *
 * @author Andriy Godovanets
 */
public class FileResourcesTreeDataProvider extends BaseTreeDataProvider {
    private static final Logger log = Logger.getLogger(FileResourcesTreeDataProvider.class);
    private List<String> fileTypes;

    @Override
    protected TreeNode createRoot(ExecutionContext executionContext) {
        List<ResourceLookup> lookups = new ArrayList<ResourceLookup>();
        for (String fileType : fileTypes) {
            FilterCriteria criteria = FilterCriteria.createFilter(FileResource.class);
            criteria.addFilterElement(FilterCriteria.createPropertyEqualsFilter("fileType", fileType));

            ResourceLookup lookupsArray[] = getRepositoryService().findResource(executionContext, criteria);
            if (lookupsArray != null) {
                lookups.addAll(Arrays.asList(lookupsArray));
            }
        }

        Folder rootFolder = getRepositoryService().getFolder(executionContext, "/");
        TreeNode root = new TreeNodeImpl(this, rootFolder.getName(), rootFolder.getLabel(), rootFolder.getResourceType(), rootFolder.getURIString(), 1);

        for (ResourceLookup realm : lookups) {
            TreeNode parent = getParentFolderNode(root, realm.getURIString());
            parent.getChildren().add(new TreeNodeImpl(this, realm.getName(),
                    realm.getLabel(), realm.getResourceType(), realm.getURIString()));
        }
        return root;
    }

    public void setFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
    }
}