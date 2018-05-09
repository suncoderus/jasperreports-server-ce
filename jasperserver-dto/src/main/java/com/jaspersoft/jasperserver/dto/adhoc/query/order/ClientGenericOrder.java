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
package com.jaspersoft.jasperserver.dto.adhoc.query.order;


import com.jaspersoft.jasperserver.dto.adhoc.query.ClientFieldReference;
import com.jaspersoft.jasperserver.dto.adhoc.query.validation.CheckGenericOrderFieldReference;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Andriy Godovanets
 */
@CheckGenericOrderFieldReference
public class ClientGenericOrder implements ClientOrder, ClientFieldReference {
    public static final Boolean IS_AGGREGATION_DEFAULT = false;

    private Boolean isAscending;
    private Boolean isAggregationLevel = IS_AGGREGATION_DEFAULT;

    private String fieldRef;


    public ClientGenericOrder() {
        // no op
    }

    public ClientGenericOrder(ClientGenericOrder sorting) {
        if (sorting != null) {
            this
                    .setAscending(sorting.isAscending())
                    .setFieldReference(sorting.getFieldReference())
                    .setAggregation(sorting.isAggregationLevel());
        }
    }

    @Override
    public ClientGenericOrder deepClone() {
        return new ClientGenericOrder(this);
    }

    @Override
    public Boolean isAscending() {
        return isAscending;
    }

    public ClientGenericOrder setAscending(Boolean isAscending) {
        this.isAscending = isAscending;
        return this;
    }
    /**
     * @return Query Field Identifier or Metadata Field Name to order by
     */
    @XmlElement(name = "fieldRef")
    public String getFieldReference() {
        return fieldRef;
    }

    public ClientGenericOrder setFieldReference(String fieldReference) {
        this.fieldRef = fieldReference;
        return this;
    }

    /**
     * Since name of the measure level can vary, we've added separate property
     * to distinguish Measure level amongst others
     *
     * Default value: false
     *
     * @return true, if Measures level is sorted
     */
    @XmlTransient
    public Boolean isAggregationLevel() {
        return isAggregationLevel;
    }

    @XmlElement(name = "aggregation")
    public Boolean isAggregation() {
        return isAggregationLevel == IS_AGGREGATION_DEFAULT ? null : isAggregationLevel;
    }

    public ClientGenericOrder setAggregation(Boolean aggregationLevel) {
        isAggregationLevel = aggregationLevel;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientGenericOrder)) return false;

        ClientGenericOrder that = (ClientGenericOrder) o;

        if (isAscending != null ? !isAscending.equals(that.isAscending) : that.isAscending != null) return false;
        if (isAggregationLevel != null ? !isAggregationLevel.equals(that.isAggregationLevel) : that.isAggregationLevel != null)
            return false;
        return fieldRef != null ? fieldRef.equals(that.fieldRef) : that.fieldRef == null;
    }

    @Override
    public int hashCode() {
        int result = isAscending != null ? isAscending.hashCode() : 0;
        result = 31 * result + (isAggregationLevel != null ? isAggregationLevel.hashCode() : 0);
        result = 31 * result + (fieldRef != null ? fieldRef.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientGenericOrder{");
        sb.append("isAscending=").append(isAscending);
        sb.append(", isAggregationLevel=").append(isAggregationLevel);
        sb.append(", fieldReference='").append(fieldRef).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
