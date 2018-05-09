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

import java.util.List;
import java.util.Map;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateRepositoryServiceImpl.java 11286 2007-12-17 17:00:30Z lucian $
 */
public interface RepositorySecurityChecker {

	/** Filters allResources.
	 *  Populates removableResources with the resources that can be deleted.
	 *  Populates editableResources with the resources that can be edited.
	 */
	void filterResources(List allResources, Map removableResources,
			Map editableResources);

	/** Filters resource.
	 *  Adds resource to removableResources if it can be deleted.
	 *  Adds resource to editableResources if it can be edited.
	 */
	void filterResource(Resource resource, Map removableResources,
			Map editableResources);

	/** Checks whether the given resource can be edited */
	boolean isEditable(Resource resource);

	/** Checks whether the given resource can be deleted */
	boolean isRemovable(Resource resource);

	boolean isResourceReadable(String uri);

	boolean isFolderReadable(String uri);

    boolean isAdministrable(String uri);

    boolean isAdministrable(Resource resource);

    boolean isExecutable(Resource resource);

}