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

package com.jaspersoft.jasperserver.api.common.error.handling;

import junit.framework.Assert;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Unit tests for {@link ExceptionOutputManagerImpl} class.
 *
 * @author dlitvak
 * @version $Id$
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        LogManager.class,
        SecurityContextHolder.class,
        ExceptionOutputManagerImpl.class
})
public class ExceptionOutputManagerImplTest {

    @Mock private Logger loggerMock;
    @Mock private SecurityContext secureContextMock;
    @Mock private Authentication authenticationMock;

    private ExceptionOutputManagerImpl outputManager;
    private Map<String, List<String>> outputControlMap;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(LogManager.class, SecurityContextHolder.class);
        PowerMockito.when(LogManager.getLogger(ExceptionOutputManagerImpl.class)).thenReturn(loggerMock);
        PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(secureContextMock);

        Mockito.doReturn(authenticationMock).when(secureContextMock).getAuthentication();
        Mockito.doReturn(true).when(authenticationMock).isAuthenticated();

        outputManager = new ExceptionOutputManagerImpl();
        outputControlMap = new LinkedHashMap<String, List<String>>() {{
            put("ERROR_UID", singletonList("ROLE_USER"));
            put("STACKTRACE", singletonList("ROLE_SUPERUSER"));
            put("MESSAGE", asList("ROLE_SUPERUSER", "ROLE_ADMINISTRATOR"));
        }};
    }

    @Test
    public void shouldReturnProperExceptionMessageAllowedFragIfUserHasPermissionToSeeExceptionMessageInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = asList(
                new MyTenantAwareGrantedAuthority("ROLE_USER", null),
                new MyTenantAwareGrantedAuthority("ROLE_ADMINISTRATOR", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        Mockito.doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isExceptionMessageAllowed = outputManager.isExceptionMessageAllowed();

        // then
        Assert.assertTrue(isExceptionMessageAllowed);
    }

    @Test
    public void shouldReturnProperExceptionMessageAllowedFragIfUserDoesNotHavePermissionToSeeExceptionMessageInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = singletonList(
                new MyTenantAwareGrantedAuthority("ROLE_USER", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        Mockito.doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isExceptionMessageAllowed = outputManager.isExceptionMessageAllowed();

        // then
        Assert.assertFalse(isExceptionMessageAllowed);
    }

    @Test
    public void shouldReturnProperUIDOutputOnFragIfUserHasPermissionToSeeUniqueIdentifierInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = singletonList(
                new MyTenantAwareGrantedAuthority("ROLE_USER", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        Mockito.doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isUIDOutputOn = outputManager.isUIDOutputOn();

        // then
        Assert.assertTrue(isUIDOutputOn);
    }

    @Test
    public void shouldReturnProperUIDOutputOnFragIfUserDoesNotHavePermissionToSeeUniqueIdentifierInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = singletonList(
                new MyTenantAwareGrantedAuthority("ROLE_SUPERUSER", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        Mockito.doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isUIDOutputOn = outputManager.isUIDOutputOn();

        // then
        Assert.assertFalse(isUIDOutputOn);
    }

    @Test
    public void shouldReturnProperStackTraceAllowedFragIfUserHasPermissionToSeeStackTraceInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = singletonList(
                new MyTenantAwareGrantedAuthority("ROLE_SUPERUSER", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        Mockito.doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isStackTraceAllowed = outputManager.isStackTraceAllowed();

        // then
        Assert.assertTrue(isStackTraceAllowed);
    }

    @Test
    public void shouldReturnProperStackTraceAllowedFragIfUserDoesNotHavePermissionToSeeStackTraceInMessages() {
        //given
        final Collection<? extends GrantedAuthority> authorities = asList(
                new MyTenantAwareGrantedAuthority("ROLE_USER", null),
                new MyTenantAwareGrantedAuthority("ROLE_ADMINISTRATOR", null)
        );

        outputManager.setOutputControlMap(outputControlMap);
        Mockito.doReturn(authorities).when(authenticationMock).getAuthorities();

        // when
        boolean isStackTraceAllowed = outputManager.isStackTraceAllowed();

        // then
        Assert.assertFalse(isStackTraceAllowed);
    }

    @After
    public void after() {
        Mockito.reset(loggerMock, secureContextMock, authenticationMock);
    }

    /**
     * For test purposes only.
     */
    private final class MyTenantAwareGrantedAuthority implements GrantedAuthority {
        private String authority;
        private String tenantId;

        public MyTenantAwareGrantedAuthority(String authority, String tenantId) {
            this.authority = authority;
            this.tenantId = tenantId;
        }

        @Override
        public String getAuthority() {
            return (tenantId == null) ? authority : authority + "|" + tenantId;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }
    }
}
