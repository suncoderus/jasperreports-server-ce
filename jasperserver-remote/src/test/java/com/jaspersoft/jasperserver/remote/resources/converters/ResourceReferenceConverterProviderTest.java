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
package com.jaspersoft.jasperserver.remote.resources.converters;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableDataType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ResourceReferenceConverterProviderTest {
    @InjectMocks
    private ResourceReferenceConverterProvider provider = new ResourceReferenceConverterProvider();
    @Mock
    private ResourceConverterProvider resourceConverterProvider = new ResourceConverterProviderImpl();
    @Mock
    private RepositoryService repositoryService = mock(RepositoryService.class);

    @BeforeClass
    public void initProvider(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getConverterForType(){
        final ResourceReferenceConverter converterForType = provider.getConverterForType(ClientReferenceableDataType.class);
        assertNotNull(converterForType);
        assertSame(converterForType.repositoryService, repositoryService);
        assertSame(converterForType.resourceConverterProvider, resourceConverterProvider);
        assertEquals(converterForType.restrictions.size(), 1);
        final ClientReferenceRestriction restriction = (ClientReferenceRestriction) converterForType.restrictions.get(0);
        assertTrue(restriction instanceof ResourceReferenceConverter.ReferenceClassRestriction);
        assertSame(((ResourceReferenceConverter.ReferenceClassRestriction)restriction).targetClientClass, ClientReferenceableDataType.class);
    }
}
