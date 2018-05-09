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
package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import java.util.List;

/**
 * <p>Options set for to client conversion.</p>
 * Currently it includes expanded option only, but it can be extended later.
 * For instance it can specify what exactly fields are required and what fields should be excluded.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ToClientConversionOptions {
    private boolean expanded;
    private List<String> includes;
    private String acceptMediaType;

    public static ToClientConversionOptions getDefault(){
        return new ToClientConversionOptions();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public ToClientConversionOptions setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public ToClientConversionOptions setIncludes(List<String> includes) {
        this.includes = includes;
        return this;
    }

    public String getAcceptMediaType() {
        return acceptMediaType;
    }

    public ToClientConversionOptions setAcceptMediaType(String acceptMediaType) {
        this.acceptMediaType = acceptMediaType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToClientConversionOptions that = (ToClientConversionOptions) o;

        if (expanded != that.expanded) return false;
        if (acceptMediaType != null ? !acceptMediaType.equals(that.acceptMediaType) : that.acceptMediaType != null)
            return false;
        if (includes != null ? !includes.equals(that.includes) : that.includes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (expanded ? 1 : 0);
        result = 31 * result + (includes != null ? includes.hashCode() : 0);
        result = 31 * result + (acceptMediaType != null ? acceptMediaType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToClientConversionOptions{" +
                "expanded=" + expanded +
                ", includes=" + includes +
                ", acceptMediaType='" + acceptMediaType + '\'' +
                '}';
    }
}
