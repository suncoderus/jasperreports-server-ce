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

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Calendar;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobIdHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsPersistenceService;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.AbstractTrigger;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.factory.InitializingBean;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulerListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;

/**
 * Implementation of {@link ReportJobsScheduler).
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ReportJobsQuartzScheduler implements ReportJobsScheduler, InitializingBean, Diagnostic {

	protected static final Log log = LogFactory.getLog(ReportJobsQuartzScheduler.class);

	private static final String GROUP = "ReportJobs";
	private static final String TRIGGER_LISTENER_NAME = "reportSchedulerTriggerListener";
	
	private static final long COEFFICIENT_MINUTE = 60l * 1000l;
	private static final long COEFFICIENT_HOUR = 60l * COEFFICIENT_MINUTE;
	private static final long COEFFICIENT_DAY = 24l * COEFFICIENT_HOUR;
	private static final long COEFFICIENT_WEEK = 7l * COEFFICIENT_DAY;
	
	private static final int COUNT_WEEKDAYS = 7;
	private static final int COUNT_MONTHS = 12;
	
	private Scheduler scheduler;
    private ReportJobsPersistenceService persistenceService;
	private Class reportExecutionJobClass;
	
	private final Set listeners;
	private final SchedulerListener schedulerListener;
	private final TriggerListener triggerListener;


    private static final String SMART_POLICY = "SMART_POLICY";
    private static final String MISFIRE_INSTRUCTION_FIRE_NOW = "MISFIRE_INSTRUCTION_FIRE_NOW";
    private static final String MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY = "MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY";
    private static final String MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT = "MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT";
    private static final String MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT = "MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT";
    private static final String MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT = "MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT";
    private static final String MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT = "MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT";
    private static final String MISFIRE_INSTRUCTION_DO_NOTHING = "MISFIRE_INSTRUCTION_DO_NOTHING";
    private static final String MISFIRE_INSTRUCTION_FIRE_ONCE_NOW = "MISFIRE_INSTRUCTION_FIRE_ONCE_NOW";


    private String repeatingSimpleJobMisfirePolicy;
    private String singleSimpleJobMisfirePolicy;
    private String calendarJobMisfirePolicy;

	public ReportJobsQuartzScheduler() {


		listeners = new HashSet();

		schedulerListener = new ReportSchedulerQuartzListener();
		triggerListener = new ReportSchedulerTriggerListener(TRIGGER_LISTENER_NAME);
	}

    public String getSingleSimpleJobMisfirePolicy() {
        return singleSimpleJobMisfirePolicy;
    }

    public void setSingleSimpleJobMisfirePolicy(String singleSimpleJobMisfirePolicy) {
        this.singleSimpleJobMisfirePolicy = singleSimpleJobMisfirePolicy;
    }

    public String getRepeatingSimpleJobMisfirePolicy() {
        return repeatingSimpleJobMisfirePolicy;
    }

    public void setRepeatingSimpleJobMisfirePolicy(String repeatingSimpleJobMisfirePolicy) {
        this.repeatingSimpleJobMisfirePolicy = repeatingSimpleJobMisfirePolicy;
    }

    public String getcalendarJobMisfirePolicy() {
        return calendarJobMisfirePolicy;
    }

    public void setcalendarJobMisfirePolicy(String calendarJobMisfirePolicy) {
        this.calendarJobMisfirePolicy = calendarJobMisfirePolicy;
    }

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

    public ReportJobsPersistenceService getPersistenceService() {
        return persistenceService;
    }

    public void setPersistenceService(ReportJobsPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public Class getReportExecutionJobClass() {
		return reportExecutionJobClass;
	}

	public void setReportExecutionJobClass(Class reportExecutionJobClass) {
		this.reportExecutionJobClass = reportExecutionJobClass;
	}






	public void afterPropertiesSet() {
		try {
            getScheduler().getListenerManager().addTriggerListener(triggerListener, (List<Matcher<TriggerKey>>) null);
            getScheduler().getListenerManager().addSchedulerListener(schedulerListener);

		} catch (SchedulerException e) {
			log.error("Error (de)registering Quartz listener", e);
			throw new JSExceptionWrapper(e);
		}
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void scheduleJob(ExecutionContext context, ReportJob job) {
		JobDetail jobDetail = createJobDetail(job);
		Trigger trigger = createTrigger(job);
		try {
            scheduler.scheduleJob(jobDetail, trigger);
			if (log.isDebugEnabled()) {
				log.debug("Created job " + jobDetail.getKey().getName() + " and trigger " + trigger.getKey().getName() +
                        " for job " + job.getId());
                //log.debug("Created job " + jobDetail.getFullName() + " and trigger " + trigger.getFullName() + " for job " + job.getId());
			}
		} catch (SchedulerException e) {
			log.error("Error scheduling Quartz job", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected JobDetail createJobDetail(ReportJob job) {
		String jobName = jobName(job.getId());
        JobDetail jobDetail = newJob(getReportExecutionJobClass()).
            withIdentity(jobName, GROUP).
                requestRecovery(true).
                build();
		return jobDetail;
	}


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void rescheduleJob(ExecutionContext context, ReportJob job) {
		try {
			Trigger oldTrigger = getReportJobTrigger(job.getId());
			
			String jobName = jobName(job.getId());
			Trigger trigger = createTrigger(job);

			if (oldTrigger == null) {
                JobDetail jobDetail = createJobDetail(job);
				scheduler.scheduleJob(jobDetail, trigger);
				if (log.isDebugEnabled()) {
					log.debug("Scheduled trigger " + trigger.getKey().getName() + " for job " + job.getId());
                    //log.debug("Scheduled trigger " + trigger.getFullName() + " for job " + job.getId());
				}
			} else {
				scheduler.rescheduleJob(oldTrigger.getKey(), trigger);
                //scheduler.rescheduleJob(oldTrigger.getName(), oldTrigger.getGroup(), trigger);

				if (log.isDebugEnabled()) {
					log.debug("Trigger " + oldTrigger.getKey().getName() + " rescheduled by " + trigger.getKey().getName() +
                            " for job " + job.getId());
                    //log.debug("Trigger " + oldTrigger.getFullName() + " rescheduled by " + trigger.getFullName() + " for job " + job.getId());
				}
			}
		} catch (SchedulerException e) {
			log.error("Error rescheduling Quartz job", e);
			throw new JSExceptionWrapper(e);
		}
	}
	
	protected Trigger getReportJobTrigger(long jobId) throws SchedulerException {
		Trigger trigger;
		String jobName = jobName(jobId);
		List<? extends Trigger> triggers = scheduler.getTriggersOfJob(getJobKey(jobName));

        // Filtering triggers by group to exclude system triggers.
        List<Trigger> filteredTriggersList = new ArrayList<Trigger>();
        for (Trigger currTrigger : triggers) {
            if (GROUP.equals(currTrigger.getKey().getGroup())) {
                filteredTriggersList.add(currTrigger);
            }
        }
        triggers = filteredTriggersList;
        if (triggers == null || triggers.isEmpty()) {
			trigger = null;
			
			if (log.isDebugEnabled()) {
				log.debug("No trigger found for job " + jobId);
			}
		} else if (triggers.size() == 1) {
			trigger = triggers.get(0);
			if (log.isDebugEnabled()) {
				log.debug("Trigger " + trigger.getKey().getName() + " found for job " + jobId);
                //log.debug("Trigger " + trigger.getFullName() + " found for job " + jobId);
			}
		} else {
			throw new JSException("jsexception.job.has.more.than.one.trigger", new Object[] {new Long(jobId)});
		}
		return trigger;
	}

	protected String jobName(long jobId) {
		return "job_" + jobId;
	}

	protected String triggerName(ReportJobTrigger jobTrigger) {
		return "trigger_" + jobTrigger.getId() + "_" + jobTrigger.getVersion();
	}

	protected Trigger createTrigger(ReportJob reportJob) {
		Trigger trigger;
		ReportJobTrigger jobTrigger = reportJob.getTrigger();
		if (jobTrigger instanceof ReportJobSimpleTrigger) {
			trigger = createTrigger((ReportJobSimpleTrigger) jobTrigger);
		} else if (jobTrigger instanceof ReportJobCalendarTrigger) {
			trigger = createTrigger((ReportJobCalendarTrigger) jobTrigger);
		} else {
			String quotedJobTrigger = "\"" + jobTrigger.getClass().getName() + "\"";
			throw new JSException("jsexception.job.unknown.trigger.type", new Object[] {quotedJobTrigger});
		}
		
		JobDataMap jobDataMap = trigger.getJobDataMap();
		jobDataMap.put(com.jaspersoft.jasperserver.api.engine.scheduling.quartz.ReportExecutionJob.JOB_DATA_KEY_DETAILS_ID, new Long(reportJob.getId()));
		jobDataMap.put(com.jaspersoft.jasperserver.api.engine.scheduling.quartz.ReportExecutionJob.JOB_DATA_KEY_USERNAME, reportJob.getUsername());

        TriggerKey tk = getTriggerKey(jobTrigger);
        Matcher<TriggerKey> matcher = KeyMatcher.keyEquals(tk);
        try {
          getScheduler().getListenerManager().addTriggerListener(triggerListener, matcher);
        } catch (org.quartz.SchedulerException e) {
            throw new JSException("Error adding Quartz Trigger Listener: "+e.getMessage());
        }
		return trigger;
	}

	protected Trigger createTrigger(ReportJobSimpleTrigger jobTrigger) {
		String triggerName = triggerName(jobTrigger);
		Date startDate = getStartDate(jobTrigger);
		Date endDate = getEndDate(jobTrigger);
        String calendarName = jobTrigger.getCalendarName();
			
		int repeatCount = repeatCount(jobTrigger);
		Trigger trigger = null;
        String policy = "";


        long interval = 0;
        if(repeatCount !=0)
        {
            int recurrenceInterval = jobTrigger.getRecurrenceInterval().intValue();
            long unitCoefficient = getIntervalUnitCoefficient(jobTrigger);
            interval = recurrenceInterval * unitCoefficient;
            policy = repeatingSimpleJobMisfirePolicy;
        }
        else
        {
            policy = singleSimpleJobMisfirePolicy;
            interval = 0;
        }


        if(policy.equals(MISFIRE_INSTRUCTION_FIRE_NOW))
        {
		  trigger = newTrigger().withIdentity(triggerName, GROUP).
                  startAt(startDate).
                  modifiedByCalendar(calendarName).
                  withSchedule(simpleSchedule().
                            withIntervalInMilliseconds(interval).
                            withRepeatCount(repeatCount).
                            withMisfireHandlingInstructionFireNow()).
                  build();


        }
        else if(policy.equals(MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY))
        {
          trigger = newTrigger().withIdentity(triggerName, GROUP).
                  startAt(startDate).
                  modifiedByCalendar(calendarName).
                  withSchedule(simpleSchedule().
                          withIntervalInMilliseconds(interval).
                          withRepeatCount(repeatCount).
                            withMisfireHandlingInstructionIgnoreMisfires()).
                  build();
		}
        else if(policy.equals(MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT))
        {
            trigger = newTrigger().withIdentity(triggerName, GROUP).
                    startAt(startDate).
                    modifiedByCalendar(calendarName).
                    withSchedule(simpleSchedule().
                            withIntervalInMilliseconds(interval).
                            withRepeatCount(repeatCount).
                            withMisfireHandlingInstructionNextWithExistingCount()).
                    build();
        }
        else if(policy.equals(MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT))
        {
            trigger = newTrigger().withIdentity(triggerName, GROUP).
                    startAt(startDate).
                    modifiedByCalendar(calendarName).
                    withSchedule(simpleSchedule().
                            withIntervalInMilliseconds(interval).
                            withRepeatCount(repeatCount).
                            withMisfireHandlingInstructionNextWithRemainingCount()).
                    build();
        }

        else if(policy.equals(MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT))
        {
            trigger = newTrigger().withIdentity(triggerName, GROUP).
                    startAt(startDate).
                    modifiedByCalendar(calendarName).
                    withSchedule(simpleSchedule().
                            withIntervalInMilliseconds(interval).
                            withRepeatCount(repeatCount).
                            withMisfireHandlingInstructionNowWithExistingCount()).
                    build();
        }

        else if(policy.equals(MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT))
        {
            trigger = newTrigger().withIdentity(triggerName, GROUP).
                    startAt(startDate).
                    modifiedByCalendar(calendarName).
                    withSchedule(simpleSchedule().
                            withIntervalInMilliseconds(interval).
                            withRepeatCount(repeatCount).
                            withMisfireHandlingInstructionNowWithRemainingCount()).
                    build();
        }
        else
        {
            trigger = newTrigger().withIdentity(triggerName, GROUP).
                    startAt(startDate).
                    endAt(endDate).
                    modifiedByCalendar(calendarName).
                    withSchedule(simpleSchedule().
                            withIntervalInMilliseconds(interval).
                            withRepeatCount(repeatCount)).
                    build();
        }

		return trigger;
	}

    protected Trigger createTrigger(ReportJobCalendarTrigger jobTrigger) {
		String triggerName = triggerName(jobTrigger);
		Date startDate = getStartDate(jobTrigger);
		Date endDate = getEndDate(jobTrigger);
        String calendarName = jobTrigger.getCalendarName();
		String cronExpression = getCronExpression(jobTrigger);
        Trigger trigger = null;
		try {
            // NOTE: We are NOT going to specify a MisfireHandlingInstruction - this allows the Quartz Scheduler
            // to use its default MISFIRE_INSTRUCTION_SMART_POLICY behavior - and for a calendar scheduled job,
            // the Quartz Scheduler will automatically reschedule this job to be run one time when a downed server
            // is restarted (and then continue with the calendar job schedule)...
            if(calendarJobMisfirePolicy.equals(MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY))
            {

                trigger = newTrigger().withIdentity(triggerName, GROUP).
                  startAt(startDate).
                  endAt(endDate).
                  modifiedByCalendar(calendarName).
                  withSchedule(cronSchedule(cronExpression).
                                inTimeZone(getTriggerTimeZone(jobTrigger)).
                                withMisfireHandlingInstructionIgnoreMisfires()).
                        build();

            }
            else if (calendarJobMisfirePolicy.equals(MISFIRE_INSTRUCTION_DO_NOTHING))
            {
                trigger = newTrigger().withIdentity(triggerName, GROUP).
                        startAt(startDate).
                        endAt(endDate).
                        modifiedByCalendar(calendarName).
                        withSchedule(cronSchedule(cronExpression).
                                inTimeZone(getTriggerTimeZone(jobTrigger)).
                                withMisfireHandlingInstructionDoNothing()).
                        build();
            }
            else if (calendarJobMisfirePolicy.equals(MISFIRE_INSTRUCTION_FIRE_ONCE_NOW))
            {
                trigger = newTrigger().withIdentity(triggerName, GROUP).
                        startAt(startDate).
                        endAt(endDate).
                        modifiedByCalendar(calendarName).
                        withSchedule(cronSchedule(cronExpression).
                                inTimeZone(getTriggerTimeZone(jobTrigger)).
                                withMisfireHandlingInstructionFireAndProceed()).
                        build();

            }
            else
            {
                trigger = newTrigger().withIdentity(triggerName, GROUP).
                        startAt(startDate).
                        endAt(endDate).
                        modifiedByCalendar(calendarName).
                        withSchedule(cronSchedule(cronExpression).
                          inTimeZone(getTriggerTimeZone(jobTrigger))).
                  build();

            }

			return trigger;
		} catch (Exception e) {
			log.error("Error creating Quartz Cron trigger", e);
			throw new JSExceptionWrapper(e);
		}
	}



	protected long getIntervalUnitCoefficient(ReportJobSimpleTrigger jobTrigger) {
		long coefficient;
		switch (jobTrigger.getRecurrenceIntervalUnit().byteValue()) {
		case ReportJobSimpleTrigger.INTERVAL_MINUTE:
			coefficient = COEFFICIENT_MINUTE;
			break;
		case ReportJobSimpleTrigger.INTERVAL_HOUR:
			coefficient = COEFFICIENT_HOUR;
			break;
		case ReportJobSimpleTrigger.INTERVAL_DAY:
			coefficient = COEFFICIENT_DAY;
			break;
		case ReportJobSimpleTrigger.INTERVAL_WEEK:
			coefficient = COEFFICIENT_WEEK;
			break;
		default:
			throw new JSException("jsexception.job.unknown.interval.unit", 
					new Object[] {jobTrigger.getRecurrenceIntervalUnit()});
		}
		return coefficient;
	}

	protected Date getEndDate(ReportJobTrigger jobTrigger) {
		return translateFromTriggerTimeZone(jobTrigger, jobTrigger.getEndDate());
	}
	
	protected Date translateFromTriggerTimeZone(ReportJobTrigger jobTrigger, Date date) {
		if (date != null) {
			TimeZone tz = getTriggerTimeZone(jobTrigger);
			if (tz != null) {
				date = DateBuilder.translateTime(date, TimeZone.getDefault(), tz);
                //date = TriggerUtils.translateTime(date, TimeZone.getDefault(), tz);
			}
		}
		return date;
	}

	protected Date getStartDate(ReportJobTrigger jobTrigger) {
		Date startDate;
		switch (jobTrigger.getStartType()) {
		case ReportJobTrigger.START_TYPE_NOW:
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			startDate = calendar.getTime();
			break;
		case ReportJobTrigger.START_TYPE_SCHEDULE:
			startDate = translateFromTriggerTimeZone(jobTrigger, jobTrigger.getStartDate());
			break;
		default:
			throw new JSException("jsexception.job.unknown.start.type", new Object[] {new Byte(jobTrigger.getStartType())});
		}
		return startDate;
	}

	protected int repeatCount(ReportJobSimpleTrigger jobTrigger) {
		int recurrenceCount = jobTrigger.getOccurrenceCount();
		int repeatCount;
		switch (recurrenceCount) {
		case ReportJobSimpleTrigger.RECUR_INDEFINITELY:
			repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
			break;
		default:
			repeatCount = recurrenceCount - 1;
			break;
		}
		return repeatCount;
	}

	protected TimeZone getTriggerTimeZone(ReportJobTrigger jobTrigger) {
		String tzId = jobTrigger.getTimezone();
		TimeZone tz;
		if (tzId == null || tzId.length() == 0) {
			tz = null;
		} else {
			tz = TimeZone.getTimeZone(tzId);
			if (tz == null) {
				String quotedTzId = "\"" + tzId + "\"";
				throw new JSException("jsexception.unknown.timezone", new Object[] {quotedTzId});
			}
		}
		return tz;
	}

	protected String getCronExpression(ReportJobCalendarTrigger jobTrigger) {
		String minutes = jobTrigger.getMinutes();
		String hours = jobTrigger.getHours();
		String weekDays;
		String monthDays;
		switch (jobTrigger.getDaysType()) {
		case ReportJobCalendarTrigger.DAYS_TYPE_ALL:
			weekDays = "?";
			monthDays = "*";
			break;
		case ReportJobCalendarTrigger.DAYS_TYPE_WEEK:
			weekDays = enumerateCronVals(jobTrigger.getWeekDays(), COUNT_WEEKDAYS);
			monthDays = "?";
			break;
		case ReportJobCalendarTrigger.DAYS_TYPE_MONTH:
			weekDays = "?";
			monthDays = jobTrigger.getMonthDays();
			break;
		default:
			throw new JSException("jsexception.job.unknown.calendar.trigger.days.type", new Object[] {new Byte(jobTrigger.getDaysType())});
		}
		String months = enumerateCronVals(jobTrigger.getMonths(), COUNT_MONTHS);
		
		StringBuffer cronExpression = new StringBuffer();
		cronExpression.append("0 ");
		cronExpression.append(minutes);
		cronExpression.append(' ');
		cronExpression.append(hours);
		cronExpression.append(' ');
		cronExpression.append(monthDays);
		cronExpression.append(' ');
		cronExpression.append(months);
		cronExpression.append(' ');
		cronExpression.append(weekDays);
		
		return cronExpression.toString();
	}
  
  protected int getMisfireCode(ReportJobTrigger jobTrigger) {
    int i = jobTrigger.getMisfireInstruction();

    //
    // 2012-03-09       thorick
    //
    // JS_MISFIRE_INSTRUCTION_NOT_SET is a special JS only code that means that
    //   the misfire instruction has NOT been set.
    // this unfortunate construct is required because the value of Quartz MISFIRE_INSTRUCTION_SMART_POLICY == 0
    // conflicts with the values will be placed into an upgraded JS ReportJobTrigger table which are also == 0.
    //
    //  sorry.
    //
    if (i == jobTrigger.JS_MISFIRE_INSTRUCTION_NOT_SET) return SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY;
    if (jobTrigger instanceof ReportJobSimpleTrigger) {
      if (i == ReportJobSimpleTrigger.JS_MISFIRE_INSTRUCTION_FIRE_NOW) return SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW;
      if (i == ReportJobSimpleTrigger.JS_MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT) return SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT;
      if (i == ReportJobSimpleTrigger.JS_MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT) return SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT;
      if (i == ReportJobSimpleTrigger.JS_MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT) return SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
      if (i == ReportJobSimpleTrigger.JS_MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT) return SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT;
    }
    else  if (jobTrigger instanceof ReportJobCalendarTrigger) {
      if (i == ReportJobCalendarTrigger.JS_MISFIRE_INSTRUCTION_DO_NOTHING) return (CalendarIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
      if (i == ReportJobCalendarTrigger.JS_MISFIRE_INSTRUCTION_FIRE_ONCE_NOW) return (CalendarIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
    }

    if (i == jobTrigger.JS_MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY) return SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY;
    if (i == jobTrigger.JS_MISFIRE_INSTRUCTION_SMART_POLICY) return SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY;
    throw  new JSException("Unhandled misfire instruction of value "+i);
  }

	protected String enumerateCronVals(SortedSet vals, int totalCount) {
		if (vals == null || vals.isEmpty()) {
			throw new JSException("jsexception.no.values.to.enumerate");
		}
		
		if (vals.size() == totalCount) {
			return "*";
		}
		
		StringBuffer enumStr = new StringBuffer();
		for (Iterator it = vals.iterator(); it.hasNext();) {
			Byte val = (Byte) it.next();
			enumStr.append(val.byteValue());
			enumStr.append(',');
		}
		return enumStr.substring(0, enumStr.length() - 1);
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void removeScheduledJob(ExecutionContext context, long jobId) {
        try {
			String jobName = jobName(jobId);
            if (scheduler.deleteJob(getJobKey(jobName))) {
				if (log.isDebugEnabled()) {
					log.debug("Job " + jobName + "deleted");
				}
			} else {
				log.info("Quartz job " + jobId + " was not found to be deleted");
			}
		} catch (SchedulerException e) {
			log.error("Error deleting Quartz job " + jobId, e);
			throw new JSExceptionWrapper(e);
		}
	}


	public ReportJobRuntimeInformation[] getJobsRuntimeInformation(ExecutionContext context, long[] jobIds) {
		if (jobIds == null) {
			return null;
		}
		
		try {
			Set executingJobNames = getExecutingJobNames();
			ReportJobRuntimeInformation[] infos = new ReportJobRuntimeInformation[jobIds.length];
			for (int i = 0; i < jobIds.length; i++) {
				infos[i] = getJobRuntimeInformation(jobIds[i], executingJobNames);
			}
			return infos;
		} catch (SchedulerException e) {
			log.error("Error while fetching Quartz runtime information", e);
			throw new JSExceptionWrapper(e);
		}
	}

    private Trigger reInitTrigger(long jobId) {
        try {
        ReportJob reportJob = persistenceService.loadJob(null, new ReportJobIdHolder(jobId));
        rescheduleJob(null, reportJob);
        Trigger trigger = getReportJobTrigger(jobId);
        return trigger;
        } catch (Exception ex) {
            log.error("Fail to re-init quartz trigger", ex);
        }
        return null;
    }


	protected ReportJobRuntimeInformation getJobRuntimeInformation(long jobId, Set executingJobNames) throws SchedulerException {
		ReportJobRuntimeInformation info = new ReportJobRuntimeInformation();
		Trigger trigger = getReportJobTrigger(jobId);
        if (trigger == null) trigger = reInitTrigger(jobId);
		if (trigger == null) {
 			info.setState(ReportJobRuntimeInformation.STATE_UNKNOWN);
		} else {
			info.setPreviousFireTime(trigger.getPreviousFireTime());
			if (trigger.mayFireAgain()) {
				info.setNextFireTime(trigger.getNextFireTime());
			}
			
			byte state = getJobState(trigger, executingJobNames);
			info.setState(state);
		}
		return info;
	}

	protected byte getJobState(Trigger trigger, Set executingJobNames) throws SchedulerException {
		byte state;
		Trigger.TriggerState quartzState = scheduler.getTriggerState(trigger.getKey());

        switch (quartzState) {
		case NORMAL:
		case BLOCKED:
			state = executingJobNames.contains(trigger.getJobKey().getName()) ?
					ReportJobRuntimeInformation.STATE_EXECUTING :
						ReportJobRuntimeInformation.STATE_NORMAL;
			break;
		case COMPLETE:
            state = executingJobNames.contains(trigger.getJobKey().getName()) ?
                    ReportJobRuntimeInformation.STATE_EXECUTING :
                    ReportJobRuntimeInformation.STATE_COMPLETE;
			break;
		case PAUSED:
			state = ReportJobRuntimeInformation.STATE_PAUSED;
			break;
		case ERROR:
			state = ReportJobRuntimeInformation.STATE_ERROR;
			break;
		default:
			state = ReportJobRuntimeInformation.STATE_UNKNOWN;
			break;
		}
		return state;
	}

	protected Set getExecutingJobNames() throws SchedulerException {
		List executingJobs = scheduler.getCurrentlyExecutingJobs();
		Set executingJobNames = new HashSet();
		for (Iterator iter = executingJobs.iterator(); iter.hasNext();) {
			JobExecutionContext executionContext = (JobExecutionContext) iter.next();
			JobDetail jobDetail = executionContext.getJobDetail();
            if (jobDetail.getKey().getGroup().equals(GROUP)) {
				executingJobNames.add(jobDetail.getKey().getName());
			}
		}
		return executingJobNames;
	}

    protected List<JobDetail> getAllJobsOfScheduler() throws SchedulerException {
        final List<JobDetail> result = new ArrayList<JobDetail>();
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(GROUP))) {
            final JobDetail jobDetail;
            try {
                jobDetail = scheduler.getJobDetail(jobKey);
                if (jobDetail != null) {
                    result.add(jobDetail);
                }
            } catch (final Exception e) {
            }
        }
        return result;
    }

    @Override
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return new DiagnosticAttributeBuilder()
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.TOTAL_SCHEDULED_JOBS, new DiagnosticCallback<Integer>() {
                    @Override
                    public Integer getDiagnosticAttributeValue() {
                        Integer totalScheduledJobs = 0;
                        try {
                            totalScheduledJobs = getAllJobsOfScheduler().size();
                        } catch (SchedulerException e) {
                            // Empty Body
                        }
                        return totalScheduledJobs;
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.TOTAL_RUNNING_JOBS, new DiagnosticCallback<Integer>() {
                    @Override
                    public Integer getDiagnosticAttributeValue() {
                        Integer totalRunningJobs = 0;
                        try {
                            totalRunningJobs = getExecutingJobNames().size();
                        } catch (SchedulerException e) {
                            // Empty Body
                        }
                        return totalRunningJobs;
                    }
                })
                .addDiagnosticAttribute(DiagnosticAttributeBuilder.RUNNING_JOBS_LIST, new DiagnosticCallback<Map<String, Map<String, Long>>>() {
                    @Override
                    public Map<String, Map<String, Long>> getDiagnosticAttributeValue() {
                        Map<String, Map<String, Long>> runningJobList = new HashMap<String, Map<String, Long>>();
                        try {
                            List<JobExecutionContext> jobExecutionContexts = scheduler.getCurrentlyExecutingJobs();
                            if (jobExecutionContexts != null && jobExecutionContexts.size() > 0) {
                                for (JobExecutionContext jobExecutionContext : jobExecutionContexts) {
                                    JobDetail jobDetail = jobExecutionContext.getJobDetail();
                                    if (jobDetail.getKey().getGroup().equals(GROUP)) {
                                        Map<String, Long> reportUrisWithTimeExecutions = new HashMap<String, Long>();
                                        Long executionTime = System.currentTimeMillis() - jobExecutionContext.getFireTime().getTime();
                                        reportUrisWithTimeExecutions.put(((ReportExecutionJob) (jobExecutionContext).getJobInstance()).reportUnit.getURI(), executionTime/1000);
                                        runningJobList.put(jobExecutionContext.getJobDetail().getKey().getName(), reportUrisWithTimeExecutions);
                                    }
                                }
                            }
                        } catch (SchedulerException e) {
                            // Empty Body
                        }
                        return runningJobList;
                    }
                }).build();
    }

    public void addReportSchedulerListener(ReportSchedulerListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public synchronized void removeReportSchedulerListener(ReportSchedulerListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

  public void pause(List<ReportJob> jobs, boolean all) {
    if (all)  {
      pauseById((List<ReportJobIdHolder>) null, all);
      return;
    }
    List<ReportJobIdHolder> ids = new ArrayList<ReportJobIdHolder>();
    for (ReportJob job : jobs) {
      ids.add(new ReportJobIdHolder(job.getId()));
    }
    pauseById(ids, all);
    return;
  }

  public void pauseById(List<ReportJobIdHolder> jobIds, boolean all) {
    boolean exception = false;
    StringBuilder sb = new StringBuilder();
    if (all) {
      try {
        //p("pause ALL");
        scheduler.pauseAll();
        //p("pause complete");
        return;
      }
      catch (SchedulerException e)  {
        sb.append("Exception while attempting to do 'pauseAll()' on Quartz Scheduler. ");
        throw new JSException(sb.toString(), e.getUnderlyingException());
      }
    }
    if (jobIds == null || jobIds.size() <= 0)  {
      return;
    }
    for (ReportJobIdHolder id : jobIds) {
      String jobName = jobName(id.getId());
      try {
        //p("pause job "+jobName);
        scheduler.pauseJob(getJobKey(jobName));
        sb.append("pause of ReportJob id='"+id+"' successful.");
      } catch (SchedulerException e) {
        exception = true;
        sb.append("Exception while attempting to do pause of ReportJob id='"+id+"' on Quartz Scheduler: ") ;
        sb.append(e.getMessage());
        sb.append("\n");
      }
    }
    //p("pause complete");
    if (exception)  {
      //p("pause encountered problems "+sb.toString());
      throw new JSException(sb.toString());
    }
  }


  public void resume(List<ReportJob> jobs, boolean all) {
    if (all)  {
      resumeById((List<ReportJobIdHolder>) null, all);
      return;
    }
    List<ReportJobIdHolder> ids = new ArrayList<ReportJobIdHolder>();
    for (ReportJob job : jobs) {
      ids.add(new ReportJobIdHolder(job.getId()));
    }
    resumeById(ids, all);
    return;
  }

  public void resumeById(List<ReportJobIdHolder> jobIds, boolean all) {
    boolean exception = false;
    StringBuilder sb = new StringBuilder();
    if (all) {
      try {
        //p("resume ALL");
        scheduler.resumeAll();
        return;
      }
      catch (SchedulerException e)  {
        sb.append("Exception while attempting to do 'resumeAll()' on Quartz Scheduler. ");
        throw new JSException(e.getUnderlyingException());
      }
    }
    if (jobIds == null || jobIds.size() <= 0)  {
      return;
    }
    for (ReportJobIdHolder id : jobIds) {
      String jobName = jobName(id.getId());
      try {
        //p("resume job "+jobName);
        scheduler.resumeJob(getJobKey(jobName));
        sb.append("resume of ReportJob id='"+id.getId()+"' successful.");
      } catch (SchedulerException e) {
        exception = true;
        sb.append("Exception while attempting to do resume of ReportJob id='"+id.getId()+"' on Quartz Scheduler: ") ;
        sb.append(e.getMessage());
        sb.append("\n");
      }
    }
    if (exception)  {
      throw new JSException(sb.toString());
    }
  }


	protected void notifyListenersOfFinalizedJob(long jobId) {
		synchronized (listeners) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				ReportSchedulerListener listener = (ReportSchedulerListener) it.next();
				listener.reportJobFinalized(jobId);
			}
		}
	}

	protected void reportTriggerFinalized(Trigger trigger) {
		long jobId = trigger.getJobDataMap().getLongValue(com.jaspersoft.jasperserver.api.engine.scheduling.quartz.ReportExecutionJob.JOB_DATA_KEY_DETAILS_ID);
        //long jobId = trigger.getJobDataMap().getLong(ReportExecutionJob.JOB_DATA_KEY_DETAILS_ID);
		notifyListenersOfFinalizedJob(jobId);
	}

    
    // convenience method to get a Quartz JobKey from JasperServer Object
    protected JobKey getJobKey(ReportJob job) {
      return getJobKey(jobName(job.getId()));
    }

    protected JobKey getJobKey(String jobName) {
      return new JobKey(jobName, GROUP);
    }

    protected TriggerKey getTriggerKey(ReportJobTrigger jobTrigger) {
      return new TriggerKey(triggerName(jobTrigger), GROUP);
    }

    public void addCalendar(String calName, org.quartz.Calendar calendar, boolean replace, boolean updateTriggers) throws JSException {
        try {
            scheduler.addCalendar(calName, calendar, replace, updateTriggers);
        } catch (Exception ex) {
            throw new JSException(ex);
        }
    }

    public boolean deleteCalendar(java.lang.String calName) throws JSException {
        try {
            return scheduler.deleteCalendar(calName);
        } catch (Exception ex) {
            throw new JSException(ex);
        }
    }

    public org.quartz.Calendar getCalendar(java.lang.String calName) throws JSException {
        try {
            return scheduler.getCalendar(calName);
        } catch (Exception ex) {
            throw new JSException(ex);
        }
    }

    public List<String> getCalendarNames() throws JSException {
        try {
            return scheduler.getCalendarNames();
        } catch (Exception ex) {
            throw new JSException(ex);
        }
    }

    protected class ReportSchedulerQuartzListener implements SchedulerListener {

        public ReportSchedulerQuartzListener() {
        }


        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
         * is unscheduled.
         * </p>
         *
         * @see SchedulerListener#schedulingDataCleared()
         */
        public void jobUnscheduled(TriggerKey triggerKey)  {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job with triggerKey: " + triggerKey + " unscheduled");
          }
        }


        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
         * has been paused.
         * </p>
         */
        public void triggerPaused(TriggerKey triggerKey)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job trigger" + triggerKey + " paused");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a
         * group of <code>{@link Trigger}s</code> has been paused.
         * </p>
         * <p/>
         * <p>If all groups were paused then triggerGroup will be null</p>
         *
         * @param triggerGroup the paused group, or null if all were paused
         */
        public void triggersPaused(String triggerGroup)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job trigger group " + triggerGroup + " paused ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
         * has been un-paused.
         * </p>
         */
        public void triggerResumed(TriggerKey triggerKey)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job trigger " + triggerKey + " resumed ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a
         * group of <code>{@link Trigger}s</code> has been un-paused.
         * </p>
         */
        public void triggersResumed(String triggerGroup)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job trigger group" + triggerGroup + " resumed ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
         * has been added.
         * </p>
         */
        public void jobAdded(JobDetail jobDetail)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job " + jobDetail.getKey() + " added. ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
         * has been deleted.
         * </p>
         */
        public void jobDeleted(JobKey jobKey)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job " + jobKey + " deleted ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
         * has been paused.
         * </p>
         */
        public void jobPaused(JobKey jobKey)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job " + jobKey + " paused ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a
         * group of <code>{@link org.quartz.JobDetail}s</code> has been paused.
         * </p>
         *
         * @param jobGroup the paused group, or null if all were paused
         */
        public void jobsPaused(String jobGroup)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job Group " + jobGroup + " paused ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a <code>{@link org.quartz.JobDetail}</code>
         * has been un-paused.
         * </p>
         */
        public void jobResumed(JobKey jobKey)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job " + jobKey + " resumed  ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> when a
         * group of <code>{@link org.quartz.JobDetail}s</code> has been un-paused.
         * </p>
         */
        public void jobsResumed(String jobGroup)   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz job  Group " + jobGroup +" resumed  ");
          }
        }


        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> to inform the listener
         * that it has move to standby mode.
         * </p>
         */
        public void schedulerInStandbyMode()   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz Scheduler in standby mode ");
          }
        }

        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> to inform the listener
         * that it has started.
         * </p>
         */
        public void schedulerStarted()   {
          if (log.isDebugEnabled()) {
            log.debug("Quartz Scheduler started");
          }
        }


        /**
         * <p>
         * Called by the <code>{@link Scheduler}</code> to inform the listener
         * that it has begun the shutdown sequence.
         * </p>
         */
        public void schedulerShuttingdown()  {
          if (log.isDebugEnabled()) {
            log.debug("Quartz Scheduler shutting down");
          }
        }

        /**
         * Called by the <code>{@link Scheduler}</code> to inform the listener
         * that all jobs, triggers and calendars were deleted.
         */
        public void schedulingDataCleared() {
          if (log.isDebugEnabled()) {
            log.debug("Quartz Scheduler Data Cleared");
          }
        }


        public void jobScheduled(Trigger trigger) {
            if (log.isDebugEnabled()) {
                log.debug("Quartz job " + trigger.getKey() + " scheduled by trigger " + trigger.getKey());
            }
        }

        public void jobUnscheduled(String name, String group) {
            if (log.isDebugEnabled()) {
                log.debug("Quartz job unscheduled " + group + "." + name);
            }
        }

        public void triggerFinalized(Trigger trigger) {
            if (log.isDebugEnabled()) {
                log.debug("Quartz trigger finalized " + trigger.getKey());
            }

            if (trigger.getKey().getGroup().equals(GROUP)) {
                reportTriggerFinalized(trigger);
            }
        }
                                    

        public void schedulerError(String msg, SchedulerException cause) {
            if (log.isInfoEnabled()) {
                log.info("Quartz scheduler error: " + msg, cause);
            }
        }

        public void schedulerShutdown() {
            if (log.isInfoEnabled()) {
                log.info("Quartz scheduler shutdown");
            }
        }

    }


    protected class ReportSchedulerTriggerListener implements TriggerListener {

		private final String name;

		public ReportSchedulerTriggerListener(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}


		public void triggerFired(Trigger trigger, JobExecutionContext context) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz trigger fired " + trigger.getKey());
			}
		}

		public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
			return false;
		}

		public void triggerMisfired(Trigger trigger) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz trigger misfired " + trigger.getKey());
			}
			
			if (trigger.getKey().getGroup().equals(GROUP) && trigger.getFireTimeAfter(new Date()) == null) {
				// TODO SteveRosen - we may not need this logic here now that Quartz is handling all misfirings...
				// reportTriggerFinalized(trigger);
			}
		}

		public void triggerComplete(Trigger trigger, JobExecutionContext context,
                                    Trigger.CompletedExecutionInstruction triggerInstructionCode) {
			if (log.isDebugEnabled()) {
				log.debug("Quartz trigger complete " + trigger.getKey() + " triggerInstructionCode=" + triggerInstructionCode);
			}
		}

	}


	public void validate(ReportJob job, ValidationErrors errors) {
		Trigger quartzTrigger = createTrigger(job);

    // this method is intended for use by the Quartz Scheduler and is not meant for public use
    //   but that's OK, we're just testing the veracity of the Trigger
    AbstractTrigger abstrTrigger = (AbstractTrigger) quartzTrigger;
    Date firstFireTime = abstrTrigger.computeFirstFireTime(null);
		if (firstFireTime == null) {
			errors.add(new ValidationErrorImpl("error.report.job.trigger.no.fire", null, null, "trigger"));
		}
	}
  
  private void p(String s) {
    System.err.println(this.getClass().getName()+" - "+s);
  }

}
