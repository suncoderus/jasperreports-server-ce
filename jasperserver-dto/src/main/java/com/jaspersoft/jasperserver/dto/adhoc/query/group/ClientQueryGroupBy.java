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
package com.jaspersoft.jasperserver.dto.adhoc.query.group;

import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxis;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxisEnum;
import com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckAllGroupPosition;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andriy Godovanets
 */
@XmlRootElement
public class ClientQueryGroupBy implements ClientGroupBy<ClientGroupAxis> {
    @Valid
    @CheckAllGroupPosition
    private List<ClientQueryGroup> groups;

    public ClientQueryGroupBy() {
        groups = new ArrayList<ClientQueryGroup>();
    }

    public ClientQueryGroupBy(ClientQueryGroupBy groupBy) {
        this();

        if (groupBy.groups != null) {
            for (ClientQueryGroup group : groupBy.groups) {
                this.groups.add(group.deepClone()); // should use deepClone here
            }
        }
    }

    @XmlTransient
    public ClientGroupAxis getGroupAxis() {
        return new ClientGroupAxis(this.groups);
    }

    @Override
    public ClientGroupAxis getAxis(ClientGroupAxisEnum name) {
        if (!ClientGroupAxisEnum.GROUPS.equals(name)) {
            throw new IllegalArgumentException(String.format("unsupported axis %s for FlatGroupBy", name.toString()));
        }
        return getGroupAxis();
    }

    @Override
    public ClientGroupAxis getAxis(int index) {
        if (index != 0) {
            throw new IllegalArgumentException(String.format("unsupported axis index: %d", index));
        }
        return getGroupAxis();
    }

    @XmlTransient
    @Override
    public List<ClientGroupAxis> getAxes() {
        return Collections.singletonList(getGroupAxis());
    }

    public ClientQueryGroupBy setGroups(List<? extends ClientQueryGroup> groups) {
        this.groups = (List<ClientQueryGroup>) groups;
        return this;
    }

    public List<? extends ClientQueryGroup> getGroups() {
        return groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientQueryGroupBy that = (ClientQueryGroupBy) o;

        return !(groups != null ? !groups.equals(that.groups) : that.groups != null);

    }

    @Override
    public int hashCode() {
        return groups != null ? groups.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientQueryGroupBy{" +
                "groups=" + groups +
                '}';
    }
}
