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

package com.jaspersoft.jasperserver.dto.executions;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiLevelQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.QueryExecutionRequestTest;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.matchers.IsClientQueryField.isClientQueryField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 * @version $Id$
 */
public class QueryExecutionDeserializationFromJSONTest extends QueryExecutionRequestTest {

    @Test
    public void ensureSelectZeroField() throws Exception {
        String jsonString = "{\n" +
                "  \"query\" : {\n" +
                "    \"select\" : {\n" +
                "      \"fields\" : [ ],\n" +
                "      \"aggregations\" : [ ]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQueryExecution cq = dtoFromJSONString(jsonString, ClientMultiLevelQueryExecution.class);

        assertThat(cq.getQuery(), is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getQuery().getSelect().getFields().size(), is(0));
    }

    @Test
    public void ensureSelectOneField() throws Exception {
        String jsonString = "{\n" +
                "\"query\" : {\n" +
                "    \"select\" : {\n" +
                "      \"fields\" : [ {\n" +
                "        \"id\" : \"fieldName\",\n" +
                "        \"field\" : \"sales\"\n" +
                "      } ],\n" +
                "      \"aggregations\" : [ ]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQueryExecution cq = dtoFromJSONString(jsonString, ClientMultiLevelQueryExecution.class);

        assertThat(cq.getQuery(), is(instanceOf(ClientMultiLevelQuery.class)));
        List<ClientQueryField> fields = cq.getQuery().getSelect().getFields();
        assertThat(fields.size(), is(1));

        assertThat(fields.get(0), isClientQueryField("fieldName","sales", "yyyy-MM-dd"));
    }

    @Test
    public void ensureSelectTwoFields() throws Exception {
        String jsonString = "{\n" +
                "  \"query\" : {\n" +
                "    \"select\" : {\n" +
                "      \"fields\" : [ {\n" +
                "        \"id\" : \"fieldName\",\n" +
                "        \"field\" : \"sales\"\n" +
                "      }, {\n" +
                "        \"id\" : \"fieldName2\",\n" +
                "        \"field\" : \"city\"\n" +
                "      } ],\n" +
                "      \"aggregations\" : [ ]\n" +
                "    }\n" +
                "}\n" +
                "}";

        ClientQueryExecution cq = dtoFromJSONString(jsonString, ClientMultiLevelQueryExecution.class);

        assertThat(cq.getQuery(), is(instanceOf(ClientMultiLevelQuery.class)));
        List<ClientQueryField> fields = cq.getQuery().getSelect().getFields();
        assertThat(fields.size(), is(2));

        assertThat(fields.get(0), isClientQueryField("fieldName","sales", "yyyy-MM-dd"));
        assertThat(fields.get(1), isClientQueryField("fieldName2","city"));
    }

    /**
     * Covers corrected case from <a href="http://jira.jaspersoft.com/browse/JRS-8192">JRS-8192</a>
     *
     * @throws Exception
     */
    @Test
    public void ensure_JRS8192() throws Exception {
        final String jsonString = "{\n" +
                "  \"dataSourceUri\":\"/organizations/organization_1/adhoc/topics/AdhocDemo\",\n" +
                "  \"query\":{\n" +
                "    \"select\":{\n" +
                "      \"fields\":[\n" +
                "        {\n" +
                "          \"field\":\"ShipCountry\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQueryExecution qer = dtoFromJSONString(jsonString, ClientMultiLevelQueryExecution.class);

        assertThat(qer, is(notNullValue()));
        assertThat(qer.getDataSourceUri(), is("/organizations/organization_1/adhoc/topics/AdhocDemo"));
        assertThat(qer.getQuery(), is(instanceOf(ClientMultiLevelQuery.class)));
    }

    @Test
    public void ensure_whereRelativeDateRangeParameter() throws Exception {
        final String jsonString = "{\n" +
                "  \"dataSourceUri\": \"/organizations/organization_1/adhoc/topics/topicsRD/EqualsDate_2\",\n" +
                "  \"query\": {\n" +
                "    \"select\": {\n" +
                "      \"fields\": [\n" +
                "        {\n" +
                "          \"field\": \"column_string\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    \"where\": {\n" +
                "      \"parameters\": [{\n" +
                "        \"name\": \"DateP\",\n" +
                "        \"expression\": {\n" +
                "          \"object\": {\n" +
                "            \"relativeDateRange\": {\n" +
                "              \"value\": \"WEEK-1\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        ClientQueryExecution qer = dtoFromJSONString(jsonString, ClientMultiLevelQueryExecution.class);

        Map<String, ClientExpressionContainer> parameters = qer.getQuery().getWhere().getParameters();

        assertThat(parameters.get("DateP").getObject(), is(instanceOf(ClientRelativeDateRange.class)));
        assertThat(((ClientRelativeDateRange) parameters.get("DateP").getObject()).getValue(), is("WEEK-1"));
    }

}