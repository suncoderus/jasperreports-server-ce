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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientFloat;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDouble;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLong;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientShort;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientByte;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientLogical;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientAdd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientDivide;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientMultiply;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientPercentRatio;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.arithmetic.ClientSubtract;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.range.ClientRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.filter.ClientFilter;
import com.jaspersoft.jasperserver.dto.adhoc.query.filter.ClientFilterHint;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Stas Chubar <schubar@tibco.com>
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id $
 */
public class ClientExpressions {

    public static final String EQUALS_DATE_FUN = "equalsDate";
    public static final String AFTER_DATE_FUN = "afterDate";
    public static final String BEFORE_DATE_FUN = "beforeDate";
    public static final String IS_ON_OR_AFTER_DATE_FUN = "isOnOrAfterDate";
    public static final String IS_ON_OR_BEFORE_DATE_FUN = "isOnOrBeforeDate";
    public static final String BETWEEN_DATE_FUN = "betweenDates";

    public static final String STARTS_WITH_FUN = "startsWith";
    public static final String ENDS_WITH_FUN = "endsWith";
    public static final String CONTAINS_FUN = "contains";

    public static final String MISSING_REPRESENTATION = "$missing$";

    public static ClientFunction function(String name, ClientExpression... params) {
        return new ClientFunction(name, list(params));
    }

    public static ClientFunction function(String name, List<ClientExpression> params) {
        return new ClientFunction(name, params);
    }

    public static ClientFunction equalsDate(ClientExpression... params) {
        return new ClientFunction(EQUALS_DATE_FUN, list(params));
    }

    public static ClientFunction afterDate(ClientExpression... params) {
        return new ClientFunction(AFTER_DATE_FUN, list(params));
    }

    public static ClientFunction beforeDate(ClientExpression... params) {
        return new ClientFunction(BEFORE_DATE_FUN, list(params));
    }

    public static ClientFunction betweenDates(ClientExpression... params) {
        return new ClientFunction(BETWEEN_DATE_FUN, list(params));
    }

    public static ClientFunction startsWith(ClientExpression... params) {
        return new ClientFunction(STARTS_WITH_FUN, list(params));
    }

    public static ClientFunction endsWith(ClientExpression... params) {
        return new ClientFunction(ENDS_WITH_FUN, list(params));
    }

    public static ClientFunction contains(ClientExpression... params) {
        return new ClientFunction(CONTAINS_FUN, list(params));
    }

    public static ClientLiteral nullLiteral() {
        return new ClientNull();
    }

    public static ClientLiteral literal(String value) {
        return new ClientString(value);
    }

    public static ClientLiteral literal(Byte value) {
        return new ClientByte(value);
    }

    public static ClientLiteral literal(Short value) {
        return new ClientShort(value);
    }

    public static ClientLiteral literal(Integer value) {
        return new ClientInteger(value);
    }

    public static ClientLiteral literal(Long value){
        return new ClientLong(value);
    }

    public static ClientLiteral literal(BigInteger value){
        return new ClientBigInteger(value);
    }

    public static ClientLiteral literal(Float value) {
        return new ClientFloat(value);
    }

    public static ClientLiteral literal(Double value) {
        return new ClientDouble(value);
    }

    public static ClientLiteral literal(BigDecimal value) {
        return new ClientBigDecimal(value);
    }

    public static ClientLiteral literal(Date value) {
        return new ClientDate(value);
    }

    public static ClientLiteral literal(Time value) {
        return new ClientTime(value);
    }

    public static ClientLiteral literal(Timestamp date) {
        return new ClientTimestamp(date);
    }

    public static ClientLiteral literal(boolean value) {
        return new ClientBoolean(value);
    }

    public static ClientLiteral literal(char value) {
        return ClientString.valueOf(value);
    }

    public static ClientLiteral string(String value) {
        return literal(value);
    }

    public static ClientLiteral integer(Integer value) {
        return literal(value);
    }

    public static ClientLiteral decimal(BigDecimal value) {
        return literal(value);
    }

    public static ClientLiteral decimal(Double value) {
        return literal(value);
    }

    public static ClientLiteral date(Date value) {
        return literal(value);
    }

    public static ClientLiteral time(Time value) {
        return literal(value);
    }

    public static ClientLiteral timestamp(Timestamp value) {
        return literal(value);
    }

    public static ClientLiteral bool(boolean value) {
        return literal(value);
    }

    public static ClientLiteral character(char value) {
        return literal(value);
    }

    public static ClientRange range(ClientExpression start, ClientExpression end) {
        return new ClientRange(start, end);
    }

    public static ClientRange range(ClientVariable start, ClientVariable end)
    {
        return new ClientRange(start, end);
    }

    public static ClientRange range(Integer start, Integer end) {
        return new ClientRange(literal(start), literal(end));
    }
    public static ClientRange range(Time start, Time end) {
        return new ClientRange(literal(start), literal(end));
    }

    public static ClientRange range(Double start, Double end) {
        return new ClientRange(literal(start), literal(end));
    }

    public static ClientRange range(BigDecimal start, BigDecimal end) {
        return new ClientRange(literal(start), literal(end));
    }

    public static ClientRange range(String start, String end) {
        return new ClientRange(literal(start), literal(end));
    }

    public static ClientVariable variable(String string) {
        return new ClientVariable(string);
    }

    public static ClientFilter filter(String string, ClientExpression expression) {
        return new ClientFilter(string, expression);
    }

    public static ClientFilter filter(String string, ClientFilterHint hint, ClientExpression expression) {
        return new ClientFilter(string, hint, expression);
    }

    public static ClientNot not(ClientExpression expression) {
        return ClientNot.not(expression);
    }

    public static ClientAnd and(ClientExpression lhs, ClientExpression rhs) {
        return ClientLogical.and(lhs, rhs);
    }

    public static ClientOr or(ClientExpression lhs, ClientExpression rhs) {
        return ClientLogical.or(lhs, rhs);
    }

    public static ClientComparison comparison(String operation, ClientExpression... operands) {
        return ClientComparison.createComparison(operation, list(operands));
    }

    public static ClientComparison comparison(String operation, List<ClientExpression> operands) {
        return ClientComparison.createComparison(operation, operands);
    }

    public static List<ClientExpression> list(ClientExpression... operands) {
        return asList(operands);
    }

    public static List<ClientExpression> operands(ClientExpression... operands) {
        return asList(operands);
    }

    public static List<ClientExpression> args(ClientExpression... operands) {
        return asList(operands);
    }

    public static ClientRange range(ClientLiteral start, ClientLiteral end) {
        return new ClientRange(start, end);
    }

    public static ClientAdd add(ClientExpression lhs, ClientExpression rhs) {
        return new ClientAdd(asList(lhs, rhs));
    }

    public static ClientMultiply multiply(ClientExpression lhs, ClientExpression rhs) {
        return new ClientMultiply(asList(lhs, rhs));
    }

    public static ClientSubtract subtract(ClientExpression lhs, ClientExpression rhs) {
        return new ClientSubtract(asList(lhs, rhs));
    }

    public static ClientDivide divide(ClientExpression lhs, ClientExpression rhs) {
        return new ClientDivide(asList(lhs, rhs));
    }

    public static ClientPercentRatio percentRatio(ClientExpression lhs, ClientExpression rhs) {
        return new ClientPercentRatio(asList(lhs, rhs));
    }

}
