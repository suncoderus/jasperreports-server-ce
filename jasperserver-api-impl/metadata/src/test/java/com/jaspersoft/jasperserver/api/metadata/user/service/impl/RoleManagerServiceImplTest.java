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

import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.RoleImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.UserImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import static org.unitils.mock.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jaspersoft.jasperserver.api.metadata.user.service.impl.testutils.CustomArgumentMatchers.userUpdated;

/**
 */
public class RoleManagerServiceImplTest extends UnitilsJUnit4 {

    @TestedObject
    private RoleManagerServiceImpl managerService;

    @InjectInto(property = "userService")
    private Mock<UserAuthorityService> userServiceMock;

    @InjectInto(property = "transactionManager")
    private Mock<AbstractPlatformTransactionManager> transactionManager;

    String demo;
    String first;
    String second;

    String bob;
    String steve;
    String bill;
    String jon;

    User bobUser;
    User steveUser;
    User billUser;
    User jonUser;

    Role demoRole;
    Role firstRole;
    Role secondRole;

    @Before
    public void before() {
        demo = "demo";
        first = "first";
        second = "second";

        bob = "bob";
        steve = "steve";
        bill = "bill";
        jon = "jon";

        bobUser = new UserImpl();
        bobUser.setUsername(bob);

        steveUser = new UserImpl();
        steveUser.setUsername(steve);

        billUser = new UserImpl();
        billUser.setUsername(bill);

        jonUser = new UserImpl();
        jonUser.setUsername(jon);

        demoRole = new RoleImpl();
        demoRole.setRoleName(demo);

        firstRole = new RoleImpl();
        firstRole.setRoleName(first);

        secondRole = new RoleImpl();
        secondRole.setRoleName(second);

        userServiceMock.resetBehavior();
    }

    @After
    public void after() {
        MockUnitils.assertNoMoreInvocations();
    }

    @Test
    public void deleteAll() {
        List<String> roles = new ArrayList<String>();
        roles.add(demo);
        roles.add(first);
        roles.add(second);

        managerService.deleteAll(null, roles);

        userServiceMock.assertInvoked().deleteRole(null, demo);
        userServiceMock.assertInvoked().deleteRole(null, first);
        userServiceMock.assertInvoked().deleteRole(null, second);
    }

    @Test
    public void deleteAllIfNull() {
        managerService.deleteAll(null, null);
    }

    @Test
    public void deleteAllIfEmptyList() {
        managerService.deleteAll(null, new ArrayList<String>());
    }

    @Test
    public void deleteAllIfListWithNullAndEmptyValue() {
        List<String> roles = new ArrayList<String>();
        roles.add(null);
        roles.add("");

        managerService.deleteAll(null, roles);
    }

    @Test
    public void updateRole() {
        Set assignedUserNameSet = new HashSet();
        Set unassignedUserNameSet = new HashSet();

        assignedUserNameSet.add(bob);
        assignedUserNameSet.add(steve);

        unassignedUserNameSet.add(bill);
        unassignedUserNameSet.add(jon);

        Set<User> assignedUserSet = new HashSet<User>();
        Set<User> unassignedUserSet = new HashSet<User>();

        assignedUserSet.add(bobUser);
        assignedUserSet.add(steveUser);

        unassignedUserSet.add(billUser);
        unassignedUserSet.add(jonUser);

        managerService.updateRole(null, demo, demoRole, assignedUserSet, unassignedUserSet);

        userServiceMock.assertInvoked().assignUsers(null, eq(demo), eq(assignedUserNameSet));
        userServiceMock.assertInvoked().unassignUsers(null, eq(demo), eq(unassignedUserNameSet));
        userServiceMock.assertInvoked().updateRole(null, eq(demo), eq(demoRole));
    }

}