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

package com.jaspersoft.jasperserver.dto.adhoc;

import com.jaspersoft.jasperserver.dto.adhoc.filters.FilterTest;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientList;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.membership.ClientIn;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 2/17/16 1:45 PM
 */
public class ClientExpressionListSerializableTests extends FilterTest {

    @Test
    public void ensureListJSON_serialization() throws Exception {
        ClientList list = new ClientList().addItem(new ClientInteger(1)).addItem(new ClientString("David Byrne"));

        final String jsonString = json(list);

        assertThat(jsonString, is(notNullValue()));
        assertThat(jsonString, is("{\n" +
                "  \"items\" : [ {\n" +
                "    \"integer\" : {\n" +
                "      \"value\" : 1\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"string\" : {\n" +
                "      \"value\" : \"David Byrne\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}"));
    }

    @Test
    public void ensureListJSON_deserialization() throws Exception {
        final String jsonString = "{\n" +
                "  \"items\" : [ {\n" +
                "    \"integer\" : {\n" +
                "      \"value\" : 1\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"string\" : {\n" +
                "      \"value\" : \"David Byrne\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}";

        ClientList dto = dtoFromJSONString(jsonString, ClientList.class);
        assertThat(dto, is(instanceOf(ClientList.class)));
        assertThat(dto.getItems().get(1), is(instanceOf(ClientString.class)));

    }

    @Test
    public void ensureExpressionList_inWhere_serialization() throws Exception {
        ClientList list = new ClientList()
                .addItem(new ClientString("David Byrne"))
                .addItem(new ClientString("Tina Weymouth"))
                .addItem(new ClientString("Chris Frantz"))
                .addItem(new ClientString("Jerry Harrison"));
        ClientIn in = new ClientIn(new ClientVariable("bandMember"), list);
        ClientWhere w = new ClientWhere(in);

        final String jsonString = json(w);

        assertThat(jsonString, is("{\n" +
                "  \"filterExpression\" : {\n" +
                "    \"object\" : {\n" +
                "      \"in\" : {\n" +
                "        \"operands\" : [ {\n" +
                "          \"variable\" : {\n" +
                "            \"name\" : \"bandMember\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"list\" : {\n" +
                "            \"items\" : [ {\n" +
                "              \"string\" : {\n" +
                "                \"value\" : \"David Byrne\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"string\" : {\n" +
                "                \"value\" : \"Tina Weymouth\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"string\" : {\n" +
                "                \"value\" : \"Chris Frantz\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"string\" : {\n" +
                "                \"value\" : \"Jerry Harrison\"\n" +
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
    public void ensureInList_inWhere_deserialization() throws Exception {
        ClientList list = new ClientList().addItem(new ClientInteger(1)).addItem(new ClientString("David Byrne"));
        ClientIn in = new ClientIn(new ClientVariable("sales"), list);
        ClientWhere w = new ClientWhere(in);

        final String jsonString = "{\n" +
                "  \"filterExpression\" : {\n" +
                "     \"object\" : {\n" +
                "       \"in\" : {\n" +
                "         \"operands\" : [ {\n" +
                "           \"variable\" : {\n" +
                "             \"name\" : \"sales\"\n" +
                "           }\n" +
                "         }, {\n" +
                "           \"list\" : {\n" +
                "             \"items\" : [ {\n" +
                "               \"integer\" : {\n" +
                "                 \"value\" : 1\n" +
                "               }\n" +
                "             }, {\n" +
                "               \"string\" : {\n" +
                "                 \"value\" : \"David Byrne\"\n" +
                "               }\n" +
                "             } ]\n" +
                "           }\n" +
                "         } ]\n" +
                "       }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        ClientWhere whereDeserialized = dtoFromJSONString(jsonString, ClientWhere.class);

        assertThat(whereDeserialized, instanceOf(ClientWhere.class));
        assertThat(whereDeserialized, is(w));

    }

    @Test
    public void ensureXMLSerialization_inWhere() throws Exception {
        ClientList list = new ClientList().addItem(new ClientInteger(1)).addItem(new ClientString("David Byrne"));
        ClientIn in = new ClientIn(new ClientVariable("sales"), list);
        ClientWhere w = new ClientWhere(in);

        final String xmlString = xml(w);

        assertThat(xmlString, is("<where>\n" +
                "    <filterExpression>\n" +
                "        <in>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <list>\n" +
                "                    <items>\n" +
                "                        <integer>\n" +
                "                            <value>1</value>\n" +
                "                        </integer>\n" +
                "                        <string>\n" +
                "                            <value>David Byrne</value>\n" +
                "                        </string>\n" +
                "                    </items>\n" +
                "                </list>\n" +
                "            </operands>\n" +
                "        </in>\n" +
                "    </filterExpression>\n" +
                "</where>"));
    }

    @Test
    public void ensureXMLDeserialization_inWhere() throws Exception {
        final String xmlString = "<where>\n" +
                "    <filterExpression>\n" +
                "        <in>\n" +
                "            <operands>\n" +
                "                <variable name=\"sales\"/>\n" +
                "                <list>\n" +
                "                    <items>\n" +
                "                        <integer>\n" +
                "                            <value>1</value>\n" +
                "                        </integer>\n" +
                "                        <string>\n" +
                "                            <value>David Byrne</value>\n" +
                "                        </string>\n" +
                "                    </items>\n" +
                "                </list>\n" +
                "            </operands>\n" +
                "        </in>\n" +
                "    </filterExpression>\n" +
                "</where>";

        ClientWhere w = dto(xmlString);

        assertThat(w, is(instanceOf(ClientWhere.class)));
        assertThat(w.getFilterExpression().getObject(), is(instanceOf(ClientIn.class)));
        assertThat(((ClientIn)w.getFilterExpression().getObject()).getRhs().get(0), is(instanceOf(ClientList.class)));
    }
}
