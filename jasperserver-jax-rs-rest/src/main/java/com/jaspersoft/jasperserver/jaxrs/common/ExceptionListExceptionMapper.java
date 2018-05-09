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

import com.jaspersoft.jasperserver.api.ExceptionListWrapper;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Component
@Provider
public class ExceptionListExceptionMapper implements ExceptionMapper<ExceptionListWrapper> {
    @Resource
    private JSExceptionWrapperMapper exceptionWrapperMapper;
    @Override
    public Response toResponse(ExceptionListWrapper exceptionListWrapper) {
        List<ErrorDescriptor> errorDescriptors = new ArrayList<ErrorDescriptor>();
        for (Exception exception : exceptionListWrapper.getExceptions()) {
            errorDescriptors.add((ErrorDescriptor) exceptionWrapperMapper.toResponse(new JSExceptionWrapper(exception))
                    .getEntity());
        }
        return Response.status(Response.Status.BAD_REQUEST).
                entity(errorDescriptors.size() == 1 ? errorDescriptors.get(0) :
                        new GenericEntity<List<ErrorDescriptor>>(errorDescriptors){}).build();
    }
}
