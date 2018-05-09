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
package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.common.service.SecurityContextProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotService;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.logging.context.LoggingContextProvider;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

/**
 * Tests for {@link ReportExecutionJob}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class ReportExecutionJobTest extends UnitilsJUnit4 {

    private static final String REPORT_UNIT_URI = "/report";

    @TestedObject
    private ReportExecutionJob reportExecutionJob;

    private Mock<LoggingContextProvider> loggingContextProviderMock;
    private Mock<JobExecutionContext> jobExecutionContextMock;
    private Mock<Scheduler> schedulerMock;
    private Mock<SchedulerContext> schedulerContextMock;
    private Mock<RepositoryService> repositoryMock;
    private Mock<EngineService> engineServiceMock;
    private Mock<ReportUnit> reportUnitMock;
    private Mock<ApplicationContext> applicationContextMock;
    private Mock<Trigger> triggerMock;
    private Mock<JobDataMap> jobDataMapMock;
    private Mock<SecurityContextProvider> securityContextProviderMock;
    private Mock<ReportJobsPersistenceService> reportJobsPersistenceServiceMock;
    private Mock<ReportJob> reportJobMock;
    private Mock<ReportJobSource> reportJobSourceMock;
    private Mock<ReportJobRepositoryDestination> reportJobRepositoryDestinationMock;
    private Mock<DataSnapshotService> dataSnapshotServiceMock;

    @Test
    public void executeMethodShouldFlushLoggingContext() throws Exception {
        jobExecutionContextMock.returns(schedulerMock).getScheduler();
        jobExecutionContextMock.returns(triggerMock).getTrigger();
        schedulerMock.returns(schedulerContextMock).getContext();
        schedulerContextMock.returns(applicationContextMock).get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_APPLICATION_CONTEXT);
        schedulerContextMock.returns(securityContextProviderMock).get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_SECURITY_CONTEXT_PROVIDER);
        schedulerContextMock.returns(reportJobsPersistenceServiceMock).get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_JOB_PERSISTENCE_SERVICE);
        schedulerContextMock.returns(repositoryMock).get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_REPOSITORY);
        schedulerContextMock.returns(engineServiceMock).get(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_ENGINE_SERVICE);
        schedulerContextMock.returns("dataSnapshotService").getString(ReportExecutionJob.SCHEDULER_CONTEXT_KEY_DATA_SNAPSHOT_SERVICE_BEAN);
        applicationContextMock.returns(dataSnapshotServiceMock).getBean("dataSnapshotService", DataSnapshotService.class);
        triggerMock.returns(jobDataMapMock).getJobDataMap();
        jobDataMapMock.returns("joeuser").getString(ReportExecutionJob.JOB_DATA_KEY_USERNAME);
        reportJobsPersistenceServiceMock.returns(reportJobMock).loadJob(null, null);
        reportJobMock.returns(reportJobRepositoryDestinationMock).getContentRepositoryDestination();
        reportJobMock.returns(reportJobSourceMock).getSource();
        reportJobSourceMock.returns(REPORT_UNIT_URI).getReportUnitURI();
        repositoryMock.returns(reportUnitMock).getResource(null, REPORT_UNIT_URI, ReportUnit.class);
        dataSnapshotServiceMock.returns(false).isSnapshotPersistenceEnabled();

        ReportExecutionJob.setLoggingContextProvider(loggingContextProviderMock.getMock());

        reportExecutionJob.execute(jobExecutionContextMock.getMock());
        loggingContextProviderMock.assertInvoked().flushContext();
    }
}
