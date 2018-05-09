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
package com.jaspersoft.jasperserver.dto.job;


import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import com.jaspersoft.jasperserver.dto.job.adapters.ExcludeDaysXmlAdapter;
import com.jaspersoft.jasperserver.dto.job.adapters.TimeZoneXmlAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "reportJobCalendar")
public class ClientJobCalendar implements DeepCloneable<ClientJobCalendar> {
    public enum Type {
        annual,
        base,
        cron,
        daily,
        holiday,
        monthly,
        weekly;
    }

    private Type calendarType;
    // base fields
    private ClientJobCalendar baseCalendar;
    private String description;
    private TimeZone timeZone;
    //annual calendar's fields
    //excludeDays field used for holiday calendar too
    private ArrayList<Calendar> excludeDays = new ArrayList<java.util.Calendar>();

    // true, if excludeDays is sorted
    private Boolean dataSorted;
    //cron calendar's fields
    private String cronExpression;

    //daily calendar's fields
    private Calendar rangeStartingCalendar;

    private Calendar rangeEndingCalendar;
    private Boolean invertTimeRange;
    //monthly calendar's fields
    // An array to store a months days which are to be excluded.
    // java.util.Calendar.get( ) as index.
    private boolean[] excludeDaysFlags;


    public ClientJobCalendar() {
    }

    public ClientJobCalendar(ClientJobCalendar other) {
        this.baseCalendar = (other.baseCalendar != null) ? new ClientJobCalendar(other.baseCalendar) : null;
        this.calendarType = other.calendarType;
        this.cronExpression = other.cronExpression;
        this.dataSorted = other.dataSorted;
        this.description = other.description;
        if (other.excludeDays != null) {
            this.excludeDays = new ArrayList<Calendar>();
            for (Calendar excludeDay : other.excludeDays) {
                this.excludeDays.add(excludeDay);
            }
        }
        if (other.excludeDaysFlags != null) {
            this.excludeDaysFlags = new boolean[other.excludeDaysFlags.length];
            for (int i = 0; i < other.excludeDaysFlags.length; i++) {
                this.excludeDaysFlags[i] = other.excludeDaysFlags[i];
            }
        }
        this.invertTimeRange = other.invertTimeRange;
        this.rangeEndingCalendar = (other.rangeEndingCalendar != null) ? (Calendar) other.rangeEndingCalendar.clone() : null;
        this.rangeStartingCalendar = (other.rangeStartingCalendar != null) ? (Calendar) other.rangeStartingCalendar.clone() : null;
        this.timeZone = (other.timeZone != null) ? (TimeZone) other.timeZone.clone() : null;
    }

    public Type getCalendarType() {
        return calendarType;
    }


    public ClientJobCalendar setCalendarType(Type calendarType) {
        this.calendarType = calendarType;
        return this;
    }

    public ClientJobCalendar getBaseCalendar() {
        return baseCalendar;
    }

    public ClientJobCalendar setBaseCalendar(ClientJobCalendar baseCalendar) {
        this.baseCalendar = baseCalendar;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ClientJobCalendar setDescription(String description) {
        this.description = description;
        return this;
    }

    @XmlJavaTypeAdapter(TimeZoneXmlAdapter.class)
    public TimeZone getTimeZone() {
        return timeZone;
    }

    public ClientJobCalendar setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    @XmlJavaTypeAdapter(ExcludeDaysXmlAdapter.class)
    public ArrayList<Calendar> getExcludeDays() {
        return excludeDays;
    }

    public ClientJobCalendar setExcludeDays(ArrayList<Calendar> excludeDays) {
        this.excludeDays = excludeDays;
        return this;
    }

    public Boolean isDataSorted() {
        return dataSorted;
    }

    public ClientJobCalendar setDataSorted(Boolean dataSorted) {
        this.dataSorted = dataSorted;
        return this;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public ClientJobCalendar setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        return this;
    }

    public Calendar getRangeStartingCalendar() {
        return rangeStartingCalendar;
    }

    public ClientJobCalendar setRangeStartingCalendar(Calendar rangeStartingCalendar) {
        this.rangeStartingCalendar = rangeStartingCalendar;
        return this;
    }

    public Calendar getRangeEndingCalendar() {
        return rangeEndingCalendar;
    }

    public ClientJobCalendar setRangeEndingCalendar(Calendar rangeEndingCalendar) {
        this.rangeEndingCalendar = rangeEndingCalendar;
        return this;
    }

    public Boolean isInvertTimeRange() {
        return invertTimeRange;
    }

    public ClientJobCalendar setInvertTimeRange(Boolean invertTimeRange) {
        this.invertTimeRange = invertTimeRange;
        return this;
    }

    @XmlElementWrapper(name = "excludeDaysFlags")
    @XmlElement(name = "excludeDayFlag")
    public boolean[] getExcludeDaysFlags() {
        return excludeDaysFlags;
    }

    public ClientJobCalendar setExcludeDaysFlags(boolean[] excludeDaysFlags) {
        this.excludeDaysFlags = excludeDaysFlags;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobCalendar)) return false;

        ClientJobCalendar that = (ClientJobCalendar) o;

        if (getCalendarType() != that.getCalendarType()) return false;
        if (getBaseCalendar() != null ? !getBaseCalendar().equals(that.getBaseCalendar()) : that.getBaseCalendar() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getTimeZone() != null ? !getTimeZone().equals(that.getTimeZone()) : that.getTimeZone() != null)
            return false;
        if (getExcludeDays() != null ? !getExcludeDays().equals(that.getExcludeDays()) : that.getExcludeDays() != null)
            return false;
        if (dataSorted != null ? !dataSorted.equals(that.dataSorted) : that.dataSorted != null) return false;
        if (getCronExpression() != null ? !getCronExpression().equals(that.getCronExpression()) : that.getCronExpression() != null)
            return false;
        if (getRangeStartingCalendar() != null ? !getRangeStartingCalendar().equals(that.getRangeStartingCalendar()) : that.getRangeStartingCalendar() != null)
            return false;
        if (getRangeEndingCalendar() != null ? !getRangeEndingCalendar().equals(that.getRangeEndingCalendar()) : that.getRangeEndingCalendar() != null)
            return false;
        if (invertTimeRange != null ? !invertTimeRange.equals(that.invertTimeRange) : that.invertTimeRange != null)
            return false;
        return Arrays.equals(getExcludeDaysFlags(), that.getExcludeDaysFlags());

    }

    @Override
    public int hashCode() {
        int result = getCalendarType() != null ? getCalendarType().hashCode() : 0;
        result = 31 * result + (getBaseCalendar() != null ? getBaseCalendar().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getTimeZone() != null ? getTimeZone().hashCode() : 0);
        result = 31 * result + (getExcludeDays() != null ? getExcludeDays().hashCode() : 0);
        result = 31 * result + (dataSorted != null ? dataSorted.hashCode() : 0);
        result = 31 * result + (getCronExpression() != null ? getCronExpression().hashCode() : 0);
        result = 31 * result + (getRangeStartingCalendar() != null ? getRangeStartingCalendar().hashCode() : 0);
        result = 31 * result + (getRangeEndingCalendar() != null ? getRangeEndingCalendar().hashCode() : 0);
        result = 31 * result + (invertTimeRange != null ? invertTimeRange.hashCode() : 0);
        result = 31 * result + (getExcludeDaysFlags() != null ? Arrays.hashCode(getExcludeDaysFlags()) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientJobCalendar{" +
                "baseCalendar=" + baseCalendar +
                ", calendarType=" + calendarType +
                ", description='" + description + '\'' +
                ", timeZone=" + timeZone +
                ", excludeDays=" + excludeDays +
                ", dataSorted=" + dataSorted +
                ", cronExpression='" + cronExpression + '\'' +
                ", rangeStartingCalendar=" + rangeStartingCalendar +
                ", rangeEndingCalendar=" + rangeEndingCalendar +
                ", invertTimeRange=" + invertTimeRange +
                ", excludeDaysFlags=" + Arrays.toString(excludeDaysFlags) +
                '}';
    }

    @Override
    public ClientJobCalendar deepClone() {
        return new ClientJobCalendar(this);
    }
}
