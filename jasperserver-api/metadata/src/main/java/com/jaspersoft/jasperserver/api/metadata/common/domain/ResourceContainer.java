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

package com.jaspersoft.jasperserver.api.metadata.common.domain;

import java.util.List;


/**
 * @author bob
 *
 */
public interface ResourceContainer extends Resource {

	/**
	 * Gets a list of {@link ResourceReference} objects for each of the resources contained by the ReportUnit
	 * @return list of report unit resources
	 */
	public abstract List<ResourceReference> getResources();

	/**
	 * get the local resource with the given name
	 * @param name name of the resource within the report unit
	 * @return a local resource
	 */
	public abstract FileResource getResourceLocal(String name);

	/**
	 * set the list of resources belonging to this report unit. The list should
	 * contain ResourceReference objects referring to the actual resources.
	 * @param resources list of ResourceReferences
	 */
	public abstract void setResources(List<ResourceReference> resources);

	/**
	 * add a FileResource to the report unit
	 * @param resource resource to be added
	 */
	public abstract void addResource(FileResource resource);

	/**
	 * add a FileResource to the report unit as a ResourceReference
	 * @param resourceReference reference to a FileResource in the repository
	 */
	public abstract void addResource(ResourceReference resourceReference);

	/**
	 * add a FileResource to the report unit using its URI within the repository
	 * @param referenceURI URI of a FileResource in the repository
	 */
	public abstract void addResourceReference(String referenceURI);

}