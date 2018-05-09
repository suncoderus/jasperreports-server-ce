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

import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImporter;
import com.jaspersoft.jasperserver.export.modules.scheduling.beans.HolidayCalendarBean;
import com.jaspersoft.jasperserver.core.util.PathUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.quartz.Calendar;
import org.quartz.impl.calendar.HolidayCalendar;

import java.util.List;
import java.util.Iterator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobsImporter.java 38014 2013-09-24 11:50:56Z ztomchenco $
 */
public class CalendarsImporter extends BaseImporterModule {

	private final static Log log = LogFactory.getLog(ResourceImporter.class);
	
	protected SchedulingModuleConfiguration configuration;
	private String updateArg;
	
	protected boolean updateExisting;

	public void init(ImporterModuleContext moduleContext) {
		super.init(moduleContext);
        this.updateExisting = hasParameter(getUpdateArg());
	}

	public List<String> process() {

		for (Iterator i = indexElement.elementIterator(configuration.getIndexCalendarElement()); i.hasNext(); ) {
			Element calendarElement = (Element) i.next();
			String name = calendarElement.getText();
            processCalendar(name);
		}
        return null;
	}

	protected void processCalendar(String name) {
        if (!isCalendarExist(name) || this.updateExisting) {

            HolidayCalendarBean bean = deserializeCalendar(name);

            HolidayCalendar calendar = new HolidayCalendar();
            bean.copyTo(calendar);

            configuration.getReportJobsScheduler().addCalendar(bean.getName(), calendar, true, true);
        }
 	}

    protected boolean isCalendarExist(String name) {
        Calendar calendar = configuration.getReportJobsScheduler().getCalendar(name);
        return calendar != null;
    }

    protected HolidayCalendarBean deserializeCalendar(String name) {
        String path = configuration.getCalendarsDir();
        String filename = getCalendarFilename(name);

        return  (HolidayCalendarBean) deserialize(path, filename, configuration.getSerializer());
    }

    protected String getCalendarFilename(String name) {
        return PathUtils.preparePathComponent(name) + ".xml";
    }

	public SchedulingModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SchedulingModuleConfiguration configuration) {
		this.configuration = configuration;
	}

    public String getUpdateArg() {
        return updateArg;
    }

    public void setUpdateArg(String updateArg) {
        this.updateArg = updateArg;
    }
}
