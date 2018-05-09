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

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * @author serhii.blazhyievskyi
 * @version $Id$
 */
@Provider
public class ErrorDescriptorContentTypeResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (responseContext != null) {
            String mediaType = "xml";
            final String accept = requestContext.getHeaderString("accept");
            if(accept != null && !accept.isEmpty()) {
                if(accept.toLowerCase().indexOf("json") > -1) {
                    mediaType = "json";
                }
            }
            String contentType = null;
            final Object entity = responseContext.getEntity();
            if (entity instanceof ErrorDescriptor) {
                contentType = "application/errorDescriptor+";
            } else if (entity instanceof java.util.List
                    && ((List)entity).get(0) instanceof com.jaspersoft.jasperserver.dto.common.ErrorDescriptor) {
                if(((List)entity).size() == 1){
                    responseContext.setEntity(((List)entity).get(0));
                    contentType = "application/errorDescriptor+";
                } else {
                    contentType = "application/collection.errorDescriptor+";
                    if("xml".equalsIgnoreCase(mediaType)){
                        responseContext.setEntity(new GenericEntity<List<ErrorDescriptor>>((List)entity){});
                    }
                }
            }
            if(contentType != null){
                final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
                if(headers != null) {
                    headers.remove("Content-Type");
                    headers.add("Content-Type", contentType + mediaType);
                }
            }
        }
    }
}
