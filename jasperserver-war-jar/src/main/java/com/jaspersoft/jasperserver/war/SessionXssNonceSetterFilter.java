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

package com.jaspersoft.jasperserver.war;

import org.owasp.csrfguard.CsrfGuard;
import org.owasp.csrfguard.util.RandomGenerator;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Add XSS Nonce to the session attributes.  This is done in a filter rather than in HttpSessionListener
 * because of the Clustering.  If the session attribute is set in HttpSessionListener.sessionCreated, other
 * nodes in the cluster would not pick it up.
 *
 * @author dlitvak
 * @version $Id$
 */
public class SessionXssNonceSetterFilter implements Filter{

    public static final String XSS_NONCE_ATTRIB_NAME = "XSS_NONCE";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpSession session = httpRequest.getSession(true);

            String tokenValue = (String) session.getAttribute(XSS_NONCE_ATTRIB_NAME);

            /* If xss nonce is not found, generate a new token and store it in the session. */
            if (tokenValue == null) {
                CsrfGuard csrfGuard = CsrfGuard.getInstance();
                tokenValue = RandomGenerator.generateRandomId(csrfGuard.getPrng(), csrfGuard.getTokenLength());
                session.setAttribute(XSS_NONCE_ATTRIB_NAME, "nonce-" + tokenValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("unable to generate the random XSS token - %s", e.getLocalizedMessage()), e);
        }

        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}

