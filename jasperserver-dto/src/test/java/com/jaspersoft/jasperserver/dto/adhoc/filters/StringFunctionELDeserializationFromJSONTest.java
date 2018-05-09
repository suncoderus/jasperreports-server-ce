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

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class StringFunctionELDeserializationFromJSONTest extends FilterTest {

    public static final String STARTS_WITH = "startsWith";
    public static final String ENDS_WITH = "endsWith";
    public static final String CONTAINS = "contains";

    @Test
    public void ensureStartsWithInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"function\" : {\n" +
                "        \"functionName\" : \"startsWith\",\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"string\" : {\n" +
                "            \"value\" : \"David Byrne\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientFunction function = (ClientFunction) w.getFilterExpression().getObject();

        assertThat(function, is(instanceOf(ClientFunction.class)));
        assertThat(function.getFunctionName(), is(STARTS_WITH));
        assertThat(function.getOperator(), is(ClientFunction.FUNCTION_ID));
        assertThat(function.getOperands().size(), is(2));
        assertThat(function.getOperands().get(0), is(instanceOf(ClientVariable.class)));
        assertThat(function.getOperands().get(1), is(instanceOf(ClientString.class)));
    }

    @Test
    public void ensureEndsWithInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"function\" : {\n" +
                "        \"functionName\" : \"endsWith\",\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"string\" : {\n" +
                "            \"value\" : \"David Byrne\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientFunction function = (ClientFunction) w.getFilterExpression().getObject();

        assertThat(function, is(instanceOf(ClientFunction.class)));
        assertThat(function.getFunctionName(), is(ENDS_WITH));
        assertThat(function.getOperator(), is(ClientFunction.FUNCTION_ID));
        assertThat(function.getOperands().size(), is(2));
        assertThat(function.getOperands().get(0), is(instanceOf(ClientVariable.class)));
        assertThat(function.getOperands().get(1), is(instanceOf(ClientString.class)));
    }

    @Test
    public void ensureContainsInWhere() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"function\" : {\n" +
                "        \"functionName\" : \"contains\",\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"string\" : {\n" +
                "            \"value\" : \"David Byrne\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);
        ClientFunction function = (ClientFunction) w.getFilterExpression().getObject();

        assertThat(function, is(instanceOf(ClientFunction.class)));
        assertThat(function.getFunctionName(), is(CONTAINS));
        assertThat(function.getOperator(), is(ClientFunction.FUNCTION_ID));
        assertThat(function.getOperands().size(), is(2));
        assertThat(function.getOperands().get(0), is(instanceOf(ClientVariable.class)));
        assertThat(function.getOperands().get(1), is(instanceOf(ClientString.class)));
    }

}