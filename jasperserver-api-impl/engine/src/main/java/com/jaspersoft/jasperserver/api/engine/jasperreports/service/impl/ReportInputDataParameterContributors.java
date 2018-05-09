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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitRequestBase;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.MaterializedDataParameter;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.types.date.DateRange;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportInputDataParameterContributors implements ReportDataParameterContributor {
	
	private ReportLoadingService reportLoadingService;

	@Override
	public void addDataParameters(ExecutionContext context, 
			ReportUnitRequestBase request, ReportUnit reportUnit, JasperReport report,
			ReportDataParameters parameters) {
		// input control values are used as data parameters
		Map<String, Object> params = request.getReportParameters();
		List<ResourceReference> inputControls = getReportLoadingService().getInputControlReferences(context, reportUnit);
		if (inputControls != null) {
			for (ResourceReference ref : inputControls) {
				String paramName = getInputControlParameterName(ref);
				if (paramName != null) {
					Object value = params == null ? null : params.get(paramName);
					Object dataValue = toDataValue(value);
					parameters.addDataParameter(paramName, dataValue);
				}
			}
		}
	}
	
	protected Object toDataValue(Object value) {
		if (value == null) {
			return null;
		}
		
		// at some point we could introduce configurable handlers for specific types
		// for now we have builtin check for date ranges
		Object dataValue;
		if (value instanceof DateRange) {
			DateRange date = (DateRange) value;
			Pair<Date, Date> effectiveValue = new Pair<Date, Date>(date.getStart(), date.getEnd());
			// storing both the original date range object and the effective value
			// the original value could be used in EngineServiceImpl.setSnapshotParameterValues
			dataValue = new MaterializedDataParameter(value, effectiveValue);
		} else {
			dataValue = value;
		}
		return dataValue;
	}

	protected String getInputControlParameterName(ResourceReference inputControlRef) {
		// we only need the input control name
		String name = null;
		if (inputControlRef.isLocal()) {
			name = inputControlRef.getLocalResource().getName();
		} else {
			String referenceURI = inputControlRef.getReferenceURI();
			if (referenceURI != null) {
				name = RepositoryUtils.getName(referenceURI);
			}
		}
		return name;
	}

	public ReportLoadingService getReportLoadingService() {
		return reportLoadingService;
	}

	public void setReportLoadingService(ReportLoadingService reportLoadingService) {
		this.reportLoadingService = reportLoadingService;
	}

}
