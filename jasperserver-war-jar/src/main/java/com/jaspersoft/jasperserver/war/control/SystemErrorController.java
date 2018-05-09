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
package com.jaspersoft.jasperserver.war.control;

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles system errors.
 *
 * @author Yuriy Plakosh
 */
public class SystemErrorController extends JRBaseMultiActionController {

    public static final String DELETE = "DELETE";

	@Resource
	private SecureExceptionHandler secureExceptionHandler;

    public SystemErrorController(){
        final List<String> supportedMethodsList = new ArrayList<String>(Arrays.asList(getSupportedMethods()));
        supportedMethodsList.add("DELETE");
        supportedMethodsList.add("PUT");
        setSupportedMethods(StringUtils.toStringArray(supportedMethodsList));
    }
    
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Handles 404 error (page not found error).
     *
     * @param req the request.
     * @param res the response.
     *
     * @return model and view.
     */
    public ModelAndView handle404(HttpServletRequest req, HttpServletResponse res) {
        return new ModelAndView("modules/system/404");
    }

    /**
     * Handles 500 error (page not found error).
     *
     * @param req the request.
     * @param res the response.
     *
     * @return model and view.
     */
    public ModelAndView handle500(HttpServletRequest req, HttpServletResponse res) {
        ModelAndView mav = new ModelAndView("modules/system/500");

        String systemErrorDetails = (String)req.getAttribute("javax.servlet.error.message");

        Object e = req.getAttribute("javax.servlet.error.exception");

        if (e != null && e instanceof Throwable) {
            logger.error("Internal server error", (Throwable)e);
			if (systemErrorDetails == null || systemErrorDetails.length() == 0) {
				ErrorDescriptor ed = secureExceptionHandler.handleException((Throwable) e);
				systemErrorDetails = ed.getMessage();
			}
        }

        mav.addObject("systemErrorDetails", systemErrorDetails);
        return mav;
    }
}
