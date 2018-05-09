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
package com.jaspersoft.jasperserver.dto.domain;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class DomElVariable implements DeepCloneable<DomElVariable> {
    private String name;
    private String type;

    public String getName() {
        return name;
    }

    public DomElVariable setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public DomElVariable setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomElVariable)) return false;

        DomElVariable that = (DomElVariable) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DomElVariable{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public DomElVariable deepClone() {
        return new DomElVariable().setName(name).setType(type);
    }
}
