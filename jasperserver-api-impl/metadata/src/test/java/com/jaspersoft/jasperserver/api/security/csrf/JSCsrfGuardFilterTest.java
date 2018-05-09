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

package com.jaspersoft.jasperserver.api.security.csrf;

import org.apache.http.protocol.HTTP;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.owasp.csrfguard.CsrfGuard;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author Anton Fomin
 *
 * jrs.csrfguard.properties:
 * * only POST,PUT,DELETE requests are scanned for CSRF
 */
public class JSCsrfGuardFilterTest extends UnitilsJUnit4 {
    public static final String CSRF_TOKEN_VALUE = "12345_Token";
    public static final String AJAX_HEADER = "X-Requested-With";
    private static JSCsrfGuardFilter filter = new JSCsrfGuardFilter();

    Mock<HttpSession> sessionMock;
    Mock<HttpServletRequest> requestMock;
    Mock<HttpServletResponse> responseMock;
    Mock<FilterChain> filterChainMock;

    @BeforeClass
    public static void setup() throws Exception {
        //load CsrfGuard properties
        InputStream is = JSCsrfGuardFilterTest.class.getClassLoader().getResourceAsStream("jrs.csrfguard.test.properties");
        Properties properties = new Properties();
        properties.load(is);
        CsrfGuard.load(properties);

        filter.setProtectedUserAgentRegexs(Arrays.asList("Mozilla/.*","Opera/.*"));
    }

    @Before
    public void setupTest() {
        sessionMock = MockUnitils.createMock(HttpSession.class);
        requestMock = MockUnitils.createMock(HttpServletRequest.class);
        responseMock = MockUnitils.createMock(HttpServletResponse.class);
        filterChainMock = MockUnitils.createMock(FilterChain.class);

        sessionMock.returns(CSRF_TOKEN_VALUE).getAttribute(CsrfGuard.getInstance().getSessionKey());

        requestMock.returns(sessionMock.getMock()).getSession();
        requestMock.returns(sessionMock.getMock()).getSession(false);
        requestMock.returns(sessionMock.getMock()).getSession(true);
        requestMock.returns(new StringBuffer("testCSRF.html")).getRequestURL();
        requestMock.returns("testCSRF.html").getRequestURI();
    }

    @Test
    public void testRequestWithoutUserAgentPasses() throws Exception {
        requestMock.returns("POST").getMethod();

        Method doFilter = JSCsrfGuardFilter.class.getDeclaredMethod("doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
        doFilter.invoke(filter, requestMock.getMock(), responseMock.getMock(), filterChainMock.getMock());

        filterChainMock.assertInvoked().doFilter(requestMock.getMock(), responseMock.getMock());
    }

    @Test
    public void testRequestWithUnknownUserAgentPasses() throws Exception {
        requestMock.returns("Unknown/1.2.3").getHeader(HTTP.USER_AGENT);
        requestMock.returns("POST").getMethod();

        Method doFilter = JSCsrfGuardFilter.class.getDeclaredMethod("doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
        doFilter.invoke(filter, requestMock.getMock(), responseMock.getMock(), filterChainMock.getMock());

        filterChainMock.assertInvoked().doFilter(requestMock.getMock(), responseMock.getMock());
    }

    @Test
    public void testGetRequestWithoutUserAgentPasses() throws Exception {
        requestMock.returns("Mozilla/1.2.3").getHeader(HTTP.USER_AGENT);
        requestMock.returns("GET").getMethod();

        Method doFilter = JSCsrfGuardFilter.class.getDeclaredMethod("doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
        doFilter.invoke(filter, requestMock.getMock(), responseMock.getMock(), filterChainMock.getMock());

        // 2nd argument is null for 'any' object invokation.  This is because org.owasp.csrfguard.CsrfGuardFilter.doFilter() wraps response in
        // org.owasp.csrfguard.http.InterceptRedirectResponse
        filterChainMock.assertInvoked().doFilter(requestMock.getMock(), null);
    }

    @Test
    public void testNonAjaxRequestWithoutTokenFails() throws Exception {
        requestMock.returns("Mozilla/1.2.3").getHeader(HTTP.USER_AGENT);
        requestMock.returns("POST").getMethod();

        Method doFilter = JSCsrfGuardFilter.class.getDeclaredMethod("doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
        doFilter.invoke(filter, requestMock.getMock(), responseMock.getMock(), filterChainMock.getMock());

        filterChainMock.assertNotInvoked().doFilter(requestMock.getMock(), responseMock.getMock());
    }

    @Test
    public void testNonAjaxRequestWithTokenPasses() throws Exception {
        requestMock.returns("Mozilla/1.2.3").getHeader(HTTP.USER_AGENT);
        requestMock.returns("POST").getMethod();
        requestMock.returns(CSRF_TOKEN_VALUE).getParameter(CsrfGuard.getInstance().getTokenName());

        Method doFilter = JSCsrfGuardFilter.class.getDeclaredMethod("doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
        doFilter.invoke(filter, requestMock.getMock(), responseMock.getMock(), filterChainMock.getMock());

        // 2nd argument is null for 'any' object invokation.  This is because org.owasp.csrfguard.CsrfGuardFilter.doFilter() wraps response in
        // org.owasp.csrfguard.http.InterceptRedirectResponse
        filterChainMock.assertInvoked().doFilter(requestMock.getMock(), null);
    }

    @Test
    public void testAjaxRequestWithoutTokenFails() throws Exception {
        requestMock.returns("Mozilla/1.2.3").getHeader(HTTP.USER_AGENT);
        requestMock.returns("POST").getMethod();
        requestMock.returns("XmlHttpRequest").getHeader(AJAX_HEADER);

        Method doFilter = JSCsrfGuardFilter.class.getDeclaredMethod("doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
        doFilter.invoke(filter, requestMock.getMock(), responseMock.getMock(), filterChainMock.getMock());

        filterChainMock.assertNotInvoked().doFilter(requestMock.getMock(), responseMock.getMock());
    }

    @Test
    public void testAjaxRequestWithTokenPasses() throws Exception {
        requestMock.returns("Mozilla/1.2.3").getHeader(HTTP.USER_AGENT);
        requestMock.returns("POST").getMethod();
        requestMock.returns("XmlHttpRequest").getHeader(AJAX_HEADER);
        requestMock.returns(CSRF_TOKEN_VALUE).getHeader(CsrfGuard.getInstance().getTokenName());

        Method doFilter = JSCsrfGuardFilter.class.getDeclaredMethod("doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
        doFilter.invoke(filter, requestMock.getMock(), responseMock.getMock(), filterChainMock.getMock());

        // 2nd argument is null for 'any' object invokation.  This is because org.owasp.csrfguard.CsrfGuardFilter.doFilter() wraps response in
        // org.owasp.csrfguard.http.InterceptRedirectResponse
        filterChainMock.assertInvoked().doFilter(requestMock.getMock(), null);
    }

}
