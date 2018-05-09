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

package com.jaspersoft.jasperserver.remote.helpers;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.TenantImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author askorodumov
 * @version $Id$
 */
public class RecipientIdentityResolverTest {
    private final RecipientIdentityResolver permissionsResolver = new RecipientIdentityResolver();
    private final RecipientIdentityResolver attributesResolver = new RecipientIdentityResolver();

    private final Role roleAdmin = new RoleImpl();
    private final User anonymousUser = new UserImpl();
    private final Tenant tenantRoot = new TenantImpl();
    private final User nonExistentUser = new UserImpl();
    private final Tenant nonExistentTenant = new TenantImpl();
    private final Role nonExistentRole = new RoleImpl();

    @Mock
    TenantService tenantService;
    @Mock
    UserAuthorityService userAuthorityService;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);

        anonymousUser.setUsername("anonymousUser");
        roleAdmin.setRoleName("ROLE_ADMINISTRATOR");
        tenantRoot.setId("organizations");

        nonExistentUser.setUsername("nonExistentUser");
        nonExistentRole.setRoleName("ROLE_NON_EXISTENT");
        nonExistentTenant.setId("non_existent_tenant");

        Map<String, Class<?>> availableProtocolsMap = new HashMap<String, Class<?>>();
        availableProtocolsMap.put("user", User.class);
        availableProtocolsMap.put("role", Role.class);
        availableProtocolsMap.put("tenant", Tenant.class);
        availableProtocolsMap = Collections.unmodifiableMap(availableProtocolsMap);

        permissionsResolver.setMap(availableProtocolsMap);
        attributesResolver.setMap(availableProtocolsMap);

        permissionsResolver.setIdentifierLabel("recipientUri");
        attributesResolver.setIdentifierLabel("holder");

        Set<String> permissionsProtocols =
                Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("user","role")));
        permissionsResolver.setRequiredProtocols(permissionsProtocols);
        Set<String> attributesProtocols =
                Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("user","tenant")));
        attributesResolver.setRequiredProtocols(attributesProtocols);

        when(userAuthorityService.getRole(any(ExecutionContext.class), eq(roleAdmin.getRoleName()))).thenReturn(roleAdmin);
        when(userAuthorityService.getUser(any(ExecutionContext.class), eq(anonymousUser.getUsername()))).thenReturn(anonymousUser);
        when(tenantService.getTenant(any(ExecutionContext.class), eq(tenantRoot.getId()))).thenReturn(tenantRoot);

        permissionsResolver.tenantService = tenantService;
        attributesResolver.tenantService = tenantService;
        permissionsResolver.userAuthorityService = userAuthorityService;
        attributesResolver.userAuthorityService = userAuthorityService;
    }

    @Test
    public void resolveRecipientObject_forExistentUser_success() throws Exception {
        RecipientIdentity userIdentity = attributesResolver.toIdentity(anonymousUser);
        String userUri = attributesResolver.toRecipientUri(anonymousUser);
        assertEquals(attributesResolver.resolveRecipientObject(userIdentity), anonymousUser);
        assertEquals(attributesResolver.resolveRecipientObject(userUri), anonymousUser);

        userIdentity = permissionsResolver.toIdentity(anonymousUser);
        userUri = permissionsResolver.toRecipientUri(anonymousUser);
        assertEquals(permissionsResolver.resolveRecipientObject(userIdentity), anonymousUser);
        assertEquals(permissionsResolver.resolveRecipientObject(userUri), anonymousUser);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void resolveRecipientObject_forFolder_exception() {
        RecipientIdentity folderIdentity = new RecipientIdentity(Folder.class, "nonExistentFolder");
        permissionsResolver.resolveRecipientObject(folderIdentity);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void resolveRecipientObject_forNonExistentUser_exception() {
        RecipientIdentity userIdentity = permissionsResolver.toIdentity(nonExistentUser);
        permissionsResolver.resolveRecipientObject(userIdentity);
    }

    @Test
    public void resolveRecipientObject_forExistingTenant_success() throws Exception {
        RecipientIdentity tenantIdentity = attributesResolver.toIdentity(tenantRoot);
        String tenantUri = attributesResolver.toRecipientUri(tenantRoot);
        assertEquals(attributesResolver.resolveRecipientObject(tenantIdentity), tenantRoot);
        assertEquals(attributesResolver.resolveRecipientObject(tenantUri), tenantRoot);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void resolveRecipientObject_forNonExistentTenant_exception() {
        RecipientIdentity tenantIdentity = attributesResolver.toIdentity(nonExistentTenant);
        attributesResolver.resolveRecipientObject(tenantIdentity);
    }

    @Test
    public void resolveRecipientObject_forExistingRole_success() throws Exception {
        RecipientIdentity roleIdentity = permissionsResolver.toIdentity(roleAdmin);
        String roleUri = permissionsResolver.toRecipientUri(roleAdmin);
        assertEquals(permissionsResolver.resolveRecipientObject(roleIdentity), roleAdmin);
        assertEquals(permissionsResolver.resolveRecipientObject(roleUri), roleAdmin);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void resolveRecipientObject_forNonExistentRole_exception() {
        RecipientIdentity roleIdentity = permissionsResolver.toIdentity(nonExistentRole);
        permissionsResolver.resolveRecipientObject(roleIdentity);
    }

    @Test
    public void toIdentity_forExistentUser_success() throws Exception {
        RecipientIdentity userIdentity = attributesResolver.toIdentity(anonymousUser);
        assertNotNull(userIdentity);
        assertEquals(anonymousUser.getUsername(), userIdentity.getId());
        assertEquals(User.class, userIdentity.getRecipientClass());

        userIdentity = permissionsResolver.toIdentity(anonymousUser);
        assertNotNull(userIdentity);
        assertEquals(anonymousUser.getUsername(), userIdentity.getId());
        assertEquals(User.class, userIdentity.getRecipientClass());
    }

    @Test
    public void toIdentity_forExistentTenant_success() throws Exception {
        RecipientIdentity tenantIdentity = attributesResolver.toIdentity(tenantRoot);
        assertNotNull(tenantIdentity);
        assertEquals(tenantRoot.getId(), tenantIdentity.getId());
        assertEquals(Tenant.class, tenantIdentity.getRecipientClass());
    }

    @Test
    public void toIdentity_forExistentRole_success() throws Exception {
        RecipientIdentity roleIdentity = permissionsResolver.toIdentity(roleAdmin);
        assertNotNull(roleIdentity);
        assertEquals(roleAdmin.getRoleName(), roleIdentity.getId());
        assertEquals(Role.class, roleIdentity.getRecipientClass());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void toIdentity_forFolder_exception() {
        Folder folder = new FolderImpl();
        folder.setName("some_folder");
        permissionsResolver.toIdentity(folder);
    }

    @Test
    public void toIdentity_forExistentUserUri_success() throws Exception {
        String userUri = attributesResolver.toRecipientUri(anonymousUser);
        assertNotNull(userUri);
        RecipientIdentity userIdentity = attributesResolver.toIdentity(userUri);
        assertNotNull(userIdentity);
        assertEquals(anonymousUser.getUsername(), userIdentity.getId());
        assertEquals(User.class, userIdentity.getRecipientClass());

        userUri = permissionsResolver.toRecipientUri(anonymousUser);
        assertNotNull(userUri);
        userIdentity = permissionsResolver.toIdentity(userUri);
        assertNotNull(userIdentity);
        assertEquals(anonymousUser.getUsername(), userIdentity.getId());
        assertEquals(User.class, userIdentity.getRecipientClass());
    }

    @Test
    public void toIdentity_forExistentTenantUri_success() throws Exception {
        String tenantUri = attributesResolver.toRecipientUri(tenantRoot);
        assertNotNull(tenantUri);
        RecipientIdentity tenantIdentity = attributesResolver.toIdentity(tenantUri);
        assertNotNull(tenantIdentity);
        assertEquals(tenantRoot.getId(), tenantIdentity.getId());
        assertEquals(Tenant.class, tenantIdentity.getRecipientClass());
    }

    @Test
    public void toIdentity_forExistentRoleUri_success() throws Exception {
        String roleUri = permissionsResolver.toRecipientUri(roleAdmin);
        assertNotNull(roleUri);
        RecipientIdentity roleIdentity = permissionsResolver.toIdentity(roleUri);
        assertNotNull(roleIdentity);
        assertEquals(roleAdmin.getRoleName(), roleIdentity.getId());
        assertEquals(Role.class, roleIdentity.getRecipientClass());
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void toIdentity_forWrongUri1_exception() {
        permissionsResolver.toIdentity("/users/joeuser");
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void toIdentity_forWrongUri2_exception() {
        permissionsResolver.toIdentity("users:/joeuser");
    }
}
