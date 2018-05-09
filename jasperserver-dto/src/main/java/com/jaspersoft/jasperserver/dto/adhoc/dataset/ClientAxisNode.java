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
package com.jaspersoft.jasperserver.dto.adhoc.dataset;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 12/7/13
 *
 * @author stas
 */
@XmlRootElement(name = "axisNode")
public class ClientAxisNode implements Serializable {
    private Integer memberIdx;
    private Integer dataIdx;
    private List<ClientAxisNode> children;
    private boolean isAll = false;

    public ClientAxisNode() {
    }

    public ClientAxisNode(final ClientAxisNode node) {
        memberIdx = node.getMemberIdx();
        dataIdx = node.getDataIdx();
        if (node.getChildren() != null) {
            this.children = new ArrayList<ClientAxisNode>(node.getChildren().size());
            for (ClientAxisNode n : node.getChildren()) {
                this.children.add(new ClientAxisNode(n));
            }
        }
        this.isAll = node.isAll();
    }

    public ClientAxisNode setChildren(List<ClientAxisNode> children) {
        this.children = children;
        return this;
    }

    public Integer getDataIdx() {
        return dataIdx;
    }

    public ClientAxisNode setDataIdx(Integer dataIdx) {
        this.dataIdx = dataIdx;
        return this;
    }

    @XmlElement(name = "memberIdx")
    public Integer getMemberIdx() {
        return memberIdx;
    }

    public ClientAxisNode setMemberIdx(Integer memberIdx) {
        this.memberIdx = memberIdx;
        return this;
    }

    @XmlElementWrapper(name = "children")
    @XmlElement(nillable = false, required = false, name = "axisNode")
    public List<ClientAxisNode> getChildren() {
        return children;
    }

    @XmlElement(name = "all")
    public boolean isAll() {
        return isAll;
    }

    public ClientAxisNode setAll(boolean isAll) {
        this.isAll = isAll;
        return this;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "memberIdx=" + memberIdx +
                ", dataIdx=" + dataIdx +
                ", isAll=" + isAll +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientAxisNode axisNode = (ClientAxisNode) o;

        if (isAll != axisNode.isAll) return false;
        if (memberIdx != null ? !memberIdx.equals(axisNode.memberIdx) : axisNode.memberIdx != null) return false;
        if (dataIdx != null ? !dataIdx.equals(axisNode.dataIdx) : axisNode.dataIdx != null) return false;
        return !(children != null ? !children.equals(axisNode.children) : axisNode.children != null);

    }

    @Override
    public int hashCode() {
        int result = memberIdx != null ? memberIdx.hashCode() : 0;
        result = 31 * result + (dataIdx != null ? dataIdx.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (isAll ? 1 : 0);
        return result;
    }
}
