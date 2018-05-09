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

import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapManagementService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author swood
 *
 */
public class OLAPController extends JRBaseMultiActionController {

    private OlapManagementService olapManagementService;

	public ModelAndView viewOlap(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/modules/olap/viewOlap");
	}

	public ModelAndView busy(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/modules/olap/busy");
	}

	public ModelAndView flush(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
        try {
           olapManagementService.flushOlapCache();
           req.setAttribute("ajaxResponseModel", "{\"result\":\"JAM_057_DONE\"}");
        } catch (Exception e) {
           req.setAttribute("ajaxResponseModel", "{\"error\":\"" + e.getMessage() + "\"}");
        }

        return new ModelAndView("/ajax/ajaxresponse");
	}

	public ModelAndView properties(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/modules/olap/properties");
	}

	public ModelAndView error(HttpServletRequest req, HttpServletResponse res)
		throws ServletException {
		return new ModelAndView("/modules/system/prepErrorPage");
	}

    public void setOlapManagementService(OlapManagementService olapManagementService) {
        this.olapManagementService = olapManagementService;
    }
}
