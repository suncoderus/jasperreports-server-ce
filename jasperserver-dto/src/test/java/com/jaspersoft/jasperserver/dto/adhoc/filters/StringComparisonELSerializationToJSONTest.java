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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientComparison;
import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.literal;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/27/16 3:47PM
 */
public class StringComparisonELSerializationToJSONTest extends FilterTest {

    @Test
    public void ensureEquals() throws Exception {
        ClientComparison comparison = variable("sales").eq(literal("Talking Heads"));

        assertThat(json(comparison), is("{\n" +
                "  \"operands\" : [ {\n" +
                "    \"variable\" : {\n" +
                "      \"name\" : \"sales\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"string\" : {\n" +
                "      \"value\" : \"Talking Heads\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));

    }

    @Test
    public void ensureNotEqual() throws Exception {
        ClientComparison comparison = variable("sales").notEq(literal("Talking Heads"));

        assertThat(json(comparison), is("{\n" +
                "  \"operands\" : [ {\n" +
                "    \"variable\" : {\n" +
                "      \"name\" : \"sales\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"string\" : {\n" +
                "      \"value\" : \"Talking Heads\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));

    }

    @Test
    public void ensureEqualsInWhere() throws Exception {
        ClientComparison comparison = variable("sales").eq(literal("Talking Heads"));

        assertThat(json(new ClientWhere(comparison)), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"equals\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"string\" : {\n" +
                "            \"value\" : \"Talking Heads\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));

    }

    @Test
    public void ensureNotEqualInWhere() throws Exception {
        ClientComparison comparison = variable("sales").notEq(literal("Talking Heads"));

        assertThat(json(new ClientWhere(comparison)), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"notEqual\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"string\" : {\n" +
                "            \"value\" : \"Talking Heads\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));

    }


}