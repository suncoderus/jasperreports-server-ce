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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */

public class ClientDatasetDetailLevelNode extends AbstractClientDatasetLevelNode<ClientDatasetDetailLevelNode, List<String[]>> {

    private List<String[]> data;

    public ClientDatasetDetailLevelNode() {
    }

    public ClientDatasetDetailLevelNode(ClientDatasetDetailLevelNode other) {
        if (other.getData() != null) {
            this.data = new ArrayList<String[]>();
            for (String[] detail : other.getData()) {
                this.data.add(Arrays.copyOf(detail, detail.length));
            }
        }
    }

    @Override
    @XmlElementWrapper(name = "data")
    @XmlElement(name = "row")
    public List<String[]> getData() {
        return this.data;
    }

    public ClientDatasetDetailLevelNode setData(List<String[]> data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "ClientDatasetDetailLevelNode{} " + super.toString();
    }
}
