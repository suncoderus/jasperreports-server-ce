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
package com.jaspersoft.jasperserver.externalAuth.test;

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalAuthProperties;
import com.jaspersoft.jasperserver.api.security.externalAuth.processors.ExternalUserSetupProcessor;
import com.jaspersoft.jasperserver.externalAuth.mocks.MockExternalJDBCUserDetailsService;
import com.jaspersoft.jasperserver.externalAuth.mocks.MockSsoTicketValidatorImpl;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static org.testng.Assert.*;

/**
 * All test methods are ran as @Transactional, i.e. within transactions, because the test extends
 * AbstractTransactionalTestNG4SpringContextTests.  By default, every test rollbacks transaction.
 *
 * If you persist some data to the database, be sure to execute sessionFactory.flush() before reading
 * the data.  If you forget to flush the data, you may NOT find the data in the database due to hibernate's
 * lazy/delayed execution of persistence context.  This may result in false negative test results.
 *
 * User: dlitvak
 * Date: 9/12/12
 */
@ContextConfiguration(locations = {"classpath:sample-applicationContext-externalAuth-sso.xml",
		"classpath:testMocks/externalAuth-test-mocks.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SSOIntegrationTest extends BaseTransactionalTestNGSpringContextTests {

	@Resource(name = "authenticationAuthoirizationFilterChainProxy")
	private FilterChainProxy filterChainProxy;

	@Resource(name = "proxyExceptionTranslationFilter")
	private ExceptionTranslationFilter proxyExceptionTranslationFilter;

	@Resource(name = "externalAuthProperties")
	private ExternalAuthProperties externalAuthProperties;

	@Resource(name = "httpBasedSsoTicketValidator")
	private MockSsoTicketValidatorImpl mockSsoTicketValidator;

	@Resource(name = "externalUserSetupProcessor")
	private ExternalUserSetupProcessor externalUserSetupProcessor;

	@Resource(name = "externalJDBCUserDetailsService")
	private MockExternalJDBCUserDetailsService mockExternalJDBCUserDetailsService;

	/**
	 * Testing sso filter chain external user creation without external roles
	 */
	@Test
	public void testUserWithoutExternalRoles() {
		try {
			logger.info("Starting testUserWithoutExternalRoles.");

			createTenant("", TenantService.ORGANIZATIONS, "root", TenantService.ORGANIZATIONS, " ", "/", "/", "default");

			//test redirect to login screen
			final MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/home.html");
			mockRequest.setServletPath("/home.html");

			final MockHttpServletResponse mockResponse = new MockHttpServletResponse();
			assertNull(mockResponse.getRedirectedUrl(), "Redirect url should be null at the start");
			filterChainProxy.doFilter(mockRequest, mockResponse, new MockFilterChain());

			// verify redirect url is correct
			final String redirectUrlStr = mockResponse.getRedirectedUrl();
			assertNotNull(redirectUrlStr, "Redirect url should not be null");
			logger.info("SSO redirect url: " + redirectUrlStr);
			String configuredLoginFormUrl =
					((LoginUrlAuthenticationEntryPoint)proxyExceptionTranslationFilter.getAuthenticationEntryPoint()).getLoginFormUrl();
			assertNotNull(mockResponse.getRedirectedUrl(), "Redirect url should NOT be null at the start");
			final String responseRedirectUrlPath = new URL(mockResponse.getRedirectedUrl()).getPath();
			final String configuredLoginFormUrlPath = new URI(configuredLoginFormUrl).getPath();
			assertTrue(responseRedirectUrlPath.equalsIgnoreCase(configuredLoginFormUrlPath), "The url to which sso redirected (" + redirectUrlStr + ") was not the as expected " + configuredLoginFormUrl);

			//test mock ticket validation
			assertNull(mockSsoTicketValidator.getTicketValidationUrl(), "Ticket validation url should be null at the start");
			final String testTicket = "testTicket";
			MockHttpServletRequest mockRequest2 = new MockHttpServletRequest("GET", "/j_spring_security_check");
			mockRequest2.setServletPath("/j_spring_security_check");

			mockRequest2.setParameter(externalAuthProperties.getTicketParameterName(), testTicket);
			RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest2));
			filterChainProxy.doFilter(mockRequest2, new MockHttpServletResponse(), new MockFilterChain());

			//check ticket validation Url is ok
			final URI ticketValidationUrl = mockSsoTicketValidator.getTicketValidationUrl();
			assertNotNull(ticketValidationUrl, "Ticket validation url should not be null");

			final URI testTicketValidationUrl = new URI(externalAuthProperties.getSsoServerTicketValidationUrl() + "?" +
					externalAuthProperties.getTicketParameterName() + "=" + testTicket +
					"&" + externalAuthProperties.getServiceParameterName() + "=" + URLEncoder.encode(mockRequest2.getRequestURL().toString(), "ISO-8859-1"));
			assertTrue(testTicketValidationUrl.equals(ticketValidationUrl), "Ticket validation url " + ticketValidationUrl + " was not as expected: " + testTicketValidationUrl);

			//check user created
			User user = userAuthorityService.getUser(new ExecutionContextImpl(), mockSsoTicketValidator.getTestValidatedPrincipal());
			assertNotNull(user, "External user was not added to jasperserver database after authentication.");
			assertTrue(user.isExternallyDefined(), "User must be externally defined in db.");
			logger.info("created user " + user.getUsername());

			Set<String> defaultInternalRoles = new HashSet<String>(externalUserSetupProcessor.getDefaultInternalRoles());
			Set<Role> userRoles = user.getRoles();
			assertNotNull(userRoles, "Test user must have non-null userRoles in this test");
			assertEquals(userRoles.size(), defaultInternalRoles.size(), "Test user must have " + defaultInternalRoles.size() + " default role(s) in this test");
			for (Role r : userRoles) {
				assertTrue(!r.isExternallyDefined(), "All roles must be internally defined in this test.");
				assertTrue(defaultInternalRoles.remove(r.getRoleName()), r.getRoleName() + " internal role was improperly assigned to the test user.");
			}
			assertEquals(defaultInternalRoles.size(), 0, "Some default internal roles are missing for the test user: " + defaultInternalRoles.toString());

			//check folder created
//			Folder folder = repositoryService.getFolder(new ExecutionContextImpl(), "/" + user.getUsername());
//			assertNotNull(folder, "User folder was not created.");
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	/**
	 * Testing sso filter chain external user creation with additional external roles
	 */
	@Test
	public void testUserWithExternalRoles() {
		try {
			logger.info("Starting testUserWithExternalRoles.");

			createTenant("", TenantService.ORGANIZATIONS, "root", TenantService.ORGANIZATIONS, " ", "/", "/", "default");

			final String EXTERNAL_TEST_ROLE_1 = "EXTERNAL_TEST_ROLE_1";
			final String EXTERNAL_TEST_ROLE_2 = "EXTERNAL_TEST_ROLE_2";
			final List<String> EXTERNAL_TEST_ROLE_LIST = new LinkedList<String>(Arrays.asList(EXTERNAL_TEST_ROLE_1, EXTERNAL_TEST_ROLE_2));
			mockExternalJDBCUserDetailsService.setExternalUserRoles(EXTERNAL_TEST_ROLE_LIST);

			//test redirect to login screen
			final MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/home.html");
			mockRequest.setServletPath("/home.html");
			final MockHttpServletResponse mockResponse = new MockHttpServletResponse();
			assertNull(mockResponse.getRedirectedUrl(), "Redirect url should be null at the start");
			filterChainProxy.doFilter(mockRequest, mockResponse, new MockFilterChain());

			// verify redirect url is correct
			final String redirectUrlStr = mockResponse.getRedirectedUrl();
			assertNotNull(redirectUrlStr, "Redirect url should not be null");
			logger.info("SSO redirect url: " + redirectUrlStr);
			String configuredLoginFormUrl =
					((LoginUrlAuthenticationEntryPoint)proxyExceptionTranslationFilter.getAuthenticationEntryPoint()).getLoginFormUrl();
			assertNotNull("Redirect url should NOT be null at the start", mockResponse.getRedirectedUrl());
			final String responseRedirectUrlPath = new URL(mockResponse.getRedirectedUrl()).getPath();
			final String configuredLoginFormUrlPath = new URI(configuredLoginFormUrl).getPath();
			assertTrue(responseRedirectUrlPath.equalsIgnoreCase(configuredLoginFormUrlPath), "The url to which sso redirected (" + redirectUrlStr + ") was not the as expected " + configuredLoginFormUrl);

			//test mock ticket validation.  Login user and verify its roles
			assertNull(mockSsoTicketValidator.getTicketValidationUrl(), "Ticket validation url should be null at the start");
			final String testTicket = "testTicket";
			MockHttpServletRequest mockRequest2 = new MockHttpServletRequest("GET", "/j_spring_security_check");
			mockRequest2.setServletPath("/j_spring_security_check");

			mockRequest2.setParameter(externalAuthProperties.getTicketParameterName(), testTicket);
			RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest2));
			filterChainProxy.doFilter(mockRequest2, new MockHttpServletResponse(), new MockFilterChain());

			//check ticket validation Url is ok
			final URI ticketValidationUrl = mockSsoTicketValidator.getTicketValidationUrl();
			assertNotNull(ticketValidationUrl, "Ticket validation url should not be null");

			final URI testTicketValidationUrl = new URI(externalAuthProperties.getSsoServerTicketValidationUrl() + "?" +
					externalAuthProperties.getTicketParameterName() + "=" + testTicket +
					"&" + externalAuthProperties.getServiceParameterName() + "=" + URLEncoder.encode(mockRequest2.getRequestURL().toString(), "ISO-8859-1"));
			assertTrue(testTicketValidationUrl.equals(ticketValidationUrl), "Ticket validation url " + ticketValidationUrl + " was not as expected: " + testTicketValidationUrl);

			//check user created
			User user = userAuthorityService.getUser(new ExecutionContextImpl(), mockSsoTicketValidator.getTestValidatedPrincipal());
			assertNotNull(user, "External user was not added to jasperserver database after authentication.");
			assertTrue(user.isExternallyDefined(), "User must be externally defined in db.");
			logger.info("created user " + user.getUsername());

			Set<String> defaultInternalRoles = new HashSet<String>(externalUserSetupProcessor.getDefaultInternalRoles());
			Set<Role> userRoles = user.getRoles();
			assertNotNull(userRoles, "Test user must have non-null userRoles in this test");

			//NOTE: we assume that there is no mapping of external to internal roles in the sample file
			final String ROLE_PREFIX = "ROLE_";
			Set<String> userInternalRoles = new HashSet<String>();
			Set<String> userExternalRoles = new HashSet<String>();
			for (Role r : userRoles) {
				String roleName = r.getRoleName();
				if (r.isExternallyDefined()) {
					userExternalRoles.add(roleName.startsWith(ROLE_PREFIX) ? roleName.substring(ROLE_PREFIX.length()): roleName);
				}
				else
					userInternalRoles.add(roleName);
			}
			assertTrue(userInternalRoles.equals(defaultInternalRoles), "The new user should be assigned all default internal roles in this test.");
			final Set<String> EXTERNAL_TEST_ROLE_SET = new HashSet<String>(EXTERNAL_TEST_ROLE_LIST);
			assertTrue(userExternalRoles.equals(EXTERNAL_TEST_ROLE_SET), "The new user should be assigned " + EXTERNAL_TEST_ROLE_SET + " external roles in this test.");

			//check folder created
//			Folder folder = repositoryService.getFolder(new ExecutionContextImpl(), "/" + user.getUsername());
//			assertNotNull(folder, "User folder was not created.");

			/* #############
             * Simulate re-login with only 1 external role EXTERNAL_TEST_ROLE_1.
             * The re-loggedin user should have only 1 external role EXTERNAL_TEST_ROLE_1.
			 */
			//change external roles
			EXTERNAL_TEST_ROLE_LIST.remove(EXTERNAL_TEST_ROLE_2);
			mockExternalJDBCUserDetailsService.setExternalUserRoles(EXTERNAL_TEST_ROLE_LIST);

			//re-login user
			final String testTicket3 = "testTicket";
			MockHttpServletRequest mockRequest3 = new MockHttpServletRequest("GET", "/j_spring_security_check");
			mockRequest3.setServletPath("/test.html");
			mockRequest3.setParameter(externalAuthProperties.getTicketParameterName(), testTicket3);
			RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest3));
			filterChainProxy.doFilter(mockRequest3, new MockHttpServletResponse(), new MockFilterChain());

			User user2 = userAuthorityService.getUser(new ExecutionContextImpl(), mockSsoTicketValidator.getTestValidatedPrincipal());
			assertNotNull(user2, "External user was not added to jasperserver database after authentication.");
			assertTrue(user2.isExternallyDefined(), "User must be externally defined in db.");

			Set<Role> userRoles2 = user2.getRoles();
			assertNotNull(userRoles2, "Test user must have non-null userRoles in this test");

			//NOTE: we assume that there is no mapping of external to internal roles in the sample file
			Set<String> userExternalRoles2 = new HashSet<String>();
			Set<String> userInternalRoles2 = new HashSet<String>();
			for (Role r : userRoles2) {
				String roleName = r.getRoleName();
				if (r.isExternallyDefined()) {
					userExternalRoles2.add(roleName.startsWith(ROLE_PREFIX) ? roleName.substring(ROLE_PREFIX.length()): roleName);
				}
				else
					userInternalRoles2.add(roleName);
			}
			assertTrue(userInternalRoles2.equals(defaultInternalRoles), "The new user should be assigned all default internal roles in this test.");
			final Set<String> EXTERNAL_TEST_ROLE_SET_2 = new HashSet<String>(EXTERNAL_TEST_ROLE_LIST);
			assertTrue(userExternalRoles2.equals(EXTERNAL_TEST_ROLE_SET_2), "The new user should be assigned " + EXTERNAL_TEST_ROLE_SET_2 + " external roles in this test.");
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}


	@AfterMethod
	public void  tearDown() {
		mockSsoTicketValidator.cleanup();
		mockExternalJDBCUserDetailsService.cleanup();
	}

}
