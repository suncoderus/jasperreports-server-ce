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
 * Listener that contains methods called to notify of resource repository events.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 * @see RepositoryEventListenerRegistry
 */
@JasperServerAPI
public interface RepositoryEventListener {

	/**
	 * Method called when a resource deletion occurs in the repository.
	 * 
	 * @param resourceItf the interface that represents the type of the resource 
	 * @param resourceURI the repository path of the deleted resource
	 */
	void onResourceDelete(Class resourceItf, String resourceURI);
	
	/**
	 * Method called when a folder deletion occurs in the repository.
	 * 
	 * @param folderURI the repository path of the deleted folder
	 * @since 1.2.1
	 */
	void onFolderDelete(String folderURI);
	
}
