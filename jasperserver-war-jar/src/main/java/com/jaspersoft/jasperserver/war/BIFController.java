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

import com.jaspersoft.jasperserver.war.util.ObjectProcessor;
import com.jaspersoft.jasperserver.war.util.ObjectSelector;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Gerald, schubar
 */
public class BIFController implements Controller {

    private String pagesLocation = "/WEB-INF/jsp/modules";

    private ObjectSelector<HttpServletRequest, ObjectProcessor<HttpServletResponse>> responseHeaderSetter;

    public String getPagesLocation() {
        return pagesLocation;
    }

    public void setPagesLocation(String pagesLocation) {
        this.pagesLocation = pagesLocation;
    }

    public void setResponseHeaderSetter(ObjectSelector<HttpServletRequest, ObjectProcessor<HttpServletResponse>> responseHeaderSetter) {
        this.responseHeaderSetter = responseHeaderSetter;
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String resource = request.getPathInfo();
        String basePath = "mods/";
        StringBuilder bifResource = new StringBuilder();
        String requestedResource = "";

        if (resource != null) {
            if(resource.contains(basePath)) {
                int i = resource.indexOf(basePath) + basePath.length();
                requestedResource = resource.substring(i);
                request.setAttribute("requestedBIFResource", requestedResource);
                bifResource.append(this.getPagesLocation()).append("/amd");
            } else {
                for (ResourceExt ext : ResourceExt.values()) {
                    if(resource.endsWith(ext.getExt())) {
                        bifResource.
                                append(this.getPagesLocation()).
                                append(resource.replace(ext.getExt(), ""));
                        break;
                    }
                }
            }
        } else {
            bifResource.append("/");
        }

        setAdditionalResponseHeaders(request, response);

        return new ModelAndView(bifResource.toString());
    }

    protected void setAdditionalResponseHeaders(HttpServletRequest request, HttpServletResponse response) {
        if(responseHeaderSetter == null) {
            return;
        }

        ObjectProcessor<HttpServletResponse> headerSetter = responseHeaderSetter.select(request);

        if (headerSetter != null) {
            headerSetter.process(response);
        }
    }

    private enum ResourceExt {
        JS(".js"), JSP(".jsp"), HTML(".html");

        private String ext;
        private ResourceExt(String ext) {
            this.ext = ext;
        }

        public String getExt() {
            return ext;
        }
    }
}
