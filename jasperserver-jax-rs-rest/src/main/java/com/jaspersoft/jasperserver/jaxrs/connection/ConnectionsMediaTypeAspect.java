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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.regex.Pattern;

/**
 * @author serhii.blazhyievskyi
 * @version $Id$
 */

@Component
@Aspect
public class ConnectionsMediaTypeAspect {
    private static final Pattern XML_MIME_TYPE = Pattern.compile("application/(.+\\+)?xml", Pattern.CASE_INSENSITIVE);

    @Before("execution(public * ConnectionsJaxrsService.*(..))")
    public void checkMediaType(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String mediaType = request.getHeader("Content-Type");
        String accept = request.getHeader("accept");
        if (mediaType != null && XML_MIME_TYPE.matcher(mediaType).matches()) {
            throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
        }
        if (accept != null && XML_MIME_TYPE.matcher(accept).matches()){
            throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
        }
    }
}
