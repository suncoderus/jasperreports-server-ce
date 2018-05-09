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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientLogical;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.literal;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientLogical.and;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by stas on 4/8/15.
 * @version $Id$
 */
public class LogicalOperatorELSerializationToXMLTest extends FilterTest {
    @Test
    public void ensureNot() throws Exception {
        ClientNot not = not(variable("sales").gtOrEq(literal(5)));

        assertThat(xml(not), is("<not>\n" +
                "    <operands>\n" +
                "        <greaterOrEqual>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <integer>\n" +
                "                    <value>5</value>\n" +
                "                </integer>\n" +
                "            </operands>\n" +
                "        </greaterOrEqual>\n" +
                "    </operands>\n" +
                "    <paren>true</paren>\n" +
                "</not>"));
    }

    @Test
    public void ensureWhereNot() throws Exception {
        ClientNot not = not(variable("sales").gtOrEq(literal(5)));

        assertThat(xml(new ClientWhere(not)), is("<where>\n" +
                "    <filterExpression>\n" +
                "        <not>\n" +
                "            <operands>\n" +
                "                <greaterOrEqual>\n" +
                "                    <operands>\n" +
                "                        <variable name=\"sales\"/>\n" +
                "                        <integer>\n" +
                "                            <value>5</value>\n" +
                "                        </integer>\n" +
                "                    </operands>\n" +
                "                </greaterOrEqual>\n" +
                "            </operands>\n" +
                "            <paren>true</paren>\n" +
                "        </not>\n" +
                "    </filterExpression>\n" +
                "</where>"));
    }

    @Test
    public void ensureNotAndTrue() throws Exception {
        ClientLogical not = and(not(variable("sales").gtOrEq(literal(5))), literal(1));

        assertThat(xml(not), is("<and>\n" +
                "    <operands>\n" +
                "        <not>\n" +
                "            <operands>\n" +
                "                <greaterOrEqual>\n" +
                "                    <operands>\n" +
                "                        <variable name=\"sales\"/>\n" +
                "                        <integer>\n" +
                "                            <value>5</value>\n" +
                "                        </integer>\n" +
                "                    </operands>\n" +
                "                </greaterOrEqual>\n" +
                "            </operands>\n" +
                "            <paren>true</paren>\n" +
                "        </not>\n" +
                "        <integer>\n" +
                "            <value>1</value>\n" +
                "        </integer>\n" +
                "    </operands>\n" +
                "</and>"));
    }

}