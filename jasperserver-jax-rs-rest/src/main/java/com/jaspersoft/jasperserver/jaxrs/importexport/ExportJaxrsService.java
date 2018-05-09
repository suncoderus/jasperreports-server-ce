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
package com.jaspersoft.jasperserver.jaxrs.importexport;

import com.jaspersoft.jasperserver.dto.importexport.ExportTask;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.async.ExportRunnable;
import com.jaspersoft.jasperserver.remote.services.async.ImportExportTask;
import com.jaspersoft.jasperserver.remote.services.async.TasksManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Zakhar.Tomchenco
 */
@Component
@Path("/export")
@Scope("prototype")
public class ExportJaxrsService  {

    @Resource
    private ImportExportService synchImportExportService;

    @Resource
    private TasksManager basicTaskManager;

    @Resource(name="messageSource")
    private MessageSource messageSource;

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response createNewTask(final ExportTask data) throws RemoteException{

        Map<String, Boolean> params = new HashMap<String, Boolean>();
        if (data.getParameters() != null){
            for (String par : data.getParameters()) {
                params.put(par, true);
            }
        }

        ExportRunnable exportRunnable = new ExportRunnable(params, data.getUris(), data.getScheduledJobs(),
                data.getRoles(), data.getUsers(), data.getResourceTypes(), LocaleContextHolder.getLocale());
        exportRunnable.setService(synchImportExportService);
        exportRunnable.setMessageSource(messageSource);
        exportRunnable.setOrganizationId(data.getOrganization());
        basicTaskManager.startTask(new ImportExportTask<InputStream>(exportRunnable));

        return Response.ok(exportRunnable.getState()).build();
    }

    @GET
    @Path("/{id}/state")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStateOfTheTask(@PathParam("id") final String taskId) throws RemoteException{
        return Response.ok(basicTaskManager.getTaskState(taskId)).build();
    }

    @GET
    @Path("/{id}/{name}")
    @Produces("application/zip")
    public Response downloadFile(@PathParam("id") final String taskId, @PathParam("name") String name) throws RemoteException{
        Response response = Response.ok(basicTaskManager.getTask(taskId).getResult()).build();
        basicTaskManager.finishTask(taskId);
        return response;
    }

    @DELETE
    @Path("/{id}")
    public Response cancel(@PathParam("id")String id){
        basicTaskManager.finishTask(id);
        return Response.noContent().build();
    }


}
