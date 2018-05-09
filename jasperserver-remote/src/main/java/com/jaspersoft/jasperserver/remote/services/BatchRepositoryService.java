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

package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.search.service.RepositorySearchResult;

import java.util.List;

/**
 * <p>Performs batch operations with resources in transaction</p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public interface BatchRepositoryService {

    /**
     * Removes resources with uris in one transaction
     *
     * @param uris of resources to remove
     * @throws AccessDeniedException if current user cannot delete at least one resource
     */
    void deleteResources(List<String> uris) throws ResourceNotFoundException, AccessDeniedException;

    /**
     * Searches resources
     *
     * @param q search query
     * @param folderUri folder, in which search should be
     * @param excludeFolders folders from this list will be excluded, NOTE: uris are relative to organization, i.e. "/temp" will exclude /temp, /organizations/organization_1/temp, etc.
     * @param type type of resource to find
     * @param excludeType type of resource to exclude
     * @param start start index for pagination
     * @param limit max quantity of results
     * @param recursive shows, if search should affect subfolders or take place in folderUri folder only
     * @param showHiddenItems show hidden
     * @param sortBy on which field sorting should be applied
     * @param accessType access type - all, modified, viewed
     * @param user to apply access type
     * @throws IllegalParameterValueException if some parameters values are invalid
     * @throws ResourceNotFoundException if specified folder uri not exists
     *
     */
    RepositorySearchResult<ClientResourceLookup> getResourcesForLookupClass(String lookupClass, String q, String folderUri, List<String> type, List<String> excludeType, List<String> containerType, List<String> excludeFolders, Integer start, Integer limit, Boolean recursive, Boolean showHiddenItems, String sortBy, AccessType accessType, User user, Boolean forceFullPage) throws IllegalParameterValueException, ResourceNotFoundException;


    /**
     * Searches resources
     *
     * @param q search query
     * @param folderUri folder, in which search should be
     * @param excludeFolders folders from this list will be excluded, NOTE: uris are relative to organization, i.e. "/temp" will exclude /temp, /organizations/organization_1/temp, etc.
     * @param type type of resource to find
     * @param excludeType type of resource to exclude
     * @param start start index for pagination
     * @param limit max quantity of results
     * @param recursive shows, if search should affect subfolders or take place in folderUri folder only
     * @param showHiddenItems show hidden
     * @param sortBy on which field sorting should be applied
     * @param accessType access type - all, modified, viewed
     * @param user to apply access type
     * @throws IllegalParameterValueException if some parameters values are invalid
     * @throws ResourceNotFoundException if specified folder uri not exists
     *
     */
    RepositorySearchResult<ClientResourceLookup> getResources(String q, String folderUri, List<String> type, List<String> excludeType, List<String> containerType, List<String> excludeFolders, Integer start, Integer limit, Boolean recursive, Boolean showHiddenItems, String sortBy, AccessType accessType, User user, Boolean forceFullPage) throws IllegalParameterValueException, ResourceNotFoundException;

    /**
     * Searches resources
     *
     * @param searchCriteria  search criteria
     * @throws IllegalParameterValueException if some parameters values are invalid
     * @throws ResourceNotFoundException if specified folder uri not exists
     *
     */
    RepositorySearchResult<ClientResourceLookup> getResources(RepositorySearchCriteria searchCriteria) throws IllegalParameterValueException, ResourceNotFoundException;

    /**
     * Gets total count of resources who matches criteria
     *
     * @param q search query
     * @param folderUri folder, in which search should be
     * @param type type of resource to find
     * @param excludeType type of resource to exclude
     * @param excludeFolders folders from this list will be excluded, NOTE: uris are relative to organization, i.e. "/temp" will exclude /temp, /organizations/organization_1/temp, etc.
     * @param recursive shows, if search should affect subfolders or take place in folderUri folder only
     * @param showHiddenItems show hidden
       @param accessType access type - all, modified, viewed
     * @param user to apply access type
     * @throws IllegalParameterValueException if some parameters values are invalid
     * @throws ResourceNotFoundException if specified folder uri not exists
     *
     */
     int getResourcesCount(String q, String folderUri, List<String> type, List<String> excludeType, List<String> excludeFolders, Boolean recursive, Boolean showHiddenItems, AccessType accessType, User user) throws IllegalParameterValueException, ResourceNotFoundException;


     /**
      * Gets total count of resources who matches criteria
      *
      * @param q search query
      * @param type type of resource to find
      * @param excludeType type of resource to exclude
      * @param excludeFolders folders from this list will be excluded, NOTE: uris are relative to organization, i.e. "/temp" will exclude /temp, /organizations/organization_1/temp, etc.
      * @param recursive shows, if search should affect subfolders or take place in folderUri folder only
      * @param showHiddenItems show hidden
        @param accessType access type - all, modified, viewed
      * @param user to apply access type
      * @throws IllegalParameterValueException if some parameters values are invalid
      * @throws ResourceNotFoundException if specified folder uri not exists
      *
      */
      List<Object[]> getResourcesCount(String q, List<ClientResourceLookup> resources, List<String> type, List<String> excludeType, List<String> excludeFolders, Boolean recursive, Boolean showHiddenItems, AccessType accessType, User user) throws IllegalParameterValueException, ResourceNotFoundException;


    /**
     * Gets total count of resources who matches criteria
     *
     * @param searchCriteria search criteria
     * @throws IllegalParameterValueException if some parameters values are invalid
     * @throws ResourceNotFoundException if specified folder uri not exists
     *
     */
    int getResourcesCount(RepositorySearchCriteria searchCriteria) throws IllegalParameterValueException, ResourceNotFoundException;

    /**
     * Gets total count of resources who matches criteria
     *
     * @param types of resources
     *
     */
    int getResourcesCountByTypes(List<String> types);

}
