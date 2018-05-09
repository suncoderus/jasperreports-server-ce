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

import com.jaspersoft.jasperserver.dto.job.ClientCalendarDaysType;
import com.jaspersoft.jasperserver.dto.job.ClientJobCalendarTrigger;
import java.util.Date;
import java.util.SortedSet;


/**
 * Job trigger model that fires at specified calendar moments.  Model is used in search/ update only.
 *
 * <p>
 * Calendar triggers model can be used inFolder define jobs that occur on specific month or
 * week days at certain time(s) of the day.
 * </p>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 */
public class ClientJobCalendarTriggerModel extends ClientJobCalendarTrigger {

    private boolean isMinutesModified = false;
    private boolean isHoursModified = false;
    private boolean isDaysTypeModified = false;
    private boolean isMonthDaysModified = false;
    private boolean isMonthsModified = false;
    private boolean isWeekDaysModified = false;
    private boolean isStartDateModified = false;
    private boolean isStartTypeModified = false;
    private boolean isEndDateModified = false;
    private boolean isTimezoneModified = false;
    private boolean isCalendarNameModified = false;
    private boolean isMisfireInstructionModified = false;

    public ClientJobCalendarTriggerModel() {
        super();
    }

    public ClientJobCalendarTriggerModel(ClientJobCalendarTriggerModel other) {
        super(other);
        this.isCalendarNameModified = other.isCalendarNameModified;
        this.isDaysTypeModified = other.isDaysTypeModified;
        this.isEndDateModified = other.isEndDateModified;
        this.isHoursModified = other.isHoursModified;
        this.isMinutesModified = other.isMinutesModified;
        this.isMisfireInstructionModified = other.isMisfireInstructionModified;
        this.isMonthDaysModified = other.isMonthDaysModified;
        this.isMonthsModified = other.isMonthsModified;
        this.isStartDateModified = other.isStartDateModified;
        this.isStartTypeModified = other.isStartTypeModified;
        this.isTimezoneModified = other.isTimezoneModified;
        this.isWeekDaysModified = other.isWeekDaysModified;
    }

    public boolean isCalendarNameModified() {
        return isCalendarNameModified;
    }

    public boolean isDaysTypeModified() {
        return isDaysTypeModified;
    }

    public boolean isEndDateModified() {
        return isEndDateModified;
    }

    public boolean isHoursModified() {
        return isHoursModified;
    }

    public boolean isMinutesModified() {
        return isMinutesModified;
    }

    public boolean isMisfireInstructionModified() {
        return isMisfireInstructionModified;
    }

    public boolean isMonthDaysModified() {
        return isMonthDaysModified;
    }

    public boolean isMonthsModified() {
        return isMonthsModified;
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

    public boolean isWeekDaysModified() {
        return isWeekDaysModified;
    }

    @Override
    public ClientJobCalendarTriggerModel setMinutes(String minutes) {
        super.setMinutes(minutes);
        isMinutesModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setHours(String hours) {
        super.setHours(hours);
        isHoursModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setDaysType(ClientCalendarDaysType daysType) {
        super.setDaysType(daysType);
        isDaysTypeModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setMonthDays(String monthDays) {
        super.setMonthDays(monthDays);
        isMonthDaysModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setMonths(SortedSet<Byte> months) {
        super.setMonths(months);
        isMonthsModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setWeekDays(SortedSet<Byte> weekDays) {
        super.setWeekDays(weekDays);
        isWeekDaysModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setStartDate(Date startDate) {
        super.setStartDate(startDate);
        isStartDateModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setStartType(int startType) {
        super.setStartType(startType);
        isStartTypeModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setEndDate(Date endDate) {
        super.setEndDate(endDate);
        isEndDateModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setTimezone(String timezone) {
        super.setTimezone(timezone);
        isTimezoneModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setCalendarName(String calendarName) {
        super.setCalendarName(calendarName);
        isCalendarNameModified = true;
        return this;
    }

    @Override
    public ClientJobCalendarTriggerModel setMisfireInstruction(Integer misfireInstruction) {
        super.setMisfireInstruction(misfireInstruction);
        isMisfireInstructionModified = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobCalendarTriggerModel)) return false;
        if (!super.equals(o)) return false;

        ClientJobCalendarTriggerModel that = (ClientJobCalendarTriggerModel) o;

        if (isMinutesModified() != that.isMinutesModified()) return false;
        if (isHoursModified() != that.isHoursModified()) return false;
        if (isDaysTypeModified() != that.isDaysTypeModified()) return false;
        if (isMonthDaysModified() != that.isMonthDaysModified()) return false;
        if (isMonthsModified() != that.isMonthsModified()) return false;
        if (isWeekDaysModified() != that.isWeekDaysModified()) return false;
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
        result = 31 * result + (isMinutesModified() ? 1 : 0);
        result = 31 * result + (isHoursModified() ? 1 : 0);
        result = 31 * result + (isDaysTypeModified() ? 1 : 0);
        result = 31 * result + (isMonthDaysModified() ? 1 : 0);
        result = 31 * result + (isMonthsModified() ? 1 : 0);
        result = 31 * result + (isWeekDaysModified() ? 1 : 0);
        result = 31 * result + (isStartDateModified() ? 1 : 0);
        result = 31 * result + (isStartTypeModified() ? 1 : 0);
        result = 31 * result + (isEndDateModified() ? 1 : 0);
        result = 31 * result + (isTimezoneModified() ? 1 : 0);
        result = 31 * result + (isCalendarNameModified() ? 1 : 0);
        result = 31 * result + (isMisfireInstructionModified() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientJobCalendarTriggerModel{" +
                "isCalendarNameModified=" + isCalendarNameModified +
                ", isMinutesModified=" + isMinutesModified +
                ", isHoursModified=" + isHoursModified +
                ", isDaysTypeModified=" + isDaysTypeModified +
                ", isMonthDaysModified=" + isMonthDaysModified +
                ", isMonthsModified=" + isMonthsModified +
                ", isWeekDaysModified=" + isWeekDaysModified +
                ", isStartDateModified=" + isStartDateModified +
                ", isStartTypeModified=" + isStartTypeModified +
                ", isEndDateModified=" + isEndDateModified +
                ", isTimezoneModified=" + isTimezoneModified +
                ", isMisfireInstructionModified=" + isMisfireInstructionModified +
                '}';
    }

    @Override
    public ClientJobCalendarTriggerModel deepClone() {
        return new ClientJobCalendarTriggerModel(this);
    }
}
