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
package com.jaspersoft.jasperserver.dto.connection.metadata;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class PartialMetadataOptions implements DeepCloneable<PartialMetadataOptions> {
    private List<String> includes;
    private List<String> expands;

    public PartialMetadataOptions(){}
    public PartialMetadataOptions(PartialMetadataOptions source){
        final List<String> includes = source.getIncludes();
        if(includes != null) {
            this.includes = new ArrayList<String>(includes);
        }
        final List<String> expands = source.getExpands();
        if(expands != null){
            this.expands = new ArrayList<String>(expands);
        }

    }

    public List<String> getIncludes() {
        return includes;
    }

    public PartialMetadataOptions setIncludes(List<String> includes) {
        this.includes = includes;
        return this;
    }

    public List<String> getExpands() {
        return expands;
    }

    public PartialMetadataOptions setExpands(List<String> expands) {
        this.expands = expands;
        return this;
    }

    @Override
    public PartialMetadataOptions deepClone() {
        return new PartialMetadataOptions(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartialMetadataOptions)) return false;

        PartialMetadataOptions that = (PartialMetadataOptions) o;

        if (includes != null ? !includes.equals(that.includes) : that.includes != null) return false;
        return expands != null ? expands.equals(that.expands) : that.expands == null;
    }

    @Override
    public int hashCode() {
        int result = includes != null ? includes.hashCode() : 0;
        result = 31 * result + (expands != null ? expands.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PartialMetadataOptions{" +
                "includes=" + includes +
                ", expands=" + expands +
                '}';
    }
}
