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

import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;

/**
 *
 * @author gtoffoli
 */
public class ServiceValidationException extends ServiceException {

        private final String objectName;
	private final ValidationErrors errors;

	public ServiceValidationException(String objectName, ValidationErrors errors) {
		super(GENERAL_ERROR2, errors.toString());

		this.objectName = objectName;
		this.errors = errors;
	}

	public ValidationErrors getErrors() {
		return errors;
	}

	public String getObjectName() {
		return objectName;
	}
}
