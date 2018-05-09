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

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;
import com.jaspersoft.jasperserver.remote.helpers.RecipientIdentity;
import com.jaspersoft.jasperserver.remote.helpers.RecipientIdentityResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class PermissionConverterTest {
    @InjectMocks
    private PermissionConverter converter;

    @Mock
    RecipientIdentityResolver resolver;

    private final String clientUri = "/public";
    private final String repoUri = "repo:/public";

    private final String userUri = "user:/me";
    private final RecipientIdentity userIdentity = new RecipientIdentity(User.class, "me");
    private final User user = new UserImpl();

    private final ObjectPermission server = new ObjectPermissionImpl();
    private final RepositoryPermission client = new RepositoryPermission();

    @BeforeMethod
    private void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        user.setUsername("me");

        server.setPermissionMask(1);
        server.setPermissionRecipient(user);
        server.setURI(repoUri);

        client.setMask(2);
        client.setRecipient(userUri);
        client.setUri(clientUri);

        when(resolver.toIdentity(userUri)).thenReturn(userIdentity);
    }

    @Test
    public void testToClient() throws Exception {
        RepositoryPermission converted = converter.toClient(server, null);

        assertEquals((int)converted.getMask(), server.getPermissionMask());
        assertEquals(converted.getRecipient(), userUri);
        assertEquals(converted.getUri(), clientUri);

    }

    @Test
    public void testToClient_metadata() throws Exception {
        server.setPermissionRecipient(new MetadataUserDetails(user));

        RepositoryPermission converted = converter.toClient(server, null);

        assertEquals((int)converted.getMask(), server.getPermissionMask());
        assertEquals(converted.getRecipient(), userUri);
        assertEquals(converted.getUri(), clientUri);

    }

    @Test
    public void testGetClientResourceType() throws Exception {
         assertEquals(converter.getClientResourceType(), RepositoryPermission.class.getName());
    }

    @Test
    public void testToServer() throws Exception {
        ObjectPermission converted = converter.toServer(client, null);

        assertEquals(converted.getPermissionMask(), (int)client.getMask());
        assertEquals(converted.getPermissionRecipient(), userIdentity);
        assertEquals(converted.getURI(), repoUri);
    }

    @Test
    public void testToServer_slash() throws Exception {
        client.setUri("public");
        ObjectPermission converted = converter.toServer(client, null);

        assertEquals(converted.getPermissionMask(), (int)client.getMask());
        assertEquals(converted.getPermissionRecipient(), userIdentity);
        assertEquals(converted.getURI(), repoUri);
    }

    @Test
    public void testGetServerResourceType() throws Exception {
        assertEquals(converter.getServerResourceType(), ObjectPermission.class.getName());
    }
}
