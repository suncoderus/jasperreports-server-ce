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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @date 1/21/16 12:45 PM
 */
public class VariableDeserializationTestFromXML extends FilterTest {

    @Test
    public void ensureVariable() throws Exception {
        String xmlString = "<variable name=\"sales\" />";

        ClientVariable variable = dto(xmlString);

        assertThat(variable, Is.is(instanceOf(ClientVariable.class)));
        assertThat(variable.getType(), Is.is("variable"));
        assertThat(variable.getName(), Is.is(instanceOf(String.class)));
    }
}
