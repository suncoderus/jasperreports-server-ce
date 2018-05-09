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

import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.dto.authority.ClientRole;
import com.jaspersoft.jasperserver.dto.authority.ClientUser;
import org.junit.Ignore;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class UserConverterTest {
    private final User server = new UserImpl();
    private final ClientUser client = new ClientUser();

    private final Role serverRole = new RoleImpl();
    private final ClientRole clientRole = new ClientRole();

    @InjectMocks
    private UserConverter converter  = new UserConverter();
    @Mock RoleConverter roleConverter;

    @Mock private HttpServletRequest httpServletRequestMock;
    @Mock private HttpSession httpSessionMock;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        client.setFullName("user.getFullName()");
        client.setPassword("user.getPassword()");
        client.setEmailAddress("user.getEmailAddress()");
        client.setExternallyDefined(true);
        client.setEnabled(true);
        client.setPreviousPasswordChangeTime(new Date());
        client.setTenantId("user.getTenantId()");
        client.setUsername("user.getUsername()");

        Set<ClientRole> clientRoles = new HashSet<ClientRole>();
        clientRoles.add(clientRole);
        client.setRoleSet(clientRoles);

        server.setFullName("user.getFullName()h");
        server.setPassword("user.getword()h");
        server.setEmailAddress("user.getEmailAdhhhdress()");
        server.setExternallyDefined(true);
        server.setEnabled(true);
        server.setPreviousPasswordChangeTime(new Date());
        server.setTenantId("user.getTenantIdghg()");
        server.setUsername("useamshhhhge()");

        Set<Role> serverRoles = new HashSet<Role>();
        serverRoles.add(serverRole);
        server.setRoles(serverRoles);

        ServletRequestAttributes servletRequestAttributesMock = new ServletRequestAttributes(httpServletRequestMock);
        RequestContextHolder.setRequestAttributes(servletRequestAttributesMock, false);
        Mockito.when(httpServletRequestMock.getSession()).thenReturn(httpSessionMock);

        Mockito.when(roleConverter.toServer(Mockito.any(ClientRole.class),  Mockito.any(ToServerConversionOptions.class))).thenReturn(serverRole);
        Mockito.when(roleConverter.toClient(Mockito.any(Role.class), Mockito.any(ToClientConversionOptions.class))).thenReturn(clientRole);
    }

    @Test
    public void testToClient() throws Exception {
        ClientUser converted = converter.toClient(server, ToClientConversionOptions.getDefault());

        assertEquals(converted.getFullName(), server.getFullName());
        assertEquals(converted.getEmailAddress(), server.getEmailAddress());
        assertEquals(converted.getPreviousPasswordChangeTime(), server.getPreviousPasswordChangeTime());
        assertEquals(converted.getTenantId(), server.getTenantId());
        assertEquals(converted.getUsername(), server.getUsername());
        assertEquals(converted.isEnabled(), (Boolean)server.isEnabled());
        assertEquals(converted.isExternallyDefined(), (Boolean)server.isExternallyDefined());
        // mock maps client role to server role, so it is enough to check if clientRole in converted
        assertTrue(converted.getRoleSet().contains(clientRole));
    }

    @Test(enabled = false)
    @Ignore
    public void testToServer() throws Exception {
        User converted = converter.toServer(client, null);

        assertEquals(converted.getFullName(), client.getFullName());
        assertEquals(converted.getEmailAddress(), client.getEmailAddress());
        assertEquals(converted.getPassword(), client.getPassword());
        assertEquals(converted.getTenantId(), client.getTenantId());
        assertEquals(converted.getUsername(), client.getUsername());
        assertEquals((Boolean)converted.isEnabled(), client.isEnabled());
        assertEquals((Boolean)converted.isExternallyDefined(), client.isExternallyDefined());
        // mock maps client role to server role, so it is enough to check if serverRole in converted
        assertTrue(converted.getRoles().contains(serverRole));
    }

    @Test
    public void testToServer_update() throws Exception {
        User converted = converter.toServer(client, server, null);

        assertEquals(converted.getFullName(), client.getFullName());
        assertEquals(converted.getEmailAddress(), client.getEmailAddress());
        assertEquals(converted.getPassword(), client.getPassword());
        assertEquals(converted.getTenantId(), client.getTenantId());
        assertEquals(converted.getUsername(), client.getUsername());
        assertEquals((Boolean)converted.isEnabled(), client.isEnabled());
        assertEquals((Boolean)converted.isExternallyDefined(), client.isExternallyDefined());
        // mock maps client role to server role, so it is enough to check if serverRole in converted
        assertTrue(converted.getRoles().contains(serverRole));
    }

    @Test(enabled = false)
    public void testToServer_setsTimeOnPassChange() throws Exception {
        User converted = converter.toServer(client, server, null);

        assertNotNull(converted.getPreviousPasswordChangeTime());
    }
}
