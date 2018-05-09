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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 08.07.2016
 */
public class ClientMultiAxesAggregationLevel extends ClientMultiAxesDatasetLevel {
    private List<ClientDatasetFieldReference> fields = new ArrayList<ClientDatasetFieldReference>();

    public ClientMultiAxesAggregationLevel() {
    }

    public ClientMultiAxesAggregationLevel(ClientMultiAxesAggregationLevel level) {
        super(level);
        for (ClientDatasetFieldReference ref : level.getFields()) {
            fields.add(new ClientDatasetFieldReference(ref));
        }
    }

    public List<ClientDatasetFieldReference> getFields() {
        return fields;
    }

    public ClientMultiAxesAggregationLevel setFields(List<ClientDatasetFieldReference> fields) {
        this.fields = fields;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientMultiAxesAggregationLevel that = (ClientMultiAxesAggregationLevel) o;

        return !(fields != null ? !fields.equals(that.fields) : that.fields != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientMultiAxesAggregationLevel{" +
                "fields=" + fields +
                ", members='" + getMembers() + '\'' +
                '}';
    }
}
