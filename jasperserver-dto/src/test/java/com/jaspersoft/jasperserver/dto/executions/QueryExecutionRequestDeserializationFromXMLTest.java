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
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.QueryExecutionRequestTest;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientQueryGroupBy;
import org.junit.Test;

import java.util.List;

import static com.jaspersoft.jasperserver.dto.matchers.IsClientQueryField.isClientQueryField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class QueryExecutionRequestDeserializationFromXMLTest extends QueryExecutionRequestTest {


    @Test
    public void ensureSelectZeroField() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <select />\n" +
                "</multiLevelQuery>";

        ClientQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        assertThat(cq.getSelect().getFields(), nullValue());
    }

    @Test
    public void ensureSelectOneField() throws Exception {
        String xmlString = "<multiLevelQuery>\n" +
                "    <select>\n" +
                "        <fields>\n" +
                "            <field>\n" +
                "                <field>sales</field>\n" +
                "                <format>yyyy-MM-dd</format>\n" +
                "                <id>fieldName</id>\n" +
                "            </field>\n" +
                "        </fields>\n" +
                "    </select>\n" +
                "</multiLevelQuery>";

        ClientQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        List<ClientQueryField> fields = cq.getSelect().getFields();
        assertThat(fields.size(), is(1));

        assertThat(fields.get(0), isClientQueryField("fieldName","sales", "yyyy-MM-dd"));
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

        ClientQuery cq = dtoForEntity(xmlString, ClientMultiLevelQuery.class);

        assertThat(cq, is(instanceOf(ClientMultiLevelQuery.class)));
        List<ClientQueryField> fields = cq.getSelect().getFields();
        assertThat(fields.size(), is(2));

        assertThat(fields.get(0), isClientQueryField("fieldName","sales", "yyyy-MM-dd"));
        assertThat(fields.get(1), isClientQueryField("fieldName2","city"));
    }

    @Test
    public void ensureSelectOneField_groupByCity() throws Exception {
        String xmlString = "<queryExecution>\n" +
                "    <query>\n" +
                "        <select>\n" +
                "            <fields>\n" +
                "                <field>sales</field>\n" +
                "                <format>yyyy-MM-dd</format>\n" +
                "                <id>fieldName</id>\n" +
                "            </fields>\n" +
                "        </select>\n" +
                "        <groupBy>\n" +
                "            <group>\n" +
                "                <field>city</field>\n" +
                "                <id>g1</id>\n" +
                "            </group>\n" +
                "        </groupBy>\n" +
                "    </query>\n" +
                "    <dataSourceUri>/public/Samples/Ad_Hoc_Views/04__Product_Results_by_Store_Type</dataSourceUri>\n" +
                "</queryExecution>";

        ClientMultiLevelQueryExecution executionRequest = dtoForEntity(xmlString, ClientMultiLevelQueryExecution.class);

        assertThat(executionRequest, is(instanceOf(ClientMultiLevelQueryExecution.class)));
        ClientMultiLevelQuery cq = executionRequest.getQuery();
        List<ClientQueryField> fields = cq.getSelect().getFields();
        ClientQueryGroupBy groupBy = cq.getGroupBy();

        assertThat(fields.size(), is(1));
        assertThat(groupBy, is(instanceOf(ClientQueryGroupBy.class)));
        assertThat(groupBy.getGroups().size(), is(1));

    }

}