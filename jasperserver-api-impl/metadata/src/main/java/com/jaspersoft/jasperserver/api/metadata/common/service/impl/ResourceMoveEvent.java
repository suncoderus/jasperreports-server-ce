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

/**
 * @author Lucian Chirita
 *
 */
public class ResourceMoveEvent {

	private final Class resourceType;
	private final String oldBaseURI;
	private final String newBaseURI;
	private final String oldResourceURI;
	private final String newResourceURI;

	public ResourceMoveEvent(Class resourceType,
			String oldBaseURI, String newBaseURI,
			String oldResourceURI, String newResourceURI) {
		this.resourceType = resourceType;
		this.oldBaseURI = oldBaseURI;
		this.newBaseURI = newBaseURI;
		this.oldResourceURI = oldResourceURI;
		this.newResourceURI = newResourceURI;
	}

	/**
	 * @return Returns the oldBaseURI.
	 */
	public String getOldBaseURI() {
		return oldBaseURI;
	}
	
	/**
	 * @return Returns the newBaseURI.
	 */
	public String getNewBaseURI() {
		return newBaseURI;
	}
	
	/**
	 * @return Returns the oldFolderURI.
	 */
	public String getOldResourceURI() {
		return oldResourceURI;
	}
	
	/**
	 * @return Returns the newFolderURI.
	 */
	public String getNewResourceURI() {
		return newResourceURI;
	}

	/**
	 * @return Returns the resourceType.
	 */
	public Class getResourceType() {
		return resourceType;
	}
	
}
