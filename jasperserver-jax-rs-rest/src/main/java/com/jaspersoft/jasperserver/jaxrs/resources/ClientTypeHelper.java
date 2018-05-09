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
package com.jaspersoft.jasperserver.jaxrs.resources;

import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;

import javax.ws.rs.core.MediaType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ClientTypeHelper {
    public static String extractClientType(MediaType mediaType){
        return mediaType == null ? null : extractClientType(mediaType.toString());
    }

    public static String extractClientType(String mediaType){
        String clientResourceType = null;
        Matcher matcher = Pattern.compile(ResourceMediaType.RESOURCE_MEDIA_TYPE_PREFIX + "([^+]+)").matcher(mediaType != null ? mediaType : "");
        if(matcher.find()){
            clientResourceType = matcher.group(1);
        }
        return clientResourceType;
    }
}
