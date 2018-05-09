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

import com.jaspersoft.jasperserver.dto.importexport.ImportTask;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.async.ImportExportTask;
import com.jaspersoft.jasperserver.remote.services.async.ImportRunnable;
import com.jaspersoft.jasperserver.remote.services.async.Task;
import com.jaspersoft.jasperserver.remote.services.async.TasksManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zakhar.Tomchenco
 */

@Component
@Path("/import")
@Scope("prototype")
public class ImportJaxrsService {
    private static final Log log = LogFactory.getLog(ImportJaxrsService.class);

    @Resource
    private ImportExportService synchImportExportService;

    @Resource
    private TasksManager basicTaskManager;

    @Resource(name="messageSource")
    private MessageSource messageSource;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response handleMultipartUpload(
            @FormDataParam("file") InputStream stream,
            @DefaultValue("true")@FormDataParam("update")  Boolean update,
            @DefaultValue("true")@FormDataParam("skip-user-update") Boolean skipUserUpdate,
            @DefaultValue("true")@FormDataParam("include-audit-events") Boolean includeAuditEvents,
            @DefaultValue("true")@FormDataParam("include-access-events") Boolean includeAccessEvents,
            @DefaultValue("true")@FormDataParam("include-monitoring-events") Boolean includeMonitoringEvents,
            @DefaultValue("true")@FormDataParam("include-server-settings") Boolean includeSettings,
            @DefaultValue("true")@FormDataParam(ImportExportService.SKIP_THEMES) Boolean skipThemes,
            @FormDataParam(ImportExportServiceImpl.ORGANIZATION) String organizationId,
            @DefaultValue("fail")@FormDataParam(ImportExportService.BROKEN_DEPENDENCIES) String strategy,
            @DefaultValue("true")@FormDataParam(ImportExportService.MERGE_ORGANIZATION) Boolean mergeOrganization ) throws Exception {

        // In case of form submission from browser we for NULL if param is absent and FALSE if it set
        // (because browsers put value 'on' or empty string and jersey parses it to FALSE)
        // That is why @DefaultValue("true") is set and also we have to negate values
        return handleUpload(stream, !update, !skipUserUpdate, !includeAuditEvents, !includeAccessEvents,
                !includeMonitoringEvents, !includeSettings, !skipThemes, organizationId, !mergeOrganization, strategy);

    }

    @POST
    @Consumes("application/zip")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response handleDirectUpload(InputStream stream,
            @DefaultValue("false")@QueryParam("update") Boolean update,
            @DefaultValue("false")@QueryParam("skipUserUpdate") Boolean skipUserUpdate,
            @DefaultValue("false")@QueryParam("includeAuditEvents") Boolean includeAuditEvents,
            @DefaultValue("false")@QueryParam("includeAccessEvents") Boolean includeAccessEvents,
            @DefaultValue("false")@QueryParam("includeMonitoringEvents") Boolean includeMonitoringEvents,
            @DefaultValue("false")@QueryParam("includeServerSettings") Boolean includeSettings,
            @DefaultValue("false")@QueryParam("skipThemes") Boolean skipThemes,
            @QueryParam(ImportExportServiceImpl.ORGANIZATION) String organizationId,
            @DefaultValue("fail")@QueryParam("brokenDependencies") String strategy,
            @DefaultValue("false")@QueryParam("mergeOrganization") Boolean mergeOrganization) throws Exception {

        return handleUpload(stream, update, skipUserUpdate, includeAuditEvents, includeAccessEvents,
                includeMonitoringEvents, includeSettings, skipThemes, organizationId, mergeOrganization, strategy);
    }

    protected Response handleUpload(InputStream stream, Boolean update, Boolean skipUserUpdate,
                                    Boolean includeAuditEvents,Boolean includeAccessEvents,
                                    Boolean includeMonitoringEvents,Boolean includeSettings,
                                    Boolean skipThemes,
                                    String organizationId,
                                    Boolean mergeOrganization,
                                    String brokenDependenciesStrategy) throws Exception {
        Map<String, Boolean> params = new HashMap<String, Boolean>();
        params.put("update", update);
        params.put("skip-user-update", skipUserUpdate);
        params.put("include-audit-events", includeAuditEvents);
        params.put("include-access-events", includeAccessEvents);
        params.put("include-monitoring-events", includeMonitoringEvents);
        params.put("include-server-settings", includeSettings);
        params.put(ImportExportService.SKIP_THEMES, skipThemes);
        params.put(ImportExportService.MERGE_ORGANIZATION, mergeOrganization);

        if(log.isDebugEnabled()){
            log.debug(new StringBuffer()
                    .append("Request to start import is: \n")
                    .append(StringUtils.join(params.entrySet(), "\n"))
                    .toString()
            );
        }

        ImportRunnable importRunner = new ImportRunnable(params, stream, LocaleContextHolder.getLocale());
        importRunner.setService(synchImportExportService);
        importRunner.setMessageSource(messageSource);
        importRunner.setOrganizationId(organizationId);
        importRunner.setBrokenDependenciesStrategy(brokenDependenciesStrategy);

        basicTaskManager.startTask(new ImportExportTask<State>(importRunner));

        return Response.ok(importRunner.getState()).build();
    }


    @GET
    @Path("/{id}/state")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStateOfTheTask(@PathParam("id") final String taskId) throws RemoteException {
        State taskState = basicTaskManager.getTaskState(taskId);
        if (!taskState.getPhase().equals(Task.INPROGRESS) && !taskState.getPhase().equals(Task.PENDING)){
            basicTaskManager.finishTask(taskId);
        }
        return Response.ok(taskState).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getTasksIds() throws RemoteException {
        return Response.ok(basicTaskManager.getTaskIds()).build();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getParamsOfTheTask(@PathParam("id") final String taskId) throws RemoteException {
        Task task = basicTaskManager.getTask(taskId);

        ImportTask taskDto = new ImportTask();
        taskDto.setOrganization(task.getOrganizationId());
        taskDto.setBrokenDependencies(task.getBrokenDependenciesStrategy());

        Map parameters = task.getParameters();
        if (parameters == null) {
            taskDto.setParameters(null);
        } else {
            List<String> parametersList = new ArrayList<String>();
            for (Object key : parameters.keySet()) {
                Object value = parameters.get(key);
                if (key instanceof String) {
                    if (value instanceof Boolean) {
                        if ((Boolean) value) {
                            parametersList.add((String) key);
                        }
                    } else if (value instanceof String) {
                        if (Boolean.parseBoolean((String) value)) {
                            parametersList.add((String) key);
                        }
                    }
                }
            }
            taskDto.setParameters(parametersList.isEmpty() ? null : parametersList);
        }

        return Response.ok(taskDto).build();
    }

    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putTask(ImportTask taskDto, @PathParam("id") String taskId)  throws RemoteException {
        Task task = basicTaskManager.getTask(taskId);
        task.updateTask(taskDto.getParameters(), taskDto.getOrganization(), taskDto.getBrokenDependencies());
        basicTaskManager.restartTask(task);

        return Response.ok(task.getState()).build();
    }

    @DELETE
    @Path("/{id}")
    public Response cancel(@PathParam("id")String id){
        basicTaskManager.finishTask(id);

        return Response.noContent().build();
    }
}
