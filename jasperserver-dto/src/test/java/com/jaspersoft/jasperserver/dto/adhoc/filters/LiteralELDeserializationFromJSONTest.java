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

package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
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
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/21/16 12:45 PM
 * @version $Id$
 */
public class LiteralELDeserializationFromJSONTest extends FilterTest {

    @Test
    public void ensureBoolean() throws Exception {
        final String booleanLiteral = "{\"value\": true}";
        ClientBoolean cl = dtoFromJSONString(booleanLiteral, ClientBoolean.class);

        assertThat(cl, is(instanceOf(ClientBoolean.class)));
        assertThat(cl.getValue(), is(instanceOf(Boolean.class)));
    }

    @Test
    public void ensureDateRange() throws Exception {
        final String dateRangeLiteral = "{\"value\": \"YEAR\"}";
        ClientRelativeDateRange cl = dtoFromJSONString(dateRangeLiteral, ClientRelativeDateRange.class);

        assertThat(cl, is(instanceOf(ClientRelativeDateRange.class)));
        assertThat(cl.getValue(), is(instanceOf(String.class)));
    }

    @Test
    public void ensureByte() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 7\n" +
                "}";
        ClientByte cl = dtoFromJSONString(literal, ClientByte.class);

        assertThat(cl, is(instanceOf(ClientByte.class)));
        assertThat(cl.getValue(), is(instanceOf(Byte.class)));
    }

    @Test
    public void ensureShort() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 7\n" +
                "}";
        ClientShort cl = dtoFromJSONString(literal, ClientShort.class);

        assertThat(cl, is(instanceOf(ClientShort.class)));
        assertThat(cl.getValue(), is(instanceOf(Short.class)));
    }

    @Test
    public void ensureInteger() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 555\n" +
                "}";
        ClientInteger cl = dtoFromJSONString(literal, ClientInteger.class);

        assertThat(cl, is(instanceOf(ClientInteger.class)));
        assertThat(cl.getValue(), is(instanceOf(Integer.class)));
    }

    @Test
    public void ensureLong() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 7\n" +
                "}";
        ClientLong cl = dtoFromJSONString(literal, ClientLong.class);

        assertThat(cl, is(instanceOf(ClientLong.class)));
        assertThat(cl.getValue(), is(instanceOf(Long.class)));
    }

    @Test
    public void ensureIntegerFromString() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"999\"\n" +
                "}";
        ClientInteger cl = dtoFromJSONString(literal, ClientInteger.class);

        assertThat(cl, is(instanceOf(ClientInteger.class)));
        assertThat(cl.getValue(), is(instanceOf(Integer.class)));
    }

    @Test
    public void ensureBigIntegerFromString() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"999999999999\"\n" +
                "}";
        ClientBigInteger cl = dtoFromJSONString(literal, ClientBigInteger.class);

        assertThat(cl, is(instanceOf(ClientBigInteger.class)));
        assertThat(cl.getValue(), is(instanceOf(BigInteger.class)));
    }

    @Test
    public void ensureString() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"Ukraine\"\n" +
                "}";
        ClientString cl = dtoFromJSONString(literal, ClientString.class);

        assertThat(cl, is(instanceOf(ClientString.class)));
        assertThat(cl.getValue(), is(instanceOf(String.class)));
    }

    @Test
    public void ensureFloat() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 6.666\n" +
                "}";
        ClientFloat cl = dtoFromJSONString(literal, ClientFloat.class);

        assertThat(cl, is(instanceOf(ClientFloat.class)));
        assertThat(cl.getValue(), is(instanceOf(Float.class)));
    }

    @Test
    public void ensureDouble() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : 6.666\n" +
                "}";
        ClientDouble cl = dtoFromJSONString(literal, ClientDouble.class);

        assertThat(cl, is(instanceOf(ClientDouble.class)));
        assertThat(cl.getValue(), is(instanceOf(Double.class)));
    }

    @Test
    public void ensureBigDecimalFromString() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"9.99234345542999123456523458574238592993011193433339\"\n" +
                "}";
        ClientBigDecimal cl = dtoFromJSONString(literal, ClientBigDecimal.class);

        assertThat(cl, is(instanceOf(ClientBigDecimal.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
        assertThat(cl.getValue().toString(), is("9.99234345542999123456523458574238592993011193433339"));
    }

    @Test
    public void ensureBigDecimal() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"1.7976931348623157E310\"\n" + // Larger than Double.MAX_VALUE by 2 orders of magnitude
                "}";
        ClientBigDecimal cl = dtoFromJSONString(literal, ClientBigDecimal.class);

        assertThat(cl, is(instanceOf(ClientBigDecimal.class)));
        assertThat(cl.getValue(), is(instanceOf(BigDecimal.class)));
        assertThat(cl.getValue().toString(), is("1.7976931348623157E+310"));
    }

    @Test
    public void ensureTime() throws Exception {
        final String literal = "{\n" +
                "  \"value\" : \"23:33:15\"\n" +
                "}";
        ClientTime cl = dtoFromJSONString(literal, ClientTime.class);

        assertThat(cl, is(instanceOf(ClientTime.class)));
        assertThat(cl.getValue(), is(instanceOf(Time.class)));
    }

    @Test
    public void ensureTimestamp() throws Exception {
        final String literal = " {\n" +
                "  \"value\" : \"2016-01-26 23:33:15\"\n" +
                "}";
        ClientTimestamp cl = dtoFromJSONString(literal, ClientTimestamp.class);

        assertThat(cl, is(instanceOf(ClientTimestamp.class)));
        assertThat(cl.getValue(), is(instanceOf(Timestamp.class)));
    }

    @Test
    public void ensureDate() throws Exception {

        final String literal = "{\n" +
                "  \"value\" : \"2016-01-26\"\n" +
                "}";

        ClientDate cl = dtoFromJSONString(literal, ClientDate.class);

        assertThat(cl, is(instanceOf(ClientDate.class)));
        assertThat(cl.getValue(), is(instanceOf(Date.class)));
    }

}
