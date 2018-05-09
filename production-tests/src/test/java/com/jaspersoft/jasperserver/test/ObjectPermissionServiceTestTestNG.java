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
package com.jaspersoft.jasperserver.test;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.JasperServerSidRetrievalStrategyImpl;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * @author swood
 *
 */
public class ObjectPermissionServiceTestTestNG extends BaseServiceSetupTestNG {

    protected static Log m_logger = LogFactory.getLog(ObjectPermissionServiceTestTestNG.class);

    public static String reportsFolderPath = Folder.SEPARATOR + "reports";
    public static String reportUnitFolder = reportsFolderPath + Folder.SEPARATOR + "samples";
    public static String reportUnitName = "AllAccounts";
    public static String reportUnitPath = reportUnitFolder + Folder.SEPARATOR + reportUnitName;

    public static final String USER_ADMIN = "admin";
    public static String USER_TEST = "TestUser";
    public static String ROLE_TEST = "ROLE_TEST";

    ReportUnit m_unit;
    Role m_userRole, m_adminRole, m_testRole;
    User m_adminUser, m_testUser;
    Folder m_reportsFolder;

    public ObjectPermissionServiceTestTestNG(){
        m_logger.info("ObjectPermissionServiceTestTestNG => constructor() called");
    }

    @BeforeClass()
    protected void onSetUp() throws Exception {
        m_logger.info("ObjectPermissionServiceTestTestNG => onSetUp() called");

        // setup the test roles and test users
        setUpRoles();
        setUpUsers();

        // set the admin user to be the authenticated user
        setAuthenticatedUser(USER_ADMIN);
        createObjectPermission("/", m_adminRole, JasperServerPermission.ADMINISTRATION.getMask());

        m_reportsFolder = getRepositoryService().getFolder(null, reportsFolderPath);
        assertTrue("null m_reportsFolder", m_reportsFolder != null);

        // Set up some data
        m_unit = (ReportUnit) getRepositoryService().getResource(null, reportUnitPath);
        assertNotNull("Null ReportUnit", m_unit);
        assertEquals("Unmatched ReportUnit name", reportUnitName, m_unit.getName());

    }

    /**
     * setUpRoles
     *      - gets the userRole (created in the CoreDataCreateTestNG class)
     *      - gets the adminRole (created in the CoreDataCreateTestNG class)
     *      - creates a new testRole
     */
    private void setUpRoles() {
        m_userRole = getRole(BaseServiceSetupTestNG.ROLE_USER);
        m_adminRole = getRole(BaseServiceSetupTestNG.ROLE_ADMINISTRATOR);
        m_testRole = createRole(ROLE_TEST);
    }

    /**
     * setUpUsers
     *      - creates a new testUser
     *      - adds a testRole to the new testUser
     *      - creates a new adminUser
     */
    private void setUpUsers() {
        m_testUser = createUser(USER_TEST, USER_TEST, USER_TEST);
        getUserAuthorityService().addRole(null, m_testUser, m_testRole);

        m_adminUser = createUser(USER_ADMIN, USER_ADMIN, USER_ADMIN);
        getUserAuthorityService().addRole(null, m_adminUser, m_adminRole);
    }

    @AfterClass()
    public void onTearDown() {
        m_logger.info("ObjectPermissionServiceTestTestNG => onTearDown() called");

        // Remove read permissions for the user at the report unit level
        User testUser = getUserAuthorityService().getUser(null, USER_TEST);
        deleteObjectPermission(reportUnitPath, testUser);

        // Remove block permissions at the folder level
        m_logger.info("ObjectPermissionServiceTestTestNG => onTearDown() - Deleting permission for " + BaseServiceSetupTestNG.ROLE_USER + " and " + BaseServiceSetupTestNG.ROLE_ADMINISTRATOR + " for " + reportUnitFolder);

        deleteObjectPermission(reportUnitFolder, m_userRole);
        deleteObjectPermission(reportUnitFolder, m_adminRole);

        // Remove block permissions at the folder level
        m_logger.info("ObjectPermissionServiceTestTestNG => onTearDown() - Deleting permission for " + ROLE_USER + " and " + Folder.SEPARATOR);
        deleteObjectPermission("/", m_userRole);

        m_logger.info("ObjectPermissionServiceTestTestNG => onTearDown() - Removing role: " + m_adminRole.getRoleName() + " from user: " + m_testUser.getUsername());
        getUserAuthorityService().removeRole(null, m_testUser, m_adminRole);

        User u = getUserAuthorityService().getUser(null, m_testUser.getUsername());

        m_logger.info("ObjectPermissionServiceTestTestNG => onTearDown() - test user assigned roles are: ");
        for (Iterator it = u.getRoles().iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();
            m_logger.info("\t" + role.getRoleName());
        }

        // remove the test user and the test role
         m_logger.info("ObjectPermissionServiceTestTestNG => onTearDown() - delete the " + USER_TEST);
        getUserAuthorityService().deleteUser(null, USER_TEST);

         m_logger.info("ObjectPermissionServiceTestTestNG => onTearDown() - delete the " + ROLE_TEST);
        getUserAuthorityService().deleteRole(null, ROLE_TEST);
    }

    /**
     * doObjectPermissionSetupTest
     *
     * For our report unit without a parent:
     *      ROLE_ADMINISTRATOR: JasperServerPermission.ADMINISTRATION
     *      testUserName: TestUser has role: ROLE_TEST
     *      adminUserName: admin has role: ROLE_ADMINISTRATOR
     */
    @Test()
    public void doObjectPermissionSetupTest() {
        m_logger.info("ObjectPermissionServiceTestTestNG => doObjectPermissionSetupTest() called");

        setAuthenticatedUser(USER_ADMIN);

        List l = getObjectPermissionService().getObjectPermissionsForObjectAndRecipient(null, m_unit, m_adminRole);
        assertTrue("getObjectPermissionsForObjectAndRecipient size not 0: " +
                    (l == null ? "null" : (new Integer(l.size())).toString()), l == null || l.size() == 0);

        ReportUnit ru = (ReportUnit) getRepositoryService().getResource(null, m_unit.getURI());
        assertTrue("Null report unit for " + m_unit.getURI(), ru != null);
        assertTrue(ru.getName().equals(reportUnitName));

        String p = ru.getParentFolder();
        Folder f = getRepositoryService().getFolder(null, p);
        assertTrue("Invalid folder: " + p, f != null);

        l = getObjectPermissionService().getObjectPermissionsForObject(null, f);
         m_logger.info("ObjectPermission for unit folder: " + l);

        assertTrue("getObjectPermissionsForObject size not 0: " +
                    (l == null ? "null" : (new Integer(l.size())).toString()), l == null || l.size() == 0);
    }

    /**
     * doAclAccessTest
     */
    // TODO Spring Security Upgrade: move test below to RepositoryAclService
    @Test(dependsOnMethods = "doObjectPermissionSetupTest")
    public void doAclAccessTest() {
        m_logger.info("ObjectPermissionServiceTestTestNG => doAclAccessTest() called");
        //Tis test is disabled because ACL logic was moved from ObjectPermissionService to other place
        return;
/*
        Authentication aUser = setAuthenticatedUser(USER_ADMIN);

        Acl acl = ((AclService) getObjectPermissionService()).readAclById(m_unit);
        assertTrue("aclEntries = null", acl != null);

        printAcl("getAcls(m_unit)", acl);
        assertTrue("aclEntries.length = " + acl.getEntries().size() + " not 2", acl.getEntries().size() == 3);

        Acl userEntries = ((AclService) getObjectPermissionService()).readAclById(m_unit, new JasperServerSidRetrievalStrategyImpl().getSids(aUser));
        assertTrue("userEntries = null", userEntries != null);

        printAcl("getAcls(m_unit, aUser)", userEntries);
        AccessControlEntry found = null;
        Object objectIdentity = acl.getObjectIdentity();
        for(AccessControlEntry ace: acl.getEntries()) {
            found= (objectIdentity instanceof Role && ((Role)objectIdentity).getRoleName().equals(BaseServiceSetupTestNG.ROLE_ADMINISTRATOR)) ? ace : null;
        }
        assertTrue("Role recipient not found", found != null);
*/
    }

    /**
     * doMethodAndObjectAccessTest
     */
    @Test(dependsOnMethods = "doAclAccessTest")
    public void doMethodAndObjectAccessTest() {
        m_logger.info("ObjectPermissionServiceTestTestNG => doMethodAndObjectAccessTest() called");

        setAuthenticatedUser(USER_ADMIN);
        assertNotNull("null testUserName", USER_TEST);

        exerciseResourcesListAccess(USER_ADMIN, true,
                "ERROR: Administrator should have access to report unit via role at root",
                "Administrator has access to report unit");
        m_logger.info("ObjectPermissionServiceTestTestNG => doMethodAndObjectAccessTest() - Administrator has access to report unit");

        /*
         * The TestUser has ROLE_TEST role, but
         * does not have ROLE_ADMINISTRATOR access to the report unit, so it will not appear in the list
         */
        exerciseResourcesListAccess(USER_TEST, false,
                "ERROR: ROLE_TEST should not have access to report unit because there is no permissions for ROLE_TEST or this user",
                "ROLE_TEST does not have access to report unit");
        m_logger.info("ObjectPermissionServiceTestTestNG => doMethodAndObjectAccessTest() - ROLE_TEST does not have access to report unit");

        /*
         * Now add permissions for the testUserName to the report unit
         */
        setAuthenticatedUser(USER_ADMIN);
        ObjectPermission op = createObjectPermission(reportUnitPath, m_testUser, JasperServerPermission.READ.getMask());

        /*
         * Should be able to access now
         */
        exerciseResourcesListAccess(USER_TEST, true,
                "ERROR: Test user should have specific access to report unit",
                "Test user has access to report unit");
        m_logger.info("ObjectPermissionServiceTestTestNG => doMethodAndObjectAccessTest() - Test user has access to report unit");

        setAuthenticatedUser(USER_ADMIN);
        getObjectPermissionService().deleteObjectPermission(null, op);

        // Give the testUserName a ROLE_ADMINISTRATOR role
        getUserAuthorityService().addRole(null, m_testUser, m_adminRole);

        /*
         * Should still be able to access
         */
        exerciseResourcesListAccess(USER_TEST, true,
                "ERROR: ROLE_ADMINISTRATOR should have access to root level",
                "ROLE_ADMINISTRATOR given access to root level");
        m_logger.info("ObjectPermissionServiceTestTestNG => doMethodAndObjectAccessTest() - ROLE_ADMINISTRATOR given access to root level");

        // Check again
        try {
        	getRepositoryService().getResource(null, reportUnitFolder);
            m_logger.info("OK: had access to " + reportUnitFolder);
        } catch (Exception e) {
            fail("ERROR: had no access to " + reportUnitFolder + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printAcl(String name, Acl acl) {

        m_logger.info("Acl: " + name);
        m_logger.info("AclObjectIdentity: " + acl.getObjectIdentity().toString());

        for(AccessControlEntry ace: acl.getEntries()) {
            m_logger.info(ace.toString());
        }

    }

    private void exerciseResourcesListAccess(String userName, boolean expectToFind,
            String testDescription, String successMessage) {
        FilterCriteria criteria = FilterCriteria.createFilter(ReportUnit.class);
        List l;

        setAuthenticatedUser(userName);

        Iterator it;
        boolean found = false;

        try {
            l = getRepositoryService().loadResourcesList(criteria);
            m_logger.info(testDescription +  "\nloadResourcesList size: " + l.size());
            it = l.iterator();
            found = false;
            while (it.hasNext() && !found) {
                ResourceLookup rlu = (ResourceLookup) it.next();
                found = rlu.getURIString().equals(reportUnitPath);
            }

            // If we found and did not expect to, or we did not find and we expected to

            if (found != expectToFind) {
                fail(userName + ": loadResourcesList " + (expectToFind ? "did not contain " : "contained un") + "expected report unit for " + reportUnitName);
            }
        } catch (AccessDeniedException ex) {
            fail(userName + ": loadResourcesList AccessDeniedException unexpected");
        }

        m_logger.info(successMessage);
    }

}
