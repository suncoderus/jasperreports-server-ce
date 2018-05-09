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

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientFolder;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.attachments.AttachmentsProcessor;
import com.jaspersoft.jasperserver.remote.resources.validation.ResourceValidator;
import com.jaspersoft.jasperserver.remote.services.PermissionsService;
import com.jaspersoft.jasperserver.war.cascade.handlers.GenericTypeProcessorRegistry;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.Validator;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ResourceConverterImplTest {
    private static final String TEST_CLIENT_OBJECT_NAME = "testClientObjectName";
    @InjectMocks
    private ResourceConverterImpl converter;
    @Mock
    private PermissionsService permissionsService;
    @Mock
    private GenericTypeProcessorRegistry genericTypeProcessorRegistry;
    @Mock
    private ClientTypeHelper clientTypeHelper;
    @Mock
    private Validator validator;

    private final ObjectPermission permission = new ObjectPermissionImpl();
    private ToServerConversionOptions options;

    @BeforeMethod
    public void resetConverter() {
        converter = mock(ResourceConverterImpl.class);
        permission.setPermissionMask(1);
        MockitoAnnotations.initMocks(this);
        when(permissionsService.getEffectivePermission(any(Resource.class), any(Authentication.class))).thenReturn(permission);
        options = ToServerConversionOptions.getDefault();
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void validateResource() {
        final Resource resource = mock(Resource.class);
        final String testResourceType = "testResourceType";
        when(resource.getResourceType()).thenReturn(testResourceType);
        final ResourceValidator validator = mock(ResourceValidator.class);
        doThrow(JSValidationException.class).when(validator).validate(resource, false);
        when(genericTypeProcessorRegistry.getTypeProcessor(testResourceType, ResourceValidator.class, false)).thenReturn(validator);
        doCallRealMethod().when(converter).validateResource(resource, false);
        converter.validateResource(resource, false);
    }

    @Test
    public void toServer_withValidation() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        ResourceLookup serverObject = new ResourceLookupImpl();
        ResourceLookup anotherServerObject = new ResourceLookupImpl();
        final ClientFolder clientObject = new ClientFolder();
        serverObject.setURIString("/test/uri");
        when(converter.toServer(clientObject, serverObject, options)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientObject, serverObject, options)).thenReturn(anotherServerObject);
        when(converter.resourceSpecificFieldsToServer(clientObject, anotherServerObject, options)).thenReturn(anotherServerObject);
        converter.toServer(clientObject, serverObject, options);
        verify(converter).validateResource(anotherServerObject, false);
    }

    @Test
    public void toServer_withValidation_slipRepoFieldsValidation() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        options.setSkipRepoFieldsValidation(true);
        ResourceLookup serverObject = new ResourceLookupImpl();
        ResourceLookup anotherServerObject = new ResourceLookupImpl();
        final ClientFolder clientObject = new ClientFolder();
        serverObject.setURIString("/test/uri");
        when(converter.toServer(clientObject, serverObject, options)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientObject, serverObject, options)).thenReturn(anotherServerObject);
        when(converter.resourceSpecificFieldsToServer(clientObject, anotherServerObject, options)).thenReturn(anotherServerObject);
        converter.toServer(clientObject, serverObject, options);
        verify(converter).validateResource(anotherServerObject, true);
    }

    @Test
    public void toServer_withValidation_optionsNull() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        ResourceLookup serverObject = new ResourceLookupImpl();
        ResourceLookup anotherServerObject = new ResourceLookupImpl();
        final ClientFolder clientObject = new ClientFolder();
        serverObject.setURIString("/test/uri");
        when(converter.toServer(clientObject, serverObject, null)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientObject, serverObject, null)).thenReturn(anotherServerObject);
        when(converter.resourceSpecificFieldsToServer(clientObject, anotherServerObject, null)).thenReturn(anotherServerObject);
        converter.toServer(clientObject, serverObject, null);
        verify(converter).validateResource(anotherServerObject, false);
    }

    @Test
    public void toServer_validationSuppressed() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        ResourceLookup serverObject = new ResourceLookupImpl();
        ResourceLookup anotherServerObject = new ResourceLookupImpl();
        final ClientFolder clientObject = new ClientFolder();
        ToServerConversionOptions toServerConversionOptions = ToServerConversionOptions.getDefault().setSuppressValidation(true);
        serverObject.setURIString("/test/uri");
        when(converter.toServer(clientObject, serverObject, toServerConversionOptions)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientObject, serverObject, toServerConversionOptions)).thenReturn(anotherServerObject);
        when(converter.resourceSpecificFieldsToServer(clientObject, anotherServerObject, toServerConversionOptions)).thenReturn(anotherServerObject);
        converter.toServer(clientObject, serverObject, toServerConversionOptions);
        verify(converter, never()).validateResource(any(Resource.class), eq(true));
    }

    @Test
    public void internalToClient() throws ParseException {
        ResourceLookup serverObject = new ResourceLookupImpl();
        final String testResourceUri = "/test/resource/uri";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final String testDescription = "testDescription";
        final String testLabel = "testLabel";
        final int testVersion = 123;
        final Date creationDate = simpleDateFormat.parse("1996-05-25T22:15:26.235-0700");
        final Date updateDate = simpleDateFormat.parse("1999-08-31T03:21:55.156-0300");
        final String creationDateString = "creationDate";
        final String updateDateString = "updateDate";

        permission.setPermissionMask(1);
        serverObject.setResourceType("ignoredResourceType");
        serverObject.setURIString(testResourceUri);
        serverObject.setCreationDate(creationDate);
        serverObject.setUpdateDate(updateDate);
        serverObject.setDescription(testDescription);
        serverObject.setLabel(testLabel);
        serverObject.setVersion(testVersion);
        final ClientFolder expectedClientObject = new ClientFolder();
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.format(eq(creationDate), any(StringBuffer.class), any(FieldPosition.class))).thenReturn(new StringBuffer(creationDateString));
        when(dateFormatMock.format(eq(updateDate), any(StringBuffer.class), any(FieldPosition.class))).thenReturn(new StringBuffer(updateDateString));
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        when(converter.genericFieldsToClient(expectedClientObject, serverObject, null)).thenCallRealMethod();
        final ClientResource clientObject = converter.genericFieldsToClient(expectedClientObject, serverObject, null);
        assertSame(clientObject, expectedClientObject);
        assertEquals(clientObject.getCreationDate(), creationDateString);
        assertEquals(clientObject.getUpdateDate(), updateDateString);
        assertEquals(clientObject.getDescription(), testDescription);
        assertEquals(clientObject.getLabel(), testLabel);
        assertEquals(clientObject.getUri(), testResourceUri);
        assertEquals(clientObject.getVersion().intValue(), testVersion);
        assertEquals(clientObject.getPermissionMask().intValue(), permission.getPermissionMask());
    }

    @Test
    public void toServer_withAttachments() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        final HashMap<String, InputStream> attachments = new HashMap<String, InputStream>();
        final ToServerConversionOptions toServerConversionOptions = ToServerConversionOptions.getDefault().setAttachments(attachments);
        final ResourceLookupImpl serverObject = new ResourceLookupImpl();
        final ClientFolder clientObject = new ClientFolder();
        when(converter.toServer(clientObject, serverObject, toServerConversionOptions)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientObject, serverObject, toServerConversionOptions)).thenReturn(serverObject);
        when(converter.resourceSpecificFieldsToServer(clientObject, serverObject, toServerConversionOptions)).thenReturn(serverObject);
        when(converter.getClientTypeClass()).thenReturn(ClientResource.class);
        final AttachmentsProcessor attachmentsProcessor = mock(AttachmentsProcessor.class);
        when(genericTypeProcessorRegistry.getTypeProcessor(ClientResource.class, AttachmentsProcessor.class, false)).thenReturn(attachmentsProcessor);
        when(attachmentsProcessor.processAttachments(serverObject, attachments)).thenReturn(serverObject);
        final Resource result = converter.toServer(clientObject, serverObject, toServerConversionOptions);
        assertSame(result, serverObject);
    }

    @Test
    public void toServer_updateMode() throws Exception {
        final String testResourceType = "testResourceType";
        final String testResourceUri = "/test/resource/uri";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final String testDescription = "testDescription";
        final String testLabel = "testLabel";
        final int testVersion = 123;
        final String creationDateString = "creationDate";
        final String updateDateString = "updateDate";
        final Date creationDate = simpleDateFormat.parse("1996-05-25T22:15:26.235-0700");
        final Date updateDate = simpleDateFormat.parse("1999-08-31T03:21:55.156-0300");
        ClientFolder clientResource = new ClientFolder();
        clientResource.setDescription(testDescription);
        clientResource.setLabel(testLabel);
        clientResource.setUpdateDate(updateDateString);
        clientResource.setUri(testResourceUri);
        clientResource.setVersion(testVersion);
        clientResource.setCreationDate(creationDateString);
        ResourceLookup serverObject = new ResourceLookupImpl();
        serverObject.setURIString(testResourceUri);
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.parse(creationDateString)).thenReturn(creationDate);
        when(dateFormatMock.parse(updateDateString)).thenReturn(updateDate);
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        when(converter.getServerResourceType()).thenReturn(testResourceType);
        when(converter.toServer(clientResource, serverObject, options)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientResource, serverObject, options)).thenCallRealMethod();
        when(converter.resourceSpecificFieldsToServer(clientResource, serverObject, options)).thenReturn(serverObject);
        final Resource resource = converter.toServer(clientResource, serverObject, options);
        assertSame(resource, serverObject);
        assertEquals(resource.getURIString(), clientResource.getUri());
        assertEquals(resource.getDescription(), clientResource.getDescription());
        assertEquals(resource.getLabel(), clientResource.getLabel());
        assertNull(resource.getResourceType());
        assertNull(resource.getUpdateDate());
        assertNull(resource.getCreationDate());
        assertEquals(resource.getVersion(), clientResource.getVersion().intValue());
    }

    @Test
    public void toServer_noVersion() throws Exception {
        final String testResourceType = "testResourceType";
        final String testResourceUri = "/test/resource/uri";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final String testDescription = "testDescription";
        final String testLabel = "testLabel";
        final String creationDateString = "creationDate";
        final String updateDateString = "updateDate";
        final Date creationDate = simpleDateFormat.parse("1996-05-25T22:15:26.235-0700");
        final Date updateDate = simpleDateFormat.parse("1999-08-31T03:21:55.156-0300");
        ClientFolder clientResource = new ClientFolder();
        clientResource.setDescription(testDescription);
        clientResource.setLabel(testLabel);
        clientResource.setUpdateDate(updateDateString);
        clientResource.setUri(testResourceUri);
        clientResource.setCreationDate(creationDateString);
        ResourceLookup serverObject = new ResourceLookupImpl();
        serverObject.setURIString(testResourceUri);
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.parse(creationDateString)).thenReturn(creationDate);
        when(dateFormatMock.parse(updateDateString)).thenReturn(updateDate);
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        when(converter.getServerResourceType()).thenReturn(testResourceType);
        when(converter.toServer(clientResource, serverObject, options)).thenCallRealMethod();
        when(converter.genericFieldsToServer(clientResource, serverObject, options)).thenCallRealMethod();
        when(converter.resourceSpecificFieldsToServer(clientResource, serverObject, options)).thenReturn(serverObject);
        final Resource resource = converter.toServer(clientResource, serverObject, options);

        assertEquals(resource.getVersion(), Resource.VERSION_NEW);
    }

    @Test
    public void toServer_resetVersion() throws Exception {
        final String testResourceType = "testResourceType";
        final String testResourceUri = "/test/resource/uri";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final String testDescription = "testDescription";
        final String testLabel = "testLabel";
        final int testVersion = 123;
        final String creationDateString = "creationDate";
        final String updateDateString = "updateDate";
        final Date creationDate = simpleDateFormat.parse("1996-05-25T22:15:26.235-0700");
        final Date updateDate = simpleDateFormat.parse("1999-08-31T03:21:55.156-0300");
        ClientFolder clientResource = new ClientFolder();
        clientResource.setDescription(testDescription);
        clientResource.setLabel(testLabel);
        clientResource.setUpdateDate(updateDateString);
        clientResource.setUri(testResourceUri);
        clientResource.setVersion(testVersion);
        clientResource.setCreationDate(creationDateString);
        ResourceLookup serverObject = new ResourceLookupImpl();
        serverObject.setURIString(testResourceUri);
        DateFormat dateFormatMock = mock(DateFormat.class);
        when(dateFormatMock.parse(creationDateString)).thenReturn(creationDate);
        when(dateFormatMock.parse(updateDateString)).thenReturn(updateDate);
        when(converter.getDateTimeFormat()).thenReturn(dateFormatMock);
        when(converter.getServerResourceType()).thenReturn(testResourceType);
        when(converter.toServer(eq(clientResource), eq(serverObject), any(ToServerConversionOptions.class))).thenCallRealMethod();
        when(converter.genericFieldsToServer(eq(clientResource), eq(serverObject), any(ToServerConversionOptions.class))).thenCallRealMethod();
        when(converter.resourceSpecificFieldsToServer(eq(clientResource), eq(serverObject), any(ToServerConversionOptions.class))).thenReturn(serverObject);
        final Resource resource = converter.toServer(clientResource, serverObject, ToServerConversionOptions.getDefault().setResetVersion(true));

        assertEquals(resource.getVersion(), Resource.VERSION_NEW);
    }

    @Test
    public void genericFieldsToServer_resourceToUpdateIsNew() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        ClientResource expectedClientObject = new ClientFolder().setLabel("testLabel");
        Resource expectedNewObject = new FolderImpl();
        when(converter.getNewResourceInstance()).thenReturn(expectedNewObject);
        when(converter.genericFieldsToServer(expectedClientObject, null, null)).thenCallRealMethod();
        when(converter.getDateTimeFormat()).thenReturn(mock(DateFormat.class));
        final Resource result = converter.genericFieldsToServer(expectedClientObject, null, null);
        assertSame(result, expectedNewObject);
        assertEquals(result.getVersion(), Resource.VERSION_NEW);
    }


    @Test
    public void toClient() {
        ResourceLookup serverObject = new ResourceLookupImpl();
        serverObject.setURIString("/test/uri");
        ClientResource expectedClientObject = new ClientFolder();
        when(converter.getNewClientObjectInstance()).thenReturn(expectedClientObject);
        final ToClientConversionOptions options = ToClientConversionOptions.getDefault();
        when(converter.toClient(serverObject, options)).thenCallRealMethod();
        converter.toClient(serverObject, options);
        verify(converter).genericFieldsToClient(expectedClientObject, serverObject, options);
    }

    @Test
    public void getNewClientObjectInstance() {
        ResourceConverterImpl<Folder, ClientFolder> converter = new ResourceConverterImpl<Folder, ClientFolder>() {
            @Override
            protected Folder resourceSpecificFieldsToServer(ClientFolder clientObject, Folder resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientFolder resourceSpecificFieldsToClient(ClientFolder client, Folder serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        final ClientFolder newClientObjectInstance = converter.getNewClientObjectInstance();
        assertNotNull(newClientObjectInstance);
    }

    @Test
    public void getClientResourceType() {
        when(clientTypeHelper.getClientResourceType()).thenReturn(TEST_CLIENT_OBJECT_NAME);
        when(converter.getClientResourceType()).thenCallRealMethod();
        assertEquals(converter.getClientResourceType(), TEST_CLIENT_OBJECT_NAME);
    }

    @Test
    public void getClientTypeClass_rawConverter() {

        final ResourceConverterImpl resourceConverter = new ResourceConverterImpl() {
            @Override
            protected Resource resourceSpecificFieldsToServer(ClientResource clientObject, Resource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientResource resourceSpecificFieldsToClient(ClientResource client, Resource serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        IllegalStateException exception = null;
        try {
            resourceConverter.getClientTypeClass();
        } catch (IllegalStateException ex) {
            exception = ex;
        }
        assertNotNull(exception);
    }

    @Test
    public void getClientTypeClass() {
        final ResourceConverterImpl resourceConverter = new ResourceConverterImpl<Folder, ClientFolder>() {
            @Override
            protected Folder resourceSpecificFieldsToServer(ClientFolder clientObject, Folder resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientFolder resourceSpecificFieldsToClient(ClientFolder client, Folder serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        final Class<?> clientTypeClass = resourceConverter.getClientTypeClass();
        assertTrue(clientTypeClass == ClientFolder.class);
    }

    @Test
    public void toServer() throws Exception {
        ClientResource expectedClientObject = new ClientFolder();
        Resource expectedNewObject = new FolderImpl();
        when(converter.toServer(expectedClientObject, null, null)).thenReturn(expectedNewObject);
        when(converter.toServer(expectedClientObject, null)).thenCallRealMethod();
        final Resource resource = converter.toServer(expectedClientObject, null);
        assertSame(resource, expectedNewObject);
    }

    @Test
    public void getNewResourceInstance() {
        when(converter.getNewResourceInstance()).thenCallRealMethod();
        final String expectedResourceTypeName = "testResourceTypeName";
        when(converter.getServerResourceType()).thenReturn(expectedResourceTypeName);
        converter.objectFactory = mock(ResourceFactory.class);
        final FolderImpl expectedFolder = new FolderImpl();
        when(converter.objectFactory.newResource(null, expectedResourceTypeName)).thenReturn(expectedFolder);
        final Object folder = converter.getNewResourceInstance();
        assertSame(folder, expectedFolder);
    }

    @Test
    public void getServerResourceType() {
        // correct resource converter for folder resources
        ResourceConverterImpl<Folder, ClientFolder> folderConverter = new ResourceConverterImpl<Folder, ClientFolder>() {
            @Override
            protected Folder resourceSpecificFieldsToServer(ClientFolder clientObject, Folder resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientFolder resourceSpecificFieldsToClient(ClientFolder client, Folder serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        final String extractedResourceName = folderConverter.getServerResourceType();
        assertEquals(extractedResourceName, Folder.class.getName());

        // incorrect resource converter for unknown resource type
        ResourceConverterImpl<?, ?> rawConverter = new ResourceConverterImpl() {
            @Override
            protected Resource resourceSpecificFieldsToServer(ClientResource clientObject, Resource resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
                return resultToUpdate;
            }

            @Override
            protected ClientResource resourceSpecificFieldsToClient(ClientResource client, Resource serverObject, ToClientConversionOptions options) {
                return client;
            }
        };
        IllegalStateException exception = null;
        try {
            rawConverter.getServerResourceType();
        } catch (IllegalStateException ex) {
            exception = ex;
        }
        assertNotNull(exception);
    }
}
