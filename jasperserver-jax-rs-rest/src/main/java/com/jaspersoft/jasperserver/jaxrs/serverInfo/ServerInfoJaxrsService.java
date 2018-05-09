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

package com.jaspersoft.jasperserver.jaxrs.serverInfo;

import com.jaspersoft.jasperserver.remote.common.RemoteServiceWrapper;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.ServerInfoService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * JAX-RS service "server information" implementation.
 *
 * @author Volodya Sabadosh (vsabadosh@jaspersoft.com)
 * @version $Id$
 */
@Component
@Scope("prototype")
@Path("/serverInfo")
public class ServerInfoJaxrsService extends RemoteServiceWrapper<ServerInfoService> {

    @Resource(name = "concreteServerInfoService")
    public void setRemoteService(ServerInfoService remoteService) {
        this.remoteService = remoteService;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getServerInfo() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo()).build();
            }
        });
    }

    @GET
    @Path("/version")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerVersion() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getVersion()).build();
            }
        });
    }

    @GET
    @Path("/edition")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerEdition() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getEdition().toString()).build();
            }
        });
    }

    @GET
    @Path("/editionName")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerEditionName() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getEditionName()).build();
            }
        });
    }

    @GET
    @Path("/licenseType")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerLicenseType() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getLicenseType()).build();
            }
        });
    }

    @GET
    @Path("/build")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerBuild() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getBuild()).build();
            }
        });
    }

    @GET
    @Path("/expiration")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerExpiration() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getExpiration()).build();
            }
        });
    }

    @GET
    @Path("/features")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getFeatures() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getFeatures()).build();
            }
        });
    }

    @GET
    @Path("/dateFormatPattern")
    @Produces(MediaType.TEXT_PLAIN)
    public Response dateFormatPattern() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getDateFormatPattern()).build();
            }
        });
    }

    @GET
    @Path("/datetimeFormatPattern")
    @Produces(MediaType.TEXT_PLAIN)
    public Response datetimeFormatPattern() {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(ServerInfoService service) throws RemoteException {
                return Response.ok(service.getServerInfo().getDatetimeFormatPattern()).build();
            }
        });
    }
}
