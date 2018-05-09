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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.jaspersoft.jasperserver.api.common.domain.ValidationError;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ValidationErrorsUtils {

	private final static ValidationErrorsUtils instance = new ValidationErrorsUtils();
	
	public static ValidationErrorsUtils instance() {
		return instance;
	}
	
	protected ValidationErrorsUtils() {
	}
	
	public void setErrors(Errors errors, ValidationErrors validationErrors, String[] fieldPrefixes) {
		Set bindErrorFields = new HashSet();
		for (Iterator it = errors.getAllErrors().iterator(); it.hasNext(); ) {
			ObjectError error = (ObjectError) it.next();
			if (error instanceof FieldError) {
				bindErrorFields.add(((FieldError) error).getField());
			}
		}
		
		if (validationErrors.isError()) {
			List errorList = validationErrors.getErrors();
			for (Iterator it = errorList.iterator(); it.hasNext();) {
				ValidationError error = (ValidationError) it.next();
				if (matches(error, fieldPrefixes)) {
					setError(errors, bindErrorFields, error);
				}
			}
		}
	}

	protected boolean matches(ValidationError error, String[] fieldPrefixes) {
		String field = error.getField();
		if (fieldPrefixes == null || field == null) {
			return true;
		}
		boolean match = false;
		for (int i = 0; !match && i < fieldPrefixes.length; i++) {
			String prefix = fieldPrefixes[i];
			match |= field.startsWith(prefix);
		}
		return match;
	}

	protected void setError(Errors errors, Set bindErrorFields, ValidationError error) {
		if (error.getField() == null) {
			errors.reject(error.getErrorCode(), error.getErrorArguments(), error.getDefaultMessage());
		} else if (!bindErrorFields.contains(error.getField())) {
			errors.rejectValue(error.getField(), error.getErrorCode(), error.getErrorArguments(), error.getDefaultMessage());
		}
	}

}
