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


/**
 * Factory of repository resource validator instances.
 * 
 * <p>
 * The repository service does not currently use this factory.
 * </p>
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: RepositoryService.java 8408 2007-05-29 23:29:12Z melih $
 * @since 2.1.0
 */
@JasperServerAPI
public interface ResourceValidatorFactory
{

	/**
	 * Retrieves a resource validator for a specific resource type.
	 * 
	 * @param resourceClassName the name of the resource class 
	 * @return a resource validator for the specified resource type
	 */
	public ResourceValidator getValidator(String resourceClassName);

}
