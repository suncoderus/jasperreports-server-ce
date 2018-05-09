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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Map;

import static net.sf.jasperreports.engine.JRParameter.REPORT_DATA_SOURCE;
import static net.sf.jasperreports.engine.JRParameter.REPORT_LOCALE;

/**
 * @author schubar
 * @version $Id: $
 */
public class ErrorTemplateReportService implements ReportDataSourceService {
    public static final String ERROR_PARAM = "error";
    public static final String TEMPLATE_REPO_URI = "/public/templates/ds_error_report.jrxml";

    private String jrxmlTemplateRepoUri;

    @Resource(name = "${bean.repositoryService}")
    protected RepositoryService repository;

    @Resource
    protected MessageSource messageSource;

    public void closeConnection() {
        // Do nothing
    }

    public void setReportParameterValues(Map parameterValues) {
        final Object error = parameterValues.get(ERROR_PARAM);

        if (error instanceof Exception) {
            Locale locale = (Locale) parameterValues.get(REPORT_LOCALE);

            parameterValues.put(REPORT_DATA_SOURCE,
                new ErrorReportDataSource((Exception) error, messageSource, locale));
        }
    }

    public FileResource getReportJrxml(ExecutionContext context) {
        return (FileResource) repository.getResource(context, getJrxmlTemplateRepoUri(), FileResource.class);
    }

    public String getJrxmlTemplateRepoUri() {
        return jrxmlTemplateRepoUri != null ? jrxmlTemplateRepoUri : TEMPLATE_REPO_URI;
    }

    public void setJrxmlTemplateRepoUri(String jrxmlTemplateRepoUri) {
        this.jrxmlTemplateRepoUri = jrxmlTemplateRepoUri;
    }

    public void setRepository(RepositoryService repository) {
        this.repository = repository;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
