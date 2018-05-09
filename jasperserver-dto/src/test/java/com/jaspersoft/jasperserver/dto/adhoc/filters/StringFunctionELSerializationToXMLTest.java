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

package com.jaspersoft.jasperserver.dto.adhoc.filters;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:45PM
 * @version $Id$
 */
public class StringFunctionELSerializationToXMLTest extends FilterTest {

    @Test
    public void ensureStartsWithInWhere() throws Exception {
        ClientFunction function = variable("sales").startsWith("Talking Heads");
        ClientWhere w = new ClientWhere(function);

        assertThat(xml(w), is("<where>\n" +
                "    <filterExpression>\n" +
                "        <function>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <string>\n" +
                "                    <value>Talking Heads</value>\n" +
                "                </string>\n" +
                "            </operands>\n" +
                "            <functionName>startsWith</functionName>\n" +
                "        </function>\n" +
                "    </filterExpression>\n" +
                "</where>"));
    }

    @Test
    public void ensureEndsWithInWhere() throws Exception {
        ClientFunction function = variable("sales").endsWith("Talking Heads");
        ClientWhere w = new ClientWhere(function);

        assertThat(xml(w), is("<where>\n" +
                "    <filterExpression>\n" +
                "        <function>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <string>\n" +
                "                    <value>Talking Heads</value>\n" +
                "                </string>\n" +
                "            </operands>\n" +
                "            <functionName>endsWith</functionName>\n" +
                "        </function>\n" +
                "    </filterExpression>\n" +
                "</where>"));
    }


    @Test
    public void ensureContainsInWhere() throws Exception {
        ClientFunction function = variable("sales").contains("Talking Heads");
        ClientWhere w = new ClientWhere(function);

        assertThat(xml(w), is("<where>\n" +
                "    <filterExpression>\n" +
                "        <function>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <string>\n" +
                "                    <value>Talking Heads</value>\n" +
                "                </string>\n" +
                "            </operands>\n" +
                "            <functionName>contains</functionName>\n" +
                "        </function>\n" +
                "    </filterExpression>\n" +
                "</where>"));
    }

}