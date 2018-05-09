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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 * @version $Id$
 */
public class QueryELSerializationToXMLTest extends QueryTest {

    @Test
    public void ensureSelectZeroField() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select()
                .build();

        assertThat(xml(cq), is("<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields/>\n" +
                "    </select>\n" +
                "</multiLevelQuery>"));

    }

    @Test
    public void ensureSelectOneField() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                .build();


        assertThat(xml(cq), is("<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>\n" +
                "                <field>sales</field>\n" +
                "                <id>fieldName</id>\n" +
                "            </field>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "</multiLevelQuery>"));

    }

    @Test
    public void ensureSelectTwoFields() throws Exception {
        ClientMultiLevelQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"),
                        new ClientQueryField().setFieldName("city").setId("fieldName2"))
                .build();


        assertThat(xml(cq), is("<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>\n" +
                "                <field>sales</field>\n" +
                "                <id>fieldName</id>\n" +
                "            </field>\n" +
                "            <field>\n" +
                "                <field>city</field>\n" +
                "                <id>fieldName2</id>\n" +
                "            </field>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "</multiLevelQuery>"));
    }

    @Test
    public void ensureSelectOneField_groupByCity() throws Exception {
        ClientQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                .build();


        assertThat(xml(cq), is("<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>\n" +
                "                <field>sales</field>\n" +
                "                <id>fieldName</id>\n" +
                "            </field>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "    <groupBy>\n" +
                "        <group>\n" +
                "            <field>city</field>\n" +
                "            <id>g1</id>\n" +
                "        </group>\n" +
                "    </groupBy>\n" +
                "</multiLevelQuery>"));

    }
    @Test
    public void ensureSelectTwoFields_groupByCity() throws Exception {
        ClientQuery cq = MultiLevelQueryBuilder
                .select(
                        new ClientQueryField().setFieldName("sales").setId("fieldName1"),
                        new ClientQueryField().setFieldName("freight").setId("fieldName2"))
                .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                .build();


        assertThat(xml(cq), is("<multiLevelQuery>\n" +
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
                "</multiLevelQuery>"));

    }
    @Test
    public void ensureSelectOneAggregation_groupByCity() throws Exception {
        ClientQuery cq = MultiLevelQueryBuilder
                .select(Collections.<ClientQueryField>emptyList(), Arrays.asList(
                        new ClientQueryAggregatedField().setFieldReference("sales").setAggregateFunction("Average")))
                .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                .build();


        assertThat(xml(cq), is("<multiLevelQuery>\n" +
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
                "</multiLevelQuery>"));

    }
    @Test
    public void ensureSelectOneField_groupByCity_and_filter() throws Exception {
        ClientQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                .where(new ClientEquals(Arrays.asList(new ClientVariable("sales"), new ClientInteger(1))))
                .build();


        assertThat(xml(cq), is("<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>\n" +
                "                <field>sales</field>\n" +
                "                <id>fieldName</id>\n" +
                "            </field>\n" +
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
                "    <groupBy>\n" +
                "        <group>\n" +
                "            <field>city</field>\n" +
                "            <id>g1</id>\n" +
                "        </group>\n" +
                "    </groupBy>\n" +
                "</multiLevelQuery>"));

    }

    @Test
    @Ignore
    public void ensureSelectOneField_groupByCity_and_filterAsString() throws Exception {
        ClientQuery cq = MultiLevelQueryBuilder
                .select(new ClientQueryField().setFieldName("sales").setId("fieldName"))
                .groupBy(new ClientQueryGroup().setFieldName("city").setId("g1"))
                .where("sales == 1")
                .build();


        assertThat(xml(cq), is("<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>\n" +
                "                <field>sales</field>\n" +
                "                <id>fieldName</id>\n" +
                "            </field>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "    <where>\n" +
                "        <filterExpression>\n" +
                "            <string>sales == 1</string>\n" +
                "        </filterExpression>\n" +
                "    </where>\n" +
                "    <groupBy>\n" +
                "        <group>\n" +
                "            <field>city</field>\n" +
                "            <id>g1</id>\n" +
                "        </group>\n" +
                "    </groupBy>\n" +
                "</multiLevelQuery>"));

    }


    @Test
    @Ignore
    public void simpleMultiLevelQuerySerialization() throws Exception {
        String originalXmlString = fixture("query/SimpleMultiLevelQuery.xml");

        ClientMultiLevelQuery query = dtoFromXMLString(originalXmlString, ClientMultiLevelQuery.class);

        String resultingXmlString = xml(query);
        assertThat(resultingXmlString, is(originalXmlString));
    }
}