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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.lang.reflect.ParameterizedType;

/**
 * This converter can be used in JAX-RS services for automatic conversion of JSON string parameters to objects.
 * Don't use this class as raw type!
 *
 * @param <T> - target object type, required. Don't use this class as raw type!
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JsonParam<T> {

    private T object;

    public JsonParam(String json) {
        try {
            if (!(this.getClass().getGenericSuperclass() instanceof ParameterizedType))
                throw new IllegalAccessException(JsonParam.class.getName() + " must be parametrized");
            Class<?> targetClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

            ObjectMapper mapper = new ObjectMapper();
            mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector());

            object = (T) mapper.readValue(json, targetClass);
        } catch (Exception e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Couldn't parse JSON string: " + e.getMessage())
                    .build());
        }
    }


    public T getObject() {
        return object;
    }
}
