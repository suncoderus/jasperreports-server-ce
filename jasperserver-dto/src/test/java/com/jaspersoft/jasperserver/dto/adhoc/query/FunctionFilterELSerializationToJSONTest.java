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

package com.jaspersoft.jasperserver.dto.adhoc.query;

import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.adhoc.DateFixtures.DATE_1961_08_26;
import static com.jaspersoft.jasperserver.dto.adhoc.DateFixtures.DATE_1979_06_23;
import static com.jaspersoft.jasperserver.dto.adhoc.DateFixtures.DATE_2009_12_1T0_0_0;
import static com.jaspersoft.jasperserver.dto.adhoc.DateFixtures.DATE_2012_1_1T0_0_0;
import static com.jaspersoft.jasperserver.dto.adhoc.DateFixtures.TIME_15_00;
import static com.jaspersoft.jasperserver.dto.adhoc.DateFixtures.TIME_3_33;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.range;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 2/9/16 1:29 PM
 */
public class FunctionFilterELSerializationToJSONTest extends QueryTest {

    public static final String HIRE_DATE = "hire_date";

    @Test
    public void ensureDateEquals_timestamp() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).equalsDate(DATE_2009_12_1T0_0_0)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"timestamp\" : {\n" +
                "              \"value\" : \"2009-12-01 00:00:00.000\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"equalsDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureDateEquals_timestamp_expressionString() throws Exception {
        ClientQuery query =
                select("equalsDate(hire_date,ts'2009-12-01 00:00:00')");

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"string\" : \"equalsDate(hire_date,ts'2009-12-01 00:00:00')\"\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureDateAfter_timestamp() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).afterDate(DATE_2009_12_1T0_0_0)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"timestamp\" : {\n" +
                "              \"value\" : \"2009-12-01 00:00:00.000\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"afterDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureDateBetween_timestamp() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).betweenDates(DATE_2009_12_1T0_0_0, DATE_2012_1_1T0_0_0)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"timestamp\" : {\n" +
                "              \"value\" : \"2009-12-01 00:00:00.000\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"timestamp\" : {\n" +
                "              \"value\" : \"2012-01-01 00:00:00.000\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"betweenDates\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    /* Time Tests */

    @Test
    public void ensureDateEquals_time() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).equalsDate(TIME_3_33)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"time\" : {\n" +
                "              \"value\" : \"03:33:00.000\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"equalsDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureDateAfter_time() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).afterDate(TIME_15_00)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"time\" : {\n" +
                "              \"value\" : \"15:00:00.000\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"afterDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureDateBetween_time() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).betweenDates(TIME_3_33, TIME_15_00)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"time\" : {\n" +
                "              \"value\" : \"03:33:00.000\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"time\" : {\n" +
                "              \"value\" : \"15:00:00.000\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"betweenDates\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }


    @Test
    public void ensureRange_time() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).in(range(TIME_3_33, TIME_15_00))));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"in\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"range\" : {\n" +
                "              \"start\" : {\n" +
                "                \"boundary\" : {\n" +
                "                  \"time\" : {\n" +
                "                    \"value\" : \"03:33:00.000\"\n" +
                "                  }\n" +
                "                }\n" +
                "              },\n" +
                "              \"end\" : {\n" +
                "                \"boundary\" : {\n" +
                "                  \"time\" : {\n" +
                "                    \"value\" : \"15:00:00.000\"\n" +
                "                  }\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          } ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }


    @Test
    public void ensureDateEquals_date() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).equalsDate(DATE_1979_06_23)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"date\" : {\n" +
                "              \"value\" : \"1979-06-23\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"equalsDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureDateAfter_date() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).afterDate(DATE_1979_06_23)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"date\" : {\n" +
                "              \"value\" : \"1979-06-23\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"afterDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureDateBetween_date() throws Exception {
        ClientQuery query =
                select((variable(HIRE_DATE).betweenDates(DATE_1961_08_26, DATE_1979_06_23)));

        String jsonString = json(query);

        assertThat(jsonString, is("{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : {\n" +
                "      \"object\" : {\n" +
                "        \"function\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"hire_date\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"date\" : {\n" +
                "              \"value\" : \"1961-08-26\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"date\" : {\n" +
                "              \"value\" : \"1979-06-23\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"betweenDates\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }



}
