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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
@XmlRootElement(name = "flatDataset")
public class ClientFlatDataset {
    private int counts;
    private List<ClientFlatDatasetFieldReference> fields = new ArrayList<ClientFlatDatasetFieldReference>();
    private List<String[]> rows;

    public ClientFlatDataset() {

    }

    public ClientFlatDataset(ClientFlatDataset dataset) {
        this.counts = dataset.getCounts();
        if (dataset.getFields() != null) {
            for (ClientFlatDatasetFieldReference field : dataset.getFields()) {
                fields.add(new ClientFlatDatasetFieldReference(field));
            }
        }
        if (dataset.getRows() != null) {
            this.rows = new ArrayList<String[]>();
            for (String[] row : dataset.getRows()) {
                this.rows.add(Arrays.copyOf(row, row.length));
            }
        }
    }

    @XmlElement(name = "counts")
    public int getCounts() {
        return counts;
    }

    public ClientFlatDataset setCounts(int counts) {
        this.counts = counts;
        return this;
    }

    public List<ClientFlatDatasetFieldReference> getFields() {
        return fields;
    }

    public void setFields(List<ClientFlatDatasetFieldReference> fields) {
        this.fields = fields;
    }

    @XmlElementWrapper(name = "rows")
    @XmlElement(name = "row")
    public List<String[]> getRows() {
        return rows;
    }

    public ClientFlatDataset setRows(List<String[]> rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientFlatDataset that = (ClientFlatDataset) o;

        if (counts != that.counts) return false;
        if (fields != null ? !fields.equals(that.fields) : that.fields != null) return false;
        return !(rows != null ? !rows.equals(that.rows) : that.rows != null);

    }

    @Override
    public int hashCode() {
        int result = counts;
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientFlatDataset{" +
                "counts=" + counts +
                ", fields=" + fields +
                ", rows=" + rows +
                '}';
    }
}
