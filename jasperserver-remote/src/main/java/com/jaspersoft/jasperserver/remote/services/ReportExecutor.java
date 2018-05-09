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
package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.remote.ServiceException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportExecutor.java 26599 2012-12-10 13:04:23Z ykovalchyk $
 */
public interface ReportExecutor {
    String getContentType(String outputFormat);
    boolean isRunnableResource(Resource resource);
    ReportUnitResult runReport(String reportUnitUri, Map<String, Object> parameters,
            ReportExecutionOptions reportExecutionOptions) throws RemoteException;
    Map<JRExporterParameter, Object> exportReport(String reportUnitURI, JasperPrint jasperPrint, String format, OutputStream output,
            HashMap exportParameters) throws ServiceException;
    JasperReportsContext getJasperReportsContext(Boolean interactive);
	PaginationParameters getExportPaginationParameters(String reportUnitURI, JasperPrint jasperPrint, String outputFormat);
}
