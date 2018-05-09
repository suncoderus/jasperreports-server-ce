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
package com.jaspersoft.jasperserver.dto.adhoc.query.expansion;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andriy Godovanets
 */
public class ClientMemberExpansion implements ClientExpandable<List<String>> {
    public static final String PATH_SEPARATOR = "/";

    private boolean isExpanded;

    @NotNull
    private List<String> path;

    public ClientMemberExpansion() {
        // no op
    }

    public ClientMemberExpansion(ClientMemberExpansion expansion) {
        if (expansion != null) {
            this
                    .setExpanded(expansion.isExpanded())
                    .setPath(new ArrayList<String>(expansion.getPath()));
        }
    }

    @Override
    public ClientMemberExpansion deepClone() {
        return new ClientMemberExpansion(this);
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    public ClientMemberExpansion setExpanded(boolean expanded) {
        this.isExpanded = expanded;
        return this;
    }

    @XmlElementWrapper(name = "path")
    @XmlElement(name = "item")
    public List<String> getPath() {
        return path;
    }

    public ClientMemberExpansion setPath(List<String> path) {
        this.path = path;
        return this;
    }

    @Override
    public List<String> get() {
        return getPath();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientMemberExpansion)) return false;

        ClientMemberExpansion expansion = (ClientMemberExpansion) o;

        if (isExpanded != expansion.isExpanded) return false;
        return path.equals(expansion.path);

    }

    @Override
    public int hashCode() {
        int result = (isExpanded ? 1 : 0);
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientMemberExpansion{");
        sb.append("isExpanded=").append(isExpanded);
        sb.append(", path=").append(path);
        sb.append('}');
        return sb.toString();
    }
}
