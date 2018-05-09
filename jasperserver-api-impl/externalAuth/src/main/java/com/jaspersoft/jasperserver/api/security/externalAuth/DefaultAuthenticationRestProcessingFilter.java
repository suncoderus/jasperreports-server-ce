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

import com.jaspersoft.jasperserver.api.security.EncryptionAuthenticationProcessingFilter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Chaim Arbiv
 * @version $id$
 * set the correct responses for the REST login.
 */
public class DefaultAuthenticationRestProcessingFilter extends EncryptionAuthenticationProcessingFilter {

    @Override
    public void afterPropertiesSet() {
        Assert.hasLength(getFilterProcessesUrl(), "filterProcessesUrl must be specified");
        Assert.isTrue(UrlUtils.isValidRedirectUrl(getFilterProcessesUrl()), getFilterProcessesUrl()+ " isn't a valid redirect URL");

        Assert.notNull(getAuthenticationManager(), "authenticationManager must be specified");

        if (getRememberMeServices() == null) {
            setRememberMeServices(new NullRememberMeServices());
        }
    }

    // On successful login it should let the filter chain end and handle the response (which should be 200)
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        response.setContentType("text/xml; charset=UTF-8");
        return;
    }

    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        response.setContentType("text/xml; charset=UTF-8");
        return;
    }
}
