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

import org.quartz.spi.ThreadExecutor;

public class LocalThreadExecutor implements ThreadExecutor {

	private static final ThreadLocal localThreadExecutor = new ThreadLocal();
	
	public static void setLocalThreadExecutor(ThreadExecutor ThreadExecutor) {
		localThreadExecutor.set(ThreadExecutor);
	}
	
	private final ThreadExecutor ThreadExecutor;
	
	public LocalThreadExecutor() {
		this.ThreadExecutor = (ThreadExecutor) localThreadExecutor.get();
		if (this.ThreadExecutor == null) {
			throw new RuntimeException("Internal error: No local thread executor set");
		}
	}

	public void initialize() {
		ThreadExecutor.initialize();
	}
	
	public void execute(Thread thread) {
		ThreadExecutor.execute(thread);
	}

}
