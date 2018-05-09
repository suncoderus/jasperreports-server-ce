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
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
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
public class NotPermissionOwnerPermissionArgumentVoterTest {
    Authentication authentication;
    ObjectPermission permission = new ObjectPermissionImpl();
    User user = new UserImpl();
    Object object;

    final NotPermissionOwnerPermissionArgumentVoter voter = new NotPermissionOwnerPermissionArgumentVoter();

    @BeforeMethod
    public void setUp() throws Exception {
        user.setUsername("name");
        user.setTenantId("tenant");

        User recipient = new UserImpl();
        recipient.setUsername(user.getUsername());
        recipient.setTenantId(user.getTenantId());

        permission.setPermissionRecipient(recipient);
        permission.setURI("repo:/");
        permission.setPermissionMask(1);
    }

    @Test
    public void testSupports_sameUser() throws Exception {
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>(1);
        authorityList.add(new GrantedAuthorityImpl("SSS"));
        authentication = new UsernamePasswordAuthenticationToken(user, user, authorityList);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_sameName() throws Exception {
        user.setTenantId("lala");
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>(1);
        authorityList.add(new GrantedAuthorityImpl("SSS"));
        authentication = new UsernamePasswordAuthenticationToken(user, user, authorityList);

        assertTrue(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_sameTenant() throws Exception {
        user.setUsername("lala");
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>(1);
        authorityList.add(new GrantedAuthorityImpl("SSS"));
        authentication = new UsernamePasswordAuthenticationToken(user, user, authorityList);

        assertTrue(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_justName() throws Exception {
        user.setTenantId(null);
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>(1);
        authorityList.add(new GrantedAuthorityImpl("SSS"));
        authentication = new UsernamePasswordAuthenticationToken(user, user, authorityList);

        assertTrue(voter.isPermitted(authentication, permission, object));
    }
}
