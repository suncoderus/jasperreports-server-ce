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

package com.jaspersoft.jasperserver.api.engine.scheduling.service;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JasperServerAPI;

/**
 * Exception type used when a report job is not found for a specified ID.
 * 
 * @author Ivan Chan
 * @version $Id$
 * @since 4.7
 * @see com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob#getId()
 */
@JasperServerAPI
public class TriggerTypeMismatchException extends JSException {

	private final long jobId;

	private TRIGGER_TYPE type = TRIGGER_TYPE.SIMPLE_TRIGGER;

	public static enum TRIGGER_TYPE {
        SIMPLE_TRIGGER("simple trigger"),
        CALENDAR_TRIGGER("calendar trigger");

		TRIGGER_TYPE(String typeName) {
			this.typeName = typeName;
		}

		private String typeName;

		public String getTypeName() {
			return typeName;
		}
	}

	/**
	 * Creates a report job not found exception.
	 *
	 * @param jobId the ID fow which finding a job failed
	 */
	public TriggerTypeMismatchException(long jobId, TRIGGER_TYPE type) {
		super("jsexception.trigger.type.mismatch: expect " + (type != TRIGGER_TYPE.SIMPLE_TRIGGER ? TRIGGER_TYPE.SIMPLE_TRIGGER.getTypeName() : TRIGGER_TYPE.CALENDAR_TRIGGER.getTypeName()) +
                " for report job ID " + jobId + " [found " + type.getTypeName() + "]"
                , new Object[] {"job Id: " + new Long(jobId),
						"actual type: " + type.getTypeName(),
						"expected type: " + (type != TRIGGER_TYPE.SIMPLE_TRIGGER ? TRIGGER_TYPE.SIMPLE_TRIGGER.getTypeName() : TRIGGER_TYPE.CALENDAR_TRIGGER.getTypeName())});
		this.jobId = jobId;
        this.type = type;
	}

	/**
	 * Returns the ID for which a job was not found.
	 *
	 * @return the ID for which a job was not found
	 */
	public long getJobId() {
		return jobId;
	}

}
