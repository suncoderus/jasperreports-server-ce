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
import java.util.List;

import com.jaspersoft.jasperserver.war.model.TreeDataFilter;
import com.jaspersoft.jasperserver.war.model.TreeNode;

/**
 * Implementation of TreeDataFilter.
 * Excludes a TreeNode instance out of a data set if its type is one of the
 * configured types
 * @author asokolnikov
 *
 */
public class TypeExclusiveTreeDataFilterImpl implements TreeDataFilter,Serializable {

    private List excludeTypesList;

    /**
     * Returns false if excludeTypesList contains a given node type.
     * Returns true otherwise
     */
    public boolean filter(TreeNode node) {
        if (excludeTypesList.contains(node.getType())) {
            return false;
        }
        return true;
    }

    public List getExcludeTypesList() {
        return excludeTypesList;
    }

    public void setExcludeTypesList(List excludeTypesList) {
        this.excludeTypesList = excludeTypesList;
    }

}
