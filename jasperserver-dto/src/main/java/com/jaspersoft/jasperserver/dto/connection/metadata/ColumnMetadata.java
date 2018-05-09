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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@XmlRootElement(name="column")
public class ColumnMetadata {
    private String name;
    private String label;
    private String javaType;

    public ColumnMetadata(){
    }

    public ColumnMetadata(ColumnMetadata source){
        name = source.getName();
        label = source.getLabel();
        javaType = source.getJavaType();
    }

    public String getName() {
        return name;
    }

    public ColumnMetadata setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public ColumnMetadata setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getJavaType() {
        return javaType;
    }

    public ColumnMetadata setJavaType(String javaType) {
        this.javaType = javaType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnMetadata that = (ColumnMetadata) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (javaType != null ? !javaType.equals(that.javaType) : that.javaType != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ColumnMetadata{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", javaType='" + javaType + '\'' +
                '}';
    }
}
