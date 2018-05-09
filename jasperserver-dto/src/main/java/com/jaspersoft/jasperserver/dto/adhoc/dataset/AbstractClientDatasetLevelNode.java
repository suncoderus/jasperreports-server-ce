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
import javax.xml.bind.annotation.XmlElements;
import java.util.List;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public abstract class AbstractClientDatasetLevelNode<T extends AbstractClientDatasetLevelNode<T, Data>, Data> {
    private List<AbstractClientDatasetLevelNode> children;

    public abstract Data getData();

    @XmlElementWrapper(name = "children")
    @XmlElements({
            @XmlElement(name = "all", type = ClientDatasetAllLevelNode.class),
            @XmlElement(name = "group", type = ClientDatasetGroupLevelNode.class),
            @XmlElement(name = "detail", type = ClientDatasetDetailLevelNode.class)})
    public List<AbstractClientDatasetLevelNode> getChildren() {
        return children;
    }

    @SuppressWarnings("unchecked")
    public T setChildren(List<AbstractClientDatasetLevelNode> children) {
        this.children = children;
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractClientDatasetLevelNode<?, ?> levelNode = (AbstractClientDatasetLevelNode<?, ?>) o;

        return !(children != null ? !children.equals(levelNode.children) : levelNode.children != null);
    }

    @Override
    public int hashCode() {
        return children != null ? children.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AbstractClientDatasetLevelNode{" +
                "data=" + getData() +
                ", children=" + children +
                '}';
    }
}
