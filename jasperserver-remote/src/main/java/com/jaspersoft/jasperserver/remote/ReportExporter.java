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

package com.jaspersoft.jasperserver.remote;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.PaginationParameters;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JasperPrint;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @@version $Id: WSExporter.java 19933 2010-12-11 15:27:37Z tmatyashovsky $
 */
public interface ReportExporter extends Serializable {
	
	public Map<JRExporterParameter, Object> exportReport (
			JasperPrint jasperPrint, 
			OutputStream output, 
			EngineService engineService, 
			HashMap exportParameters,
			ExecutionContext executionContext,
			String reportUnitURI
			) throws Exception;

    public String getContentType();
    
	public PaginationParameters getPaginationParameters(JRPropertiesHolder propertiesHolder);

}
