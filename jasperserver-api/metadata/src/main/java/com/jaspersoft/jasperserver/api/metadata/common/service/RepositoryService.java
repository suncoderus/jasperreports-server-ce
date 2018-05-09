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
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.api.search.SearchSorter;
import com.jaspersoft.jasperserver.api.search.TransformerFactory;

import java.util.List;
import java.util.Map;


/**
 * Top level class for accessing the repository metadata.
 * 
 * <p>
 * This class has various methods for retrieving resources from the repository
 * and for uploading resources to the repository.
 * </p>
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @author Sherman Wood
 * @author Lucian Chirita
 * @author Tony Kavanagh
 * @author Ionut Nedelcu
 * @author Bob Tinsman
 * @author Andrew Sokolnikov
 * @author Stas Chubar
 * @author Yuri Plakosh
 * @version $Id$
 * @since 1.0
 */
@JasperServerAPI
public interface RepositoryService
{
    /**
    * To be used as an attribute of execution context and mark the
    * current operation as importing or overwriting the resource
    */
   public static final String IS_IMPORTING = "isImporting";
   public static final String IS_OVERWRITING = "isOverwriting";

	/**
	 * Retrieves the full details of a resource from the repository.
	 * 
	 * <p>
	 * For resources that include binary data (such as {@link FileResource} and
	 * {@link ContentResource}), the data will not be returned by this method
	 * and a separate call needs to be made if the data is required.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param uri the path of the resource in the repository
	 * @return the resource if found or <code>null</code> otherwise
	 * @see #getResourceData(ExecutionContext, String)
	 * @see #getContentResourceData(ExecutionContext, String)
	 * @see #getResource(ExecutionContext, String, Class)
	 */
	public Resource getResource(ExecutionContext context, String uri);
	
	/**
	 * Retrieves the full details of a resource from the repository.
	 * 
	 * <p>
	 * This method is preferred over {@link #getResource(ExecutionContext, String)}
	 * when the expected type of the resource is known.
	 * If a resource exists in the repository at the specified path but is not
	 * of the expected type, <code>null</code> will be returned.
	 * </p>
	 * 
	 * 
     * @param context the caller execution context
	 * @param uri the path of the resource in the repository
	 * @param resourceType the expected type of the resource represented by
	 * a resource interface
	 * @return the resource if found or <code>null</code> otherwise
	 * @since 2.0.0
	 */
	public Resource getResource(ExecutionContext context, String uri, Class resourceType);

	public <T extends Resource> T makeResourceCopy(ExecutionContext context, String uri, int version, 
			Class<T> resourceType, String copyURI);

	/**
	 * Retrieves the data associated to a repository file resource.
	 *
	 * <p>
	 * File resource references will be traversed until reaching the resource
	 * which contains data.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param uri the path of the resource in the repository
	 * @return a data container for the file resource
	 * @throws JSResourceNotFoundException if a file resource is not found at
	 * the specified path
	 * @see FileResource
	 * @see FileResource#isReference()
	 */
	public FileResourceData getResourceData(ExecutionContext context, String uri) throws JSResourceNotFoundException;

	/**
	 * Retrieves the data associated to a repository content resource.
	 * 
	 * <p>
	 * Data will be returned only for the top level content resource.
	 * If the resource has subresources, separate calls will have to be made
	 * to fetch the data for each subresource.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param uri the path of the resource in the repository
	 * @return a data container for the content resource
	 * @throws JSResourceNotFoundException if a file resource is not found at
	 * the specified path
	 * @see ContentResource
	 * @see ContentResource#getResources()
	 */
	public FileResourceData getContentResourceData(ExecutionContext context, String uri) throws JSResourceNotFoundException;

	/**
	 * Returns the details of a repository folder.
	 * 
     * @param context the caller execution context
	 * @param uri the path of the folder in the repository
	 * @return the folder details, or <code>null</code> if the folder was not 
	 * found
	 */
	public Folder getFolder(ExecutionContext context, String uri);

	/**
	 * Creates a new folder or updates the details of an existing folder in the 
	 * repository.
	 * 
	 * <p>
	 * If the folder object is marked as new, then a new folder is created
	 * in the repository at the path specified by the object.
	 * Otherwise, the folder is located in the repository and its details
	 * are updated according to the folder object.
     * </p>
     * 
     * <p>
     * If the "IS_IMPORTING" parameter is set in the executionContext, then this method
     * sets the updateDate from the imported folder properties.
     * If the "IS_IMPORTING" is not set, then sets the update date
     * to the current state.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param folder the folder to create or update
	 * @see Resource#isNew()
	 */
	public void saveFolder(ExecutionContext context, Folder folder);

	/**
	 * Lists all non-hidden folders in the repository.
	 * 
     * @param context the caller execution context
	 * @return a list of {@link Folder} objects sorted by repository paths
	 */
	public List getAllFolders(ExecutionContext context);

    public int getFoldersCount(String parentURI);

	/**
	 * Returns a list of all folders under a specific parent folders.
         *
	 * <p>
	 * The list returned by this method contains all visible folders
	 * that have the specified folder as ancestor, including the specified
	 * folder itself.
         *
	 * @param context the execution context
	 * @param parentURI the URI of the parent folder
	 * @return a list of {@link Folder} instances sorted by repository paths
	 * @since 3.5.0
	 */
	public List getAllSubfolders(ExecutionContext context, String parentURI);

	/**
	 * Lists all direct subfolders of a repository folder.
	 * 
	 * <p>
	 * Hidden folders are not included.
	 * If the parent folder is not found in the repository, an empty list
	 * is returned.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param folderURI the path of the parent folder
	 * @return a list of {@link Folder} instances sorted by names
	 */
	public List getSubFolders(ExecutionContext context, String folderURI);
	
	/**
	 * Determines if a folder exists at a specific path in the repository.
	 * 
     * @param context the caller execution context
	 * @param uri the path to look for in the repository
	 * @return <code>true</code> if a folder with the specified path exists
	 * in the repository
	 * @since 1.2.0
	 * @see #repositoryPathExists(ExecutionContext, String)
	 */
	public boolean folderExists(ExecutionContext context, String uri);

	/**
	 * Checks whether a resource follows the rules imposed by the validator
	 * configured for the specific resource type.
	 * 
     * @param context the caller execution context
	 * @param resource the resource to check
	 * @param filter a filter that specifies a subset of errors that should be
	 * reported by the validator
	 * @return a validation object which contains errors if any found
	 * @since 2.1.0
	 * @see ResourceValidator
	 * @see ValidationErrors#isError()
	 */
	public ValidationErrors validateResource(ExecutionContext context, Resource resource, ValidationErrorFilter filter);
	
	/**
	 * Checks whether a folder follows the rules imposed by the validator
	 * configured for folders.
	 * 
     * @param context the caller execution context
	 * @param folder the folder to check
	 * @param filter a filter that specifies a subset of errors that should be
	 * reported by the validator
	 * @return a validation object which contains errors if any found
	 * @since 3.0.0
	 * @see ValidationErrors#isError()
	 */
	public ValidationErrors validateFolder(ExecutionContext context, Folder folder, ValidationErrorFilter filter);
	

	/**
	 * Saves a new resource into the repository or updates an existing one.
     *
     * <p>
     * If the "IS_IMPORTING" parameter is set in the executionContext, then this
     * method sets a current creationDate to the resource and sets the updateDate
     * from the imported resource. If the "IS_OVERWRITING" parameter is set, changes
     * the creationDate and the updateDate to the current state.
     * </p>
     *
	 * <p>
	 * Calling this is the same as calling
	 * {@link #saveResource(ExecutionContext, Resource, boolean) saveResource(context, resource, false)}.
	 * </p>
	 * 
	 * @param context the execution context
	 * @param resource the resource to be saved or updated
	 */
	public void saveResource(ExecutionContext context, Resource resource);

    /**
     * Same as saveResource(ExecutionContext context, Resource resource); but don't force flush
     * Useful in cascade operations
     *
     * @since 5.0.1
     * @param context
     * @param resource
     */
    public void saveResourceNoFlush(ExecutionContext context, Resource resource);


    /**
	 * Saves a new resource into the repository or updates an existing one.
	 * 
	 * <p>
	 * If the resource object is marked as new, it will be created in the
	 * repository by copying all the details from the resource object.
	 * If a resource already exists at the path set in the resource object,
	 * the save operation will fail.
	 * </p>
	 * 
	 * <p>
	 * If the resource is not marked as new, the repository will attempt locate 
	 * it at the path set in the resource object.
	 * If the resource is found in the repository, its version numbers is compared
	 * with the version number set in the resource object; if the version do not
	 * match the operation will fail.
	 * If the versions match, the repository resource will be updated with all
	 * the details passed in the resource object.
	 * All subresources will be updated along with the top-level resource.
	 * Subresources present in the repository resource but not in the updated 
	 * resource obejct are deleted, and newly added subresources are saved in
	 * the repository.
	 * </p>
	 * 
	 * <p>
	 * This method is identical to {@link #saveResource(ExecutionContext, Resource)},
	 * except that it allows the caller to specify whether the resource creation
	 * date is to be updated in the case when an existing resource is being
	 * updated.
	 * </p>
	 * 
	 * @param context the execution context
	 * @param resource the resource to be saved or updated
	 * @param updateCreationDate used to specify that when updating an existing resource,
	 * its creation date needs to be updated as well
	 * 
	 * @see Resource#isNew()
	 * @see Resource#getVersion()
	 * @see Resource#getCreationDate()
	 * @since 3.5.0
	 */
    @Deprecated
	public void saveResource(ExecutionContext context, Resource resource,
			boolean updateCreationDate);

	/**
	 * Deletes a resource from the repository.
	 * 
	 * <p>
	 * If a resource is not found at the specified path, the operation will
	 * result in an exception.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param uri the repository path of the resource to delete
	 */
	public void deleteResource(ExecutionContext context, String uri);

    /**
     * Same as deleteResource(ExecutionContext context, String uri) but performs no flush
     *
     * @since 5.0.1
     * @param context
     * @param uri
     */
    public void deleteResourceNoFlush(ExecutionContext context, String uri);

    /**
	 * Deletes a folder from the repository.
	 * 
	 * <p>
	 * If a folder is not found at the specified path, the operation will
	 * result in an exception.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param uri the repository path of the folder to delete
	 */
	public void deleteFolder(ExecutionContext context, String uri);

	/**
	 * Deletes several resources and folders from the repository in a single
	 * operation.
	 * 
	 * <p>
	 * If any of the paths are not found in the repository, the operation will
	 * result in an exception and all the changes will be rolled back.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param resourceURIs the repository paths of the resources to delete
	 * @param folderURIs the repository paths of the folders to delete
	 * @deprecated The spring method-based security configuration is incorrect for this method;
	 * you will get an exception if you call it. Use deleteFolder() or deleteResource() instead.
	 */
	public void delete(ExecutionContext context, String resourceURIs[], String folderURIs[]);
	
	
	/**
	 * Find resources that match a set of filter criteria.
	 * 
     * @param context the caller execution context
	 * @param criteria a filter used to search resources  
	 * @return array of found resources
	 */
	public ResourceLookup[] findResource(ExecutionContext context, FilterCriteria criteria);
	
	/**
	 * Find resources that match one of several filter criteria.
	 * 
	 * <p>
	 * Resources are located for each of the filter criteria, and collected
	 * in a single list.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param criteria array of filters used to search resources
	 * @return array of found resources sorted by repository paths
	 */
	public ResourceLookup[] findResources(ExecutionContext context, FilterCriteria[] criteria);

	/**
	 * Find resources that match a set of filter criteria.
	 * 
	 * @param filterCriteria a filter used to search resources
	 * @return a list of {@link ResourceLookup} objects sorted by repository paths
	 */
	public List loadResourcesList(final FilterCriteria filterCriteria);

	/**
	 * Find resources that match a set of filter criteria.
	 *
     * @param context the caller execution context
	 * @param filterCriteria a filter used to search resources
	 * @return a list of {@link ResourceLookup} objects sorted by repository paths
	 */
	public List loadResourcesList(ExecutionContext context, FilterCriteria filterCriteria);

	/**
	 * Find resources that match one of several filter criteria.
	 * 
	 * <p>
	 * Resources are located for each of the filter criteria, and collected
	 * in a single list.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param filterCriteria array of filters used to search resources
	 * @return a list of {@link ResourceLookup} objects sorted by repository paths
	 * @since 2.0.0
	 */
	public List loadResourcesList(ExecutionContext context, FilterCriteria[] filterCriteria);

	/**
	 * Determines a count of resources per resource types.
	 * 
     * @param context the caller execution context
	 * @param text a text to search for in the resource labels and descriptions
	 * @param resourceTypeList the types of resources to search for
	 * @param filters list of filters to apply to the results
	 * @param typeSpecificFilters filters per resource type to apply to the results
	 * @param sorter specifies the sorting to apply to the results
	 * @param transformerFactory a result transformer factory
	 * @return a map containing count of resources that match the filters 
	 * per resource types
	 * @since 3.7.0
	 */
    public Map<Class, Integer> loadResourcesMapCount(ExecutionContext context, String text, List<Class> resourceTypeList,
            List<SearchFilter> filters, Map<String, SearchFilter> typeSpecificFilters, SearchSorter sorter,
            TransformerFactory transformerFactory);

    /**
     * Lists resources by internal IDs.
     * 
     * @param idList the list of IDs of the resources to be listed
     * @return a list of {@link ResourceLookup} objects
	 * @since 3.7.0
     */
    public List<ResourceLookup> getResourcesByIdList(List<Long> idList);

    /**
     * Returns internal IDs of resources that match specified filters.
     * 
     * @param context the caller execution context
     * @param text a text to search for in the resource labels and descriptions
     * @param type the type of resources to list
     * @param aClass the persistent resource type
     * @param filters list of filters to apply to the results
     * @param typeSpecificFilters filters per resource type to apply to the results
     * @param sorter specifies the sorting to apply to the results
     * @param transformerFactory a result transformer factory
     * @param firstResult the index of the first result to return
     * @param maxResult the maximum number of results to retrieve
     * @return a list of IDs of resources that match the filters
	 * @since 3.7.0
     */
    public List<Long> getResourcesIds(ExecutionContext context, String text, Class type, Class aClass,
                                                  List<SearchFilter> filters, Map<String, SearchFilter> typeSpecificFilters,
                                                  SearchSorter sorter, TransformerFactory transformerFactory,
                                                  int firstResult, int maxResult);
    
    /**
     * Lists resource details for a set of filter criteria.
     * 
     * @param filterCriteria the criteria to search the repository for
     * @return a list of resource detail objects sorted by repository paths
     */
	public List loadClientResources(FilterCriteria filterCriteria);

	/**
	 * Instantiate an empty resource of a given type.
	 * 
	 * <p>
	 * The resource type is an interface; the repository will use its implementation
	 * class mapping to determine the actual type of the object to instantiate.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param _class the type of resource to instantiate
	 * @return an empty resource of the given type
	 */
	public Resource newResource(ExecutionContext context, Class _class);

	/**
	 * Determines the name of the hidden folder in which child resources
	 * of a given resource will be located.
	 * 
	 * @param resourceName the name of the parent resource
	 * @return the name of the resource children folder
	 */
	public String getChildrenFolderName(String resourceName);

	/**
	 * Determines if a resource of a specific type exists at a specific path in the 
	 * repository.
	 * 
     * @param executionContext the caller execution context
	 * @param uri the path to look for in the repository
	 * @return <code>true</code> if a resource exists in the repository at the
	 * specified path
	 * @see #resourceExists(ExecutionContext, String, Class)
	 */
	public boolean resourceExists(ExecutionContext executionContext, String uri);

	/**
	 * Determines if a resource of a specific type exists at a specific path in the 
	 * repository.
	 * 
     * @param executionContext the caller execution context
	 * @param uri the path to look for in the repository
	 * @param resourceType the type of the resource to look for
	 * @return <code>true</code> if a resource of the specified type exists 
	 * in the repository at the specified path
	 * @since 2.0.0
	 */
	public boolean resourceExists(ExecutionContext executionContext, String uri, Class resourceType);

	/**
	 * Determines if any resource that match a set of filter criteria exist 
	 * in the repository.
	 * 
     * @param executionContext the caller execution context
	 * @param filterCriteria the filter criteria
	 * @return <code>true</code> if any resources match the filter criteria
	 * @since 1.2.1
	 */
	public boolean resourceExists(ExecutionContext executionContext, FilterCriteria filterCriteria);

	/**
	 * Determines whether a resource or folder having a specified URI exists in the repository.
	 * 
	 * <p>
	 * Repository entities are uniquely identified by their URI.  This method can be used to check
	 * whether a URI is already present in the repository.
	 * </p>
	 * 
	 * @param executionContext the execution context
	 * @param uri the URI
	 * @return <code>true</code> iff the URI is present in the repository
	 * @since 2.1.0
	 */
	public boolean repositoryPathExists(ExecutionContext executionContext, String uri);
	
	/**
	 * Sets a given folder as hidden, so it won't appear in the results for any 
	 * repository operation.
	 * 
	 * @param uri the repository path of the folder
	 * @since 2.1.0
	 */
	void hideFolder(String uri);
	
	/**
	 * Unset a given folder as hidden.
	 * 
	 * @param uri the repository path of the folder
	 * @since 2.1.0
	 * @see #hideFolder(String)
	 */
	void unhideFolder(String uri);
	
	/**
	 * Moves a folder to another location in the repository.
	 * 
	 * <p>
	 * The name of the moved folder will be preserved.
	 * If the name already exists under the destination folder, the operation 
	 * will result in an exception.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param sourceURI the path of the folder to move
	 * @param destinationFolderURI the path of the folder under which the folder
	 * will be moved
	 * @since 3.0.0
	 */
	void moveFolder(ExecutionContext context, String sourceURI, String destinationFolderURI);

	/**
	 * Moves a resource to another location in the repository.
	 * 
	 * <p>
	 * The name of the moved resource will be preserved.
	 * If the name already exists under the destination folder, the operation 
	 * will result in an exception.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param sourceURI the path of the resource to move
	 * @param destinationFolderURI the path of the folder under which the resource
	 * will be moved
	 * @since 3.0.0
	 */
	void moveResource(ExecutionContext context, String sourceURI, String destinationFolderURI);

	/**
	 * Copies a resource to a new location.
	 * 
	 * <p>
	 * If the copy path already exists in the repository, a new name will be
	 * automatically generated for the copied resource.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param sourceURI the path of the resource to copy
	 * @param destinationURI the path at which to create the copy in the 
	 * repository, including the resource copy name
	 * @return the copied resource
	 * @since 3.0.0
	 */
	Resource copyResource(ExecutionContext context, String sourceURI, String destinationURI);

	/**
	 * Copies a resource to a new location and change it's label.
	 *
     * @param context the caller execution context
	 * @param sourceURI the path of the resource to copy
	 * @param destinationURI the path at which to create the copy in the
	 * repository, including the resource copy name
	 * @param label the new label for copy. Nullable, no renaming happens if null.
	 * @return the copied resource
	 * @since 6.3.0
	 */
	Resource copyRenameResource(ExecutionContext context, String sourceURI, String destinationURI, String label);

	/**
	 * Copies a repository folder to a new location.
	 * 
	 * <p>
	 * If the copy path already exists in the repository, a new name will be
	 * automatically generated for the copied folder.
	 * </p>
	 * 
	 * <p>
	 * The subfolders and resources under the copied folder are copied recursively.
	 * References between resources in the source folder are translated to the 
	 * resources in the new folder; references to resources outside the source
	 * folder are preserved unchanged.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param sourceURI the path of the folder to copy
	 * @param destinationURI the path at which to create the copy in the 
	 * repository, including the folder copy name
	 * @return the details of the folder copy
	 * @since 3.0.0
	 */
	Folder copyFolder(ExecutionContext context, String sourceURI, String destinationURI);

	/**
	 * Copies a repository folder to a new location and change it's label..
	 *
	 * <p>
	 * The subfolders and resources under the copied folder are copied recursively.
	 * References between resources in the source folder are translated to the
	 * resources in the new folder; references to resources outside the source
	 * folder are preserved unchanged.
	 * </p>
	 *
     * @param context the caller execution context
	 * @param sourceURI the path of the folder to copy
	 * @param destinationURI the path at which to create the copy in the
	 * repository, including the folder copy name 	 *
	 * @param label the new label for copy. Nullable, no renaming happens if null.
	 * @return the details of the folder copy
	 * @since 6.3.0
	 */
	Folder copyRenameFolder(ExecutionContext context, String sourceURI, String destinationURI, String label);
	
	/**
	 * Copies several resources into a destination folder.
	 * 
	 * <p>
	 * Names will be automatically assigned for the copied resources.
	 * If possible, the names of the original resources will be preserved.
	 * If any of the names already exists in the destination folder, a new
	 * name will be automatically set for the resource copy.
	 * </p>
	 * 
     * @param context the caller execution context
	 * @param resources the URIs of the resources to copy
	 * @param destinationFolder the URI of the destination folder
	 * @since 3.5.0
	 */
	void copyResources(ExecutionContext context, 
			String[] resources, String destinationFolder);
			
	List<ResourceLookup> getResources(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory,
            List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory, int current,
            int max);

    int getResourcesCount(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory,
            List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory);

    //EGS: sumtotal
    List<Object[]> getResourcesCountList(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory,
            List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory);
    
    /**
     * Looking for resources which depend on the specified resource.
     * Current implementation is looking for ReportUnits only.
     *
     * Note: Hidden folders are excluded...
     *
     * @param context
     * @param uri
     * @param searchCriteriaFactory
     * @param current - page number (used for pagination)
     * @param max - resources per page or resources to be loaded id current is 0
     * @return
     */
    List<ResourceLookup> getDependentResources(ExecutionContext context,
                                               String uri, SearchCriteriaFactory searchCriteriaFactory,
                                               int current, int max);

 /*   List<ResourceLookup> getDomainDependentResources(ExecutionContext context,
            String uri, SearchCriteriaFactory searchCriteriaFactory,
            int current, int max);
 */   
    
    
    /**
     * Transform  resource lookup path to relative URIs
     * @param internalPath
     * @return  external path
     */
    String transformPathToExternal(String internalPath);

    /**
     * run an HQL query and return either resource objects or lookups
     * @param context
     * @param asLookups
     * @param hqlQueryString
     * @return
     */
	public abstract List<? extends Object> getResources(ExecutionContext context, boolean asLookups, String hqlQueryString);
}
