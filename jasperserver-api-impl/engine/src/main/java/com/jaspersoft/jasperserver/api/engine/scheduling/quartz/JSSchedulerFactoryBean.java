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
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.ThreadExecutor;
//import org.springframework.core.task.TaskExecutor;
import java.util.concurrent.Executor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JSSchedulerFactoryBean extends SchedulerFactoryBean {

	private static final Log log = LogFactory.getLog(JSSchedulerFactoryBean.class);
	
	private ThreadExecutor ThreadExecutor;
	
	public JSSchedulerFactoryBean() {
		setSchedulerFactoryClass(JSSchedulerFactory.class);
	}
	
	protected Scheduler createScheduler(SchedulerFactory schedulerFactory, 
			String schedulerName)
			throws SchedulerException {
		try {
			if (ThreadExecutor != null) {
				if (!(schedulerFactory instanceof JSSchedulerFactory)) {
					throw new RuntimeException(
							"A JSSchedulerFactory scheduler factory is required");
				}
				
				LocalThreadExecutor.setLocalThreadExecutor(ThreadExecutor);
				
				JSSchedulerFactory factory = (JSSchedulerFactory) schedulerFactory;
				factory.getInitProps().setProperty(StdSchedulerFactory.PROP_THREAD_EXECUTOR_CLASS, 
						LocalThreadExecutor.class.getName());
                //factory.getInitProps().setProperty(StdSchedulerFactory.PROP_THREAD_RUNNER_CLASS,
				//		LocalThreadExecutor.class.getName());
				factory.reinit();
			}
			
			return super.createScheduler(schedulerFactory, schedulerName);
		} finally {
			if (ThreadExecutor != null) {
				LocalThreadExecutor.setLocalThreadExecutor(null);
			}
		}
	}

	public ThreadExecutor getThreadExecutor() {
		return ThreadExecutor;
	}

	public void setThreadExecutor(ThreadExecutor ThreadExecutor) {
		if (ThreadExecutor instanceof NullThreadExecutor) {
			ThreadExecutor = null;
		}
			
		this.ThreadExecutor = ThreadExecutor;
	}

    // 2012-01-24 thorick: The arg seems to have changed:
    //                      Spring 2:  org.springframework.core.task.TaskExecutor
    //                      Spring 3:  java.util.concurrent.Executor

	public void setTaskExecutor(Executor taskExecutor) {
		if (taskExecutor instanceof NullTaskExecutor) {
			taskExecutor = null;
		}
		
		super.setTaskExecutor(taskExecutor);
	}
    /*
    public void setTaskExecutor(TaskExecutor taskExecutor) {
		if (taskExecutor instanceof NullTaskExecutor) {
			taskExecutor = null;
		}

		super.setTaskExecutor(taskExecutor);
	}
	*/

}
