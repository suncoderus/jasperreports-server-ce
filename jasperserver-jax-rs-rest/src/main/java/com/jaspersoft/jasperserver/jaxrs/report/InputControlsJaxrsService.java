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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.dto.reports.ReportParameters;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControlsListWrapper;
import com.jaspersoft.jasperserver.remote.common.CallTemplate;
import com.jaspersoft.jasperserver.remote.common.RemoteServiceWrapper;
import com.jaspersoft.jasperserver.remote.exception.ModificationNotAllowedException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.war.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.war.cascade.InputControlsLogicService;
import com.jaspersoft.jasperserver.war.cascade.InputControlsValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: InputControlsJaxrsService.java 26672 2012-12-11 13:47:44Z ztomchenco $
 */
@Service
@Path("/reports/{reportUnitURI: .+}/inputControls")
@CallTemplate(ReportsServiceCallTemplate.class)
@Scope("prototype")
public class InputControlsJaxrsService extends RemoteServiceWrapper<InputControlsLogicService> {
    public static String EXCLUDE_PARAMETER = "exclude";
    public static String STATE_VALUE = "state";

    @Resource(name = "inputControlsLogicService")
    public void setRemoteService(InputControlsLogicService remoteService) {
        this.remoteService = remoteService;
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportInputParameters(@PathParam("reportUnitURI") final String reportUnitURI, @Context final HttpServletRequest request) {
        // parameters map can be safely cast to Map<String, String[]>
        @SuppressWarnings("unchecked")
        final Map<String, String[]> rawParameters = request.getParameterMap();
        return internalGetReportInputParameters(reportUnitURI, null, rawParameters);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportInputParametersViaPost(@PathParam("reportUnitURI") String reportUnitURI, Map<String, String[]> jsonParameters) {
        return internalGetReportInputParameters(reportUnitURI, null, jsonParameters);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getReportInputParametersViaPost(@PathParam("reportUnitURI") String reportUnitURI, ReportParameters parameters) {
        return internalGetReportInputParameters(reportUnitURI, null, parameters.getRawParameters());
    }

    protected Response internalGetReportInputParameters(final String reportUnitUri, final Set<String> inputControlIds, final Map<String, String[]> rawParameters) {
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(InputControlsLogicService remoteService) throws RemoteException {
                List<ReportInputControl> inputControlsForReport;
                try {
                    if (rawParameters.containsKey(EXCLUDE_PARAMETER) && Arrays.asList(rawParameters.get(EXCLUDE_PARAMETER)).contains(STATE_VALUE)){
                        inputControlsForReport = remoteService.getInputControlsStructure(Folder.SEPARATOR + reportUnitUri, inputControlIds);
                    } else {
                        inputControlsForReport = remoteService.getInputControlsWithValues(Folder.SEPARATOR + reportUnitUri, inputControlIds, rawParameters);
                    }
                } catch (CascadeResourceNotFoundException e) {
                    throw new ResourceNotFoundException("URI:" + e.getResourceUri() + " Type:" + e.getResourceType());
                }
                if (inputControlsForReport != null && !inputControlsForReport.isEmpty())
                    return Response.ok(new ReportInputControlsListWrapper(inputControlsForReport)).build();
                else
                    return Response.status(Response.Status.NO_CONTENT).build();
            }
        });
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response reorderInputControls(@PathParam("reportUnitURI") final String reportUnitURI, final ReportInputControlsListWrapper toUpdateWrapper) {
        return callRemoteService(new ConcreteCaller<Response>() {
            @Override
            public Response call(InputControlsLogicService remoteService) throws RemoteException {
                final List<ReportInputControl> toUpdate = toUpdateWrapper.getInputParameters();
                List<String> newList = new ArrayList<String>(toUpdate.size());
                for (ReportInputControl control : toUpdate) {
                    newList.add(control.getId());
                }
                final List<ReportInputControl> updated;
                try {
                    updated = remoteService.reorderInputControls("/" + reportUnitURI, newList);
                } catch (CascadeResourceNotFoundException e) {
                    throw new ResourceNotFoundException("URI:" + e.getResourceUri() + " Type:" + e.getResourceType());
                } catch (AccessDeniedException spe) {
                    throw new ModificationNotAllowedException("");
                } catch (InputControlsValidationException e) {
                    throw new ModificationNotAllowedException(new ErrorDescriptor()
                            .setMessage("Malformed data")
                            .addParameters(e.getErrors()));
                }
                return Response.ok(new ReportInputControlsListWrapper(updated)).build();
            }
        });
    }

    @GET
    @Path("/{inputControlIds: [^;/]+(;[^;/]+)*}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportInputParametersForSpecifiedInputControls(
            @PathParam("reportUnitURI") String reportUnitURI,
            @PathParam("inputControlIds") PathSegment inputControlIds,
            @Context HttpServletRequest request) {
        // parameters map can be safely cast to Map<String, String[]>
        @SuppressWarnings("unchecked")
        final Map<String, String[]> parameterMap = request.getParameterMap();
        return internalGetReportInputParameters(reportUnitURI, getInputControlIdsFromPathSegment(inputControlIds), parameterMap);
    }

    @POST
    @Path("/{inputControlIds: [^;/]+(;[^;/]+)*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportInputParametersForSpecifiedInputControlsViaPost(@PathParam("reportUnitURI") String reportUnitURI, @PathParam("inputControlIds") PathSegment inputControlIds, Map<String, String[]> jsonParameters) {
        return internalGetReportInputParameters(reportUnitURI, getInputControlIdsFromPathSegment(inputControlIds), jsonParameters);
    }

    @POST
    @Path("/{inputControlIds: [^;/]+(;[^;/]+)*}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getReportInputParametersForSpecifiedInputControlsViaPost(@PathParam("reportUnitURI") String reportUnitURI, @PathParam("inputControlIds") PathSegment inputControlIds, ReportParameters parameters) {
        return internalGetReportInputParameters(reportUnitURI, getInputControlIdsFromPathSegment(inputControlIds), parameters.getRawParameters());
    }

    @GET
    @Path("/values")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getInputControlsInitialValues(@PathParam("reportUnitURI") final String reportUnitURI,
            @Context final HttpServletRequest request, @QueryParam("freshData") @DefaultValue("false") Boolean freshData) {
        // parameters map can be safely cast to Map<String, String[]>
        @SuppressWarnings("unchecked")
        final Map<String, String[]> parameterMap = request.getParameterMap();
        return internalGetInputControlsInitialValues(reportUnitURI, parameterMap, freshData);
    }

    @POST
    @Path("/values")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInputControlsInitialValuesViaPost(@PathParam("reportUnitURI") String reportUnitURI,
                                                         Map<String, String[]> jsonParameters, @QueryParam("freshData") @DefaultValue("false") Boolean freshData) {
        return internalGetInputControlsInitialValues(reportUnitURI, jsonParameters, freshData);
    }

    @POST
    @Path("/values")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getInputControlsInitialValuesViaPost(@PathParam("reportUnitURI") String reportUnitURI,
            ReportParameters parameters, @QueryParam("freshData") @DefaultValue("false") Boolean freshData) {
        return internalGetInputControlsInitialValues(reportUnitURI, parameters.getRawParameters(), freshData);
    }

    protected Response internalGetInputControlsInitialValues(final String reportUnitURI, final Map<String, String[]> parameters, final boolean freshData) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(InputControlsLogicService remoteService) throws RemoteException {
                final String completeInputControlUri = Folder.SEPARATOR + reportUnitURI;
                List<InputControlState> values;
                try {
                    values = remoteService.getValuesForInputControls(completeInputControlUri, null, parameters, freshData);
                } catch (CascadeResourceNotFoundException e) {
                    throw new ResourceNotFoundException("URI:" + e.getResourceUri() + " Type:" + e.getResourceType());
                }
                if (values != null && !values.isEmpty())
                    return Response.ok(new InputControlStateListWrapper(values)).build();
                else
                    return Response.status(Response.Status.NO_CONTENT).entity("No input controls values found for the report /" + reportUnitURI).build();
            }
        });
    }

    @GET
    @Path("/{inputControlIds: [^;/]+(;[^;/]+)*}/values")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getReportInputControlValues(
            @PathParam("reportUnitURI") final String reportUnitUri,
            @PathParam("inputControlIds") final PathSegment inputControlIds,
            @Context final HttpServletRequest request,
            @QueryParam("freshData") @DefaultValue("false") Boolean freshData) {
        // parameters map can be safely cast to Map<String, String[]>
        @SuppressWarnings("unchecked")
        final Map<String, String[]> parameterMap = request.getParameterMap();
        return internalGetInputControlValues(Folder.SEPARATOR + reportUnitUri, inputControlIds, parameterMap, freshData);
    }

    @POST
    @Path("/{inputControlIds: [^;/]+(;[^;/]+)*}/values")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportInputControlValuesViaPost(
            @PathParam("reportUnitURI") final String reportUnitUri,
            @PathParam("inputControlIds") final PathSegment inputControlIds,
            Map<String, String[]> jsonParameters,
            @QueryParam("freshData") @DefaultValue("false") Boolean freshData) {
        return internalGetInputControlValues(Folder.SEPARATOR + reportUnitUri, inputControlIds,
                jsonParameters, freshData);
    }

    @POST
    @Path("/{inputControlIds: [^;/]+(;[^;/]+)*}/values")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getReportInputControlValuesViaPost(
            @PathParam("reportUnitURI") final String reportUnitUri,
            @PathParam("inputControlIds") final PathSegment inputControlIds,
            ReportParameters parameters,
            @QueryParam("freshData") @DefaultValue("false") Boolean freshData) {
        return internalGetInputControlValues(Folder.SEPARATOR + reportUnitUri, inputControlIds,
                parameters.getRawParameters(), freshData);
    }

    protected Response internalGetInputControlValues(
            final String reportUnitUri,
            final PathSegment inputControlIdsSegment,
            final Map<String, String[]> parameters,
            final boolean freshData) {
        return callRemoteService(new ConcreteCaller<Response>() {
            public Response call(InputControlsLogicService remoteService) throws RemoteException {
                List<InputControlState> values;
                try {
                    values = remoteService.getValuesForInputControls(reportUnitUri, getInputControlIdsFromPathSegment(inputControlIdsSegment), parameters, freshData);
                } catch (CascadeResourceNotFoundException e) {
                    throw new ResourceNotFoundException("URI:" + e.getResourceUri() + " Type:" + e.getResourceType());
                }
                if (values != null && !values.isEmpty())
                    return Response.ok(new InputControlStateListWrapper(values)).build();
                else
                    return Response.status(Response.Status.NO_CONTENT).entity("No input controls values found for the report /" + reportUnitUri).build();
            }
        });
    }

    protected Set<String> getInputControlIdsFromPathSegment(PathSegment inputControlIdsSegment) {
        Set<String> inputControlIds = new HashSet<String>();
        inputControlIds.add(inputControlIdsSegment.getPath());
        for (String currentId : inputControlIdsSegment.getMatrixParameters().keySet())
            inputControlIds.add(currentId);
        return inputControlIds;
    }
}
