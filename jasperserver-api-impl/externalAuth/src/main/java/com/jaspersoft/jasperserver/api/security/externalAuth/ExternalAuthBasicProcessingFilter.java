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

import com.jaspersoft.jasperserver.api.security.internalAuth.InternalAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: dlitvak
 * Date: 4/24/13
 */
public class ExternalAuthBasicProcessingFilter extends BasicAuthenticationFilter {
	private ExternalDataSynchronizer externalDataSynchronizer;

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();

		Assert.notNull(externalDataSynchronizer, "externalDataSynchronizer cannot be null");
	}

	public ExternalAuthBasicProcessingFilter() {
		super();
	}

	public ExternalAuthBasicProcessingFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	public ExternalAuthBasicProcessingFilter(AuthenticationManager authenticationManager,
											 AuthenticationEntryPoint authenticationEntryPoint) {
		super(authenticationManager, authenticationEntryPoint);
	}

	@Override
	public void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
											  Authentication authResult) throws IOException {
		try {
			if (!(authResult instanceof InternalAuthenticationToken))
				externalDataSynchronizer.synchronize();
		} catch (RuntimeException e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			throw e;
		}
	}


	protected ExternalDataSynchronizer getExternalDataSynchronizer() {
		return externalDataSynchronizer;
	}

	/**
	 * Method injecting post-authentication synchronizer
	 *
	 * @param externalDataSynchronizer
	 */
	public void setExternalDataSynchronizer(ExternalDataSynchronizer externalDataSynchronizer) {
		this.externalDataSynchronizer = externalDataSynchronizer;
	}
}
