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

package com.jaspersoft.jasperserver.api.security.externalAuth.sso;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalAuthProperties;
import com.jaspersoft.jasperserver.api.security.externalAuth.ExternalUserDetails;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * This class validates SSO token by contacting an external SSO server
 *
 * @author Chaim Arbiv
 */
@JasperServerAPI
public abstract class AbstractSsoTicketValidator implements SsoTicketValidator {
	private final static Logger logger = LogManager.getLogger(AbstractSsoTicketValidator.class);

	private ExternalAuthProperties externalAuthProperties;
	private ClientHttpRequestFactory clientHttpRequestFactory;

    /**
     * Validate SSO token via HTTP request to an SSO server.
	 *
     * @param ticket ticket/token from SSO server
     * @return Authentication object with principal information returned from the sso
     * @throws AuthenticationServiceException when ticket/token validation fails
     */
    @Override
    public abstract ExternalUserDetails validate(final Object ticket) throws AuthenticationServiceException;

    /**
     * Parse the response from SSO server into an Authentication object.
     *
     * @param response SSO server response to a token validation request
     * @return ExternalUserDetails parsed from the response if the token was valid.
     * @throws AuthenticationServiceException if SSO token was invalid.
     */
    protected abstract ExternalUserDetails parseResponseFromServer(final ClientHttpResponse response) throws AuthenticationServiceException;

	public ExternalAuthProperties getExternalAuthProperties() {
		return externalAuthProperties;
	}

	public void setExternalAuthProperties(ExternalAuthProperties externalAuthProperties) {
		this.externalAuthProperties = externalAuthProperties;
	}

	public ClientHttpRequestFactory getClientHttpRequestFactory() {
		return clientHttpRequestFactory;
	}

	public void setClientHttpRequestFactory(ClientHttpRequestFactory clientHttpRequestFactory) {
		this.clientHttpRequestFactory = clientHttpRequestFactory;
	}
}
