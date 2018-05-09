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
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.XlsExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id$
 */
public class XlsxReportOutput extends AbstractReportOutput
{

	private static final Log log = LogFactory.getLog(XlsxReportOutput.class);

	private XlsExportParametersBean exportParams;
	
	public XlsxReportOutput()
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
			JRXlsxExporter exporter = new JRXlsxExporter(getJasperReportsContext());
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			
			boolean close = false;
			DataContainer xlsxData = jobContext.createDataContainer(this);
			OutputStream xlsxDataOut = xlsxData.getOutputStream();
			try {
	            SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(xlsxDataOut);
	            exporter.setExporterOutput(exporterOutput);

	            SimpleXlsxExporterConfiguration exporterConfiguration = new SimpleXlsxExporterConfiguration();
	            exporterConfiguration.setCreateCustomPalette(Boolean.TRUE);

				if(exportParams != null)
				{
					SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
					configuration.setOnePagePerSheet(exportParams.getOnePagePerSheet());
					configuration.setDetectCellType(exportParams.getDetectCellType());
					configuration.setRemoveEmptySpaceBetweenRows(exportParams.getRemoveEmptySpaceBetweenRows());
					configuration.setRemoveEmptySpaceBetweenColumns(exportParams.getRemoveEmptySpaceBetweenColumns());
					configuration.setWhitePageBackground(exportParams.getWhitePageBackground());
					configuration.setIgnoreGraphics(exportParams.getIgnoreGraphics());
					configuration.setCollapseRowSpan(exportParams.getCollapseRowSpan());
					configuration.setIgnoreCellBorder(exportParams.getIgnoreCellBorder());
					configuration.setFontSizeFixEnabled(exportParams.getFontSizeFixEnabled());
					configuration.setMaxRowsPerSheet(exportParams.getMaximumRowsPerSheet());
					configuration.setFormatPatternsMap(exportParams.getXlsFormatPatternsMap());
					exporter.setConfiguration(configuration);
				}	

				exporter.setConfiguration(exporterConfiguration);
				
				exporter.exportReport();

				close = false;
				xlsxDataOut.close();
				
				String baseFilename = jobContext.getBaseFilename();
				if (isIgnorePagination() != null && isIgnorePagination()
						&& jobContext.hasOutput(ReportJob.OUTPUT_FORMAT_XLSX))
				{
					//we have both paginated and unpaginated XLSX outputs, we need a different name
					baseFilename += "_nopag";
				}
				String filename = baseFilename + ".xlsx";
				return new ReportOutput(xlsxData, ContentResource.TYPE_XLSX, filename);
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						xlsxDataOut.close();
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
	public XlsExportParametersBean getExportParams() {
		return exportParams;
	}

	/**
	 * @param exportParams The exportParams to set.
	 */
	public void setExportParams(XlsExportParametersBean exportParams) {
		this.exportParams = exportParams;
	}
	
	@Override
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null)
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), XlsExportParametersBean.PROPERTY_XLS_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}
}
