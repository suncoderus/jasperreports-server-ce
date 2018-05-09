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
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * @author Alexander Povshik
 * @author Andriy Godovanets
 */
public class TypedTreeDataProvider extends BaseTreeDataProvider {
    public static final Logger log = Logger.getLogger(TypedTreeDataProvider.class);
    private List<String> supportedClasses;
    private TreeDataFilter filter;
    private List<String> supportedCustomReportDataSourceServices;
    private final String customReportDataSourceClass = "com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource";
    private EngineService engineService;

    public TreeDataFilter getFilter() {
        return filter;
    }

    public void setFilter(TreeDataFilter filter) {
        this.filter = filter;
    }

    @Override
    protected TreeNode createRoot(ExecutionContext executionContext) {
        List<ResourceLookup> lookups = new ArrayList<ResourceLookup>();
        for (String clazz : supportedClasses) {
            try {
                ResourceLookup lookupsArray[] = getRepositoryService().findResource(executionContext, FilterCriteria.createFilter(Class.forName(clazz)));
                if (lookupsArray != null) {
                    // not all custom data sources are supported in all cases
                    // need to determine by the custom report data source service
                    if ((supportedCustomReportDataSourceServices != null) && (supportedCustomReportDataSourceServices.size() > 0) &&  (supportedClasses.contains(customReportDataSourceClass))) {
                        for (ResourceLookup resourceLookup : lookupsArray) {
                            if (resourceLookup.getResourceType().equals(customReportDataSourceClass)) {
                                CustomReportDataSource customReportDataSource = (CustomReportDataSource) getRepositoryService().getResource(executionContext, resourceLookup.getURI());
                                if (supportedCustomReportDataSourceServices.contains(customReportDataSource.getServiceClass()) ||
                                		isCustomDomainMetadataProvider(customReportDataSource)) {
                                    lookups.add(resourceLookup);
                                }
                            } else {
                               lookups.add(resourceLookup);
                            }
                        }
                    } else {
                        lookups.addAll(Arrays.asList(lookupsArray));
                    }
                }
            } catch (ClassNotFoundException e) {
                log.warn("TypedTreeDateProvider supported class not found: ", e);
            }
        }

        Folder rootFolder = getRepositoryService().getFolder(executionContext, "/");
        TreeNode root = new TreeNodeImpl(this, rootFolder.getName(), rootFolder.getLabel(), rootFolder.getResourceType(), rootFolder.getURIString(), 1);

        for (ResourceLookup lookup : lookups) {
            TreeNode parent = getParentFolderNode(root, lookup.getURIString());
            parent.getChildren().add(
                    new TreeNodeImpl(this, lookup.getName(), lookup.getLabel(), lookup.getResourceType(), lookup.getURIString()));
        }
        return filterTree(root);
    }

    /**
     * Checks if the datasource provides metadata.
     */
    private boolean isCustomDomainMetadataProvider(CustomReportDataSource customReportDataSource) {
        if (engineService == null) {
            return false;
        }
        try {
            return engineService.isCustomDomainMetadataProvider(customReportDataSource);
        } catch (Exception ex) {
            log.error(ex);
            return false;
        }
    }

    private TreeNode filterTree(TreeNode tree) {
        if(filter == null) {
            return tree;
        }
        List<TreeNode> children = tree.getChildren();
        if(children == null) {
            return tree;
        }
        Iterator<TreeNode> i = children.iterator();
        while(i.hasNext()) {
            TreeNode child = i.next();
            if(!filter.filter(child)) {
                i.remove();
            } else {
                filterTree(child);
            }
        }
        return tree;
    }

    public void setSupportedClasses(List<String> supportedClasses) {
        this.supportedClasses = supportedClasses;
    }

    public void setSupportedCustomReportDataSourceServices(List<String> supportedCustomReportDataSourceServices) {
        this.supportedCustomReportDataSourceServices = supportedCustomReportDataSourceServices;
    }

	public void setEngineService(EngineService engineService) {
		this.engineService = engineService;
	}
}