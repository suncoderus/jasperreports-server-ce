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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTime;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientTimestamp;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 2/9/16 1:29 PM
 */
public class FunctionFilterELDeserializationFromJSONTest extends QueryTest {

    public static final String HIRE_DATE = "hire_date";

    @Test
    public void ensureDateEquals_timestamp() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"2009-12-01 00:00:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"equalsDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.EQUALS_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTimestamp.class))));
    }

    @Test
    public void ensureDateEquals_timestamp_filterExpression() throws Exception {
        String jsonString = "{\n" +
                "  \"where\" : {\n" +
                "    \"filterExpression\" : \"equalsDate(hire_date,ts'2009-12-01 00:00:00')\"\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getExpression(), is("equalsDate(hire_date,ts'2009-12-01 00:00:00')"));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(nullValue()));
    }

    @Test
    public void ensureDateAfter_timestamp() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"2009-12-01 00:00:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"afterDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.AFTER_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTimestamp.class))));
    }

    @Test
    public void ensureDateBetween_timestamp() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"03:33:00\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"time\" : {\n" +
                "              \"value\" : \"15:00:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"betweenDates\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.BETWEEN_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTime.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(2), is((instanceOf(ClientTime.class))));
    }

    @Test
    public void ensureDateEquals_time() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"03:33:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"equalsDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.EQUALS_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTime.class))));
    }

    @Test
    public void ensureDateAfter_time() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"15:00:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"afterDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.AFTER_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTime.class))));
    }

    @Test
    public void ensureDateBetween_time() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"03:33:00\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"time\" : {\n" +
                "              \"value\" : \"15:00:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"betweenDates\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.BETWEEN_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTime.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(2), is((instanceOf(ClientTime.class))));
    }

    @Test
    public void ensureDateEquals_date() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"2009-12-01 00:00:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"equalsDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.EQUALS_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTimestamp.class))));
    }

    @Test
    public void ensureDateAfter_date() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"2009-12-01 00:00:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"afterDate\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.AFTER_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTimestamp.class))));
    }

    @Test
    public void ensureDateBetween_date() throws Exception {
        String jsonString = "{\n" +
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
                "              \"value\" : \"03:33:00\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"time\" : {\n" +
                "              \"value\" : \"15:00:00\"\n" +
                "            }\n" +
                "          } ],\n" +
                "          \"functionName\" : \"betweenDates\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientQuery.class)));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientFunction.class)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getFunctionName(), is((ClientExpressions.BETWEEN_DATE_FUN)));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(0), is((instanceOf(ClientVariable.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(1), is((instanceOf(ClientTime.class))));
        assertThat(((ClientFunction) cq.getWhere().getFilterExpression().getObject()).getOperands().get(2), is((instanceOf(ClientTime.class))));
    }


}
