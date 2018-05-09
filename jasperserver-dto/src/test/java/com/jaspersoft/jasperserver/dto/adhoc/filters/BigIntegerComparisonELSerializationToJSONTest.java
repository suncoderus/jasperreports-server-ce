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

import java.math.BigInteger;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.literal;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 *
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 */
public class BigIntegerComparisonELSerializationToJSONTest extends FilterTest {
    @Test
    public void ensureGreaterOrEqual() throws Exception {
        ClientComparison comparison = variable("sales").gtOrEq(literal(new BigInteger("55555555555")));

        assertThat(json(comparison), is("{\n" +
                "  \"operands\" : [ {\n" +
                "    \"variable\" : {\n" +
                "      \"name\" : \"sales\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"bigInteger\" : {\n" +
                "      \"value\" : \"55555555555\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void ensureGreaterOrEqualInWhere() throws Exception {
        ClientComparison comparison = variable("sales").gtOrEq(literal(new BigInteger("55555555555")));
        ClientWhere w = new ClientWhere(comparison);

        assertThat(json(w), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"greaterOrEqual\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"bigInteger\" : {\n" +
                "            \"value\" : \"55555555555\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureLessOrEqualInWhere() throws Exception {
        ClientComparison comparison = variable("sales").ltOrEq(literal(new BigInteger("55555555555")));
        ClientWhere w = new ClientWhere(comparison);

        assertThat(json(w), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"lessOrEqual\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"bigInteger\" : {\n" +
                "            \"value\" : \"55555555555\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }


    @Test
    public void ensureLessInWhere() throws Exception {
        ClientComparison comparison = variable("sales").lt(literal(new BigInteger("55555555555")));
        ClientWhere w = new ClientWhere(comparison);

        assertThat(json(w), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"less\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"bigInteger\" : {\n" +
                "            \"value\" : \"55555555555\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureGreaterInWhere() throws Exception {
        ClientComparison comparison = variable("sales").gt(literal(new BigInteger("55555555555")));
        ClientWhere w = new ClientWhere(comparison);

        assertThat(json(w), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"greater\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"sales\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"bigInteger\" : {\n" +
                "            \"value\" : \"55555555555\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }
}