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

package com.jaspersoft.jasperserver.dto.adhoc.datasource;


/**
 * Field that belongs to some dimension
 *
 * @author Andriy Godovanets
 */
public class ClientDataSourceLevel extends ClientDataSourceField {

    private String dimensionName;

    public ClientDataSourceLevel() {
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public ClientDataSourceLevel setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
        return this;
    }

    public ClientDataSourceLevel setClientDataSourceField(ClientDataSourceField field) {
        setName(field.getName());
        setType(field.getType());
        setFormat(field.getFormat());

        setHierarchyName(field.getHierarchyName());

        setAggregateExpression(field.getAggregateExpression());
        setAggregateArg(field.getAggregateArg());
        setAggregateType(field.getAggregateType());
        setAggregateFunction(field.getAggregateFunction());
        return this;
    }

    @Override
    public ClientDataSourceLevel setAggregateArg(String aggregateArg) {
        return (ClientDataSourceLevel) super.setAggregateArg(aggregateArg);
    }

    @Override
    public ClientDataSourceLevel setAggregateExpression(String aggregateExpression) {
        return (ClientDataSourceLevel) super.setAggregateExpression(aggregateExpression);
    }

    @Override
    public ClientDataSourceLevel setAggregateFunction(String aggregateFunction) {
        return (ClientDataSourceLevel) super.setAggregateFunction(aggregateFunction);
    }

    @Override
    public ClientDataSourceLevel setAggregateType(String aggregateType) {
        return (ClientDataSourceLevel) super.setAggregateType(aggregateType);
    }

    @Override
    public ClientDataSourceLevel setFormat(String format) {
        return (ClientDataSourceLevel) super.setFormat(format);
    }

    @Override
    public ClientDataSourceLevel setHierarchyName(String hierarchyName) {
        return (ClientDataSourceLevel) super.setHierarchyName(hierarchyName);
    }

    @Override
    public ClientDataSourceLevel setName(String name) {
        return (ClientDataSourceLevel) super.setName(name);
    }

    @Override
    public ClientDataSourceLevel setType(String type) {
        return (ClientDataSourceLevel) super.setType(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientDataSourceLevel)) return false;
        if (!super.equals(o)) return false;

        ClientDataSourceLevel that = (ClientDataSourceLevel) o;

        return dimensionName.equals(that.dimensionName);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + dimensionName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientDataSourceLevel{");
        sb.append("dimensionName='").append(dimensionName).append('\'');
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", hierarchyName='").append(getHierarchyName()).append('\'');
        sb.append(", type='").append(getType()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
