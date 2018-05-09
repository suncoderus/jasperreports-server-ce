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
package com.jaspersoft.jasperserver.dto.adhoc.query.select;

import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryClause;
import com.jaspersoft.jasperserver.dto.adhoc.query.ast.ClientQueryVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.validation.groups.MultiAxisQueryValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andriy Godovanets
 * @author Stas Chubar <schubar@tibco.com>
 *
 * @version $Id$
 */
public class ClientSelect implements ClientQueryClause {
    @Valid
    @Null(groups = MultiAxisQueryValidationGroup.class, message = "query.details.unsupported")
    private List<ClientQueryField> fields;

    @Valid
    private List<ClientQueryAggregatedField> aggregations;

    public ClientSelect() {
        // no op
    }

    public ClientSelect(ClientSelect select) {
        if (select != null) {
            if (select.getFields() != null) {
                this.fields = new ArrayList<ClientQueryField>();
                for (ClientQueryField field : select.getFields()) {
                    this.fields.add(new ClientQueryField(field));
                }
            }

            if (select.getAggregations() != null) {
                this.aggregations = new ArrayList<ClientQueryAggregatedField>();
                for (ClientQueryAggregatedField aggregatedField : select.getAggregations()) {
                    this.aggregations.add(new ClientQueryAggregatedField(aggregatedField));
                }
            }
        }
    }

    public ClientSelect(List<ClientQueryField> fields) {
        setFields(new ArrayList<ClientQueryField>(fields));
    }

    public ClientQueryField getField(int index) {
        return fields.get(index);
    }

    public ClientQueryField getField(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot search query field by null id");
        }
        Iterator<ClientQueryField> itr = fields.iterator();
        while (itr.hasNext()) {
            ClientQueryField field = itr.next();
            if (id.equals(field.getId()) || field.getFieldName().equals(id)) {
                return field;
            }
        }
        return null;
    }

    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    public List<ClientQueryField> getFields() {
        return fields;
    }

    public ClientSelect setFields(List<ClientQueryField> fields) {
        this.fields = (fields != null) ? fields : new ArrayList<ClientQueryField>();
        return this;
    }

    @XmlElementWrapper(name = "aggregations")
    @XmlElement(name = "aggregation")
    public List<ClientQueryAggregatedField> getAggregations() {
        return aggregations;
    }

    public ClientSelect setAggregations(List<ClientQueryAggregatedField> aggregations) {
        this.aggregations = (aggregations != null) ? aggregations : new ArrayList<ClientQueryAggregatedField>();
        return this;
    }

    @Override
    public void accept(ClientQueryVisitor visitor) {
        if (this.fields != null) {
            for (ClientQueryField field: this.fields) {
                field.accept(visitor);
            }
        }
        if (this.aggregations != null) {
            for (ClientQueryAggregatedField aggregatedField: this.aggregations) {
                aggregatedField.accept(visitor);
            }
        }
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientSelect that = (ClientSelect) o;

        if (getFields() != null ? !getFields().equals(that.getFields()) : that.getFields() != null) return false;
        return getAggregations() != null ? getAggregations().equals(that.getAggregations()) : that.getAggregations() == null;

    }

    @Override
    public int hashCode() {
        int result = getFields() != null ? getFields().hashCode() : 0;
        result = 31 * result + (getAggregations() != null ? getAggregations().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientSelect{" +
                "fields=" + fields +
                ", aggregations=" + aggregations +
                '}';
    }
}
