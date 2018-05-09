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

package com.jaspersoft.jasperserver.remote.exporters;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.PdfExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.remote.ReportExporter;
import com.jaspersoft.jasperserver.remote.services.ReportOutputPages;

/**
 * This exporter does not extend an AbstractExporter, since it relies on the server
 * API to do the job of exporting a PDF file in the correct way (i.e. by
 * resolving PDF fonts by looking at the URI of the report unit
 *
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: PdfWSExporter.java 19933 2010-12-11 15:27:37Z tmatyashovsky $
 */
@Service("remotePdfExporter")
@Scope("prototype")
public class PdfExporter implements ReportExporter {

    @Resource(name = "pdfExportParameters")
    PdfExportParametersBean exportParams;

	@Resource(name = "jasperReportsRemoteContext")
    private JasperReportsContext jasperReportsContext;

	public Map<JRExporterParameter, Object> exportReport(
			JasperPrint jasperPrint,
			OutputStream output,
			EngineService engineService,
			HashMap exportParameters,
			ExecutionContext executionContext,
			String reportUnitURI
			) throws Exception
	{
		//set the input/output parameters in the map
		exportParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exportParameters.put(JRExporterParameter.OUTPUT_STREAM, output);

		//use the PDF export params bean
		if (exportParams != null) {
//			String printLocaleCode = jasperPrint.getLocaleCode();
//			Locale printLocale = LocaleHelper.getInstance().getLocale(printLocaleCode);
		}

                // For this implementation, we have to check most of the parameters by our self, we
                // cannot relais on the benefit of using the AbstractExporter...

		//Check for a RUN_OUTPUT_PAGE key and transform it in a PAGE_INDEX export parameter
                if (exportParameters.get(Argument.RUN_OUTPUT_PAGE) != null)
                {
                    int pageIndex = Integer.parseInt( (String)exportParameters.get(Argument.RUN_OUTPUT_PAGE));
                    pageIndex--; // transform a 1 index page to 0 indexed page...
                    exportParameters.put(JRExporterParameter.PAGE_INDEX, pageIndex );
                }else if(exportParameters.get(Argument.RUN_OUTPUT_PAGES) != null){
                    // cast is safe because of known key
                    @SuppressWarnings("unchecked")
                    ReportOutputPages pages = (ReportOutputPages) exportParameters.get(Argument.RUN_OUTPUT_PAGES);
                    if(pages.getPage() != null){
                        exportParameters.put(JRExporterParameter.PAGE_INDEX, pages.getPage() - 1);
                    } else if(pages.getStartPage() != null && pages.getEndPage() != null) {
                        exportParameters.put(JRExporterParameter.START_PAGE_INDEX, pages.getStartPage() - 1);
                        exportParameters.put(JRExporterParameter.END_PAGE_INDEX, pages.getEndPage() - 1);
                    }
                }

                engineService.exportToPdf(executionContext,
				reportUnitURI, exportParameters);

		return exportParameters;

	}

    @Override
    public String getContentType() {
        return "application/pdf";
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
	public PaginationParameters getPaginationParameters(JRPropertiesHolder propertiesHolder) {
		JRPropertiesUtil properties = JRPropertiesUtil.getInstance(jasperReportsContext);
		Boolean paginated = properties.getBooleanProperty(propertiesHolder, PdfExportParametersBean.PROPERTY_PDF_PAGINATED);
		Integer maxPageHeight = properties.getIntegerProperty(propertiesHolder, PdfExportParametersBean.PROPERTY_PDF_MAX_PAGE_HEIGHT);
		Integer maxPageWidth = properties.getIntegerProperty(propertiesHolder, PdfExportParametersBean.PROPERTY_PDF_MAX_PAGE_WIDTH);
		
		PaginationParameters pagination = new PaginationParameters();
		pagination.setPaginated(paginated);
		pagination.setMaxPageHeight(maxPageHeight);
		pagination.setMaxPageWidth(maxPageWidth);
		return pagination;
	}
}
