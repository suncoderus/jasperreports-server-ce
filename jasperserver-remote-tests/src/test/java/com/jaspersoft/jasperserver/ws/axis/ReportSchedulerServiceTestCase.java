/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.ws.axis;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.rpc.ServiceException;

import junit.framework.TestCase;

import com.jaspersoft.jasperserver.war.JasperServerConstants;
import com.jaspersoft.jasperserver.ws.scheduling.CalendarDaysType;
import com.jaspersoft.jasperserver.ws.scheduling.IntervalUnit;
import com.jaspersoft.jasperserver.ws.scheduling.Job;
import com.jaspersoft.jasperserver.ws.scheduling.JobCalendarTrigger;
import com.jaspersoft.jasperserver.ws.scheduling.JobMailNotification;
import com.jaspersoft.jasperserver.ws.scheduling.JobParameter;
import com.jaspersoft.jasperserver.ws.scheduling.JobRepositoryDestination;
import com.jaspersoft.jasperserver.ws.scheduling.JobSimpleTrigger;
import com.jaspersoft.jasperserver.ws.scheduling.JobSummary;
import com.jaspersoft.jasperserver.ws.scheduling.ReportSchedulerFacade;
import com.jaspersoft.jasperserver.ws.scheduling.ResultSendType;
import com.jaspersoft.jasperserver.ws.scheduling.RuntimeJobState;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportSchedulerServiceTestCase extends TestCase {
	
	private String endPointUrl = JasperServerConstants.instance().WS_SCHEDULING_END_POINT_URL;
	private Calendar future;
	private Calendar futureEnd;
	private Date today;
	private ReportSchedulerFacade service;

	private long job1Id;
	private long job2Id;
	private long job3Id;
	
    public ReportSchedulerServiceTestCase(java.lang.String name) {
        super(name);
    }

	public void setUp() throws MalformedURLException, ServiceException {
		service = new ReportSchedulerFacade(new URL(endPointUrl), 
				JasperServerConstants.instance().USERNAME,
				JasperServerConstants.instance().PASSWORD
				);
		
		future = Calendar.getInstance();
		future.set(Calendar.MILLISECOND, 0);
		future.set(Calendar.SECOND, 0);
		future.add(Calendar.YEAR, 1);
		
		futureEnd = (Calendar) future.clone();
		futureEnd.add(Calendar.MONTH, 3);
		
		Calendar todayCal = Calendar.getInstance();
		todayCal.set(Calendar.MILLISECOND, 0);
		todayCal.set(Calendar.SECOND, 0);
		todayCal.set(Calendar.MINUTE, 0);
		todayCal.set(Calendar.HOUR, 0);
		today = todayCal.getTime();
	}

    public void testService() throws Exception {
    	createJob1();
    	getJob1();
    	getReportJobs1();
    	deleteJob1();
    	
    	createJob2();
    	getJob2();
    	updateJob2();
    	
    	createJob3();
    	getJob3();
    	
    	getAllJobs23();
    	deleteJobs23();
    }

	protected void createJob1() throws RemoteException {
		Job job = new Job();
    	job.setReportUnitURI("/reports/samples/AllAccounts");
    	job.setLabel("Label 1");
    	job.setDescription("Description 1");
    	
    	JobSimpleTrigger trigger = new JobSimpleTrigger();
		trigger.setStartDate(future);
    	trigger.setOccurrenceCount(3);
    	trigger.setRecurrenceInterval(new Integer(2));
    	trigger.setRecurrenceIntervalUnit(IntervalUnit.HOUR);
    	job.setSimpleTrigger(trigger);
    	
    	job.setBaseOutputFilename("Accounts1");
    	job.setOutputFormats(new String[]{"PDF", "HTML"});
    	
    	JobRepositoryDestination repoDest = new JobRepositoryDestination();
    	repoDest.setFolderURI("/ContentFiles");
    	repoDest.setOutputDescription("Report output");
    	repoDest.setSequentialFilenames(true);
    	repoDest.setTimestampPattern("yyyyMMdd");
    	job.setRepositoryDestination(repoDest);
    	
    	JobMailNotification notification = new JobMailNotification();
    	notification.setSubject("Mail");
    	notification.setMessageText("Reports");
    	notification.setResultSendType(ResultSendType.SEND_ATTACHMENT);
    	notification.setSkipEmptyReports(true);
    	notification.setToAddresses(new String[]{"joe@company.com", "smith@company.com"});
    	job.setMailNotification(notification);
    	
    	Job savedJob = service.scheduleJob(job);
    	assertTrue(savedJob.getId() > 0);
    	assertEquals(0, savedJob.getVersion());
    	
    	job1Id = savedJob.getId();
	}

	protected void getJob1() throws RemoteException {
		Job job = service.getJob(job1Id);
		assertNotNull(job);
		assertEquals(job1Id, job.getId());
		assertEquals(0, job.getVersion());
		assertEquals("/reports/samples/AllAccounts", job.getReportUnitURI());
		assertEquals("Label 1", job.getLabel());
		assertEquals("Description 1", job.getDescription());
		
		assertNull(job.getCalendarTrigger());
		JobSimpleTrigger trigger = job.getSimpleTrigger();
		assertNotNull(trigger);
		assertNotNull(trigger.getStartDate());
		assertEquals(future.getTimeInMillis(), trigger.getStartDate().getTimeInMillis());
		assertNull(trigger.getEndDate());
		assertEquals(3, trigger.getOccurrenceCount());
		assertNotNull(trigger.getRecurrenceInterval());
		assertEquals(2, trigger.getRecurrenceInterval().intValue());
		assertEquals(IntervalUnit.HOUR, trigger.getRecurrenceIntervalUnit());
		
		assertEquals("Accounts1", job.getBaseOutputFilename());
		String[] formats = job.getOutputFormats();
		assertNotNull(formats);
		assertEquals(2, formats.length);
		assertIn("PDF", formats);
		assertIn("HTML", formats);
    	
		JobRepositoryDestination repoDest = job.getRepositoryDestination();
		assertNotNull(repoDest);
		assertEquals("/ContentFiles", repoDest.getFolderURI());
		assertEquals("Report output", repoDest.getOutputDescription());
		assertTrue(repoDest.isSequentialFilenames());
		assertEquals("yyyyMMdd", repoDest.getTimestampPattern());
		assertFalse(repoDest.isOverwriteFiles());
		
		JobMailNotification notification = job.getMailNotification();
		assertNotNull(notification);
		assertEquals("Mail", notification.getSubject());
		assertEquals("Reports", notification.getMessageText());
		assertEquals(ResultSendType.SEND_ATTACHMENT, notification.getResultSendType());
		assertTrue(notification.isSkipEmptyReports());
		String[] toAddresses = notification.getToAddresses();
		assertNotNull(toAddresses);
		assertEquals(2, toAddresses.length);
		assertEquals("joe@company.com", toAddresses[0]);
		assertEquals("smith@company.com", toAddresses[1]);
	}

	protected static void assertIn(String value, String[] values) {
		assertNotNull(values);
		
		boolean found = false;
		for (int i = 0; i < values.length; i++) {
			if (values[i].equalsIgnoreCase(value)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	protected void getReportJobs1() throws RemoteException {
		JobSummary[] jobs = service.getReportJobs("/reports/samples/AllAccounts");
		assertNotNull(jobs);
		assertTrue(jobs.length > 0);
		
		JobSummary job = findJob(jobs, job1Id);
		assertNotNull(job);
		
		assertEquals(0, job.getVersion());
		assertEquals("/reports/samples/AllAccounts", job.getReportUnitURI());
		assertEquals("Label 1", job.getLabel());
		assertEquals(RuntimeJobState.NORMAL, job.getState());
		assertNull(job.getPreviousFireTime());
		assertNotNull(job.getNextFireTime());
		assertEquals(future.getTimeInMillis(), job.getNextFireTime().getTimeInMillis());
	}

	protected void deleteJob1() throws RemoteException {
		service.deleteJob(job1Id);
		
		JobSummary[] jobs = service.getReportJobs("/reports/samples/AllAccounts");
		if (jobs != null && jobs.length > 0) {
			for (int i = 0; i < jobs.length; i++) {
				assertFalse(jobs[i].getId() == job1Id);
			}
		}
	}

	protected void createJob2() throws RemoteException {
		Job job = new Job();
    	job.setReportUnitURI("/reports/samples/AllAccounts");
    	job.setLabel("Label 2");
    	job.setDescription("Description 2");

    	JobCalendarTrigger trigger = new JobCalendarTrigger();
    	trigger.setTimezone("Europe/Berlin");
    	trigger.setStartDate(future);
    	trigger.setEndDate(futureEnd);
    	trigger.setMinutes("0");
    	trigger.setHours("23");
    	trigger.setDaysType(CalendarDaysType.WEEK);
    	trigger.setWeekDays(new int[]{2, 5});
    	trigger.setMonths(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
    	job.setCalendarTrigger(trigger);
    	
    	job.setBaseOutputFilename("Accounts2");
    	job.setOutputFormats(new String[]{"PDF"});
    	job.setOutputLocale("de");
    	
    	JobRepositoryDestination repoDest = new JobRepositoryDestination();
    	repoDest.setFolderURI("/ContentFiles/pdf");
    	job.setRepositoryDestination(repoDest);
    	
    	Job savedJob = service.scheduleJob(job);
    	assertTrue(savedJob.getId() > 0);
    	assertEquals(0, savedJob.getVersion());
    	
    	job2Id = savedJob.getId();
	}

	protected void getJob2() throws RemoteException {
		Job job = service.getJob(job2Id);
		assertNotNull(job);
		assertEquals(job2Id, job.getId());
		assertEquals(0, job.getVersion());
		assertEquals("/reports/samples/AllAccounts", job.getReportUnitURI());
		assertEquals("Label 2", job.getLabel());
		assertEquals("Description 2", job.getDescription());
		
		assertNull(job.getSimpleTrigger());
		JobCalendarTrigger trigger = job.getCalendarTrigger();
		assertNotNull(trigger);
		assertEquals("Europe/Berlin", trigger.getTimezone());
		assertNotNull(trigger.getStartDate());
		assertEquals(future.getTimeInMillis(), trigger.getStartDate().getTimeInMillis());
		assertNotNull(trigger.getEndDate());
		assertEquals(futureEnd.getTimeInMillis(), trigger.getEndDate().getTimeInMillis());
		assertEquals("0", trigger.getMinutes());
		assertEquals("23", trigger.getHours());
		assertEquals(CalendarDaysType.WEEK, trigger.getDaysType());
		int[] weekDays = trigger.getWeekDays();
		assertNotNull(weekDays);
		assertEquals(2, weekDays.length);
		assertEquals(2, weekDays[0]);
		assertEquals(5, weekDays[1]);
		int[] months = trigger.getMonths();
		assertNotNull(months);
		assertEquals(12, months.length);
		
		assertEquals("Accounts2", job.getBaseOutputFilename());
		String[] formats = job.getOutputFormats();
		assertNotNull(formats);
		assertEquals(1, formats.length);
		assertEquals("PDF", formats[0]);
		assertEquals("de", job.getOutputLocale());
    	
		JobRepositoryDestination repoDest = job.getRepositoryDestination();
		assertNotNull(repoDest);
		assertEquals("/ContentFiles/pdf", repoDest.getFolderURI());
		assertFalse(repoDest.isSequentialFilenames());
		assertFalse(repoDest.isOverwriteFiles());
		
		assertNull(job.getMailNotification());
	}

	protected void updateJob2() throws RemoteException {
		Job job = service.getJob(job2Id);
		assertNotNull(job);
		
		job.setLabel("Label 2 updated");
		job.setOutputFormats(new String[]{"HTML", "XLS"});
		job.getCalendarTrigger().setMinutes("30");
		job.getRepositoryDestination().setSequentialFilenames(true);
		service.updateJob(job);
		
		Job updated = service.getJob(job2Id);
		assertEquals(1, updated.getVersion());
		assertEquals("Label 2 updated", updated.getLabel());
		assertEquals("Description 2", updated.getDescription());
		String[] formats = updated.getOutputFormats();
		assertNotNull(formats);
		assertEquals(2, formats.length);
		assertIn("HTML", formats);
		assertIn("XLS", formats);
		
		JobRepositoryDestination repoDest = updated.getRepositoryDestination();
		assertEquals(1, repoDest.getVersion());
		assertTrue(repoDest.isSequentialFilenames());
		assertFalse(repoDest.isOverwriteFiles());
		
		JobCalendarTrigger trigger = updated.getCalendarTrigger();
		assertEquals(1, repoDest.getVersion());
		assertEquals("30", trigger.getMinutes());
		assertEquals("23", trigger.getHours());
	}

	protected void createJob3() throws RemoteException {
		Job job = new Job();
    	job.setReportUnitURI("/reports/samples/SalesByMonth");
    	job.setLabel("Label 3");
    	job.setDescription("Description 3");
    	
    	JobParameter[] params = new JobParameter[]{
    			new JobParameter("TextInput", new Integer(22)),
    			new JobParameter("CheckboxInput", Boolean.TRUE),
    			new JobParameter("ListInput", "2"),
    			new JobParameter("DateInput", today),
    	};
    	job.setParameters(params);
    	
    	JobSimpleTrigger trigger = new JobSimpleTrigger();
		trigger.setStartDate(future);
    	trigger.setOccurrenceCount(1);
    	job.setSimpleTrigger(trigger);
    	
    	job.setBaseOutputFilename("Sales3");
    	job.setOutputFormats(new String[]{"PDF"});
    	
    	JobRepositoryDestination repoDest = new JobRepositoryDestination();
    	repoDest.setFolderURI("/ContentFiles");
    	job.setRepositoryDestination(repoDest);
    	
    	Job savedJob = service.scheduleJob(job);
    	assertTrue(savedJob.getId() > 0);
    	assertEquals(0, savedJob.getVersion());
    	
    	job3Id = savedJob.getId();
	}
	
	protected void getJob3() throws RemoteException {
		Job job = service.getJob(job3Id);
		assertNotNull(job);
		assertEquals(job3Id, job.getId());
		assertEquals(0, job.getVersion());
		assertEquals("/reports/samples/SalesByMonth", job.getReportUnitURI());
		assertEquals("Label 3", job.getLabel());
		assertEquals("Description 3", job.getDescription());
		
		JobParameter[] params = job.getParameters();
		assertNotNull(params);
		assertEquals(4, params.length);
		assertJobParameter(params, "TextInput", new Integer(22));
		assertJobParameter(params, "CheckboxInput", Boolean.TRUE);
		assertJobParameter(params, "ListInput", "2");
		assertJobParameter(params, "DateInput", today);
	}

	protected void assertJobParameter(JobParameter[] params, String name, Object value) {
		assertNotNull(params);
		JobParameter param = null;
		for (int i = 0; i < params.length; i++) {
			if (name.equals(params[i].getName())) {
				param = params[i];
				break;
			}
		}
		assertNotNull(param);
		assertEquals(value, param.getValue());
	}

	protected void getAllJobs23() throws RemoteException {
		JobSummary[] jobs = service.getAllJobs();
		assertNotNull(jobs);
		assertTrue(jobs.length >= 2);

		JobSummary job2 = findJob(jobs, job2Id);
		assertNotNull(job2);
		assertEquals("/reports/samples/AllAccounts", job2.getReportUnitURI());
		assertEquals("Label 2 updated", job2.getLabel());
		assertEquals(RuntimeJobState.NORMAL, job2.getState());

		JobSummary job3 = findJob(jobs, job3Id);
		assertNotNull(job3);
		assertEquals("/reports/samples/SalesByMonth", job3.getReportUnitURI());
		assertEquals("Label 3", job3.getLabel());
		assertEquals(RuntimeJobState.NORMAL, job3.getState());
	}
	
	protected JobSummary findJob(JobSummary[] jobs, long jobId) {
		JobSummary job = null;
		if (jobs != null) {
			for (int i = 0; i < jobs.length; i++) {
				if (jobs[i].getId() == jobId) {
					job = jobs[i];
					break;
				}
			}
		}
		return job;
	}

	protected void deleteJobs23() throws RemoteException {
		service.deleteJobs(new long[]{job2Id, job3Id});
		
		JobSummary[] jobs = service.getAllJobs();
		JobSummary job2 = findJob(jobs, job2Id);
		assertNull(job2);
		JobSummary job3 = findJob(jobs, job3Id);
		assertNull(job3);
	}
}
