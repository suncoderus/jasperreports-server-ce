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

import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.builders.LocalizedErrorDescriptorBuilder;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.ParamException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Provider
@Component
public class GenericParamExceptionMapper implements ExceptionMapper<ParamException> {

    @Resource(name= "localizedErrorDescriptorBuilder")
    private LocalizedErrorDescriptorBuilder localizedErrorDescriptorBuilder;

    @Override
    public Response toResponse(ParamException exception) {
        Throwable cause = exception.getCause();
        if(cause instanceof ExtractorException && cause.getCause() != null){
            // fix of JRS-13813
            cause = cause.getCause();
        }
        ErrorDescriptor descriptor = localizedErrorDescriptorBuilder.createDescriptor(
                LocalizedErrorDescriptorBuilder.BUNDLE_PREFIX + IllegalParameterValueException.ERROR_CODE,
                exception.getParameterName(),
                "",
                cause);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(descriptor)
                .build();

    }
}
