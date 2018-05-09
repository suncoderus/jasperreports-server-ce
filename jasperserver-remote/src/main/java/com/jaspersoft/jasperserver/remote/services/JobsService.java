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
package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.dto.job.ClientJobCalendar;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceAlreadyExistsException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Jobs service provide access to report scheduling functionality
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface JobsService {
    /**
     * Delete report job with given ID
     * @param id - report job ID to delete
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public void deleteJob(long id) throws RemoteException;

    /**
     * Delete set of report jobs with given IDs
     * @param ids - array of report job IDs to delete
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public void deleteJobs(long[] ids) throws RemoteException;

    /**
     * Read report job with given ID
     * @param id - report job ID to read
     * @return - report job
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public ReportJob getJob(long id) throws RemoteException;

    /**
     * Schedule (create) report job
     * @param reportJob - report job to schedule
     * @return created report job
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public ReportJob scheduleJob(ReportJob reportJob) throws RemoteException;

    /**
     * Update report job
     * @param reportJob - report job to update
     * @return updated report job
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public ReportJob updateJob(ReportJob reportJob) throws RemoteException;

    /**
     * Read summaries of all existing report jobs
     * @return List of report job summaries
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public List<ReportJobSummary> getAllJobs() throws RemoteException;

    /**
     * Read summaries of report jobs matching to given search criteria. Paging functionality is included.
     *
     * @param reportJobCriteria - example object
     * @param startIndex        - start index of block
     * @param numberOfRows      - block size
     * @param sortType          - column for sorting
     * @param isAscending       - sorting direction, if true - ascending
     * @return List of report job summaries
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public List<ReportJobSummary> getJobSummariesByExample(ReportJobModel reportJobCriteria, Integer startIndex, Integer numberOfRows,
                                                           ReportJobModel.ReportJobSortType sortType, Boolean isAscending) throws RemoteException;

    /**
     * Read summaries of report jobs for report with given URI
     *
     * @param reportURI - report URI to search
     * @return List of report job summaries for report with given URI
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public List<ReportJobSummary> getReportJobs(String reportURI) throws RemoteException;

    /**
     * Read current state of report job with given ID
     * @param jobId - report job ID to read
     * @return report job state (runtime information)
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public ReportJobRuntimeInformation getReportJobState(long jobId) throws RemoteException;

    /**
     * Updates collection of report job objects in one call.
     *
     * @param jobIds                   - list of report job ID to update
     * @param jobModel                 - contain fields, which should be updated.
     * @param replaceTriggerIgnoreType - if true, then trigger need to be replaced (trigger type is ignored), else - trigger is updated.
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     * @deprecated  Replaced by {@link JobsService#updateJobs(List, ReportJobModel, Boolean)}.
        */
    public void updateReportJobs(List<Long> jobIds, ReportJobModel jobModel, Boolean replaceTriggerIgnoreType) throws RemoteException;

    /**
     * Updates collection of report job objects in one call.
     *
     * @param jobIds                   - list of report job ID to update
     * @param jobModel                 - contain fields, which should be updated.
     * @param replaceTriggerIgnoreType - if true, then trigger need to be replaced (trigger type is ignored), else - trigger is updated.
     * @throws RemoteException - thrown if any internal error occurs (validation errors are included)
     */
    public List<Long> updateJobs(List<Long> jobIds, ReportJobModel jobModel, Boolean replaceTriggerIgnoreType) throws RemoteException;

    /**
     * Pause jobs.
     *
     * @param jobIds - list of report job ID to update. Null or empty list means all
     *
     * @deprecated Replaced by {@link JobsService#pauseJobs(List)}.
     */
    public void pause(List<Long> jobIds);

    /**
     * Pause jobs.
     *
     * @param jobIds - list of report job ID to update. Null or empty list means all
     */
    public List<Long> pauseJobs(List<Long> jobIds);

    /**

    /**
     * Resume paused jobs
     *
     * @param jobIds - list of report job ID to update. Null or empty list means all
     *
     * @deprecated Replaced by {@link JobsService#resumeJobs(List)}.
     */
    public void resume(List<Long> jobIds);

    /**
     * Resume paused jobs
     *
     * @param jobIds - list of report job ID to update. Null or empty list means all
     */
    List<Long> resumeJobs(List<Long> jobIds);

    /**
     * Take input List<Long> (list of report job ID) schedule each job as a cloned job to be
     * run once and immediately.  The run-once Job's Trigger will have its misfire instruction
     * set to MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY
     * No attempt is made to retry the run-once job in the event of failure during execution
     *
     * @param jobIds - list of report job ID
     */
    public void scheduleJobsOnceNow(List<Long> jobIds) throws ResourceNotFoundException;

    /**
     * Get the names of all registered Calendars.
     * @return names of all registered Calendars
     * @throws RemoteException if any error occurs
     */
    public List<String> getCalendarNames() throws RemoteException;

    /**
     * Get the names of registered Calendars of specific type.
     * @param type - the calendar type to filter
     * @return the list of calendar names
     * @throws RemoteException in case if not able to get calendar names.
     */
    List<String> getCalendarNames(ClientJobCalendar.Type type) throws RemoteException;

    /**
     * Delete the identified Calendar from the Scheduler.
     *
     * @param calendarName the name of the Calendar to delete
     */
    public void deleteCalendar(String calendarName);

    /**
     * Add (register) the given Calendar to the Scheduler.
     *
     * @param calendarName   - the name of the calendar to register
     * @param jobCalendar    - job calendar to register
     * @param replace        - If <code>true</code>, any <code>Calendar</code> existing in the <code>JobStore</code> with the same name should be over-written.
     * @param updateTriggers - whether or not to update existing triggers that referenced the already existing calendar so that they are 'correct' based on the new trigger.
     * @throws MandatoryParameterNotFoundException - if mandatory parameter isn't provided
     * @throws ResourceAlreadyExistsException      - if target calendar already exists
     * @throws IllegalParameterValueException      - if some field's value is illegal
     */
    public void addCalendar(String calendarName, ClientJobCalendar jobCalendar, Boolean replace, Boolean updateTriggers) throws MandatoryParameterNotFoundException, ResourceAlreadyExistsException, IllegalParameterValueException;

    /**
     * Get the calendar by the name
     *
     * @param calendarName - name of the calendar
     * @return registered calendar
     */
    public ClientJobCalendar getCalendar(String calendarName);
}
