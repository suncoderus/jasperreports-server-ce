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

package com.jaspersoft.jasperserver.jaxrs.connection;

import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * @author ykovalch
 * @version $Id$
 */

@Component
@Aspect
public class BlockContextAspect {
    private static final List<String> mimeTypesToBlock = Arrays.asList(
            ResourceMediaType.DOMAIN_JSON.toLowerCase(),
            ResourceMediaType.SEMANTIC_LAYER_DATA_SOURCE_JSON.toLowerCase(),
            ResourceMediaType.REPORT_UNIT_JSON.toLowerCase(),
            ResourceMediaType.DOMAIN_TOPIC_JSON.toLowerCase());
    @Before("execution(public * ConnectionsJaxrsService.createConnection(..))")
    public void checkMediaType(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String contentType = request.getHeader("Content-Type");
        if (contentType != null) {
            final String loverCaseContentType = contentType.toLowerCase();
            for (String mimeType : mimeTypesToBlock) {
                if(loverCaseContentType.contains(mimeType)){
                    throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
                }
            }
        }
    }

}