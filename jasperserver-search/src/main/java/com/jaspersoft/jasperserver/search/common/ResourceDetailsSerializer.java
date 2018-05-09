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
package com.jaspersoft.jasperserver.search.common;

import com.jaspersoft.jasperserver.search.util.JSONConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author schubar
 */
public class ResourceDetailsSerializer extends JsonSerializer<ResourceDetails> {

    @Override
    public void serialize(ResourceDetails value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        String desc = value.getDescription();

        jgen.writeStartObject();

        jgen.writeFieldName(JSONConverter.RESOURCE_NAME);
        jgen.writeString(value.getName());
        jgen.writeFieldName(JSONConverter.RESOURCE_LABEL);
        jgen.writeString(value.getLabel());
        jgen.writeFieldName(JSONConverter.RESOURCE_DESC);
        jgen.writeString((desc != null) ? desc.replace("\\n", "<br>") : "");
        jgen.writeFieldName(JSONConverter.RESOURCE_URI);
        jgen.writeString(value.getURI());
        jgen.writeFieldName(JSONConverter.RESOURCE_URI_STRING);
        jgen.writeString(value.getURIString());

        jgen.writeFieldName(JSONConverter.RESOURCE_PARENT_URI);
        jgen.writeString(value.getParentURI());
        jgen.writeFieldName(JSONConverter.RESOURCE_PARENT_FOLDER);
        jgen.writeString(value.getParentFolder());

        jgen.writeFieldName(JSONConverter.RESOURCE_RESOURCE_TYPE);
        jgen.writeString(value.getResourceType());

        jgen.writeFieldName(JSONConverter.RESOURCE_PERMISSIONS);
        jgen.writeString(JSONConverter.getPermissionsMask(value));

        jgen.writeEndObject();
    }
}
