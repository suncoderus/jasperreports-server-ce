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

import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.RepositorySecurityChecker;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.model.JSONObject;
import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * Implementation of TreeDataProvider. Provides Repository browsing.
 * It takes in account the current user's privileges and adds Extra Property
 * to each TreeNode which is an object that has property 'isWritable'.
 * On the client side you can access it by referencing to node.param.extra.isWritable
 * @see com.jaspersoft.jasperserver.war.model.TreeNode
 * @author asokolnikov
 *
 */
public class RepositoryTreeDataProviderImpl implements TreeDataProvider {
    
    private RepositoryService repositoryService;
    private RepositorySecurityChecker repositoryServiceSecurityChecker;
    private TreeDataFilter filter;
    
    /**
     * Extra Property bean for TreeNode.
     * Specific to Repository Tree Data Provider
     * @author asokolnikov
     *
     */
    private static class Permissions implements JSONObject {
        public boolean isWritable = true;
        public boolean isRemovable = true;
        public String toJSONString() {
//        	StringBuffer str = new StringBuffer("{");
//        	str.append((isWritable) ? "\"isWritable\":true" : "\"isWritable\":false");
//        	str.append((isRemovable) ? ",\"isRemovable\":true" : ",\"isRemovable\":false");
//        	str.append("}");
//
//            return str.toString();

            org.json.JSONObject jsonObject = new org.json.JSONObject();
            try {
                jsonObject.put("isWritable", isWritable);
                jsonObject.put("isRemovable", isRemovable);
            } catch (org.json.JSONException ignored) { }

            return jsonObject.toString();
        }
    }
    
    /**
     * Returns an instance of TreeNode for a given URI.
     * <b>depth</b> parameter controls how many levels of children
     * are going to be preloaded within the requested node 
     * (0 - no children preloaded, 1 - only immeadiate children preloaded, etc.)
     * @param executionContext ExecutionContext instance 
     * @param uri a unique string indentifying the node (uses "/node1/node11" convention)
     * @param depth children tree depth to be preloaded
     * @return TreeNode instance OR null of not found
     */
    public List getChildren(ExecutionContext executionContext, String parentUri, int depth) {
        TreeNode n = getNode(executionContext, parentUri, depth + 1);
        if (n != null) {
            return n.getChildren();
        }
        return null;
    }

    /**
     * Returns a list of TreeNode instances which are immediate children of 
     * a node identified by a given URI. Each node in the list may have its
     * children preloaded if depth is greater that 0
     * Returns an empty list if no children found. 
     * @param executionContext ExecutionContext instance 
     * @param parentUri a unique string indentifying the node
     * @param depth children tree depth to be preloaded
     * @return List of TreeNode instances OR null if parent not found
     */
    public TreeNode getNode(ExecutionContext executionContext, String uri, int depth) {
        
        Resource resource = repositoryService.getResource(executionContext, uri);
        if (resource != null) {
            return createNode(resource, false);
        }
        
        Folder folder = repositoryService.getFolder(executionContext, uri);
        if (folder != null) {
            TreeNode node = createNode(folder, true);
            if (depth > 0) {
                processFolder(node, depth - 1);
            }
            return node;
        }
        
        return null;
    }
    
    private TreeNode createNode(Resource resource, boolean isFolder) {
        Permissions extraProperty = new Permissions();
        extraProperty.isWritable = repositoryServiceSecurityChecker.isEditable(resource);
        extraProperty.isRemovable = repositoryServiceSecurityChecker.isRemovable(resource);
        if (isFolder) {
            return new TreeNodeImpl(this, 
                    resource.getName(), resource.getLabel(), 
                    resource.getResourceType(), resource.getURIString(),
                    1, extraProperty);
        }
        return new TreeNodeImpl(this, 
                resource.getName(), resource.getLabel(), 
                resource.getResourceType(), resource.getURIString(),
                extraProperty);
    }
    
    private void processFolder(TreeNode folder, int depth) {
        
        String folderURI = folder.getUriString();
        FilterCriteria criteria = FilterCriteria.createFilter();
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(folderURI));
        
        List folders = repositoryService.getSubFolders(null, folderURI);
        
        List resources = repositoryService.loadResourcesList(null, criteria);
        
        /*List allResources = new ArrayList();
        allResources.addAll(folders);
        allResources.addAll(resources);*/
        
        if (folders != null) {
            for (Iterator iter = folders.iterator(); iter.hasNext(); ) {
                Folder f = (Folder) iter.next();
                TreeNode n = createNode(f, true);
                if (filter == null || filter.filter(n)) {
                    folder.getChildren().add(n);
                    if (depth > 0) {
                        processFolder(n, depth - 1);
                    }
                }
            }
        }
        if (resources != null) {
            for (Iterator iter = resources.iterator(); iter.hasNext(); ) {
                Resource r = (Resource) iter.next();
                TreeNode n = createNode(r, false);
                if (filter == null || filter.filter(n)) {
                    folder.getChildren().add(n);
                }
            }
        }
        
    }
    
    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public RepositorySecurityChecker getRepositoryServiceSecurityChecker() {
        return repositoryServiceSecurityChecker;
    }

    public void setRepositoryServiceSecurityChecker(
            RepositorySecurityChecker repositoryServiceSecurityChecker) {
        this.repositoryServiceSecurityChecker = repositoryServiceSecurityChecker;
    }

    public TreeDataFilter getFilter() {
        return filter;
    }

    public void setFilter(TreeDataFilter filter) {
        this.filter = filter;
    }

}
