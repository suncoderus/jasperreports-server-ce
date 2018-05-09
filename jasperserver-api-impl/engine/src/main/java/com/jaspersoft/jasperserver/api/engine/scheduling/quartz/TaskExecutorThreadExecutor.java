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
import org.quartz.spi.ThreadExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.SchedulingAwareRunnable;

public class TaskExecutorThreadExecutor implements ThreadExecutor {

	private static final Log log = LogFactory.getLog(TaskExecutorThreadExecutor.class);
	
	private TaskExecutor taskExecutor;

	public void initialize() {
		// NOP
	}
	
	public void execute(final Thread thread) {
		taskExecutor.execute(new SchedulingAwareRunnable() {
			public boolean isLongLived() {
				return true;
			}

			public void run() {
				thread.run();
			}
		});
	}

	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

}
