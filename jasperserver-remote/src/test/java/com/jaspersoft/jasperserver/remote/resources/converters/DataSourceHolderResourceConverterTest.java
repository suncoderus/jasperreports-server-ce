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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.dto.resources.AbstractClientDataSourceHolder;
import com.jaspersoft.jasperserver.dto.resources.ClientAwsDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientQuery;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableDataSource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.Authentication;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class DataSourceHolderResourceConverterTest {
    private static final String TEST_RESORUCE_URI = "/test/resoruce/uri";
    private static final String TEST_REFERENCE_URI = "/test/reference/uri";

    @InjectMocks
    private DataSourceHolderResourceConverter converter = mock(DataSourceHolderResourceConverter.class);
    @Mock
    protected ResourceReferenceConverterProvider resourceReferenceConverterProvider;
    @Mock
    private PermissionsService permissionsService;

    private ResourceReferenceConverter resourceReferenceConverter = mock(ResourceReferenceConverter.class);
    private final ObjectPermission permission = new ObjectPermissionImpl();

    private ResourceReference testResourceReference = new ResourceReference(TEST_REFERENCE_URI);
    private Query serverObject = new QueryImpl();

    @BeforeClass
    public void initConverter() throws Exception, MandatoryParameterNotFoundException {
        MockitoAnnotations.initMocks(this);
        when(converter.getDateTimeFormat()).thenReturn(new SimpleDateFormat());
        when(converter.genericFieldsToClient(any(AbstractClientDataSourceHolder.class), any(Resource.class), any(ToClientConversionOptions.class))).thenCallRealMethod();
        when(converter.genericFieldsToServer(any(AbstractClientDataSourceHolder.class), any(Resource.class), any(ToServerConversionOptions.class))).thenCallRealMethod();
        when(converter.getDataSourceFromResource(any(Resource.class))).thenAnswer(new Answer<ResourceReference>() {
            @Override
            public ResourceReference answer(InvocationOnMock invocation) throws Throwable {
                return ((Query) invocation.getArguments()[0]).getDataSource();
            }
        });
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] arguments = invocationOnMock.getArguments();
                ((Query)arguments[1]).setDataSource((ResourceReference) arguments[0]);
                return null;
            }
        }).when(converter).setDataSourceToResource(any(ResourceReference.class), any(Resource.class));
        when(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableDataSource.class)).thenReturn(resourceReferenceConverter);
        permission.setPermissionMask(1);
        when(permissionsService.getEffectivePermission(any(Resource.class), any(Authentication.class))).thenReturn(permission);
    }

    @Test
    public void genericFieldsToClient_DataSource(){
        final ClientQuery clientObject = new ClientQuery();
        testResourceReference.setLocalResource(serverObject);
        final ClientAwsDataSource expectedClientDataSource = new ClientAwsDataSource();
        serverObject.setDataSource(testResourceReference);
        serverObject.setURIString(TEST_RESORUCE_URI);
        final ToClientConversionOptions options = ToClientConversionOptions.getDefault();
        when(resourceReferenceConverter.toClient(testResourceReference, null)).thenReturn(expectedClientDataSource);
        final AbstractClientDataSourceHolder result = converter.genericFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        assertNotNull(result.getDataSource());
        assertSame(result.getDataSource(), expectedClientDataSource);
        assertEquals(result.getUri(), TEST_RESORUCE_URI);
    }

    @Test
    public void genericFieldsToServer_DataSource_nullIsChangedToReference() throws Exception {
        final ClientQuery clientObject = new ClientQuery();
        clientObject.setLabel("m");
        final ClientAwsDataSource clientDataSource = new ClientAwsDataSource();
        clientObject.setDataSource(clientDataSource);
        clientObject.setUri(TEST_RESORUCE_URI);
        serverObject.setDataSource((ResourceReference)null);
        testResourceReference.setReference(TEST_REFERENCE_URI);
        when(resourceReferenceConverter.toServer(clientDataSource, null, null)).thenReturn(testResourceReference);
        final Resource result = converter.genericFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertSame(((Query)result).getDataSource(), testResourceReference);
        assertEquals(result.getURIString(), TEST_RESORUCE_URI);
    }

    @Test
    public void genericFieldsToServer_DataSource_referenceIsChangedToNull() throws Exception {
        final ClientQuery clientObject = new ClientQuery();
        clientObject.setLabel("m");
        final ClientAwsDataSource clientDataSource = new ClientAwsDataSource();
        testResourceReference.setReference("/test/resoruce/uri");
        serverObject.setDataSource(testResourceReference);
        when(resourceReferenceConverter.toServer(clientDataSource, null)).thenReturn(testResourceReference);
        final Resource result = converter.genericFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertNull(((Query) result).getDataSource());
    }

    @Test
    public void genericFieldsToServer_DataSource_Null() throws Exception {
        final ClientQuery clientObject = new ClientQuery();
        clientObject.setLabel("m");
        final ClientAwsDataSource clientDataSource = new ClientAwsDataSource();
        testResourceReference.setReference("/test/resoruce/uri");
        serverObject.setDataSource((ResourceReference)null);
        when(resourceReferenceConverter.toServer(clientDataSource, null)).thenReturn(testResourceReference);
        final Resource result = converter.genericFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertNull(((Query) result).getDataSource());
    }

    @Test
    public void genericFieldsToServer_newInstanceOfServerObjectIsCreated() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        final ClientQuery clientObject = new ClientQuery();
        clientObject.setLabel("m");
        final ClientAwsDataSource clientDataSource = new ClientAwsDataSource();
        clientObject.setDataSource(clientDataSource);
        testResourceReference.setReference("/test/resoruce/uri");
        when(resourceReferenceConverter.toServer(clientDataSource, null, null)).thenReturn(testResourceReference);
        final QueryImpl newServerObject = new QueryImpl();
        when(converter.getNewResourceInstance()).thenReturn(newServerObject);
        final Resource resource = converter.genericFieldsToServer(clientObject, null, null);
        assertSame(resource, newServerObject);
        assertSame(((QueryImpl)resource).getDataSource(), testResourceReference);
    }
}
