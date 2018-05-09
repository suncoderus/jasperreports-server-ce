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

import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Component
@Provider
public class JSResourceNotFoundExceptionMapper implements ExceptionMapper<JSResourceNotFoundException> {
    @Override
    public Response toResponse(JSResourceNotFoundException exception) {
        String uri = null;
        if (exception.getArgs() != null && exception.getArgs().length > 0) {
            // usually the first argument is a resource URI
            uri = (String) exception.getArgs()[0];
        }
        return Response.status(Response.Status.NOT_FOUND).entity(new ResourceNotFoundException(uri).getErrorDescriptor()).build();
    }
}
