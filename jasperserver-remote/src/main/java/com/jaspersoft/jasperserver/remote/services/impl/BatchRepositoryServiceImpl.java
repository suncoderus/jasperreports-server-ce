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

package com.jaspersoft.jasperserver.remote.services.impl;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ResourceInUseException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.converters.LookupResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.services.BatchRepositoryService;
import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettingsResolver;
import com.jaspersoft.jasperserver.search.service.ItemProcessor;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.search.service.RepositorySearchResult;
import com.jaspersoft.jasperserver.search.service.RepositorySearchService;
import com.jaspersoft.jasperserver.search.service.impl.RepositorySearchAccumulator;
import com.jaspersoft.jasperserver.search.service.impl.RepositorySearchCriteriaImpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class BatchRepositoryServiceImpl implements BatchRepositoryService {

    protected static final Log log = LogFactory.getLog(BatchRepositoryServiceImpl.class);
    // pattern below differs from one declared in configurationBean. Here is a reason:
    // search service has to support '-' character in 'folderUri' search parameter. See JRS-11370 for more information.
    protected static final Pattern ILLEGAL_URI_SYMBOL_PATTERN = Pattern.compile("[~!#\\$%^|\\s`&*()\\+={}\\[\\]:;\"',?\\|\\\\]");
    protected static final Set<String> FILE_TYPES;

    static {
        Set<String> fileTypes = new HashSet<String>();
        for (ClientFile.FileType fileType : ClientFile.FileType.values()) {
            fileTypes.add(fileType.toString());
        }

        FILE_TYPES = Collections.unmodifiableSet(fileTypes);
    }

    @javax.annotation.Resource(name = "concreteRepository")
    private RepositoryService service;
    @javax.annotation.Resource
    private RepositorySearchService repositorySearchService;
    @javax.annotation.Resource
    private LookupResourceConverter lookupResourceConverter;
    @javax.annotation.Resource
    private ResourceConverterProvider resourceConverterProvider;
    @javax.annotation.Resource
    private UriHardModifyProtectionChecker uriHardModifyProtectionChecker;
    @javax.annotation.Resource
    private SearchCriteriaFactory searchCriteriaFactory;
    @javax.annotation.Resource(name = "restSearchModeSettingsResolver")
    private SearchModeSettingsResolver searchModeSettingsResolver;


    @Override
    public RepositorySearchResult<ClientResourceLookup> getResourcesForLookupClass(String lookupClass, String q, String folderUri, List<String> type, List<String> excludeType, List<String> containerType, List<String> excludeFolders, Integer start, Integer limit, Boolean recursive, Boolean showHiddenItems, String sortBy, AccessType accessType, User user, Boolean forceFullPage) throws IllegalParameterValueException, ResourceNotFoundException {
        SearchMode mode = (recursive == null || recursive) ? SearchMode.SEARCH : SearchMode.BROWSE;
        RepositorySearchConfiguration configuration = getConfiguration(mode);

        final RepositorySearchCriteriaImpl.Builder builder = new RepositorySearchCriteriaImpl.Builder()
                .setLookupClass(lookupClass)
                .setMaxCount(limit != null ? limit : configuration.getItemsPerPage())
                .setForceFullPage(forceFullPage != null ? forceFullPage : false)
                .setFolderUri(folderUri != null ? folderUri : Folder.SEPARATOR)
                .setContainerResourceTypes(containerType)
                .setStartIndex(start != null ? start : 0)
                .setExcludeRelativePaths(excludeFolders)
                .setShowHidden(showHiddenItems)
                .setAccessType(accessType)
                .setResourceTypes(type ==null? Collections.EMPTY_LIST:type)
                .setExcludeResourceTypes(excludeType == null ? Collections.EMPTY_LIST : excludeType)
                .setSearchMode(mode)
                .setSortBy(sortBy)
                .setSearchText(q)
                .setUser(user);

        return getResources(builder.getCriteria());
    }

    @Override
    public RepositorySearchResult<ClientResourceLookup> getResources(String q, String folderUri, List<String> type, List<String> excludeType, List<String> containerType, List<String> excludeFolders, Integer start, Integer limit, Boolean recursive, Boolean showHiddenItems, String sortBy, AccessType accessType, User user, Boolean forceFullPage) throws IllegalParameterValueException, ResourceNotFoundException {
    	return getResourcesForLookupClass(
    			(containerType==null || containerType.isEmpty()) && type!=null && type.size()==1 && type.get(0).equals("folder")?RepoFolder.class.getName():null,
    			q, folderUri, type, excludeType, containerType, excludeFolders, start, limit, recursive, showHiddenItems, sortBy, accessType, user, forceFullPage);
    }

    public RepositorySearchResult<ClientResourceLookup> getResources(RepositorySearchCriteria criteria) throws IllegalParameterValueException, ResourceNotFoundException {
        if (!validateCriteria(criteria)) {

            return RepositorySearchAccumulator.EMPTY_RESULT;
        }

        if (criteria.isForceFullPage()) {
            RepositorySearchResult<ResourceLookup> result =
                    repositorySearchService.getLookupsForFullPage(null, criteria);

            return result.transform(new ItemProcessor<ResourceLookup, ClientResourceLookup>() {
                @Override
                public ClientResourceLookup call(ResourceLookup resource) {
                    return lookupResourceConverter.toClient(resource, null);
                }
            });
        } else {
            RepositorySearchAccumulator<ClientResourceLookup> result =
                    new RepositorySearchAccumulator<ClientResourceLookup>(
                            criteria.getStartIndex(), criteria.getMaxCount(), criteria.getMaxCount());

            List<ResourceLookup> resources = repositorySearchService.getLookups(null, criteria);
            result.fill(criteria, transform(resources));

            return result;
        }

    }


    protected boolean validateCriteria(RepositorySearchCriteria searchCriteria)
            throws IllegalParameterValueException, ResourceNotFoundException {

        if (searchCriteria != null && processType(searchCriteria)){
            String folderUri = searchCriteria.getFolderUri();
            if(folderUri != null){
                validateFolderUri(folderUri);
            }

            return true;
        }

        return false;
    }

    protected List<ClientResourceLookup> transform(List<ResourceLookup> lookups) {
        List<ClientResourceLookup> result = new ArrayList<ClientResourceLookup>(lookups.size());
        if (lookups != null) {
            for (ResourceLookup lookup : lookups) {
                result.add(lookupResourceConverter.toClient(lookup, null));
            }
        }
        return result;
    }


    @Override
    public int getResourcesCount(String q, String folderUri, List<String> type, List<String> excludeType, List<String> excludedFolders, Boolean recursive, Boolean showHiddenItems, AccessType accessType, User user) throws IllegalParameterValueException, ResourceNotFoundException {
    	return getResourcesCountForLookupClass(
    			type!=null && type.size()==1 && type.get(0).equals("folder")?RepoFolder.class.getName():null,
    			q, folderUri, type, excludeType, excludedFolders, recursive, showHiddenItems, accessType, user);
    }

    private int getResourcesCountForLookupClass(String lookupClass, String q, String folderUri, List<String> type, List<String> excludeType, List<String> excludedFolders, Boolean recursive, Boolean showHiddenItems, AccessType accessType, User user) throws IllegalParameterValueException, ResourceNotFoundException {
    	final RepositorySearchCriteriaImpl.Builder builder =
    			new RepositorySearchCriteriaImpl.Builder()
			        .setSearchMode(recursive == null || recursive ? SearchMode.SEARCH : SearchMode.BROWSE)
			        .setFolderUri(folderUri != null ? folderUri : Folder.SEPARATOR)
			        .setExcludeRelativePaths(excludedFolders)
			        .setShowHidden(showHiddenItems)
			        .setAccessType(accessType)
			        .setResourceTypes(type)
                    .setExcludeResourceTypes(excludeType)
			        .setSearchText(q)
			        .setUser(user)
        			.setExcludeFolders(!type.contains("folder"))
        			.setLookupClass(lookupClass);
        return getResourcesCount(builder.getCriteria());
    }

    public int getResourcesCount(RepositorySearchCriteria searchCriteria) throws IllegalParameterValueException, ResourceNotFoundException {
        Integer result = 0;

        if (searchCriteria != null && processType(searchCriteria)) {
            String folderUri = searchCriteria.getFolderUri();
            if(folderUri != null){
                validateFolderUri(folderUri);
            }
            result = repositorySearchService.getResultsCount(null, searchCriteria);
        }
        return result;
    }

    public List getResourcesCountList(RepositorySearchCriteria searchCriteria) throws IllegalParameterValueException, ResourceNotFoundException {
        List result = null;

        if (searchCriteria != null && processType(searchCriteria)) {
            List<ClientResourceLookup> resources = searchCriteria.getResources();
            if(resources!=null){
                for(ClientResourceLookup resource:resources){
                        if(resource.getUri()!=null){
                                validateFolderUri(resource.getUri());
                        }
                }
            }
            result = repositorySearchService.getResultsCountList(null, searchCriteria);
        }
        return result;
    }

    protected boolean processType(RepositorySearchCriteria searchCriteria) {
        Boolean searchSuitable = true;
        List<String> types = searchCriteria.getResourceTypes();
        List<String> excludeTypes = searchCriteria.getExcludeResourceTypes();
        if (!CollectionUtils.isEmpty(types)) {
            if (types.contains(ClientTypeHelper.extractClientType(ClientResource.class))) {
                // ResourceLookup is a valid repository resource, but without persistence. So, search for resource lookups isn't suitable.
                searchSuitable = false;
            } else {
                List<String> serverTypes = new LinkedList<String>();
                if (types.remove(ClientTypeHelper.extractClientType(ClientFile.class))) {
                    // here is an exception from a common rule: one client object corresponds to te only server object and back
                    // Single ClientFile client object corresponds to either FileResource or ContentResource on server side.
                    // Do this trick here
                    serverTypes.add(FileResource.class.getName());
                    serverTypes.add(ContentResource.class.getName());
                }

                // TODO: implement generic solution handling any fileType of FileResource
                if (types.remove(FileResource.TYPE_SECURE_FILE)) {
                    serverTypes.add(FileResource.TYPE_SECURE_FILE);
                    serverTypes.add(FileResource.class.getName());
                }

                List<String> fileTypes = new ArrayList<String>();
                for (String type : types) {
                    try {
                        if (FILE_TYPES.contains(type)){
                            fileTypes.add(type);
                        } else {
                            serverTypes.add(resourceConverterProvider.getToServerConverter(type).getServerResourceType());
                        }
                    } catch (IllegalParameterValueException e) {
                        // ignore wrong or unknown types
                    }
                }
                searchCriteria.setResourceTypes(serverTypes);
                searchCriteria.setFileResourceTypes(fileTypes);
                // if all types incorrect - we should not search anything
                searchSuitable = !serverTypes.isEmpty() || !fileTypes.isEmpty();

                if (searchCriteria.getContainerResourceTypes() != null){
                    List<String> containerServerTypes = new ArrayList<String>();
                    for (String type : searchCriteria.getContainerResourceTypes()) {
                        try {
                            containerServerTypes.add(resourceConverterProvider.getToServerConverter(type).getServerResourceType());
                        } catch (IllegalParameterValueException e) {
                            // ignore wrong or unknown types
                        }
                    }
                    searchCriteria.setContainerResourceTypes(containerServerTypes);
                }

            }
        } else if (!CollectionUtils.isEmpty(excludeTypes)) {
            List<String> serverTypes = new ArrayList<String>();
            for (String type : excludeTypes) {
                try {
                    serverTypes.add(resourceConverterProvider.getToServerConverter(type).getServerResourceType());
                } catch (IllegalParameterValueException e) {
                    // ignore wrong or unknown types
                }
            }
            searchCriteria.setExcludeResourceTypes(serverTypes);
        }

        return searchSuitable;
    }

    /**
     * This method validates if folder URI is correct and exists
     *
     * @param folderUri - folder URI to check
     * @throws IllegalParameterValueException - in case if URI has invalid format
     * @throws ResourceNotFoundException - in case if folder with given URI doesn't exist
     */
    protected void validateFolderUri(String folderUri) throws IllegalParameterValueException, ResourceNotFoundException {
        if(!folderUri.startsWith(Folder.SEPARATOR) || ILLEGAL_URI_SYMBOL_PATTERN.matcher(folderUri).find()){
            throw new IllegalParameterValueException("folderUri", folderUri);
        } else if(!service.folderExists(null, folderUri)){
            throw new ResourceNotFoundException(folderUri);
        }
    }


    public void deleteResources(List<String> uris) throws AccessDeniedException, ResourceNotFoundException {
        for (String uri: uris){
            if(uriHardModifyProtectionChecker.isHardModifyProtected(uri)){
                throw new AccessDeniedException(uri);
            }
            try{
                Resource resource = service.getResource(null, uri);
                if (resource != null){
                    List<ResourceLookup> dependentResources = service.getDependentResources(null, uri, searchCriteriaFactory, 0, 0);
                    if (dependentResources == null || dependentResources.isEmpty()){
                        service.deleteResource(null, uri);
                    } else {
                        throw new ResourceInUseException(dependentResources);
                    }
                } else {
                    resource = service.getFolder(null, uri);
                    if (resource != null){
                        service.deleteFolder(null, uri);
                    }
                }
            } catch (org.springframework.security.access.AccessDeniedException sse) {
                throw new AccessDeniedException(uri);
            } catch (JSExceptionWrapper w) {
                throw SingleRepositoryServiceImpl.getRootException(w);
                    }
                }
            }


    @Override
    public int getResourcesCountByTypes(List<String> types) {

        Integer totalCount = 0;

        try {
            totalCount = getResourcesCount(null, (String) null, types, null, null, true, false, null, null);
        } catch (IllegalParameterValueException e) {
            log.error(e);
        } catch (ResourceNotFoundException e) {
            log.error(e);
        }

        return totalCount;
    }

    protected RepositorySearchConfiguration getConfiguration(SearchMode mode) {
        return searchModeSettingsResolver.getSettings(mode).getRepositorySearchConfiguration();
    }

    @Override
    public List getResourcesCount(String q,
                    List<ClientResourceLookup> resources, List<String> type,
                    List<String> excludeType, List<String> excludedFolders, Boolean recursive,
                    Boolean showHiddenItems, AccessType accessType, User user)
                    throws IllegalParameterValueException, ResourceNotFoundException {

                    boolean excludeFolders = !type.contains("folder");

            final RepositorySearchCriteriaImpl.Builder builder = new RepositorySearchCriteriaImpl.Builder()
                    .setSearchMode(recursive == null || recursive ? SearchMode.SEARCH : SearchMode.BROWSE)
                    .setResources(resources)
                    .setExcludeRelativePaths(excludedFolders)
                    .setExcludeFolders(excludeFolders)
                    .setShowHidden(showHiddenItems)
                    .setAccessType(accessType)
                    .setResourceTypes(type)
                    .setExcludeResourceTypes(excludeType)
                    .setSearchText(q)
                    .setUser(user);

            return getResourcesCountList(builder.getCriteria());
    }
}
