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
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.TxtExportParametersBean;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleTextReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id$
 */
public class TxtReportOutput extends AbstractReportOutput
{

	private static final Log log = LogFactory.getLog(TxtReportOutput.class);

	private TxtExportParametersBean exportParams;
	public TxtReportOutput()
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
			JRTextExporter exporter = new JRTextExporter(getJasperReportsContext());
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			
			boolean close = false;
			DataContainer txtData = jobContext.createDataContainer(this);
			OutputStream txtDataOut = txtData.getOutputStream();
			try {
	            SimpleWriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(txtDataOut, jobContext.getCharacterEncoding());
	            exporter.setExporterOutput(exporterOutput);
				
				if(exportParams != null)
				{
					SimpleTextReportConfiguration configuration = new SimpleTextReportConfiguration();
					configuration.setCharHeight(exportParams.getCharacterHeight());
					configuration.setCharWidth(exportParams.getCharacterWidth());
					configuration.setPageHeightInChars(exportParams.getPageHeight());
					configuration.setPageWidthInChars(exportParams.getPageWidth());
					exporter.setConfiguration(configuration);
				}
				exporter.exportReport();
				
				close = false;
				txtDataOut.close();

				String fileName = jobContext.getBaseFilename() + ".txt";
				return new ReportOutput(txtData, ContentResource.TYPE_TXT, fileName);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						txtDataOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
			
		} catch (JRException e) {
			throw new JSExceptionWrapper(e);
		}
	}

	/**
	 * @return Returns the exportParams.
	 */
	public TxtExportParametersBean getExportParams() {
		return exportParams;
	}

	/**
	 * @param exportParams The exportParams to set.
	 */
	public void setExportParams(TxtExportParametersBean exportParams) {
		this.exportParams = exportParams;
	}
	
	@Override
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null)
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), TxtExportParametersBean.PROPERTY_TEXT_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}
}
