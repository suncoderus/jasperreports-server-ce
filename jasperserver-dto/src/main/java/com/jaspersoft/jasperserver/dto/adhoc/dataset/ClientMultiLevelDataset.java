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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
@XmlRootElement(name = "multiLevelDataset")
public class ClientMultiLevelDataset {
    private Integer counts;
    private List<AbstractClientDatasetLevel> levels;
    private List<AbstractClientDatasetLevelNode> levelDataNodes;

    public ClientMultiLevelDataset() {
    }

    public ClientMultiLevelDataset(ClientMultiLevelDataset other) {
        this.counts = other.getCounts();

        if (other.getLevels() != null) {
            this.levels = new ArrayList<AbstractClientDatasetLevel>();
            for (AbstractClientDatasetLevel clientDatasetLevel : other.getLevels()) {
                AbstractClientDatasetLevel level = null;
                if (clientDatasetLevel instanceof ClientDatasetAllLevel) {
                    level = new ClientDatasetAllLevel((ClientDatasetAllLevel)clientDatasetLevel);
                } else if (clientDatasetLevel instanceof ClientDatasetGroupLevel) {
                    level = new ClientDatasetGroupLevel((ClientDatasetGroupLevel)clientDatasetLevel);
                } else if (clientDatasetLevel instanceof ClientDatasetDetailLevel) {
                    level = new ClientDatasetDetailLevel((ClientDatasetDetailLevel)clientDatasetLevel);
                }
                this.levels.add(level);
            }
        }
        if (other.getLevelDataNodes() != null) {
            this.levelDataNodes = new ArrayList<AbstractClientDatasetLevelNode>();
            for (AbstractClientDatasetLevelNode datasetLevelNode : other.getLevelDataNodes()) {
                AbstractClientDatasetLevelNode levelNode = null;
                if (datasetLevelNode instanceof ClientDatasetAllLevelNode) {
                    levelNode = new ClientDatasetAllLevelNode((ClientDatasetAllLevelNode)datasetLevelNode);
                } else if (datasetLevelNode instanceof ClientDatasetGroupLevelNode) {
                    levelNode = new ClientDatasetGroupLevelNode((ClientDatasetGroupLevelNode)datasetLevelNode);
                } else if (datasetLevelNode instanceof ClientDatasetDetailLevelNode) {
                    levelNode = new ClientDatasetDetailLevelNode((ClientDatasetDetailLevelNode)datasetLevelNode);
                }
                this.levelDataNodes.add(levelNode);
            }
        }
    }

    @XmlElement(name = "counts")
    public Integer getCounts() {
        return counts;
    }

    public ClientMultiLevelDataset setCounts(Integer counts) {
        this.counts = counts;
        return this;
    }

    @XmlElementWrapper(name = "levels")
    @XmlElements({
            @XmlElement(name = "all", type = ClientDatasetAllLevel.class),
            @XmlElement(name = "group", type = ClientDatasetGroupLevel.class),
            @XmlElement(name = "detail", type = ClientDatasetDetailLevel.class)})
    public List<AbstractClientDatasetLevel> getLevels() {
        return levels;
    }

    public ClientMultiLevelDataset setLevels(List<AbstractClientDatasetLevel> levels) {
        this.levels = levels;
        return this;
    }

    @XmlElementWrapper(name = "levelDataNodes")
    @XmlElements({
            @XmlElement(name = "all", type = ClientDatasetAllLevelNode.class),
            @XmlElement(name = "group", type = ClientDatasetGroupLevelNode.class),
            @XmlElement(name = "detail", type = ClientDatasetDetailLevelNode.class)})
    public List<AbstractClientDatasetLevelNode> getLevelDataNodes() {
        return levelDataNodes;
    }

    public void setLevelDataNodes(List<AbstractClientDatasetLevelNode> levelDataNodes) {
        this.levelDataNodes = levelDataNodes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientMultiLevelDataset that = (ClientMultiLevelDataset) o;

        if (counts != null ? !counts.equals(that.counts) : that.counts != null) return false;
        if (levelDataNodes != null ? !levelDataNodes.equals(that.levelDataNodes) : that.levelDataNodes != null)
            return false;
        if (levels != null ? !levels.equals(that.levels) : that.levels != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = counts != null ? counts.hashCode() : 0;
        result = 31 * result + (levels != null ? levels.hashCode() : 0);
        result = 31 * result + (levelDataNodes != null ? levelDataNodes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientMultiLevelDataset{" +
                "counts=" + counts +
                ", levels=" + levels +
                ", levelDataNodes=" + levelDataNodes +
                '}';
    }
}
