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
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientRelativeDateRange;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientAggregates.countAll;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.allGroup;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.asc;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.desc;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.field;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.group;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.source;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiLevelQueryBuilder.select;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

/**
 * @author askorodumov
 * @version $Id$
 */
@RunWith(Parameterized.class)
public class ClientMultiLevelQueryExecutionTest {
    private static final String TYPE_STRING = "java.lang.String";
    private static final String SALES_STORE_REGION_SALES_CITY =
            "sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_city";

    private static final String DATASOURCE_URI = "/public/Samples/Ad_Hoc_Views/just_a_data_source";
    private static final String STORE_SALES = "sales_fact_ALL.sales_fact_ALL__store_sales_2013";
    private static final String TYPE_DOUBLE = "java.lang.Double";

    private String testName;
    private ClientMultiLevelQueryExecution execution;

    public ClientMultiLevelQueryExecutionTest(String testName, ClientMultiLevelQueryExecution execution) {
        this.testName = testName;
        this.execution = execution;
    }

    public void setExecution(ClientMultiLevelQueryExecution execution) {
        this.execution = execution;
    }

    @Test
    public void copyConstructor() throws Exception {
        ClientMultiLevelQueryExecution actual = new ClientMultiLevelQueryExecution(execution);
        assertEquals(execution, actual);
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        Map<String, ClientExpressionContainer> params = new HashMap<String, ClientExpressionContainer>(1);
        params.put("DateP", new ClientExpressionContainer(new ClientRelativeDateRange().setValue("WEEK-1")));

        String SALES = "sales1";
        return Arrays.asList(new Object[][] {
                {
                        "executionWithField",
                        new ClientMultiLevelQueryExecution(
                                select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                                        .build(), DATASOURCE_URI)
                },
                {
                        "executionWithEmptySelect",
                        new ClientMultiLevelQueryExecution(select().build(), DATASOURCE_URI)
                },

                {
                        "executionSelectTwoFields",
                        new ClientMultiLevelQueryExecution(
                                select(new ClientQueryField().setFieldName("sales").setId("fieldName"),
                                        new ClientQueryField().setFieldName("city").setId("fieldName2"))
                                        .build(), DATASOURCE_URI)
                },
                {
                        "executionWithSelectAndGroupBy",
                        new ClientMultiLevelQueryExecution(
                                select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                                        .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                                        .build(), DATASOURCE_URI)
                },
                {
                        "executionWithSelectAndWhere",
                        new ClientMultiLevelQueryExecution(
                                (ClientMultiLevelQuery) select(
                                        new ClientQueryField().setFieldName("sales").setId("fieldName"))
                                .build()
                                        .setWhere(new ClientWhere().setParameters(params)), DATASOURCE_URI)
                },
                {
                        "executionWithSelectOrderByAndWhere",
                        new ClientMultiLevelQueryExecution(
                                select(field(SALES, source(STORE_SALES, TYPE_DOUBLE)))
                                    .where(variable(SALES).lt(new BigDecimal("0.51")))
                                    .orderBy(asc(SALES))
                                .build(), DATASOURCE_URI)
                },
                {
                        "executionWithAllGroup",
                        new ClientMultiLevelQueryExecution(
                                select(
                                        singletonList(field(SALES, source(STORE_SALES, TYPE_DOUBLE))),
                                        singletonList(countAll(field(SALES, source(STORE_SALES, TYPE_DOUBLE)))))
                                        .groupBy(allGroup())
                                .build(), DATASOURCE_URI)
                },
                {
                        "SelectSameDSFieldsTwiceWithAggregations",
                        new ClientMultiLevelQueryExecution(
                                select(
                                        field("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                        field("city2", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                        .aggregates(
                                                countAll(field("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING))),
                                                countAll(field("city2", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING))))
                                .build(), DATASOURCE_URI)
                },
                {
                        "SortAscDetailsSalesFieldGroupedByCities",
                        new ClientMultiLevelQueryExecution(
                                select(field(SALES, source(STORE_SALES, TYPE_DOUBLE)))
                                        .orderBy(asc(SALES))
                                        .groupBy(group("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                .build(), DATASOURCE_URI)
                },
                {
                        "SELECT_StoreName_AND_AGGREGATE_SalseFact_retunrNameDetailsAndSalesFactTotal",
                        new ClientMultiLevelQueryExecution(
                                select(
                                        new ClientQueryField().setId("name")
                                                .setFieldName("sales_fact_ALL.sales__store.sales__store__store_name"))
                                        .aggregates(
                                                new ClientQueryAggregatedField()
                                                        .setFieldReference("sales_fact_ALL.sales_fact_ALL__store_sales_98")
                                                        .setAggregateFunction("Sum"))
                                .build(), DATASOURCE_URI)
                },
                {
                        "GroupSalesByCity",
                        new ClientMultiLevelQueryExecution(
                                select(
                                        singletonList(field(SALES, source(STORE_SALES, TYPE_DOUBLE))),
                                        singletonList(countAll(field(SALES, source(STORE_SALES, TYPE_DOUBLE)))))
                                        .groupBy(group(
                                                "city1",
                                                "city",
                                                source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                .build(), DATASOURCE_URI)
                },
                {
                        "orderByTwoFields",
                        new ClientMultiLevelQueryExecution(
                                select(field("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                        .orderBy(
                                                desc(source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                                asc(source(STORE_SALES, TYPE_DOUBLE))
                                                )
                                .build(), DATASOURCE_URI)
                }
        });
    }
}
