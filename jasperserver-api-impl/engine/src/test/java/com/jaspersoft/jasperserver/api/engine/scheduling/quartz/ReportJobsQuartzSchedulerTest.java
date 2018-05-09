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

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import org.junit.Before;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.unitils.UnitilsJUnit4;
import org.junit.Test;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.core.MockObject;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link ReportJobsQuartzScheduler}
 *
 * @author vsabadosh
 */
public class ReportJobsQuartzSchedulerTest extends UnitilsJUnit4 {

    @TestedObject
    private ReportJobsQuartzScheduler reportJobsQuartzScheduler;

    @InjectInto(property = "scheduler")
    private Mock<Scheduler> scheduler;
    private Mock<OperableTrigger> triggerMock;
    private Mock<JobDataMap> jobDataMapMock;

    private Mock<ReportUnit> reportUnit1;
    private Mock<ReportUnit> reportUnit2;
    private Mock<ReportUnit> reportUnit3;
    private Mock<ReportUnit> reportUnit4;

    private Mock<ReportExecutionJob> reportExecutionJobMock1;
    private Mock<ReportExecutionJob> reportExecutionJobMock2;
    private Mock<ReportExecutionJob> reportExecutionJobMock3;
    private Mock<ReportExecutionJob> reportExecutionJobMock4;

    
    private Mock<JobExecutionContext> jobExecutionContext1;
    private Mock<JobExecutionContext> jobExecutionContext2;
    private Mock<JobExecutionContext> jobExecutionContext3;
    private Mock<JobExecutionContext> jobExecutionContext4;
    
    private static final String GROUP = "ReportJobs";
    private static final String reportURI1 = "/public/diagnostic/DiagnosticReport_1"; 
    private static final String reportURI2 = "/public/diagnostic/DiagnosticReport_2"; 
    private static final String reportURI3 = "/public/diagnostic/DiagnosticReport_3"; 
    private static final String reportURI4 = "/public/diagnostic/DiagnosticReport_4";
    
    private static final String jobKeyName1 = "jobKey1"; 
    private static final String jobKeyName2 = "jobKey2"; 
    private static final String jobKeyName3 = "jobKey3"; 
    private static final String jobKeyName4 = "jobKey4"; 
    
    private JobKey jobKey1 = new JobKey(jobKeyName1, GROUP);
    private JobKey jobKey2 = new JobKey(jobKeyName2, GROUP);
    private JobKey jobKey3 = new JobKey(jobKeyName3);
    private JobKey jobKey4 = new JobKey(jobKeyName4);

    @Before
    public void setUp() throws SchedulerException {
        Set<JobKey> jobKeys = new HashSet<JobKey>();
        jobKeys.add(jobKey1);
        jobKeys.add(jobKey2);
        jobKeys.add(jobKey3);
        jobKeys.add(jobKey4);
        scheduler.returns(jobKeys).getJobKeys(null);

        JobDetailImpl jobDetail1 = new JobDetailImpl();
        jobDetail1.setKey(jobKey1);
        JobDetailImpl jobDetail2 = new JobDetailImpl();
        jobDetail2.setKey(jobKey2);
        JobDetailImpl jobDetail3 = new JobDetailImpl();
        jobDetail3.setKey(jobKey3);
        JobDetailImpl jobDetail4 = new JobDetailImpl();
        jobDetail4.setKey(jobKey4);

        scheduler.returns(jobDetail1).getJobDetail(jobKey1);
        scheduler.returns(jobDetail2).getJobDetail(jobKey2);
        scheduler.returns(jobDetail3).getJobDetail(jobKey3);
        scheduler.returns(jobDetail4).getJobDetail(jobKey4);

        triggerMock.returns(jobDataMapMock).getJobDataMap();
        
        List<JobExecutionContext> jobExecutionContexts = new ArrayList<JobExecutionContext>();

        reportUnit1.returns(reportURI1).getURI();
        reportUnit2.returns(reportURI2).getURI();
        reportUnit3.returns(reportURI3).getURI();
        reportUnit4.returns(reportURI4).getURI();
        
        reportExecutionJobMock1.getMock().reportUnit = reportUnit1.getMock();
        reportExecutionJobMock2.getMock().reportUnit = reportUnit2.getMock();
        reportExecutionJobMock3.getMock().reportUnit = reportUnit3.getMock();
        reportExecutionJobMock4.getMock().reportUnit = reportUnit4.getMock();

        jobExecutionContext1.returns(jobDetail1).getJobDetail();
        jobExecutionContext2.returns(jobDetail2).getJobDetail();
        jobExecutionContext3.returns(jobDetail3).getJobDetail();
        jobExecutionContext4.returns(jobDetail4).getJobDetail();

        jobExecutionContext1.returns(new Date()).getFireTime();
        jobExecutionContext2.returns(new Date()).getFireTime();
        jobExecutionContext3.returns(new Date()).getFireTime();
        jobExecutionContext4.returns(new Date()).getFireTime();

        jobExecutionContext1.returns(reportExecutionJobMock1).getJobInstance();
        jobExecutionContext2.returns(reportExecutionJobMock2).getJobInstance();
        jobExecutionContext3.returns(reportExecutionJobMock3).getJobInstance();
        jobExecutionContext4.returns(reportExecutionJobMock4).getJobInstance();

        jobExecutionContexts.add(jobExecutionContext1.getMock());
        jobExecutionContexts.add(jobExecutionContext2.getMock());
        jobExecutionContexts.add(jobExecutionContext3.getMock());
        jobExecutionContexts.add(jobExecutionContext4.getMock());

        scheduler.returns(jobExecutionContexts).getCurrentlyExecutingJobs();
    }

    @Test
    public void getDiagnosticDataTest() throws SchedulerException {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = reportJobsQuartzScheduler.getDiagnosticData();

        //Testing total size of diagnostic attributes collected from SessionRegistryDiagnosticService
        assertEquals(3, resultDiagnosticData.size());

        //Test collecting of TOTAL_SCHEDULED_JOBS
        int totalScheduledJobs = (Integer)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.
                TOTAL_SCHEDULED_JOBS, null, null)).getDiagnosticAttributeValue();
        assertEquals(4, totalScheduledJobs);

        scheduler.assertInvoked().getJobKeys(null);
        scheduler.assertInvoked().getJobDetail(jobKey1);
        scheduler.assertInvoked().getJobDetail(jobKey2);
        scheduler.assertInvoked().getJobDetail(jobKey3);
        scheduler.assertInvoked().getJobDetail(jobKey4);

        //Test collecting of TOTAL_RUNNING_JOBS
        int totalRunningJobs = (Integer)resultDiagnosticData.get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.
                TOTAL_RUNNING_JOBS, null, null)).getDiagnosticAttributeValue();
        assertEquals(2, totalRunningJobs);

        //Test collecting of RUNNING_JOBS_LIST
        Map<String, Map<String, Long>> runnningJobList = (Map<String, Map<String, Long>>)resultDiagnosticData.
                get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.RUNNING_JOBS_LIST, null, null)).
                getDiagnosticAttributeValue();

        assertEquals(2, runnningJobList.size());
        
        Map<String, Long> reportUrisWithTimeExecutions1 = runnningJobList.get(jobKeyName1);
        Map<String, Long> reportUrisWithTimeExecutions2 = runnningJobList.get(jobKeyName2);

        assertTrue(reportUrisWithTimeExecutions1.get(reportURI1) != null);
        assertTrue(reportUrisWithTimeExecutions2.get(reportURI2) != null);
    }

}
