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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * Inclusive filter based on list of URIs.
 * It includes only resource with exact uri, its parent and all its children
 * @author asokolnikov
 *
 */
public class UriInclusiveTreeDataFilterImpl implements TreeDataFilter, Serializable {
    
    private List uriList;

    /**
     * Returns true if node is a resource, a parent of a resource, or a child 
     * of a resource with uri which is in uriList.
     * Returns false otherwise.
     * Returns false if no uriList configured.
     */
    public boolean filter(TreeNode node) {
        if (uriList != null) {
            String nodeUri = node.getUriString();
            for (Iterator iter = uriList.iterator(); iter.hasNext(); ) {
                String uri = (String) iter.next();
                if (nodeUri.indexOf(uri) == 0 || uri.indexOf(nodeUri) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public List getUriList() {
        return uriList;
    }

    public void setUriList(List uriList) {
        this.uriList = uriList;
    }

}
