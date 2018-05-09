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

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ClientDashboardFoundation {
    private String id;
    private String description;
    private String layout;
    private String wiring;
    private String components;

    public String getId() {
        return id;
    }

    public ClientDashboardFoundation setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ClientDashboardFoundation setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLayout() {
        return layout;
    }

    public ClientDashboardFoundation setLayout(String layout) {
        this.layout = layout;
        return this;
    }

    public String getWiring() {
        return wiring;
    }

    public ClientDashboardFoundation setWiring(String wiring) {
        this.wiring = wiring;
        return this;
    }

    public String getComponents() {
        return components;
    }

    public ClientDashboardFoundation setComponents(String components) {
        this.components = components;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDashboardFoundation that = (ClientDashboardFoundation) o;

        if (components != null ? !components.equals(that.components) : that.components != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (layout != null ? !layout.equals(that.layout) : that.layout != null) return false;
        if (wiring != null ? !wiring.equals(that.wiring) : that.wiring != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (layout != null ? layout.hashCode() : 0);
        result = 31 * result + (wiring != null ? wiring.hashCode() : 0);
        result = 31 * result + (components != null ? components.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientDashboardFoundation{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", layout='" + layout + '\'' +
                ", wiring='" + wiring + '\'' +
                ", components='" + components + '\'' +
                '}';
    }
}
