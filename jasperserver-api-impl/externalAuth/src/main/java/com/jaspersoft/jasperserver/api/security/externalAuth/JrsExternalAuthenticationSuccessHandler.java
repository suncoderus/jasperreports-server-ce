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

import com.jaspersoft.jasperserver.api.security.JrsAuthenticationSuccessHandler;
import com.jaspersoft.jasperserver.api.security.internalAuth.InternalAuthenticationToken;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: dlitvak
 * Date: 7/28/14
 */
public class JrsExternalAuthenticationSuccessHandler extends JrsAuthenticationSuccessHandler implements InitializingBean {
	private ExternalDataSynchronizer externalDataSynchronizer;

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(externalDataSynchronizer, "externalDataSynchronizer cannot be null");
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		try {
			super.onAuthenticationSuccess(request, response, authentication);

			if (!(authentication instanceof InternalAuthenticationToken))
				externalDataSynchronizer.synchronize();
		} catch (RuntimeException e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			throw e;
		}
	}

	/**
	 *
	 * @param externalDataSynchronizer
	 */
	public void setExternalDataSynchronizer(ExternalDataSynchronizer externalDataSynchronizer) {
		this.externalDataSynchronizer = externalDataSynchronizer;
	}

	protected ExternalDataSynchronizer getExternalDataSynchronizer() {
		return externalDataSynchronizer;
	}
}
