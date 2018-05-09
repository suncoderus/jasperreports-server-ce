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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.export.modules.common.ReportParameterValueBean;
import com.jaspersoft.jasperserver.export.modules.scheduling.SchedulingModuleConfiguration;
import org.quartz.Calendar;
import org.quartz.impl.calendar.HolidayCalendar;

import java.util.*;

/**
 * @author tkavanagh
 * @version $Id: ReportJobSourceBean.java 23810 2012-05-18 05:19:56Z ykovalchyk $
 */
public class HolidayCalendarBean {

	private String name;
	private String description;
	private String timezone;
	private Date[] excludedDates;

    public void copyFrom(HolidayCalendar src) {

        setDescription(src.getDescription());
        if (src.getTimeZone() != null) {
            setTimezone(src.getTimeZone().getID());
        }

        int size = src.getExcludedDates().size();
        this.excludedDates = src.getExcludedDates().toArray(new Date[size]);
    }

	public void copyTo(HolidayCalendar dest) {
		dest.setDescription(getDescription());
        if (getTimezone() != null) {
            dest.setTimeZone(TimeZone.getTimeZone(getTimezone()));
        }

        for(Date date : getExcludedDates()) {
            dest.addExcludedDate(date);
        }
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date[] getExcludedDates() {
        return excludedDates;
    }

    public void setExcludedDates(Date[] excludedDates) {
        this.excludedDates = excludedDates;
    }
}
