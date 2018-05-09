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
package com.jaspersoft.jasperserver.api.engine.common.service;

import java.util.List;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.LogEvent;

/**
 * A service interface used for persistent logging of scheduled reports.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface LoggingService {
	/**
	 * Create an instance of a LogEvent which can be passed to the other LoggingService calls
	 * @return new LogEvent
	 */
	LogEvent instantiateLogEvent();

	/**
	 * Persist an event
	 * @param event event to store
	 */
	void log(LogEvent event);

	/**
	 * Update fields on an existing event
	 * @param event existing event to update
	 */
	void update(LogEvent event);

	/**
	 * Find events logged for the User object in the current SecurityContext.
	 * @param context locale/timezone information
	 * @return event list
	 */
	List getUserEvents(ExecutionContext context);

	/**
	 * Find events logged for the User object in the current SecurityContext
	 * which have not been retrieved yet.
	 * @param context locale/timezone information
	 * @return event list
	 */
	List getUnreadEvents(ExecutionContext context);

	/**
	 * Count the events logged for the User object in the current SecurityContext.
	 * @param context locale/timezone information
	 * @return number of events
	 */
	public int getUserEventsCount(ExecutionContext context);

	/**
	 * Find the event with a particular id value
	 * @param context locale/timezone information
	 * @param id id of LogEvent
	 * @return matching event (if any)
	 */
	LogEvent getLogEvent(ExecutionContext context, long id);
	
	/**
	 * Get the maximum event age in days.
	 * 
	 * @return the maximum event age in days
	 * @see #setMaximumAge(int)
	 */
	int getMaximumAge();
	
	
	/**
	 * Set the maximum event age in days.
	 * <p>
	 * The event is guaranteed to be kept in the log for at least this number of days.
	 * After this period the event will be eligible for {@link #purge() purge}.
	 * 
	 * @param days the maximum event age in days
	 */
	void setMaximumAge(int days);
	
	
	/**
	 * Purges events that surpassed the {@link #getMaximumAge() maximum age}.
	 * 
	 * @see #setMaximumAge(int)
	 */
	void purge();
	
	/**
	 * delete events matching a list of id's
	 * 
	 * @param context locale/timezone information
	 * @param events array of LogEvent id's
	 */
	void delete(ExecutionContext context, long[] events);
	
}
