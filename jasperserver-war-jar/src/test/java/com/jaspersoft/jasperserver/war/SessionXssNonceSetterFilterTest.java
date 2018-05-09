package com.jaspersoft.jasperserver.war;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.owasp.csrfguard.CsrfGuard;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.Properties;

import static com.jaspersoft.jasperserver.war.SessionXssNonceSetterFilter.XSS_NONCE_ATTRIB_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author dlitvak
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class SessionXssNonceSetterFilterTest {

    private SessionXssNonceSetterFilter filter = new SessionXssNonceSetterFilter();

    private HttpSession sessionMock = new MockHttpSession();

    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private FilterChain filterChainMock;

    @BeforeClass
    public static void setup() throws Exception {
        //load CsrfGuard properties
        InputStream is = SessionXssNonceSetterFilterTest.class.getClassLoader().getResourceAsStream("jrs.csrfguard.sessionxssnonce.test.properties");
        Properties properties = new Properties();
        properties.load(is);
        CsrfGuard.load(properties);
    }

    @Before
    public void setupTest() {
        when(requestMock.getSession()).thenReturn(sessionMock);
        when(requestMock.getSession(true)).thenReturn(sessionMock);
        when(requestMock.getSession(false)).thenReturn(sessionMock);

    }

    @Test
    public void testRandomTokenIsGeneratedOncePerSesssion() throws Exception {
        filter.doFilter(requestMock, responseMock, filterChainMock);
        Object token1 = sessionMock.getAttribute(XSS_NONCE_ATTRIB_NAME);
        filter.doFilter(requestMock, responseMock, filterChainMock);
        Object token2 = sessionMock.getAttribute(XSS_NONCE_ATTRIB_NAME);

        assertNotNull("XSS Nonce is null", token1 );
        assertNotNull("XSS Nonces is null", token2 );
        assertTrue("XSS Nonces must be strings", token1 instanceof String && token2 instanceof String);
        assertEquals("XSS Nonce was generated more than once per session.", token1, token2);
    }

    @Test
    public void testRandomTokensAreUniquePerSesssion() throws Exception {
        filter.doFilter(requestMock, responseMock, filterChainMock);
        Object token1 = sessionMock.getAttribute(XSS_NONCE_ATTRIB_NAME);
        sessionMock.removeAttribute(XSS_NONCE_ATTRIB_NAME);
        filter.doFilter(requestMock, responseMock, filterChainMock);
        Object token2 = sessionMock.getAttribute(XSS_NONCE_ATTRIB_NAME);

        assertNotNull("XSS Nonce is null", token1 );
        assertNotNull("XSS Nonces is null", token2 );
        assertTrue("XSS Nonces must be strings", token1 instanceof String && token2 instanceof String);
        assertTrue("XSS Nonces are not random", !token1.equals(token2));
    }
}

