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

package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.AbstractHtmlExporter;
import net.sf.jasperreports.export.HtmlExporterConfiguration;
import net.sf.jasperreports.export.HtmlReportConfiguration;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRHtmlExportUtils.java 19932 2010-12-11 15:24:29Z tmatyashovsky $
 * @deprecated Replaced by {@link ExportUtil}.
 */
public class HtmlExportUtil 
{

	/**
	 * @deprecated Replaced by {@link ExportUtil#getHtmlExporter(JasperReportsContext)}.
	 */
	public static AbstractHtmlExporter<HtmlReportConfiguration, HtmlExporterConfiguration> getHtmlExporter(JasperReportsContext jasperReportsContext)
	{
		return ExportUtil.getInstance(jasperReportsContext).createHtmlExporter();
	}
		
}
