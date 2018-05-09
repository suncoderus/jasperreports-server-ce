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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.converters.LookupResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProviderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConverter;
import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.common.ResourceDetails;
import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettings;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettingsResolver;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.search.service.RepositorySearchService;
import com.jaspersoft.jasperserver.search.strategy.ResourceLoadStrategy;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: $
 */
public class BatchRepositoryServiceImplTest {
    @InjectMocks
    private BatchRepositoryServiceImpl service;
    @Mock
    private RepositorySearchService repositorySearchService;
    @Mock
    private LookupResourceConverter lookupResourceConverter;
    @Mock
    private ResourceConverterProviderImpl resourceConverterProvider;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private UriHardModifyProtectionChecker uriHardModifyProtectionChecker;
    @Mock
    private SearchCriteriaFactory searchCriteriaFactory;
    @Mock
    private ResourceLoadStrategy resourceLoadStrategy;
    @Mock
    private SearchModeSettingsResolver searchModeSettingsResolver;

    final SearchModeSettings searchModeSettings = new SearchModeSettings();

    private ArgumentCaptor<RepositorySearchCriteria> repositorySearchCriteriaArgumentCaptor;
    private ArgumentCaptor<RepositorySearchCriteria> repositorySearchCriteriaArgumentCaptorForCount;
    private List<ResourceLookup> expectedResourceDetailsList = new ArrayList<ResourceLookup>();
    private final Integer expectedResourceDetailsListSize = 10;
    private final String uri = "/test";

    final Resource res = new FolderImpl();
    final List<String> uris = new LinkedList<String>();

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);

        RepositorySearchConfiguration configuration = new RepositorySearchConfiguration();
        configuration.setItemsPerPage(100);
        searchModeSettings.setRepositorySearchConfiguration(configuration);
    }

    @BeforeMethod
    public void prepareSearchService(){
        expectedResourceDetailsList.clear();
        repositorySearchCriteriaArgumentCaptor = ArgumentCaptor.forClass(RepositorySearchCriteria.class);
        when(searchModeSettingsResolver.getSettings(SearchMode.SEARCH)).thenReturn(this.searchModeSettings);
        when(searchModeSettingsResolver.getSettings(SearchMode.BROWSE)).thenReturn(this.searchModeSettings);

        when(repositorySearchService.getLookups(isNull(ExecutionContext.class), repositorySearchCriteriaArgumentCaptor.capture())).thenReturn(expectedResourceDetailsList);
        when(repositorySearchService.getResultsCount(isNull(ExecutionContext.class), any(RepositorySearchCriteria.class))).thenReturn(expectedResourceDetailsListSize);

        reset(repositoryService, uriHardModifyProtectionChecker);
        when(repositoryService.getFolder(isNull(ExecutionContext.class), any(String.class))).thenReturn(new FolderImpl());

        when(uriHardModifyProtectionChecker.isHardModifyProtected(any(String.class))).thenReturn(false);

        res.setURIString(uri);
        uris.clear();
        uris.add(uri);
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void getResources_validateFolderUri_invalidFormat() throws ResourceNotFoundException, IllegalParameterValueException {
        service.getResources(null, "not.valid.uri", null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void getResources_validateFolderUri_folderDoesntExist() throws ResourceNotFoundException, IllegalParameterValueException {
        reset(repositoryService);
        service.getResources(null, "/doesnt/exist", null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test(groups = {"GET"})
    public void getResources_allParameterAreForwardedCorrectly() throws IllegalParameterValueException, ResourceNotFoundException {
        final String q = "testQuery";
        final String folderUri = "/test/folder/uri";
        final List<String> type = Arrays.asList("clientType");
        final String serverType = "serverType";
        final Integer start = 10;
        final Integer limit = 20;
        final Boolean recursive = false;
        final Boolean showHidden = false;
        final Boolean forceTotalCount = false;
        final String sortBy = "testSortBy";
        ToServerConverter converterMock = mock(ToServerConverter.class);
        when(converterMock.getServerResourceType()).thenReturn(serverType);
        when(resourceConverterProvider.getToServerConverter(type.get(0))).thenReturn(converterMock);
        when(repositoryService.folderExists(any(ExecutionContext.class), anyString())).thenReturn(true);
        when(repositoryService.getFolder(any(ExecutionContext.class), anyString())).thenReturn(new FolderImpl());
        service.getResources(q, folderUri, type, null, type, type, start, limit, recursive, showHidden, sortBy, null, null, null);
        final RepositorySearchCriteria value = repositorySearchCriteriaArgumentCaptor.getValue();
        assertEquals(value.getSearchText(), q);
        assertEquals(value.getFolderUri(), folderUri);
        assertEquals(value.getResourceTypes().get(0), serverType);
        assertEquals(value.getStartIndex(), start.intValue());
        assertEquals(value.getMaxCount(), limit.intValue());
        assertEquals(value.getSortBy(), sortBy);
    }

    @Test(groups = {"GET"}, expectedExceptions = IllegalParameterValueException.class)
    public void getResources_invalidFolderUri() throws IllegalParameterValueException, ResourceNotFoundException {
        final String q = "testQuery";
        final String folderUri = "test/folder/uri";
        final List<String> type = Arrays.asList("clientType");
        final String serverType = "serverType";
        final Integer start = 10;
        final Integer limit = 20;
        final Boolean recursive = false;
        final Boolean showHidden = false;
        final Boolean forceTotalCount = false;
        final String sortBy = "testSortBy";
        ToServerConverter converterMock = mock(ToServerConverter.class);
        when(converterMock.getServerResourceType()).thenReturn(serverType);
        when(resourceConverterProvider.getToServerConverter(type.get(0))).thenReturn(converterMock);
        when(repositoryService.getFolder(any(ExecutionContext.class), anyString())).thenReturn(new FolderImpl());
        when(repositorySearchService.getResultsCount(isNull(ExecutionContext.class), any(RepositorySearchCriteria.class))).thenReturn(20);
        when(resourceLoadStrategy.getItemsToLoadCount(20, 0)).thenReturn(20);

        service.getResources(q, folderUri, type, null, type, type, start, limit, recursive, showHidden, sortBy, null, null, null);

        verify(repositorySearchService).
                getLookups(isNull(ExecutionContext.class), repositorySearchCriteriaArgumentCaptor.capture());

        final RepositorySearchCriteria value = repositorySearchCriteriaArgumentCaptor.getValue();
        assertEquals(value.getSearchText(), q);
        assertEquals(value.getFolderUri(), folderUri);
        assertEquals(value.getResourceTypes().get(0), serverType);
        assertEquals(value.getStartIndex(), start.intValue());
        assertEquals(value.getMaxCount(), limit.intValue());
        assertEquals(value.getSortBy(), sortBy);
    }

    @Test(groups = {"GET"})
    public void getResources_excludeTypeIsForwardedCorrectly() throws IllegalParameterValueException, ResourceNotFoundException {
        final String clientType = "excludeType";
        final String serverType = "serverExcludeType";
        final List<String> excludeType = Collections.singletonList(clientType);
        ToServerConverter converterMock = mock(ToServerConverter.class);
        when(converterMock.getServerResourceType()).thenReturn(serverType);
        when(resourceConverterProvider.getToServerConverter(clientType)).thenReturn(converterMock);
        when(repositoryService.folderExists(any(ExecutionContext.class), anyString())).thenReturn(true);
        service.getResources(null, null, null, excludeType, null, null, null, null, null, null, null, null, null, null);
        final RepositorySearchCriteria value = repositorySearchCriteriaArgumentCaptor.getValue();
        assertEquals(value.getExcludeResourceTypes(), Collections.singletonList(serverType));
    }

    @Test(groups = {"GET"}, expectedExceptions = ResourceNotFoundException.class)
    public void getResources_folderNotExists() throws IllegalParameterValueException, ResourceNotFoundException {
        reset(repositoryService);
        final String q = "testQuery";
        final String folderUri = "/test/folder/uri";
        final List<String> type = Arrays.asList("clientType");
        final String serverType = "serverType";
        final Integer start = 10;
        final Integer limit = 20;
        final Boolean recursive = false;
        final Boolean showHidden = false;
        final Boolean forceTotalCount = false;
        final String sortBy = "testSortBy";
        ToServerConverter converterMock = mock(ToServerConverter.class);
        when(converterMock.getServerResourceType()).thenReturn(serverType);
        when(resourceConverterProvider.getToServerConverter(type.get(0))).thenReturn(converterMock);
        service.getResources(q, folderUri, type, null, type, type, start, limit, recursive, showHidden, sortBy, null, null, null);
        final RepositorySearchCriteria value = repositorySearchCriteriaArgumentCaptor.getValue();
        assertEquals(value.getSearchText(), q);
        assertEquals(value.getFolderUri(), folderUri);
        assertEquals(value.getResourceTypes().get(0), serverType);
        assertEquals(value.getStartIndex(), start.intValue());
        assertEquals(value.getMaxCount(), limit.intValue());
        assertEquals(value.getSortBy(), sortBy);
    }

    @Test(groups = {"GET"})
    public void getResources_resourceLookup_empty() throws IllegalParameterValueException, ResourceNotFoundException {
        IllegalParameterValueException exception = null;
        final String resourceLookupType = ClientTypeHelper.extractClientType(ClientResource.class);
        when(resourceConverterProvider.getToServerConverter(resourceLookupType)).thenReturn((ToServerConverter)new LookupResourceConverter());
        try {
            final List<ClientResourceLookup> resources = service.getResources(null, null, Arrays.asList(resourceLookupType), null, null, null, null, null, null, null, null, null, null, null).getItems();
            assertTrue(resources.isEmpty());
        } catch (IllegalParameterValueException e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test(groups = {"GET"})
    public void getResources_defaultValues() throws IllegalParameterValueException, ResourceNotFoundException {
        when(resourceLoadStrategy.getItemsToLoadCount(100, 0)).thenReturn(100);
        when(repositoryService.folderExists(any(ExecutionContext.class), anyString())).thenReturn(true);

        service.getResources(null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        final RepositorySearchCriteria value = repositorySearchCriteriaArgumentCaptor.getValue();
        assertEquals(value.getStartIndex(), 0);
        assertEquals(value.getMaxCount(), 100);
        assertEquals(value.getFolderUri(), Folder.SEPARATOR);
        assertFalse(value.isShowHidden());
    }

    @Test(groups = {"GET"})
    public void getResources_toClientConversion() throws IllegalParameterValueException, ResourceNotFoundException {
        ResourceDetails details = new ResourceDetails();
        final int resultsCount = 10;
        for(int i = 0; i < resultsCount; i++){
            expectedResourceDetailsList.add(details);
        }
        ClientResourceLookup clientResourceLookup = new ClientResourceLookup();
        when(repositoryService.folderExists(any(ExecutionContext.class), anyString())).thenReturn(true);
        when(lookupResourceConverter.toClient(any(ResourceDetails.class), isNull(ToClientConversionOptions.class))).thenReturn(clientResourceLookup);
        List<ClientResourceLookup> entity = service.getResources(null, null, null, null, null, null, null,null,null, null, null, null, null, null).getItems();

        assertEquals(entity.size(), resultsCount);
        for(ClientResource currentResultItem : entity){
            assertSame(currentResultItem, clientResourceLookup);
        }
    }

    @Test(groups = {"GET"},dependsOnMethods = "getResources_toClientConversion")
    public void getResources_ResponseCode_200() throws IllegalParameterValueException, ResourceNotFoundException {
        ResourceDetails details = new ResourceDetails();
        final int resultsCount = 10;
        for (int i = 0; i < resultsCount; i++) {
            expectedResourceDetailsList.add(details);
        }
        ClientResourceLookup clientResourceLookup = new ClientResourceLookup();
        when(resourceLoadStrategy.getItemsToLoadCount(20, 10)).thenReturn(expectedResourceDetailsListSize);
        when(lookupResourceConverter.toClient(any(ResourceDetails.class), isNull(ToClientConversionOptions.class))).thenReturn(clientResourceLookup);
        when(repositoryService.folderExists(any(ExecutionContext.class), anyString())).thenReturn(true);

        final List<ClientResourceLookup> result = service.getResources(null, null, null, null, null, null, null,null, null, null, null, null, null, null).getItems();

        assertEquals(result.size(), resultsCount);
    }

    @Test(groups = {"GET"}, dependsOnMethods = "getResources_toClientConversion")
    public void getResources_ResponseCode_204() throws IllegalParameterValueException, ResourceNotFoundException {
        ClientResourceLookup clientResourceLookup = new ClientResourceLookup();
        when(lookupResourceConverter.toClient(any(ResourceDetails.class), isNull(ToClientConversionOptions.class))).thenReturn(clientResourceLookup);
        when(repositoryService.folderExists(any(ExecutionContext.class), anyString())).thenReturn(true);
        final List<ClientResourceLookup> result = service.getResources(null, null, null, null, null, null,null, null, null, null, null, null, null, null).getItems();

        assertEquals(expectedResourceDetailsList.size(), 0);
    }

    @Test(expectedExceptions = com.jaspersoft.jasperserver.remote.exception.AccessDeniedException.class)
    public void deleteResources_modifyProtected() throws com.jaspersoft.jasperserver.remote.exception.AccessDeniedException, ResourceNotFoundException {
        when(uriHardModifyProtectionChecker.isHardModifyProtected(uri)).thenReturn(true);

        service.deleteResources(uris);
    }

    @Test(expectedExceptions = {com.jaspersoft.jasperserver.remote.exception.AccessDeniedException.class})
    public void deleteResources_HasDependentResources() throws Exception{
        List<ResourceLookup> dependencies = new LinkedList<ResourceLookup>();
        dependencies.add(new ResourceLookupImpl());
        dependencies.get(0).setURIString(uri);

        Mockito.when(repositoryService.getResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri))).thenReturn(res);
        Mockito.when(repositoryService.getDependentResources(Mockito.any(ExecutionContext.class), Mockito.anyString(), Mockito.same(searchCriteriaFactory), anyInt(), anyInt())).thenReturn(dependencies);

        service.deleteResources(uris);
    }

    @Test
    public void testDeleteResources_Resource() throws Exception {
        Mockito.when(repositoryService.getResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri))).thenReturn(res);

        service.deleteResources(uris);

        Mockito.verify(repositoryService).deleteResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test
    public void testDeleteResources_Folder() throws Exception {
        Mockito.when(repositoryService.getResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri))).thenReturn((Folder)res);

        service.deleteResources(uris);

        Mockito.verify(repositoryService).deleteFolder(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(expectedExceptions = {com.jaspersoft.jasperserver.remote.exception.AccessDeniedException.class})
    public void testDeleteResources_NoPermission_Resource() throws Exception {
        Mockito.when(repositoryService.getResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri))).thenReturn(res);
        Mockito.doThrow(new AccessDeniedException("NO")).when(repositoryService).deleteResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri));

        service.deleteResources(uris);
    }
    @Test(expectedExceptions = {com.jaspersoft.jasperserver.remote.exception.AccessDeniedException.class})
    public void testDeleteResources_NoPermission_Folder() throws Exception {
        Mockito.when(repositoryService.getResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri))).thenReturn((Folder)res);
        Mockito.doThrow(new AccessDeniedException("NO")).when(repositoryService).deleteFolder(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri));

        service.deleteResources(uris);
    }

    @Test
    public void testDeleteResources_Multiple() throws Exception {
        Resource res1 = new FolderImpl();
        String res1uri = "/a";

        Resource res2 = new FolderImpl();
        String res2uri = "/b";

        uris.add(res1uri);
        uris.add(res2uri);


        Mockito.when(repositoryService.getResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(uri))).thenReturn(res);
        Mockito.when(repositoryService.getResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(res1uri))).thenReturn(res1);
        Mockito.when(repositoryService.getResource(Mockito.isNull(ExecutionContext.class), Mockito.eq(res2uri))).thenReturn(res2);

        service.deleteResources(uris);

        Mockito.verify(repositoryService).deleteResource(Mockito.isNull(ExecutionContext.class),Mockito.eq(uri));
        Mockito.verify(repositoryService).deleteResource(Mockito.isNull(ExecutionContext.class),Mockito.eq(res1uri));
        Mockito.verify(repositoryService).deleteResource(Mockito.isNull(ExecutionContext.class),Mockito.eq(res2uri));
    }
}
