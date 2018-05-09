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
package com.jaspersoft.jasperserver.war.validation;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.SimpleValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.service.ServletContextInformation;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.ValidationUtil;
import com.jaspersoft.jasperserver.war.common.JasperServerConst;
import com.jaspersoft.jasperserver.war.dto.FileResourceWrapper;
import com.jaspersoft.jasperserver.war.dto.InputControlWrapper;
import com.jaspersoft.jasperserver.war.dto.ReportUnitWrapper;

/**
 * A validator for our form backing object. Note that this validator supports
 * individual validation of each step in the "create report flow".
 */
public class ReportDetailsValidator implements Validator {

	private RepositoryService repository;
	private ServletContextInformation servletContextInformation;

	public RepositoryService getRepository()
	{
		return repository;
	}

	public void setRepository(RepositoryService repository)
	{
		this.repository = repository;
	}

	public boolean supports(Class clazz) {
		return ReportUnitWrapper.class.isAssignableFrom(clazz);
	}

	public void validate(Object o, Errors errors) {
		ReportUnitWrapper reportDetails = (ReportUnitWrapper) o;
		validateNameLabelDesc(reportDetails, errors);
		validateJrxmlUpload(reportDetails, errors);
		validateResources(reportDetails, errors);
		validateReportViewForm(reportDetails, errors);
	}

	public void validateNameLabelDesc(ReportUnitWrapper ruWrapper, Errors uiErrors) 
	{
		SimpleValidationErrorFilter filter = new SimpleValidationErrorFilter();
		
		filter.addErrorFieldToInclude("reportUnit.name");
		filter.addErrorFieldToInclude("reportUnit.label");
		filter.addErrorFieldToInclude("reportUnit.description");
		if (!ruWrapper.isAloneNewMode())
		{
			filter.addErrorCodeToExclude("ReportDetailsValidator.error.duplicate");
		}
		
		ValidationErrors errors = getRepository().validateResource(null, ruWrapper.getReportUnit(), filter);
		
		ValidationUtil.copyErrors(errors, uiErrors);
	}

	/* FIXME does not seem to be used anymore; check methods with the same name in the project
	public void validateURIString(ReportUnitWrapper reportUnit, Errors errors) {
		if (reportUnit.getSource() == null) {
			errors.rejectValue("source", "ReportDetailsValidator.error.invalid.jrxml");
		} else {
			if (reportUnit.getSource().equals(
					JasperServerConst.FIELD_CHOICE_CONT_REPO)) {
				if (reportUnit.getJrxmlUri() == null
						|| reportUnit.getJrxmlUri().length() == 0) {
					errors.rejectValue("jrxmlUri", "ReportDetailsValidator.error.not.reusable");
				}
			}
		}

	}
	*/

	public void validateJrxmlUpload(ReportUnitWrapper wrapper, Errors errors) {
		if (wrapper.getSource() == null) {
			errors.rejectValue("source", "ReportDetailsValidator.error.invalid.jrxml");
		} else {
			if (wrapper.getSource().equals(
					JasperServerConst.FIELD_CHOICE_FILE_SYSTEM)
					&& !wrapper.isJrxmlLocated()) {
				// FileResource mainReport = (FileResource)
				// wrapper.getReportUnit().getMainReport().getLocalResource();
				if (wrapper.getJrxmlData() == null
						|| wrapper.getJrxmlData().length == 0) {
					errors.rejectValue("jrxmlData", "ReportDetailsValidator.error.invalid.jrxml");
				}
			}
		}
	}

	public void validateResources(ReportUnitWrapper wrapper, Errors errors) {
		boolean allResLocated = true;
		boolean allControlsLocated = true;
		List sugRes = wrapper.getSuggestedResources();
		if (sugRes != null && !sugRes.isEmpty()) {
			for (int i = 0; i < sugRes.size(); i++) {
				FileResourceWrapper resWrap = (FileResourceWrapper) sugRes
						.get(i);
				if (!resWrap.isLocated()) {
					allResLocated = false;
					break;
				}
			}
		}
		List sugContr = wrapper.getSuggestedControls();
		if (sugContr != null && !sugContr.isEmpty()) {
			for (int i = 0; i < sugContr.size(); i++) {
				InputControlWrapper icWrap = (InputControlWrapper) sugContr
						.get(i);
				if (!icWrap.isLocated()) {
					allControlsLocated = false;
					break;
				}
			}
		}
		if (!allControlsLocated && !allResLocated)
			errors.rejectValue("validationMessage", "ReportDetailsValidator.error.controls.resources.located");
		else if (!allControlsLocated)
			errors.rejectValue("validationMessage", "ReportDetailsValidator.error.controls.located");
		else if (!allResLocated)
			errors.rejectValue("validationMessage", "ReportDetailsValidator.error.resources.located");
		
		validateInputControlView(wrapper, errors);
	}

	protected void validateInputControlView(ReportUnitWrapper ruWrapper, Errors uiErrors) 
	{
		SimpleValidationErrorFilter filter = new SimpleValidationErrorFilter();
		filter.addErrorFieldToInclude("reportUnit.inputControlRenderingView");
		ValidationErrors errors = getRepository().validateResource(null, ruWrapper.getReportUnit(), filter);
		ValidationUtil.copyErrors(errors, uiErrors);
	}

	public void validateReportViewForm(ReportUnitWrapper ruWrapper, Errors uiErrors) 
	{
		SimpleValidationErrorFilter filter = new SimpleValidationErrorFilter();
		filter.addErrorFieldToInclude("reportUnit.reportRenderingView");
		ValidationErrors errors = getRepository().validateResource(null, ruWrapper.getReportUnit(), filter);
		ValidationUtil.copyErrors(errors, uiErrors);
	}

	public ServletContextInformation getServletContextInformation() {
		return servletContextInformation;
	}

	public void setServletContextInformation(
			ServletContextInformation servletContextInformation) {
		this.servletContextInformation = servletContextInformation;
	}
}
  
  