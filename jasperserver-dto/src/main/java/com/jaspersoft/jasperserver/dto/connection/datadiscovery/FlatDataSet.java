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
package com.jaspersoft.jasperserver.dto.connection.datadiscovery;

import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "flatDataSet")
public class FlatDataSet {
    private List<String[]> data;
    private ResourceGroupElement metadata;
    public FlatDataSet(){}
    public FlatDataSet(FlatDataSet source){
        final ResourceGroupElement metadata = source.getMetadata();
        if(metadata != null){
            setMetadata(metadata.deepClone());
        }
        final List<String[]> sourceData = source.getData();
        if (data != null) {
            this.data = new ArrayList<String[]>();
            for (String[] row : sourceData) {
                this.data.add(Arrays.copyOf(row, row.length));
            }
        }
    }

    @XmlElementWrapper(name = "rows")
    @XmlElement(name = "row")
    public List<String[]> getData() {
        return data;
    }

    public FlatDataSet setData(List<String[]> data) {
        this.data = data;
        return this;
    }

    public ResourceGroupElement getMetadata() {
        return metadata;
    }

    public FlatDataSet setMetadata(ResourceGroupElement metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlatDataSet)) return false;

        FlatDataSet that = (FlatDataSet) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return metadata != null ? metadata.equals(that.metadata) : that.metadata == null;

    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FlatDataSet{" +
                "data=" + data +
                ", metadata=" + metadata +
                '}';
    }
}
