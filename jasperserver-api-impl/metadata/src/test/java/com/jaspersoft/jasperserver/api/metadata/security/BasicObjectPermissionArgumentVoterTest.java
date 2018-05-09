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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.util.SimpleMethodInvocation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class BasicObjectPermissionArgumentVoterTest {
    Object object;
    private BasicObjectPermissionArgumentVoter voter = mock(BasicObjectPermissionArgumentVoter.class);

    @BeforeMethod
    public void resetMock(){
        reset(voter);
    }

    @Test
    public void supports_class(){
        when(voter.supports(any(Class.class))).thenCallRealMethod();
        assertTrue(voter.supports(MethodInvocation.class));
        assertTrue(voter.supports(ObjectPermission.class));
        assertTrue(voter.supports(Collection.class));
        assertFalse(voter.supports(String.class));
    }

    @Test
    public void supports_class_config_argumentTypeCheck(){
        when(voter.supports(String.class)).thenReturn(true);
        when(voter.supports(Integer.class)).thenReturn(false);
        when(voter.supports(any(ConfigAttribute.class))).thenReturn(true);
        when(voter.supports(any(Class.class), any(Collection.class))).thenCallRealMethod();
        List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>(1);
        attributes.add(new ConfigAttributeMock("test"));
        assertTrue(voter.supports("testString", attributes));
        assertFalse(voter.supports(Integer.valueOf(1), attributes));
    }

    private static class ConfigAttributeMock implements ConfigAttribute{
        private String attribute;
        public ConfigAttributeMock(String attribute){
            this.attribute = attribute;
        }
        @Override
        public String getAttribute() {
            return attribute;
        }
    }

    @Test
    public void supports_class_config_attributesCheck(){
        when(voter.supports(any(Class.class))).thenReturn(true);
        when(voter.supports(any(ConfigAttribute.class))).thenReturn(true);
        when(voter.supports(any(Class.class), any(Collection.class))).thenCallRealMethod();
        List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>(1);
        attributes.add(new ConfigAttributeMock("testAttribute"));
        assertTrue(voter.supports("testString", attributes));
        when(voter.supports(any(ConfigAttribute.class))).thenReturn(false);
        assertFalse(voter.supports("testString", attributes));
    }

    @Test
    public void vote_ACCESS_ABSTAIN_noPermissions(){
        when(voter.getObjectPermissions(any(Object.class))).thenReturn(new ArrayList<ObjectPermission>());
        when(voter.supports(any(Object.class), isNull(Collection.class))).thenReturn(true);
        when(voter.vote(isNull(Authentication.class), any(Object.class), isNull(Collection.class))).thenCallRealMethod();
        assertEquals(voter.vote(null, new Object(), null), AccessDecisionVoter.ACCESS_ABSTAIN);
    }

    @Test
    public void vote_ACCESS_ABSTAIN_supportsFalse(){
        final ArrayList<ObjectPermission> objectPermissions = new ArrayList<ObjectPermission>();
        objectPermissions.add(new ObjectPermissionImpl());
        when(voter.getObjectPermissions(any(Object.class))).thenReturn(objectPermissions);
        when(voter.supports(any(Object.class), isNull(Collection.class))).thenReturn(false);
        when(voter.vote(isNull(Authentication.class), any(Object.class), isNull(Collection.class))).thenCallRealMethod();
        assertEquals(voter.vote(null, new Object(), null), AccessDecisionVoter.ACCESS_ABSTAIN);
    }

    @Test
    public void vote_ACCESS_DENIED(){
        final List<ObjectPermission> permissions = new ArrayList<ObjectPermission>();
        final ObjectPermissionImpl objectPermission = new ObjectPermissionImpl();
        permissions.add(objectPermission);
        when(voter.getObjectPermissions(any(Object.class))).thenReturn(permissions);
        when(voter.supports(any(Object.class), isNull(Collection.class))).thenReturn(true);
        when(voter.vote(isNull(Authentication.class), any(Object.class), isNull(Collection.class))).thenCallRealMethod();
        assertEquals(voter.vote(null, object, null), AccessDecisionVoter.ACCESS_DENIED);
        verify(voter).isPermitted(null, objectPermission, object);
    }

    @Test
    public void vote_ACCESS_DENIED_multiplePermissions(){
        final List<ObjectPermission> permissions = new ArrayList<ObjectPermission>();
        final ObjectPermissionImpl objectPermission1 = new ObjectPermissionImpl();
        final ObjectPermissionImpl objectPermission2 = new ObjectPermissionImpl();
        permissions.add(objectPermission1);
        permissions.add(objectPermission2);
        when(voter.getObjectPermissions(any(Object.class))).thenReturn(permissions);
        when(voter.supports(any(Object.class), isNull(Collection.class))).thenReturn(true);
        when(voter.vote(isNull(Authentication.class), any(Object.class), isNull(Collection.class))).thenCallRealMethod();
        when(voter.isPermitted(null, objectPermission1, object)).thenReturn(true);
        when(voter.isPermitted(null, objectPermission2, object)).thenReturn(false);
        assertEquals(voter.vote(null, new Object(), null), AccessDecisionVoter.ACCESS_DENIED);
    }

    @Test
    public void vote_ACCESS_GRANTED(){
        final List<ObjectPermission> permissions = new ArrayList<ObjectPermission>();
        final ObjectPermissionImpl objectPermission = new ObjectPermissionImpl();
        permissions.add(objectPermission);
        when(voter.getObjectPermissions(any(Object.class))).thenReturn(permissions);
        when(voter.supports(any(Object.class), isNull(Collection.class))).thenReturn(true);
        when(voter.vote(isNull(Authentication.class), any(Object.class), isNull(Collection.class))).thenCallRealMethod();
        when(voter.isPermitted(null, objectPermission, object)).thenReturn(true);
        assertEquals(voter.vote(null, object, null), AccessDecisionVoter.ACCESS_GRANTED);
    }

    @Test
    public void vote_ACCESS_DENIED_Privileged(){
        final List<ObjectPermission> permissions = new ArrayList<ObjectPermission>();
        final ObjectPermissionImpl objectPermission = new ObjectPermissionImpl();
        final ExecutionContext context = new ExecutionContextImpl();
        final MethodInvocation methodInvocation  = new SimpleMethodInvocation(null, null, new Object[]{context});
        permissions.add(objectPermission);
        context.getAttributes().add(ObjectPermissionService.PRIVILEGED_OPERATION);
        when(voter.getObjectPermissions(any(Object.class))).thenReturn(permissions);
        when(voter.supports(any(Object.class), isNull(Collection.class))).thenReturn(true);
        when(voter.vote(isNull(Authentication.class), any(Object.class), isNull(Collection.class))).thenCallRealMethod();
        assertEquals(voter.vote(null, methodInvocation, null), AccessDecisionVoter.ACCESS_GRANTED);
    }

    @Test
    public void getObjectPermissions_singlePermission(){
        when(voter.getObjectPermissions(any(Object.class))).thenCallRealMethod();
        final ObjectPermissionImpl objectPermission = new ObjectPermissionImpl();
        final Collection<ObjectPermission> objectPermissions = voter.getObjectPermissions(objectPermission);
        assertNotNull(objectPermissions);
        assertEquals(objectPermissions.size(), 1);
        assertSame(objectPermissions.iterator().next(), objectPermission);
    }

    @Test
    public void getObjectPermissions_anotherType(){
        when(voter.getObjectPermissions(any(Object.class))).thenCallRealMethod();
        final Collection<ObjectPermission> objectPermissions = voter.getObjectPermissions("testString");
        assertNotNull(objectPermissions);
        assertTrue(objectPermissions.isEmpty());
    }

    @Test
    public void getObjectPermissions_PermissionsCollection(){
        when(voter.getObjectPermissions(any(Object.class))).thenCallRealMethod();
        List<ObjectPermission> permissionsCollection = new ArrayList<ObjectPermission>();
        permissionsCollection.add(new ObjectPermissionImpl());
        permissionsCollection.add(new ObjectPermissionImpl());
        permissionsCollection.add(new ObjectPermissionImpl());
        permissionsCollection.add(new ObjectPermissionImpl());
        final Collection<ObjectPermission> objectPermissions = voter.getObjectPermissions(permissionsCollection);
        assertNotNull(objectPermissions);
        assertEquals(objectPermissions.size(), permissionsCollection.size());
        assertTrue(objectPermissions.containsAll(permissionsCollection));
    }

    @Test
    public void getObjectPermissions_PermissionsCollection_anotherType(){
        when(voter.getObjectPermissions(any(Object.class))).thenCallRealMethod();
        List<String> permissionsCollection = new ArrayList<String>();
        permissionsCollection.add("test1");
        permissionsCollection.add("test2");
        permissionsCollection.add("test3");
        permissionsCollection.add("test4");
        final Collection<ObjectPermission> objectPermissions = voter.getObjectPermissions(permissionsCollection);
        assertNotNull(objectPermissions);
        assertTrue(objectPermissions.isEmpty());
    }

    @Test
    public void getObjectPermissions_methodInvocation(){
        when(voter.getObjectPermissions(any(Object.class))).thenCallRealMethod();
        MethodInvocation methodInvocation = mock(MethodInvocation.class);
        final ObjectPermissionImpl expectedObject = new ObjectPermissionImpl();
        when(methodInvocation.getArguments()).thenReturn(new Object[]{expectedObject});
        final Collection<ObjectPermission> objectPermissions = voter.getObjectPermissions(methodInvocation);
        assertNotNull(objectPermissions);
        assertEquals(objectPermissions.size(), 1);
        assertSame(objectPermissions.iterator().next(), expectedObject);
    }

    @Test
    public void getObjectPermissions_methodInvocation_multipleArguments(){
        when(voter.getObjectPermissions(any(Object.class))).thenCallRealMethod();
        MethodInvocation methodInvocation = mock(MethodInvocation.class);
        final ObjectPermissionImpl expectedObject1 = new ObjectPermissionImpl();
        final ObjectPermissionImpl expectedObject2 = new ObjectPermissionImpl();
        final ObjectPermissionImpl expectedObject3 = new ObjectPermissionImpl();
        when(methodInvocation.getArguments()).thenReturn(new Object[]{expectedObject1, expectedObject2, expectedObject3});
        final Collection<ObjectPermission> objectPermissions = voter.getObjectPermissions(methodInvocation);
        assertNotNull(objectPermissions);
        assertEquals(objectPermissions.size(), 3);
        List<ObjectPermission> inputPermissions = new ArrayList<ObjectPermission>();
        inputPermissions.add(expectedObject1);
        inputPermissions.add(expectedObject2);
        inputPermissions.add(expectedObject3);
        assertTrue(objectPermissions.containsAll(inputPermissions));
    }
}
