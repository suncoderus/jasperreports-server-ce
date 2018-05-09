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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientQueryGroupBy;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class QueryELDeserializationFromXMLTest extends QueryTest {

    @Test
    public void ensureSelectZeroField() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <select />\n" +
                "</multiLevelQuery>";

        ClientMultiLevelQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getSelect().getFields(), nullValue());
    }

    @Test
    public void ensureSelectOneField() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>sales</field>\n" +
                "            <format>yyyy-MM-dd</format>\n" +
                "            <id>fieldName</id>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "</multiLevelQuery>";

        ClientMultiLevelQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getSelect().getFields().size(), is(1));
    }

    @Test
    public void ensureSelectTwoFields() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>\n" +
                "                <field>sales</field>\n" +
                "                <format>yyyy-MM-dd</format>\n" +
                "                <id>fieldName</id>\n" +
                "            </field>\n" +
                "            <field>\n" +
                "                <field>city</field>\n" +
                "                <id>fieldName2</id>\n" +
                "            </field>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "</multiLevelQuery>";

        ClientMultiLevelQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getSelect().getFields().size(), is(2));
    }

    @Test
    public void ensureSelectOneField_groupByCity() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <groupBy>\n" +
                "        <fieldName>city</fieldName>\n" +
                "        <id>g1</id>\n" +
                "    </groupBy>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>sales</field>\n" +
                "            <format>yyyy-MM-dd</format>\n" +
                "            <id>fieldName</id>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "</multiLevelQuery>";

        ClientMultiLevelQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getGroupBy(), is(instanceOf(ClientQueryGroupBy.class)));
        assertThat(cq.getSelect().getFields().size(), is(1));
    }

    @Test
    public void ensureSelectTwoFields_groupByCity() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>\n" +
                "                <field>sales</field>\n" +
                "                <id>fieldName1</id>\n" +
                "            </field>\n" +
                "            <field>\n" +
                "                <field>freight</field>\n" +
                "                <id>fieldName2</id>\n" +
                "            </field>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "    <groupBy>\n" +
                "        <group>\n" +
                "            <field>city</field>\n" +
                "            <id>g1</id>\n" +
                "        </group>\n" +
                "    </groupBy>\n" +
                "</multiLevelQuery>";

        ClientMultiLevelQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getGroupBy(), is(instanceOf(ClientQueryGroupBy.class)));
        assertThat(cq.getSelect().getFields().size(), is(2));
        assertThat(cq.getSelect().getFields().get(0).getId(), is("fieldName1"));
        assertThat(cq.getSelect().getFields().get(0).getFieldName(), is("sales"));
        assertThat(cq.getSelect().getFields().get(1).getId(), is("fieldName2"));
        assertThat(cq.getSelect().getFields().get(1).getFieldName(), is("freight"));
    }

    @Test
    public void ensureSelectOneAggregate_groupByCity() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <aggregations>\n" +
                "            <aggregation>\n" +
                "                <functionName>Average</functionName>\n" +
                "                <fieldRef>sales</fieldRef>\n" +
                "            </aggregation>\n" +
                "        </aggregations>\n" +
                "        <fields/>\n" +
                "    </select>\n" +
                "    <groupBy>\n" +
                "        <group>\n" +
                "            <field>city</field>\n" +
                "            <id>g1</id>\n" +
                "        </group>\n" +
                "    </groupBy>\n" +
                "</multiLevelQuery>";

        ClientMultiLevelQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getGroupBy(), is(instanceOf(ClientQueryGroupBy.class)));
        assertThat(cq.getSelect().getAggregations().size(), is(1));
        assertThat(cq.getSelect().getAggregations().get(0).getFieldReference(), is("sales"));
        assertThat(cq.getSelect().getAggregations().get(0).getAggregateFunction(), is("Average"));
        assertThat(cq.getSelect().getAggregations().get(0).getAggregateExpression(), is(nullValue()));
    }

    @Test
    public void ensureSelectOneField_groupByCity_and_filter() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <groupBy>\n" +
                "        <fieldName>city</fieldName>\n" +
                "        <id>g1</id>\n" +
                "    </groupBy>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>sales</field>\n" +
                "            <format>yyyy-MM-dd</format>\n" +
                "            <id>fieldName</id>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "    <where>\n" +
                "        <filterExpression>\n" +
                "            <equals>\n" +
                "                <operands>\n" +
                "                    <variable name=\"sales\"/>\n" +
                "                    <integer>\n" +
                "                        <value>1</value>\n" +
                "                    </integer>\n" +
                "                </operands>\n" +
                "            </equals>\n" +
                "        </filterExpression>\n" +
                "    </where>\n" +
                "</multiLevelQuery>";

        ClientMultiLevelQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getGroupBy(), is(instanceOf(ClientQueryGroupBy.class)));
        assertThat(cq.getSelect().getFields().size(), is(1));
        assertThat(cq.getWhere().getFilterExpression().getObject(), is(instanceOf(ClientEquals.class)));

        final ClientEquals filterExpressionObject = (ClientEquals) cq.getWhere().getFilterExpression().getObject();
        assertThat(filterExpressionObject.getLhs(), is(instanceOf(ClientVariable.class)));
        assertThat(filterExpressionObject.getLhs().toString(), is("sales"));
        assertThat(filterExpressionObject.getRhs().toString(), is("1"));
    }

    @Test
    @Ignore
    public void ensureSelectOneField_groupByCity_and_filterAsString() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <groupBy>\n" +
                "        <fieldName>city</fieldName>\n" +
                "        <id>g1</id>\n" +
                "    </groupBy>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>sales</field>\n" +
                "            <format>yyyy-MM-dd</format>\n" +
                "            <id>fieldName</id>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "    <where>\n" +
                "        <filterExpression>\n" +
                "            <string>sales == 1</string>\n" +
                "        </filterExpression>\n" +
                "    </where>\n" +
                "</multiLevelQuery>";

        ClientMultiLevelQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getGroupBy(), is(instanceOf(ClientQueryGroupBy.class)));
        assertThat(cq.getSelect().getFields().size(), is(1));
        assertThat(cq.getWhere().getFilterExpression().getExpression(), is("sales == 1"));
    }


}