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

package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.RtfExportParametersBean;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id$
 */
public class RtfReportOutput extends AbstractReportOutput
{

	private static final Log log = LogFactory.getLog(RtfReportOutput.class);

	public RtfReportOutput()
	{
	}

	/** 
	 * @see com.jaspersoft.jasperserver.api.engine.scheduling.quartz.Output#getOutput()
	 */
	public ReportOutput getOutput(
			ReportJobContext jobContext,
			JasperPrint jasperPrint) throws JobExecutionException
	{
		try {
			JRRtfExporter exporter = new JRRtfExporter(getJasperReportsContext());
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			
			boolean close = false;
			DataContainer rtfData = jobContext.createDataContainer(this);
			OutputStream rtfDataOut = rtfData.getOutputStream();
			try {
	            SimpleWriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(rtfDataOut);
	            exporter.setExporterOutput(exporterOutput);

	            exporter.exportReport();
				
				close = false;
				rtfDataOut.close();

				String fileName = jobContext.getBaseFilename() + ".rtf";
				return new ReportOutput(rtfData, ContentResource.TYPE_RTF, fileName);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						rtfDataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}
	
	@Override
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
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
