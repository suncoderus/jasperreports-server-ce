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

import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientLevelExpansion;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientGenericOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientAggregates.aggregate;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientAggregates.sum;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.asc;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.desc;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.source;
import static com.jaspersoft.jasperserver.dto.adhoc.query.ClientQueryBuilder.topN;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiAxisQueryBuilder.aggRef;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiAxisQueryBuilder.allLevel;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiAxisQueryBuilder.collapseMember;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiAxisQueryBuilder.expand;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiAxisQueryBuilder.expandMember;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiAxisQueryBuilder.level;
import static com.jaspersoft.jasperserver.dto.adhoc.query.MultiAxisQueryBuilder.select;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

/**
 * @author askorodumov
 * @version $Id: Id $
 */
@RunWith(Parameterized.class)
public class ClientMultiAxesQueryExecutionTest {
    private static final String TYPE_STRING = "java.lang.String";
    private static final String TYPE_DOUBLE = "java.lang.Double";

    private static final String DATASOURCE_URI = "/public/Samples/Ad_Hoc_Views/just_a_data_source";

    private static final String SALES_STORE_REGION_SALES_CITY = "sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_city";
    private static final String SALES_STORE_REGION_SALES_COUNTRY = "sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_country";
    private static final String SALES_STORE_REGION_SALES_STATE = "sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_state_province";

    private static final String OPENED_DATE_FIELD_NAME = "sales_fact_ALL.sales__store.sales__store__store_features.sales__store__store_features__first_opened_date";

    private static final String STORE_SALES = "sales_fact_ALL.sales_fact_ALL__store_sales_2013";
    private static final String STORE_COST = "sales_fact_ALL.sales_fact_ALL__store_cost_2013";

    private String testName;
    private ClientMultiAxesQueryExecution execution;

    public ClientMultiAxesQueryExecutionTest(String testName, ClientMultiAxesQueryExecution execution) {
        this.testName = testName;
        this.execution = execution;
    }

    @Test
    public void copyConstructor() throws Exception {
        ClientMultiAxesQueryExecution actual = new ClientMultiAxesQueryExecution(execution);
        assertEquals(execution, actual);
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "GroupMeasuresByFewRowsLevelsAndExpandMember",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)))
                                        .groupByRows(
                                                level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING)),
                                                level("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                                aggRef())
                                        .expandInRows(expandMember("/OR"))
                                .build(), DATASOURCE_URI)
                },
                {
                        "SELECT_SumSales_GROUPBY_ROWS_Cities_ORDER_Top5CostWithOthers",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)),
                                        sum(source(STORE_COST, TYPE_DOUBLE)).setId("Sumcost1"))
                                        .groupByColumns(aggRef())
                                        .groupByRows(
                                                level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING)),
                                                level("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                        .expandInRows(level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING)))
                                        .orderBy(
                                                topN(singletonList(STORE_COST), 5).setCreateOtherBucket(true),
                                                asc(level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING))))
                                .build(), DATASOURCE_URI)
                },
                {
                        "SELECT_SumSales_GROUPBY_ROWS_states_ORDER_Top2CostWithoutOthers",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_COST, TYPE_DOUBLE)))
                                        .groupByColumns(aggRef())
                                        .groupByRows(
                                                level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING))
                                                        .setIncludeAll(true))
                                        .orderBy(topN(singletonList(STORE_COST), 2).setCreateOtherBucket(false))
                                        .build(), DATASOURCE_URI)
                },
                {
                        "GroupMeasuresByColumnLevelsAndExpandAndToggleMember",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)))
                                        .groupByColumns(
                                                level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING)),
                                                level("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                                aggRef())
                                        .expandInColumns(expand("state1"), collapseMember("/OR"))
                                .build(), DATASOURCE_URI)
                },
                {
                        "SELECT_AGG_ShippedDate_GROUP_BY_COLUMNS_ShippedDate",
                        new ClientMultiAxesQueryExecution(
                                select(new ClientQueryAggregatedField().setFieldReference("ShippedDate"))
                                        .groupByColumns(new ClientQueryLevel().setFieldName("ShippedDate"))
                                        .groupByRows(aggRef())
                                .build(), DATASOURCE_URI)
                },
                {
                        "NOT_support_allLevel",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)))
                                        .groupByColumns(allLevel(null, null))
                                .build(), DATASOURCE_URI)
                },
                {
                        "GROUP_ROWS_CountryStateAndCity_EXPAND_allMember",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)))
                                        .groupByRows(
                                                level("country1", source(SALES_STORE_REGION_SALES_COUNTRY, TYPE_STRING)),
                                                level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING)),
                                                level("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                                aggRef())
                                        .expandInRows("country1", "state1", "city1")
                                .build(), DATASOURCE_URI)
                },
                {
                        "GROUP_BY_COLUMNS_ShippedDate_ShippedDate",
                        new ClientMultiAxesQueryExecution(
                                select()
                                        .groupByColumns(
                                            new ClientQueryLevel(
                                                    new ClientQueryLevel().setFieldName("ShippedDate")).setId("date1"),
                                            new ClientQueryLevel().setFieldName("ShippedDate"))
                                        .expandInColumns("date1")
                                        .groupByRows(aggRef())
                                .build(), DATASOURCE_URI)
                },
                {
                        "queryWithLimit",
                        new ClientMultiAxesQueryExecution(
                                select()
                                        .groupByColumns(
                                            new ClientQueryLevel(new ClientQueryLevel().setFieldName("ShippedDate"))
                                                    .setCategorizer("year")
                                                    .setId("date1"),
                                            new ClientQueryLevel().setFieldName("ShippedDate"))
                                        .expandInColumns("date1")
                                        .groupByRows(aggRef())
                                        .limit(10)
                                .build(), DATASOURCE_URI)
                },
                {
                        "Expand_Measures_Level_If_Specified",
                        new ClientMultiAxesQueryExecution(
                                select(
                                        new ClientQueryAggregatedField().setFieldReference("Freight"),
                                        new ClientQueryAggregatedField().setFieldReference("EmployeeID"))
                                        .groupByRows(
                                                new ClientQueryLevel().setFieldName("ShipCountry"),
                                                aggRef(),
                                                new ClientQueryLevel().setFieldName("ShipCity"))
                                        .expandInRows(new ClientLevelExpansion()
                                        .setAggregationLevel(true)
                                        .setExpanded(true))
                                .build(), DATASOURCE_URI)
                },
                {
                        "GROUP_BY_ROWS_ShippedDate_CAT_Year_ShippedDate",
                        new ClientMultiAxesQueryExecution(
                                select()
                                        .groupByRows(
                                            new ClientQueryLevel(new ClientQueryLevel().setFieldName("ShippedDate"))
                                                    .setId("date1").setCategorizer("year"),
                                            new ClientQueryLevel().setFieldName("ShippedDate"))
                                        .expandInRows("date1")
                                .build(), DATASOURCE_URI)
                },
                {
                        "not_Expand_Measures_Level_ByDefault",
                        new ClientMultiAxesQueryExecution(
                                select(
                                        new ClientQueryAggregatedField().setFieldReference("Freight"),
                                        new ClientQueryAggregatedField().setFieldReference("EmployeeID"))
                                        .groupByRows(
                                                new ClientQueryLevel().setFieldName("ShipCountry"),
                                                aggRef(),
                                                new ClientQueryLevel().setFieldName("ShipCity"))
                                .build(), DATASOURCE_URI)
                },
                {
                        "SELECT_AGG_Date",
                        new ClientMultiAxesQueryExecution(
                                select(new ClientQueryAggregatedField().setFieldReference(OPENED_DATE_FIELD_NAME))
                                        .groupByColumns(aggRef())
                                .build(), DATASOURCE_URI)
                },
                {
                        "SELECT_SumSales_GROUPBY_ROWS_States_Cities_ORDER_DescState_AscCity",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)))
                                        .groupByColumns(aggRef())
                                        .groupByRows(
                                                level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING)),
                                                level("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)))
                                        .expandInRows("state1")
                                        .orderBy(
                                                desc(level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING))),
                                                asc(level("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING))))
                                .build(), DATASOURCE_URI)
                },
                {
                        "AggregateFieldWithDifferentType_AND_aggregationsShouldBeReturnedAsNumberTypes",
                        new ClientMultiAxesQueryExecution(
                                select(
                                    aggregate(source("store_sales")),
                                    aggregate(source("store_store_number")),
                                    aggregate(source("store_store_type")),
                                    aggregate(source("store_first_opened_date")),
                                    aggregate(source("store_video_store")))
                                        .groupByRows(aggRef())
                                        .groupByColumns(level("store_coffee_bar_id", source("store_coffee_bar")))
                                .build(), DATASOURCE_URI)
                },
                {
                        "GroupByCountryAndRegion_EXPAND_CountryLevelAndUsaMember_returnExpandedCountryLevelAndUsaMember",
                        new ClientMultiAxesQueryExecution(
                                select(new ClientQueryAggregatedField().setFieldReference("Freight"))
                                        .groupByColumns(aggRef())
                                        .groupByRows(
                                                new ClientQueryLevel().setFieldName("ShipCountry"),
                                                new ClientQueryLevel().setFieldName("ShipRegion"))
                                        .expandInRows(expand("ShipCountry"), expandMember("USA"))
                                .build(), DATASOURCE_URI)
                },
                {
                        "GroupMeasuresByFewColumnLevelsAndExpandMember",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)))
                                        .groupByColumns(
                                                level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING)),
                                                level("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                                aggRef())
                                        .expandInColumns(expandMember("/OR"))
                                .build(), DATASOURCE_URI)
                },
                {
                        "GroupMeasuresByFewRowsLevelsAndExpandMember",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)))
                                        .groupByRows(
                                                level("state1", source(SALES_STORE_REGION_SALES_STATE, TYPE_STRING)),
                                                level("city1", source(SALES_STORE_REGION_SALES_CITY, TYPE_STRING)),
                                                aggRef())
                                        .expandInRows(expandMember("/OR"))
                                .build(), DATASOURCE_URI)
                },
                {
                        "EmptySelect_AND_GroupColumnBySomeFields_returnEmptyDetailsData",
                        new ClientMultiAxesQueryExecution(
                                select()
                                        .groupByColumns(
                                            new ClientQueryLevel().setFieldName("store_store_type"),
                                            new ClientQueryLevel().setFieldName("sales_district"),
                                            new ClientQueryLevel().setFieldName("store_store_country"))
                                        .expandInColumns(
                                            new ClientLevelExpansion().setLevelReference("store_store_country")
                                                    .setExpanded(true))
                                .build(), DATASOURCE_URI)
                },
                {
                        "OrderBy_StateLevelNameWithoutUsingLevelId",
                        new ClientMultiAxesQueryExecution(
                                select(sum(source(STORE_SALES, TYPE_DOUBLE)))
                                        .groupByRows(new ClientQueryLevel().setFieldName(SALES_STORE_REGION_SALES_STATE))
                                        .groupByColumns(aggRef())
                                        .orderBy(
                                                new ClientGenericOrder().setAscending(false)
                                                        .setFieldReference(SALES_STORE_REGION_SALES_STATE))
                                .build(), DATASOURCE_URI)
                }
        });
    }
}
