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
package com.jaspersoft.jasperserver.api.engine.scheduling.hibernate;

import java.sql.Timestamp;
import java.util.Date;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class PersistentReportJobTrigger {

	public static final byte START_TYPE_NOW = 1;

	private long id;
	private int version;
	private String timezone;
	private byte startType;
	private Timestamp startDate;
	private Timestamp endDate;
  private String calendarName;
  private int misfireInstruction;
	
	public PersistentReportJobTrigger() {
		version = ReportJob.VERSION_NEW;
	}

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

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public byte getStartType() {
		return startType;
	}

	public void setStartType(byte startType) {
		this.startType = startType;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

  public int getMisfireInstruction() {
    //
    // 2012-03-07 thorick:  note the ALTER TABLE SQL that will add this column to the upgraded DBs
    //                      and it looks like these new columns will the value '0'.
    //                      '0' happens to be the Quartz value of MISFIRE_INSTRUCTION_SMART_POLICY
    //                      so we have to work around this, unfortunately.

    return misfireInstruction;
  }
          
  public void setMisfireInstruction(int i) {
    this.misfireInstruction = i;
  }
         
	public void copyFrom(ReportJobTrigger trigger) {
        setCalendarName(trigger.getCalendarName());
		setTimezone(trigger.getTimezone());
		setStartType(trigger.getStartType());
		setStartDate(toTimestamp(trigger.getStartDate()));
		setEndDate(toTimestamp(trigger.getEndDate()));

        setMisfireInstruction(trigger.getMisfireInstruction());
	}

    public abstract void copyFromModel(ReportJobTrigger trigger);

	protected Timestamp toTimestamp(Date date) {
		return date == null ? null : new Timestamp(date.getTime());
	}

	public abstract ReportJobTrigger toClient();

	protected final void copyTo(ReportJobTrigger trigger) {
		trigger.setId(getId());
		trigger.setVersion(getVersion());
		trigger.setTimezone(getTimezone());
		trigger.setStartType(getStartType());
		trigger.setStartDate(getStartDate());
		trigger.setEndDate(getEndDate());
    trigger.setCalendarName((getCalendarName()));
    trigger.setMisfireInstruction(getMisfireInstruction());
	}

	public boolean isNew() {
		return getVersion() == ReportJob.VERSION_NEW;
	}
	
	public abstract boolean supports(Class triggerClass);

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }
}
