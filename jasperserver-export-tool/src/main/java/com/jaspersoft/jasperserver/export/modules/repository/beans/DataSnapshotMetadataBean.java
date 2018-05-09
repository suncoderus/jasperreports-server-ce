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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshotMetadata;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.export.modules.common.ReportParameterValueBean;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;

import java.util.Date;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DataSnapshotMetadataBean {

	private Date snapshotDate;
	private ReportParameterValueBean[] parameters;

	public DataSnapshotMetadataBean() {
	}
	
	public void copyFrom(DataCacheSnapshotMetadata metadata, String reportUnitURI, 
			ResourceExportHandler exportHandler) {
		setSnapshotDate(metadata.getSnapshotDate());
		
		Map<String, Object> snapshotParams = metadata.getParameters();
		ReportParameterValueBean[] params = exportHandler.getConfiguration()
				.getReportParametersTranslator().getBeanParameterValues(
						reportUnitURI, snapshotParams);
		setParameters(params);
	}

	public Map<String, Object> getParametersMap(ReportUnit reportUnit, ResourceImportHandler importHandler) {
		Map<String, Object> params = importHandler.getConfiguration()
				.getReportParametersTranslator().getParameterValues(
						reportUnit, getParameters(), importHandler.getExecutionContext());
		return params;
	}
	
	public Date getSnapshotDate() {
		return snapshotDate;
	}
	
	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

	public ReportParameterValueBean[] getParameters() {
		return parameters;
	}

	public void setParameters(ReportParameterValueBean[] parameters) {
		this.parameters = parameters;
	}
	
}
