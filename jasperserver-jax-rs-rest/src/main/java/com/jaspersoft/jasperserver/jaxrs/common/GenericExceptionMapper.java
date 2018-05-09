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
package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.remote.exception.ErrorDescriptorBuildingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.UnmarshalException;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Provider
@Component
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    private static final Log log = LogFactory.getLog(GenericExceptionMapper.class);

    @Resource
    private XmlParseExceptionMapper xmlParseExceptionMapper;
    @Resource
    private ErrorDescriptorBuildingService errorDescriptorBuildingService;

    public Response toResponse(Exception exception) {
        Response response;
        if (exception instanceof WebApplicationException) {
            response = handleWebApplicationException(exception);
        } else {
            log.error("Unexpected error occurs", exception);
            response = Response.serverError().entity(errorDescriptorBuildingService.buildErrorDescriptor(exception)).build();
        }
        return response;
    }

    private Response handleWebApplicationException(Exception exception) {
        if (exception.getCause() instanceof UnmarshalException) {
            return xmlParseExceptionMapper.toResponse((UnmarshalException) exception.getCause());
        }
        return ((WebApplicationException) exception).getResponse();
    }
}
