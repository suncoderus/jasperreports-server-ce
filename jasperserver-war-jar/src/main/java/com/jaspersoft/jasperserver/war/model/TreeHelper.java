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
package com.jaspersoft.jasperserver.war.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

/**
 * This class provides a set of convenient static methods to make 
 * tree related operation easier to perform
 * 
 * @author asokolnikov
 */
public class TreeHelper {
    
    /**
     * Returns a subtree from a given root URI. Subtree will include
     * all child nodes for a given prefetchedUri list. 
     * If a uri does not belong to rootUri subtree, it gets ignored.
     * Root Node and each intermediate node will have all its immediate children loaded.
     * 
     * Typical example for using the method could be UI which requires 
     * to show some nodes selected after tree gets displayed. It guarantees
     * that selected nodes are fetched, otherwise children get loaded on demand.
     * 
     * @param executionContext ExecutionContext instance
     * @param dataProvider Data Provider for a tree
     * @param rootUri the root of requested subtree (could be "/")
     * @param prefetchedUris list of all children at any hierarchy depth requested to be loaded
     * @return root node for subtree
     */
    public static TreeNode getSubtree(ExecutionContext executionContext, 
            TreeDataProvider dataProvider, String rootUri, List prefetchedUris, int depth) {
        
        if (rootUri.length() == 0 /*|| rootUri.charAt(rootUri.length() - 1) != '/'*/) {
            rootUri = rootUri + '/';
        }
     
        TreeNode root = dataProvider.getNode(executionContext, rootUri, 0);
        if (root == null) {
            return null;
        }
        
        List list = new ArrayList(prefetchedUris);
        Collections.sort(list);
        
        MiniNode logicalTree = new MiniNode(rootUri);
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            String uri = (String) iter.next();
            if (uri.indexOf(rootUri) != 0) {
                // node is not a child of given root
                iter.remove();
            } else {
                int k = rootUri.length();
                MiniNode cn = logicalTree;
                while (k < uri.length()) {
                    int i = uri.indexOf('/', k);
                    String cu = (i >= 0) ? uri.substring(0, i) : uri;
                    MiniNode n = (MiniNode) cn.getChildren().get(cu);
                    if (n == null) {
                        n = new MiniNode(cu);
                        cn.getChildren().put(cu, n);
                    }
                    cn = n;
                    if (i < 0) {
                        break;
                    }
                    k = i + 1;
                }
            }
        }
        
        processMiniNode(executionContext, dataProvider, root, logicalTree, depth);
        
        return root;
    }
    
    private static class MiniNode {
        private String uri;
        private Map/*<String uri, MiniNode node>*/ children = new HashMap();
        MiniNode (String uri) {
            this.uri = uri;
        }
        Map getChildren() {
            return children;
        }
    }
    
//    private static void processMiniNode(ExecutionContext executionContext,
//            TreeDataProvider dataProvider, TreeNode root, MiniNode node) {
//
//        List children = dataProvider.getChildren(executionContext, root.getUriString(), 0);
//        if (children != null) {
//            root.getChildren().addAll(children);
//        } else {
//            return;
//        }
//        for (Iterator iter = node.getChildren().keySet().iterator(); iter.hasNext(); ) {
//            String uri = (String) iter.next();
//            MiniNode ch = (MiniNode) node.getChildren().get(uri);
//            TreeNode cn = findChildByUri(root, uri);
//            if (cn != null && !ch.getChildren().isEmpty()) {
//                processMiniNode(executionContext, dataProvider, cn, ch);
//            }
//        }
//    }

    private static void processMiniNode(ExecutionContext executionContext,
            TreeDataProvider dataProvider, TreeNode root, MiniNode node, int depth) {

        List children = dataProvider.getChildren(executionContext, root.getUriString(), 0);
        if (children != null) {
//            root.getChildren().addAll(children);
            List newChildren = new ArrayList(children);
            root.getChildren().clear();
            root.getChildren().addAll(newChildren);
        } else {
            return;
        }
        for (Iterator iter = node.getChildren().keySet().iterator(); iter.hasNext(); ) {
            String uri = (String) iter.next();
            MiniNode ch = (MiniNode) node.getChildren().get(uri);
            TreeNode cn = findChildByUri(root, uri);
            if (depth > 0) {
                List nodeChildren = dataProvider.getChildren(executionContext, cn.getUriString(), 0);
                if (nodeChildren != null) {
                    cn.getChildren().addAll(nodeChildren);
                }
            }
            if (cn != null && !ch.getChildren().isEmpty()) {
                processMiniNode(executionContext, dataProvider, cn, ch, depth);
            }
        }
    }
    
    private static TreeNode findChildByUri(TreeNode parent, String uri) {
        List children = parent.getChildren();
        if (children != null) {
            for (Iterator iter = children.iterator(); iter.hasNext(); ) {
                TreeNode cn = (TreeNode) iter.next();
                if (cn.getUriString().equals(uri)) {
                    return cn;
                }
            }
        }
        return null;
    }

}
