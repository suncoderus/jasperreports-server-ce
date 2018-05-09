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
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 2/3/16 9:00 AM
 */
public class MAQueryELSerializationToXMLTest extends QueryTest {

    @Test
    public void ensureSelectOneFieldMultiDimensional_groupByRows() throws Exception {
        ClientMultiAxisQuery cq = MultiAxisQueryBuilder
                .select(new ClientQueryAggregatedField().setFieldReference("sales").setId("id"))
                .groupByRows(new ClientQueryLevel().setFieldName("level1").setId("l1"))
                .build();

        assertThat(xml(cq), is("<multiAxisQuery>\n" +
                "    <select>\n" +
                "        <aggregations>\n" +
                "            <aggregation>\n" +
                "                <fieldRef>sales</fieldRef>\n" +
                "                <id>id</id>\n" +
                "            </aggregation>\n" +
                "        </aggregations>\n" +
                "    </select>\n" +
                "    <groupBy>\n" +
                "        <rows>\n" +
                "            <items>\n" +
                "                <level>\n" +
                "                    <field>level1</field>\n" +
                "                    <id>l1</id>\n" +
                "                </level>\n" +
                "            </items>\n" +
                "        </rows>\n" +
                "    </groupBy>\n" +
                "</multiAxisQuery>"));

    }

    @Test
    @Ignore
    public void simpleMultiAxisQuerySerialization() throws Exception {
        String originalXmlString = fixture("query/SimpleMultiAxisQuery.xml");

        ClientMultiAxisQuery query = dtoFromXMLString(originalXmlString, ClientMultiAxisQuery.class);

        String resultingXmlString = xml(query);
        assertThat(resultingXmlString, is(originalXmlString));
    }
}
