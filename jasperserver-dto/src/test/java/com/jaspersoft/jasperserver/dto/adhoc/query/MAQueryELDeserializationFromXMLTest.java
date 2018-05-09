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

import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientMultiAxisGroupBy;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 2/3/16 9:00 AM
 */
public class MAQueryELDeserializationFromXMLTest extends QueryTest {

    @Test
    public void ensureGroupBy_isProperImpl() throws Exception {
        String xml = "<multiAxisQuery>\n" +
                "    <groupBy>\n" +
                "        <entry>\n" +
                "            <key>rows</key>\n" +
                "            <value>\n" +
                "                <items>\n" +
                "                    <fieldName>level1</fieldName>\n" +
                "                    <id>l1</id>\n" +
                "                </items>\n" +
                "            </value>\n" +
                "        </entry>\n" +
                "    </groupBy>\n" +
                "    <select>\n" +
                "        <aggregations>\n" +
                "            <field>sales</field>\n" +
                "            <id>id</id>\n" +
                "        </aggregations>\n" +
                "    </select>\n" +
                "</multiAxisQuery>";

        ClientQuery cq = dto(xml);

        assertThat(cq, is(instanceOf(ClientMultiAxisQuery.class)));
        assertThat(cq.getGroupBy(), is(instanceOf(ClientMultiAxisGroupBy.class)));
    }
}
