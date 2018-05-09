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
package com.jaspersoft.jasperserver.jaxrs.connection;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.jaxrs.common.JaxrsEntityParser;
import com.jaspersoft.jasperserver.remote.connection.ContextsManager;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.UnsupportedOperationRemoteException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Service
// this service should be available through multiple path: rest_v2/connections and rest_v2/contexts
@Path("/{path:connections|contexts}")
@Scope("prototype")
public class ConnectionsJaxrsService {
    private static final Pattern EXTRACT_CONNECTION_TYPE_PATTERN = Pattern.compile("application/([^\\.]+)\\.([^(\\.|\\+)]+)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern CONNECTION_METADATA_PATTERN = Pattern.compile("application/([^\\.]+)(\\.[^.]+)?\\.metadata\\+.+",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern EXTRACT_CLIENT_TYPE_PATTERN = Pattern.compile("application/(.+)\\+json",
            Pattern.CASE_INSENSITIVE);
    @Context
    private Providers providers;
    @Context
    private HttpHeaders httpHeaders;
    @Resource
    private ContextsManager contextsManager;

    @POST
    public Response createConnection(InputStream stream, @HeaderParam(HttpHeaders.CONTENT_TYPE) MediaType mediaType,
            @HeaderParam(HttpHeaders.ACCEPT) MediaType accept, @Context HttpServletRequest request) throws URISyntaxException, IllegalParameterValueException, IOException {
        final Class<?> connectionClass = getConnectionClass(mediaType);
        final String acceptString = accept != null ? accept.toString() : "";
        final boolean isMetadataRequested = CONNECTION_METADATA_PATTERN.matcher(acceptString).matches();
        final Object connectionDescription = parseEntity(connectionClass, stream, mediaType);
        if(connectionDescription == null){
            throw new MandatoryParameterNotFoundException("body");
        }
        final Response errorResponse = Response.status(Response.Status.NOT_ACCEPTABLE).build();
        final WebApplicationException notAcceptable = new WebApplicationException(errorResponse);
        if(isMetadataRequested){
            final Matcher matcher = EXTRACT_CLIENT_TYPE_PATTERN.matcher(acceptString);
            String metadataClientType = null;
            if(matcher.find()){
                metadataClientType = matcher.group(1);
            }
            if(metadataClientType == null
                    || !contextsManager.isMetadataSupported(connectionDescription, metadataClientType)) {
                throw notAcceptable;
            }
        }
        final UUID connectionId = contextsManager.createConnection(connectionDescription);
        final StringBuffer locationBuffer = request.getRequestURL().append("/").append(connectionId.toString());
        if (isMetadataRequested) {
            final Object connectionMetadata;
            try {
                connectionMetadata = contextsManager.getConnectionMetadata(connectionId, request.getParameterMap());
            } catch (UnsupportedOperationRemoteException e){
                throw notAcceptable;
            }
            if (connectionMetadata == null) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            final String metadataClientResourceType = contextsManager
                    .getMetadataClientResourceType(connectionDescription);
            return Response.ok(connectionMetadata, clientTypeToMimeType(metadataClientResourceType))
                    .location(new URI(locationBuffer.append("/metadata").toString())).build();
        } else {
            return Response.created(new URI(locationBuffer.toString()))
                    .entity(connectionDescription).build();
        }
    }

    protected String clientTypeToMimeType(String clientType){
        return "application/" + clientType + "+json";
    }

    protected Object parseEntity(Class<?> connectionClass, InputStream stream, MediaType mediaType) throws IOException {
        return JaxrsEntityParser.newInstance(providers, httpHeaders).parseEntity(connectionClass, stream, mediaType);
    }

    @GET
    @Path("/{uuid}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getConnectionDetails(@PathParam("uuid") UUID uuid) throws ResourceNotFoundException {
        final Object connection = contextsManager.getConnection(uuid);
        return connection != null ? Response.ok(connection).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{uuid}")
    public Response removeConnectionDetails(@PathParam("uuid") UUID uuid) throws ResourceNotFoundException {
        try {
            contextsManager.removeConnection(uuid);
        } catch (ResourceNotFoundException e){
            // no such context. Do nothing. It's expected state after this operation completion.
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/{uuid}/metadata")
    public Response getConnectionMetadata(@PathParam("uuid") UUID uuid, @Context HttpServletRequest request) {
        final Object connectionMetadata = contextsManager.getConnectionMetadata(uuid, request.getParameterMap());
        if (connectionMetadata == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        final String metadataClientResourceType = contextsManager
                .getMetadataClientResourceType(contextsManager.getConnection(uuid));
        return Response.ok(connectionMetadata, clientTypeToMimeType(metadataClientResourceType)).build();
    }

    @POST
    @Path("/{uuid}/metadata")
    public Response getConnectionMetadata(@PathParam("uuid") UUID uuid, InputStream stream,
            @HeaderParam(HttpHeaders.CONTENT_TYPE) MediaType mediaType) throws IOException {
        Class<?> paramsClass = contextsManager.getMetadataParamsClass(getType(mediaType));
        if (paramsClass == null) {
            throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
        }
        final Object params = parseEntity(paramsClass, stream, mediaType);
        final Object connectionMetadata = contextsManager.getConnectionMetadata(uuid, params);
        return  connectionMetadata != null ? Response.ok(connectionMetadata).build() : Response.noContent().build();
    }

    @POST
    @Path("/{uuid}/data")
    public Response executeQuery(@PathParam("uuid") UUID uuid, InputStream stream,
            @HeaderParam(HttpHeaders.CONTENT_TYPE) MediaType mediaType,
            @HeaderParam(HttpHeaders.ACCEPT) MediaType accept, @Context HttpServletRequest request) throws IOException {
        final Class<?> queryClass = getQueryClass(mediaType);
        final Object query = parseEntity(queryClass, stream, mediaType);
        final boolean isMetadataRequested = accept != null && accept.toString().contains("metadata");
        return Response.ok(isMetadataRequested ? contextsManager.executeQueryForMetadata(uuid, query)
                : contextsManager.executeQuery(uuid, query, request.getParameterMap())).build();
    }

    protected String getType(MediaType mediaType){
        if(mediaType ==  null){
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MandatoryParameterNotFoundException("Content-Type").getErrorDescriptor()).build());
        }
        Matcher matcher = EXTRACT_CONNECTION_TYPE_PATTERN.matcher(mediaType.toString());
        return matcher.find() ? matcher.group(2) : null;
    }

    protected Class<?> getConnectionClass(MediaType mediaType) {
        Class<?> connectionClass = contextsManager.getConnectionDescriptionClass(getType(mediaType));
        if (connectionClass == null) {
            throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
        }
        return connectionClass;
    }

    protected Class<?> getQueryClass(MediaType mediaType){
        Class<?> queryClass = String.class;
        final String type = getType(mediaType);
        if(type != null) {
            queryClass = contextsManager.getQueryClass(type);
            if (queryClass == null) {
                throw new WebApplicationException(Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                        .entity(new ErrorDescriptor()
                                .setErrorCode("mediatype.not.supported")
                                .setMessage(mediaType.getSubtype() + " is not supported")).build());
            }
        }
        return queryClass;
    }
}
