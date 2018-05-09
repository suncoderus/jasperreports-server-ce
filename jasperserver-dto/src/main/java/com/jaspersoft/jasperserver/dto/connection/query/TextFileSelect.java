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
package com.jaspersoft.jasperserver.dto.connection.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class TextFileSelect {
    private List<String> columns;

    public TextFileSelect(){}
    public TextFileSelect(TextFileSelect source){
        final List<String> sourceColumns = source.getColumns();
        if(sourceColumns != null){
            columns = new ArrayList<String>(sourceColumns);
        }
    }

    public List<String> getColumns() {
        return columns;
    }

    public TextFileSelect setColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }

    public TextFileSelect addColumn(String... columns){
        if(columns != null && columns.length > 0) {
            if (this.columns == null) {
                this.columns = new ArrayList<String>();
            }
            Collections.addAll(this.columns, columns);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFileSelect)) return false;

        TextFileSelect select = (TextFileSelect) o;

        if (columns != null ? !columns.equals(select.columns) : select.columns != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return columns != null ? columns.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TextFileSelect{" +
                "columns=" + columns +
                '}';
    }
}
