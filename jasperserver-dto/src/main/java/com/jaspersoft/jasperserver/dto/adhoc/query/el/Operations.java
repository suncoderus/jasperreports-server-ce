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

package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLong;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientShort;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientByte;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientFloat;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDouble;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLiteralType;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * <p/>
 * </p>
 *
 * @author Stas Chubar (schubar@tibco.com)
 * @version $Id$
 */
public abstract class Operations<T extends ClientExpression> {
    protected abstract T getMe();

    public ClientComparison eq(ClientExpression expression) {
        return ClientComparison.eq(getMe(), expression);
    }

    public ClientComparison eq(String value) {
        return ClientComparison.eq(getMe(), new ClientString(value));
    }

    public ClientComparison eq(Byte value) {
        return ClientComparison.eq(getMe(), new ClientByte(value));
    }

    public ClientComparison eq(Short value) {
        return ClientComparison.eq(getMe(), new ClientShort(value));
    }

    public ClientComparison eq(Integer value) {
        return ClientComparison.eq(getMe(), new ClientInteger(value));
    }

    public ClientComparison eq(Long value){
        return ClientComparison.eq(getMe(), new ClientLong(value));
    }

    public ClientComparison eq(BigInteger value){
        return ClientComparison.eq(getMe(), new ClientBigInteger(value));
    }

    public ClientComparison eq(Float value) {
        return ClientComparison.eq(getMe(), new ClientFloat(value));
    }

    public ClientComparison eq(Double value) {
        return ClientComparison.eq(getMe(), new ClientDouble(value));
    }

    public ClientComparison eq(BigDecimal value) {
        return ClientComparison.eq(getMe(), new ClientBigDecimal(value));
    }

    public ClientComparison notEq(ClientExpression expression) {
        return ClientComparison.notEq(getMe(), expression);
    }

    public ClientComparison notEq(String value) {
        return ClientComparison.notEq(getMe(), new ClientString(value));
    }

    public ClientComparison notEq(Byte value) {
        return ClientComparison.notEq(getMe(), new ClientByte(value));
    }

    public ClientComparison notEq(Short value) {
        return ClientComparison.notEq(getMe(), new ClientShort(value));
    }

    public ClientComparison notEq(Integer value) {
        return ClientComparison.notEq(getMe(), new ClientInteger(value));
    }

    public ClientComparison notEq(Long value) {
        return ClientComparison.notEq(getMe(), new ClientLong(value));
    }

    public ClientComparison notEq(BigInteger value) {
        return ClientComparison.notEq(getMe(), new ClientBigInteger(value));
    }

    public ClientComparison notEq(Float value) {
        return ClientComparison.notEq(getMe(), new ClientFloat(value));
    }

    public ClientComparison notEq(Double value) {
        return ClientComparison.notEq(getMe(), new ClientDouble(value));
    }

    public ClientComparison notEq(BigDecimal value) {
        return ClientComparison.notEq(getMe(), new ClientBigDecimal(value));
    }

    public ClientFunction startsWith(String value) {
        return new ClientFunction(ClientExpressions.STARTS_WITH_FUN, ClientExpressions.list(getMe(), new ClientString(value)));
    }

    public ClientFunction startsWith(ClientVariable variable) {
        return new ClientFunction(ClientExpressions.STARTS_WITH_FUN, ClientExpressions.list(getMe(), new ClientVariable(variable)));
    }

    public ClientFunction endsWith(String value) {
        return new ClientFunction(ClientExpressions.ENDS_WITH_FUN, ClientExpressions.list(getMe(), new ClientString(value)));
    }

    public ClientFunction contains(String value) {
        return new ClientFunction(ClientExpressions.CONTAINS_FUN, ClientExpressions.list(getMe(), new ClientString(value)));
    }

    public ClientComparison gtOrEq(ClientExpression expression) {
        return ClientComparison.gtOrEq(getMe(), expression);
    }

    public ClientComparison gtOrEq(Byte value) {
        return ClientComparison.gtOrEq(getMe(), new ClientByte(value));
    }

    public ClientComparison gtOrEq(Short value) {
        return ClientComparison.gtOrEq(getMe(), new ClientShort(value));
    }

    public ClientComparison gtOrEq(Integer value) {
        return ClientComparison.gtOrEq(getMe(), new ClientInteger(value));
    }

    public ClientComparison gtOrEq(Long value) {
        return ClientComparison.gtOrEq(getMe(), new ClientLong(value));
    }

    public ClientComparison gtOrEq(BigInteger value) {
        return ClientComparison.gtOrEq(getMe(), new ClientBigInteger(value));
    }

    public ClientComparison gtOrEq(Float value) {
        return ClientComparison.gtOrEq(getMe(), new ClientFloat(value));
    }

    public ClientComparison gtOrEq(Double value) {
        return ClientComparison.gtOrEq(getMe(), new ClientDouble(value));
    }

    public ClientComparison gtOrEq(BigDecimal value) {
        return ClientComparison.gtOrEq(getMe(), new ClientBigDecimal(value));
    }

    public ClientComparison ltOrEq(ClientExpression expression) {
        return ClientComparison.ltOrEq(getMe(), expression);
    }

    public ClientComparison ltOrEq(Byte value) {
        return ClientComparison.ltOrEq(getMe(), new ClientByte(value));
    }

    public ClientComparison ltOrEq(Short value) {
        return ClientComparison.ltOrEq(getMe(), new ClientShort(value));
    }

    public ClientComparison ltOrEq(Integer value) {
        return ClientComparison.ltOrEq(getMe(), new ClientInteger(value));
    }

    public ClientComparison ltOrEq(Long value) {
        return ClientComparison.ltOrEq(getMe(), new ClientLong(value));
    }

    public ClientComparison ltOrEq(BigInteger value) {
        return ClientComparison.ltOrEq(getMe(), new ClientBigInteger(value));
    }

    public ClientComparison ltOrEq(Float value) {
        return ClientComparison.ltOrEq(getMe(), new ClientFloat(value));
    }

    public ClientComparison ltOrEq(Double value) {
        return ClientComparison.ltOrEq(getMe(), new ClientDouble(value));
    }

    public ClientComparison ltOrEq(BigDecimal value) {
        return ClientComparison.ltOrEq(getMe(), new ClientBigDecimal(value));
    }

    public ClientComparison gt(ClientExpression expression) {
        return ClientComparison.gt(getMe(), expression);
    }

    public ClientComparison gt(Byte value) {
        return ClientComparison.gt(getMe(), new ClientByte(value));
    }

    public ClientComparison gt(Short value) {
        return ClientComparison.gt(getMe(), new ClientShort(value));
    }

    public ClientComparison gt(Integer value) {
        return ClientComparison.gt(getMe(), new ClientInteger(value));
    }

    public ClientComparison gt(Long value) {
        return ClientComparison.gt(getMe(), new ClientLong(value));
    }

    public ClientComparison gt(BigInteger value) {
        return ClientComparison.gt(getMe(), new ClientBigInteger(value));
    }

    public ClientComparison gt(Float value) {
        return ClientComparison.gt(getMe(), new ClientFloat(value));
    }

    public ClientComparison gt(Double value) {
        return ClientComparison.gt(getMe(), new ClientDouble(value));
    }

    public ClientComparison gt(BigDecimal value) {
        return ClientComparison.gt(getMe(), new ClientBigDecimal(value));
    }

    public ClientComparison lt(ClientExpression expression) {
        return ClientComparison.lt(getMe(), expression);
    }

    public ClientComparison lt(Byte value) {
        return ClientComparison.lt(getMe(), new ClientByte(value));
    }

    public ClientComparison lt(Short value) {
        return ClientComparison.lt(getMe(), new ClientShort(value));
    }

    public ClientComparison lt(Integer value) {
        return ClientComparison.lt(getMe(), new ClientInteger(value));
    }

    public ClientComparison lt(Long value) {
        return ClientComparison.lt(getMe(), new ClientLong(value));
    }

    public ClientComparison lt(BigInteger value) {
        return ClientComparison.lt(getMe(), new ClientBigInteger(value));
    }

    public ClientComparison lt(Float value) {
        return ClientComparison.lt(getMe(), new ClientFloat(value));
    }

    public ClientComparison lt(Double value) {
        return ClientComparison.lt(getMe(), new ClientDouble(value));
    }

    public ClientComparison lt(BigDecimal value) {
        return ClientComparison.lt(getMe(), new ClientBigDecimal(value));
    }

    protected <T> List<ClientExpression> valuesToLiteralList(ClientLiteralType type, T... objects) {
        List<ClientExpression> result = new ArrayList<ClientExpression>();
        for (Object obj : objects) {
            result.add((ClientExpression) type.getLiteralInstance().setValue(obj));
        }
        return result;
    }

    public ClientFunction equalsDate(Date value) {
        return new ClientFunction(ClientExpressions.EQUALS_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.date(value)));
    }

    public ClientFunction beforeDate(Date value) {
        return new ClientFunction(ClientExpressions.BEFORE_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.date(value)));
    }

    public ClientFunction afterDate(Date value) {
        return new ClientFunction(ClientExpressions.AFTER_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.date(value)));
    }

    public ClientFunction isOnOrBeforeDate(Date value) {
        return new ClientFunction(ClientExpressions.IS_ON_OR_BEFORE_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.date(value)));
    }

    public ClientFunction isOnOrAfterDate(Date value) {
        return new ClientFunction(ClientExpressions.IS_ON_OR_AFTER_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.date(value)));
    }

    public ClientFunction betweenDates(Date begin, Date end) {
        return new ClientFunction(ClientExpressions.BETWEEN_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.date(begin), ClientExpressions.date(end)));
    }

    public ClientFunction equalsDate(Timestamp value) {
        return new ClientFunction(ClientExpressions.EQUALS_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.timestamp(value)));
    }

    public ClientFunction beforeDate(Timestamp value) {
        return new ClientFunction(ClientExpressions.BEFORE_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.timestamp(value)));
    }

    public ClientFunction afterDate(Timestamp value) {
        return new ClientFunction(ClientExpressions.AFTER_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.timestamp(value)));
    }

    public ClientFunction isOnOrBeforeDate(Timestamp value) {
        return new ClientFunction(ClientExpressions.IS_ON_OR_BEFORE_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.timestamp(value)));
    }

    public ClientFunction isOnOrAfterDate(Timestamp value) {
        return new ClientFunction(ClientExpressions.IS_ON_OR_AFTER_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.timestamp(value)));
    }

    public ClientFunction betweenDates(Timestamp begin, Timestamp end) {
        return new ClientFunction(ClientExpressions.BETWEEN_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.timestamp(begin), ClientExpressions.timestamp(end)));
    }

    public ClientFunction equalsDate(Time value) {
        return new ClientFunction(ClientExpressions.EQUALS_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.time(value)));
    }

    public ClientFunction beforeDate(Time value) {
        return new ClientFunction(ClientExpressions.BEFORE_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.time(value)));
    }

    public ClientFunction afterDate(Time value) {
        return new ClientFunction(ClientExpressions.AFTER_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.time(value)));
    }

    public ClientFunction isOnOrBeforeDate(Time value) {
        return new ClientFunction(ClientExpressions.IS_ON_OR_BEFORE_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.time(value)));
    }

    public ClientFunction isOnOrAfterDate(Time value) {
        return new ClientFunction(ClientExpressions.IS_ON_OR_AFTER_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.time(value)));
    }

    public ClientFunction betweenDates(Time begin, Time end) {
        return new ClientFunction(ClientExpressions.BETWEEN_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.time(begin), ClientExpressions.time(end)));
    }

    public ClientFunction equalsDate(String value) {
        return new ClientFunction(ClientExpressions.EQUALS_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.string(value)));
    }

    public ClientFunction beforeDate(String value) {
        return new ClientFunction(ClientExpressions.BEFORE_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.string(value)));
    }

    public ClientFunction afterDate(String value) {
        return new ClientFunction(ClientExpressions.AFTER_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.string(value)));
    }

    public ClientFunction isOnOrBeforeDate(String value) {
        return new ClientFunction(ClientExpressions.IS_ON_OR_BEFORE_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.string(value)));
    }

    public ClientFunction isOnOrAfterDate(String value) {
        return new ClientFunction(ClientExpressions.IS_ON_OR_AFTER_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.string(value)));
    }

    public ClientFunction betweenDates(String begin, String end) {
        return new ClientFunction(ClientExpressions.BETWEEN_DATE_FUN, ClientExpressions.list(getMe(), ClientExpressions.string(begin), ClientExpressions.string(end)));
    }


}
