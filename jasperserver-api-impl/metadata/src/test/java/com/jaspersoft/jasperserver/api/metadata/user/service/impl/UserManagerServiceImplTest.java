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
import com.jaspersoft.jasperserver.api.metadata.user.service.UserManagerService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;

import static com.jaspersoft.jasperserver.api.metadata.user.service.impl.testutils.CustomArgumentMatchers.*;
import static org.unitils.mock.ArgumentMatchers.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
public class UserManagerServiceImplTest extends UnitilsJUnit4 {

    @TestedObject
    private UserManagerServiceImpl managerService;

    @InjectInto(property = "userService")
    private Mock<UserAuthorityService> userServiceMock;

    String demo;
    String first;
    String second; 

    User demoUser;
    User firstUser;
    User secondUser;

    Role demoRole;
    Role firstRole;
    Role secondRole;


    @Before
    public void before() {
        demo = "demo";
        first = "first";
        second = "second";

        demoUser = new UserImpl();
        demoUser.setUsername(demo);

        firstUser = new UserImpl();
        firstUser.setUsername(first);
        firstUser.setEnabled(true);

        secondUser = new UserImpl();
        secondUser.setUsername(second);
        secondUser.setEnabled(false);        

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
    public void enableAll() {
        List<String> users = new ArrayList<String>();
        users.add(demo);
        users.add(first);
        users.add(second);

        userServiceMock.returns(clone(demoUser)).getUser(null, demo);
        userServiceMock.returns(clone(firstUser)).getUser(null, first);
        userServiceMock.returns(clone(secondUser)).getUser(null, second);

        managerService.enableAll(null, users);

        userServiceMock.assertInvoked().getUser(null, demo);
        userServiceMock.assertInvoked().updateUser(null, demo, userUpdated(demoUser));

        userServiceMock.assertInvoked().getUser(null, first);

        userServiceMock.assertInvoked().getUser(null, second);
        userServiceMock.assertInvoked().updateUser(null, second, userUpdated(secondUser));
    }

    @Test
    public void enableAllIfNull() {
        // check if null
        managerService.enableAll(null, null);
    }

    @Test
    public void enableAllIfEmptyList() {
        managerService.enableAll(null, new ArrayList<String>());
    }

    @Test
    public void enableAllIfListWithNullAndEmptyValue() {
        List<String> users = new ArrayList<String>();
        users.add(null);
        users.add("");

        managerService.enableAll(null, users);
    }

    @Test
    public void disableAll() {
        List<String> users = new ArrayList<String>();
        users.add(demo);
        users.add(first);
        users.add(second);

        userServiceMock.returns(clone(demoUser)).getUser(null, demo);
        userServiceMock.returns(clone(firstUser)).getUser(null, first);
        userServiceMock.returns(clone(secondUser)).getUser(null, second);

        managerService.disableAll(null, users);

        userServiceMock.assertInvoked().getUser(null, demo);

        userServiceMock.assertInvoked().getUser(null, first);
        userServiceMock.assertInvoked().updateUser(null, first, userUpdated(firstUser));

        userServiceMock.assertInvoked().getUser(null, second);
    }

    @Test
    public void disableAllIfNull() {
        managerService.disableAll(null, null);
    }

    @Test
    public void disableAllIfEmptyList() {
        managerService.disableAll(null, new ArrayList<String>());
    }

    @Test
    public void disableAllIfListWithNullAndEmptyValue() {
        List<String> users = new ArrayList<String>();
        users.add(null);
        users.add("");

        managerService.disableAll(null, users);
    }

    @Test
    public void deleteAll() {
        // in ideal case
        List<String> users = new ArrayList<String>();
        users.add(demo);
        users.add(first);
        users.add(second);

        managerService.deleteAll(null, users);
        
        userServiceMock.assertInvoked().deleteUser(null, demo);
        userServiceMock.assertInvoked().deleteUser(null, first);
        userServiceMock.assertInvoked().deleteUser(null, second);
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
        List<String> users = new ArrayList<String>();
        users.add(null);
        users.add("");

        managerService.deleteAll(null, users);
    }

//    @Test
    public void updateUser() {
        Set assignedNameSet = new HashSet();
        Set unassignedNameSet = new HashSet();

        assignedNameSet.add(first);
        assignedNameSet.add(second);

        unassignedNameSet.add(demo);

        List<Role> available = new ArrayList<Role>();
        Set<Role> assignedSet = new HashSet<Role>();
        Set<Role> unassignedSet = new HashSet<Role>();

        available.add(firstRole);
        available.add(secondRole);

        assignedSet.add(firstRole);
        assignedSet.add(secondRole);

        unassignedSet.add(demoRole);

        FilterCriteria filter = FilterCriteria.createFilter(Role.class);

        userServiceMock.returns(clone(demoUser)).getUser(null, demo);
        userServiceMock.returns(available).getRoles(null, filter);

        managerService.updateUser(null, demo, demoUser, assignedSet, unassignedSet);

        userServiceMock.assertInvoked().getUser(null, demo);
        userServiceMock.assertInvoked().getRoles(null, filter);
        userServiceMock.assertInvoked().updateUser(null, eq(demo), eq(demoUser));
    }


    private User clone(User user) {
        UserImpl clone = new UserImpl();

        clone.setUsername(user.getUsername());
        clone.setTenantId(user.getTenantId());
        clone.setEnabled(user.isEnabled());

        return clone;
    }

}
