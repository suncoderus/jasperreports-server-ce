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
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryServiceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.api.search.SearchFilter;
import com.jaspersoft.jasperserver.api.search.SearchSorter;
import com.jaspersoft.jasperserver.api.search.TransformerFactory;
import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.filter.ResourceTypeSearchCriteriaFactory;
import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettings;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettingsResolver;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.PartialMock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class RepositorySearchServiceImplTest extends UnitilsJUnit4 {

    @TestedObject
    private PartialMock<RepositorySearchServiceImpl> repositorySearchService;

    @Test
    public void getResultsFromSearchCriteriaWithFolders() {

        final String resourceTypeToCheck = Folder.class.getName();

        RepositorySearchConfiguration configuration = MockUnitils.createMock(RepositorySearchConfiguration.class).getMock();
        final Mock<SearchModeSettings> searchModeSettingsMock = MockUnitils.createMock(SearchModeSettings.class);
        searchModeSettingsMock.returns(configuration).getRepositorySearchConfiguration();
        final SearchModeSettings searchModeSettings = searchModeSettingsMock.getMock();
        final Mock<SearchModeSettingsResolver> resolverMock = MockUnitils.createMock(SearchModeSettingsResolver.class);
        resolverMock.returns(searchModeSettings).getSettings(SearchMode.SEARCH);
        final ResourceTypeSearchCriteriaFactory defaultSearchCriteriaFactory = new ResourceTypeSearchCriteriaFactory();
        RepositoryService repositoryService = new HibernateRepositoryServiceImpl(){
            @Override
            public List<ResourceLookup> getResources(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory, List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory, int current, int max) {
                assertEquals(ResourceLookup.class.getName(), ((ResourceTypeSearchCriteriaFactory)searchCriteriaFactory).getResourceType());
                return new ArrayList<ResourceLookup>();
            }
        };


        RepositorySearchCriteria searchCritera = new RepositorySearchCriteriaImpl.Builder().setSearchMode(SearchMode.SEARCH).setResourceTypes(resourceTypeToCheck).getCriteria();

        RepositorySearchServiceImpl service = new RepositorySearchServiceImpl();
        service.setSearchModeSettingsResolver(resolverMock.getMock());
        service.setDefaultSearchCriteriaFactory(defaultSearchCriteriaFactory);
        service.setRepositoryService(repositoryService);
        service.getResults(null, searchCritera);
        assertFalse(searchCritera.getResourceTypes() == null || searchCritera.getResourceTypes().isEmpty());
    }

    @Test
    public void getResultsFromSearchCriteriaWithoutFolders() {

        final String resourceTypeToCheck = ReportUnit.class.getName();

        RepositorySearchConfiguration configuration = MockUnitils.createMock(RepositorySearchConfiguration.class).getMock();
        final Mock<SearchModeSettings> searchModeSettingsMock = MockUnitils.createMock(SearchModeSettings.class);
        searchModeSettingsMock.returns(configuration).getRepositorySearchConfiguration();
        final SearchModeSettings searchModeSettings = searchModeSettingsMock.getMock();
        final Mock<SearchModeSettingsResolver> resolverMock = MockUnitils.createMock(SearchModeSettingsResolver.class);
        resolverMock.returns(searchModeSettings).getSettings(SearchMode.SEARCH);
        final ResourceTypeSearchCriteriaFactory defaultSearchCriteriaFactory = new ResourceTypeSearchCriteriaFactory();
        RepositoryService repositoryService = new HibernateRepositoryServiceImpl(){
            @Override
            public List<ResourceLookup> getResources(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory, List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory, int current, int max) {
                assertEquals(Resource.class.getName(), ((ResourceTypeSearchCriteriaFactory)searchCriteriaFactory).getResourceType());
                return new ArrayList<ResourceLookup>();
            }
        };


        RepositorySearchCriteria searchCritera = new RepositorySearchCriteriaImpl.Builder().setSearchMode(SearchMode.SEARCH).setResourceTypes(resourceTypeToCheck).setExcludeFolders(true).getCriteria();

        RepositorySearchServiceImpl service = new RepositorySearchServiceImpl();
        service.setSearchModeSettingsResolver(resolverMock.getMock());
        service.setDefaultSearchCriteriaFactory(defaultSearchCriteriaFactory);
        service.setRepositoryService(repositoryService);
        service.getResults(null, searchCritera);
        assertFalse(searchCritera.getResourceTypes() == null || searchCritera.getResourceTypes().isEmpty());
    }

    @Test
    public void getResultsFromSearchCriteriaWithoutFolders_folder() {

        final String resourceTypeToCheck = Folder.class.getName();

        RepositorySearchConfiguration configuration = MockUnitils.createMock(RepositorySearchConfiguration.class).getMock();
        final Mock<SearchModeSettings> searchModeSettingsMock = MockUnitils.createMock(SearchModeSettings.class);
        searchModeSettingsMock.returns(configuration).getRepositorySearchConfiguration();
        final SearchModeSettings searchModeSettings = searchModeSettingsMock.getMock();
        final Mock<SearchModeSettingsResolver> resolverMock = MockUnitils.createMock(SearchModeSettingsResolver.class);
        resolverMock.returns(searchModeSettings).getSettings(SearchMode.SEARCH);
        final ResourceTypeSearchCriteriaFactory defaultSearchCriteriaFactory = new ResourceTypeSearchCriteriaFactory();
        RepositoryService repositoryService = new HibernateRepositoryServiceImpl(){
            @Override
            public List<ResourceLookup> getResources(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory, List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory, int current, int max) {
                assertEquals(Resource.class.getName(), ((ResourceTypeSearchCriteriaFactory)searchCriteriaFactory).getResourceType());
                return new ArrayList<ResourceLookup>();
            }
        };


        RepositorySearchCriteria searchCritera = new RepositorySearchCriteriaImpl.Builder().setSearchMode(SearchMode.SEARCH).setResourceTypes(resourceTypeToCheck).setExcludeFolders(true).getCriteria();

        RepositorySearchServiceImpl service = new RepositorySearchServiceImpl();
        service.setSearchModeSettingsResolver(resolverMock.getMock());
        service.setDefaultSearchCriteriaFactory(defaultSearchCriteriaFactory);
        service.setRepositoryService(repositoryService);
        service.getResults(null, searchCritera);
        assertFalse(searchCritera.getResourceTypes() == null || searchCritera.getResourceTypes().isEmpty());
    }

    @Test
    public void getDiagnosticDataTest() {
        //Setup mock behavior
        Mock<RepositoryService> repositoryServiceMock = MockUnitils.createMock(RepositoryService.class);
        Mock<Map> filterOptionToResourceTypesMock = MockUnitils.createMock(Map.class);
        Mock<SearchCriteriaFactory> defaultSearchCriteriaFactoryMock = MockUnitils.createMock(SearchCriteriaFactory.class);
        Mock<TransformerFactory> transformerFactoryMock = MockUnitils.createMock(TransformerFactory.class);

        repositorySearchService.getMock().setRepositoryService(repositoryServiceMock.getMock());
        repositorySearchService.getMock().setFilterOptionToResourceTypes(filterOptionToResourceTypesMock.getMock());
        repositorySearchService.getMock().setDefaultSearchCriteriaFactory(defaultSearchCriteriaFactoryMock.getMock());
        repositorySearchService.getMock().setTransformerFactory(transformerFactoryMock.getMock());

        Mock<RepositorySearchConfiguration> configuration =  MockUnitils.createMock(RepositorySearchConfiguration.class);
        repositorySearchService.returns(configuration).getConfiguration(null);

        Mock<SearchCriteriaFactory> factory = MockUnitils.createMock(SearchCriteriaFactory.class);
        defaultSearchCriteriaFactoryMock.returns(factory).newFactory(null);

        Mock<SearchFilter> searchFilterMock1 = MockUnitils.createMock(SearchFilter.class);
        Mock<SearchFilter> searchFilterMock2 = MockUnitils.createMock(SearchFilter.class);
        Mock<SearchFilter> searchFilterMock3 = MockUnitils.createMock(SearchFilter.class);

        ArrayList<SearchFilter> allFiltersList = new ArrayList<SearchFilter>();
        allFiltersList.add(searchFilterMock1.getMock());
        allFiltersList.add(searchFilterMock2.getMock());
        allFiltersList.add(searchFilterMock3.getMock());

        Mock<ExecutionContext> executionContextMock = MockUnitils.createMock(ExecutionContext.class);

        repositorySearchService.returns(allFiltersList).createAllFiltersList(configuration.getMock(), null);
        repositorySearchService.returns(executionContextMock).putCriteriaToContext(null, null);

        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = repositorySearchService.getMock().getDiagnosticData();

        //Testing total size of diagnostic attributes collected from SessionRegistryDiagnosticService
        assertEquals(5, resultDiagnosticData.size());

        repositoryServiceMock.returns(30).getResourcesCount(executionContextMock.getMock(), factory.getMock(), allFiltersList, null, transformerFactoryMock.getMock());

        //Test getting total report count
        int totalReportCount = (Integer)resultDiagnosticData.
                get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.TOTAL_REPORTS_COUNT, null, null)).getDiagnosticAttributeValue();

        filterOptionToResourceTypesMock.assertInvoked().get("resourceTypeFilter-reports");
        assertEquals(30, totalReportCount);

        //Refresh getResourceCount for repository service mock
        repositoryServiceMock.resetBehavior();
        repositoryServiceMock.returns(40).getResourcesCount(executionContextMock.getMock(), factory.getMock(), allFiltersList, null, transformerFactoryMock.getMock());

        //Test getting total reports output count
        int totalReportsOutputCount = (Integer)resultDiagnosticData.
                get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.TOTAL_REPORT_OUTPUTS_COUNT, null, null)).getDiagnosticAttributeValue();

        filterOptionToResourceTypesMock.assertInvoked().get("resourceTypeFilter-reportOutput");
        assertEquals(40, totalReportsOutputCount);

        //Refresh getResourceCount for repository service mock
        repositoryServiceMock.resetBehavior();
        repositoryServiceMock.returns(50).getResourcesCount(executionContextMock.getMock(), factory.getMock(), allFiltersList, null, transformerFactoryMock.getMock());

        //Test getting total olap views count
        int totalOlapViewCount = (Integer)resultDiagnosticData.
                get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.TOTAL_OLAP_VIEWS_COUNT, null, null)).getDiagnosticAttributeValue();

        filterOptionToResourceTypesMock.assertInvoked().get("resourceTypeFilter-view");
        assertEquals(50, totalOlapViewCount);

        //Refresh getResourceCount for repository service mock
        repositoryServiceMock.resetBehavior();
        repositoryServiceMock.returns(60).getResourcesCount(executionContextMock.getMock(), factory.getMock(), allFiltersList, null, transformerFactoryMock.getMock());

        //Test getting total data sources count
        int totalDataSourcesCount = (Integer)resultDiagnosticData.
                get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.TOTAL_DATA_SOURCES_COUNT, null, null)).getDiagnosticAttributeValue();

        filterOptionToResourceTypesMock.assertInvoked().get("resourceTypeFilter-dataSources");
        assertEquals(60, totalDataSourcesCount);

        repositoryServiceMock.returns(2).getFoldersCount(null);

        //Test getting total data sources count
        int totalFoldersCount = (Integer)resultDiagnosticData.
                get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.TOTAL_FOLDERS_COUNT, null, null)).getDiagnosticAttributeValue();
        assertEquals(2, totalFoldersCount);
    }

}
