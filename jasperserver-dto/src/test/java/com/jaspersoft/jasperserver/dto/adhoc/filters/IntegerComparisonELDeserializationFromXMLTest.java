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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientGreaterOrEqual;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientLessOrEqual;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:45PM
 */
public class IntegerComparisonELDeserializationFromXMLTest extends FilterTest {

    @Test
    public void ensureGreaterOrEqualInWhere() throws Exception {
        String xml = "<where>\n" +
                "    <filterExpression>\n" +
                "        <greaterOrEqual>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <integer>\n" +
                "                    <value>5</value>\n" +
                "                </integer>\n" +
                "            </operands>\n" +
                "        </greaterOrEqual>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere where = dto(xml);

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientGreaterOrEqual.class)));
        assertThat(((ClientGreaterOrEqual) where.getFilterExpression().getObject()).getOperator(), is(ClientGreaterOrEqual.OPERATOR_ID));
        assertThat((((ClientGreaterOrEqual) where.getFilterExpression().getObject()).getOperands().get(0)), is(instanceOf(ClientVariable.class)));
        assertThat((((ClientGreaterOrEqual) where.getFilterExpression().getObject()).getOperands().get(1)), is(instanceOf(ClientInteger.class)));

    }

    @Test
    public void ensureLessOrEqualInWhere() throws Exception {
        String xml = "<where>\n" +
                "    <filterExpression>\n" +
                "        <lessOrEqual>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <integer>\n" +
                "                    <value>5</value>\n" +
                "                </integer>\n" +
                "            </operands>\n" +
                "        </lessOrEqual>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere where = dto(xml);

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientLessOrEqual.class)));
        assertThat(((ClientLessOrEqual) where.getFilterExpression().getObject()).getOperator(), is(ClientLessOrEqual.OPERATOR_ID));
        assertThat((((ClientLessOrEqual) where.getFilterExpression().getObject()).getOperands().get(0)), is(instanceOf(ClientVariable.class)));
        assertThat((((ClientLessOrEqual) where.getFilterExpression().getObject()).getOperands().get(1)), is(instanceOf(ClientInteger.class)));
    }
}