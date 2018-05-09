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
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.war.model.JSONObject;
import com.jaspersoft.jasperserver.war.model.TreeDataProvider;
import com.jaspersoft.jasperserver.war.model.TreeNode;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

/**
 * @author Anton Fomin
 * @author Andriy Godovanets
 */
public abstract class BaseTreeDataProvider implements TreeDataProvider {
    private RepositoryService repositoryService;

    class Extra implements JSONObject {
        private String json;
        Extra(String description, String subType, String creator) {
            json = "{\"desc\":" + (description == null ? "null" : "\"" + description.replaceAll("\"","\\\\\"") + "\"") + ",\"subType\":\"" + subType + "\",\"creator\":\"" + creator + "\"}";
        }
        public String toJSONString() {
            return json;
        }
    }

    protected JSONObject newExtra(String description, String subType, String creator) {
        return new Extra(description, subType, creator);
    }

    public List getChildren(ExecutionContext executionContext,
            String parentUri, int depth) {

        TreeNode node = getNode(executionContext, parentUri, depth + 1);
        return node.getChildren();
    }

    public TreeNode getNode(ExecutionContext executionContext, String uri, int depth) {
        TreeNode node = createRoot(executionContext);
        String[] pathParts = uri.split("/");
        StringBuffer curPath = new StringBuffer();

        if ("/".equals(uri)) {
            return cutNodeHierarchyToGivenDepth(node, depth);
        }

        for (int i = 1; i < pathParts.length; i++) {
            curPath.append("/").append(pathParts[i]);
            for (TreeNode ch : (List<TreeNode>) node.getChildren()) {
                if (curPath.toString().equals(ch.getUriString())) {
                    node = ch;
                    break;
                }
            }
        }

        return cutNodeHierarchyToGivenDepth(node, depth);
    }

    protected abstract TreeNode createRoot(ExecutionContext executionContext);

    protected TreeNode cutNodeHierarchyToGivenDepth(TreeNode node, int depth) {
        if (node == null) {
            return null;
        }
        TreeNode retNode = node.clone(false);
        retNode.getChildren().clear();
        if (depth > 0 && node.getChildren() != null && node.getChildren().size() > 0) {
            for (TreeNode chNode : (List<TreeNode>) node.getChildren()) {
                retNode.getChildren().add(cutNodeHierarchyToGivenDepth(chNode, depth - 1));
            }
        }
        return retNode;
    }

    protected TreeNode getParentFolderNode(TreeNode parent, String uri) {
        String[] pathParts = uri.split("/");
        StringBuffer curPath = new StringBuffer();
        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext();
        for (int i = 1; i < pathParts.length - 1; i++) {
            boolean found = false;
            curPath.append("/").append(pathParts[i]);
            for (TreeNode ch : (List<TreeNode>) parent.getChildren()) {
                if (curPath.toString().equals(ch.getUriString())) {
                    parent = ch;
                    found = true;
                    break;
                }
            }
            if (!found) {
                try {
                    Folder f = repositoryService.getFolder(context, curPath.toString());
                    TreeNode n = new TreeNodeImpl(this,
                            f.getName(), f.getLabel(), f.getResourceType(), f.getURIString(), 1);
                    parent.getChildren().add(n);
                    parent = n;
                } catch (AccessDeniedException e){
                    // dummy node to be disposed (not  part of the tree)
                    return new TreeNodeImpl(this,   "", "", "", "", 1);
                }
            }
        }
        return parent;
    }

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }
}
