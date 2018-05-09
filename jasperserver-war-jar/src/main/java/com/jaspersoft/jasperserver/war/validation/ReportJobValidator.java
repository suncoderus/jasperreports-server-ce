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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.war.action.ReportJobEditAction;
import com.jaspersoft.jasperserver.war.util.ValidationErrorsUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportJobValidator implements Validator {

	private ReportSchedulingService schedulingService;
	private ValidationErrorsUtils validationUtils = ValidationErrorsUtils.instance();
	
	public ReportSchedulingService getSchedulingService() {
		return schedulingService;
	}

	public void setSchedulingService(ReportSchedulingService schedulingService) {
		this.schedulingService = schedulingService;
	}

	public ValidationErrorsUtils getValidationUtils() {
		return validationUtils;
	}

	public void setValidationUtils(ValidationErrorsUtils validationUtils) {
		this.validationUtils = validationUtils;
	}

	public boolean supports(Class clazz) {
		return ReportJob.class.isAssignableFrom(clazz);
	}

	public void validateJobDetails(ReportJob job, Errors errors) {
		validate(job, errors, ReportJobEditAction.VALIDATION_FIELDS_DETAILS);
	}

	public void validateJobTrigger(ReportJob job, Errors errors) {
		validate(job, errors, ReportJobEditAction.VALIDATION_FIELDS_TRIGGER);
	}

	public void validateJobOutput(ReportJob job, Errors errors) {
		validate(job, errors, ReportJobEditAction.VALIDATION_FIELDS_OUTPUT);
	}

	public void validate(Object obj, Errors errors) {
		validate((ReportJob) obj, errors, null);
	}
	
	protected void validate(ReportJob job, Errors errors, String[] fieldPrefixes) {
		ValidationErrors validationErrors = schedulingService.validateJob(null, job);//TODO context
		validationUtils.setErrors(errors, validationErrors, fieldPrefixes);
	}

}
