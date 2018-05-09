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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.ExportUtil;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.services.ExportExecution;
import com.jaspersoft.jasperserver.remote.services.ExportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportExecution;
import com.jaspersoft.jasperserver.remote.services.ReportExecutionOptions;
import com.jaspersoft.jasperserver.remote.services.ReportOutputPages;
import com.jaspersoft.jasperserver.remote.services.ReportOutputResource;
import com.jaspersoft.jasperserver.remote.services.RunReportService;
import com.jaspersoft.jasperserver.remote.utils.AuditHelper;
import com.jaspersoft.jasperserver.war.util.JRHtmlExportUtils;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.export.AbstractHtmlExporter;
import net.sf.jasperreports.engine.export.HtmlResourceHandler;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.MapHtmlResourceHandler;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.renderers.util.RendererUtil;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public abstract class AbstractHtmlExportStrategy implements HtmlExportStrategy {
    private final static Log log = LogFactory.getLog(FullHtmlExportStrategy.class);
    @Resource(name = "jasperReportsRemoteContext")
    private JasperReportsContext jasperReportsContext;
    @Resource
    private AuditHelper auditHelper;
    @Value("${deploy.base.url:}")
    private String deployBaseUrl;
    @Resource
    private SecureExceptionHandler secureExceptionHandler;
    @Resource
    private CharacterEncodingProvider encodingProvider;

    @Override
    public void export(ReportExecution reportExecution, ExportExecution exportExecution, JasperPrint jasperPrint) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final String contextPath;
        final ExportExecutionOptions exportExecutionOptions = exportExecution.getOptions();
        String baseUrl = exportExecutionOptions.getBaseUrl();
        final ReportExecutionOptions reportExecutionOptions = reportExecution.getOptions();
        if (baseUrl != null) {
            // if baseUrl is specified fro this export execution, then use it first
            contextPath = baseUrl;
        } else if (deployBaseUrl != null && !deployBaseUrl.isEmpty()) {
            // no baseUrl is specified fro this export execution, but it is specified for JRS
            contextPath = deployBaseUrl;
        } else {
            // no baseUrl is specified, use contextPath from request or no prefix at all
            contextPath = (reportExecutionOptions.getContextPath() != null ? reportExecutionOptions.getContextPath() : "");
        }
        final AbstractHtmlExporter exporter = prepareExporter(reportExecution, exportExecution, contextPath);
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
        exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, encodingProvider.getCharacterEncoding());
        // collecting the images into a map
        Map<String, byte[]> imagesMap = new LinkedHashMap<String, byte[]>();
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);

        String attachmentsPrefix = exportExecutionOptions.getAttachmentsPrefix() != null ?
                exportExecutionOptions.getAttachmentsPrefix() : reportExecutionOptions.getDefaultAttachmentsPrefixTemplate();
        if (attachmentsPrefix != null) {
            attachmentsPrefix = attachmentsPrefix
                    .replace(RunReportService.CONTEXT_PATH_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER, reportExecutionOptions.getContextPath() != null ? reportExecutionOptions.getContextPath() : "")
                    .replace(RunReportService.REPORT_EXECUTION_ID_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER, reportExecution.getRequestId())
                    .replace(RunReportService.EXPORT_EXECUTION_ID_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER, exportExecution.getId());
        }

        String resourcePattern = ((attachmentsPrefix != null) ? attachmentsPrefix : "images/") + "{0}";
        HtmlResourceHandler resourceHandler =
                new MapHtmlResourceHandler(
                        new WebHtmlResourceHandler(resourcePattern),
                        imagesMap
                );
        exporter.setImageHandler(resourceHandler);
        exporter.setResourceHandler(resourceHandler);
        final ReportOutputPages pages = exportExecutionOptions.getPages();
        try {
            exporter.exportReport();
        } catch (RemoteException e) {
            auditHelper.addExceptionToAllAuditEvents(e);
            throw e;
        } catch (JRRuntimeException e) {
            if (JRAbstractExporter.EXCEPTION_MESSAGE_KEY_PAGE_INDEX_OUT_OF_RANGE.equals(e.getMessageKey())
            		|| JRAbstractExporter.EXCEPTION_MESSAGE_KEY_START_PAGE_INDEX_OUT_OF_RANGE.equals(e.getMessageKey())
            		|| JRAbstractExporter.EXCEPTION_MESSAGE_KEY_END_PAGE_INDEX_OUT_OF_RANGE.equals(e.getMessageKey())) {
                final String pagesString = pages.toString();
                throw new RemoteException(new ErrorDescriptor().setMessage(
                        "Page number out of range : " + pagesString + " of "
                        + (reportExecution.getTotalPages() != null ? " Total pages: " + reportExecution.getTotalPages() : "")
                        + " (while exporting the report)").setErrorCode("page.number.out.of.range")
                        .addParameters(pages, "" + reportExecution.getTotalPages()));
            } else {
                throw new RemoteException(e, secureExceptionHandler);
            }
        } catch (Exception e) {
            log.debug("Error exporting report", e);
            auditHelper.addExceptionToAllAuditEvents(e);
            throw new RemoteException(
                    new ErrorDescriptor()
                            .setErrorCode("webservices.error.errorExportingReportUnit").setParameters(e.getMessage()), e
            );
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                log.error("caught exception: " + ex.getMessage(), ex);
            }
        }
        exportExecution.setOutputResource(new ReportOutputResource().setContentType("text/html")
                .setData(outputStream.toByteArray()).setPages(pages != null ? pages.toString() : null));
        putImages(exporter.getParameters(), exportExecution.getAttachments());
    }

    protected AbstractHtmlExporter prepareExporter(ReportExecution reportExecution, ExportExecution exportExecution, final String contextPath) {
        // use new instance of jasper reports context to allow modifications
        SimpleJasperReportsContext context = new SimpleJasperReportsContext();
        context.setParent(jasperReportsContext);
        final AbstractHtmlExporter exporter = ExportUtil.getInstance(context).createHtmlExporter();
        final ExportExecutionOptions exportExecutionOptions = exportExecution.getOptions();
        ReportOutputPages pages = exportExecutionOptions.getPages();
        if(pages != null) {
            if (pages.getPage() != null) {
                exporter.setParameter(JRExporterParameter.PAGE_INDEX, pages.getPage() - 1);
            } else if (pages.getStartPage() != null && pages.getEndPage() != null) {
                exporter.setParameter(JRExporterParameter.START_PAGE_INDEX, pages.getStartPage() - 1);
                exporter.setParameter(JRExporterParameter.END_PAGE_INDEX, pages.getEndPage() - 1);
            }
        }
        // JR requires HttpServletRequest instance to get contextPath from it.
        // Seems it's the only field queried from the request object.
        // We need to do the trick with proxy to send contextPath to JR without having real request object.
        // We can't just inject HttpServletRequest, because in case of asynchronous export this class is invoked
        // from a different thread without valid request bound to it.
        HttpServletRequest proxy = (HttpServletRequest) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object result = null;
                        if ("getContextPath".equals(method.getName())) {
                            result = contextPath;
                        }
                        return result;
                    }
                });
        exporter.setParameter(JRHtmlExportUtils.PARAMETER_HTTP_REQUEST, proxy);
        exporter.setFontHandler(new WebHtmlResourceHandler(contextPath + "/reportresource?&font={0}"));// html exporter font handler no longer used in JR; consider removing

        ReportContext reportContext = reportExecution.getFinalReportUnitResult().getReportContext();
        if (reportContext != null) 
        {
            reportContext.setParameterValue("contextPath", contextPath);// context path to be used by the font handler in JSON exporter
        }
        
        return exporter;
    }


    /**
     * Place images to output container.
     *
     * @param exportParameters - export result, contains images
     * @param outputContainer  - output container to fill with images
     * @throws com.jaspersoft.jasperserver.remote.exception.RemoteException if any error occurs
     */
    protected void putImages(Map<JRExporterParameter, Object> exportParameters, Map<String, ReportOutputResource> outputContainer) throws RemoteException {
        try {
            // cast is safe because of known parameter key
            @SuppressWarnings("unchecked")
            Map<String, byte[]> imagesMap = (Map<String, byte[]>) exportParameters.get(JRHtmlExporterParameter.IMAGES_MAP);
            if (imagesMap != null && !imagesMap.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("imagesMap : " + Arrays.asList(imagesMap.keySet().toArray()));
                }
                for (String name : imagesMap.keySet()) {
                    byte[] data = imagesMap.get(name);
                    if (log.isDebugEnabled()) {
                        log.debug("Adding image for HTML: " + name);
                    }
					outputContainer.put(name, new ReportOutputResource()
                            .setContentType(imageMimeType(data)).setData(data).setFileName(name));
                }
            }
        } catch (Throwable e) {
			ErrorDescriptor ed = secureExceptionHandler.handleException(e, new ErrorDescriptor().setErrorCode("webservices.error.errorAddingImage"));

			log.error(ed.getMessage(), e);
            throw new RemoteException(ed, e);
        }
    }

	protected String imageMimeType(byte[] data) {
		String mimeType = null;
		ImageTypeEnum imageType = JRTypeSniffer.getImageTypeValue(data);
		if (imageType != ImageTypeEnum.UNKNOWN) {
			mimeType = imageType.getMimeType();
		} else if (RendererUtil.getInstance(jasperReportsContext).isSvgData(data)) {
			mimeType = RendererUtil.SVG_MIME_TYPE;
		}
		return mimeType;
	}
}
