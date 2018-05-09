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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class QuartzSchedulerControl implements ApplicationContextAware, ApplicationListener {
	
	private static final Log log = LogFactory.getLog(QuartzSchedulerControl.class);
	
	private Scheduler scheduler;

    private ApplicationContext applicationContext;

    private static boolean schedulerStarted = false;

	public Scheduler getScheduler() {
		return scheduler;
	}

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void start() {
        log.debug("QuartzSchedulerControl => start() just got called - will DELAY the REAL START of the Quartz Scheduler until later...");
        log.debug("QuartzSchedulerControl => start() schedulerStarted = " + schedulerStarted);
	}

    // We made this class an ApplicationListener so it can discover at bootup time when a ContextRefreshedEvent has
    // been generated - this is the safe moment to then start the Quartz scheduler, since the Jasperserver environment
    // will be properly initialized at that point - otherwise, the Quartz scheduler might be trying to start a scheduled
    // job that was MISFIRED (due to the server being down, for example) - and this will lead to unexpected results
    // (including crashes because various important beans have not been fully initialized yet)...
    public void onApplicationEvent(ApplicationEvent event)  {
        if (event instanceof ContextRefreshedEvent)  {
            log.debug("QuartzSchedulerControl => Got a ContextRefreshedEvent event !!!!!");
            try {
                //if scheduler is in standby mode and current context in not export-import context then scheduler will started
                //otherwise scheduler will not be started because job during import-export can't be executed.
                if (getScheduler().isInStandbyMode() && !applicationContext.containsBean("importCommandMetadata")) {
                    if( !schedulerStarted )
                    {
                        getScheduler().start();
                        schedulerStarted = true;
                        log.debug("QuartzSchedulerControl => start() STARTED the Quartz scheduler, schedulerStarted = " + schedulerStarted);
                    }
                } else if (!getScheduler().isInStandbyMode()) {
                    if (log.isDebugEnabled()) {
                        log.debug("QuartzSchedulerControl => Scheduler already running.");
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("QuartzSchedulerControl => Scheduler can't be started in the scope of import-export tool.");
                    }
                }
            } catch (SchedulerException e) {
                log.error("QuartzSchedulerControl => Error starting the scheduler", e);
                throw new JSExceptionWrapper(e);
            }
        }
    }
}
