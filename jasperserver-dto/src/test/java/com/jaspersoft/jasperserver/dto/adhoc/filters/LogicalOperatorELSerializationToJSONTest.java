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
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientAnd;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientOr;
import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.literal;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions.variable;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientLogical.and;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientLogical.or;
import static com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.logical.ClientNot.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by stas on 4/8/15.
 */
public class LogicalOperatorELSerializationToJSONTest extends FilterTest {
    @Test
    public void ensureNot() throws Exception {
        ClientNot not = not(variable("sales").gtOrEq(literal(5)));

        assertThat(json(not), is("{\n" +
                "  \"operands\" : [ {\n" +
                "    \"greaterOrEqual\" : {\n" +
                "      \"operands\" : [ {\n" +
                "        \"variable\" : {\n" +
                "          \"name\" : \"sales\"\n" +
                "        }\n" +
                "      }, {\n" +
                "        \"integer\" : {\n" +
                "          \"value\" : 5\n" +
                "        }\n" +
                "      } ]\n" +
                "    }\n" +
                "  } ],\n" +
                "  \"paren\" : true\n" +
                "}"));
    }

    @Test
    public void ensureWhereNot() throws Exception {
        ClientNot not = not(variable("sales").gtOrEq(literal(5)));

        assertThat(json(new ClientWhere(not)), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"not\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"greaterOrEqual\" : {\n" +
                "            \"operands\" : [ {\n" +
                "              \"variable\" : {\n" +
                "                \"name\" : \"sales\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"integer\" : {\n" +
                "                \"value\" : 5\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        } ],\n" +
                "        \"paren\" : true\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureNotAndTrue() throws Exception {
        ClientAnd and = and(not(variable("sales").gtOrEq(literal(5))), literal(1));

        assertThat(json(and), is("{\n" +
                "  \"operands\" : [ {\n" +
                "    \"not\" : {\n" +
                "      \"operands\" : [ {\n" +
                "        \"greaterOrEqual\" : {\n" +
                "          \"operands\" : [ {\n" +
                "            \"variable\" : {\n" +
                "              \"name\" : \"sales\"\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"integer\" : {\n" +
                "              \"value\" : 5\n" +
                "            }\n" +
                "          } ]\n" +
                "        }\n" +
                "      } ],\n" +
                "      \"paren\" : true\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"integer\" : {\n" +
                "      \"value\" : 1\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void ensureWhereAnd() throws Exception {

        ClientAnd and = and(variable("sales").gtOrEq(literal(1)), variable("sales").ltOrEq(literal(250)));

        assertThat(json(new ClientWhere(and)), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"and\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"greaterOrEqual\" : {\n" +
                "            \"operands\" : [ {\n" +
                "              \"variable\" : {\n" +
                "                \"name\" : \"sales\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"integer\" : {\n" +
                "                \"value\" : 1\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"lessOrEqual\" : {\n" +
                "            \"operands\" : [ {\n" +
                "              \"variable\" : {\n" +
                "                \"name\" : \"sales\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"integer\" : {\n" +
                "                \"value\" : 250\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    @Test
    public void ensureWhereOr() throws Exception {

        ClientOr or = or(variable("sales").gtOrEq(literal(1)), variable("sales").ltOrEq(literal(250)));

        assertThat(json(new ClientWhere(or)), is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"or\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"greaterOrEqual\" : {\n" +
                "            \"operands\" : [ {\n" +
                "              \"variable\" : {\n" +
                "                \"name\" : \"sales\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"integer\" : {\n" +
                "                \"value\" : 1\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"lessOrEqual\" : {\n" +
                "            \"operands\" : [ {\n" +
                "              \"variable\" : {\n" +
                "                \"name\" : \"sales\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"integer\" : {\n" +
                "                \"value\" : 250\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }
}