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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PdfExportParametersBean;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id$
 */
public class PdfReportOutput extends AbstractReportOutput 
{

	private static final Log log = LogFactory.getLog(PdfReportOutput.class);

	private PdfExportParametersBean exportParams;
	
	public PdfReportOutput()
	{
	}

	/** 
	 * @see com.jaspersoft.jasperserver.api.engine.scheduling.quartz.Output#getOutput()
	 */
	public ReportOutput getOutput(
			ReportJobContext jobContext,
			JasperPrint jasperPrint) throws JobExecutionException
	{
		Map params = new HashMap();
		params.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
		
		boolean close = true;
		DataContainer pdfData = jobContext.createDataContainer(this);
		OutputStream pdfDataOut = pdfData.getOutputStream();
		
		try {
			params.put(JRExporterParameter.OUTPUT_STREAM, pdfDataOut);
			
			EngineService engineService = jobContext.getEngineService();
			engineService.exportToPdf(jobContext.getExecutionContext(), jobContext.getReportUnitURI(), params);
			
			close = false;
			pdfDataOut.close();
		} catch (IOException e) {
			throw new JSExceptionWrapper(e);
		} finally {
			if (close) {
				try {
					pdfDataOut.close();
				} catch (IOException e) {
					log.error("Error closing stream", e);
				}
			}
		}
		
		String filename = jobContext.getBaseFilename() + ".pdf";
		return new ReportOutput(pdfData, ContentResource.TYPE_PDF, filename);
	}

	/**
	 * @return Returns the exportParams.
	 */
	public PdfExportParametersBean getExportParams() {
		return exportParams;
	}

	/**
	 * @param exportParams The exportParams to set.
	 */
	public void setExportParams(PdfExportParametersBean exportParams) {
		this.exportParams = exportParams;
	}
	
	@Override
	public Boolean isPaginationPreferred(JRPropertiesHolder propertiesHolder){
		Boolean isPaginationPreferred = super.isPaginationPreferred(propertiesHolder);
		if (isPaginationPreferred == null)
		{
			if (propertiesHolder != null)
			{
				isPaginationPreferred = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(propertiesHolder.getPropertiesMap(), PdfExportParametersBean.PROPERTY_PDF_PAGINATED);
			}
		}
		return isPaginationPreferred;
	}

	@Override
	protected Integer getMaxPageHeight(JRPropertiesHolder propertiesHolder) {
		Integer maxPageHeight = super.getMaxPageHeight(propertiesHolder);
		if (maxPageHeight == null && propertiesHolder != null) {
			maxPageHeight = JRPropertiesUtil.getInstance(getJasperReportsContext()).getIntegerProperty(
					propertiesHolder, PdfExportParametersBean.PROPERTY_PDF_MAX_PAGE_HEIGHT);
		}
		return maxPageHeight;
	}

	@Override
	protected Integer getMaxPageWidth(JRPropertiesHolder propertiesHolder) {
		Integer maxPageWidth = super.getMaxPageWidth(propertiesHolder);
		if (maxPageWidth == null && propertiesHolder != null) {
			maxPageWidth = JRPropertiesUtil.getInstance(getJasperReportsContext()).getIntegerProperty(
					propertiesHolder, PdfExportParametersBean.PROPERTY_PDF_MAX_PAGE_WIDTH);
		}
		return maxPageWidth;
	}
}
