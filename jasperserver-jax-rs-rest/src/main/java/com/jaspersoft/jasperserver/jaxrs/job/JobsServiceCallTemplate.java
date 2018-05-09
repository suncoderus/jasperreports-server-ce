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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.TriggerTypeMismatchException;
import com.jaspersoft.jasperserver.remote.common.RemoteServiceCallTemplate;
import com.jaspersoft.jasperserver.remote.common.RemoteServiceInTemplateCaller;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.services.JobsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.core.Response;


/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class JobsServiceCallTemplate implements RemoteServiceCallTemplate<JobsService> {
    private static final Log log = LogFactory.getLog(JobsServiceCallTemplate.class);

    // in case of JobsJaxrsService ResponseType is always of type javax.ws.rs.core.Response
    @SuppressWarnings("unchecked")
    public <ResponseType> ResponseType callRemoteService(RemoteServiceInTemplateCaller<ResponseType, JobsService> caller, JobsService service) {
        Response response = null;
        Exception exceptionToLog = null;
        javax.ws.rs.core.Response.Status status = Response.Status.BAD_REQUEST;
        Object entity = null;
        try {
            response = (Response) caller.call(service);
        } catch (ReportJobNotFoundException e) {
            status = Response.Status.NOT_FOUND;
            final long jobId = e.getJobId();
            entity = new ErrorDescriptor()
                    .setErrorCode(ResourceNotFoundException.ERROR_CODE_RESOURCE_NOT_FOUND)
                    .setMessage("Requested job " + jobId + " doesn't exist or has finished execution and now is removed")
                    .addParameters(jobId);
        } catch (AccessDeniedException e) {
            status = Response.Status.FORBIDDEN;
            entity = new ErrorDescriptor().setErrorCode("access.denied").setMessage(e.getMessage());
        } catch (JSValidationException e) {
            entity = e.getErrors();
        } catch (JSExceptionWrapper e) {
            throw e;
        } catch (JSException e) {
            entity = new ErrorDescriptor().setMessage(e.getMessage());

            if (e.getCause() instanceof TriggerTypeMismatchException) {
                TriggerTypeMismatchException cause = (TriggerTypeMismatchException) e.getCause();
                ((ErrorDescriptor)entity).addParameters(cause.getArgs()[0], cause.getArgs()[1], cause.getArgs()[2]);
                status = Response.Status.FORBIDDEN;
            }
        } catch (RemoteException e) {
            entity = e.getErrorDescriptor();
            if (e.isUnexpected()){
                exceptionToLog = e;
                status = Response.Status.INTERNAL_SERVER_ERROR;
            }
        } catch (Exception e) {
            ErrorDescriptor errorDescriptor = new ErrorDescriptor().setErrorCode(ErrorDescriptor.ERROR_CODE_UNEXPECTED_ERROR)
                    .setParameters(e.getClass().getName());
            if(e.getMessage() != null)
                errorDescriptor.setMessage(e.getMessage());
            entity = errorDescriptor;
            exceptionToLog = e;
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        if(exceptionToLog != null)
            log.error("Unexpected error occurs", exceptionToLog);
        return (ResponseType) (entity != null ? Response.status(status).entity(entity).build() : response);
    }
}
