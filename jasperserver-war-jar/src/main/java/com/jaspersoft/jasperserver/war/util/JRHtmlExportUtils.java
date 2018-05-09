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

package com.jaspersoft.jasperserver.war.util;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.ExportUtil;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JRHtmlExportUtils 
{

	/**
	 * @deprecated Replaced by {@link ExportUtil#HTTP_SERVLET_REQUEST}.
	 */
	public static final net.sf.jasperreports.engine.export.JRHtmlExporterParameter PARAMETER_HTTP_REQUEST =
		// derived class required because of protected constructor
		new net.sf.jasperreports.engine.export.JRHtmlExporterParameter("HttpServletRequest") {}; 
	
}
