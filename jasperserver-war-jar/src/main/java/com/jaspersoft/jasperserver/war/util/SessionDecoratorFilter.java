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

import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * <p>Puts sessionDecorator parameter into the session if it was found in the request parameters list.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 * @since 5.0.1
 */
public class SessionDecoratorFilter implements Filter {
    private static String SESSION_DECORATOR = "sessionDecorator";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing.
    }

    @Override
    public void destroy() {
        // Nothing.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        String sessionDecorator = request.getParameter(SESSION_DECORATOR);

        if (!StringUtils.isEmpty(sessionDecorator)) {
            if (request instanceof HttpServletRequest) {
                HttpSession session = ((HttpServletRequest) request).getSession();

                session.setAttribute(SESSION_DECORATOR, sessionDecorator);
            }
        }

        chain.doFilter(request, response);
    }
}
