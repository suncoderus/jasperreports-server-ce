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
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.security.JasperServerPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author swood
 *
 */
public class PermissionsServiceTestTestNG extends BaseServiceSetupTestNG {

    protected static Log m_logger = LogFactory.getLog(PermissionsServiceTestTestNG.class);

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

    public PermissionsServiceTestTestNG(){
        m_logger.info("PermissionsServiceTestTestNG => constructor() called");
    }

    @BeforeClass()
    protected void onSetUp() throws Exception {
        m_logger.info("PermissionsServiceTestTestNG => onSetUp() called");

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
        getUserAuthorityService().addRole(null, m_testUser, m_adminRole);

        //m_adminUser = createUser(USER_ADMIN, USER_ADMIN, USER_ADMIN);
        //getUserAuthorityService().addRole(null, m_adminUser, m_adminRole);
    }

    @AfterClass()
    public void onTearDown() {
        m_logger.info("PermissionsServiceTestTestNG => onTearDown() called");

        setAuthenticatedUser(USER_ADMIN);

        // Remove block permissions at the folder level
        m_logger.info("PermissionsServiceTestTestNG => onTearDown() - Deleting permission for " + BaseServiceSetupTestNG.ROLE_USER + " and " + BaseServiceSetupTestNG.ROLE_ADMINISTRATOR + " for " + reportUnitFolder);

        deleteObjectPermission(reportUnitFolder, m_testRole);
        deleteObjectPermission(reportsFolderPath, m_adminRole);

        // Remove block permissions at the folder level
        m_logger.info("PermissionsServiceTestTestNG => onTearDown() - Deleting permission for " + ROLE_USER + " and " + Folder.SEPARATOR);
        deleteObjectPermission("/", m_userRole);

        m_logger.info("PermissionsServiceTestTestNG => onTearDown() - Removing role: " + m_adminRole.getRoleName() + " from user: " + m_testUser.getUsername());
        getUserAuthorityService().removeRole(null, m_testUser, m_adminRole);
        getUserAuthorityService().removeRole(null, m_testUser, m_testRole);

        // remove the test user and the test role
        m_logger.info("PermissionsServiceTestTestNG => onTearDown() - delete the " + USER_TEST);
        getUserAuthorityService().deleteUser(null, USER_TEST);

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
        m_logger.info("PermissionsServiceTestTestNG => doObjectPermissionSetupTest() called");

        setAuthenticatedUser(USER_TEST);

        try {
	        int i1 = getPermissionsService().getAppliedPermissionMaskForObjectAndCurrentUser(reportUnitPath);
	        assertEquals(i1, JasperServerPermission.ADMINISTRATION.getMask());

	        createObjectPermission(reportsFolderPath, m_adminRole, JasperServerPermission.READ_WRITE.getMask());
	        createObjectPermission(reportUnitFolder, m_testRole, JasperServerPermission.DELETE.getMask());
	        int i2 = getPermissionsService().getAppliedPermissionMaskForObjectAndCurrentUser(reportUnitPath);
	        assertEquals(i2, JasperServerPermission.READ_WRITE.getMask()|JasperServerPermission.DELETE.getMask());

	        int i3 = getPermissionsService().getAppliedPermissionMaskForObjectAndCurrentUser(reportsFolderPath);
	        assertEquals(i3, JasperServerPermission.READ_WRITE.getMask());

        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
    }
}
