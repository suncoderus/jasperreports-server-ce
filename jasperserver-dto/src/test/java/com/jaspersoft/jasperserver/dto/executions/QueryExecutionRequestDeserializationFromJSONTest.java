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
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import org.junit.Test;

import java.util.List;

import static com.jaspersoft.jasperserver.dto.matchers.IsClientQueryField.isClientQueryField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class QueryExecutionRequestDeserializationFromJSONTest extends QueryExecutionRequestTest {

    @Test
    public void ensureSelectZeroField() throws Exception {
        String jsonString = "{\n" +
                "  \"query\" : {\n" +
                "      \"select\" : {\n" +
                "        \"fields\" : [ ],\n" +
                "        \"aggregations\" : [ ]\n" +
                "      }\n" +
                "  },\n" +
                "  \"dataSourceUri\" : \"/uri\"\n" +
                "}";

        ClientQueryExecution cq = dtoFromJSONString(jsonString, ClientMultiLevelQueryExecution.class);

        assertThat(cq.getQuery(), is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getQuery().getSelect().getFields().size(), is(0));
    }

    @Test
    public void ensureSelectOneField() throws Exception {
        String jsonString = "{\n" +
                "  \"query\" : {\n" +
                "      \"select\" : {\n" +
                "        \"fields\" : [ {\n" +
                "          \"id\" : \"fieldName\",\n" +
                "          \"field\" : \"sales\"\n" +
                "        } ],\n" +
                "        \"aggregations\" : [ ]\n" +
                "      }\n" +
                "  },\n" +
                "  \"dataSourceUri\" : \"/uri\"\n" +
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
                "      \"select\" : {\n" +
                "        \"fields\" : [ {\n" +
                "          \"id\" : \"fieldName\",\n" +
                "          \"field\" : \"sales\"\n" +
                "        }, {\n" +
                "          \"id\" : \"fieldName2\",\n" +
                "          \"field\" : \"city\"\n" +
                "        } ],\n" +
                "        \"aggregations\" : [ ]\n" +
                "      }\n" +
                "  },\n" +
                "  \"dataSourceUri\" : \"/uri\"\n" +
                "}";

        ClientQueryExecution cq = dtoFromJSONString(jsonString, ClientMultiLevelQueryExecution.class);

        assertThat(cq.getQuery(), is(instanceOf(ClientMultiLevelQuery.class)));
        List<ClientQueryField> fields = cq.getQuery().getSelect().getFields();
        assertThat(fields.size(), is(2));

        assertThat(fields.get(0), isClientQueryField("fieldName","sales", "yyyy-MM-dd"));
        assertThat(fields.get(1), isClientQueryField("fieldName2","city"));
    }

}