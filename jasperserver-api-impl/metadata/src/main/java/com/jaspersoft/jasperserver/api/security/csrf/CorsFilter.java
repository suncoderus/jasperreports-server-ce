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
package com.jaspersoft.jasperserver.api.security.csrf;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Simple HTTP access control (CORS) filter which allows cross domain requests for resources it is applied to in
 * web.xml. Only GET and OPTIONS (preflighted) requests are allowed right now.
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Access-Control-Allow-Origin", "*");

        // Preflighted request.
        if (request.getMethod().equals("OPTIONS")) {
            response.addHeader("Access-Control-Allow-Methods", "GET");
            response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(request, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}