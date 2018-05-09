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
package com.jaspersoft.jasperserver.dto.resources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ClientDashboardResource {
    private String name;
    private String type;
    private ClientReferenceable resource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElements({
            /*ClientReference is included here to serve as resource reference*/
            @XmlElement(type = ClientReference.class, name = "resourceReference"),
            @XmlElement(type = ClientFile.class, name = "file"),
            @XmlElement(type = ClientInputControl.class, name = "inputControl"),
            @XmlElement(type = ClientReportUnit.class, name = "ClientReportUnit"),
            @XmlElement(type = ClientAdhocDataView.class, name = "ClientAdhocDataView")
    })
    public ClientReferenceable getResource() {
        return resource;
    }

    public void setResource(ClientReferenceable resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDashboardResource that = (ClientDashboardResource) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (resource != null ? !resource.equals(that.resource) : that.resource != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientDashboardResource{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", resource=" + resource +
                '}';
    }
}
