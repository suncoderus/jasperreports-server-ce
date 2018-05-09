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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.common.service.ServletContextInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceValidator;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: ResourceFactoryImpl.java 8408 2007-05-29 23:29:12Z melih $
 */
public class ReportUnitValidator extends BaseResourceValidator implements ResourceValidator
{

	/**
	 * 
	 */
	private RepositoryService repository;
	private ServletContextInformation servletContextInformation;

	
	/**
	 * 
	 */
	public RepositoryService getRepositoryService() 
	{
		return repository;
	}

	
	/**
	 * 
	 */
	public void setRepositoryService(RepositoryService repository) 
	{
		this.repository = repository;
	}

	
	/**
	 * 
	 */
	public ServletContextInformation getServletContextInformation() 
	{
		return servletContextInformation;
	}

	
	/**
	 * 
	 */
	public void setServletContextInformation(ServletContextInformation servletContextInformation) 
	{
		this.servletContextInformation = servletContextInformation;
	}

	
	/**
	 * 
	 */
	public ValidationErrors validate(Resource resource, ValidationErrorFilter filter)
	{
		ValidationErrors errors = new ValidationErrorsImpl();
		ReportUnit report = (ReportUnit)resource;
		
		validateLabel(report, filter, errors);
		validateName(report, filter, errors);
		validateDescription(report, filter, errors);
		validateInputControlView(report, filter, errors);
		validateRenderingView(report, filter, errors);
		validateMainReport(report, filter, errors);
		
		return errors;
	}


	private void validateInputControlView(ReportUnit report, ValidationErrorFilter filter, ValidationErrors errors) 
	{
		if (filter == null || filter.matchErrorField("reportUnit.inputControlRenderingView"))
		{
			String inputControlView = report.getInputControlRenderingView();
			if (inputControlView != null && inputControlView.length() > 0) 
			{
				if (inputControlView.length() > 100) 
				{
					errors.add(new ValidationErrorImpl("ReportDetailsValidator.error.too.long", 
							new Object[]{new Integer(100)}, null, "reportUnit.inputControlRenderingView"));
				}
				else if (!getServletContextInformation().jspExists(inputControlView)) 
				{
					errors.add(new ValidationErrorImpl("ReportDetailsValidator.error.inexisting.jsp", null, null, "reportUnit.inputControlRenderingView"));
				}
			}
		}
	}


	private void validateRenderingView(ReportUnit report, ValidationErrorFilter filter, ValidationErrors errors) 
	{
		if (filter == null || filter.matchErrorField("reportUnit.reportRenderingView"))
		{
			String reportRenderingView = report.getReportRenderingView();
			if (reportRenderingView != null && reportRenderingView.length() > 0) 
			{
				if (reportRenderingView.length() > 100) 
				{
					errors.add(new ValidationErrorImpl("ReportDetailsValidator.error.too.long", 
							new Object[]{new Integer(100)}, null, "reportUnit.reportRenderingView"));
				}
				else if (!getServletContextInformation().jspExists(reportRenderingView)) 
				{
					errors.add(new ValidationErrorImpl("ReportDetailsValidator.error.inexisting.jsp", null, null, "reportUnit.reportRenderingView"));
				}
			}
		}
	}


	protected void validateMainReport(ReportUnit report, 
			ValidationErrorFilter filter, ValidationErrors errors)
	{
		if (filter == null || filter.matchErrorField("reportUnit.mainReport"))
		{
			ResourceReference mainReport = report.getMainReport();
			if (mainReport == null
					|| (mainReport.isLocal() 
							? mainReport.getLocalResource() == null 
							: mainReport.getReferenceURI() == null))
			{
				errors.add(new ValidationErrorImpl("ReportDetailsValidator.error.not.empty", 
						null, null, "reportUnit.mainReport"));
			}
		}
	}

	protected String getErrorMessagePrefix() {
		return "ReportDetailsValidator.";
	}
	
	protected String getFieldPrefix() {
		return "reportUnit.";
	}

}
