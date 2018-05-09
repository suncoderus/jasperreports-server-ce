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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.function;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.nullLiteral;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id:$
 */
public class NullELSerializationToXMLTest extends FilterTest {

    @Test
    public void ensureXML() throws Exception {
        ClientExpression aNull = nullLiteral();

        assertThat(xml(aNull), is("<NULL/>"));
    }

    @Test
    public void ensureComparisonXml() throws Exception {
        ClientExpression expression = variable("sales").notEq(nullLiteral());

        assertThat(xml(expression), is(
                "<notEqual>\n" +
                        "    <operands>\n" +
                        "        <variable name=\"sales\"/>\n" +
                        "        <NULL/>\n" +
                        "    </operands>\n" +
                        "</notEqual>"));
    }


    @Test
    public void ensureFilterComparisonXml() throws Exception {
        ClientExpression expression = variable("sales").notEq(nullLiteral());
        ClientWhere w = new ClientWhere(expression);

        assertThat(xml(w), is(
                "<where>\n" +
                        "    <filterExpression>\n" +
                        "        <notEqual>\n" +
                        "            <operands>\n" +
                        "                <variable name=\"sales\"/>\n" +
                        "                <NULL/>\n" +
                        "            </operands>\n" +
                        "        </notEqual>\n" +
                        "    </filterExpression>\n" +
                        "</where>"));
    }

    @Test
    public void ensureFilterFunctionXml() throws Exception {
        ClientExpression expression = function("has", variable("sales"), nullLiteral());
        ClientWhere w = new ClientWhere(expression);

        assertThat(xml(w), is(
                "<where>\n" +
                        "    <filterExpression>\n" +
                        "        <function>\n" +
                        "            <operands>\n" +
                        "                <variable name=\"sales\"/>\n" +
                        "                <NULL/>\n" +
                        "            </operands>\n" +
                        "            <functionName>has</functionName>\n" +
                        "        </function>\n" +
                        "    </filterExpression>\n" +
                        "</where>"));
    }


}