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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.AwsReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientAwsDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientDataType;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceable;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableDataType;
import com.jaspersoft.jasperserver.dto.resources.ClientUriHolder;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ResourceReferenceConverterTest {
    private ResourceReferenceConverter converter;
    private ResourceConverterProvider resourceConverterProvider;
    private RepositoryService repositoryService;
    private PermissionsService permissionsService;
    private ConfigurationBean configurationBean = new ConfigurationBean();
    private ToServerConversionOptions options = ToServerConversionOptions.getDefault();

    @BeforeMethod
    public void initMocks() {
        configurationBean.setResourceIdNotSupportedSymbols("@#$%");
        resourceConverterProvider = mock(ResourceConverterProvider.class);
        repositoryService = mock(RepositoryService.class);
        converter = new ResourceReferenceConverter(resourceConverterProvider, repositoryService, permissionsService, configurationBean);
    }

    @Test
    public void toClient_nullProducesNull(){
        assertNull(converter.toClient(null, null));
    }

    @Test
    public void toClient_localResource_expanded() {
        final Resource localResource = new DataTypeImpl();
        final String testUri = "/test/resource/uri";
        localResource.setURIString(testUri);
        final ResourceReference serverObject = new ResourceReference(localResource);
        final ClientDataType expectedClientObject = new ClientDataType();
        expectedClientObject.setUri(testUri);
        when(resourceConverterProvider.getToClientConverter(localResource)).thenReturn(new ToClientConverter() {
            @Override
            public Object toClient(Object serverObject, Object options) {
                assertSame(serverObject, localResource);
                return expectedClientObject;
            }

            @Override
            public String getClientResourceType() {
                return null;
            }
        });
        final ClientUriHolder result = converter.toClient(serverObject, new ToClientConversionOptions().setExpanded(true));
        assertSame(result, expectedClientObject);
        assertEquals(result.getUri(), testUri);
    }

    @Test
    public void toClient_localResource_collapsed() {
        final Resource localResource = new DataTypeImpl();
        final String testUri = "/test/resource/uri";
        localResource.setURIString(testUri);
        final ResourceReference serverObject = new ResourceReference(localResource);
        final ClientDataType expectedClientObject = new ClientDataType();
        expectedClientObject.setUri(testUri);
        when(resourceConverterProvider.getToClientConverter(localResource)).thenReturn(new ToClientConverter() {
            @Override
            public Object toClient(Object serverObject, Object options) {
                assertSame(serverObject, localResource);
                return expectedClientObject;
            }

            @Override
            public String getClientResourceType() {
                return null;
            }
        });
        final ClientUriHolder result = converter.toClient(serverObject, null);
        assertEquals(result.getUri(), testUri);
        assertTrue(result instanceof ClientReference);
    }

    @Test
    public void toClient_reference() {
        final String expectedReferenceUri = "/test/reference/uri";
        final ResourceReference serverObject = new ResourceReference(expectedReferenceUri);
        final ClientReference expectedReference = new ClientReference();
        expectedReference.setUri(expectedReferenceUri);
        final ClientUriHolder result = converter.toClient(serverObject, null);
        assertTrue(new ReflectionEquals(expectedReference).matches(result));
    }

    @Test
    public void toServer_create_noInitialReference() throws Exception {
        final ClientJdbcDataSource clientObject = new ClientJdbcDataSource();
        final ResourceReferenceConverter referenceConverter = mock(ResourceReferenceConverter.class);
        when(referenceConverter.toServer(clientObject, null)).thenCallRealMethod();
        final ResourceReference resourceReference = referenceConverter.toServer(clientObject, null);
        assertNull(resourceReference);
        verify(referenceConverter).toServer(clientObject, null);
    }

    @Test
    public void toServer() throws Exception {
        final ClientReference reference = new ClientReference();
        final ResourceReferenceConverter converterMock = mock(ResourceReferenceConverter.class);
        when(converterMock.toServer(any(ClientReferenceable.class), isNull(ResourceReference.class), any(ToServerConversionOptions.class))).thenCallRealMethod();
        converterMock.toServer(reference, null, options);
        verify(converterMock).toServerReference(same(reference), isNull(ResourceReference.class), same(options));
        final ClientDataType dataType = new ClientDataType();
        converterMock.toServer(dataType, null, options);
        verify(converterMock).toServerLocalResource(same(dataType), isNull(ResourceReference.class), same(options));
        final ResourceReference result = converter.toServer(null, null);
        assertNull(result);
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void validateReference_uriIsNull_exception() throws Exception {
        converter.validateAndGetReference(null, options.getOwnersUri());
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void validateReference_resourceDoesntExist() throws Exception {
        converter.validateAndGetReference("/test/resource/uri", options.getOwnersUri());
    }

    @Test()
    public void validateReference_additionalRestrictionIsCalled() throws Exception {
        final String referenceUri = "/test/resource/uri";
        final DataType resource = new DataTypeImpl();
        final ClientDataType clientTargetResource = new ClientDataType();
        when(repositoryService.getResource(any(ExecutionContext.class), eq(referenceUri))).thenReturn(resource);
        when(resourceConverterProvider.getToClientConverter(resource)).thenReturn(new ToClientConverter() {
            @Override
            public Object toClient(Object serverObject, Object options) {
                return clientTargetResource;
            }

            @Override
            public String getClientResourceType() {
                return null;
            }
        });
        final ClientReferenceRestriction restriction = mock(ClientReferenceRestriction.class);
        final IllegalParameterValueException expectedException = new IllegalParameterValueException("");
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw expectedException;
            }
        }).when(restriction).validateReference(clientTargetResource);
        IllegalParameterValueException exception = null;
        try {
            converter.addReferenceRestriction(restriction).validateAndGetReference(referenceUri, options.getOwnersUri());
        } catch (IllegalParameterValueException e) {
            exception = e;
        }
        assertSame(exception, expectedException);
    }

    @Test
    public void toServerReference_theSameReferenceUri_noUpdate() throws Exception {
        final String expectedResourceUri = "/test/resource/uri";
        final ClientReference clientObject = new ClientReference();
        clientObject.setUri(expectedResourceUri);
        final ResourceReference resultToUpdate = new ResourceReference(expectedResourceUri);
        final ResourceReference result = converter.toServerReference(clientObject, resultToUpdate, options);
        assertSame(result, resultToUpdate);
        assertFalse(result.isLocal());
        assertEquals(result.getReferenceURI(), expectedResourceUri);
    }

    @Test
    public void toServerReference_validationIsCalled() throws Exception {
        final ClientReference reference = new ClientReference();
        final String referenceUri = "/test/resource/uri";
        reference.setUri(referenceUri);
        final ResourceReferenceConverter converterMock = mock(ResourceReferenceConverter.class);
        when(converterMock.toServerReference(reference, null, options)).thenCallRealMethod();
        converterMock.toServerReference(reference, null, options);
        verify(converterMock).validateAndGetReference(referenceUri, options.getOwnersUri());
    }

    @Test
    public void toServerReference_initialResourceReferenceIsNull_newInstanceIsCreated() throws Exception {
        final String expectedResourceUri = "/test/resource/uri";
        final ClientReference clientObject = new ClientReference();
        clientObject.setUri(expectedResourceUri);
        // let repository mock find resource with our test URI
        when(repositoryService.getResource(any(ExecutionContext.class), eq(expectedResourceUri))).thenReturn(mock(Resource.class));
        // new ResourceReference is created if initially is null and client object contains referenceUri
        ResourceReference result = converter.toServerReference(clientObject, null, options);
        assertNotNull(result);
        assertFalse(result.isLocal());
        assertEquals(result.getReferenceURI(), expectedResourceUri);
    }

    @Test
    public void toServerReference_anotherReferenceUri_referenceIsUpdated() throws Exception {
        final String expectedResourceUri = "/test/resource/uri";
        final ClientReference clientObject = new ClientReference();
        clientObject.setUri(expectedResourceUri);
        // let repository mock find resource with our test URI
        when(repositoryService.getResource(any(ExecutionContext.class), eq(expectedResourceUri))).thenReturn(mock(Resource.class));
        final ResourceReference resultToUpdate = new ResourceReference("/another/resource/uri");
        ResourceReference result = converter.toServerReference(clientObject, resultToUpdate, options);
        assertSame(result, resultToUpdate);
        assertFalse(result.isLocal());
        // existing ResourceReference instance is updated with new reference URI
        assertEquals(result.getReferenceURI(), expectedResourceUri);
    }

    @Test
    public void toServerReference_sameUri_localResourceIsNotChangedToReference() throws Exception {
        final String expectedResourceUri = "/test/resource/uri";
        final ClientReference clientObject = new ClientReference();
        clientObject.setUri(expectedResourceUri);
        // let repository mock find resource with our test URI
        when(repositoryService.getResource(isNull(ExecutionContext.class), eq(expectedResourceUri))).thenReturn(mock(Resource.class));
        // reference to update is local resource.
        final ResourceReference resultToUpdate = new ResourceReference(new DataTypeImpl());
        resultToUpdate.getLocalResource().setURIString(expectedResourceUri);
        ResourceReference result = converter.toServerReference(clientObject, resultToUpdate, options);
        assertSame(result, resultToUpdate);
        assertTrue(result.isLocal());
    }


    @Test
    public void toServerReference_changedUri_noContext_resultisReference() throws Exception {
        final String expectedResourceUri = "/test/resource/uri_files/local";
        final ClientReference clientObject = new ClientReference();
        clientObject.setUri(expectedResourceUri);
        Resource testResource = mock(Resource.class);
        // let repository mock find resource with our test URI
        when(repositoryService.getResource(any(ExecutionContext.class), eq(expectedResourceUri))).thenReturn(testResource);
        // reference some another resource
        final ResourceReference resultToUpdate = new ResourceReference(new DataTypeImpl());
        resultToUpdate.getLocalResource().setURIString("/some/another/resource");
        ResourceReference result = converter.toServerReference(clientObject, resultToUpdate, options);
        assertEquals(result.getTargetURI(), expectedResourceUri);
        assertFalse(result.isLocal());
    }


    @Test
    public void toServerReference_changedUri_noResultToUpdate_noContext_resultisReference() throws Exception {
        final String expectedResourceUri = "/test/resource/uri_files/local";
        final ClientReference clientObject = new ClientReference();
        clientObject.setUri(expectedResourceUri);
        Resource testResource = new DataTypeImpl();
        testResource.setURIString(expectedResourceUri);
        // let repository mock find resource with our test URI
        when(repositoryService.getResource(any(ExecutionContext.class), eq(expectedResourceUri))).thenReturn(testResource);
        ResourceReference result = converter.toServerReference(clientObject, null, options);
        assertEquals(result.getTargetURI(), expectedResourceUri);
        assertFalse(result.isLocal());
    }

    @Test
    public void toServerReference_changedUri_noResultToUpdate_localResourceIsNotChangedToReference() throws Exception {
        final String parentUri = "/test/resource/uri";
        final String expectedResourceUri = parentUri + "_files/local";
        final ClientReference clientObject = new ClientReference();
        clientObject.setUri(expectedResourceUri);
        Resource testResource = new DataTypeImpl();
        testResource.setURIString(expectedResourceUri);
        // let repository mock find resource with our test URI
        when(repositoryService.getResource(any(ExecutionContext.class), eq(expectedResourceUri))).thenReturn(testResource);
        // reference some another resource
        final ResourceReference resultToUpdate = new ResourceReference(new DataTypeImpl());
        resultToUpdate.getLocalResource().setURIString("/some/another/resource");
        ResourceReference result = converter.toServerReference(clientObject, null, ToServerConversionOptions.getDefault().setOwnersUri(parentUri));
        assertEquals(result.getTargetURI(), expectedResourceUri);
        assertTrue(result.isLocal());
    }

    @Test
    public void toServerReference_changedUri_notReallyLocal() throws Exception {
        final String parentUri = "/test/resource/uri";
        final String expectedResourceUri = parentUri + "_files_and_folders";
        final ClientReference clientObject = new ClientReference();
        clientObject.setUri(expectedResourceUri);
        Resource testResource = new DataTypeImpl();
        testResource.setURIString(expectedResourceUri);
        // let repository mock find resource with our test URI
        when(repositoryService.getResource(any(ExecutionContext.class), eq(expectedResourceUri))).thenReturn(testResource);
        // reference some another resource
        final ResourceReference resultToUpdate = new ResourceReference(new DataTypeImpl());
        resultToUpdate.getLocalResource().setURIString("/some/another/resource");
        ResourceReference result = converter.toServerReference(clientObject, null, ToServerConversionOptions.getDefault().setOwnersUri(parentUri));
        assertEquals(result.getTargetURI(), expectedResourceUri);
        assertFalse(result.isLocal());
    }

    @Test
    public void toServerLocalResource_resourceReferenceIsNull() throws Exception {
        final ClientAwsDataSource localResource = new ClientAwsDataSource().setLabel("L");
        final AwsReportDataSource expectedLocalResource = new AwsReportDataSourceImpl();
        final AwsDataSourceResourceConverter awsDataSourceResourceConverter = mock(AwsDataSourceResourceConverter.class);
        when(resourceConverterProvider.getToServerConverter(localResource)).thenReturn((ToServerConverter) awsDataSourceResourceConverter);
        when(awsDataSourceResourceConverter.toServer(localResource, options)).thenReturn(expectedLocalResource);
        final ResourceReference result = converter.toServerLocalResource(localResource, null, options);
        assertNotNull(result);
        assertTrue(result.isLocal());
        assertSame(result.getLocalResource(), expectedLocalResource);
    }

    @Test
    public void toServerLocalResource_referenceIsUpdated() throws Exception {
        final ClientAwsDataSource localResource = new ClientAwsDataSource().setLabel("L");
        final ResourceReference referenceToUpdate = new ResourceReference("/test/resource/uri");
        final AwsReportDataSource expectedLocalResource = new AwsReportDataSourceImpl();
        final AwsDataSourceResourceConverter awsDataSourceResourceConverter = mock(AwsDataSourceResourceConverter.class);
        when(resourceConverterProvider.getToServerConverter(localResource)).thenReturn((ToServerConverter) awsDataSourceResourceConverter);
        when(awsDataSourceResourceConverter.toServer(localResource, options)).thenReturn(expectedLocalResource);
        final ResourceReference result = converter.toServerLocalResource(localResource, referenceToUpdate, options);
        assertSame(result, referenceToUpdate);
        assertTrue(result.isLocal());
        assertSame(result.getLocalResource(), expectedLocalResource);
    }

    @Test
    public void toServerLocalResource_localResourceIsReplaced() throws Exception {
        final ClientAwsDataSource localResource = new ClientAwsDataSource().setLabel("L");;
        final ResourceReference referenceToUpdate = new ResourceReference(new JdbcReportDataSourceImpl());
        final AwsReportDataSource expectedLocalResource = new AwsReportDataSourceImpl();
        final AwsDataSourceResourceConverter awsDataSourceResourceConverter = mock(AwsDataSourceResourceConverter.class);
        when(resourceConverterProvider.getToServerConverter(localResource)).thenReturn((ToServerConverter) awsDataSourceResourceConverter);
        when(awsDataSourceResourceConverter.toServer(localResource, options)).thenReturn(expectedLocalResource);
        final ResourceReference result = converter.toServerLocalResource(localResource, referenceToUpdate, options);
        assertSame(result, referenceToUpdate);
        assertTrue(result.isLocal());
        assertSame(result.getLocalResource(), expectedLocalResource);
    }

    @Test
    public void toServerLocalResource_versionIsReset() throws Exception {
        final ClientAwsDataSource localResource = new ClientAwsDataSource().setLabel("L");;
        localResource.setVersion(100);
        final ResourceReference referenceToUpdate = new ResourceReference(new JdbcReportDataSourceImpl());
        final AwsReportDataSource expectedLocalResource = new AwsReportDataSourceImpl();
        final AwsDataSourceResourceConverter awsDataSourceResourceConverter = mock(AwsDataSourceResourceConverter.class);
        when(resourceConverterProvider.getToServerConverter(localResource)).thenReturn((ToServerConverter) awsDataSourceResourceConverter);
        when(awsDataSourceResourceConverter.toServer(eq(localResource), any(ToServerConversionOptions.class))).thenReturn(expectedLocalResource);
        final ResourceReference result = converter.toServerLocalResource(localResource, referenceToUpdate, ToServerConversionOptions.getDefault().setResetVersion(true));
        assertSame(result, referenceToUpdate);
        assertTrue(result.isLocal());
        assertSame(result.getLocalResource(), expectedLocalResource);
        assertEquals(result.getLocalResource().getVersion(), Resource.VERSION_NEW);
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void referenceClassRestriction_exception() throws Exception {
        new ResourceReferenceConverter.ReferenceClassRestriction(ClientReferenceableDataType.class).validateReference(new ClientJdbcDataSource());
    }

    @Test
    public void referenceClassRestriction_validReference() throws Exception {
        IllegalParameterValueException exception = null;
        try {
            new ResourceReferenceConverter.ReferenceClassRestriction(ClientReferenceableDataType.class).validateReference(new ClientDataType());
        } catch (IllegalParameterValueException e) {
            exception = e;
        }
        assertNull(exception);
    }

    @Test(expectedExceptions = IllegalParameterValueException.class)
    public void fileTypeRestriction_exception() throws Exception {
        final ClientFile file = new ClientFile();
        file.setType(ClientFile.FileType.css);
        new ResourceReferenceConverter.FileTypeRestriction(ClientFile.FileType.jrxml).validateReference(file);
    }

    @Test
    public void fileTypeRestriction_validReference() throws Exception {
        final ClientFile file = new ClientFile();
        file.setType(ClientFile.FileType.jrxml);
        IllegalParameterValueException exception = null;
        try {
            // no exception for non file resources. Another restriction checks resource types
            new ResourceReferenceConverter.FileTypeRestriction(ClientFile.FileType.jrxml).validateReference(new ClientDataType());
            // no exception for file of correct type
            new ResourceReferenceConverter.FileTypeRestriction(ClientFile.FileType.jrxml).validateReference(file);
        } catch (IllegalParameterValueException e) {
            exception = e;
        }
        assertNull(exception);
    }
}
