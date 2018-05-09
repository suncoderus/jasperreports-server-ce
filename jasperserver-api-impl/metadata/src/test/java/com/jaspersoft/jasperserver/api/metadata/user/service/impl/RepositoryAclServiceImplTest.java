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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;


import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.util.ImportRunMonitor;
import com.jaspersoft.jasperserver.api.metadata.common.domain.PermissionUriProtocol;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerAclImpl;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.reset;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author ohavavka
 */
public class RepositoryAclServiceImplTest {

    @InjectMocks
    RepositoryAclServiceImpl service = new RepositoryAclServiceImpl();

    @Mock
    ObjectPermissionService objectPermissionService;
    JasperServerSidRetrievalStrategyImpl sidRetrievalStrategy = new JasperServerSidRetrievalStrategyImpl();
    List<AccessControlEntry> acesRootFolder = new ArrayList<AccessControlEntry>();
    List<AccessControlEntry> acesOrgFolder = new ArrayList<AccessControlEntry>();
    List<AccessControlEntry> acesImportFolder = new ArrayList<AccessControlEntry>();

    String roleAdministratorString="ROLE_ADMINISTRATOR";
    String roleUserString="ROLE_USER";
    Sid adminRoleSid = new GrantedAuthoritySid(roleAdministratorString);
    Sid userRoleSid = new GrantedAuthoritySid(roleUserString);
    Sid currentUserSid = new PrincipalSid("importUser");
    Role adminRole = new RoleImpl();
    Role userRole = new RoleImpl();
    ObjectIdentity rootFolder = new InternalURIDefinition("repo:/", PermissionUriProtocol.RESOURCE);
    ObjectIdentity orgFolder = new InternalURIDefinition("repo:/organizations", PermissionUriProtocol.RESOURCE);
    ObjectIdentity org1Folder = new InternalURIDefinition("repo:/organizations/organization_1", PermissionUriProtocol.RESOURCE);
    ObjectIdentity org1FolderIncorectUri = new InternalURIDefinition("repo:/organizations//organization_1", PermissionUriProtocol.RESOURCE);
    ObjectIdentity org1FolderWithoutProtocol = new InternalURIDefinition("/organizations/organization_1", PermissionUriProtocol.RESOURCE);
    ObjectIdentity org2Folder = new InternalURIDefinition("/organizations/organization_2", PermissionUriProtocol.RESOURCE);
    ObjectIdentity importFolder = new InternalURIDefinition("/someFolder/someFolder_2//test", PermissionUriProtocol.RESOURCE);
    List rootObjectPermissions,orgObjectPermissions,orgObjectPermissionsCombined;
    Acl rootAcl,orgAcl,org1Acl,org2Acl,importAcl;
    private static int AdminPermission = JasperServerPermission.ADMINISTRATION.getMask();
    private static int ReadPermission = JasperServerPermission.READ.getMask();

    @BeforeClass
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service.setAclLookupStrategy(service);
        service.setSidRetrievalStrategy(sidRetrievalStrategy);
        service.setPermissionService(objectPermissionService);
        acesRootFolder.add(new AccessControlEntryImpl(null,new JasperServerAclImpl(rootFolder,null),adminRoleSid, new JasperServerPermission(AdminPermission),true,false,false));
        acesOrgFolder.add(new AccessControlEntryImpl(null,new JasperServerAclImpl(orgFolder,null),userRoleSid, new JasperServerPermission(ReadPermission),true,false,false));
        acesImportFolder.add(new AccessControlEntryImpl(null,new JasperServerAclImpl(importFolder,null),currentUserSid, new JasperServerPermission(AdminPermission),true,false,false));
        rootAcl = new JasperServerAclImpl(rootFolder, acesRootFolder);
        orgAcl = new JasperServerAclImpl(orgFolder,acesOrgFolder,rootAcl);
        org1Acl = new JasperServerAclImpl(org1Folder,null,orgAcl);
        org2Acl = new JasperServerAclImpl(org2Folder,null,orgAcl);
        importAcl = new JasperServerAclImpl(importFolder,acesImportFolder);

        adminRole.setRoleName(roleAdministratorString);
        userRole.setRoleName(roleUserString);
        rootObjectPermissions = new ArrayList();
        ObjectPermissionImpl objectPermission = new ObjectPermissionImpl();
        objectPermission.setURI(rootFolder.toString());
        objectPermission.setPermissionMask(AdminPermission);
        objectPermission.setPermissionRecipient(adminRole);
        rootObjectPermissions.add(objectPermission);

        orgObjectPermissions = new ArrayList();
        objectPermission = new ObjectPermissionImpl();
        objectPermission.setURI(orgFolder.toString());
        objectPermission.setPermissionMask(ReadPermission);
        objectPermission.setPermissionRecipient(userRole);

        orgObjectPermissions.add(objectPermission);

        orgObjectPermissionsCombined = new ArrayList();
        orgObjectPermissionsCombined.addAll(rootObjectPermissions);
        orgObjectPermissionsCombined.addAll(orgObjectPermissions);
    }

    private String decodeAcl(Acl acl) {
        if (acl.isEntriesInheriting()&&acl.getParentAcl()!=null) {
            String result = decodeAcl(acl.getParentAcl());

            return result.concat("{").concat(acl.toString()).concat("}");
        }
        return acl.toString();
    }

    @BeforeMethod
    public void refresh() throws Exception {
        reset(objectPermissionService);
        // root permissions
        when(objectPermissionService.getObjectPermissionsForObject(any(ExecutionContext.class),eq(rootFolder),any(Object[].class))).thenReturn(rootObjectPermissions);
        // folder full set of permissions
        when(objectPermissionService.getObjectPermissionsForObject(any(ExecutionContext.class),eq(orgFolder),any(Object[].class))).thenReturn(orgObjectPermissionsCombined);
        // subfolder permissions are absent so we return full set of parents
        when(objectPermissionService.getObjectPermissionsForObject(any(ExecutionContext.class),eq(org1Folder),any(Object[].class))).thenReturn(orgObjectPermissionsCombined);
        // subfolder permissions are absent so we return just parent set of records - in current optimized model this is not correct case.
        when(objectPermissionService.getObjectPermissionsForObject(any(ExecutionContext.class),eq(org2Folder),any(Object[].class))).thenReturn(orgObjectPermissions);

    }

    @Test
    public void readRootACLTest() throws Exception {
        assertEquals(decodeAcl(rootAcl),decodeAcl(service.readAclById(rootFolder)));
    }

    @Test
    public void readHierarchyAcl() throws Exception {
        assertEquals(decodeAcl(orgAcl),decodeAcl(service.readAclById(orgFolder)));
        assertEquals(decodeAcl(org1Acl),decodeAcl(service.readAclById(org1Folder)));
    }

    @Test()
    public void readHierarchyAclWithIncorrectUri() throws Exception {
        // incorrect URI can load incorrect set of permissions
        assertEquals(decodeAcl(org1Acl),decodeAcl(service.readAclById(org1FolderIncorectUri)));
    }

    @Test()
    public void readHierarchyAclWithoutProtocolInUri() throws Exception {
        assertEquals(decodeAcl(org1Acl),decodeAcl(service.readAclById(org1FolderWithoutProtocol)));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void readHierarchyAclWithMissingCacheEntry() throws Exception {
        // in some cases cache records of parents could be missing(for example high level parent was having permissions change), service should fail it
        assertEquals(decodeAcl(org2Acl),decodeAcl(service.readAclById(org2Folder)));

    }
    //test disabled because it`s require SpringContext
    @Test(enabled = false)
    public void readAclDuringImport() throws Exception {
        // When import is happend we always should get Admin access
        ImportRunMonitor.start();
        assertEquals(decodeAcl(org2Acl),decodeAcl(service.readAclById(org2Folder)));
        ImportRunMonitor.stop();

    }

}