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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryUnsecure;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;

import java.util.List;

/**
 * @author swood
 *
 */
public interface HibernateRepositoryService extends RepositoryService, RepositoryUnsecure {
	/**
	 * 
	 * @param context
	 * @param uri
	 * @return the resource if found or null otherwise
	 */
	public Resource getResource(ExecutionContext context, String uri);


	/**
	 * 
	 * @param context
	 * @param uri
	 * @return
	 * @throws JSResourceNotFoundException
	 */
	public FileResourceData getResourceData(ExecutionContext context, String uri) throws JSResourceNotFoundException;

	

	/**
	 * 
	 */
	public void saveResource(ExecutionContext context, Resource resource);
	

	public void deleteResource(ExecutionContext context, String uri);

	
	public void deleteFolder(ExecutionContext context, String uri);

	
	public void delete(ExecutionContext context, String resourceURIs[], String folderURIs[]);
	
	
	/**
	 * Given filtering criteria, find relevant Resources.
	 * 
	 * @param context
	 * @param criteria
	 * @return Array of found Resources
	 */
	public ResourceLookup[] findResource(ExecutionContext context, FilterCriteria criteria);
	
	/*
	 * Return a Resource that does not yet contain content.
	 * 
	 * @param context
	 * @param class - class of resource to create
	 * @return Resource
	 */
	public Resource newResource(ExecutionContext context, Class _class);

	public RepoResource findByURI(Class persistentClass, String uri, boolean required);
	
	public RepoResource getRepoResource(Resource resource);
    // Based on the client object, find the related persistent object
	public Object getPersistentObject(Object clientObject);

    /**
     * Searches for RepoResource entities based on search criteria.
     * @param context
     * @param criteria
     * @return
     */
    public List<RepoResource> findRepoResources(ExecutionContext context, FilterCriteria criteria);

    /**
     * Searches for RepoFolder entities based on search criteria.
     * @param context
     * @param criteria
     * @return
     */
    public List<RepoFolder> findRepoFolders(ExecutionContext context, FilterCriteria criteria);

    /**
     * Performs bulk delete of resources and/or folders
     * @param context
     * @param resources list of resources to delete, can be null
     * @param folders list of folders to delete, can be null
     */
    public void deleteAll(ExecutionContext context, List<RepoResource> resources, List<RepoFolder> folders);

    /**
     * Performs bulk save or update of resources and/or folders
     * @param context
     * @param resources list of resources, can be null
     * @param folders list of folders, can be null
     */
    public void saveOrUpdateAll(ExecutionContext context, List<RepoResource> resources, List<RepoFolder> folders);

}
