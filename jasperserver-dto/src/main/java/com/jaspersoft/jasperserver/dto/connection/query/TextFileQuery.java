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

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class TextFileQuery {
    private Integer offset;
    private Integer limit;
    private TextFileSelect select;
    private TextFileConvert convert;

    public TextFileQuery(){}

    public TextFileQuery(TextFileQuery source){
        final TextFileSelect sourceSelect = source.getSelect();
        if(sourceSelect != null){
            select = new TextFileSelect(sourceSelect);
        }
        offset = source.getOffset();
        limit = source.getLimit();
        convert = source.getConvert();
    }

    public TextFileConvert getConvert() {
        return convert;
    }

    public TextFileQuery setConvert(TextFileConvert convert) {
        this.convert = convert;
        return this;
    }

    public TextFileSelect getSelect() {
        return select;
    }

    public TextFileQuery setSelect(TextFileSelect select) {
        this.select = select;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public TextFileQuery setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public TextFileQuery setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFileQuery)) return false;

        TextFileQuery that = (TextFileQuery) o;

        if (convert != null ? !convert.equals(that.convert) : that.convert != null) return false;
        if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
        if (offset != null ? !offset.equals(that.offset) : that.offset != null) return false;
        if (select != null ? !select.equals(that.select) : that.select != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = offset != null ? offset.hashCode() : 0;
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (select != null ? select.hashCode() : 0);
        result = 31 * result + (convert != null ? convert.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TextFileQuery{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", select=" + select +
                ", convert=" + convert +
                '}';
    }
}
