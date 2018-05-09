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

import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.builders.LocalizedErrorDescriptorBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author: Zakhar.Tomchenco
 */
@Provider
@Component
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {

    @Resource(name= "localizedErrorDescriptorBuilder")
    private LocalizedErrorDescriptorBuilder localizedErrorDescriptorBuilder;

    public Response toResponse(AccessDeniedException exception) {
        return Response.status(Response.Status.FORBIDDEN)
                       .entity(localizedErrorDescriptorBuilder.localizeDescriptor(exception.getErrorDescriptor()))
                       .build();
    }
}
