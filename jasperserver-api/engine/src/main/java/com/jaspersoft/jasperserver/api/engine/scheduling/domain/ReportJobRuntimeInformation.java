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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import java.io.Serializable;
import java.util.Date;

/**
 * Execution runtime information provided by the scheduled for active jobs.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 * @see ReportJobSummary#getRuntimeInformation()
 */
@JasperServerAPI
public class ReportJobRuntimeInformation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Job execution state that indicates that the trigger for the job was not
	 * found or was in an unknown state.
	 * 
	 * @see #getState()
	 */
	public static final byte STATE_UNKNOWN = 0;
	
	/**
	 * Job execution state that indicates that the trigger for the job is
	 * waiting for the next execution.
	 * 
	 * @see #getState()
	 */
	public static final byte STATE_NORMAL = 1;
	
	/**
	 * Job execution state that indicates that the job is currently running.
	 * 
	 * @see #getState()
	 */
	public static final byte STATE_EXECUTING = 2;
	
	/**
	 * Job execution state that indicates that the trigger for the job has
	 * been paused in the scheduler.
	 * 
	 * @see #getState()
	 */
	public static final byte STATE_PAUSED = 3;
	
	/**
	 * Job execution state that indicates that the job has no remaining
	 * executions.
	 * 
	 * @see #getState()
	 */
	public static final byte STATE_COMPLETE = 4;
	
	/**
	 * Job execution state that indicates that the scheduler has encountered
	 * a system error when attempting to execute the job.
	 * 
	 * @see #getState()
	 */
	public static final byte STATE_ERROR = 5;
	
	private Byte state;
	private Date previousFireTime;
	private Date nextFireTime;
	
	/**
	 * Creates an empty object.
	 */
	public ReportJobRuntimeInformation() {
	}

	/**
	 * Returns the next job fire time, or <code>null</code> if the job is at the
	 * last execution.
	 * 
	 * @return the next job fire time
	 */
	public Date getNextFireTime() {
		return nextFireTime;
	}

	/**
	 * Sets the job next fire time.
	 * 
	 * @param nextFireTime the next fire time for the job
	 */
	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	/**
	 * Returns the previous job fire time, or <code>null</code> if the job
	 * hasn't been yet executed.
	 * 
	 * @return the job previous fire time
	 */
	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	/**
	 * Sets the job previous fire time.
	 * 
	 * @param previousFireTime the previous fire time of the job
	 */
	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	/**
	 * Returns the execution state of the job trigger.
	 * 
	 * @return one of the <code>STATE_*</code> constants
	 */
	public Byte getStateCode() {
		return state;
	}

	/**
	 * Returns the execution state of the job trigger.
	 *
	 * @return one of the <code>STATE_*</code> constants
     * @deprecated use #getStateCode() instead
	*/
	public byte getState() {
		return getStateCode();
	}

	/**
	 * Sets the execution state of the job trigger.
	 * 
	 * @param state one of the <code>STATE_*</code> constants
     * @deprecated use #setStateCode(Byte state) instead
	 */
	public void setState(byte state) {
		setStateCode(state);
	}

	/**
	 * Sets the execution state of the job trigger.
	 *
	 * @param state one of the <code>STATE_*</code> constants
	 */
	public void setStateCode(Byte state) {
		this.state = state;
	}
	
}
