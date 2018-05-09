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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.api.search.SearchSorter;
import com.jaspersoft.jasperserver.api.search.TransformerFactory;

import java.util.List;
import java.util.Map;
import java.util.Collections;
/**
 * User: Zakhar.Tomchenco
 * Date: 7/25/12
 * Time: 4:20 PM
 */
public class HibernateRepositoryServiceMock implements HibernateRepositoryService {
    @Override
    public Resource getResource(ExecutionContext context, String uri) {
        return null; 
    }

    @Override
    public Resource getResource(ExecutionContext context, String uri, Class resourceType) {
        return null; 
    }

    @Override
    public <T extends Resource> T makeResourceCopy(ExecutionContext context, String uri, int version, Class<T> resourceType, String copyURI) {
        return null; 
    }

    @Override
    public FileResourceData getResourceData(ExecutionContext context, String uri) throws JSResourceNotFoundException {
        return null; 
    }

    @Override
    public FileResourceData getContentResourceData(ExecutionContext context, String uri) throws JSResourceNotFoundException {
        return null; 
    }

    @Override
    public Resource copyRenameResource(ExecutionContext context, String sourceURI, String destinationURI, String label) {
        return null;
    }

    @Override
    public Folder getFolder(ExecutionContext context, String uri) {
        return null; 
    }

    @Override
    public void saveFolder(ExecutionContext context, Folder folder) {
       
    }

    @Override
    public List getAllFolders(ExecutionContext context) {
        return null; 
    }


    public int getFoldersCount(String folderURI) {
        return 0;
    }

    @Override
    public List getAllSubfolders(ExecutionContext context, String parentURI) {
        return null; 
    }

    @Override
    public List getSubFolders(ExecutionContext context, String folderURI) {
        return null; 
    }

    @Override
    public boolean folderExists(ExecutionContext context, String uri) {
        return false; 
    }

    @Override
    public ValidationErrors validateResource(ExecutionContext context, Resource resource, ValidationErrorFilter filter) {
        return null; 
    }

    @Override
    public ValidationErrors validateFolder(ExecutionContext context, Folder folder, ValidationErrorFilter filter) {
        return null; 
    }

    @Override
    public void saveResource(ExecutionContext context, Resource resource) {
       
    }

    @Override
    public void saveResourceNoFlush(ExecutionContext context, Resource resource) {

    }

    @Override
    public void saveResource(ExecutionContext context, Resource resource, boolean updateCreationDate) {
       
    }

    @Override
    public void deleteResource(ExecutionContext context, String uri) {
       
    }

    @Override
    public void deleteResourceNoFlush(ExecutionContext context, String uri) {

    }

    @Override
    public void deleteFolder(ExecutionContext context, String uri) {
       
    }

    @Override
    public void delete(ExecutionContext context, String[] resourceURIs, String[] folderURIs) {
       
    }

    @Override
    public ResourceLookup[] findResource(ExecutionContext context, FilterCriteria criteria) {
        return new ResourceLookup[0]; 
    }

    @Override
    public ResourceLookup[] findResources(ExecutionContext context, FilterCriteria[] criteria) {
        return new ResourceLookup[0]; 
    }

    @Override
    public List loadResourcesList(FilterCriteria filterCriteria) {
        return null; 
    }

    @Override
    public List loadResourcesList(ExecutionContext context, FilterCriteria filterCriteria) {
        return null; 
    }

    @Override
    public List loadResourcesList(ExecutionContext context, FilterCriteria[] filterCriteria) {
        return null; 
    }

    @Override
    public Map<Class, Integer> loadResourcesMapCount(ExecutionContext context, String text, List<Class> resourceTypeList, List<SearchFilter> filters, Map<String, SearchFilter> typeSpecificFilters, SearchSorter sorter, TransformerFactory transformerFactory) {
        return null; 
    }

    @Override
    public List<ResourceLookup> getResourcesByIdList(List<Long> idList) {
        return null; 
    }

    @Override
    public List<Long> getResourcesIds(ExecutionContext context, String text, Class type, Class aClass, List<SearchFilter> filters, Map<String, SearchFilter> typeSpecificFilters, SearchSorter sorter, TransformerFactory transformerFactory, int firstResult, int maxResult) {
        return null; 
    }

    @Override
    public List loadClientResources(FilterCriteria filterCriteria) {
        return null; 
    }

    @Override
    public Resource newResource(ExecutionContext context, Class _class) {
        return null; 
    }

    @Override
    public RepoResource findByURI(Class persistentClass, String uri, boolean required) {
        return null; 
    }

    @Override
    public RepoResource getRepoResource(Resource resource) {
        return null; 
    }

    @Override
    public Object getPersistentObject(Object clientObject) {
        return null; 
    }

    @Override
    public String getChildrenFolderName(String resourceName) {
        return null; 
    }

    @Override
    public boolean resourceExists(ExecutionContext executionContext, String uri) {
        return false; 
    }

    @Override
    public boolean resourceExists(ExecutionContext executionContext, String uri, Class resourceType) {
        return false; 
    }

    @Override
    public boolean resourceExists(ExecutionContext executionContext, FilterCriteria filterCriteria) {
        return false; 
    }

    @Override
    public boolean repositoryPathExists(ExecutionContext executionContext, String uri) {
        return false; 
    }

    @Override
    public void hideFolder(String uri) {
       
    }

    @Override
    public void unhideFolder(String uri) {
       
    }

    @Override
    public void moveFolder(ExecutionContext context, String sourceURI, String destinationFolderURI) {
       
    }

    @Override
    public void moveResource(ExecutionContext context, String sourceURI, String destinationFolderURI) {
       
    }

    @Override
    public Resource copyResource(ExecutionContext context, String sourceURI, String destinationURI) {
        return null; 
    }

    @Override
    public Folder copyFolder(ExecutionContext context, String sourceURI, String destinationURI) {
        return null; 
    }

    @Override
    public Folder copyRenameFolder(ExecutionContext context, String sourceURI, String destinationURI, String label) {
        return null;
    }

    @Override
    public void copyResources(ExecutionContext context, String[] resources, String destinationFolder) {
       
    }

    @Override
    public List<ResourceLookup> getResources(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory, List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory, int current, int max) {
        return null; 
    }

    @Override
    public int getResourcesCount(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory, List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory) {
        return 0; 
    }

    @Override
    public List getResourcesCountList(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory, List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory) {
        return Collections.EMPTY_LIST;
    }
    
    @Override
    public List<ResourceLookup> getDependentResources(ExecutionContext context, String uri, SearchCriteriaFactory searchCriteriaFactory, int current, int max) {
        return null; 
    }

    @Override
    public Resource getResourceUnsecure(ExecutionContext context, String uri) {
        return null; 
    }

    @Override
    public ResourceLookup getResourceLookupUnsecure(ExecutionContext context, String uri) {
        return null; 
    }

    @Override
    public Folder getFolderUnsecure(ExecutionContext context, String uri) {
        return null; 
    }

    @Override
    public void replaceFileResourceData(String uri, DataContainer data) {
       
    }

    @Override
    public String transformPathToExternal(String internalPath) {
        return null;
    }

    @Override
    public List<RepoResource> findRepoResources(ExecutionContext context, FilterCriteria criteria) {
        return null;
    }

    @Override
    public List<RepoFolder> findRepoFolders(ExecutionContext context, FilterCriteria criteria) {
        return null;
    }

    @Override
    public void deleteAll(ExecutionContext context, List<RepoResource> resources, List<RepoFolder> folders) {
    }

    @Override
    public void saveOrUpdateAll(ExecutionContext context, List<RepoResource> resources, List<RepoFolder> folders) {
    }

	@Override
	public List<? extends Object> getResources(ExecutionContext context,
			boolean asLookups, String hqlQueryString) {
		// TODO Auto-generated method stub
		return null;
	}
}
