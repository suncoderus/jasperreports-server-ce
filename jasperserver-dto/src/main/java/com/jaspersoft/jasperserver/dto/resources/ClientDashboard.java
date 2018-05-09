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

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk, Zakhar.Tomchenko
 * @version $Id: ClientDashboard.java 36344 2013-12-03 17:36:14Z ztomchenco $
 */
@XmlRootElement(name = ResourceMediaType.DASHBOARD_CLIENT_TYPE)
public class ClientDashboard extends ClientResource<ClientDashboard> {
    private List<ClientDashboardFoundation> foundations;
    private List<ClientDashboardResource> resources;
    private String defaultFoundation;

    public List<ClientDashboardFoundation> getFoundations() {
        return foundations;
    }

    public ClientDashboard setFoundations(List<ClientDashboardFoundation> foundations) {
        this.foundations = foundations;
        return this;
    }

    public List<ClientDashboardResource> getResources() {
        return resources;
    }

    public String getDefaultFoundation() {
        return defaultFoundation;
    }

    public void setDefaultFoundation(String defaultFoundation) {
        this.defaultFoundation = defaultFoundation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientDashboard that = (ClientDashboard) o;

        if (defaultFoundation != null ? !defaultFoundation.equals(that.defaultFoundation) : that.defaultFoundation != null)
            return false;
        if (foundations != null ? !foundations.equals(that.foundations) : that.foundations != null) return false;
        if (resources != null ? !resources.equals(that.resources) : that.resources != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (foundations != null ? foundations.hashCode() : 0);
        result = 31 * result + (resources != null ? resources.hashCode() : 0);
        result = 31 * result + (defaultFoundation != null ? defaultFoundation.hashCode() : 0);
        return result;
    }

    public ClientDashboard setResources(List<ClientDashboardResource> resources) {
        this.resources = resources;
        return this;
    }

    @Override
    public String toString() {
        return "ClientDashboard{" +
                "version=" + getVersion() +
                ", permissionMask=" + getPermissionMask() +
                ", uri='" + getUri() + '\'' +
                ", label='" + getLabel() + '\'' +
                '}';
    }
}