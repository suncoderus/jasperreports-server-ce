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
package com.jaspersoft.jasperserver.jaxrs.report;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportExecutionStatusInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.SchedulerReportExecutionStatusSearchCriteria;
import com.jaspersoft.jasperserver.dto.reports.ReportParameter;
import com.jaspersoft.jasperserver.dto.reports.ReportParameters;
import com.jaspersoft.jasperserver.remote.common.CallTemplate;
import com.jaspersoft.jasperserver.remote.common.RemoteServiceWrapper;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.ExportExecution;
import com.jaspersoft.jasperserver.remote.services.ExportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportExecution;
import com.jaspersoft.jasperserver.remote.services.ReportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportOutputPages;
import com.jaspersoft.jasperserver.remote.services.ReportOutputResource;
import com.jaspersoft.jasperserver.remote.services.RunReportService;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportExecutionsJaxrsService.java 26714 2012-12-12 10:18:22Z ykovalchyk $
 */
@Service
@Path("/reportExecutions")
@Scope("prototype")
@CallTemplate(ReportsServiceCallTemplate.class)
public class ReportExecutionsJaxrsService extends RemoteServiceWrapper<RunReportService> {
    @Resource(name = "runReportService")
    public void setRemoteService(RunReportService remoteService) {
        this.remoteService = remoteService;
    }

    @GET
    @Path("/{executionId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportExecution(@PathParam("executionId") final String executionId) {
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                return Response.ok(remoteService.getReportExecution(executionId)).build();
            }
        });
    }

    @DELETE
    @Path("/{executionId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteReportExecution(@PathParam("executionId") final String executionId) {
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                remoteService.deleteReportExecution(executionId);
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        });
    }

    @GET
    @Path("/{executionId}/exports/{exportId}/outputResource")
    public Response getOutputResource(@PathParam("executionId") final String executionId,
            @PathParam("exportId") final String exportId,
            @QueryParam("suppressContentDisposition") @DefaultValue("false") final Boolean suppressContentDisposition) {
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                ReportOutputResource reportOutputResource = remoteService.getOutputResource(executionId, exportId);
                return ReportExecutionHelper.buildResponseFromOutputResource(reportOutputResource, suppressContentDisposition);
            }
        });
    }

    @POST
    @Path("/{executionId}/parameters")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportInputParametersViaPost(@PathParam("executionId") final String executionId,
            @QueryParam("freshData") @DefaultValue("false") Boolean freshData, List<ReportParameter> parameterList) {
        final ReportExecution reportExecution = remoteService.getReportExecution(executionId);
        reportExecution.setRawParameters(new ReportParameters(parameterList).getRawParameters());
        remoteService.startReportExecution(reportExecution, new ReportExecutionOptions(reportExecution.getOptions()).setFreshData(freshData));
        return Response.noContent().build();
    }

    /**
     * Asynchronous exporting.
     *
     * @param executionId - report unit request ID
     * @return Response with ExportExecution instance as entity
     */
    @POST
    @Path("/{executionId}/exports")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response export(@PathParam("executionId") final String executionId,
            final ExportExecutionOptions exportOptions) {
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                return Response.ok(remoteService.executeExport(executionId, exportOptions)).build();
            }
        });
    }

    @GET
    @Path("/{executionId}/exports/{exportId}/attachments/{attachment}")
    public Response getAttachment(@PathParam("executionId") final String executionId,
            @PathParam("exportId") final String exportId, final @PathParam("attachment") String attachmentName) {
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                final ReportOutputResource attachment = remoteService.getAttachment(executionId, exportId, attachmentName);
                return ReportExecutionHelper.buildResponseFromOutputResource(attachment);
            }
        });
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportsRuntimeInformation(
            @QueryParam("reportURI") final String reportURI,
            @QueryParam("jobID") final String jobID,
            @QueryParam("jobLabel") final String jobLabel,
            @QueryParam("userName") final String userName,
            @QueryParam("fireTimeFrom") final String fireTimeFrom,
            @QueryParam("fireTimeTo") final String fireTimeTo) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(RunReportService remoteService) throws RemoteException {
                SchedulerReportExecutionStatusSearchCriteria criteria = null;
                if (StringUtils.isNotEmpty(reportURI)
                        || StringUtils.isNotEmpty(jobID)
                        || StringUtils.isNotEmpty(jobLabel)
                        || StringUtils.isNotEmpty(userName)
                        || StringUtils.isNotEmpty(fireTimeFrom)
                        || StringUtils.isNotEmpty(fireTimeTo)) {
                    criteria = new SchedulerReportExecutionStatusSearchCriteria();
                    criteria.setReportURI(StringUtils.isNotEmpty(reportURI) ? reportURI : null);
                    criteria.setJobLabel(StringUtils.isNotEmpty(jobLabel) ? jobLabel : null);
                    criteria.setUserName(StringUtils.isNotEmpty(userName) ? userName : null);
                    criteria.setJobID(StringUtils.isNotEmpty(jobID) ? Long.valueOf(jobID) : null);
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
                    try {
                        criteria.setFireTimeFrom(StringUtils.isNotEmpty(fireTimeFrom) ? simpleDateFormat.parse(fireTimeFrom) : null);
                    } catch (ParseException e) {
                        throw new IllegalParameterValueException("fireTimeFrom", fireTimeFrom);
                    }
                    try {
                        criteria.setFireTimeTo(StringUtils.isNotEmpty(fireTimeTo) ? simpleDateFormat.parse(fireTimeTo) : null);
                    } catch (ParseException e) {
                        throw new IllegalParameterValueException("fireTimeTo", fireTimeTo);
                    }
                }
                Set<ReportExecutionStatusInformation> currentlyRunningReports = remoteService.getCurrentlyRunningReports(criteria);
                if (currentlyRunningReports != null && !currentlyRunningReports.isEmpty())
                    return Response.ok(new ReportExecutionsSetWrapper(currentlyRunningReports)).build();
                else
                    return Response.status(Response.Status.NO_CONTENT).build();
            }
        });
    }

    @GET
    @Path("/{executionId}/exports/{exportId}/status")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getExportExecutionStatus(@PathParam("executionId") final String executionId,
            @PathParam("exportId") final String exportId){
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                final String status = remoteService.getExportExecution(executionId, exportId).getStatus().toString();
                ReportExecutionStatusEntity statusEntity = new ReportExecutionStatusEntity();
                statusEntity.setValue(status);
                return Response.ok(statusEntity).build();
            }
        });
    }

    @GET
    @Path("/{executionId}/exports/{exportId}/status")
    @Produces({"application/status+json", "application/status+xml"})
    public Response getExportExecutionStatusObject(@PathParam("executionId") final String executionId,
            @PathParam("exportId") final String exportId){
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                final ExportExecution exportExecution = remoteService.getExportExecution(executionId, exportId);
                return Response.ok( new ReportExecutionStatusObject()
                                .setValue(exportExecution.getStatus())
                                .setErrorDescriptor(exportExecution.getErrorDescriptor())
                ).build();
            }
        });
    }

    @GET
    @Path("/{executionId}/status")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportExecutionStatus(@PathParam("executionId") final String executionId){
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                final String status = remoteService.getReportExecution(executionId).getStatus().toString();
                ReportExecutionStatusEntity statusEntity = new ReportExecutionStatusEntity();
                statusEntity.setValue(status);
                return Response.ok(statusEntity).build();
            }
        });
    }

    @GET
    @Path("/{executionId}/status")
    @Produces({"application/status+json", "application/status+xml"})
    public Response getReportExecutionStatusObject(@PathParam("executionId") final String executionId){
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(RunReportService remoteService) throws RemoteException {
                final ReportExecution reportExecution = remoteService.getReportExecution(executionId);
                return Response.ok( new ReportExecutionStatusObject()
                                .setValue(reportExecution.getStatus())
                                .setErrorDescriptor(reportExecution.getErrorDescriptor())
                ).build();
            }
        });
    }

    @PUT
    @Path("/{executionId}/status")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response cancelReportExecution(@PathParam("executionId") final String executionId, ReportExecutionStatusEntity statusEntity) {
        Response response;
        if (statusEntity != null && ReportExecutionStatusEntity.VALUE_CANCELLED.equals(statusEntity.getValue()))
            response = callRemoteService(new ConcreteCaller<Response>() {
                public Response call(RunReportService remoteService) throws RemoteException {
                    final Boolean cancellationResult = remoteService.cancelReportExecution(executionId);
                    return cancellationResult ? Response.ok(new ReportExecutionStatusEntity()).build()
                            : Response.status(Response.Status.NO_CONTENT).build();
                }
            });
        else
            response = Response.status(Response.Status.BAD_REQUEST).build();
        return response;
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportOutputMetadata(final ReportExecutionRequest reportExecutionRequest,
            @Context final HttpServletRequest request) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(RunReportService remoteService) throws RemoteException {
                final ReportExecutionOptions reportExecutionOptions = new ReportExecutionOptions()
                        .setIgnorePagination(reportExecutionRequest.getIgnorePagination())
                        .setTransformerKey(reportExecutionRequest.getTransformerKey())
                        .setFreshData(reportExecutionRequest.getFreshData())
                        .setSaveDataSnapshot(reportExecutionRequest.getSaveDataSnapshot())
                        .setInteractive(reportExecutionRequest.getInteractive())
                        .setAsync(reportExecutionRequest.getAsync())
                        .setDefaultAttachmentsPrefixTemplate(ReportExecutionHelper
                                .getDefaultAttachmentsPrefixTemplateFromRequest(request.getRequestURI().replace(request.getContextPath(), "") + "/"))
                        .setContextPath(request.getContextPath());
                final ExportExecutionOptions exportOptions = new ExportExecutionOptions().setOutputFormat(reportExecutionRequest.getOutputFormat())
                        .setPages(ReportOutputPages.valueOf(reportExecutionRequest.getPages()))
                        .setMarkupType(reportExecutionRequest.getMarkupType())
                        .setBaseUrl(reportExecutionRequest.getBaseUrl())
                        .setAttachmentsPrefix(reportExecutionRequest.getAttachmentsPrefix())
                        .setAllowInlineScripts(reportExecutionRequest.isAllowInlineScripts())
                        .setAnchor(reportExecutionRequest.getAnchor());
                return Response.ok(remoteService.getReportExecutionFromRawParameters(reportExecutionRequest.getReportUnitUri(),
                        reportExecutionRequest.getParameters() != null ?
                                reportExecutionRequest.getParameters().getRawParameters() :
                                new HashMap<String, String[]>(), reportExecutionOptions, exportOptions)).build();
            }
        });
    }
}
