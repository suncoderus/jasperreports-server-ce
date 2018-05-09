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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientNotEqual;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;


/**
 * Created by stas on 4/8/15.
 * @version $Id$
 */
public class NullELDeserializationFromJSONTest extends FilterTest {

    @Test
    public void ensureEmptyObject() throws Exception {
        String json = "{ }";

        ClientExpression expression = dtoFromJSONString(json, ClientNull.class);

        assertThat(expression, is(instanceOf(ClientNull.class)));
    }

    @Test
    public void ensureNullObject() throws Exception {
        String json = "{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"notEqual\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"NULL\" : { }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
        ClientWhere w = dtoFromJSONString(json, ClientWhere.class);


        assertThat(w.getFilterExpression().getObject(), is(instanceOf(ClientNotEqual.class)));
        final ClientExpression nullValue = ((ClientNotEqual) w.getFilterExpression().getObject()).getOperands().get(1);
        assertThat(nullValue, is(instanceOf(ClientNull.class)));
    }

}