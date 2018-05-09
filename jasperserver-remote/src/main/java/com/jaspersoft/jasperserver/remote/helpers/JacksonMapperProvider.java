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
package com.jaspersoft.jasperserver.remote.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.springframework.stereotype.Service;

/**
 * // TODO Andriy G: fix wrong usages of this provider in JacksonJsonMarshaller, JacksonMapperContextResolver, JacksonMapperProvider
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
public class JacksonMapperProvider extends JacksonJaxbJsonProvider {
    private static ObjectMapper mapper;

    public JacksonMapperProvider(){
        super(getObjectMapper(), DEFAULT_ANNOTATIONS);
    }

    public JacksonMapperProvider(Annotations... annotationsToUse) {
        super(getObjectMapper(), annotationsToUse);
    }

    public JacksonMapperProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
    }

    public static ObjectMapper getObjectMapper() {
        if (mapper == null) {
            synchronized (JacksonMapperProvider.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    AnnotationIntrospector primary = new JaxbAnnotationIntrospector();
                    AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
                    AnnotationIntrospector pair = AnnotationIntrospector.pair(primary, secondary);
                    mapper.setAnnotationIntrospector(pair);
                    // Serialize dates using ISO8601 format
                    // Jackson uses timestamps by default, so use StdDateFormat to get ISO8601
                    mapper.setDateFormat(new StdDateFormat());
                    // Prevent exceptions from being thrown for unknown properties
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    // Use XML wrapper name as JSON property name
                    mapper.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, true);
                    mapper.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);
                    // ignore fields with null values
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                }
            }
        }
        return mapper;
    }
}
