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

import com.jaspersoft.jasperserver.dto.adhoc.DateFixtures;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDate;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientDouble;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientByte;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientShort;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientLong;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBigDecimal;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientFloat;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/21/16 12:45 PM
 * @version $Id$
 */
public class LiteralELSerializationToJSONTest extends FilterTest {

    @Test
    public void booleanAsLiteral() throws Exception {
        assertThat(json(new ClientBoolean(true)), is("{\n  \"value\" : true\n}"));
    }

    @Test
    public void dateRange() throws Exception {
        assertThat(json(new ClientRelativeDateRange("YEAR")), is("{\n  \"value\" : \"YEAR\"\n}"));
    }

    @Test
    public void integerAsLiteral() throws Exception {
        assertThat(json(new ClientInteger(555)), is(
                "{\n" +
                "  \"value\" : 555\n" +
                "}"));
    }

    @Test
    public void byteAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientByte(new Byte("123"))), is(
                "{\n" +
                        "  \"value\" : 123\n" +
                        "}"));
    }

    @Test
    public void shortAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientShort(new Short("123"))), is(
                "{\n" +
                        "  \"value\" : 123\n" +
                        "}"));
    }

    @Test
    public void integerAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientInteger(new Integer("123"))), is(
                "{\n" +
                        "  \"value\" : 123\n" +
                        "}"));
    }

    @Test
    public void longAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientLong(new Long("123"))), is(
                "{\n" +
                        "  \"value\" : \"123\"\n" +
                        "}"));
    }

    @Test
    public void bigIntegerAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientBigInteger(new BigInteger("999999999999999999999999999999999999999999999999"))), is(
                "{\n" +
                        "  \"value\" : \"999999999999999999999999999999999999999999999999\"\n" +
                        "}"));
    }

    @Test
    public void floatAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientFloat("9.99")), is(
                "{\n" +
                        "  \"value\" : 9.99\n" +
                        "}"));
    }

    @Test
    public void doubleAsLiteral_valueIsString() throws Exception {
        assertThat(json(new ClientDouble("9.99")), is(
                "{\n" +
                        "  \"value\" : 9.99\n" +
                        "}"));
    }

    @Test
    public void bigDecimalAsLiteral_valueIsString() throws Exception {
        Locale.setDefault(Locale.FRANCE);
        assertThat(json(new ClientBigDecimal("9.99999999999999999999999999999999999999999999999")), is(
                "{\n" +
                        "  \"value\" : \"9.99999999999999999999999999999999999999999999999\"\n" +
                        "}"));
    }

    @Test
    public void timeAsLiteral() throws Exception {
        assertThat(json(new ClientTime(DateFixtures.TIME_15_00)), is(
                "{\n" +
                        "  \"value\" : \"15:00:00.000\"\n" +
                        "}"));
    }

    @Test
    public void timestampAsLiteral() throws Exception {
        assertThat(json(new ClientTimestamp(DateFixtures.DATE_2009_1_1T0_0_0)), is(
                "{\n" +
                        "  \"value\" : \"2009-01-01 00:00:00.000\"\n" +
                        "}"));
    }

    @Test
    public void dateAsLiteral() throws Exception {
        assertThat(json(new ClientDate(DateFixtures.DATE_1961_08_26)), is(
                "{\n" +
                        "  \"value\" : \"1961-08-26\"\n" +
                        "}"));
    }

    @Test
    public void decimalAsLiteral() throws Exception {
        assertThat(json(ClientBigDecimal.valueOf("5.0")), is(
                "{\n" +
                        "  \"value\" : \"5.0\"\n" +
                        "}"));
    }

    @Test
    public void stringAsLiteral() throws Exception {
        assertThat(json(new ClientString("Talking Heads")), is(
                "{\n" +
                        "  \"value\" : \"Talking Heads\"\n" +
                        "}"));
    }


}
