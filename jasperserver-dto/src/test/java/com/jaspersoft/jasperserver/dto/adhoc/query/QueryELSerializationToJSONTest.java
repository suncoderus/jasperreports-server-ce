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

import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class QueryELSerializationToJSONTest extends QueryTest {

    @Test
    public void testSimpleQuerySerialization() throws Exception {
        String queryJsonString = fixture("query/SimpleMultiLevelQuery.json");

        ClientMultiLevelQuery flatQuery = dtoFromJSONString(queryJsonString, ClientMultiLevelQuery.class);

        assertThat(toJsonObject(flatQuery), Matchers.equalTo(toJsonObject(queryJsonString)));
    }

    @Test
    public void ensureSelectZeroField() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select()
                .build();


        assertThat(json(cq), is("{\n" +
                "  \"select\" : { }\n" +
                "}"));

    }

    @Test
    public void ensureSelectOneField() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                .build();


        assertThat(json(cq), is("{\n" +
                "  \"select\" : {\n" +
                "    \"fields\" : [ {\n" +
                "      \"id\" : \"fieldName\",\n" +
                "      \"field\" : \"sales\"\n" +
                "    } ]\n" +
                "  }\n" +
                "}"));

    }


    @Test
    public void ensureSelectTwoFields() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"),
                        new ClientQueryField().setFieldName("city").setId("fieldName2"))
                .build();


        assertThat(json(cq), is("{\n" +
                "  \"select\" : {\n" +
                "    \"fields\" : [ {\n" +
                "      \"id\" : \"fieldName\",\n" +
                "      \"field\" : \"sales\"\n" +
                "    }, {\n" +
                "      \"id\" : \"fieldName2\",\n" +
                "      \"field\" : \"city\"\n" +
                "    } ]\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureSelectOneAggregation_groupByCity() throws Exception {
        ClientQuery cq = MultiLevelQueryBuilder
                .select(Collections.<ClientQueryField>emptyList(), Arrays.asList(
                        new ClientQueryAggregatedField().setFieldReference("sales").setAggregateFunction("Average")))
                .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                .build();


        assertThat(json(cq), is("{\n" +
                "  \"select\" : {\n" +
                "    \"aggregations\" : [ {\n" +
                "      \"functionName\" : \"Average\",\n" +
                "      \"fieldRef\" : \"sales\"\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"groupBy\" : [ {\n" +
                "    \"group\" : {\n" +
                "      \"id\" : \"g1\",\n" +
                "      \"field\" : \"city\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));

    }



}