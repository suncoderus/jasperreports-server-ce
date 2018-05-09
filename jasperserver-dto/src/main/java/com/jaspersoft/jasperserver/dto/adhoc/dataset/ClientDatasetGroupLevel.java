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
public class ClientDatasetGroupLevel extends AbstractClientDatasetLevel {
    private String fieldRef;
    private String type;
    private List<String> members;

    public ClientDatasetGroupLevel() {}

    public ClientDatasetGroupLevel(ClientDatasetGroupLevel level) {
        super(level);
        this.fieldRef = level.getFieldRef();
        if (level.getMembers() != null) {
            this.members = new ArrayList<String>(level.getMembers());
        }
    }

    @XmlElement(name="reference")
    public String getFieldRef() {
        return fieldRef;
    }

    public void setFieldRef(String name) {
        this.fieldRef = name;
    }

    @XmlElementWrapper(name="members")
    @XmlElement(name="member")
    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    @XmlElementWrapper(name = "aggregations")
    @XmlElement(name = "aggregation")
    public List<ClientDatasetFieldReference> getFieldRefs() {
        return super.getFieldRefs();
    }

    @XmlElement(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientDatasetGroupLevel that = (ClientDatasetGroupLevel) o;

        if (fieldRef != null ? !fieldRef.equals(that.fieldRef) : that.fieldRef != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return !(members != null ? !members.equals(that.members) : that.members != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fieldRef != null ? fieldRef.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (members != null ? members.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientDatasetGroupLevel{" +
                "fieldRef='" + fieldRef + '\'' +
                ", type='" + type + '\'' +
                ", members=" + members +
                '}';
    }
}
