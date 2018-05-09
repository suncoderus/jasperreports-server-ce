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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vsabadosh
 * @version $Id$
 */
@XmlRootElement(name = ResourceMediaType.OLAP_UNIT_CLIENT_TYPE)
public class ClientOlapUnit extends ClientResource<ClientOlapUnit> {
    private String mdxQuery;
    private ClientReferenciableOlapConnection olapConnection;

    public ClientOlapUnit(ClientOlapUnit other) {
        this.mdxQuery = other.getMdxQuery();

        ClientReferenciableOlapConnection srcClientReferenciableOlapConnection = other.getOlapConnection();
        if (srcClientReferenciableOlapConnection != null){
            if (srcClientReferenciableOlapConnection instanceof ClientMondrianConnection){
                olapConnection = new ClientMondrianConnection((ClientMondrianConnection) srcClientReferenciableOlapConnection);
            } else if (srcClientReferenciableOlapConnection instanceof ClientReference){
                olapConnection = new ClientReference((ClientReference) srcClientReferenciableOlapConnection);
            } else if (srcClientReferenciableOlapConnection instanceof ClientSecureMondrianConnection){
                olapConnection = new ClientSecureMondrianConnection((ClientSecureMondrianConnection) srcClientReferenciableOlapConnection);
            } else if (srcClientReferenciableOlapConnection instanceof ClientXmlaConnection){
                olapConnection = new ClientXmlaConnection((ClientXmlaConnection) srcClientReferenciableOlapConnection);
            }
        }
    }

    public ClientOlapUnit() {
    }

    @XmlElements({
    /*ClientReference is included here to serve as resource reference*/
            @XmlElement(type = ClientReference.class, name = "olapConnectionReference"),
            @XmlElement(type = ClientMondrianConnection.class, name = "mondrianConnection"),
            @XmlElement(type = ClientSecureMondrianConnection.class, name = "secureMondrianConnection"),
            @XmlElement(type = ClientXmlaConnection.class, name = "xmlaConnection")})
    public ClientReferenciableOlapConnection getOlapConnection() {
        return olapConnection;
    }

    public ClientOlapUnit setOlapConnection(ClientReferenciableOlapConnection olapConnection) {
        this.olapConnection = olapConnection;
        return this;
    }

    public String getMdxQuery() {
        return mdxQuery;
    }

    public ClientOlapUnit setMdxQuery(String mdxQuery) {
        this.mdxQuery = mdxQuery;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientOlapUnit that = (ClientOlapUnit) o;

        if (mdxQuery != null ? !mdxQuery.equals(that.mdxQuery) : that.mdxQuery != null) return false;
        if (olapConnection != null ? !olapConnection.equals(that.olapConnection) : that.olapConnection != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mdxQuery != null ? mdxQuery.hashCode() : 0);
        result = 31 * result + (olapConnection != null ? olapConnection.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientOlapUnit{" +
                "mdxQuery='" + mdxQuery + '\'' +
                ", olapConnection=" + (olapConnection != null ? olapConnection.getUri() : "null") +
                ", version=" + getVersion() +
                ", permissionMask=" + getPermissionMask() +
                ", uri='" + getUri() + '\'' +
                ", label='" + getLabel() + '\'' +
                '}';
    }
}
