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

package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientRole;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class RoleConverterTest {
    private final Role server = new RoleImpl();
    private final ClientRole client = new ClientRole();
    private final RoleConverter converter  = new RoleConverter();

    @BeforeMethod
    public void setUp() throws Exception {
        server.setExternallyDefined(true);
        server.setRoleName("server");
        server.setTenantId("st");

        client.setName("client");
        client.setExternallyDefined(true);
        client.setTenantId("seva");
    }

    @Test
    public void testToClient() throws Exception {
        ClientRole converted = converter.toClient(server, null);

        assertEquals(converted.getName(), server.getRoleName());
        assertEquals(converted.getTenantId(), server.getTenantId());
        assertEquals(converted.isExternallyDefined(), server.isExternallyDefined());
    }

    @Test
    public void testToServer() throws Exception {
        Role converted = converter.toServer(client, null);

        assertEquals(converted.getRoleName(), client.getName());
        assertEquals(converted.getTenantId(), client.getTenantId());
        assertEquals(converted.isExternallyDefined(), client.isExternallyDefined());
    }

    @Test
    public void testToServer_update() throws Exception {
        Role converted = converter.toServer(client, server, null);

        assertEquals(converted, server);
        assertEquals(converted.getRoleName(), client.getName());
        assertEquals(converted.getTenantId(), client.getTenantId());
        assertEquals(converted.isExternallyDefined(), client.isExternallyDefined());
    }
}
