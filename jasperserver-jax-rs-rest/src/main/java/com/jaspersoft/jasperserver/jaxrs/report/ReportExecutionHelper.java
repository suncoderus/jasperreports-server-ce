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
package com.jaspersoft.jasperserver.jaxrs.report;

import com.jaspersoft.jasperserver.remote.services.ReportOutputResource;
import com.jaspersoft.jasperserver.remote.services.RunReportService;

import javax.ws.rs.core.Response;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportExecutionHelper.java 26599 2012-12-10 13:04:23Z ykovalchyk $
 */
public class ReportExecutionHelper {

    /**
     * Build default report attachments prefix template,
     * which is used by RunReportService in case if attachmentsPrefix isn't specified explicitly
     *
     * @param infix normally this infix contains string "/rest_v2/reportExecutions/", but is extracted from the request
     *              to avoid hardcoding of jersey servlet path (i.e. "rest_v2")
     * @return report attachments prefix template
     */
    public static String getDefaultAttachmentsPrefixTemplateFromRequest(String infix) {
        return RunReportService.CONTEXT_PATH_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER + infix
                + RunReportService.REPORT_EXECUTION_ID_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER + "/exports/"
                + RunReportService.EXPORT_EXECUTION_ID_ATTACHMENTS_PREFIX_TEMPLATE_PLACEHOLDER + "/attachments/";
    }

    public static Response buildResponseFromOutputResource(ReportOutputResource outputResource){
        return buildResponseFromOutputResource(outputResource, false);
    }

    public static Response buildResponseFromOutputResource(ReportOutputResource outputResource, Boolean suppressContentDisposition) {
        final Response.ResponseBuilder responseBuilder;
        if (outputResource.getData() == null || outputResource.getData().length == 0) {
            responseBuilder = Response.noContent();
        } else {
            String contentType = outputResource.getContentType();
            responseBuilder = Response.ok(outputResource.getData(), contentType);
            if (!suppressContentDisposition && outputResource.getFileName() != null && !"text/html".equals(contentType)) {
                responseBuilder.header("Content-Disposition", "attachment; filename=\"" + outputResource.getFileName() + "\"");
            }
            if (outputResource.getOutputFinal() != null) {
                responseBuilder.header("output-final", outputResource.getOutputFinal());
            }
            final String pages = outputResource.getPages();
            if (pages != null && !pages.isEmpty()) {
                responseBuilder.header("report-pages", pages);
            }
        }
        return responseBuilder.build();
    }
}
