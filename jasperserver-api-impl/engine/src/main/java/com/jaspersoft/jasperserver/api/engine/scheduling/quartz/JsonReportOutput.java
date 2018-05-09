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
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JsonMetadataExporter;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JsonReportOutput extends AbstractReportOutput
{

	private static final Log log = LogFactory.getLog(JsonReportOutput.class);

	public JsonReportOutput()
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
			JsonMetadataExporter exporter = new JsonMetadataExporter(getJasperReportsContext());
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			
			boolean close = false;
			DataContainer dataContainer = jobContext.createDataContainer(this);
			OutputStream dataOut = dataContainer.getOutputStream();
			try {
				exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, jobContext.getCharacterEncoding());
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dataOut);
				
				exporter.exportReport();
				
				close = false;
				dataOut.close();

				String fileName = jobContext.getBaseFilename() + ".json";
				return new ReportOutput(dataContainer, ContentResource.TYPE_JSON, fileName);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						dataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
			
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	protected String getFileExtension()
	{
		return "json";
	}
	
	@Override
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		return isPaginationPreferred;
		// TODO lucianc
/*		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null)
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), PptxExportParametersBean.PROPERTY_PPTX_PAGINATED);
			}
		}
		return isPaginationPreferred;
*/	}
}
