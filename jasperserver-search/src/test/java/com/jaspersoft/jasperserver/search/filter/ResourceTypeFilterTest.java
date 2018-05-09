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

package com.jaspersoft.jasperserver.search.filter;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.common.SearchAttributes;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import com.jaspersoft.jasperserver.search.service.impl.RepositorySearchCriteriaImpl;
import com.jaspersoft.jasperserver.search.state.State;
import org.hamcrest.Matcher;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ExistsSubqueryExpression;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>Tests {@code ResourceTypeFilter} class.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceTypeFilterTest {
    @Mock
    private Map<String, List<String>> filterOptionToResourceTypes;

    @Mock
    private SearchCriteria searchCriteria;

    @Mock
    private ResourceFactory resourceFactory;

    @Mock
    private ExecutionContext executionContext;

    @Mock
    private SearchAttributes searchAttributes;

    @Mock
    private State state;

    @Mock
    private Map<String, String> customStringStringMap;

    private final static String CLIENT_TYPE_1 = "com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JndiJdbcReportDataSource"; 
    private final static String CLIENT_TYPE_2 = "com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource"; 
    
    private final static String PERSISTENT_TYPE_1 = "com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoJndiJdbcDataSource"; 
    private final static String PERSISTENT_TYPE_2 = "com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource.RepoJdbcDataSource";
    
    @Before
    public void setUp() {
        when(executionContext.getAttributes()).thenReturn(Collections.singletonList(searchAttributes));
        when(searchAttributes.getState()).thenReturn(state);
        when(searchAttributes.getState().getCustomFiltersMap()).thenReturn(customStringStringMap);
    }

    @Test
    public void applyRestrictionsWithoutSearchAttributes() {
        when(executionContext.getAttributes()).thenReturn(null);

        ResourceTypeFilter resourceTypeFilter = new ResourceTypeFilter();

        resourceTypeFilter.setFilterOptionToResourceTypes(filterOptionToResourceTypes);
        resourceTypeFilter.setPersistentClassMappings(resourceFactory);

        try {
            resourceTypeFilter.applyRestrictions(null, executionContext, searchCriteria);
            fail("Exception had to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Resource type filter not found in the custom filters map.", e.getMessage());
        }
    }

    @Test
    public void applyRestrictionsWithoutState() {
        when(searchAttributes.getState()).thenReturn(null);

        ResourceTypeFilter resourceTypeFilter = new ResourceTypeFilter();

        resourceTypeFilter.setFilterOptionToResourceTypes(filterOptionToResourceTypes);
        resourceTypeFilter.setPersistentClassMappings(resourceFactory);

        try {
            resourceTypeFilter.applyRestrictions(null, executionContext, searchCriteria);
            fail("Exception had to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Resource type filter not found in the custom filters map.", e.getMessage());
        }
    }

    @Test
    public void applyRestrictionsWithoutFilter() {
        when(searchAttributes.getState().getCustomFiltersMap().get("resourceTypeFilter")).thenReturn(null);

        ResourceTypeFilter resourceTypeFilter = new ResourceTypeFilter();

        resourceTypeFilter.setFilterOptionToResourceTypes(filterOptionToResourceTypes);
        resourceTypeFilter.setPersistentClassMappings(resourceFactory);

        resourceTypeFilter.applyRestrictions(null, executionContext, searchCriteria);

        verify(searchCriteria, never()).add(null);
    }

    @Test
    public void applyRestrictionsWithoutMapping() {
        when(searchAttributes.getState().getCustomFiltersMap().get("resourceTypeFilter")).thenReturn("someOption");
        when(filterOptionToResourceTypes.get("someOption")).thenReturn(null);

        ResourceTypeFilter resourceTypeFilter = new ResourceTypeFilter();

        resourceTypeFilter.setFilterOptionToResourceTypes(filterOptionToResourceTypes);
        resourceTypeFilter.setPersistentClassMappings(resourceFactory);

        resourceTypeFilter.applyRestrictions(null, executionContext, searchCriteria);

        verify(searchCriteria, never()).add(null);
    }

    @Test
    public void applyRestrictions() throws IOException {
        List<String> resourceTypes = new ArrayList<String>();
        resourceTypes.add(CLIENT_TYPE_1);
        resourceTypes.add(CLIENT_TYPE_2);

        when(searchAttributes.getState().getCustomFiltersMap().get("resourceTypeFilter")).thenReturn("someOption");
        when(filterOptionToResourceTypes.get("someOption")).thenReturn(resourceTypes);

        when(resourceFactory.getImplementationClassName(CLIENT_TYPE_1)).thenReturn(PERSISTENT_TYPE_1);
        when(resourceFactory.getImplementationClassName(CLIENT_TYPE_2)).thenReturn(PERSISTENT_TYPE_2);

        ResourceTypeFilter resourceTypeFilter = new ResourceTypeFilter();

        resourceTypeFilter.setFilterOptionToResourceTypes(filterOptionToResourceTypes);
        resourceTypeFilter.setPersistentClassMappings(resourceFactory);

        // Expected criteria.
        SearchCriteria expectedCriteria = SearchCriteria.forClass(RepoResource.class);
        expectedCriteria.add(Restrictions.in("resourceType", resourceTypes));

        // Actual criteria.
        SearchCriteria actualCriteria = SearchCriteria.forClass(RepoResource.class);
        SearchCriteria actualCriteriaSpy = spy(actualCriteria);
        resourceTypeFilter.applyRestrictions(null, executionContext, actualCriteriaSpy);

        assertArrayEquals(serializeToByteArray(expectedCriteria), serializeToByteArray(actualCriteria));
        verify(actualCriteriaSpy, times(1)).add(any(Criterion.class));

        // Second actual criteria.
        SearchCriteria secondCallActualCriteria = SearchCriteria.forClass(RepoResource.class);
        SearchCriteria secondCallActualCriteriaSpy = spy(secondCallActualCriteria);
        resourceTypeFilter.applyRestrictions(null, executionContext, secondCallActualCriteriaSpy);

        assertArrayEquals(serializeToByteArray(expectedCriteria), serializeToByteArray(secondCallActualCriteria));
        verify(secondCallActualCriteriaSpy, times(1)).add(any(Criterion.class));
    }

    @Test
    public void applyRestrictions_repositorySearchCriteria_defaultTypes() throws IOException {
        List<String> types = new ArrayList<String>();
        types.add(CLIENT_TYPE_1);

        RepositorySearchCriteria repositorySearchCriteria = new RepositorySearchCriteriaImpl();
        repositorySearchCriteria.setResourceTypes(types);
        when(executionContext.getAttributes()).thenReturn(Collections.singletonList(repositorySearchCriteria));

        ResourceTypeFilter resourceTypeFilter = new ResourceTypeFilter();

        SearchCriteria actualCriteria = SearchCriteria.forClass(RepoResource.class);
        SearchCriteria actualCriteriaSpy = spy(actualCriteria);
        resourceTypeFilter.applyRestrictions(null, executionContext, actualCriteriaSpy);

        verify(actualCriteriaSpy, times(1)).add(Mockito.argThat(new ArgumentMatcher<Criterion>() {
            @Override
            public boolean matches(Object argument) {
                return Restrictions.in("resourceType", Collections.singletonList(CLIENT_TYPE_1)).toString()
                        .equals(argument.toString());
            }
        }));
    }

    @Test
    public void applyRestrictions_repositorySearchCriteria_filterSecureFileType() throws IOException {
        List<String> types = new ArrayList<String>();
        types.add(CLIENT_TYPE_1);
        types.add(FileResource.TYPE_SECURE_FILE);
        types.add(FileResource.class.getName());

        RepositorySearchCriteria repositorySearchCriteria = new RepositorySearchCriteriaImpl();
        repositorySearchCriteria.setResourceTypes(types);
        when(executionContext.getAttributes()).thenReturn(Collections.singletonList(repositorySearchCriteria));

        ResourceTypeFilter resourceTypeFilter = new ResourceTypeFilter();

        SearchCriteria actualCriteria = SearchCriteria.forClass(RepoResource.class);
        SearchCriteria actualCriteriaSpy = spy(actualCriteria);
        resourceTypeFilter.applyRestrictions(null, executionContext, actualCriteriaSpy);

        verify(actualCriteriaSpy, times(1)).add(Mockito.argThat(new ArgumentMatcher<Criterion>() {
            @Override
            public boolean matches(Object argument) {
                return argument instanceof ExistsSubqueryExpression;
            }
        }));
    }

    @Test
    public void applyRestrictions_repositorySearchCriteria_addFolders() throws IOException {
        List<String> types = new ArrayList<String>();
        types.add(Folder.class.getName());

        RepositorySearchCriteria repositorySearchCriteria = new RepositorySearchCriteriaImpl();
        repositorySearchCriteria.setResourceTypes(types);
        when(executionContext.getAttributes()).thenReturn(Collections.singletonList(repositorySearchCriteria));

        ResourceTypeFilter resourceTypeFilter = new ResourceTypeFilter();

        SearchCriteria actualCriteria = SearchCriteria.forClass(RepoResource.class);
        SearchCriteria actualCriteriaSpy = spy(actualCriteria);

        resourceTypeFilter.applyRestrictions(null, executionContext, actualCriteriaSpy);

        Matcher<Criterion> matcher = new ArgumentMatcher<Criterion>() {
            @Override
            public boolean matches(Object argument) {
                return argument != null &&
                        Restrictions.isNull("resourceType").toString().equals(argument.toString());
            }
        };

        verify(actualCriteriaSpy, never()).add(Mockito.argThat(matcher));

        resourceTypeFilter.applyRestrictions(ResourceLookup.class.getName(), executionContext, actualCriteriaSpy);
        verify(actualCriteriaSpy, times(1)).add(Mockito.argThat(matcher));

        types.remove(0);
        resourceTypeFilter.applyRestrictions(Resource.class.getName(), executionContext, actualCriteriaSpy);
        verify(actualCriteriaSpy, times(1)).add(Mockito.argThat(matcher));
    }

    private byte[] serializeToByteArray(Serializable serializable) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(serializable);
        byte[] bytes = bos.toByteArray();

        out.close();
        bos.close();

        return bytes;
    }
}
