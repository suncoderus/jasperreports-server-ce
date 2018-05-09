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
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientQueryGroupBy;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>, Andriy Godovanets <agodovan@tibco.com
 * @date 1/27/16 3:47PM
 */
public class QueryELDeserializationFromJSONTest extends QueryTest {
    @Test
    public void testSimpleQueryDeserialization() throws Exception {
        String queryJsonString = fixture("query/SimpleMultiLevelQuery.json");

        ClientMultiLevelQuery flatQuery = dtoFromJSONString(queryJsonString, ClientMultiLevelQuery.class);

        ClientSelect flatSelect = flatQuery.getSelect();

        assertThat(flatSelect.getAggregations().size(), Matchers.is(4));
        ClientQueryAggregatedField openDateAgg = flatSelect.getAggregations().get(0);
        assertThat(openDateAgg.getAggregateExpression(), Matchers.is("CountAll(open_date)"));
        assertThat(openDateAgg.getFieldReference(), Matchers.is("open_date"));

        ClientQueryAggregatedField calcFieldAgg = flatSelect.getAggregations().get(1);
        assertThat(calcFieldAgg.getName(), Matchers.is("calcField"));
        assertThat(calcFieldAgg.getId(), Matchers.nullValue());
        assertThat(calcFieldAgg.getAggregateExpression(), Matchers.is("Sum(calcField)"));
        assertThat(calcFieldAgg.getFieldReference(), Matchers.is("calcField"));

        ClientQueryAggregatedField cityAgg = flatSelect.getAggregations().get(2);
        assertThat(cityAgg.getName(), Matchers.is("city"));
        assertThat(cityAgg.getId(), Matchers.nullValue());
        assertThat(cityAgg.getAggregateExpression(), Matchers.is("CountDistinct(city)"));
        assertThat(cityAgg.getFieldReference(), Matchers.is("city"));

        ClientQueryAggregatedField salesAgg = flatSelect.getAggregations().get(3);
        assertThat(salesAgg.getName(), Matchers.is("sales"));
        assertThat(salesAgg.getAggregateExpression(), Matchers.is("Sum(sales)"));
        assertThat(salesAgg.getFieldReference(), Matchers.is("sales"));

        assertThat(flatSelect.getFields().size(), Matchers.is(4));

        ClientQueryField open_date = flatSelect.getField("open_date");
        assertThat(open_date.getName(), Matchers.is("open_date"));
        assertThat(open_date.getFieldName(), Matchers.is("open_date"));

        ClientQueryField calcField = flatSelect.getField("calcField");
        assertThat(calcField.getName(), Matchers.is("calcField"));
        assertThat(calcField.getFieldName(), Matchers.is("calcField"));

        ClientQueryField city = flatSelect.getField("city");
        assertThat(city.getName(), Matchers.is("city"));
        assertThat(city.getFieldName(), Matchers.is("city"));

        ClientQueryField sales = flatSelect.getField("sales");
        assertThat(sales.getName(), Matchers.is("sales"));
        assertThat(sales.getFieldName(), Matchers.is("sales"));

        ClientQueryGroupBy groupBy = flatQuery.getGroupBy();

        assertThat(groupBy.getGroupAxis().get(0).getCategorizer(), equalTo("year"));
        assertThat(groupBy.getGroupAxis().get(0).getName(), equalTo("open_date"));
        assertThat(groupBy.getGroupAxis().get(0).getFieldName(), equalTo("open_date"));

        List<ClientGenericOrder> order = flatQuery.getOrderBy();
        assertThat(order.get(0).isAscending(), Matchers.is(true));
        assertThat((order.get(0)).getFieldReference(), Matchers.is("open_date1"));
        assertThat(order.get(1).isAscending(), Matchers.is(false));
        assertThat((order.get(1)).getFieldReference(), Matchers.is("country"));
        assertThat(order.get(2).isAscending(), Matchers.is(false));
        assertThat((order.get(2)).getFieldReference(), Matchers.is("city1"));
    }

    @Test
    public void ensureSelectZeroField() throws Exception {
        String jsonString = "{\n" +
                "  \"select\" : {\n" +
                "    \"fields\" : [ ],\n" +
                "    \"aggregations\" : [ ]\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getSelect().getFields().size(), is(0));
    }

    @Test
    public void ensureSelectOneField() throws Exception {
        String jsonString = "{\n" +
                "  \"select\" : {\n" +
                "    \"fields\" : [ {\n" +
                "      \"id\" : \"fieldName\",\n" +
                "      \"field\" : \"sales\"\n" +
                "    } ],\n" +
                "    \"aggregations\" : [ ]\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getSelect().getFields().size(), is(1));
    }

    @Test
    public void ensureSelectTwoFields() throws Exception {
        String jsonString = "{\n" +
                "  \"select\" : {\n" +
                "    \"fields\" : [ {\n" +
                "      \"id\" : \"fieldName\",\n" +
                "      \"field\" : \"sales\"\n" +
                "    }, {\n" +
                "      \"id\" : \"fieldName2\",\n" +
                "      \"field\" : \"city\"\n" +
                "    } ],\n" +
                "    \"aggregations\" : [ ]\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getSelect().getFields().size(), is(2));
    }

    @Test
    public void ensureSelectOneAggregate_groupByCity() throws Exception {
        String jsonString = "{\n" +
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
                "}";

        ClientMultiLevelQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getGroupBy(), is(instanceOf(ClientQueryGroupBy.class)));
        assertThat(cq.getSelect().getAggregations().size(), is(1));
        assertThat(cq.getSelect().getAggregations().get(0).getFieldReference(), is("sales"));
        assertThat(cq.getSelect().getAggregations().get(0).getAggregateFunction(), is("Average"));
        assertThat(cq.getSelect().getAggregations().get(0).getExpressionContainer(), is(nullValue()));
    }

    @Test
    public void ensureParameters() throws Exception {
        String jsonString = "{\n" +
                "  \"select\": {\n" +
                "    \"fields\": [\n" +
                "      {\n" +
                "        \"field\": \"id\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"field\": \"date_modified\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"field\": \"deleted\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"where\": {\n" +
                "    \"parameters\": [\n" +
                "      {\n" +
                "        \"name\": \"Country_multi_select\",\n" +
                "        \"expression\": {\n" +
                "          \"object\": {\n" +
                "            \"list\": {\n" +
                "              \"items\": [\n" +
                "                {\n" +
                "                  \"string\": {\n" +
                "                    \"value\": \"USA\"\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"string\": {\n" +
                "                    \"value\": \"Mexico\"\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Cascading_state_multi_select\",\n" +
                "        \"expression\": {\n" +
                "          \"object\": {\n" +
                "            \"list\": {\n" +
                "              \"items\": [\n" +
                "                {\n" +
                "                  \"string\": {\n" +
                "                    \"value\": \"CA\"\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"string\": {\n" +
                "                    \"value\": \"DF\"\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"string\": {\n" +
                "                    \"value\": \"Guerrero\"\n" +
                "                  }\n" +
                "                },\n" +
                "                {\n" +
                "                  \"string\": {\n" +
                "                    \"value\": \"OR\"\n" +
                "                  }\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Cascading_name_single_select\",\n" +
                "        \"expression\": {\n" +
                "          \"object\": {\n" +
                "            \"string\": \"Adams-Steen Transportation Holdings\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        ClientMultiLevelQuery cq = dtoFromJSONString(jsonString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getWhere().getParameters().size(), is(3));
        assertThat(cq.getWhere().getParameters().get("Country_multi_select").toString(), is("('USA', 'Mexico')"));
        assertThat(cq.getWhere().getParameters().get("Cascading_state_multi_select").toString(), is("('CA', 'DF', 'Guerrero', 'OR')"));
        assertThat(cq.getWhere().getParameters().get("Cascading_name_single_select").toString(), is("'Adams-Steen Transportation Holdings'"));
    }

}