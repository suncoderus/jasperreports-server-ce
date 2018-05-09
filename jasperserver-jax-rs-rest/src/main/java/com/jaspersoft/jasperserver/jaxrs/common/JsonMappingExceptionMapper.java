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

import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Provider
@Component
public class JsonMappingExceptionMapper extends AbstractSerializationExceptionMapper
        implements ExceptionMapper<JsonMappingException> {

    @Override
    public Response toResponse(JsonMappingException exception) {
        JsonMappingException e = new JsonMappingException(exception.getOriginalMessage(), exception);

        String path = buildPath(exception);
        return buildResponse( buildErrorDescriptor(e).addParameters(path, exception.getCause()));
    }

    private String buildPath(JsonMappingException exception) {
        if (exception.getPath() == null) return "";

        List<String> listOfPath = new ArrayList<String>();
        for (JsonMappingException.Reference ref : exception.getPath()) {
            listOfPath.add(ref.getFieldName());
        }
        return StringUtils.join(listOfPath, ".");
    }
}
