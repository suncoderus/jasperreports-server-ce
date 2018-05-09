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

import java.util.Map;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSourceModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.metadata.common.util.NullValue;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PersistentReportJobSource {

	private String reportUnitURI;
	private Map parameters;
	
	public PersistentReportJobSource() {
	}

	public String getReportUnitURI() {
		return reportUnitURI;
	}

	public void setReportUnitURI(String reportUnitURI) {
		this.reportUnitURI = reportUnitURI;
	}

	public Map getParametersMap() {
		return parameters;
	}

	public void setParametersMap(Map parameters) {
		this.parameters = parameters;
	}

    public void copyFrom(ReportJobSource source) {
		setReportUnitURI(source.getReportUnitURI());
		setParametersMap(NullValue.replaceWithNullValues(source.getParametersMap()));
	}

	public void copyFrom(ReportJobSourceModel source) {
		if (source.isReportUnitURIModified()) setReportUnitURI(source.getReportUnitURI());
		if (source.isParametersMapModified()) setParametersMap(NullValue.replaceWithNullValues(source.getParametersMap()));
	}

	public ReportJobSource toClient() {
		ReportJobSource source = new ReportJobSource();
		source.setReportUnitURI(getReportUnitURI());
		source.setParametersMap(NullValue.restoreNulls(getParametersMap()));
		return source;
	}

}
