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

import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.util.test.BaseServiceSetupTestNG;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * @author swood
 * @version $Id$
 */
public class UserAuthorityServiceTestTestNG extends BaseServiceSetupTestNG {

    protected static Log m_logger = LogFactory.getLog(UserAuthorityServiceTestTestNG.class);

	public UserAuthorityServiceTestTestNG(){
        m_logger.info("UserAuthorityServiceTestTestNG => constructor() called");
    }

    @BeforeClass()
    protected void onSetUp() throws Exception {
        m_logger.info("UserAuthorityServiceTestTestNG => onSetUp() called");
    }

    @AfterClass()
	public void onTearDown() {
        m_logger.info("UserAuthorityServiceTestTestNG => onTearDown() called");

	    // get rid of the specific roles that were created just for this particular test
        m_logger.info("UserAuthorityServiceTestTestNG => onTearDown() deleting ROLE 'anotherRole'");
        getUserAuthorityService().deleteRole(null, "anotherRole");

        m_logger.info("UserAuthorityServiceTestTestNG => onTearDown() deleting ROLE 'ROLE_ETL_ADMIN'");
        getUserAuthorityService().deleteRole(null, "ROLE_ETL_ADMIN");

        m_logger.info("UserAuthorityServiceTestTestNG => onTearDown() deleting ROLE 'ROLE_ETL'");
        getUserAuthorityService().deleteRole(null, "ROLE_ETL");

        m_logger.info("UserAuthorityServiceTestTestNG => onTearDown() deleting ROLE 'newRole'");
        getUserAuthorityService().deleteRole(null, "newRole");

	    // get rid of the specific users that were created just for this particular test
        m_logger.info("UserAuthorityServiceTestTestNG => onTearDown() deleting USER 'etladmin'");
        getUserAuthorityService().deleteUser(null, "etladmin");

        m_logger.info("UserAuthorityServiceTestTestNG => onTearDown() deleting USER 'etluser'");
        getUserAuthorityService().deleteUser(null, "etluser");

        m_logger.info("UserAuthorityServiceTestTestNG => onTearDown() deleting USER 'newUser'");
        getUserAuthorityService().deleteUser(null, "newUser");
	}

    /**
     *  doNewUserTest
     */
    @Test()
	public void doNewUserTest() {
        m_logger.info("UserAuthorityServiceTestTestNG => doNewUserTest() called");

        // create a new user, "newUser"
        m_logger.info("UserAuthorityServiceTestTestNG => doNewUserTest() creating USER 'newUser'");
		User newUser = createUser("newUser", "newPassword", "newUser");

		// verify there are no roles added yet
		assertTrue(newUser.getRoles() == null || newUser.getRoles().isEmpty());

        // create and add a new "newRole" to this new user and verify...
        m_logger.info("UserAuthorityServiceTestTestNG => doNewUserTest() creating and adding ROLE 'newRole'");
        createRole("newRole");
		addRole(newUser, "newRole");
		assertTrue(newUser.getRoles() != null || newUser.getRoles().size() == 1);
	}

    /**
     *  doOrdinaryETLUserTest
     */
    @Test(dependsOnMethods = "doNewUserTest")
	public void doOrdinaryETLUserTest() {
        m_logger.info("UserAuthorityServiceTestTestNG => doOrdinaryETLUserTest() called");

        // create a new user, "etluser"
        m_logger.info("UserAuthorityServiceTestTestNG => doOrdinaryETLUserTest() creating USER 'etluser'");
		User newUser = createUser("etluser", "etluser", "etluser");

		// verify there are no roles added yet
		assertTrue(newUser.getRoles() == null || newUser.getRoles().isEmpty());

        // add an existing "ROLE_USER" to this new user and verify...
		addRole(newUser, BaseServiceSetupTestNG.ROLE_USER);
		assertTrue(newUser.getRoles() != null || newUser.getRoles().size() == 1);

        // create and add a new "ROLE_ETL" to this new user and verify...
        m_logger.info("UserAuthorityServiceTestTestNG => doOrdinaryETLUserTest() creating and adding ROLE 'ROLE_ETL'");
        createRole("ROLE_ETL");
		addRole(newUser, "ROLE_ETL");
		assertTrue(newUser.getRoles() != null || newUser.getRoles().size() == 2);
	}

    /**
     *  doAdminETLUserTest
     */
    @Test(dependsOnMethods = "doOrdinaryETLUserTest")
	public void doAdminETLUserTest() {
        m_logger.info("UserAuthorityServiceTestTestNG => doAdminETLUserTest() called");

        // create a new user, "etladmin"
        m_logger.info("UserAuthorityServiceTestTestNG => doAdminETLUserTest() creating USER 'etladmin'");
		User newUser = createUser("etladmin", "etladmin", "etladmin");

		// verify there are no roles added yet
		assertTrue(newUser.getRoles() == null || newUser.getRoles().isEmpty());

        // add an existing "ROLE_USER" to this new user and verify...
		addRole(newUser, BaseServiceSetupTestNG.ROLE_USER);
		assertTrue(newUser.getRoles() != null || newUser.getRoles().size() == 1);

        // add an existing "ROLE_ADMINISTRATOR" to this new user and verify...
		addRole(newUser, BaseServiceSetupTestNG.ROLE_ADMINISTRATOR);
		assertTrue(newUser.getRoles() != null || newUser.getRoles().size() == 2);

        // create and add a new "ROLE_ETL_ADMIN" to this new user and verify...
        m_logger.info("UserAuthorityServiceTestTestNG => doAdminETLUserTest() creating and adding ROLE 'ROLE_ETL_ADMIN'");
        createRole("ROLE_ETL_ADMIN");
		addRole(newUser, "ROLE_ETL_ADMIN");
		assertTrue(newUser.getRoles() != null || newUser.getRoles().size() == 3);
	}

    /**
     *  doUpdateUserTest
     */
    @Test(dependsOnMethods = "doAdminETLUserTest")
	public void doUpdateUserTest() {
        m_logger.info("UserAuthorityServiceTestTestNG => doUpdateUserTest() called");

        // get the "newUser" user that was created in our first test and verify the user is enabled...
		User newUser = getUser("newUser");
		assertNotNull(newUser);
		assertTrue(newUser.isEnabled());

        // now disable the "newUser" and verify the user is disabled...
		getUserAuthorityService().disableUser(null, newUser.getUsername());
		newUser = getUser("newUser");
		assertTrue("error: user still disabled", !newUser.isEnabled());

        // create and add "anotherRole" to this "newUser" and verify...
        m_logger.info("UserAuthorityServiceTestTestNG => doUpdateUserTest() creating and adding ROLE 'anotherRole'");
        createRole("anotherRole");
		addRole(newUser, "anotherRole");
		Role anotherRole = getUserAuthorityService().getRole(null, "anotherRole");
		assertTrue("error: 'anotherRole' does not exist", anotherRole != null);
		assertTrue("error: newUser does not have anotherRole", newUser.getRoles().contains(anotherRole));

        // remove "anotherRole" from this "newUser" and verify...
		getUserAuthorityService().putUser(null, newUser);
		newUser.removeRole(anotherRole);
		assertTrue("error: newUser still contains anotherRole", !newUser.getRoles().contains(anotherRole));

        // re-fetch this "newUser" from the authority service and re-verify the "anotherRole" really is gone...
		getUserAuthorityService().putUser(null, newUser);
		newUser = getUserAuthorityService().getUser(null, "newUser");
		assertTrue("error: after retrieval - newUser REALLY still contains anotherRole", !newUser.getRoles().contains(anotherRole));
	}

    /**
     *  doGetUsersAndRolesTest
     */
    @Test(dependsOnMethods = "doUpdateUserTest" )
	public void doGetUsersAndRolesTest() {
        m_logger.info("UserAuthorityServiceTestTestNG => doGetUsersAndRolesTest() called");

		// Depending on the order of test classes that are run, we could have more users and roles
		// than we created here.
		List results = getUserAuthorityService().getUsers(null, null);
		assertTrue("getUsers right size: expected at least 3, got " + results.size(), results.size() >= 3);
		results = getUserAuthorityService().getRoles(null, null);
		assertTrue("getRoles right size: expected at least 4, got " + results.size(), results.size() >= 4);
	}

}
