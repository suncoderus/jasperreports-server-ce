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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import org.apache.log4j.Logger;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.model.TreeNode;


/**
 * @author Anton Fomin
 * @author Andriy Godovanets
 */
public class DataSourceTreeDataProvider extends BaseTreeDataProvider {
    public static final Logger log = Logger.getLogger(DataSourceTreeDataProvider.class);
    private List<String> supportedClasses;


    @Override
    protected TreeNode createRoot(ExecutionContext executionContext) {
        List<ResourceLookup> lookups = new ArrayList<ResourceLookup>();
        for (String clazz : supportedClasses) {
            try {
                ResourceLookup lookupsArray[] = getRepositoryService().findResource(executionContext, FilterCriteria.createFilter(Class.forName(clazz)));
                if (lookupsArray != null) {
                    lookups.addAll(Arrays.asList(lookupsArray));
                }
            } catch (ClassNotFoundException e) {
                log.warn("DataSourceTreeDataProvider supported class not found: ", e);
            }
        }

        Folder rootFolder = getRepositoryService().getFolder(executionContext, "/");
        TreeNode root = new TreeNodeImpl(this, rootFolder.getName(), rootFolder.getLabel(), rootFolder.getResourceType(), rootFolder.getURIString(), 1);

        for (ResourceLookup lookup : lookups) {
            TreeNode parent = getParentFolderNode(root, lookup.getURIString());
            parent.getChildren().add(
                    new TreeNodeImpl(this, lookup.getName(), lookup.getLabel(), lookup.getResourceType(), lookup.getURIString()));
        }
        return root;
    }

    public void setSupportedClasses(List<String> supportedClasses) {
        this.supportedClasses = supportedClasses;
    }
}