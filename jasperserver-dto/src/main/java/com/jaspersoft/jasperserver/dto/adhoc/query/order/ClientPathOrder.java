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
package com.jaspersoft.jasperserver.dto.adhoc.query.order;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Measure sorting used for aggregated datasets.
 * Along with QueryAggregatedField meta-info it stores group sorting path,
 * for sorting measure just in some subgroup
 *
 * @author Andriy Godovanets
 */
public class ClientPathOrder implements ClientOrder {
    @NotNull
    private Boolean isAscending;

    @NotNull
    @Size(min = 1)
    private List<String> path;

    public ClientPathOrder() {
        // no op
    }

    public ClientPathOrder(ClientPathOrder sorting) {
        if (sorting == null) {
            throw new IllegalArgumentException("Original sorting object is null");
        }
        if (sorting.isAscending() != null) {
            setAscending(sorting.isAscending());
        }
        if (sorting.getPath() != null) {
            this.path = new ArrayList<String>(sorting.path);
        }
    }

    @Override
    public ClientPathOrder deepClone() {
        return new ClientPathOrder(this);
    }

    /**
     *
     * @return Path to the member to sort by
     */
    @XmlElementWrapper(name = "path")
    @XmlElement(name = "item")
    public List<String> getPath() {
        return path;
    }

    public ClientPathOrder setPath(List<String> path) {
        this.path = path;
        return this;
    }

    @Override
    public Boolean isAscending() {
        return isAscending;
    }

    public ClientPathOrder setAscending(Boolean ascending) {
        isAscending = ascending;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientPathOrder that = (ClientPathOrder) o;

        return getPath() != null ? getPath().equals(that.getPath()) : that.getPath() == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.path != null ? this.path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MeasureSorting{" +
                "isAscending=" + isAscending() +
                ", path=" + path +
                "}";
    }
}
