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
package com.jaspersoft.jasperserver.export.modules.common.rd;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.util.rd.DateRangeUtil;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.DateRangeBuilder;

import java.util.Date;

/**
 * DTO which is used for XML serialization-deserialization
 * for {@link net.sf.jasperreports.types.date.DateRange} instances
 * since they are not directly supported serialization due to immutability.
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class DateRangeDTO {
    private String expression;
    private Date date;
    private String valueClass;

    public DateRangeDTO() {
    }

    public DateRangeDTO(DateRange source) {
        if (source == null) {
            throw new IllegalArgumentException();
        }

        expression = DateRangeUtil.getExpression(source);
        if (expression == null) {
            date = source.getStart();
        }
        valueClass = source.getStart().getClass().getName();
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getValueClass() {
        return valueClass;
    }

    public void setValueClass(String valueClass) {
        this.valueClass = valueClass;
    }

    public DateRange toDateRange() {
        try {
            DateRangeBuilder builder = expression == null ? new DateRangeBuilder(date) : new DateRangeBuilder(expression);

            //Cast is guaranteed by DateRange interface which methods should be used to determine value class string
            @SuppressWarnings("unchecked")
            Class<? extends Date> clazz = (Class<? extends Date>)Class.forName(valueClass);

            return builder.set(clazz).toDateRange();
        } catch (ClassNotFoundException e) {
            throw new JSException(e);
        }
    }
}
