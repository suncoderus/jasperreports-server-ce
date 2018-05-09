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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This servlet forwards requests from a path like
 * http://server:port/jrs/runtime/38479AD4/[scripts or optimized-scripts]/path/resource
 * to
 * http://server:port/jrs/[scripts or optimized-scripts]/path/resource
 * The "38479AD4" part is dynamically generated and changes on server start.
 *
 * The purpose for it is to help a proper cache management on client side.
 * We want resources to be cached by a browser for as long as we can, to reduce traffic between
 * the browser and the server related to static files (like javascript, etc.)
 * But we also want these resources to be updated when a client upgrades the server or makes modifications.
 *
 * @author asokolnikov
 */
public class ResourceForwardingServlet extends HttpServlet {
    private static Logger logger = LogManager.getLogger(ResourceForwardingServlet.class);

    //Forwarded parameter was added 'cos WebSphere erase parameters from request on forward which results in bug JRS-10031
    //Do not remove this to avoid possible issues with WebSphere
    public static final String FORWARDED_PARAMETERS = "forwardedParameters";

    private static final String FWD_FORBIDDEN_DIRECTORIES_PARAM = "forwardForbiddenDirectories";
    private List<String> forwardForbiddenDirectories = new ArrayList<String>() {{ add("WEB-INF"); add("META-INF");}};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String resourcePath = req.getPathInfo() == null ? path : path.concat(req.getPathInfo());
        String newResourcePath = resourcePath.replaceFirst("^/[^/]+/[^/]*", "");

        if (logger.isDebugEnabled())
            logger.debug("Forwarded resource path " + resourcePath + " to " + newResourcePath);

        for (String forbiddenDir : forwardForbiddenDirectories) {
            if (newResourcePath.toUpperCase().startsWith("/" + forbiddenDir + "/")) {
                resp.sendError(403);
                return;
            }
        }

        if (req.getParameterMap() != null) {
            req.setAttribute(FORWARDED_PARAMETERS, req.getParameterMap());
        }
        req.getRequestDispatcher(newResourcePath).forward(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String forwardForbiddenDirectoriesStr = config.getInitParameter(FWD_FORBIDDEN_DIRECTORIES_PARAM);
        if (forwardForbiddenDirectoriesStr != null && !forwardForbiddenDirectoriesStr.trim().isEmpty()) {
            for (String fdir : forwardForbiddenDirectoriesStr.split(","))
                forwardForbiddenDirectories.add(fdir.trim().toUpperCase());
        }
    }

}
