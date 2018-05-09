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
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.ReportParameterValueBean;
import com.jaspersoft.jasperserver.export.modules.common.TenantStrHolderPattern;
import com.jaspersoft.jasperserver.export.modules.scheduling.SchedulingModuleConfiguration;

import java.util.Map;

/**
 * @author tkavanagh
 * @version $Id$
 */
public class ReportJobSourceBean {

	private String reportUnitURI;
	private ReportParameterValueBean[] parameters;

	public void copyFrom(ReportJobSource src, 
			SchedulingModuleConfiguration configuration) {
		setReportUnitURI(src.getReportUnitURI());
		copyParametersFrom(src, configuration);
	}

	protected void copyParametersFrom(ReportJobSource src, 
			SchedulingModuleConfiguration configuration) {
		Map reportParameters = src.getParametersMap();
		ReportParameterValueBean[] params = configuration.getReportParametersTranslator().getBeanParameterValues(
				src.getReportUnitURI(), reportParameters);
		setParameters(params);
	}

	public void copyTo(ReportJobSource dest, String newReportUri, 
			SchedulingModuleConfiguration configuration, ExecutionContext context,
			ImporterModuleContext importContext) {
		dest.setReportUnitURI(newReportUri);
		copyParametersMap(dest, newReportUri, configuration, context);

		if (!importContext.getNewGeneratedTenantIds().isEmpty()) {
			dest.setReportUnitURI(TenantStrHolderPattern.TENANT_FOLDER_URI
					.replaceWithNewTenantIds(importContext.getNewGeneratedTenantIds(), dest.getReportUnitURI()));
		}
	}

	protected void copyParametersMap(ReportJobSource dest, 
			String newReportUri, SchedulingModuleConfiguration configuration, ExecutionContext context) {
		Map params = configuration.getReportParametersTranslator().getParameterValues(
				newReportUri, getParameters(), context);
		dest.setParametersMap(params);
	}

	public String getReportUnitURI() {
		return reportUnitURI;
	}
	
	public void setReportUnitURI(String reportUnitURI) {
		this.reportUnitURI = reportUnitURI;
	}

	public ReportParameterValueBean[] getParameters() {
		return parameters;
	}
	
	public void setParameters(ReportParameterValueBean[] parameters) {
		this.parameters = parameters;
	}

}
