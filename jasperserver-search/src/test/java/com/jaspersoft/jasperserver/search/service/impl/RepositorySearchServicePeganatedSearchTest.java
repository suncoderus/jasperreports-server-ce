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

package com.jaspersoft.jasperserver.search.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.api.search.SearchSorter;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.common.ResourceDetails;
import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettings;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettingsResolver;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.search.service.RepositorySearchService;
import com.jaspersoft.jasperserver.search.strategy.ResourceLoadStrategy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.junit.Ignore;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static com.jaspersoft.jasperserver.search.service.impl.RepositorySearchCriteriaImpl.Builder;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Ignore
public class RepositorySearchServicePeganatedSearchTest {
    public static final int ITEMS_PER_PAGE = 5;
    public static final int ITEMS_TOTAL_SIZE = 16;

//    @InjectMocks
//    private BatchRepositoryServiceImpl service;
//    @Mock
//    private RepositorySearchService searchService;
//    @Mock
//    private LookupResourceConverter lookupConverter;
//    @Mock
//    private ResourceConverterProviderImpl converterProvider;
//    @Mock
//    private RepositoryService repositoryService;
//    @Mock
//    private UriHardModifyProtectionChecker uriHardModifyProtectionChecker;
//    @Mock
//    private SearchCriteriaFactory searchCriteriaFactory;
//    @Mock
//    private ResourceLoadStrategy resourceLoadStrategy;
//    @Mock
//    private SearchModeSettingsResolver searchModeSettingsResolver;
//
//    final SearchModeSettings searchModeSettings = new SearchModeSettings();
//
//    private List<ResourceLookup> lookups = new ArrayList<ResourceLookup>();
////    private final Integer lookupsTotalSize = 20;
//
//    private final List<String> resourceTypes = Arrays.asList("clientType");
//
//    @BeforeClass
//    public void init(){
//        MockitoAnnotations.initMocks(this);
//
//        RepositorySearchConfiguration configuration = new RepositorySearchConfiguration();
//        configuration.setItemsPerPage(100);
//        searchModeSettings.setRepositorySearchConfiguration(configuration);
//    }
//
//    @BeforeMethod
//    public void prepareService() throws Exception {
//        ToServerConverter converterMock = mock(ToServerConverter.class);
//        when(converterMock.getServerResourceType()).thenReturn("serverType");
//        when(converterProvider.getToServerConverter(resourceTypes.get(0))).thenReturn(converterMock);
//
//        when(searchModeSettingsResolver.getSettings(SearchMode.SEARCH)).thenReturn(this.searchModeSettings);
//        when(searchModeSettingsResolver.getSettings(SearchMode.BROWSE)).thenReturn(this.searchModeSettings);
//
//        when(lookupConverter.toClient(any(ResourceDetails.class), isNull(ToClientConversionOptions.class))).then(new Answer<Object>() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                Object[] args = invocation.getArguments();
//                Object mock = invocation.getMock();
//
//                if (args.length == 0 || args[0] == null) {
//                    return null;
//                }
//                ClientResourceLookup clientLookup = new ClientResourceLookup();
//                clientLookup.setLabel(((ResourceLookup) args[0]).getLabel());
//
//                return clientLookup;
//            }
//        });
//
//        reset(repositoryService, uriHardModifyProtectionChecker, searchService);
//        when(repositoryService.getFolder(isNullCtx(), any(String.class))).
//                thenReturn(new FolderImpl());
//
//        when(uriHardModifyProtectionChecker.isHardModifyProtected(any(String.class))).
//                thenReturn(false);
//        when(searchService.getResultsCount(isNullCtx(), anyCriteria())).
//                thenReturn(ITEMS_TOTAL_SIZE);
//    }
//
//    @BeforeMethod
//    public void givenLookups(){
//        lookups.clear();
//
//        for (int i = 0; i < ITEMS_TOTAL_SIZE; i++) {
//            ResourceDetails resource = new ResourceDetails();
//            resource.setName("r" + i);
//            resource.setLabel("r" + i);
//            lookups.add(resource);
//        }
//    }
//
//    @Test(expectedExceptions = ResourceNotFoundException.class)
//    public void shouldFailToSearch() throws ResourceNotFoundException, IllegalParameterValueException {
//        reset(repositoryService);
//        service.getResources(null, "/doesnt/exist", null, null, null, null, null, null, null, null, null);
//    }
//
//    @Test(groups = {"All lookups"})
//    public void shouldFindFirstPage() throws IllegalParameterValueException, ResourceNotFoundException {
//        int givenOffset = 0;
//        List<ResourceLookup> givenLookups =
//                new ArrayList<ResourceLookup>(this.lookups.subList(givenOffset, ITEMS_PER_PAGE));
//
//        when(searchService.getLookups(isNullCtx(), anyCriteria())).
//                thenReturn(givenLookups);
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult<ClientResourceLookup> result = service.getResources(criteriaForPage(0, ITEMS_PER_PAGE));
//        assertResult(result.getItems(), "r0", "r1", "r2", "r3", "r4");
//
//        assertEquals(result.getClientOffset(), 0);
//        assertEquals(result.getNextOffset(), 5);
//        assertEquals(result.getClientLimit(), ITEMS_PER_PAGE);
//        assertEquals(result.getNextLimit(), 0);
//        assertTrue(result.isFull());
//
//        verify(searchService, times(1)).getLookups(isNullCtx(), anyCriteria());
//    }
//
//    @Test(groups = {"All lookups"})
//    public void shouldFindSecondPage() throws IllegalParameterValueException, ResourceNotFoundException {
//        int givenOffset = 5;
//        List<ResourceLookup> givenLookups =
//                new ArrayList<ResourceLookup>(this.lookups.subList(givenOffset, givenOffset + ITEMS_PER_PAGE));
//
//        when(searchService.getLookups(isNullCtx(), anyCriteria())).
//                thenReturn(givenLookups);
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult<ClientResourceLookup> result = service.getResources(criteriaForPage(givenOffset, ITEMS_PER_PAGE));
//        assertResult(result.getItems(), "r5", "r6", "r7", "r8", "r9");
//        assertEquals(result.getClientOffset(), givenOffset);
//        assertEquals(result.getNextOffset(), givenOffset + ITEMS_PER_PAGE);
//        assertEquals(result.getClientLimit(), ITEMS_PER_PAGE);
//        assertEquals(result.getNextLimit(), 0);
//        assertTrue(result.isFull());
//
//        verify(searchService, times(1)).getLookups(isNullCtx(), anyCriteria());
//    }
//
//    @Test(groups = {"All lookups"})
//    public void shouldReturnAllResourcesIfLimitIsZero() throws IllegalParameterValueException, ResourceNotFoundException {
//        int givenOffset = 0;
//        List<ResourceLookup> givenLookups =
//                new ArrayList<ResourceLookup>(this.lookups.subList(givenOffset, this.lookups.size()));
//
//        when(searchService.getLookups(isNullCtx(), anyCriteria())).
//                thenReturn(givenLookups);
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult<ClientResourceLookup> result =
//                service.getResources(criteriaForPage(givenOffset, 0));
//        assertResult(result.getItems(), "r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7", "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15");
//        assertEquals(result.getClientOffset(), givenOffset);
//        assertEquals(result.getNextOffset(), 0);
//        assertEquals(result.getClientLimit(), 0);
//        assertEquals(result.getNextLimit(), 0);
//        assertTrue(result.isFull());
//
//        verify(searchService, times(1)).getLookups(isNullCtx(), anyCriteria());
//    }
//
//    @Test(groups = {"All lookups"})
//    public void shouldReturnAllResourcesIfLimitToBig() throws IllegalParameterValueException, ResourceNotFoundException {
//        int givenOffset = 0;
//        List<ResourceLookup> givenLookups =
//                new ArrayList<ResourceLookup>(this.lookups.subList(givenOffset, this.lookups.size()));
//
//        when(searchService.getLookups(isNullCtx(), anyCriteria())).
//                thenReturn(givenLookups);
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult<ClientResourceLookup> result =
//                service.getResources(criteriaForPage(givenOffset, ITEMS_TOTAL_SIZE * 2));
//        assertResult(result.getItems(), "r0", "r1", "r2", "r3", "r4", "r5", "r6", "r7", "r8", "r9", "r10", "r11", "r12", "r13", "r14", "r15");
//        assertEquals(result.getClientOffset(), givenOffset);
//        assertEquals(result.getNextOffset(), ITEMS_TOTAL_SIZE * 2);
//        assertEquals(result.getClientLimit(), ITEMS_TOTAL_SIZE * 2);
//        assertEquals(result.getNextLimit(), ITEMS_TOTAL_SIZE);
//        assertTrue(result.isFull());
//
//        verify(searchService, times(1)).getLookups(isNullCtx(), anyCriteria());
//    }
//
//    @Test(groups = {"Filtered lookups"})
//    public void shouldReturnFullFirstPageInCaseOfFilteredLookups() throws IllegalParameterValueException, ResourceNotFoundException {
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(0, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(0, 3));
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(5, 1)))).
//                thenReturn(createPage(5, 1, 2, 3, 4));
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(5, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(ITEMS_PER_PAGE));
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult result = service.getResources(criteriaForPage(0, ITEMS_PER_PAGE));
//        assertResult(result.getItems(), "r0", "r1", "r2", "r4", "r5");
//        assertEquals(result.getClientOffset(), 0);
//        assertEquals(result.getNextOffset(), 6);
//        assertEquals(result.getClientLimit(), ITEMS_PER_PAGE);
//        assertEquals(result.getNextLimit(), 0);
//        assertTrue(result.isFull());
//
//        verify(searchService, times(2)).getLookups(isNullCtx(), anyCriteria());
//    }
//
//    @Test(groups = {"Filtered lookups"})
//    public void shouldReturnFirstTwoPagesInCaseOfFilteredLookups() throws IllegalParameterValueException, ResourceNotFoundException {
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(0, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(0, 3));
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(5, 1)))).
//                thenReturn(Collections.EMPTY_LIST);
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(6, 1)))).
//                thenReturn(createPage(6, 1, 2, 3, 4));
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult firstPageResult = service.getResources(criteriaForPage(0, ITEMS_PER_PAGE));
//        assertResult(firstPageResult.getItems(), "r0", "r1", "r2", "r4", "r6");
//        assertEquals(firstPageResult.getClientOffset(), 0);
//        assertEquals(firstPageResult.getNextOffset(), 7);
//        assertEquals(firstPageResult.getClientLimit(), ITEMS_PER_PAGE);
//        assertEquals(firstPageResult.getNextLimit(), 0);
//        assertTrue(firstPageResult.isFull());
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(5, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(ITEMS_PER_PAGE));
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(ITEMS_PER_PAGE + 5, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(ITEMS_PER_PAGE + 5));
//
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult secondPageResult = service.getResources(criteriaForPage(5, ITEMS_PER_PAGE));
//        assertResult(secondPageResult.getItems(), "r5", "r6", "r7", "r8", "r9");
//        assertEquals(secondPageResult.getClientOffset(), 5);
//        assertEquals(secondPageResult.getNextOffset(), ITEMS_PER_PAGE * 2);
//        assertEquals(secondPageResult.getClientLimit(), ITEMS_PER_PAGE);
//        assertEquals(secondPageResult.getNextLimit(), 0);
//        assertTrue(secondPageResult.isFull());
//
//        verify(searchService, times(4)).getLookups(isNullCtx(), anyCriteria());
//    }
//
//    @Test(groups = {"Filtered lookups"})
//    public void shouldReturnFirstTwoPagesWithoutDuplicatesInCaseOfFilteredLookups() throws IllegalParameterValueException, ResourceNotFoundException {
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(0, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(0, 3));
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(5, 1)))).
//                thenReturn(createPage(5, 1, 2, 3, 4));
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult firstPageResult = service.getResources(criteriaForPage(0, ITEMS_PER_PAGE));
//        assertResult(firstPageResult.getItems(), "r0", "r1", "r2", "r4", "r5");
//        assertEquals(firstPageResult.getClientOffset(), 0);
//        assertEquals(firstPageResult.getNextOffset(), 6);
//        assertEquals(firstPageResult.getClientLimit(), ITEMS_PER_PAGE);
//        assertEquals(firstPageResult.getNextLimit(), 0);
//        assertTrue(firstPageResult.isFull());
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(ITEMS_PER_PAGE, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(ITEMS_PER_PAGE));
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(6, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(6));
//
//
//        int nextOffset = firstPageResult.getNextOffset();
//        assertEquals(nextOffset, 6);
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult secondPageResult =
//                service.getResources(criteriaForPage(nextOffset, ITEMS_PER_PAGE));
//        assertResult(secondPageResult.getItems(), "r6", "r7", "r8", "r9", "r10");
//        assertEquals(secondPageResult.getClientOffset(), nextOffset);
//        assertEquals(secondPageResult.getNextOffset(), 11);
//        assertEquals(secondPageResult.getClientLimit(), ITEMS_PER_PAGE);
//        assertEquals(secondPageResult.getNextLimit(), 0);
//        assertTrue(secondPageResult.isFull());
//
//        verify(searchService, times(3)).getLookups(isNullCtx(), anyCriteria());
//    }
//
//    @Test(groups = {"Filtered lookups"})
//    public void shouldReturnFullSecondPageInCaseOfFilteredLookups() throws IllegalParameterValueException, ResourceNotFoundException {
//        int givenOffset = 5;
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(givenOffset, ITEMS_PER_PAGE)))).
//                thenReturn(createPage(givenOffset, 1, 2, 3, 4));
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(10, 4)))).
//                thenReturn(createPage(10, 0, 1, 4));
//
//        when(searchService.getLookups(isNullCtx(), eqCriteria(criteriaForPage(14, 2)))).
//                thenReturn(createPage(14));
//
//        com.jaspersoft.jasperserver.search.service.RepositorySearchResult<ClientResourceLookup> result = service.getResources(criteriaForPage(givenOffset, ITEMS_PER_PAGE));
//        assertResult(result.getItems(), "r5", "r12", "r13", "r14", "r15");
//        assertEquals(result.getClientOffset(), givenOffset);
//        assertEquals(result.getNextOffset(), ITEMS_TOTAL_SIZE);
//        assertEquals(result.getClientLimit(), ITEMS_PER_PAGE);
//        assertEquals(result.getNextLimit(), 0);
//        assertTrue(result.isFull());
//
//        verify(searchService, times(3)).getLookups(isNullCtx(), anyCriteria());
//    }
//
//    private void assertResult(final List<ClientResourceLookup> result, final String ... resourceLabels) {
//        assertEquals(result.size(), resourceLabels.length);
//
//        Collection<String> resultColl = CollectionUtils.collect(result, new Transformer() {
//            @Override
//            public String transform(Object input) {
//                final ClientResourceLookup entity = (ClientResourceLookup) input;
//                return entity.getLabel();
//            }
//        });
//
//        String[] resultAsArray = Arrays.copyOf(resultColl.toArray(new String[resultColl.size()]), resultColl.size());
//
//        assertEquals(resultAsArray, resourceLabels);
//    }
//    private ExecutionContext isNullCtx() {
//        return isNull(ExecutionContext.class);
//    }
//
//    private RepositorySearchCriteria anyCriteria() {
//        return any(RepositorySearchCriteria.class);
//    }
//
//    private SearchCriteriaFactory anyFactory() {
//        return any(SearchCriteriaFactory.class);
//    }
//
//    private List<SearchFilter> anyFilterList() {
//        return any(List.class);
//    }
//
//    private SearchSorter anySorter() {
//        return any(SearchSorter.class);
//    }
//
//    private RepositorySearchCriteria criteriaForPage(final int start, final int max) {
//        return criteriaBuilder().setStartIndex(start).setMaxCount(max).getCriteria();
//    }
//
//    private RepositorySearchCriteria eqCriteria(final RepositorySearchCriteria criteria) {
//        return refEq(criteria, /*"searchMode", "sortBy", "searchText", "folderUri" , */"resourceTypes");
//    }
//
//    private List<ResourceLookup> createPage(final int givenOffset, int ... removeIndexes) {
//        int maxPage = givenOffset + ITEMS_PER_PAGE;
//        maxPage = maxPage > this.lookups.size() ? this.lookups.size() :  maxPage;
//
//        List<ResourceLookup> page = new ArrayList<ResourceLookup>();
//        List<ResourceLookup> subList = this.lookups.subList(givenOffset, maxPage);
//
//        for (int i = 0; i < subList.size(); i++) {
//            if (Arrays.binarySearch(removeIndexes, i) < 0) {
//                page.add(subList.get(i));
//            }
//        }
//
//        return page;
//    }
//
//    private Builder criteriaBuilder() {
//        return new Builder()
//                .setSearchMode(SearchMode.BROWSE)
//                .setFolderUri(Folder.SEPARATOR)
//                .setStartIndex(0)
//                .setMaxCount(5)
//                .setCheckAll(true)
//                .setShowHidden(false)
//                .setAccessType(null)
//                .setSortBy("label")
//                .setSearchText("text")
//                .setUser(null)
//                .setResourceTypes(this.resourceTypes);
//    }
}
