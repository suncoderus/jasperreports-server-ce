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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.common.XlsExportParametersBean;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.ExportUtil;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Giulio Toffoli (original sanda zaharia (shertage@users.sourceforge.net))
 * @version $Id$
 */
@Service("remoteXlsExporter")
@Scope("prototype")
public class XlsExporter extends AbstractExporter {

    public static final String ONE_PAGE_PER_SHEET = "onePagePerSheet";
    @Resource(name = "xlsExportParameters")
    private XlsExportParametersBean exportParams;

    @Resource
    protected MessageSource messageSource;

    public XlsExporter() {
    	super(XlsExportParametersBean.PROPERTY_XLS_PAGINATED);
	}
    
    @Override
    public JRExporter createExporter() throws Exception {
        return ExportUtil.getInstance(getJasperReportsContext()).createXlsExporter();
    }

    @Override
    public void configureExporter(JRExporter exporter, HashMap exportParameters) throws Exception {
        if (exportParams != null) {
            if (exportParams.isOverrideReportHints())
                exporter.setParameter(JRExporterParameter.PARAMETERS_OVERRIDE_REPORT_HINTS, Boolean.TRUE);
            if (exportParams.getOnePagePerSheet() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, exportParams.getOnePagePerSheet());
            if(exportParameters.get(ONE_PAGE_PER_SHEET) != null)
                exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.valueOf(String.valueOf(getSingleParameterValue(ONE_PAGE_PER_SHEET, exportParameters))));
            if (exportParams.getDetectCellType() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, exportParams.getDetectCellType());
            if (exportParams.getRemoveEmptySpaceBetweenRows() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, exportParams.getRemoveEmptySpaceBetweenRows());
            if (exportParams.getRemoveEmptySpaceBetweenColumns() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, exportParams.getRemoveEmptySpaceBetweenColumns());
            if (exportParams.getWhitePageBackground() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, exportParams.getWhitePageBackground());
            if (exportParams.getIgnoreGraphics() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, exportParams.getIgnoreGraphics());
            if (exportParams.getCollapseRowSpan() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, exportParams.getCollapseRowSpan());
            if (exportParams.getIgnoreCellBorder() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, exportParams.getIgnoreCellBorder());
            if (exportParams.getFontSizeFixEnabled() != null)
                exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, exportParams.getFontSizeFixEnabled());
            if (exportParams.getMaximumRowsPerSheet() != null)
                exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, exportParams.getMaximumRowsPerSheet());
            if (exportParams.getXlsFormatPatternsMap() != null && !exportParams.getXlsFormatPatternsMap().isEmpty())
                exporter.setParameter(JRXlsExporterParameter.FORMAT_PATTERNS_MAP, exportParams.getXlsFormatPatternsMap());
        }
        exporter.setParameter(JRXlsExporterParameter.CREATE_CUSTOM_PALETTE, Boolean.TRUE);
    }

    @Override
    public Map<JRExporterParameter, Object> exportReport(JasperPrint jasperPrint, OutputStream output, EngineService engineService, HashMap exportParameters, ExecutionContext executionContext, String reportUnitURI) throws Exception {
        return super.exportReport(jasperPrint, output, engineService, exportParameters, executionContext, reportUnitURI);
    }

    /**
     * @param exportParams The exportParams to set.
     */
    public void setExportParams(XlsExportParametersBean exportParams) {
        this.exportParams = exportParams;
    }

    @Override
    public String getContentType() {
        return "application/xls";
    }
}
