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
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.JasperServerSidRetrievalStrategyImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.ResourceObjectIdentityRetrievalStrategyImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class IsAdministrableObjectPermissionArgumentVoterTest {
    @Mock
    Authentication authentication;
    ObjectPermission permission = new ObjectPermissionImpl();
    User user = new UserImpl();
    Object object;
    Sid sid;
    List<Sid> sids;
    ObjectIdentityRetrievalStrategy oidStrategy;
    ObjectIdentity oid;

    @InjectMocks
    IsAdministrableObjectPermissionArgumentVoter voter = new IsAdministrableObjectPermissionArgumentVoter();
    @Mock
    private AclService provider;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        user.setUsername("name");
        user.setTenantId("tenant");

        User recipient = new UserImpl();
        recipient.setUsername(user.getUsername());
        recipient.setTenantId(user.getTenantId());
        sid = new JasperServerSidRetrievalStrategyImpl().getSid(recipient);
        sids = new JasperServerSidRetrievalStrategyImpl().getSids(authentication);
        sids.add(sid);
        oidStrategy = new ResourceObjectIdentityRetrievalStrategyImpl();

        permission.setPermissionRecipient(recipient);
        permission.setURI("repo:/");
        permission.setPermissionMask(1);
        oid = oidStrategy.getObjectIdentity(permission.getURI());

    }

    @Test
    public void testSupports() throws Exception {
        List entries = new ArrayList<AccessControlEntry>();
        Acl acl = new JasperServerAclImpl(oid,null);
        entries.add(new AccessControlEntryImpl(null,acl, sid,JasperServerPermission.ADMINISTRATION,true,false,false));
        acl = new JasperServerAclImpl(oid,entries);
        when(provider.readAclById(any(ObjectIdentity.class),anyListOf(Sid.class))).thenReturn(acl);
        when(authentication.getPrincipal()).thenReturn(user);

        assertTrue(voter.isPermitted(authentication, permission, object));
        assertTrue(true);
    }

    @Test
    public void testSupports_not() throws Exception {
        List entries = new ArrayList<AccessControlEntry>();
        Acl acl = new JasperServerAclImpl(oid,null);
        entries.add(new AccessControlEntryImpl(null,acl, sid,JasperServerPermission.READ,true,false,false));
        acl = new JasperServerAclImpl(oid,entries);

        when(provider.readAclById(any(ObjectIdentity.class),anyListOf(Sid.class))).thenReturn(acl);
        when(authentication.getPrincipal()).thenReturn(user);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_notMany() throws Exception {
        List entries = new ArrayList<AccessControlEntry>();
        Acl acl = new JasperServerAclImpl(oid,null);
        entries.add(new AccessControlEntryImpl(null,acl, sid,JasperServerPermission.READ,true,false,false));
        entries.add(new AccessControlEntryImpl(null,acl, sid,JasperServerPermission.WRITE,true,false,false));
        acl = new JasperServerAclImpl(oid,entries);

        when(provider.readAclById(any(ObjectIdentity.class),anyListOf(Sid.class))).thenReturn(acl);
        when(authentication.getPrincipal()).thenReturn(user);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_many() throws Exception {
        List entries = new ArrayList<AccessControlEntry>();
        Acl acl = new JasperServerAclImpl(oid,null);
        entries.add(new AccessControlEntryImpl(null,acl, sid,JasperServerPermission.READ,true,false,false));
        entries.add(new AccessControlEntryImpl(null,acl, sid,JasperServerPermission.ADMINISTRATION,true,false,false));
        acl = new JasperServerAclImpl(oid,entries);
        when(provider.readAclById(any(ObjectIdentity.class),anyListOf(Sid.class))).thenReturn(acl);
        when(authentication.getPrincipal()).thenReturn(user);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_empty() throws Exception {
        Acl acl = new JasperServerAclImpl(oid,null);
        when(provider.readAclById(any(ObjectIdentity.class),anyListOf(Sid.class))).thenReturn(acl);
        assertFalse(voter.isPermitted(authentication, permission, object));
    }

    @Test
    public void testSupports_null() throws Exception {
        when(provider.readAclById(any(ObjectIdentity.class),anyListOf(Sid.class))).thenReturn(null);

        assertFalse(voter.isPermitted(authentication, permission, object));
    }


}
