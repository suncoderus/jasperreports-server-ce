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

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobRepositoryDestinationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobRuntimeInformationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.hibernate.HibernateReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.DuplicateOutputLocationException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobNotFoundException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobRuntimeInfoException;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulerListener;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.TriggerTypeMismatchException;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.user.service.impl.ExternalUserService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ReportSchedulingFacade
    implements ReportSchedulingService, ReportSchedulingInternalService, ReportSchedulerListener, InitializingBean {
/***
      TODO SteveRosen  the autoScheduleFailedJobsOnStartup logic will be removed soon because we found a better way!
                 ApplicationListener {
    // thorick: for test only
    private static int failedJobRunCount = 0;
***/

	private static final Log log = LogFactory.getLog(ReportSchedulingFacade.class);

	private ReportJobsPersistenceService persistenceService;
	private ReportJobsInternalService jobsInternalService;
	private ReportJobsScheduler scheduler;
	private ReportJobValidator validator;

    private ExternalUserService externalUserService;

    private AuditContext auditContext;

    private Map outputKeyMapping;

    private boolean enableSaveToHostFS;

/***
      TODO SteveRosen  the autoScheduleFailedJobsOnStartup logic will be removed soon because we found a better way!
  private boolean autoScheduleFailedJobsOnStartup = false;
***/
    private int runOnceNowDelayMilliSeconds =  500;     // arbitrary delay to spread out 'run once queue starts'

	/**
	 * @return Returns the outputKeyMapping.
	 */
	public Map getOutputKeyMapping() {
		return outputKeyMapping;
	}

	/**
	 * @param outputKeyMapping The outputKeyMapping to set.
	 */
	public void setOutputKeyMapping(Map outputKeyMapping) {
		this.outputKeyMapping = outputKeyMapping;
	}

	public ReportJobsPersistenceService getPersistenceService() {
		return persistenceService;
	}

	public void setPersistenceService(
			ReportJobsPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public ReportJobsScheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(ReportJobsScheduler scheduler) {
		this.scheduler = scheduler;
 	}

	public ReportJobValidator getValidator() {
		return validator;
	}

	public void setValidator(ReportJobValidator validator) {
		this.validator = validator;
	}

	public ReportJobsInternalService getJobsInternalService() {
		return jobsInternalService;
	}

	public void setJobsInternalService(ReportJobsInternalService jobsInternalService) {
		this.jobsInternalService = jobsInternalService;
	}

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    public ExternalUserService getExternalUserService() {
      return externalUserService;
    }

    public void setExternalUserService(ExternalUserService externalUserService) {
      this.externalUserService = externalUserService;
    }

/***
  TODO SteveRosen  the autoScheduleFailedJobsOnStartup logic will be removed soon because we found a better way!
  public void setAutoScheduleFailedJobsOnStartup(boolean b) {
    this.autoScheduleFailedJobsOnStartup = b;
  }

  public boolean getAutoScheduleFailedJobsOnStartup() {
    return autoScheduleFailedJobsOnStartup;
  }
***/

  public void setRunOnceNowDelayMilliSeconds(int i) {
    this.runOnceNowDelayMilliSeconds = i;
  }


  public int getRunOnceNowDelayMilliSeconds() {
    return runOnceNowDelayMilliSeconds;
  }


  public void afterPropertiesSet() throws Exception {
		getScheduler().addReportSchedulerListener(this);

/***
  TODO SteveRosen  the autoScheduleFailedJobsOnStartup logic will be removed soon because we found a better way!
      System.out.println("WARNING !  autoScheduleOfFailedJobsOnStartup is set to "+
          autoScheduleFailedJobsOnStartup);
***/
  }

/***
  TODO SteveRosen  the autoScheduleFailedJobsOnStartup logic will be removed soon because we found a better way!
    int eventCount;

    public void onApplicationEvent(ApplicationEvent event)  {
      p(" onApplicationEvent got event "+(eventCount++)+" "+event.getClass().getName());
      if (event instanceof ContextRefreshedEvent)  {
        p("processing ContextRefreshedEvent !");
        //
        // 2012-03-09  thorick
        //
        // If configured do auto restart of missed jobs now
        if (autoScheduleFailedJobsOnStartup) {
          if (log.isDebugEnabled()) {
            log.debug("autoScheduleFailedJobsOnStartup is true.  Proceeding to schedule any jobs with stale nextTriggerFireTimes.");
          }
          scheduleFailedJobs(new Date());
        }
        else {
          if (log.isDebugEnabled()) {
            log.debug("autoScheduleFailedJobsOnStartup is false.  Skip scheduling any jobs with stale nextTriggerFireTimes.");
          }
        }
      }
    }

    private void scheduleFailedJobs(Date onOrBefore) {

      // concurrency isn't critical here
      failedJobRunCount++;
      //p("scheduleFailedJobs on instance " + this.hashCode() + " runcount=" + failedJobRunCount);

      // hack alert:
      // afterPropertiesSet() is being called more than once on instances(?) of this Bean class
      //   I had thought that there would be only once instance of this bean per server
      //   apparently not (assuming that afterPropertiesSet() gets called only once per Bean instance).
      //
      if (failedJobRunCount > 1) {
        if (log.isDebugEnabled()) {
          log.debug("skipping duplicate run " + failedJobRunCount + " of scheduleFailedJobs as we have already run this at least once upon startup");
        }
        return;
      }

      SecurityContext origSecurityContext = SecurityContextHolder.getContext();
      if (log.isDebugEnabled()) {
          Authentication auth = origSecurityContext.getAuthentication();
          Object principal = null;
          if (auth != null) {
            principal = auth.getPrincipal();
          }
          String prinClass = principal == null ? "NULL"  : principal.getClass().getName();

          log.debug("read original security Context with principal object "+prinClass);
      }

      try {
        p("scheduleFailedJobs  start security setup.");
        User user = getExternalUserService().getUser(null, "tempadmin");
        if( user == null )
        {
            p("scheduleFailedJobs - create a temporary admin user (tempadmin)");
            user  = getExternalUserService().newUser(null);
            user.setUsername("tempadmin");
            user.setPassword("tempadmin");
            user.setFullName("tempadmin User");
            user.setEnabled(true);
            user.setPreviousPasswordChangeTime(new Date());
            getExternalUserService().putUser(null, user);
        }

        Role admin = new RoleImpl();
        admin.setRoleName("ROLE_ADMINISTRATOR");
        user.addRole(admin);
        Role superU = new RoleImpl();
        superU.setRoleName("ROLE_SUPERUSER");
        user.addRole(superU);

        getExternalUserService().makeUserLoggedIn(user);
        p("scheduleFailedJobs   security setup complete.");

        ExecutionContext context = ExecutionContextImpl.getRuntimeExecutionContext(null);
        List<ReportJobSummary> jobs = getJobsByNextFireTime(
            context,
            null,
            null,
            onOrBefore,
            null);
        if (log.isDebugEnabled()) {
          log.debug("found " + jobs.size() + " failed jobs with unfired triggers to reschedule");
        }
        if (jobs == null || jobs.size() <= 0) return;

        List<ReportJob> reportJobs = new ArrayList<ReportJob>();
        for (ReportJobSummary rjs : jobs) {
          try {
            ReportJob rj = getScheduledJob(context, rjs.getId());
            reportJobs.add(rj);
          } catch (Throwable th) {
            log.debug("scheduleFailedJobs: unable to load ReportJob " + rjs.getId() + ", skipping");
          }
        }

        p("about to queue " + reportJobs.size() + " jobs to be run");
        scheduleJobsOnceNow(context, reportJobs);

        //p("scheduleJobsOnceNow, complete");
        //p("reseting SEcurity context holder to original");
        //SecurityContextHolder.setContext(origSecurityContext);

        //p("clearing security context");
        //SecurityContextHolder.clearContext();
      } catch (Throwable th) {
        //p("reseting SEcurity context holder to original");
        //SecurityContextHolder.setContext(origSecurityContext);

        //p("clearing security context");
        //SecurityContextHolder.clearContext();

        th.printStackTrace();
        throw new JSException("Error while trying to scheduleFailedJobs " + th.getMessage());
      } finally {
        // restore whatever context we had coming into this method
        p("scheduleFailedJobs - delete the temporary admin user (tempadmin)");
        getExternalUserService().deleteUser(null, "tempadmin");

        //p("THORICK  TRY:  DO NOT  RESET THE SecurityContext");
        //boolean resetContext = false;
        //if (resetContext)
        p("reseting SEcurity context holder to original");
        SecurityContextHolder.setContext(origSecurityContext);
      }
    }
***/

    private String getReportJobFormatsAsString(ReportJob job) {

    	// fix for pluggable output formats
//        Map<Byte, String> formatNamesMap = new HashMap<Byte, String>();
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_PDF, "PDF");
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_HTML, "HTML");
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_XLS, "XLS");
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_RTF, "RTF");
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_CSV, "CSV");
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_ODT, "ODT");
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_TXT, "TXT");
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_DOCX, "DOCX");
//        formatNamesMap.put(ReportJob.OUTPUT_FORMAT_ODS, "ODS");       

        StringBuilder sb = new StringBuilder();
        for (Object outputFormat: job.getOutputFormats()) {
            if (sb.length() > 0) {
                sb.append(",");
            }

         // fix for pluggable output formats
//            sb.append(formatNamesMap.get(Byte.valueOf(outputFormat.toString())));
            sb.append(((String)outputKeyMapping.get(outputFormat.toString())).toUpperCase());
        }

        return sb.toString();
    }

    private String getReportJobNotificationEmailsAsString(ReportJob job) {
        if (job.getMailNotification() == null) {
            return null;
        }

        List emailsList = new ArrayList();
        if (job.getMailNotification().getToAddresses() != null) {
            emailsList.addAll(job.getMailNotification().getToAddresses());
        }
        if (job.getMailNotification().getCcAddresses() != null) {
            emailsList.addAll(job.getMailNotification().getCcAddresses());
        }
        if (job.getMailNotification().getBccAddresses() != null) {
            emailsList.addAll(job.getMailNotification().getBccAddresses());
        }

        StringBuilder sb = new StringBuilder();
        for (Object email: emailsList) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(email.toString());
        }

        return sb.toString();
    }

    private String getSetOfBytesAsString(Set set, Map<Byte, String> namesMap) {
        if (set == null || set.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Object element: set) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(namesMap.get(Byte.valueOf(element.toString())));
        }

        return sb.toString();
    }

    public void createDeleteReportSchedulingEvent() {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                auditContext.createAuditEvent("deleteReportScheduling");
            }
        });
    }

    public void closeDeleteReportSchedulingEvent() {
        auditContext.doInAuditContext("deleteReportScheduling", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });
    }

    public void addParamsToAuditEvent(final ReportJob job, final String jobType) {
        auditContext.doInAuditContext(jobType, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditEvent.setResourceUri(job.getSource().getReportUnitURI());
                auditContext.setResourceTypeToAuditEvent(ReportUnit.class.getName(), auditEvent);

                auditContext.addPropertyToAuditEvent("jobLabel", job.getLabel(), auditEvent);
                auditContext.addPropertyToAuditEvent("jobDescription", job.getDescription(), auditEvent);
                auditContext.addPropertyToAuditEvent("jobBaseOutputFilename", job.getBaseOutputFilename(), auditEvent);
                auditContext.addPropertyToAuditEvent("jobOutputFormats", getReportJobFormatsAsString(job), auditEvent);
                auditContext.addPropertyToAuditEvent("jobOutputLocale", job.getOutputLocale(), auditEvent);
                auditContext.addPropertyToAuditEvent("jobDestinationFolder", job.getContentRepositoryDestination().getFolderURI(), auditEvent);
                auditContext.addPropertyToAuditEvent("jobNotificationEmails", getReportJobNotificationEmailsAsString(job), auditEvent);

                auditContext.addPropertyToAuditEvent("jobTriggerStartType",
                        job.getTrigger().getStartType() == ReportJobTrigger.START_TYPE_NOW ? "NOW" : "SCHEDULE",
                        auditEvent);

                auditContext.addPropertyToAuditEvent("jobTimezone", job.getTrigger().getTimezone(), auditEvent);
                auditContext.addPropertyToAuditEvent("jobStartDate", job.getTrigger().getStartDate(), auditEvent);
                auditContext.addPropertyToAuditEvent("jobEndDate", job.getTrigger().getEndDate(), auditEvent);

                if (job.getTrigger() instanceof ReportJobSimpleTrigger) {
                    auditContext.addPropertyToAuditEvent("jobTriggerType", "SIMPLE_TRIGGER", auditEvent);
                    ReportJobSimpleTrigger trigger = (ReportJobSimpleTrigger)job.getTrigger();
                    auditContext.addPropertyToAuditEvent("jobSimpleTriggerOccurenceCount",
                            trigger.getOccurrenceCount(), auditEvent);
                    auditContext.addPropertyToAuditEvent("jobSimpleTriggerRecurrenceInterval",
                            trigger.getRecurrenceInterval(), auditEvent);

                    Map<Byte, String> intervalUnitNamesMap = new HashMap<Byte, String>();
                    intervalUnitNamesMap.put(ReportJobSimpleTrigger.INTERVAL_MINUTE, "MINUTE");
                    intervalUnitNamesMap.put(ReportJobSimpleTrigger.INTERVAL_HOUR, "HOUR");
                    intervalUnitNamesMap.put(ReportJobSimpleTrigger.INTERVAL_DAY, "DAY");
                    intervalUnitNamesMap.put(ReportJobSimpleTrigger.INTERVAL_WEEK, "WEEK");
                    String intervalUnitName = intervalUnitNamesMap.get(trigger.getRecurrenceIntervalUnit());
                    auditContext.addPropertyToAuditEvent("jobSimpleTriggerRecurrenceIntervalUnit",
                            intervalUnitName, auditEvent);
                } else {
                    ReportJobCalendarTrigger trigger = (ReportJobCalendarTrigger)job.getTrigger();
                    auditContext.addPropertyToAuditEvent("jobTriggerType", "CALENDAR_TRIGGER", auditEvent);
                    auditContext.addPropertyToAuditEvent("jobCalendarTriggerMinutes", trigger.getMinutes(), auditEvent);
                    auditContext.addPropertyToAuditEvent("jobCalendarTriggerHours", trigger.getHours(), auditEvent);

                    Map<Byte, String> daysTypeNamesMap = new HashMap<Byte, String>();
                    daysTypeNamesMap.put(ReportJobCalendarTrigger.DAYS_TYPE_ALL, "ALL");
                    daysTypeNamesMap.put(ReportJobCalendarTrigger.DAYS_TYPE_WEEK, "WEEK");
                    daysTypeNamesMap.put(ReportJobCalendarTrigger.DAYS_TYPE_MONTH, "MONTH");

                    auditContext.addPropertyToAuditEvent("jobCalendarTriggerDaysType",
                            daysTypeNamesMap.get(trigger.getDaysType()), auditEvent);

                    Map<Byte, String> weekDaysMap = new HashMap<Byte, String>();
                    weekDaysMap.put((byte)2, "mon");
                    weekDaysMap.put((byte)3, "tue");
                    weekDaysMap.put((byte)4, "wen");
                    weekDaysMap.put((byte)5, "thu");
                    weekDaysMap.put((byte)6, "fri");
                    weekDaysMap.put((byte)7, "sat");
                    weekDaysMap.put((byte)1, "sun");
                    auditContext.addPropertyToAuditEvent("jobCalendarTriggerWeekDays",
                            getSetOfBytesAsString(trigger.getWeekDays(), weekDaysMap), auditEvent);
                    auditContext.addPropertyToAuditEvent("jobCalendarTriggerMonthDays", trigger.getMonthDays(), auditEvent);

                    Map<Byte, String> monthsMap = new HashMap<Byte, String>();
                    monthsMap.put((byte)1, "jan");
                    monthsMap.put((byte)2, "feb");
                    monthsMap.put((byte)3, "mar");
                    monthsMap.put((byte)4, "apr");
                    monthsMap.put((byte)5, "may");
                    monthsMap.put((byte)6, "jun");
                    monthsMap.put((byte)7, "jul");
                    monthsMap.put((byte)8, "aug");
                    monthsMap.put((byte)9, "sep");
                    monthsMap.put((byte)10, "oct");
                    monthsMap.put((byte)11, "nov");
                    monthsMap.put((byte)12, "dec");
                    auditContext.addPropertyToAuditEvent("jobCalendarTriggerMonths",
                            getSetOfBytesAsString(trigger.getMonths(), monthsMap), auditEvent);
                }

                if (job.getSource().getParametersMap() != null) {
                    for (Object key: job.getSource().getParametersMap().keySet()) {
                        String stringKey = key.toString();
                        Object value = job.getSource().getParametersMap().get(key);
                        String stringValue = value != null ?  value.toString() : "";
                        String param = stringKey + "=" + stringValue;
                        auditContext.addPropertyToAuditEvent("jobParam", param, auditEvent);
                    }
                }
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ReportJob scheduleJob(ExecutionContext context, ReportJob job) {
		validate(context, job);
        addParamsToAuditEvent(job, "scheduleReport");
		ReportJob savedJob = persistenceService.saveJob(context, job);
		scheduler.scheduleJob(context, savedJob);
		return savedJob;
	}


  /**
   *   Take input List<ReportJob>  schedule each job as a cloned job to be
   *   run once and immediately.  The run-once Job's Trigger will have its misfire instruction
   *   set to MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY
   *   No attempt is made to retry the run-once job in the event of failure during execution
   *
   *   Note: We rely on the enclosing Transaction to see that either all of the jobs get persisted
   *          or NONE of them do.
   *
   * @param context
   * @param jobs
   * @return     List<ReportJob>  list of jobs successfully scheduled
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public List<ReportJob> scheduleJobsOnceNow(ExecutionContext context, List<ReportJob> jobs) {
    List<ReportJob> queuedJobs = new ArrayList<ReportJob>();

    if (jobs == null || jobs.size() <= 0) return queuedJobs;

    long startDelay = 0;

    for (ReportJob job : jobs) {

      if (log.isDebugEnabled()) {
          log.debug("start: schedule ReportJob "+job.getId());
      }

      ReportJob copy = new ReportJob(job);

      // Run the Job with a new Trigger:  run once with no Misfire handling.
      ReportJobTrigger oldTrigger = job.getTrigger();
      ReportJobSimpleTrigger newTrigger = new ReportJobSimpleTrigger();
      copy.setTrigger(newTrigger);


      //newTrigger.setStartType(ReportJobTrigger.START_TYPE_NOW);
      startDelay = startDelay + runOnceNowDelayMilliSeconds;
      newTrigger.setStartType(ReportJobTrigger.START_TYPE_SCHEDULE);
      Date now = new Date();
      Date startDate = new Date(now.getTime() + startDelay);
      newTrigger.setStartDate(startDate);
      newTrigger.setOccurrenceCount(1);
      //p("it is now "+now);
      //p("setting fire time to "+startDate);

      newTrigger.setTimezone(oldTrigger.getTimezone());
      newTrigger.setMisfireInstruction(ReportJobTrigger.JS_MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY);
      newTrigger.setOccurrenceCount(1);

      // we can't have identical BaseOutputFileNames
      String oldBaseOutputFileName = job.getBaseOutputFilename();
      String newBaseOutputFileName = oldBaseOutputFileName+"_retry"+(new Date().getTime());
      copy.setBaseOutputFilename(newBaseOutputFileName);

       //p("scheduleJobsOnceNow ReportJob id='"+job.getId()+
       //    "', version='"+job.getVersion()+
       //    ", baseOutputFileName='"+
       //job.getBaseOutputFilename()+", about to validate NEW ReportJob copy with baseOutputFileName='"+
       //copy.getBaseOutputFilename()+"'");


      validate(context, copy);
      addParamsToAuditEvent(copy, "scheduleReport");

      //p("about to save to Repo runOnceNowJob "+copy.getId());
      ReportJob savedCopyJob = persistenceService.saveJob(context, copy);

      //p("about to schedule savedCopyJob "+savedCopyJob.getId());
      scheduler.scheduleJob(context, savedCopyJob);

      if (log.isDebugEnabled()) {
          log.debug("DONE.    scheduled savedCopyJob "+savedCopyJob.getId());
      }

      queuedJobs.add(savedCopyJob);
    }
     return queuedJobs;
  }

  /**
   *   Take input List<ReportJobIdHolder>
   *     look up actual ReportJobs and
   *     call the core
   *     scheduleJobsOnceNow method.
   *
   *
   * @param context
   * @param jobIdHolders
   * @return     List<ReportJob>  list of jobs successfully scheduled
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public List<ReportJobIdHolder> scheduleJobsOnceNowById(ExecutionContext context, List<ReportJobIdHolder> jobIdHolders) {
    ArrayList<ReportJobIdHolder> queuedJobIds = new ArrayList<ReportJobIdHolder>();
    if (jobIdHolders == null || jobIdHolders.size() <= 0)  return queuedJobIds;
    List<ReportJob> jobs = new ArrayList<ReportJob>();
    ReportJob job = null;
    for (ReportJobIdHolder idHolder : jobIdHolders) {
      try {
        job = persistenceService.loadJob(context, idHolder);
      }catch (Exception e) {
        StringBuilder sb = new StringBuilder();
        for (ReportJobIdHolder h : jobIdHolders) {
          sb.append(h.getId()+", ");
        }
        throw new JSException("unable to load ReportJob with id="+idHolder.getId()+
            ".  Aborting scheduleJobsOnceNowById for all ids='"+sb.toString()+"'", e);
      }
      jobs.add(job);
    }
    List<ReportJob> queuedJobs = scheduleJobsOnceNow(context, jobs);
    if (queuedJobs == null || queuedJobs.size() <= 0)  return  queuedJobIds;
    for (ReportJob job1 : queuedJobs) {
      queuedJobIds.add(new ReportJobIdHolder(job1.getId()));
    }
    return queuedJobIds;
  }


  public void pause(List<ReportJob> jobs, boolean all) {
    scheduler.pause(jobs, all);
  }

  public void resume(List<ReportJob> jobs, boolean all) {
    scheduler.resume(jobs, all);
  }

    public void pauseById(ExecutionContext context, List<ReportJobIdHolder> jobs, boolean all) {
        scheduler.pauseById(jobs, all); // for now this method is not in the interface
        for (ReportJobIdHolder job : jobs) {
            addParamsToAuditEvent(getScheduledJob(context, job.getId()), "pauseReportScheduling");
        }
    }


    @Override
    public List<Long> pauseJobs(ExecutionContext context, List<ReportJobIdHolder> jobs, boolean all) {
        scheduler.pauseById(jobs, all); // for now this method is not in the interface
        ArrayList<Long> pausedIds = new ArrayList<Long>();
        for (Iterator<ReportJobIdHolder> iterator = jobs.iterator(); iterator.hasNext(); ) {
            long id = iterator.next().getId();
            try {
                addParamsToAuditEvent(getScheduledJob(context, id), "pauseReportScheduling");
                pausedIds.add(id);
            } catch (ReportJobNotFoundException e) {
                iterator.remove();
            }
        }
        return pausedIds;
    }

    public void resumeById(ExecutionContext context, List<ReportJobIdHolder> jobs, boolean all) {
        scheduler.resumeById(jobs, all);
        for (ReportJobIdHolder job : jobs) {
            addParamsToAuditEvent(getScheduledJob(context, job.getId()), "resumeReportScheduling");
        }
    }


    @Override
    public List<Long> resumeJobs(ExecutionContext context, List<ReportJobIdHolder> jobs, boolean all) {
        scheduler.resumeById(jobs, all);
        ArrayList<Long> resumedIds = new ArrayList<Long>();
        for (Iterator<ReportJobIdHolder> iterator = jobs.iterator(); iterator.hasNext(); ) {
            long id = iterator.next().getId();
            try {
                addParamsToAuditEvent(getScheduledJob(context, id), "resumeReportScheduling");
                resumedIds.add(id);
            } catch (ReportJobNotFoundException e) {
                iterator.remove();
            }
        }
        return resumedIds;
    }

    protected void validate(ExecutionContext context, ReportJob job) {
        ValidationErrors errors = validator.validateJob(context, job);
        validateScheduledJobOutputLocation(errors, context, job);
        if (errors.isError()) {
            throw new JSValidationException(errors);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
	public List getScheduledJobs(ExecutionContext context, String reportUnitURI) {
		return getScheduledJobSummaries(context, reportUnitURI);
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List getScheduledJobs(ExecutionContext context) {
		return getScheduledJobSummaries(context);
	}


    @Transactional(propagation = Propagation.REQUIRED)
    public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context, String reportUnitURI) {
		List jobs = persistenceService.listJobs(context, reportUnitURI);
		setSummaryRuntimeInformation(context, jobs);
		return jobs;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context) {
		List jobs = persistenceService.listJobs(context);
		setSummaryRuntimeInformation(context, jobs);
		return jobs;
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ReportJobSummary> getScheduledJobSummaries(ExecutionContext context, ReportJobModel reportJobCriteria, int startIndex,
            int numberOfRows, ReportJobModel.ReportJobSortType sortType, boolean isAscending) throws ReportJobRuntimeInfoException {
         // cannot apply runtime information sorting in persistence service
        if ((sortType == ReportJobModel.ReportJobSortType.SORTBY_STATUS) ||
                (sortType == ReportJobModel.ReportJobSortType.SORTBY_LASTRUN) ||
                (sortType == ReportJobModel.ReportJobSortType.SORTBY_NEXTRUN)) {
            // get all the search job without apply sorting and pagination
            List<ReportJobSummary> jobs = persistenceService.listJobs(context, reportJobCriteria, 0, -1, null, isAscending);
            setSummaryRuntimeInformation(context, jobs);

            if (reportJobCriteria.isRuntimeInformationModified() && reportJobCriteria.getRuntimeInformationModel() != null)
                jobs = filterSummaryRuntimeInformation(jobs, reportJobCriteria.getRuntimeInformationModel());

            // apply runtime sorting
            Comparator<ReportJobSummary> comparator = getRunTimeInfoComparator(context, sortType);
            if (comparator != null) {
                if (!isAscending) comparator = Collections.reverseOrder(comparator);
                Collections.sort(jobs, comparator);
            } else if (!isAscending) {
                Collections.reverse(jobs);
            }
            // apply pagination
            int beginningIndex = 0;
            if (startIndex > 0) beginningIndex = startIndex;
            if ((beginningIndex == 0) && (numberOfRows == -1)) return jobs;
            List<ReportJobSummary> newList = new ArrayList<ReportJobSummary>();
            if (beginningIndex >= jobs.size()) return newList;
            int showRowCount = numberOfRows;
            if ((numberOfRows < 0) || (numberOfRows > (jobs.size() - startIndex))) showRowCount = jobs.size() - beginningIndex;
            for (int i = beginningIndex; i < (showRowCount + beginningIndex); i++) {
                newList.add(jobs.get(i));
            }
            return newList;
        } else {
            final List<ReportJobSummary> reportJobSummaries = persistenceService.listJobs(context, reportJobCriteria, startIndex, numberOfRows, sortType, isAscending);
            setSummaryRuntimeInformation(context, reportJobSummaries);
            return reportJobSummaries;
        }
    }



    //
    // 2012-03-06  thorick
    // This method is *specifically* not Transactional.  The underlying ReportJobSummary fetch is.
    //
    //@Transactional(propagation = Propagation.REQUIRED)
    public List<ReportJobSummary> getJobsByNextFireTime(ExecutionContext context,
                                                List<ReportJob> searchList,
                                                Date startNextTriggerFireDate,
                                                Date endNextTriggerFireDate,
                                                List<Byte> includeTriggerStates) {
      //
      // Review / todo: 2012-03-07 thorick: it may yield some benefit to have the underlying Hibernate
      //                         query exclude any jobs whose Calendar start and end dates
      //                         lie OUTSIDE of our nextTriggerFire time dates
      //
      // get the big list for starters
      p("getJobsByNextFireTime  START");
      List<ReportJobSummary> list = null;
      if (searchList != null && searchList.size() > 0)  {
        list = persistenceService.listJobs(context, searchList);
      }
      else {
          p("about to do persistenceService.listJobs(context)");

          // THORICK CATCH ANYTHING
          try {
          list = persistenceService.listJobs(context);
          p("DONE.  about to do persistenceService.listJobs(context)");
          } catch (Throwable th)  {
            p("ERROR    GOT THROWABLE "+th.getClass().getName()+" FROM persistenceService "+th.getMessage());
            th.printStackTrace();
            throw new RuntimeException(th);
          }
      }
      // apply filters record by record
      p("getJobsByNextFireTime  List has "+list.size()+" candidate ReportJobSummary entries");
      p("current time is "+new Date());
      p("startDate=" + (startNextTriggerFireDate == null ? "NULL" : startNextTriggerFireDate.toString()));
      p("endDate=" + (endNextTriggerFireDate == null ? "NULL" : endNextTriggerFireDate.toString()));


      if (startNextTriggerFireDate == null && endNextTriggerFireDate == null && includeTriggerStates == null)  return list;


      // prepare filtering
      setSummaryRuntimeInformation(context, list);

      List<ReportJobSummary> filteredList = new LinkedList<ReportJobSummary>(list);
      Iterator<ReportJobSummary> it = list.iterator();
      while (it.hasNext()) {
        ReportJobSummary rjs = it.next();
        //p("next ReportJobSummary "+rjs.getId());
        ReportJobRuntimeInformation rjr = rjs.getRuntimeInformation();
        if (rjr != null) {
          Date nextFireTime = rjr.getNextFireTime();
          if (nextFireTime != null) {
            if (startNextTriggerFireDate != null) {
              //p("startDate="+startNextTriggerFireDate+", nextFireTime="+nextFireTime);
              if (nextFireTime.before(startNextTriggerFireDate)) {
                //p(rjs.getId()+" remove from list.");
                filteredList.remove(rjs);
                continue;
              }
            }
            if (endNextTriggerFireDate != null) {
              //p("endDate="+endNextTriggerFireDate+", nextFireTime="+nextFireTime);
              if (nextFireTime.after(endNextTriggerFireDate)) {
                //p(rjs.getId()+"remove from list.");
                filteredList.remove(rjs);
                continue;
              }
            }
          }
          if (includeTriggerStates != null && includeTriggerStates.size() > 0) {
            Byte triggerState = rjr.getStateCode();
            //p("check TriggerStates  state of this trigger == '"+triggerState+"'");
            if (!includeTriggerStates.contains(triggerState)) {
              filteredList.remove(rjs);
              //p(rjs.getId()+"remove from list.");
              continue;
            }
            //p("trigger state OK, keep "+rjs.getId()+" in list.");
          }
        }
      }
      return filteredList;
    }


    private void p(String s) {
		log.info(s);
    }


    private List<ReportJobSummary> filterSummaryRuntimeInformation(List<ReportJobSummary> summaryList, ReportJobRuntimeInformationModel runtimeInfo) {
        if (summaryList == null) return null;
        List<ReportJobSummary> newList = new ArrayList<ReportJobSummary>();
        for (ReportJobSummary reportJobSummary: summaryList) {
            if (reportJobSummary.getRuntimeInformation() == null) continue;
            if (runtimeInfo.isNextFireTimeModified() && runtimeInfo.getNextFireTime() != null) {
                if (!equals(reportJobSummary.getRuntimeInformation().getNextFireTime(), runtimeInfo.getNextFireTime())) continue;
            } else if (runtimeInfo.isPreviousFireTimeModified() && runtimeInfo.getPreviousFireTime() != null) {
                if (!equals(reportJobSummary.getRuntimeInformation().getPreviousFireTime(), runtimeInfo.getPreviousFireTime())) continue;
            } else if (runtimeInfo.isStateModified() && runtimeInfo.getStateCode() != null) {
                if (!runtimeInfo.getStateCode().equals(reportJobSummary.getRuntimeInformation().getStateCode())) continue;
            }
            newList.add(reportJobSummary);
        }
        return newList;
    }

    private boolean equals(Object obj1, Object obj2) {
        if (obj1 == obj2) return true;
        if ((obj1 == null) || (obj2 == null)) return false;
        if ((obj1 instanceof Date) && (obj2 instanceof Date)) {
            Calendar calendar1  = new GregorianCalendar();
            calendar1.setTime((Date)obj1);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);
            Calendar calendar2  = new GregorianCalendar();
            calendar2.setTime((Date)obj2);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.MILLISECOND, 0);
            return (calendar1.compareTo(calendar2) == 0);
        } else if ((obj1 instanceof String) && (obj2 instanceof String)) {
            return ((String) obj1).equalsIgnoreCase((String)obj2);
        } else return obj1.equals(obj2);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void validateScheduledJobOutputLocation(ValidationErrors errors, ExecutionContext context, ReportJob job) {
        if ((job == null) || (job.getContentRepositoryDestination() == null) || !job.getContentRepositoryDestination().isSaveToRepository()) return;
        String job_FolderURI = (job.getContentRepositoryDestination().isUsingDefaultReportOutputFolderURI()?
                job.getContentRepositoryDestination().getDefaultReportOutputFolderURI() : job.getContentRepositoryDestination().getFolderURI());
        if (job_FolderURI == null) return;

        ReportJobModel reportJobCriteria = new ReportJobModel();
        reportJobCriteria.setBaseOutputFilename(job.getBaseOutputFilename());
        ReportJobRepositoryDestinationModel reportJobRepositoryDestinationModel = new ReportJobRepositoryDestinationModel();
        reportJobRepositoryDestinationModel.setSaveToRepository(true);
        reportJobRepositoryDestinationModel.setOverwriteFiles(false);
        reportJobRepositoryDestinationModel.setUsingDefaultReportOutputFolderURI(false);
        reportJobRepositoryDestinationModel.setFolderURI(job_FolderURI);
        reportJobCriteria.setContentRepositoryDestinationModel(reportJobRepositoryDestinationModel);
        List<ReportJobSummary> jobs = persistenceService.listJobs(context, reportJobCriteria, 0, -1, null, true);

        reportJobRepositoryDestinationModel = new ReportJobRepositoryDestinationModel();
        reportJobRepositoryDestinationModel.setSaveToRepository(true);
        reportJobRepositoryDestinationModel.setOverwriteFiles(false);
        reportJobRepositoryDestinationModel.setUsingDefaultReportOutputFolderURI(true);
        reportJobCriteria.setContentRepositoryDestinationModel(reportJobRepositoryDestinationModel);
        jobs.addAll(persistenceService.listJobs(context, reportJobCriteria, 0, -1, null, true));

        for (Iterator it = jobs.iterator(); it.hasNext();) {
            ReportJobSummary jobSummary = (ReportJobSummary) it.next();
            if (jobSummary.getId() != job.getId()) {
                    errors.add(new ValidationErrorImpl("error.duplicate.report.job.output.filename",
                            new Object[]{job.getBaseOutputFilename(), job_FolderURI}, null, "baseOutputFilename"));
                return;
                }
            }

        }


    public ReportJobRuntimeInformation getJobRuntimeInformation(ExecutionContext context, long jobId){
        ReportJobRuntimeInformation[] runtimeInfos = scheduler.getJobsRuntimeInformation(context, new long[]{jobId});
        return runtimeInfos != null ? runtimeInfos[0] : null;
    }

	public void setSummaryRuntimeInformation(ExecutionContext context, List jobs) {
		if (jobs != null && !jobs.isEmpty()) {
			long[] jobIds = new long[jobs.size()];
			int idx = 0;
			for (Iterator it = jobs.iterator(); it.hasNext(); ++idx) {
				ReportJobSummary job = (ReportJobSummary) it.next();
				jobIds[idx] = job.getId();
			}

			ReportJobRuntimeInformation[] runtimeInfos = scheduler.getJobsRuntimeInformation(context, jobIds);

			idx = 0;
			for (Iterator it = jobs.iterator(); it.hasNext(); ++idx) {
				ReportJobSummary job = (ReportJobSummary) it.next();
				job.setRuntimeInformation(runtimeInfos[idx]);
			}
		}
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void removeScheduledJob(ExecutionContext context, long jobId) {
        createDeleteReportSchedulingEvent();
		deleteJob(context, jobId);
        closeDeleteReportSchedulingEvent();
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void removeScheduledJobs(ExecutionContext context, long[] jobIds) {
		for (long jobId : jobIds) {
            createDeleteReportSchedulingEvent();
			deleteJob(context, jobId);
            closeDeleteReportSchedulingEvent();
		}
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void removeReportUnitJobs(String reportUnitURI) {
		long[] deletedJobIds = getJobsInternalService().deleteReportUnitJobs(reportUnitURI);
		unscheduleJobs(deletedJobIds);
	}

	protected void unscheduleJobs(long[] deletedJobIds) {
		if (deletedJobIds != null && deletedJobIds.length > 0) {
			for (long jobId : deletedJobIds)
				scheduler.removeScheduledJob(null, jobId);
		}
	}

	protected void deleteJob(ExecutionContext context, long jobId) {
        addParamsToAuditEvent(getScheduledJob(context, jobId), "deleteReportScheduling");
		scheduler.removeScheduledJob(context, jobId);
		persistenceService.deleteJob(context, new ReportJobIdHolder(jobId));
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public ReportJob getScheduledJob(ExecutionContext context, long jobId) {
		return persistenceService.loadJob(context, new ReportJobIdHolder(jobId));
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void reportJobFinalized(long jobId) {
		if (log.isDebugEnabled()) {
			log.debug("Job " + jobId + " finalized, deleting data");
		}

		getJobsInternalService().deleteJob(jobId);
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateScheduledJob(ExecutionContext context, ReportJob job) {
		validate(context, job);

		ReportJobTrigger origTrigger = job.getTrigger();
		long origTriggerId = origTrigger.getId();
		int origTriggerVersion = origTrigger.getVersion();

        addParamsToAuditEvent(job, "updateReportScheduling");

		ReportJob savedJob = persistenceService.updateJob(context, job);
		ReportJobTrigger updatedTrigger = savedJob.getTrigger();

		if (updatedTrigger.getId() != origTriggerId || updatedTrigger.getVersion() != origTriggerVersion) {
			scheduler.rescheduleJob(context, savedJob);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Trigger attributes not changed for job " + job.getId() + ", the job will not be rescheduled");
			}
		}
	}


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public List<ReportJobIdHolder> updateScheduledJobsByID(ExecutionContext context, List<ReportJobIdHolder> reportJobHolders,
                                                           ReportJobModel jobModel, boolean replaceTriggerIgnoreType)
            throws TriggerTypeMismatchException, ReportJobNotFoundException, DuplicateOutputLocationException {
        if ((jobModel == null) || (reportJobHolders == null) || reportJobHolders.isEmpty())
            return Collections.EMPTY_LIST;
        List<ReportJob> reportJobList = persistenceService.loadJobs(context, reportJobHolders);
        if (reportJobList == null || reportJobList.isEmpty()) return Collections.EMPTY_LIST;

        ValidationErrors validationErrorsResult = new ValidationErrorsImpl();
        ValidationErrors validationErrors;
        for (ReportJob reportJob : reportJobList) {
            reportJob.applyModel(jobModel, replaceTriggerIgnoreType);
            validationErrors = validator.validateJob(context, reportJob);
            if (validationErrors.isError()) {
                validationErrorsResult.addErrors(validationErrors.getErrors());
            }
        }
        if (validationErrorsResult.isError()) throw new JSValidationException(validationErrorsResult);

        reportJobList = updateScheduledJobs(context, reportJobList, jobModel, replaceTriggerIgnoreType);
        List<ReportJobIdHolder> idHolderList = new ArrayList<ReportJobIdHolder>();
        for (ReportJob job : reportJobList) {
            idHolderList.add(new ReportJobIdHolder(job.getId()));
        }
        return idHolderList;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public List<ReportJob> updateScheduledJobs(ExecutionContext context, List<ReportJob> reportJobList,
             ReportJobModel jobModel, boolean replaceTriggerIgnoreType) throws TriggerTypeMismatchException, DuplicateOutputLocationException {
        List<Long> origTriggerIdList = new ArrayList<Long>();
        List<Integer> origTriggerVersionList = new ArrayList<Integer>();
        for (ReportJob job : reportJobList) {
            ReportJobTrigger origTrigger = job.getTrigger();
            origTriggerIdList.add(origTrigger.getId());
            origTriggerVersionList.add(origTrigger.getVersion());
            addParamsToAuditEvent(job, "updateReportScheduling");
        }
        List<ReportJob> savedJobs = persistenceService.updateJobs(context, reportJobList, jobModel, replaceTriggerIgnoreType);
        for (ReportJob savedJob: savedJobs)         {
            int index = getIndex(reportJobList, savedJob.getId());
            if (index < 0) break;
            ReportJobTrigger updatedTrigger = savedJob.getTrigger();

            if (updatedTrigger.getId() != origTriggerIdList.get(index) || updatedTrigger.getVersion() != origTriggerVersionList.get(index)) {
                scheduler.rescheduleJob(context, savedJob);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Trigger attributes not changed for job " + savedJob.getId() + ", the job will not be rescheduled");
                }
            }
        }
        return savedJobs;
    }

    private void foundInvalidID(List<ReportJobIdHolder> expectedIDs, List<ReportJob> actualIDs) throws ReportJobNotFoundException {
        if (actualIDs == null) throw new ReportJobNotFoundException(expectedIDs.get(0).getId());
        ArrayList<Long> foundIDList = new ArrayList<Long>();
        for (ReportJob actualIDJob : actualIDs) foundIDList.add(actualIDJob.getId());
        for (ReportJobIdHolder expectedID : expectedIDs) {
            long id = expectedID.getId();
            if (!foundIDList.contains(id)) throw new ReportJobNotFoundException(id);
        }
    }

    private int getIndex(List<ReportJob> reportJobList, long id) {
        for (int i = 0; i < reportJobList.size(); i++) {
            if (reportJobList.get(i).getId() == id) return i;
        }
        return -1;
    }

	public ValidationErrors validateJob(ExecutionContext context, ReportJob job) {
		ValidationErrors errors = validator.validateJob(context, job);
        validateScheduledJobOutputLocation(errors, context, job);
        if (!hasTriggerErrors(errors)) {
			scheduler.validate(job, errors);
		}
		return errors;
	}

	protected boolean hasTriggerErrors(ValidationErrors errors) {
		boolean triggerError = false;
		for(Iterator it = errors.getErrors().iterator(); !triggerError && it.hasNext(); ) {
			ValidationError error = (ValidationError) it.next();
			String field = error.getField();
			if (field != null && (field.equals("trigger") || field.startsWith("trigger."))) {
				triggerError = true;
			}
		}
		return triggerError;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ReportJob saveJob(ExecutionContext context, ReportJob job) {
		validateSaveJob(context, job);
		ReportJob savedJob = persistenceService.saveJob(context, job, false);
		scheduler.scheduleJob(context, savedJob);
		return savedJob;
	}

	protected void validateSaveJob(ExecutionContext context, ReportJob job) {
		ValidationErrors errors = validator.validateJob(context, job);

		// allow jobs with past start dates to be saved
		errors.removeError("error.before.current.date", "trigger.startDate");

		if (errors.isError()) {
			throw new JSValidationException(errors);
		}
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateReportUnitURI(String oldURI, String newURI) {
		getJobsInternalService().updateReportUnitURI(oldURI, newURI);
	}

        private Comparator<ReportJobSummary> getRunTimeInfoComparator(final ExecutionContext context, final ReportJobModel.ReportJobSortType sortBy) {
        if ((sortBy == null) || (sortBy == ReportJobModel.ReportJobSortType.NONE)) return null;
        return new Comparator<ReportJobSummary>() {
            public int compare(ReportJobSummary o1, ReportJobSummary o2) {
                switch (sortBy) {
                    case SORTBY_STATUS:
                        return getRuntimeInformation(context, o1).getStateCode() -
                                getRuntimeInformation(context, o2).getStateCode();
                    case SORTBY_LASTRUN:
                        return HibernateReportJobsPersistenceService.compareObject(getRuntimeInformation(context, o1).getPreviousFireTime(),
                                getRuntimeInformation(context, o2).getPreviousFireTime());
                    case SORTBY_NEXTRUN:
                        return HibernateReportJobsPersistenceService.compareObject(getRuntimeInformation(context, o1).getNextFireTime(),
                                getRuntimeInformation(context, o2).getNextFireTime());
                    default:
                        return 0;
                }
            }
        };
    }

    private ReportJobRuntimeInformation getRuntimeInformation(ExecutionContext context, ReportJobSummary jobSummary) {
        if (jobSummary.getRuntimeInformation() != null) return jobSummary.getRuntimeInformation();
        ReportJobRuntimeInformation info = getJobRuntimeInformation(context, jobSummary.getId());
        jobSummary.setRuntimeInformation(info);
        return info;
    }

    public boolean isEnableSaveToHostFS() {
        return enableSaveToHostFS;
    }

    public void setEnableSaveToHostFS(boolean enableSaveToHostFS) {
        this.enableSaveToHostFS = enableSaveToHostFS;
    }
}
