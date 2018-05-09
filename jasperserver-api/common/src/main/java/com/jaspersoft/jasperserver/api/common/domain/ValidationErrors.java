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
package com.jaspersoft.jasperserver.api.common.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.List;

/**
 * The result of a validation operation, consisting of a set of validation
 * errors.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 */
@JasperServerAPI
public interface ValidationErrors {

	/**
	 * Determines whether any error has been detected during the validation.
	 * 
	 * @return <code>true</code> if the validation resulted in error(s)
	 * @see #getErrors()
	 */
	boolean isError();
	
	/**
	 * Returns the list of errors detected during the validation.
	 * 
	 * @return the list of errors detected during the validation, empty if no
	 * errors were found 
	 */
	List getErrors();

	/**
	 * Adds an error to the list of validation errors.
	 * 
	 * @param error the error to add
	 * @since 1.2.0
	 */
	void add(ValidationError error);

	/**
	 * Adds list of errors to the list of validation errors.
	 *
	 * @param errors the error to add
	 * @since 1.2.0
	 */
	void addErrors(List errors);

	/**
	 * Removes any previously added errors that match a specified code and 
	 * field. 
	 * 
	 * @param code the code of the errors to remove
	 * @param field the field for which errors are to be removed
	 * @since 3.0.0
	 */
	void removeError(String code, String field);
	
}
