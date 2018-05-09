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

import com.jaspersoft.jasperserver.api.engine.scheduling.ReportSchedulingInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportJobsScheduler;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.modules.common.ReportParametersTranslator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class SchedulingModuleConfiguration {
	
	private RepositoryService repository;
	private ReportSchedulingService reportScheduler;
    private ReportSchedulingInternalService internalReportScheduler;
    private ReportJobsScheduler reportJobsScheduler;
    private UserAuthorityService authorityService;
    private ReportParametersTranslator reportParametersTranslator;

    private ObjectSerializer serializer;
	private String reportJobsDir;
	private String calendarsDir;
	private String indexCalendarElement;
	private String indexReportUnitElement;
	private String reportUnitIndexFilename;
    private boolean allowReportsUnitsUrisOnly = true;
	
	public ReportSchedulingService getReportScheduler() {
		return reportScheduler;
	}
	
	public void setReportScheduler(ReportSchedulingService reportScheduler) {
		this.reportScheduler = reportScheduler;
	}
	
	public ObjectSerializer getSerializer() {
		return serializer;
	}
	
	public void setSerializer(ObjectSerializer serializer) {
		this.serializer = serializer;
	}

	public String getReportJobsDir() {
		return reportJobsDir;
	}

	public void setReportJobsDir(String reportJobsDir) {
		this.reportJobsDir = reportJobsDir;
	}

	public String getCalendarsDir() {
		return calendarsDir;
	}

	public void setCalendarsDir(String calendarsDir) {
		this.calendarsDir = calendarsDir;
	}

	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

    public String getIndexCalendarElement() {
        return indexCalendarElement;
    }

    public void setIndexCalendarElement(String indexCalendarElement) {
        this.indexCalendarElement = indexCalendarElement;
    }

    public String getIndexReportUnitElement() {
		return indexReportUnitElement;
	}

	public void setIndexReportUnitElement(String indexReportUnitElement) {
		this.indexReportUnitElement = indexReportUnitElement;
	}

	public String getReportUnitIndexFilename() {
		return reportUnitIndexFilename;
	}

	public void setReportUnitIndexFilename(String reportUnitIndexFilename) {
		this.reportUnitIndexFilename = reportUnitIndexFilename;
	}

	public ReportSchedulingInternalService getInternalReportScheduler() {
		return internalReportScheduler;
	}

	public void setInternalReportScheduler(
			ReportSchedulingInternalService internalReportScheduler) {
		this.internalReportScheduler = internalReportScheduler;
	}

    public ReportJobsScheduler getReportJobsScheduler() {
        return reportJobsScheduler;
    }

    public void setReportJobsScheduler(ReportJobsScheduler reportJobsScheduler) {
        this.reportJobsScheduler = reportJobsScheduler;
    }

    public UserAuthorityService getAuthorityService() {
		return authorityService;
	}

	public void setAuthorityService(UserAuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	public ReportParametersTranslator getReportParametersTranslator() {
		return reportParametersTranslator;
	}

	public void setReportParametersTranslator(
			ReportParametersTranslator reportParametersTranslator) {
		this.reportParametersTranslator = reportParametersTranslator;
	}

    public boolean isAllowReportsUnitsUrisOnly() {
        return allowReportsUnitsUrisOnly;
    }

    public void setAllowReportsUnitsUrisOnly(boolean allowReportsUnitsUrisOnly) {
        this.allowReportsUnitsUrisOnly = allowReportsUnitsUrisOnly;
    }
}
