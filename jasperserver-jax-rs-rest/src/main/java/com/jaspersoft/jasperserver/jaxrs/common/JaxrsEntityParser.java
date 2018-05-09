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

import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JaxrsEntityParser {
    private final Providers providers;
    private final HttpHeaders httpHeaders;

    private JaxrsEntityParser(Providers providers, HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
        this.providers = providers;
    }

    public static JaxrsEntityParser newInstance(Providers providers, HttpHeaders httpHeaders) {
        if (providers == null || httpHeaders == null) {
            throw new NullPointerException();
        }
        return new JaxrsEntityParser(providers, httpHeaders);
    }

    public <T> T parseEntity(Class<T> clazz, InputStream entityStream, MediaType mediaType) throws IOException {
        return parseEntity(clazz, clazz, entityStream, mediaType);
    }

    public <T> T parseEntity(Class<T> clazz, Type genericType, InputStream entityStream, MediaType mediaType) throws IOException {
        // code below comes from com.sun.jersey.multipart.BodyPart#getEntityAs(Class<T> clazz)
        Annotation annotations[] = new Annotation[0];
        MessageBodyReader<T> reader =
                providers.getMessageBodyReader(clazz, genericType, annotations, mediaType);
        if (reader == null) {
            throw new IllegalArgumentException("No available MessageBodyReader for class " + clazz.getName()
                    + " and media type " + mediaType);
        }
        try {
            return reader.readFrom(clazz, clazz, annotations, mediaType, httpHeaders.getRequestHeaders(), entityStream);
        } catch (EOFException e) {
            throw new MandatoryParameterNotFoundException("body");
        }
    }
}
