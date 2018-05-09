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
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;

/**
 * Repository service to be used internally for operations that should
 * skip any checks related to the resource permissions.
 * 
 * @author Giulio Toffoli
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: RepositoryUnsecure.java 5175 2006-10-18 13:45:03Z giulio $
 * @since 1.2.1
 * @see RepositoryService
 */
@JasperServerAPI
public interface RepositoryUnsecure {

	/**
	 * Retrieves the full details of a resource from the repository.
	 * 
     * @param context the caller execution context
	 * @param uri the path of the resource in the repository
	 * @return the resource if found or <code>null</code> otherwise
	 * @see RepositoryService#getResource(ExecutionContext, String)
	 */
	public Resource getResourceUnsecure(ExecutionContext context, String uri);

	/**
	 * Retrieves the summary resource information of a resource from the 
	 * repository.
	 * 
	 * <p>
	 * This method should preferred over
	 * {@link #getResourceUnsecure(ExecutionContext, String)} when the full
	 * resource details are not required.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param uri the path of the resource in the repository
	 * @return the resource summary information if found or <code>null</code> 
	 * otherwise
	 */
	public ResourceLookup getResourceLookupUnsecure(ExecutionContext context, String uri);
	
	/**
	 * Returns the details of a repository folder.
	 * 
     * @param context the caller execution context
	 * @param uri the path of the folder in the repository
	 * @return the folder details, or <code>null</code> if the folder was not 
	 * found
	 * @see RepositoryService#getFolder(ExecutionContext, String)
	 */
	public Folder getFolderUnsecure(ExecutionContext context, String uri);

	public void replaceFileResourceData(String uri, DataContainer data);
	
}
