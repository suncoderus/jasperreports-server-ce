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
import java.util.List;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class ClientDatasetGroupLevelNode extends AbstractClientDatasetLevelNode<ClientDatasetGroupLevelNode, List<String>> {
    private int memberIdx;
    private List<String> data;

    public ClientDatasetGroupLevelNode() {

    }

    @Override
    @XmlElementWrapper(name = "data")
    @XmlElement(name = "item")
    public List<String> getData() {
        return this.data;
    }

    public ClientDatasetGroupLevelNode setData(List<String> data) {
        this.data = data;
        return this;
    }

    public ClientDatasetGroupLevelNode(ClientDatasetGroupLevelNode other) {
        if (other.getData() != null) {
            this.data = new ArrayList<String>(other.getData());
        }
        this.memberIdx = other.getMemberIdx();
    }

    @XmlElement(name="memberIdx")
    public int getMemberIdx() {
        return memberIdx;
    }

    public ClientDatasetGroupLevelNode setMemberIdx(int memberIdx) {
        this.memberIdx = memberIdx;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientDatasetGroupLevelNode that = (ClientDatasetGroupLevelNode) o;

        if (memberIdx != that.memberIdx) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + memberIdx;
        return result;
    }

    @Override
    public String toString() {
        return "ClientDatasetGroupLevelNode{" +
                "memberIdx=" + memberIdx +
                "} " + super.toString();
    }
}
