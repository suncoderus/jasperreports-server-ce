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
package com.jaspersoft.jasperserver.export.modules.scheduling.beans;

import java.util.Date;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;


/**
 * @author tkavanagh
 * @version $Id$
 */
public abstract class ReportJobTriggerBean {

	private long id;
	private int version;
	private String timezone;
	private byte startType;
	private Date startDate;
	private Date endDate;
    private int misfireInstruction =  ReportJobTrigger.JS_MISFIRE_INSTRUCTION_NOT_SET;
	
	public void copyFrom(ReportJobTrigger trigger) {
		setId(trigger.getId());
		setVersion(trigger.getVersion());
		setTimezone(trigger.getTimezone());
		setStartType(trigger.getStartType());
		setStartDate(trigger.getStartDate());
		setEndDate(trigger.getEndDate());
        setMisfireInstruction(trigger.getMisfireInstruction());
	}
	
	public void copyTo(ReportJobTrigger trigger) {
		trigger.setTimezone(getTimezone());
		trigger.setStartType(getStartType());
		trigger.setStartDate(getStartDate());
		trigger.setEndDate(getEndDate());
        trigger.setMisfireInstruction(getMisfireInstruction());
	}
	
	public abstract ReportJobTrigger toJobTrigger();
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	public byte getStartType() {
		return startType;
	}
	
	public void setStartType(byte startType) {
		this.startType = startType;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

    public int getMisfireInstruction() {
        return misfireInstruction;
    }

    public void setMisfireInstruction(int misfireInstruction) {
        this.misfireInstruction = misfireInstruction;
    }

}
