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
package com.jaspersoft.jasperserver.war.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.AbstractView;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.war.action.hyperlinks.HyperlinkProducerFactoryFlowFactory;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import net.sf.jasperreports.engine.export.JRXml4SwfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleReportExportConfiguration;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ReportImageController.java 8408 2007-05-29 23:29:12Z melih $
 */
public class XmlExportController  implements Controller
{

	private SessionObjectSerieAccessor jasperPrintAccessor;
	private String jasperPrintNameParameter;
	private String pageParameter;
	private HyperlinkProducerFactoryFlowFactory hyperlinkProducerFactory;
	private JasperReportsContext jasperReportsContext;

	protected static class XmlExportView extends AbstractView 
	{
		private final byte[] xmlData;

		public XmlExportView(byte[] xmlData) 
		{
			this.xmlData = xmlData;
		}

		protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception 
		{
			if (xmlData != null && xmlData.length > 0) 
			{
				response.setContentType("text/xml");
				response.setContentLength(xmlData.length);
				ServletOutputStream ouputStream = response.getOutputStream();
				ouputStream.write(xmlData, 0, xmlData.length);
				ouputStream.flush();
				ouputStream.close();
			} else {
				response.getOutputStream().close();
			}
		}
		
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws JRException
	{
		String jasperPrintName = request.getParameter(getJasperPrintNameParameter());
		ReportUnitResult result = (ReportUnitResult) getJasperPrintAccessor().getObject(request, jasperPrintName);
		JasperPrint jasperPrint = (result == null ? null : result.getJasperPrint());
		if (jasperPrint == null) {
			throw new JSException("jsexception.jasperprint.not.found", new Object[] {jasperPrintName});
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRXml4SwfExporter exporter = new JRXml4SwfExporter(getJasperReportsContext());
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleXmlExporterOutput(baos, "UTF-8"));

		SimpleReportExportConfiguration xmlReportConfig = new SimpleReportExportConfiguration();
		
		if (getHyperlinkProducerFactory() != null) 
		{
			JRHyperlinkProducerFactory factory = getHyperlinkProducerFactory().getHyperlinkProducerFactory(request, response);
			if (factory != null)
			{
				xmlReportConfig.setHyperlinkProducerFactory(factory);
			}
		}
		
		int pageIndex = -1;
		try
		{
			pageIndex = Integer.parseInt(request.getParameter("page"));
		}
		catch(Exception e)
		{
		}
		if (pageIndex >= 0 && jasperPrint.getPages().size() > 0)
		{
			xmlReportConfig.setPageIndex(pageIndex);
		}

		exporter.setConfiguration(xmlReportConfig);
		
		try 
		{
			exporter.exportReport();
		} 
		catch (JRException e) 
		{
			throw new JSException(e);
		}
		finally
		{
			try
			{
				baos.close();
			}
			catch (IOException ex)
			{
			}
		}
		
		View view = new XmlExportView(baos.toByteArray());
		
		return new ModelAndView(view);
	}

	public SessionObjectSerieAccessor getJasperPrintAccessor() {
		return jasperPrintAccessor;
	}

	public void setJasperPrintAccessor(
			SessionObjectSerieAccessor jasperPrintAccessor) {
		this.jasperPrintAccessor = jasperPrintAccessor;
	}

	public String getJasperPrintNameParameter() {
		return jasperPrintNameParameter;
	}

	public void setJasperPrintNameParameter(String jasperPrintNameAttribute) {
		this.jasperPrintNameParameter = jasperPrintNameAttribute;
	}

	public String getPageParameter() {
		return pageParameter;
	}

	public void setPageParameter(String pageParameter) {
		this.pageParameter = pageParameter;
	}

	public HyperlinkProducerFactoryFlowFactory getHyperlinkProducerFactory() {
		return hyperlinkProducerFactory;
	}

	public void setHyperlinkProducerFactory(HyperlinkProducerFactoryFlowFactory hyperlinkProducerFactory) {
		this.hyperlinkProducerFactory = hyperlinkProducerFactory;
	}

	public JasperReportsContext getJasperReportsContext() {
		return jasperReportsContext;
	}

	public void setJasperReportsContext(JasperReportsContext jasperReportsContext) {
		this.jasperReportsContext = jasperReportsContext;
	}

}
