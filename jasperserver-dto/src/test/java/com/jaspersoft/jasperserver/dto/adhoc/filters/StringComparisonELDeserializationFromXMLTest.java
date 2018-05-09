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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class StringComparisonELDeserializationFromXMLTest extends FilterTest {

    @Test
    public void ensureEqualsInWhere() throws Exception {
        String xml = "<where>\n" +
                "    <filterExpression>\n" +
                "        <equals>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <string>\n" +
                "                    <value>David Byrne</value>\n" +
                "                </string>\n" +
                "            </operands>\n" +
                "        </equals>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere where = dto(xml);
        ClientComparison filters = (ClientComparison) where.getFilterExpression().getObject();

        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientEquals.class)));
        assertThat(filters.getOperator(), is(ClientEquals.OPERATOR_ID));
        assertThat(filters.getOperands().get(0), is(instanceOf(ClientVariable.class)));
        assertThat(filters.getOperands().get(1), is(instanceOf(ClientString.class)));
        assertThat(((ClientString) filters.getOperands().get(1)).getValue().toString(), is("David Byrne"));

    }

    @Test
    public void ensureNotEqualInWhere() throws Exception {
        String xml = "<where>\n" +
                "    <filterExpression>\n" +
                "        <notEqual>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <string>\n" +
                "                    <value>David Byrne</value>\n" +
                "                </string>\n" +
                "            </operands>\n" +
                "        </notEqual>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere where = dto(xml);


        assertThat(where.getFilterExpression().getObject(), is(instanceOf(ClientNotEqual.class)));
        ClientComparison filters = (ClientComparison) where.getFilterExpression().getObject();

        assertThat(filters.getOperator(), is(ClientNotEqual.OPERATOR_ID));
        assertThat((filters.getOperands().get(0)), is(instanceOf(ClientVariable.class)));
        assertThat((filters.getOperands().get(1)), is(instanceOf(ClientString.class)));
        assertThat(((ClientString) filters.getOperands().get(1)).getValue().toString(), is("David Byrne"));
    }

}