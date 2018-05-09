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
package com.jaspersoft.jasperserver.jaxrs.job;

import com.jaspersoft.jasperserver.api.common.util.PaginationConstants;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSourceModel;
import com.jaspersoft.jasperserver.dto.job.ClientJobCalendar;
import com.jaspersoft.jasperserver.dto.job.ClientJobSummary;
import com.jaspersoft.jasperserver.dto.job.ClientReportJob;
import com.jaspersoft.jasperserver.dto.job.JobClientConstants;
import com.jaspersoft.jasperserver.dto.job.model.ClientReportJobModel;
import com.jaspersoft.jasperserver.dto.job.wrappers.ClientCalendarNameListWrapper;
import com.jaspersoft.jasperserver.dto.job.wrappers.ClientJobIdListWrapper;
import com.jaspersoft.jasperserver.dto.job.wrappers.ClientJobSummariesListWrapper;
import com.jaspersoft.jasperserver.remote.common.CallTemplate;
import com.jaspersoft.jasperserver.remote.common.RemoteServiceWrapper;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.JobsService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * JAX-RS service "jobs" implementation
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@Component
@Scope("prototype")
@Path("/jobs")
@CallTemplate(JobsServiceCallTemplate.class)
public class JobsJaxrsService extends RemoteServiceWrapper<JobsService> {
    protected static final Log log = LogFactory.getLog(JobsJaxrsService.class);

    @Resource
    private ReportJobConverter reportJobConverter;
    @Resource
    private ReportJobModelConverter reportJobModelConverter;
    @Resource
    private ReportJobSummaryConverter reportJobSummaryConverter;
    @Context
    private HttpHeaders httpHeaders;

    @Resource(name = "jobsService")
    public void setRemoteService(JobsService remoteService) {
        this.remoteService = remoteService;
    }

    @DELETE
    @Path("/{id: \\d+}")
    public Response deleteJob(@PathParam("id") final long id) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                service.deleteJob(id);
                return Response.ok("" + id).build();
            }
        });
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteJobs(@QueryParam("id") final List<Long> ids) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                long[] idsArray = new long[ids.size()];
                for (int i = 0; i < ids.size(); i++)
                    idsArray[i] = ids.get(i);
                service.deleteJobs(idsArray);
                return Response.ok(new JobIdListWrapper(ids)).build();
            }
        });
    }

    @GET
    @Path("/{id: \\d+}")
    // qs is specified to ensure, that application/xml is used if no Accept header specified in a request
    @Produces({"application/json;qs=.5", "application/xml;qs=1"})
    public Response getJob(@PathParam("id") final long id) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                return Response.ok(reportJobConverter.toClient(service.getJob(id), null)).build();
            }
        });
    }

    @GET
    @Path("/{id: \\d+}")
    @Produces(JobClientConstants.JOB_V_1_1_JSON_MEDIA_TYPE)
    public Response getJobWithProcessedParameters(@PathParam("id") final long id) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                return Response.ok(reportJobConverter.toClient(service.getJob(id), null)).build();
            }
        });
    }


    @PUT
    @Produces(JobClientConstants.JOB_V_1_1_JSON_MEDIA_TYPE)
    @Consumes(JobClientConstants.JOB_V_1_1_JSON_MEDIA_TYPE)
    public Response scheduleJobWithProcessedParameters(final ClientReportJob clientReportJob) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                return Response.ok(reportJobConverter.toClient(service.scheduleJob(reportJobConverter.toServer(clientReportJob, null)), null)).build();
            }
        });
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response scheduleJob(final ClientReportJob clientReportJob) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                return Response.ok(reportJobConverter.toClient(service.scheduleJob(reportJobConverter.toServer(clientReportJob, null)), null)).build();
            }
        });
    }

    @POST
    @Path("/{id: \\d+}")
    @Produces(JobClientConstants.JOB_V_1_1_JSON_MEDIA_TYPE)
    @Consumes(JobClientConstants.JOB_V_1_1_JSON_MEDIA_TYPE)
    public Response updateJobWithProcessedParameters(@PathParam("id") final long id, final ClientReportJob clientReportJob) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                if (clientReportJob.getId() == null || id != clientReportJob.getId())
                    clientReportJob.setId(id);

                return Response.ok(reportJobConverter.toClient(service.updateJob(reportJobConverter.toServer(clientReportJob, null)), null)).build();
            }
        });
    }

    @POST
    @Path("/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateJob(@PathParam("id") final long id, final ClientReportJob clientReportJob) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                if (clientReportJob.getId() == null || id != clientReportJob.getId())//null safe
                    clientReportJob.setId(id);
                return Response.ok(reportJobConverter.toClient(service.updateJob(reportJobConverter.toServer(clientReportJob, null)), null)).build();
            }
        });
    }

    /**
     * This method allows to update a collection of jobs in one call.
     *
     * @param jobIds                   - list of report job ID to update
     * @param clientReportJobModel                 - contain fields, which should be updated.
     * @param replaceTriggerIgnoreType - if true, then trigger need to be replaced (trigger type is ignored), else - trigger is updated.
     * @return empty response with status OK (code 200)
     */
    @POST
    @Produces(JobClientConstants.JOB_V_1_1_JSON_MEDIA_TYPE)
    @Consumes(JobClientConstants.JOB_V_1_1_JSON_MEDIA_TYPE)
    public Response updateJobsWithProcessedParameters(@QueryParam("id") final List<Long> jobIds, final ClientReportJobModel clientReportJobModel,
                                                      @QueryParam("replaceTriggerIgnoreType") @DefaultValue("false") final Boolean replaceTriggerIgnoreType) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                remoteService.updateJobs(jobIds, reportJobModelConverter.toServer(clientReportJobModel, null), replaceTriggerIgnoreType);
                return Response.ok(new ClientJobIdListWrapper(jobIds)).build();
            }
        });
    }

    /**
     * This method allows to update a collection of jobs in one call.
     *
     * @param jobIds                   - list of report job ID to update
     * @param clientReportJobModel                 - contain fields, which should be updated.
     * @param replaceTriggerIgnoreType - if true, then trigger need to be replaced (trigger type is ignored), else - trigger is updated.
     * @return empty response with status OK (code 200)
     */
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateJobs(@QueryParam("id") final List<Long> jobIds, final ClientReportJobModel clientReportJobModel,
                               @QueryParam("replaceTriggerIgnoreType") @DefaultValue("false") final Boolean replaceTriggerIgnoreType) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                List<Long> updatedJobIds = remoteService.updateJobs(jobIds, reportJobModelConverter.toServer(clientReportJobModel, null), replaceTriggerIgnoreType);
                return Response.ok(new ClientJobIdListWrapper(updatedJobIds)).build();
            }
        });
    }

    /**
     * This method is used to get list of report job summary objects by given search criteria.
     * Fields of summary objects can be specified as separate parameters, all the other report job object's fields can be specified in example parameter (JSON string).
     * If some of summary fields specified in corresponding parameter and inside of example parameter, then value from example parameter is used for search.
     *
     * @param reportURI        - URI of the target report
     * @param owner            - report job creator user's name
     * @param jobName          - name of the report job
     * @param state            - runtime state of the report (defined but not implemented in current release)
     * @param previousFireTime - previous fire time of the report job (defined but not implemented in current release)
     * @param nextFireTime     - next fire time of the report job (defined but not implemented in current release)
     * @param exampleConverter - ReportJobModel in JSON format wrapped by JSON unmarshaller
     * @param startIndex       - block start index (pagination)
     * @param numberOfRows     - number of rows in a block (pagination)
     * @param offset           - see {startIndex}
     * @param limit            - see {numberOfRows}
     * @param sortType         - sorting column, possible values: NONE, SORTBY_JOBID, SORTBY_JOBNAME, SORTBY_REPORTURI, SORTBY_REPORTNAME,
     *                         SORTBY_REPORTFOLDER, SORTBY_OWNER, SORTBY_STATUS, SORTBY_LASTRUN, SORTBY_NEXTRUN
     * @param isAscending      - sorting direction, ascending if true
     * @return list of report job summaries
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportJobs(
            @QueryParam("reportUnitURI") final String reportURI,
            @QueryParam("owner") final String owner,
            @QueryParam("label") final String jobName,
            @QueryParam("state") final String state,
            @QueryParam("previousFireTime") final Date previousFireTime,
            @QueryParam("nextFireTime") final Date nextFireTime,
            @QueryParam("example") final ReportJobModelJsonParam exampleConverter,
            @QueryParam(PaginationConstants.PARAM_LIMIT) final Integer limit,
            @QueryParam(PaginationConstants.PARAM_OFFSET) final Integer offset,
            @QueryParam("startIndex") final Integer startIndex,
            @QueryParam("numberOfRows") final Integer numberOfRows,
            @QueryParam("sortType") final ReportJobModel.ReportJobSortType sortType,
            @QueryParam("isAscending") final Boolean isAscending
    ) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                ReportJobModel criteriaObject = exampleConverter != null ? exampleConverter.getObject() : null;

                if (reportURI != null
                        || owner != null
                        || jobName != null
                        || state != null
                        || previousFireTime != null
                        || nextFireTime != null) {
                    if (criteriaObject == null) {
                        criteriaObject = new ReportJobModel();
                    }
                    if (reportURI != null && (criteriaObject.getSourceModel() == null
                            || criteriaObject.getSourceModel().getReportUnitURI() == null)) {
                        if (criteriaObject.getSourceModel() == null) {
                            criteriaObject.setSourceModel(new ReportJobSourceModel());
                        }
                        criteriaObject.getSourceModel().setReportUnitURI(reportURI);
                    }
                    if (owner != null && criteriaObject.getUsername() == null) {
                        criteriaObject.setUsername(owner);
                    }
                    if (jobName != null && criteriaObject.getLabel() == null) {
                        criteriaObject.setLabel(jobName);
                    }

                    if (state != null)              { /* TODO state criteria            */ }
                    if (previousFireTime != null)   { /* TODO previousFireTime criteria */ }
                    if (nextFireTime != null)       { /* TODO nextFireTime criteria     */ }
                }

                List<ReportJobSummary> result = service.getJobSummariesByExample(
                        criteriaObject,
                        offset == null ? startIndex : offset,
                        limit  == null ? numberOfRows : limit,
                        sortType,
                        isAscending);
                List<ClientJobSummary> clientResult = null;
                if (result != null && !result.isEmpty()) {
                    clientResult = new ArrayList<ClientJobSummary>(result.size());
                    for (ReportJobSummary reportJobSummary : result) {
                        clientResult.add(reportJobSummaryConverter.toClient(reportJobSummary, null));

                    }
                }
                return clientResult!= null && !clientResult.isEmpty()
                        ? Response.ok(new ClientJobSummariesListWrapper(clientResult)).build()
                        : Response.status(Response.Status.NO_CONTENT).build();
            }
        });
    }

    @GET
    @Path("/{id: \\d+}/state")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getJobState(@PathParam("id") final long id) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService service) throws RemoteException {
                ReportJobRuntimeInformation reportJobState = service.getReportJobState(id);
                if (reportJobState != null)
                    return Response.ok(reportJobSummaryConverter.toClientJobState(reportJobState)).build();
                else
                    return Response.status(Response.Status.NOT_FOUND).build();
            }
        });
    }

    /**
     * Pause currently scheduled jobs execution. Does not delete the jobs
     *
     * @param jobIdListWrapper - list of job ID to pause. Empty list means "pause all"
     * @return empty OK response
     */
    @POST
    @Path("/pause")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response pause(final ClientJobIdListWrapper jobIdListWrapper) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                List<Long> pausedIds = remoteService.pauseJobs(jobIdListWrapper.getIds());
                return Response.ok(new JobIdListWrapper(pausedIds)).build();
            }
        });
    }

    /**
     * Resume currently scheduled jobs execution.
     *
     * @param jobIdListWrapper - list of job ID to pause. Empty list means "resume all"
     * @return empty OK response
     */
    @POST
    @Path("/resume")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response resume(final ClientJobIdListWrapper jobIdListWrapper) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                List<Long> resumedJobs = remoteService.resumeJobs(jobIdListWrapper.getIds());
                return Response.ok(new JobIdListWrapper(resumedJobs)).build();
            }
        });
    }

    @POST
    @Path("/restart")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response scheduleJobsOnceNow(final ClientJobIdListWrapper jobIdListWrapper) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                remoteService.scheduleJobsOnceNow(jobIdListWrapper.getIds());
                return Response.ok(jobIdListWrapper).build();
            }
        });
    }

    @GET
    @Path("/calendars")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCalendarNames(final @QueryParam("calendarType") String calendarType) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                ClientJobCalendar.Type type = null;
                if (calendarType != null) {
                    try {
                        type = ClientJobCalendar.Type.valueOf(calendarType);
                    } catch (IllegalArgumentException e) {
                        // just log. Given calendar type is invalid. Let's return empty response.
                        log.error("Unable to find corresponding calendar type enum item for '" + calendarType
                                + "'.", e);
                        return Response.status(Response.Status.NO_CONTENT).build();
                    }
                }
                final List<String> calendarNames = remoteService.getCalendarNames(type);
                if (calendarNames != null && !calendarNames.isEmpty())
                    return Response.ok(new ClientCalendarNameListWrapper(calendarNames)).build();
                else
                    return Response.status(Response.Status.NO_CONTENT).build();
            }
        });
    }

    @GET
    @Path("/calendars/{calendarName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCalendarByName(@PathParam("calendarName") final String calendarName) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                final ClientJobCalendar calendar = remoteService.getCalendar(calendarName);
                if (calendar != null)
                    return Response.ok(calendar).build();
                else
                    return Response.status(Response.Status.NOT_FOUND).build();
            }
        });
    }

    @DELETE
    @Path("/calendars/{calendarName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCalendar(@PathParam("calendarName") final String calendarName) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                final ClientJobCalendar calendar = remoteService.getCalendar(calendarName);
                //Bug 42132
                if (calendar != null) {
                    remoteService.deleteCalendar(calendarName);
                    return Response.ok(calendarName).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }

            }
        });
    }

    @PUT
    @Path("/calendars/{calendarName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putCalendar(
            @PathParam("calendarName") final String calendarName,
            final ClientJobCalendar calendar,
            @QueryParam("replace") @DefaultValue("false") final Boolean replace,
            @QueryParam("updateTriggers") @DefaultValue("false") final Boolean updateTriggers) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(JobsService remoteService) throws RemoteException {
                remoteService.addCalendar(calendarName, calendar, replace, updateTriggers);
                return Response.ok(calendar).build();
            }
        });
    }
}
