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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientBoolean;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientInteger;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/21/16 12:45 PM
 * @version $Id$
 */
public class LiteralELDeserializationFromXMLTest extends FilterTest {

    @Test
    public void booleanLiteralStringIsSerializedAsLiteral() throws Exception {
        final String booleanLiteral =
                "<boolean>\n" +
                "    <value>true</value>\n" +
                "</boolean>";
        ClientLiteral cl = dto(booleanLiteral);

        assertThat(cl, is(instanceOf(ClientBoolean.class)));
        assertThat(((ClientBoolean)cl).getValue(), is(instanceOf(Boolean.class)));
    }

    @Test
    public void integerLiteralStringIsSerializedAsLiteral() throws Exception {
        final String literal = "<integer>\n" +
                "    <value>555</value>\n" +
                "</integer>";
        ClientLiteral cl = dto(literal);

        assertThat(cl, is(instanceOf(ClientInteger.class)));
        assertThat(((ClientInteger)cl).getValue(), is(instanceOf(Integer.class)));
    }
}
