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

package com.jaspersoft.jasperserver.api.engine.scheduling;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.engine.BaseUnitTest;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.*;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import org.junit.Test;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Taras Matyashovsky
 */
public class ReportSchedulingFacadeTest extends BaseUnitTest {

    @TestedObject
    private ReportSchedulingFacade reportSchedulingFacade;

    @InjectInto(property = "persistenceService")
    private Mock<ReportJobsPersistenceService> persistenceService;

    @InjectInto(property = "jobsInternalService")
    private Mock<ReportJobsInternalService> jobsInternalService;

    @InjectInto(property = "scheduler")
    private Mock<ReportJobsScheduler> scheduler;

    @InjectInto(property = "validator")
    private Mock<ReportJobValidator> validator;

    @InjectInto(property = "auditContext")
    private Mock<AuditContext> auditContext;

    @InjectInto(property = "outputKeyMapping")
    private Mock<Map> outputKeyMapping;

    @Test
    public void getScheduledJobs() {
        ExecutionContext executionContext = getExecutionContext();

        /* Creating stub for output jobs. */
        List<ReportJobSummary> jobs = new ArrayList<ReportJobSummary>();
        jobs.add(new ReportJobSummary());

        /* Persistence service should once return jobs. */
        persistenceService.onceReturns(jobs).listJobs(executionContext);

        /* Creating stub for output report job runtime information. */
        ReportJobRuntimeInformation[] reportJobRuntimeInformation = new ReportJobRuntimeInformation[1];
        reportJobRuntimeInformation[0] = new ReportJobRuntimeInformation();

        /* Scheduler should once return information about runtime jobs. */
        scheduler.onceReturns(reportJobRuntimeInformation).getJobsRuntimeInformation(executionContext, new long[1]);

        /* Target method invocation. */
        List scheduledJobs = reportSchedulingFacade.getScheduledJobs(executionContext);

        /* Assertions. */
        assertNotNull("Scheduled jobs should not be null", scheduledJobs);
        assertEquals(1, scheduledJobs.size());

        /* Persistence service have been invoked from tested object with request to list jobs. */
        persistenceService.assertInvoked().listJobs(executionContext);

        /* Scheduler have been invoked from tested object with request to retrieve job runtime information. */
        scheduler.assertInvoked().getJobsRuntimeInformation(executionContext, new long[1]);
    }

    @Test
    public void removeScheduledJobs() {
        ExecutionContext executionContext = getExecutionContext();

        /* Identifiers of jobs to delete. */
        long[] jobIds = new long[1];
        jobIds[0] = 1;

        /* Stub for audit context. */
        auditContext.oncePerforms(null).doInAuditContext(null);

        /* Persistence service should once return job for deletion. */
        persistenceService.onceReturns(new ReportJob()).loadJob(executionContext, new ReportJobIdHolder(jobIds[0]));

        /* Stub for audit context. */
        auditContext.oncePerforms(null).doInAuditContext("deleteReportScheduling", null);

        /* Scheduler should once remove scheduled job. */
        scheduler.oncePerforms(null).removeScheduledJob(executionContext, jobIds[0]);

        /* Persistence service should once delete job. */
        persistenceService.oncePerforms(null).deleteJob(executionContext, new ReportJobIdHolder(jobIds[0]));

        /* Stub for audit context. */
        auditContext.oncePerforms(null).doInAuditContext("deleteReportScheduling", null);

        /* Target method invocation. */
        reportSchedulingFacade.removeScheduledJobs(executionContext, jobIds);

        /* Audit context have been invoked from tested object. */
        auditContext.assertInvoked().doInAuditContext(null);
        auditContext.assertInvoked().doInAuditContext("deleteReportScheduling", null);
        auditContext.assertInvoked().doInAuditContext("deleteReportScheduling", null);

        /* Persistence service have been invoked from tested object with request to load job. */
        persistenceService.assertInvoked().loadJob(executionContext, new ReportJobIdHolder(jobIds[0]));

        /* Persistence service have been invoked from tested object with request to delete job. */
        persistenceService.assertInvoked().deleteJob(executionContext, new ReportJobIdHolder(jobIds[0]));

        /* Scheduler have been invoked from tested object with request to remove scheduled job. */
        scheduler.assertInvoked().removeScheduledJob(executionContext, jobIds[0]);
    }

    @Test
    public void reportJobFinalized() {
        long jobId = 1L;

        /* Job internal service should once delete job. */
        jobsInternalService.oncePerforms(null).deleteJob(jobId);

        /* Target method invocation. */
        reportSchedulingFacade.reportJobFinalized(jobId);

        /* Job internal service have been invoked from tested object with request to delete job. */
        jobsInternalService.assertInvoked().deleteJob(jobId);
    }

    @Test
    public void updateScheduledJobSuccessWithRescheduling() {
        ExecutionContext executionContext = getExecutionContext();

        /* Dummy input report job. */
        ReportJob reportJob = new ReportJob();
        reportJob.setTrigger(new ReportJobSimpleTrigger());
        reportJob.getTrigger().setId(1L);

        ValidationErrors validationErrors = new ValidationErrorsImpl();
        /* Validator should once validate a job. */
        validator.onceReturns(validationErrors).validateJob(executionContext, reportJob);

        /* Stub for audit context. */
        auditContext.oncePerforms(null).doInAuditContext("updateReportScheduling", null);

        /* Dummy input report job. */
        ReportJob updatedJob = new ReportJob();
        updatedJob.setTrigger(new ReportJobSimpleTrigger());
        updatedJob.getTrigger().setId(2L);

        /* Persistence service should once update job. */
        persistenceService.onceReturns(updatedJob).updateJob(executionContext, reportJob);

        /* Trigger's ids do not match. Job should be rescheduled. */
        scheduler.oncePerforms(null).rescheduleJob(executionContext, reportJob);
        
        /* Target method invocation. */
        reportSchedulingFacade.updateScheduledJob(executionContext, reportJob);

        /* Audit context have been invoked from tested object. */
        auditContext.assertInvoked().doInAuditContext("updateReportScheduling", null);

        /* Validator have been invoked from tested object with request to validate job. */
        validator.assertInvoked().validateJob(executionContext, reportJob);

        /* Persistence service have been invoked from tested object with request to update job. */
        persistenceService.assertInvoked().updateJob(executionContext, reportJob);

        /* Scheduler have been invoked from tested object with request to reschedule job. */
        scheduler.assertInvoked().rescheduleJob(executionContext, updatedJob);
    }

    @Test
    public void updateScheduledJobSuccessWithoutRescheduling() {
        ExecutionContext executionContext = getExecutionContext();

        /* Dummy input report job. */
        ReportJob reportJob = new ReportJob();
        reportJob.setTrigger(new ReportJobSimpleTrigger());

        ValidationErrors validationErrors = new ValidationErrorsImpl();
        /* Validator should once validate a job. */
        validator.onceReturns(validationErrors).validateJob(executionContext, reportJob);

        /* Stub for audit context. */
        auditContext.oncePerforms(null).doInAuditContext("updateReportScheduling", null);

        /* Dummy output report job. */
        ReportJob updatedJob = new ReportJob();
        updatedJob.setTrigger(new ReportJobSimpleTrigger());
        
        /* Persistence service should once update job. */
        persistenceService.onceReturns(updatedJob).updateJob(executionContext, reportJob);

        /* Target method invocation. */
        reportSchedulingFacade.updateScheduledJob(executionContext, reportJob);

        /* Audit context have been invoked from tested object. */
        auditContext.assertInvoked().doInAuditContext("updateReportScheduling", null);

        /* Validator have been invoked from tested object with request to validate job. */
        validator.assertInvoked().validateJob(executionContext, reportJob);

        /* Persistence service have been invoked from tested object with request to update job. */
        persistenceService.assertInvoked().updateJob(executionContext, reportJob);

        /* Scheduler have not been invoked from tested object with request to reschedule job. */
        scheduler.assertNotInvoked().rescheduleJob(executionContext, updatedJob);
    }

    @Test
    public void updateScheduledJobFailure() {
        ExecutionContext executionContext = getExecutionContext();

        /* Dummy input report job. */
        ReportJob reportJob = new ReportJob();

        ValidationErrors validationErrors = new ValidationErrorsImpl();
        validationErrors.add(new ValidationErrorImpl("code", null, "message"));

        /* Validator should once validate a job with errors. */
        validator.onceReturns(validationErrors).validateJob(executionContext, reportJob);

        try {
            /* Target method invocation. Should fail. */
            reportSchedulingFacade.updateScheduledJob(executionContext, reportJob);
            fail();
        } catch (JSValidationException e) {
            /* Validator have been invoked from tested object with request to validate job. */
            validator.assertInvoked().validateJob(executionContext, reportJob);

            /* Persistence service have not been invoked from tested object with request to update job. */
            persistenceService.assertNotInvoked().updateJob(executionContext, reportJob);
        }
    }

    @Test
    public void updateReportUnitURI() {
        /* Job internal service should once update report URI. */
        jobsInternalService.onceReturns(new long[1]).updateReportUnitURI("/oldURI", "/newURI");

        /* Target method invocation. */
        reportSchedulingFacade.updateReportUnitURI("/oldURI", "/newURI");

        /* Job internal service have been invoked from tested object with request to update report URI. */
        jobsInternalService.assertInvoked().updateReportUnitURI("/oldURI", "/newURI");
    }

}
