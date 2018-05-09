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

package com.jaspersoft.jasperserver.export.modules.scheduling;

import com.jaspersoft.jasperserver.export.modules.BaseExporterModule;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.HolidayCalendarBean;
import com.jaspersoft.jasperserver.core.util.PathUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import org.quartz.Calendar;
import org.quartz.impl.calendar.HolidayCalendar;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobsExporter.java 37706 2013-09-18 15:28:43Z ztomchenco $
 */
public class CalendarsExporter extends BaseExporterModule {
    protected static final Pattern RESOURCE_ID_INVALID_CHAR =
            Pattern.compile("[^\\p{L}\\p{N}]");

    protected static final String RESOURCE_ID_CHAR_REPLACEMENT = "_";

	protected SchedulingModuleConfiguration configuration;

	protected String calendarsArg;

	protected boolean isToProcess() {
		return hasParameter(calendarsArg);
	}
	
	public void process() {

        List<String> calendarNames = configuration.getReportJobsScheduler().getCalendarNames();

        if (CollectionUtils.isNotEmpty(calendarNames)) {
            mkdir(configuration.getCalendarsDir());

            for (String name: calendarNames) {
                processCalendar(name);
            }
        }
    }

    private void processCalendar(String name) {
        if(StringUtils.isEmpty(name)) {
            return;
        }

        Calendar calendar = configuration.getReportJobsScheduler().getCalendar(name);

        if (calendar == null) {
            return;
        }

        if (calendar instanceof HolidayCalendar) {
            exportCalendar(configuration.getCalendarsDir(), name, (HolidayCalendar) calendar);
            addIndexElement(name);

            commandOut.info(calendarExportInfo(name));
        } else {
            commandOut.debug(calendarExportOmit(name));
        }
	}

    protected void exportCalendar(String folderPath, String name, HolidayCalendar calendar) {
        HolidayCalendarBean calendarBean = new HolidayCalendarBean();
        calendarBean.setName(name);
        calendarBean.copyFrom(calendar);

        serialize(calendarBean, folderPath, getFilename(name), configuration.getSerializer());
    }

    protected Element addIndexElement(String calendarName) {
        Element roleElement = getIndexElement().addElement(configuration.getIndexCalendarElement());
        roleElement.setText(calendarName);
        return roleElement;
    }

    protected String getFilename(String name) {
        return PathUtils.preparePathComponent(name) + ".xml";
    }

    private String calendarExportInfo(String name) {
        StringBuilder info = new StringBuilder("Exported ").
                append(name).
                append(" holiday calendar.");

        return info.toString();
    }

    private String calendarExportOmit(String name) {
        StringBuilder info = new StringBuilder("Calendar ").
                append(name).
                append(" does not exported.");

        return info.toString();
    }


    public SchedulingModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SchedulingModuleConfiguration configuration) {
		this.configuration = configuration;
	}

    public String getCalendarsArg() {
        return calendarsArg;
    }

    public void setCalendarsArg(String calendarsArg) {
        this.calendarsArg = calendarsArg;
    }
}
