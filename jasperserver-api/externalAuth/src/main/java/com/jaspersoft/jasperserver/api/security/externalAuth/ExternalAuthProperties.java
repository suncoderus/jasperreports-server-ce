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
package com.jaspersoft.jasperserver.api.security.externalAuth;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;

/**
 * External authentication properties
 *
 * User: dlitvak
 * Date: 8/28/12
 */
@JasperServerAPI
public class ExternalAuthProperties implements InitializingBean {
	public static final String SSO_SERVER_LOCATION_CONFIGURATION_CONSTATNT = "#ssoServerLocation#";

	//if true, always display tenant id input field on the login form (default is false)
	private boolean alwaysRequestOrgIdOnLoginForm;
	private String logoutUrl;

	// url for the request from the SSO to the server with ticket in request
	private String authenticationProcessingUrl;
	// the parameter name to mark the SSO server to redirect back to the server with the ticket
	private String serviceParameterName;
	// the parameter name which the ticket will be added to in either the url or header
	private String ticketParameterName;

	// url to issue a ticket
	private String externalLoginUrl;
	// internal login page url. This page redirects to externalLoginUrl.
	// This extra-page solution is needed because of same origin principle in AJAX:
	// ajax cannot redirect across domains upon session expiration.
	private String loginUrl;

	// url to validate a ticket
	private String ssoServerTicketValidationUrl;

	//custom properties
	private Map<String, String> customPropertyMap;

	//sso server location
	private String ssoServerLocation;

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	/**
	 *
	 * @return URI intercepted by AuthenticationProcessingFilter to start authentication process.
	 * SSO server redirects to this URI on accepting user credentials.
	 */
	public String getAuthenticationProcessingUrl() {
		return authenticationProcessingUrl;
	}

	public void setAuthenticationProcessingUrl(String authenticationProcessingUrls) {
		this.authenticationProcessingUrl = authenticationProcessingUrls;
	}

	/**
	 *
	 * @return SSO server login screen URL. AuthenticationEntryPoint redirects to this URL
	 * instead of login screen.
	 */
	public String getExternalLoginUrl() {
		if (externalLoginUrl != null && externalLoginUrl.startsWith(SSO_SERVER_LOCATION_CONFIGURATION_CONSTATNT))
			externalLoginUrl = externalLoginUrl.replace(SSO_SERVER_LOCATION_CONFIGURATION_CONSTATNT, ssoServerLocation);
		return externalLoginUrl;
	}

	public void setExternalLoginUrl(String externalLoginUrl) {
		this.externalLoginUrl = externalLoginUrl;
	}

	/**
	 *
	 * @return URL for SSO token/ticket validation
	 */
	public String getSsoServerTicketValidationUrl() {
		if (ssoServerTicketValidationUrl != null && ssoServerTicketValidationUrl.startsWith(SSO_SERVER_LOCATION_CONFIGURATION_CONSTATNT))
			ssoServerTicketValidationUrl = ssoServerTicketValidationUrl.replace(SSO_SERVER_LOCATION_CONFIGURATION_CONSTATNT, ssoServerLocation);
		return ssoServerTicketValidationUrl;
	}

	public void setSsoServerTicketValidationUrl(String ssoServerTicketValidationUrl) {
		this.ssoServerTicketValidationUrl = ssoServerTicketValidationUrl;
	}

	/**
	 * Service parameter name in ssoServerTicketValidationUrl
	 * @return
	 */
	public String getServiceParameterName() {
		return serviceParameterName;
	}

	public void setServiceParameterName(String serviceParameterName) {
		this.serviceParameterName = serviceParameterName;
	}

	/**
	 *
	 * @return SSO token parameter name in ssoServerTicketValidationUrl
	 */
	public String getTicketParameterName() {
		return ticketParameterName;
	}

	public void setTicketParameterName(String ticketParameterName) {
		this.ticketParameterName = ticketParameterName;
	}

	/**
	 *
	 * @param propertyName
	 * @return A value corresponding to propertyName key in the customPropertyMap; configured in application context
	 */
	public String getCustomSsoProperty(String propertyName) {
		return customPropertyMap.get(propertyName);
	}

	/**
	 * Injected via Spring configuration
	 * @param customPropertyMap
	 */
	public void setCustomPropertyMap(Map<String, String> customPropertyMap) {
		this.customPropertyMap = customPropertyMap;
	}

	public void setSsoServerLocation(String ssoServerLocation) {
		this.ssoServerLocation = ssoServerLocation;
	}

	public String getSsoServerLocation() {
		return ssoServerLocation;
	}

	public boolean isAlwaysRequestOrgIdOnLoginForm() {
		return alwaysRequestOrgIdOnLoginForm;
	}

	/**
	 * Sets up login form tenant id field.
	 * @param alwaysRequestOrgIdOnLoginForm if true, always display tenant id input field on the login form (default is false)
	 */
	public void setAlwaysRequestOrgIdOnLoginForm(boolean alwaysRequestOrgIdOnLoginForm) {
		this.alwaysRequestOrgIdOnLoginForm = alwaysRequestOrgIdOnLoginForm;
	}

	public String getLogoutUrl() {
		if (logoutUrl != null && logoutUrl.startsWith(SSO_SERVER_LOCATION_CONFIGURATION_CONSTATNT))
			logoutUrl = logoutUrl.replace(SSO_SERVER_LOCATION_CONFIGURATION_CONSTATNT, ssoServerLocation);
		return logoutUrl;
	}

	/**
	 *
	 * @param logoutUrl URL to which the user is redirected during external authentication
	 */
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLoginUrl() {
		return loginUrl;
	}
}
