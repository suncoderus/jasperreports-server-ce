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
package com.jaspersoft.jasperserver.dto.adhoc.query.field;

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceField;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientField;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientIdentifiable;
import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryVisitor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Andriy Godovanets
 */
public class ClientQueryField implements ClientField, ClientIdentifiable<String>, ClientQueryExpression {
    private String id;
    private String type;
    private boolean measure;
    @NotNull
    private String field;

    public ClientQueryField() {
        // no op
    }

    public ClientQueryField(ClientQueryField field) {
        if (field != null) {
            this
                    .setId(field.getId())
                    .setFieldName(field.getFieldName());
            this.type = field.type;
            this.measure = field.measure;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public ClientQueryField setId(String id) {
        this.id = id;
        return this;
    }

    @XmlElement(name = "field")
    public String getFieldName() {
        return field;
    }

    public ClientQueryField setFieldName(String fieldName) {
        this.field = fieldName;
        return this;
    }

    /**
     * This name is calculated query field name: if id is define, that this is id, else it's fieldName.
     * This name is related to Field Reference
     *
     * @return Client field name
     */
    @Override
    @XmlTransient
    public String getName() {
        return getFieldName();
    }

    @Override
    @XmlTransient
    public String getType() {
        return type;
    }

    @XmlTransient
    public boolean isMeasure() {
        return measure;
    }

    public ClientQueryField setDataSourceField(ClientDataSourceField field) {
        if (field != null) {
            type = field.getType();
            measure = field.isMeasure();
            setFieldName(field.getName());
        }
        return this;
    }

    @Override
    public void accept(ClientQueryVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientQueryField that = (ClientQueryField) o;

        if (isMeasure() != that.isMeasure()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) return false;
        return getFieldName() != null ? getFieldName().equals(that.getFieldName()) : that.getFieldName() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (isMeasure() ? 1 : 0);
        result = 31 * result + (getFieldName() != null ? getFieldName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientQueryField{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", measure=" + measure +
                ", field='" + field + '\'' +
                '}';
    }
}
