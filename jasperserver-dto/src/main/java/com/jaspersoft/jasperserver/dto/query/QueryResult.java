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
package com.jaspersoft.jasperserver.dto.query;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Paul Lysak
 *         Date: 11.02.13
 *         Time: 17:35
 */
@XmlRootElement(name = "queryResult")
@XmlType(propOrder = {"names", "rows"})
public class QueryResult {
    private List<String> names = new ArrayList<String>();

    private List<QueryResultRow> rows = new ArrayList<QueryResultRow>();

    public QueryResult() {
    }

    public QueryResult(List<String> names, QueryResultRow... rows) {
        this.names = names;
        this.rows.addAll(Arrays.asList(rows));
    }

    @XmlElement(name = "name")
    @XmlElementWrapper(name = "names")
    public List<String> getNames() {
        return names;
    }

    public QueryResult setNames(List<String> names) {
        this.names = names;
        return this;
    }

    @XmlElement(name = "row")
    @XmlElementWrapper(name = "values")
    public List<QueryResultRow> getRows() {
        return rows;
    }

    public QueryResult setRows(List<QueryResultRow> rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "names=" + names +
                ", rows=" + rows +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryResult that = (QueryResult) o;

        if (names != null ? !names.equals(that.names) : that.names != null) return false;
        if (rows != null ? !rows.equals(that.rows) : that.rows != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = names != null ? names.hashCode() : 0;
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        return result;
    }


}
