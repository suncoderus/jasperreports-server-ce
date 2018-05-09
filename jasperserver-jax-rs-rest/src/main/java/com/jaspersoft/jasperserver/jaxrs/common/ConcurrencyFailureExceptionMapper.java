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

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map a {@link ConcurrencyFailureException} exception to a {@link Response}.
 *
 * @author askorodumov
 * @version $Id$
 */
@Provider
@Component
public class ConcurrencyFailureExceptionMapper implements ExceptionMapper<ConcurrencyFailureException> {
    private static final String ERROR_CODE = "concurrent.modification";

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(ConcurrencyFailureException exception) {
        return Response
                .status(Response.Status.CONFLICT)
                .entity(new ErrorDescriptor()
                        .setErrorCode(ERROR_CODE)
                        .setMessage("Concurrent modification request error"))
                .build();
    }
}
