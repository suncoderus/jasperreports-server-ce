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

package com.jaspersoft.jasperserver.war.util;

import com.jaspersoft.jasperserver.api.security.encryption.EncryptionRequestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lucian Chirita
 */
public class RequestParameterAuthenticationFilter implements Filter {

	private static final Log log = LogFactory.getLog(RequestParameterAuthenticationFilter.class);

	private AuthenticationManager authenticationManager;
	private String authenticationFailureUrl;
	private String[] excludeUrls;

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (requiresAuthentication(httpRequest)) {
			String username = EncryptionRequestUtils.getValue(httpRequest, UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
			String password = EncryptionRequestUtils.getValue(httpRequest, UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY);
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
			authRequest.setDetails(new WebAuthenticationDetails(httpRequest));

			Authentication authResult;
			final SecurityContext securityContext = SecurityContextHolder.getContext();
			try {
				authResult = authenticationManager.authenticate(authRequest);
			} catch (AuthenticationException e) {
				if (log.isDebugEnabled()) {
					log.debug("User " + username + " failed to authenticate: " + e.toString());
				}

				securityContext.setAuthentication(null);
				httpResponse.sendRedirect(httpResponse.encodeRedirectURL(getFullFailureUrl(httpRequest)));
				return;
			}

			if (log.isDebugEnabled()) {
				log.debug("User " + username + " authenticated: " + authResult);
			}

			securityContext.setAuthentication(authResult);
            //[bug 40360] - Fix Spring Security multi-threading bug.
            new HttpSessionSecurityContextRepository().saveContext(securityContext, (HttpServletRequest)request, (HttpServletResponse)response);
			onSuccessfulAuthentication(httpRequest, httpResponse, authResult);
		}

		chain.doFilter(request, response);
	}

	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
											  Authentication authResult) throws IOException, ServletException {

	}

	protected boolean requiresAuthentication(HttpServletRequest request) {
		boolean authenticate;
		String username = EncryptionRequestUtils.getValue(request, UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
		if (username == null) {
			authenticate = false;
		} else {
			Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
			if (existingAuth != null
					&& existingAuth.isAuthenticated()
					&& (existingAuth.getName().equals(username) || username.startsWith(existingAuth.getName() + "|" /*username|tenantId case*/))) {
				authenticate = false;
			} else {
				authenticate = !isUrlExcluded(request);
			}
		}
		return authenticate;
	}

	protected boolean isUrlExcluded(HttpServletRequest request) {
		if (excludeUrls == null || excludeUrls.length == 0) {
			return false;
		}

		String uri = request.getRequestURI();
		int pathParamIndex = uri.indexOf(';');
		if (pathParamIndex > 0) {
			uri = uri.substring(0, pathParamIndex);
		}

		for (int i = 0; i < excludeUrls.length; i++) {
			String excludedUrl = excludeUrls[i];
			if (uri.endsWith(request.getContextPath() + excludedUrl)) {
				return true;
			}
		}
		return false;
	}

	protected String getFullFailureUrl(HttpServletRequest request) {
		return request.getContextPath() + authenticationFailureUrl;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public String getAuthenticationFailureUrl() {
		return authenticationFailureUrl;
	}

	public void setAuthenticationFailureUrl(String authenticationFailureUrl) {
		this.authenticationFailureUrl = authenticationFailureUrl;
	}

	public String[] getExcludeUrls() {
		return excludeUrls;
	}

	public void setExcludeUrls(String[] excludeUrls) {
		this.excludeUrls = excludeUrls;
	}
}
