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
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.service.ImplementationObjectFactory;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * Factory of repository resource objects.
 * 
 * <p>
 * This factory can be used to create concrete resource resource objects for
 * a resource type defined by an interface.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @author Sherman Wood
 * @version $Id$
 * @since 1.0
 */
@JasperServerAPI
public interface ResourceFactory extends ImplementationObjectFactory {

	/**
	 * Creates a resource object of a specified type.
	 * 
	 * <p>
	 * The resource type is an interface.
	 * A mapping that defines resource implementation classes is used to 
	 * determine the actual type of the create resource object.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param _class the interface representing the resource type
	 * @return a resource object of the specified type
	 */
	Resource newResource(ExecutionContext context, Class _class);

	/**
	 * Creates a resource object of a type specified by a key.
	 * 
     * @param context the caller execution context
	 * @param id the key of the resource type
	 * @return a resource object of the specified type
	 */
	Resource newResource(ExecutionContext context, String id);

}
