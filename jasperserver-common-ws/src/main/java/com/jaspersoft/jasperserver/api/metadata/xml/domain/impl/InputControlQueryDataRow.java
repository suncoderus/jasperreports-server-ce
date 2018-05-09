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

package com.jaspersoft.jasperserver.api.metadata.xml.domain.impl;

/**
 *
 * @author gtoffoli
 */
public class InputControlQueryDataRow {
    
    private Object value = null;
    private java.util.List columnValues = null;
    
    /** Creates a new instance of InputControlQueryDataRow */
    public InputControlQueryDataRow() {
        columnValues = new java.util.ArrayList();
    }

    public java.util.List getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(java.util.List columnValues) {
        this.columnValues = columnValues;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
}
