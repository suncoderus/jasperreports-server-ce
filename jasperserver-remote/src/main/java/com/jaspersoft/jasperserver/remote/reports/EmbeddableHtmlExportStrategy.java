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
import com.jaspersoft.jasperserver.war.action.hyperlinks.HyperlinkProducerFactoryFlowFactory;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.export.AbstractHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@Service
public class EmbeddableHtmlExportStrategy extends AbstractHtmlExportStrategy {
    @Resource(name = "viewReportHyperlinkProducerFactory")
    private HyperlinkProducerFactoryFlowFactory hyperlinkProducerFactory;
    @Override
    protected AbstractHtmlExporter prepareExporter(final ReportExecution reportExecution, ExportExecution exportExecution, String contextPath) {
        final AbstractHtmlExporter exporter = super.prepareExporter(reportExecution, exportExecution, contextPath);
        exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
        exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
        // there is no valid request and response in the async export context.
        // Let's proxy request to forward required parameters to hyperlink producers
        HttpServletRequest requestProxy = (HttpServletRequest) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        if ("getAttribute".equals(method.getName())) {
                            result = "reportResult".equals(args[0]) ? reportExecution.getReportUnitResult() : null;
                        }
                        return result;
                    }
                });
        exporter.setParameter(JRExporterParameter.HYPERLINK_PRODUCER_FACTORY, hyperlinkProducerFactory.getHyperlinkProducerFactory(requestProxy, null));
        ReportContext reportContext = reportExecution.getFinalReportUnitResult().getReportContext();
        if (reportContext != null) {
            reportContext.setParameterValue(ReportContext.REQUEST_PARAMETER_APPLICATION_DOMAIN, exportExecution.getOptions().getBaseUrl());
            exporter.setReportContext(reportContext);
        }
        return exporter;
    }
}
