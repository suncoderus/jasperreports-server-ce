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

package com.jaspersoft.jasperserver.dto.serverinfo;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Grant Bacon <gbacon@tibco.com>
 * @version $Id$
 * @date 5/6/16 12:00 p.m.
 */
public class ServerInfoSerializationToXMLTest extends ServerInfoTest {


    @Test
    public void ensureRootElement() throws Exception {
        ServerInfo si = new ServerInfo();

        String xmlString = xml(si);

        assertThat(xmlString, is("<serverInfo/>"));
    }

    @Test
    public void ensureFilledElement() throws Exception {
        ServerInfo si = new ServerInfo()
                .setDateFormatPattern("YYYY-MM-DD")
                .setBuild("20060101_0201")
                .setEditionName("Edition Name");

        String xmlString = xml(si);

        assertThat(xmlString, is("<serverInfo>\n" +
                "    <build>20060101_0201</build>\n" +
                "    <dateFormatPattern>YYYY-MM-DD</dateFormatPattern>\n" +
                "    <editionName>Edition Name</editionName>\n" +
                "</serverInfo>"));
    }

}
