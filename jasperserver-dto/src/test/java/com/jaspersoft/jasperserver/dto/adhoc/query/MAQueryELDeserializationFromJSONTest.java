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

import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientExpandable;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientLevelExpansion;
import com.jaspersoft.jasperserver.dto.adhoc.query.expansion.ClientMemberExpansion;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientMultiAxisGroupBy;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxisEnum;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientLevelAxis;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientPathOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 2/3/16 9:00 AM
 */
public class MAQueryELDeserializationFromJSONTest extends QueryTest {

    @Test
    public void ensureGroupBy_isProperImpl() throws Exception {
        String json = "{\n" +
                "  \"select\" : {\n" +
                "    \"aggregations\" : [ {\n" +
                "      \"id\" : \"id\",\n" +
                "      \"fieldRef\" : \"sales\"\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"groupBy\" : {\n" +
                "    \"rows\" : {\n" +
                "      \"items\" : [ {\n" +
                "        \"level\" : {\n" +
                "          \"id\" : \"l1\",\n" +
                "          \"field\" : \"level1\"\n" +
                "        }\n" +
                "      } ]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientQuery cq = dtoFromJSONString(json, ClientMultiAxisQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiAxisQuery.class)));
        assertThat(cq.getGroupBy(), is(instanceOf(ClientMultiAxisGroupBy.class)));
    }

    @Test
    public void testSimpleMultiaxisQueryDeserialization() throws Exception {
        String queryJsonString = fixture("query/SimpleMultiAxisQuery.json");

        ClientMultiAxisQuery query = dtoFromJSONString(queryJsonString, ClientMultiAxisQuery.class);


        ClientSelect select = query.getSelect();
        assertThat(select.getAggregations().size(), equalTo(3));

        ClientMultiAxisGroupBy groupBy = query.getGroupBy();
        ClientLevelAxis rowAxis = groupBy.getAxis(ClientGroupAxisEnum.ROWS);
        assertThat(rowAxis.getItems().size(), equalTo(2));
        assertThat(groupBy.getAxis(1).getItems().size(), equalTo(2));

        assertThat(rowAxis.get(0).getFieldName(), Matchers.is("state"));
        assertThat(rowAxis.get(1).getId(), Matchers.is("__levelAggregations__"));

        ClientLevelAxis columnAxis = groupBy.getAxis(ClientGroupAxisEnum.COLUMNS);
        assertThat(columnAxis.getItems().size(), equalTo(4));
        assertThat(groupBy.getAxis(0).getItems().size(), equalTo(4));

        assertThat(columnAxis.get(0).getId(), nullValue());
        assertThat(columnAxis.get(0).getFieldName(), equalTo("(All)"));
        assertThat(columnAxis.get(0).getDimension(), equalTo("Category"));
        assertThat(columnAxis.get(1).getId(), equalTo("open_date1"));
        assertThat(columnAxis.get(1).getCategorizer(), equalTo("year"));
        assertThat(columnAxis.get(2).getId(), equalTo("open_date2"));
        assertThat(columnAxis.get(2).getCategorizer(), equalTo("quarter"));
        assertThat(columnAxis.get(3).getId(), nullValue());
        assertThat(columnAxis.get(3).getFieldName(), equalTo("All"));
        assertThat(columnAxis.get(3).getDimension(), equalTo("Product"));


        List<ClientExpandable> columnExpansions = columnAxis.getExpansions();
        assertThat(columnExpansions.size(), Matchers.is(3));

        ClientLevelExpansion level = (ClientLevelExpansion) columnExpansions.get(0);
        assertThat(level.isExpanded(), Matchers.is(true));
        assertThat(level.getLevelReference(), Matchers.is("open_date1"));

        assertThat(columnExpansions.get(1).isExpanded(), Matchers.is(false));

        assertThat(columnExpansions.get(2).isExpanded(), Matchers.is(true));

        List<ClientExpandable> rowExpansions = rowAxis.getExpansions();
        assertThat(rowExpansions.size(), Matchers.is(1));

        ClientMemberExpansion member = (ClientMemberExpansion) rowExpansions.get(0);
        assertThat(member.isExpanded(), Matchers.is(true));
        assertThat(member.getPath(), equalTo(Arrays.asList("USA", "CA")));

        List<? extends ClientOrder> order = query.getOrderBy();

        assertThat(order.get(0).isAscending(), Matchers.is(false));
        assertThat(((ClientPathOrder) order.get(0)).getPath(), equalTo(Collections.singletonList("2004")));
    }
}
