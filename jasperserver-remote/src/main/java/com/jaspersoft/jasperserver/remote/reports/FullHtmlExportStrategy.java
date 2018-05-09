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
package com.jaspersoft.jasperserver.remote.reports;

import com.jaspersoft.jasperserver.remote.services.ExportExecution;
import com.jaspersoft.jasperserver.remote.services.ReportExecution;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.export.AbstractHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class FullHtmlExportStrategy extends AbstractHtmlExportStrategy {
    @Resource
    private List<String> htmlReportHeaderIncludes;
    @Override
    protected AbstractHtmlExporter prepareExporter(ReportExecution reportExecution, ExportExecution exportExecution, String contextPath) {
        final AbstractHtmlExporter exporter = super.prepareExporter(reportExecution, exportExecution, contextPath);

        final boolean allowInlineScripts = exportExecution.getOptions().isAllowInlineScripts();
        if (reportExecution.getOptions().isInteractive() && allowInlineScripts) {
            exporter.getJasperReportsContext().setProperty("com.jaspersoft.jasperreports.highcharts.html.export.type", "standalone");
        }
        if (!allowInlineScripts) {
            ReportContext reportContext = reportExecution.getFinalReportUnitResult().getReportContext();
            if (reportContext != null) {
                reportContext.setParameterValue(ReportContext.REQUEST_PARAMETER_APPLICATION_DOMAIN, exportExecution.getOptions().getBaseUrl());
                exporter.setReportContext(reportContext);
            }
        }
        StringBuilder htmlHeader = new StringBuilder();
        for (String currentInclude : htmlReportHeaderIncludes) {
            htmlHeader.append(currentInclude.replaceAll("\\{contextPath\\}", contextPath));
        }
        exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, htmlHeader.toString());
        return exporter;
    }
}
