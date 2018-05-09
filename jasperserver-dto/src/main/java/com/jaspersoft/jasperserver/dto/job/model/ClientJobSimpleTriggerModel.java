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

package com.jaspersoft.jasperserver.dto.job.model;

import com.jaspersoft.jasperserver.dto.job.ClientIntervalUnitType;
import com.jaspersoft.jasperserver.dto.job.ClientJobSimpleTrigger;
import java.util.Date;

/**
 * Job trigger model which fires at fixed time intervals.
 * Model is used in search/ update only.
 * <p/>
 * <p>
 * Such triggers can be used for jobs that need inFolder fire only once at a specified
 * moment, or for jobs that need inFolder fire several times at fixed intervals.
 * The intervals can be specified in minutes, hours, days (equivalent inFolder 24 hours)
 * and weeks (equivalend inFolder 7 days).
 * </p>
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 * @since 4.7
 */
public class ClientJobSimpleTriggerModel extends ClientJobSimpleTrigger {

    private boolean isOccurrenceCountModified = false;
    private boolean isRecurrenceIntervalUnitModified = false;
    private boolean isRecurrenceIntervalModified = false;
    private boolean isStartDateModified = false;
    private boolean isStartTypeModified = false;
    private boolean isEndDateModified = false;
    private boolean isTimezoneModified = false;
    private boolean isCalendarNameModified = false;
    private boolean isMisfireInstructionModified = false;

    /**
     * Create an empty simple job trigger;
     */
    public ClientJobSimpleTriggerModel() {
        super();
    }

    public ClientJobSimpleTriggerModel(ClientJobSimpleTriggerModel other) {
        super(other);
        this.isCalendarNameModified = other.isCalendarNameModified;
        this.isEndDateModified = other.isEndDateModified;
        this.isMisfireInstructionModified = other.isMisfireInstructionModified;
        this.isOccurrenceCountModified = other.isOccurrenceCountModified;
        this.isRecurrenceIntervalModified = other.isRecurrenceIntervalModified;
        this.isRecurrenceIntervalUnitModified = other.isRecurrenceIntervalUnitModified;
        this.isStartDateModified = other.isStartDateModified;
        this.isStartTypeModified = other.isStartTypeModified;
        this.isTimezoneModified = other.isTimezoneModified;
    }

    public boolean isCalendarNameModified() {
        return isCalendarNameModified;
    }

    public boolean isEndDateModified() {
        return isEndDateModified;
    }

    public boolean isMisfireInstructionModified() {
        return isMisfireInstructionModified;
    }

    public boolean isOccurrenceCountModified() {
        return isOccurrenceCountModified;
    }

    public boolean isRecurrenceIntervalModified() {
        return isRecurrenceIntervalModified;
    }

    public boolean isRecurrenceIntervalUnitModified() {
        return isRecurrenceIntervalUnitModified;
    }

    public boolean isStartDateModified() {
        return isStartDateModified;
    }

    public boolean isStartTypeModified() {
        return isStartTypeModified;
    }

    public boolean isTimezoneModified() {
        return isTimezoneModified;
    }

    @Override
    public ClientJobSimpleTriggerModel setOccurrenceCount(Integer occurrenceCount) {
        super.setOccurrenceCount(occurrenceCount);
        isOccurrenceCountModified = true;
        return this;
    }

    @Override
    public ClientJobSimpleTriggerModel setRecurrenceInterval(Integer recurrenceInterval) {
        super.setRecurrenceInterval(recurrenceInterval);
        isRecurrenceIntervalModified = true;
        return this;
    }

    @Override
    public ClientJobSimpleTriggerModel setRecurrenceIntervalUnit(ClientIntervalUnitType recurrenceIntervalUnit) {
        super.setRecurrenceIntervalUnit(recurrenceIntervalUnit);
        isRecurrenceIntervalUnitModified = true;
        return this;
    }

    @Override
    public ClientJobSimpleTriggerModel setStartDate(Date startDate) {
        super.setStartDate(startDate);
        isStartDateModified = true;
        return this;
    }

    @Override
    public ClientJobSimpleTriggerModel setStartType(int startType) {
        super.setStartType(startType);
        isStartTypeModified = true;
        return this;
    }

    @Override
    public ClientJobSimpleTriggerModel setEndDate(Date endDate) {
        super.setEndDate(endDate);
        isEndDateModified = true;
        return this;
    }

    @Override
    public ClientJobSimpleTriggerModel setTimezone(String timezone) {
        super.setTimezone(timezone);
        isTimezoneModified = true;
        return this;
    }

    @Override
    public ClientJobSimpleTriggerModel setCalendarName(String calendarName) {
        super.setCalendarName(calendarName);
        isCalendarNameModified = true;
        return this;
    }

    @Override
    public ClientJobSimpleTriggerModel setMisfireInstruction(Integer misfireInstruction) {
        super.setMisfireInstruction(misfireInstruction);
        isMisfireInstructionModified = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobSimpleTriggerModel)) return false;
        if (!super.equals(o)) return false;

        ClientJobSimpleTriggerModel that = (ClientJobSimpleTriggerModel) o;

        if (isOccurrenceCountModified() != that.isOccurrenceCountModified()) return false;
        if (isRecurrenceIntervalUnitModified() != that.isRecurrenceIntervalUnitModified()) return false;
        if (isRecurrenceIntervalModified() != that.isRecurrenceIntervalModified()) return false;
        if (isStartDateModified() != that.isStartDateModified()) return false;
        if (isStartTypeModified() != that.isStartTypeModified()) return false;
        if (isEndDateModified() != that.isEndDateModified()) return false;
        if (isTimezoneModified() != that.isTimezoneModified()) return false;
        if (isCalendarNameModified() != that.isCalendarNameModified()) return false;
        return isMisfireInstructionModified() == that.isMisfireInstructionModified();

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isOccurrenceCountModified() ? 1 : 0);
        result = 31 * result + (isRecurrenceIntervalUnitModified() ? 1 : 0);
        result = 31 * result + (isRecurrenceIntervalModified() ? 1 : 0);
        result = 31 * result + (isStartDateModified() ? 1 : 0);
        result = 31 * result + (isStartTypeModified() ? 1 : 0);
        result = 31 * result + (isEndDateModified() ? 1 : 0);
        result = 31 * result + (isTimezoneModified() ? 1 : 0);
        result = 31 * result + (isCalendarNameModified() ? 1 : 0);
        result = 31 * result + (isMisfireInstructionModified() ? 1 : 0);
        return result;
    }

    @Override
    public String
    toString() {
        return "ClientJobSimpleTriggerModel{" +
                "isCalendarNameModified=" + isCalendarNameModified +
                ", isOccurrenceCountModified=" + isOccurrenceCountModified +
                ", isRecurrenceIntervalUnitModified=" + isRecurrenceIntervalUnitModified +
                ", isRecurrenceIntervalModified=" + isRecurrenceIntervalModified +
                ", isStartDateModified=" + isStartDateModified +
                ", isStartTypeModified=" + isStartTypeModified +
                ", isEndDateModified=" + isEndDateModified +
                ", isTimezoneModified=" + isTimezoneModified +
                ", isMisfireInstructionModified=" + isMisfireInstructionModified +
                '}';
    }

    @Override
    public ClientJobSimpleTriggerModel deepClone() {
        return new ClientJobSimpleTriggerModel(this);
    }
}
