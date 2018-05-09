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

package com.jaspersoft.jasperserver.api.metadata.security;

import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class IsRecipientOwnPermissionArgumentVoterTest {
    Authentication authentication;
    ObjectPermission permission = new ObjectPermissionImpl();
    User user = new UserImpl();
    Role role = new RoleImpl();
    Object object;

    IsRecipientOwnPermissionArgumentVoter voter = new IsRecipientOwnPermissionArgumentVoter();


    @BeforeMethod
    public void setUp() throws Exception {
        role.setRoleName("role");

        user.setUsername("name");
        user.setTenantId("tenant");

        User recipient = new UserImpl();
        recipient.setUsername(user.getUsername());
        recipient.setTenantId(user.getTenantId());
        recipient.getRoles().add(role);

        permission.setPermissionRecipient(recipient);
        permission.setURI("repo:/");
        permission.setPermissionMask(1);

        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>(1);
        authorityList.add(new GrantedAuthorityImpl(role.getRoleName()));
        authentication = new UsernamePasswordAuthenticationToken(user, user, authorityList);
    }

    @Test
    public void testSupports_user() throws Exception {
        assertTrue(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_role() throws Exception {
        permission.setPermissionRecipient(role);

        assertTrue(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_wrongUser() throws Exception {
        user.setUsername("Ulazlo");

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_wrongRole() throws Exception {
        permission.setPermissionRecipient(role);
        role.setRoleName("afsdasf");

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_wrongUserTenant() throws Exception {
        user.setTenantId("Ulazlo");

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_wrongRoleTenant() throws Exception {
        permission.setPermissionRecipient(role);
        role.setTenantId("afsdasf");

        assertFalse(voter.isPermitted(authentication, permission, object));
    }
}
