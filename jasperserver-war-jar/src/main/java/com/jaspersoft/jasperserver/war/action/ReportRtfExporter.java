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
package com.jaspersoft.jasperserver.war.action;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.RtfExportParametersBean;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportRtfExporter extends AbstractReportExporter 
{

	public void export(RequestContext context, ExecutionContext executionContext, JasperPrint jasperPrint, OutputStream outputStream) throws JRException
	{
		JRRtfExporter exporter = new JRRtfExporter(getJasperReportsContext());
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));// no need for encoding param as rtf is ascii format

		exporter.exportReport();
	}

	protected String getContentType(RequestContext context) {
		return "application/rtf";
	}

	protected void setAdditionalResponseHeaders(RequestContext context, HttpServletResponse response) {
		super.setAdditionalResponseHeaders(context, response);
		response.setHeader("Content-Disposition", "inline; filename=\"" + getFilename(context) + "\"");
	}

	protected String getDownloadFileExtension() {
		return "rtf";
	}
	
	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters(RequestContext context) {
		return null;
	}
	
	@Override
	protected Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder) {
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null) 
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), RtfExportParametersBean.PROPERTY_RTF_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}
}
