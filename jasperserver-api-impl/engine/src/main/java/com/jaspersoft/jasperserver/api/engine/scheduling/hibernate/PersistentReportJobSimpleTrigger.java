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

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSimpleTriggerModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PersistentReportJobSimpleTrigger extends PersistentReportJobTrigger {

	private int occurrenceCount;
	private Integer recurrenceInterval;
	private Byte recurrenceIntervalUnit;
	
	public PersistentReportJobSimpleTrigger() {
	}

	public int getOccurrenceCount() {
		return occurrenceCount;
	}

	public void setOccurrenceCount(int recurrenceCount) {
		this.occurrenceCount = recurrenceCount;
	}

	public Byte getRecurrenceIntervalUnit() {
		return recurrenceIntervalUnit;
	}

	public void setRecurrenceIntervalUnit(Byte recurrenceInterval) {
		this.recurrenceIntervalUnit = recurrenceInterval;
	}

	public Integer getRecurrenceInterval() {
		return recurrenceInterval;
	}

	public void setRecurrenceInterval(Integer recurrenceInterval) {
		this.recurrenceInterval = recurrenceInterval;
	}

	public void copyFrom(ReportJobTrigger trigger) {
		super.copyFrom(trigger);

		ReportJobSimpleTrigger simpleTrigger = (ReportJobSimpleTrigger) trigger;
		setOccurrenceCount(simpleTrigger.getOccurrenceCount());
		setRecurrenceInterval(simpleTrigger.getRecurrenceInterval());
		setRecurrenceIntervalUnit(simpleTrigger.getRecurrenceIntervalUnit());
	}

    public void copyFromModel(ReportJobTrigger trigger) {
		ReportJobSimpleTriggerModel simpleTrigger = (ReportJobSimpleTriggerModel) trigger;
        if (simpleTrigger.isCalendarNameModified()) setCalendarName(trigger.getCalendarName());
        if (simpleTrigger.isTimezoneModified()) setTimezone(trigger.getTimezone());
		if (simpleTrigger.isStartTypeModified()){
			byte startType = trigger.getStartType();
			setStartType(startType);
			if (startType == START_TYPE_NOW) {
				setStartDate(null);
				setEndDate(null);
			} else {
				if (simpleTrigger.isStartDateModified()) setStartDate(toTimestamp(trigger.getStartDate()));
				if (simpleTrigger.isEndDateModified()) setEndDate(toTimestamp(trigger.getEndDate()));
			}
		}


		if (simpleTrigger.isOccurrenceCountModified()) setOccurrenceCount(simpleTrigger.getOccurrenceCount());
		if (simpleTrigger.isRecurrenceIntervalModified()) setRecurrenceInterval(simpleTrigger.getRecurrenceInterval());
		if (simpleTrigger.isRecurrenceIntervalUnitModified()) setRecurrenceIntervalUnit(simpleTrigger.getRecurrenceIntervalUnit());
    if (simpleTrigger.isMisfireInstructionModified()) setMisfireInstruction(simpleTrigger.getMisfireInstruction());
	}


	public ReportJobTrigger toClient() {
		ReportJobSimpleTrigger trigger = new ReportJobSimpleTrigger();
		super.copyTo(trigger);
		trigger.setOccurrenceCount(getOccurrenceCount());
		trigger.setRecurrenceInterval(getRecurrenceInterval());
		trigger.setRecurrenceIntervalUnit(getRecurrenceIntervalUnit());
		return trigger;
	}

	public boolean supports(Class triggerClass) {
		return ReportJobSimpleTrigger.class.isAssignableFrom(triggerClass) || ReportJobSimpleTriggerModel.class.isAssignableFrom(triggerClass);
	}
	
}
