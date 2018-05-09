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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.DuplicateOutputLocationException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.TriggerTypeMismatchException;

import java.util.Date;
import java.util.List;

/**
 * @author : Zakhar.Tomchenco
 */
public class ReportSchedulingServiceMock implements ReportSchedulingService {
    @Override
    public ReportJob scheduleJob(ExecutionContext context, ReportJob job) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJob> scheduleJobsOnceNow(ExecutionContext context, List<ReportJob> jobs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobIdHolder> scheduleJobsOnceNowById(ExecutionContext context, List<ReportJobIdHolder> jobs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateScheduledJob(ExecutionContext context, ReportJob job) throws ReportJobNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobIdHolder> updateScheduledJobsByID(ExecutionContext context, List<ReportJobIdHolder> reportJobHolders, ReportJobModel jobModel, boolean replaceTriggerIgnoreType) throws TriggerTypeMismatchException, ReportJobNotFoundException, DuplicateOutputLocationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJob> updateScheduledJobs(ExecutionContext context, List<ReportJob> reportJobs, ReportJobModel jobModel, boolean replaceTriggerIgnoreType) throws TriggerTypeMismatchException, DuplicateOutputLocationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List getScheduledJobs(ExecutionContext context, String reportUnitURI) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List getScheduledJobs(ExecutionContext context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context, String reportUnitURI) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context, ReportJobModel reportJobCriteria, int startIndex, int numberOfRows, ReportJobModel.ReportJobSortType sortType, boolean isAscending) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReportJobSummary> getJobsByNextFireTime(ExecutionContext context, List<ReportJob> searchList, Date startDate, Date endDate, List<Byte> includeTriggerStates) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ReportJobRuntimeInformation getJobRuntimeInformation(ExecutionContext context, long jobId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeScheduledJob(ExecutionContext context, long jobId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeScheduledJobs(ExecutionContext context, long[] jobIds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ReportJob getScheduledJob(ExecutionContext context, long jobId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ValidationErrors validateJob(ExecutionContext context, ReportJob job) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pause(List<ReportJob> jobs, boolean all) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resume(List<ReportJob> jobs, boolean all) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pauseById(ExecutionContext context, List<ReportJobIdHolder> jobs, boolean all) {
    }

    @Override
    public List<Long> pauseJobs(ExecutionContext context, List<ReportJobIdHolder> jobs, boolean all) {
        return null;
    }

    @Override
    public void resumeById(ExecutionContext context, List<ReportJobIdHolder> jobs, boolean all) {
    }

    @Override
    public List<Long> resumeJobs(ExecutionContext context, List<ReportJobIdHolder> jobs, boolean all) {
        return null;
    }
}
