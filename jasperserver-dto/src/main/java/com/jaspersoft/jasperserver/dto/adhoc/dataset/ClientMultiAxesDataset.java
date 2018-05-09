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
 * Created by schubar on 12/3/13.
 * @version $Id$
 */
@XmlRootElement
public class ClientMultiAxesDataset {
    private List<ClientAxis> axes;

    private List<String[]> data;
    private List<Integer> counts = new ArrayList<Integer>();

    public ClientMultiAxesDataset() {}

    // Constructor for the deep copy
    public ClientMultiAxesDataset(final ClientMultiAxesDataset dataset) {
        counts.addAll(dataset.getCounts());

        axes = new ArrayList<ClientAxis>();
        for (ClientAxis axis : dataset.getAxes()) {
            axes.add(new ClientAxis(axis));
        }
        data = new ArrayList<String[]>();
        for (String[] values : dataset.getData()) {
            data.add(Arrays.copyOf(values, values.length));
        }
    }

    @XmlElementWrapper(name = "axes")
    @XmlElement(name = "axis")
    public List<ClientAxis> getAxes() {
        return axes;
    }

    public void setAxes(List<ClientAxis> axes) {
        this.axes = axes;
    }

    @XmlElementWrapper(name = "data")
    @XmlElement(name = "column")
    public List<String[]> getData() {
        return data;
    }

    public ClientMultiAxesDataset setData(List<String[]> data) {
        this.data = data;
        return this;
    }

    public List<Integer> getCounts() {
        return counts;
    }

    public ClientMultiAxesDataset setCounts(List<Integer> counts) {
        this.counts = counts;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientMultiAxesDataset that = (ClientMultiAxesDataset) o;

        if (axes != null ? !axes.equals(that.axes) : that.axes != null) return false;
        if (data == that.data) return true;
        for (int i = 0; i < data.size(); i++) {
            if (!Arrays.equals(data.get(i), that.data.get(i))) {
                return false;
            }
        }
        return !(counts != null ? !counts.equals(that.counts) : that.counts != null);

    }

    @Override
    public int hashCode() {
        int result = axes != null ? axes.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (counts != null ? counts.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientMultiAxesDataset{" +
                "axes=" + axes +
                ", data=" + data +
                ", counts=" + counts +
                '}';
    }
}
