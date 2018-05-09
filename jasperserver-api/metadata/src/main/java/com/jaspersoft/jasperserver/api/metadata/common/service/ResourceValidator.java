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
package com.jaspersoft.jasperserver.api.metadata.common.service;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * Validator of repository resources.
 * 
 * <p>
 * A validator is specific to a resource type and is used to verify that
 * resources of that type obey certain rules.
 * It is important to note that resources are not automatically validated
 * before being saved in the repository, therefore it is the responsibility
 * of the code that saves the resources to validate them before the save
 * operation is performed.
 * </p>
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepositoryService.java 8408 2007-05-29 23:29:12Z melih $
 * @see RepositoryService#validateResource(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, Resource, ValidationErrorFilter)
 * @since 2.1.0
 */
@JasperServerAPI
public interface ResourceValidator
{

	/**
	 * Checks whether a resource follows the rules imposed by this validator.
	 * 
	 * @param resource the resource to check
	 * @param filter a filter that specifies a subset of errors that should be
	 * reported by the validator
	 * @return a validation object which contains errors if any found
	 * @see ValidationErrors#isError()
	 */
	public ValidationErrors validate(Resource resource, ValidationErrorFilter filter);

}
