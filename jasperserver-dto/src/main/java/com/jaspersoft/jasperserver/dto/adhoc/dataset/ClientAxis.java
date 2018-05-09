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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 05.04.2016
 */
public class ClientAxis {
    private ClientAxisNode axisNode;
    private List<ClientMultiAxesDatasetLevel> levels = new ArrayList<ClientMultiAxesDatasetLevel>();

    public ClientAxis() {
    }

    public ClientAxis(ClientAxis axis) {
        axisNode = new ClientAxisNode(axis.getAxisNode());
        for (ClientMultiAxesDatasetLevel groupAxis : axis.getLevels()) {
            if (groupAxis instanceof ClientMultiAxesAggregationLevel) {
                levels.add(new ClientMultiAxesAggregationLevel((ClientMultiAxesAggregationLevel) groupAxis));
            } else {
                levels.add(new ClientMultiAxesGroupLevel((ClientMultiAxesGroupLevel) groupAxis));
            }
        }
    }

    public ClientAxisNode getAxisNode() {
        return axisNode;
    }

    public ClientAxis setAxisNode(ClientAxisNode axisNode) {
        this.axisNode = axisNode;
        return this;
    }

    @XmlElementWrapper(name = "levels")
    @XmlElements({
            @XmlElement(name = "level", type = ClientMultiAxesGroupLevel.class),
            @XmlElement(name = "aggregation", type = ClientMultiAxesAggregationLevel.class)})
    public List<ClientMultiAxesDatasetLevel> getLevels() {
        return levels;
    }

    public ClientAxis setLevels(List<ClientMultiAxesDatasetLevel> levels) {
        this.levels = levels;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientAxis that = (ClientAxis) o;

        if (axisNode != null ? !axisNode.equals(that.axisNode) : that.axisNode != null) return false;
        return !(levels != null ? !levels.equals(that.levels) : that.levels != null);

    }

    @Override
    public int hashCode() {
        int result = axisNode != null ? axisNode.hashCode() : 0;
        result = 31 * result + (levels != null ? levels.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientAxis{" +
                "axisNode=" + axisNode +
                ", levels=" + levels +
                '}';
    }
}
