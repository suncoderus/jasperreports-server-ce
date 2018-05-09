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

import org.owasp.csrfguard.CsrfGuard;
import org.owasp.csrfguard.servlet.JavaScriptServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class delegates to org.owasp.csrfguard.servlet.JavaScriptServlet.
 * This is introduced as a workaround CsrfGuardHttpSessionListener in CSRFGuard lib
 * breaking the server clustering.
 *
 * @author dlitvak
 * @version $Id$
 */
public class JSJavaScriptServlet extends HttpServlet {
    private static final JavaScriptServlet jss = new JavaScriptServlet();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        jss.doGet(req, resp);
    }

    /*
     * Method generating and returning CSRF Token from session.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        synchronized (this) {
            CsrfGuard csrfGuard = CsrfGuard.getInstance();
            String csrfToken = csrfGuard.getTokenValue(req);
            if (csrfToken == null || csrfToken.length() == 0)
                csrfGuard.updateToken(req.getSession(false));
        }
        jss.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        jss.init(config);
    }
}
