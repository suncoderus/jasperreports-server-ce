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
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.CsvExportParametersBean;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id$
 */
public class CsvReportOutput extends AbstractReportOutput 
{

	private static final Log log = LogFactory.getLog(CsvReportOutput.class);

	private CsvExportParametersBean exportParams;
	
	public CsvReportOutput()
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
			JRCsvExporter exporter = new JRCsvExporter(getJasperReportsContext());
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			
			boolean close = false;
			DataContainer csvData = jobContext.createDataContainer(this);
			OutputStream csvDataOut = csvData.getOutputStream();
			try {
	            SimpleWriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(csvDataOut, jobContext.getCharacterEncoding());
	            exporter.setExporterOutput(exporterOutput);
				
				if(exportParams != null)
				{
					SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
					configuration.setFieldDelimiter(exportParams.getFieldDelimiter());
					exporter.setConfiguration(configuration);
				}
				
				exporter.exportReport();
				
				close = false;
				csvDataOut.close();

				String fileName = jobContext.getBaseFilename() + ".csv";
				return new ReportOutput(csvData, ContentResource.TYPE_CSV, fileName);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						csvDataOut.close();
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
	public CsvExportParametersBean getExportParams() {
		return exportParams;
	}
	/**
	 * @param exportParams The exportParams to set.
	 */
	public void setExportParams(CsvExportParametersBean exportParams) {
		this.exportParams = exportParams;
	}
	
	@Override
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null)
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), CsvExportParametersBean.PROPERTY_CSV_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}
}
