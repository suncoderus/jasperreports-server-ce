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
package com.jaspersoft.jasperserver.jaxrs.resources;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ContentResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.DataTypeImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;
import com.jaspersoft.jasperserver.dto.common.PatchDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientDataType;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientInputControl;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.remote.exception.AccessDeniedException;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.exception.PatchException;
import com.jaspersoft.jasperserver.remote.exception.RemoteException;
import com.jaspersoft.jasperserver.remote.exception.ResourceNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConverter;
import com.jaspersoft.jasperserver.remote.services.SingleRepositoryService;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ResourceDetailsJaxrsServiceTest {
    @InjectMocks
    private ResourceDetailsJaxrsService service = new ResourceDetailsJaxrsService();
    @Mock
    private ResourceConverterProvider resourceConverterProvider;
    @Mock
    private SingleRepositoryService repositoryService;
    @Mock
    private ToServerConverter serverConverter;
    @Mock
    private ToClientConverter clientConverter;
    @Mock
    private Map<String, String> contentTypeMapping;

    final private String uri = "test/folder";
    final private String uri2 = "/test/r";
    final private Resource folder = new FolderImpl();
    final private Resource res = new InputControlImpl();
    final private ClientInputControl clientRes = new ClientInputControl();

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void cleanUp() throws Exception{
        Mockito.reset(repositoryService, resourceConverterProvider, serverConverter, contentTypeMapping);
        when(resourceConverterProvider.getToServerConverter(any(ClientResource.class))).thenReturn(serverConverter);
        when(resourceConverterProvider.getToClientConverter(any(Resource.class))).thenReturn(clientConverter);
        when(resourceConverterProvider.getToClientConverter(any(String.class), any(String.class))).thenReturn(clientConverter);

        when(serverConverter.toServer(eq(clientRes), any(ToServerConversionOptions.class))).thenReturn(res);
        when(clientConverter.toClient(eq(res), any(ToClientConversionOptions.class))).thenReturn(clientRes);

        when(serverConverter.getServerResourceType()).thenReturn("server");
        when(clientConverter.getClientResourceType()).thenReturn("client");

        folder.setURIString("/");
        clientRes.setUri(Folder.SEPARATOR + uri);
        res.setURIString(Folder.SEPARATOR + uri);
        res.setVersion(0);

        clientRes.setLabel("client");
        res.setLabel("server");
    }

    @Test(groups = "GET")
    public void getResourceDetails_xml()  throws Exception{
        final DataType serverObject = new DataTypeImpl();
        final ClientDataType clientDataType = new ClientDataType();
        final String uri = "/test/resource/uri";
        final String clientResourceType = "testClientResourceType";
        when(repositoryService.getResource(eq(uri))).thenReturn(serverObject);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn((ToClientConverter)new ToClientConverter<Resource, ClientResource, ToClientConversionOptions>() {
            @Override
            public ClientResource toClient(Resource serverObject, ToClientConversionOptions options) {
                return clientDataType;
            }

            @Override
            public String getClientResourceType() {
                return clientResourceType;
            }
        });
        final Response response = service.getResourceDetails(uri, MediaType.APPLICATION_XML, null, null);
        assertNotNull(response);
        assertSame(response.getEntity(), clientDataType);
        final List<Object> contentTypeHeaders = response.getMetadata().get(HttpHeaders.CONTENT_TYPE);
        assertNotNull(contentTypeHeaders);
        assertFalse(contentTypeHeaders.isEmpty());
        assertEquals(contentTypeHeaders.get(0), ResourceMediaType.RESOURCE_XML_TEMPLATE.replace(ResourceMediaType.RESOURCE_TYPE_PLACEHOLDER, clientResourceType));
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(groups = "GET")
    public void getResourceDetails_json() throws Exception{
        final DataType serverObject = new DataTypeImpl();
        final ClientDataType clientDataType = new ClientDataType();
        final String uri = "test/resource/uri";
        final String clientResourceType = "testClientResourceType";
        when(repositoryService.getResource(eq(uri))).thenReturn(serverObject);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn((ToClientConverter)new ToClientConverter<Resource, ClientResource, ToClientConversionOptions>() {
            @Override
            public ClientResource toClient(Resource serverObject, ToClientConversionOptions options) {
                return clientDataType;
            }

            @Override
            public String getClientResourceType() {
                return clientResourceType;
            }
        });
        final Response response = service.getResourceDetails(uri, MediaType.APPLICATION_JSON, null, null);
        assertNotNull(response);
        assertSame(response.getEntity(), clientDataType);
        final List<Object> contentTypeHeaders = response.getMetadata().get(HttpHeaders.CONTENT_TYPE);
        assertNotNull(contentTypeHeaders);
        assertFalse(contentTypeHeaders.isEmpty());
        assertEquals(contentTypeHeaders.get(0), ResourceMediaType.RESOURCE_JSON_TEMPLATE.replace(ResourceMediaType.RESOURCE_TYPE_PLACEHOLDER, clientResourceType));
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(expectedExceptions = {ResourceNotFoundException.class})
    public void getResourceDetails_null() throws Exception {
        final String uri = "test/resource/uri";
        when(repositoryService.getResource(Folder.SEPARATOR + uri)).thenThrow(ResourceNotFoundException.class);
        final Response response = service.getResourceDetails(uri, MediaType.APPLICATION_JSON, null, null);
    }

    @Test(groups = "GET")
    public void getResourceDetails_file_json() throws Exception{
        final ContentResource serverObject = new ContentResourceImpl();
        final ClientFile clientFile = new ClientFile();
        final String uri = "test/resource/uri";
        final String clientResourceType = ResourceMediaType.FILE_CLIENT_TYPE;
        when(repositoryService.getResource(eq(uri))).thenReturn(serverObject);
        when(resourceConverterProvider.getToClientConverter(serverObject.getResourceType(), clientResourceType)).thenReturn((ToClientConverter)new ToClientConverter<Resource, ClientResource, ToClientConversionOptions>() {
            @Override
            public ClientResource toClient(Resource serverObject, ToClientConversionOptions options) {
                return clientFile;
            }

            @Override
            public String getClientResourceType() {
                return clientResourceType;
            }
        });
        final Response response = service.getResourceDetails(uri, ResourceMediaType.FILE_JSON, null, null);
        assertNotNull(response);
        assertSame(response.getEntity(), clientFile);
        final List<Object> contentTypeHeaders = response.getMetadata().get(HttpHeaders.CONTENT_TYPE);
        assertNotNull(contentTypeHeaders);
        assertFalse(contentTypeHeaders.isEmpty());
        assertEquals(contentTypeHeaders.get(0), ResourceMediaType.RESOURCE_JSON_TEMPLATE.replace(ResourceMediaType.RESOURCE_TYPE_PLACEHOLDER, clientResourceType));
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(groups = "GET")
    public void getResourceDetails_file_binary() throws Exception{
        final ContentResource serverObject = new ContentResourceImpl();
        final ClientFile clientDataType = new ClientFile();
        final String uri = "test/resource/uri";
        final String clientResourceType = ResourceMediaType.FILE_CLIENT_TYPE;
        serverObject.setName("index.html");

        when(repositoryService.getResource(eq(uri))).thenReturn(serverObject);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn((ToClientConverter)new ToClientConverter<Resource, ClientResource, ToClientConversionOptions>() {
            @Override
            public ClientResource toClient(Resource serverObject, ToClientConversionOptions options) {
                return clientDataType;
            }

            @Override
            public String getClientResourceType() {
                return clientResourceType;
            }
        });
        when(repositoryService.getFileResourceData(serverObject)).thenReturn(new FileResourceData(new byte[10]));

        final Response response = service.getResourceDetails(uri, null, null, null);

        assertNotNull(response);
        final List<Object> contentTypeHeaders = response.getMetadata().get(HttpHeaders.CONTENT_TYPE);
        assertNotNull(contentTypeHeaders);
        assertFalse(contentTypeHeaders.isEmpty());
        assertEquals(contentTypeHeaders.get(0).toString(), MediaType.APPLICATION_OCTET_STREAM);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

    @Test(groups = "DELETE")
    public void testDeleteResource_delegation() throws Exception{
        Response response = service.deleteResource(uri);

        verify(repositoryService).deleteResource(uri);
    }

    @Test(groups = "DELETE")
    public void testDeleteResource_Status() throws Exception{
        Response response = service.deleteResource(uri);

        assertEquals(response.getStatus(), HttpServletResponse.SC_NO_CONTENT);
    }

    @Test(groups = "POST")
    public void testCreateResource_convertsToServer() throws Exception {
        String name = "ups";
        Resource created = new DataTypeImpl();
        Mockito.when(repositoryService.getResource(anyString())).thenReturn(res);
        Mockito.when(repositoryService.createResource(any(Resource.class), anyString(), anyBoolean(), eq(false))).thenReturn(created);
        Mockito.when(repositoryService.getUniqueName(anyString(), anyString())).thenReturn(name);

        service.createResource(clientRes, uri, true, false, null);

        Mockito.verify(serverConverter).toServer(clientRes, ToServerConversionOptions.getDefault().setOwnersUri(uri + Folder.SEPARATOR + name).setResetVersion(true));
        Mockito.verify(clientConverter).toClient(created, null);
    }

    @Test(groups = "POST")
    public void testCreateResource_dryRun() throws Exception {
        String name = "ups";
        Mockito.when(repositoryService.getResource(anyString())).thenReturn(res);
        Mockito.when(repositoryService.getUniqueName(anyString(), anyString())).thenReturn(name);

        service.createResource(clientRes, uri, true, true, null);
        Mockito.verify(repositoryService).createResource(any(Resource.class), anyString(), anyBoolean(), eq(true));
    }

    @Test(groups = "COPY")
    public void testCopyResource_copy() throws Exception {
        Mockito.when(repositoryService.getResource(anyString())).thenReturn(res);
        Response response = service.defaultPostHandler(null,uri + uri, uri,"", "", "", null, true, true, null, false);

        verify(repositoryService).copyResource(uri, uri + uri, true, true, null);
    }

    @Test(groups = "COPY")
    public void testCopyResource_status() throws Exception {
        Mockito.when(repositoryService.getResource(anyString())).thenReturn(res);
        Response response = service.defaultPostHandler(null, uri, uri + uri, "", "", "", null, true, true, null, false);

        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
    }

    @Test(groups = "COPY", expectedExceptions = {ResourceNotFoundException.class}, dependsOnMethods = {"testCopyResource_copy"})
    public void testCopyResource_notFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(repositoryService).copyResource(anyString(), anyString(),
                anyBoolean(), anyBoolean(), isNull(String.class));

        Response response = service.defaultPostHandler(null, uri, uri + uri, "", "", "", "",true, true, null, false);
    }

    @Test(groups = "COPY", expectedExceptions = {AccessDeniedException.class}, dependsOnMethods = {"testCopyResource_copy"})
    public void testCopyResource_accessDenied() throws Exception {
        doThrow(AccessDeniedException.class).when(repositoryService).copyResource(anyString(), anyString(), anyBoolean(),
                anyBoolean(), isNull(String.class));

        Response response = service.defaultPostHandler(null, uri, uri + uri, "", "", "","", true, true, null, false);
    }

    @Test(groups = "MOVE")
    public void testMoveResource_move() throws Exception {
        Mockito.when(repositoryService.getResource(anyString())).thenReturn(res);
        Response response = service.defaultPutHandler(null,Folder.SEPARATOR +  uri + uri, uri, "", null ,"", null, true,
                true, null, false);

        verify(repositoryService).moveResource(uri, Folder.SEPARATOR + uri + uri, true, true, null);
    }

    @Test(groups = "MOVE")
    public void testMoveResource_status() throws Exception {
        Mockito.when(repositoryService.getResource(anyString())).thenReturn(res);
        Response response = service.defaultPutHandler(null, Folder.SEPARATOR + uri, uri + uri, "", null ,"",null, true,
                true, null, false);

        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
    }

    @Test(groups = "MOVE", expectedExceptions = {ResourceNotFoundException.class}, dependsOnMethods = {"testCopyResource_copy"})
    public void testMoveResource_notFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(repositoryService).moveResource(anyString(), anyString(),
                anyBoolean(), anyBoolean(), isNull(String.class));

        Response response = service.defaultPutHandler(null, uri, uri + uri, "", null, "",null, true, true, null, false);
    }

    @Test(groups = "MOVE", expectedExceptions = {AccessDeniedException.class}, dependsOnMethods = {"testCopyResource_copy"})
    public void testMoveResource_accessDenied() throws Exception {
        doThrow(AccessDeniedException.class).when(repositoryService).moveResource(anyString(), anyString(), anyBoolean(),
                anyBoolean(), isNull(String.class));

        Response response = service.defaultPutHandler(null, uri, uri + uri, "", null, "", null,true, true, null, false);
    }

    @Test(groups = "CREATE_RESOURCE_MULTIPART")
    public void createResourceViaForm_fileCreation() throws RemoteException {
        ResourceDetailsJaxrsService serviceMock = mock(ResourceDetailsJaxrsService.class);
        final FormDataMultiPart multiPart = new FormDataMultiPart();
        final String expectedLabel = "testLabel";
        final String expectedDescription = "testDescription";
        final String expectedType = "testType";
        final InputStream expectedData = mock(InputStream.class);
        final FormDataBodyPart bodyPartMock = mock(FormDataBodyPart.class);
        when(bodyPartMock.getEntityAs(InputStream.class)).thenReturn(expectedData);
        when(bodyPartMock.getName()).thenReturn("data");
        multiPart.bodyPart(bodyPartMock);
        multiPart.field("label", expectedLabel);
        multiPart.field("description", expectedDescription);
        multiPart.field("type", expectedType);
        final String expectedMimeType = "someMimeType";
        when(serviceMock.createResourceViaForm(multiPart, uri, Boolean.FALSE, expectedMimeType, false)).thenCallRealMethod();
        when(serviceMock.createFileViaForm(eq(expectedData), eq(uri), eq(expectedLabel), eq(expectedDescription), eq(expectedType), eq(Boolean.FALSE), eq(false))).thenReturn(clientRes);
        final ClientResource resource = serviceMock.createResourceViaForm(multiPart, uri, Boolean.FALSE, expectedMimeType, false);
        assertSame(resource, clientRes);
    }

    @Test(groups = "CREATE_RESOURCE_MULTIPART")
    public void createResourceViaForm_resourceCreation() throws RemoteException {
        final FormDataMultiPart multiPart = new FormDataMultiPart();
        FormDataBodyPart bodyPartMock = mock(FormDataBodyPart.class);
        when(bodyPartMock.getEntityAs(ClientInputControl.class)).thenReturn(clientRes);
        when(bodyPartMock.getName()).thenReturn("resource");
        when(bodyPartMock.getMediaType()).thenReturn(MediaType.valueOf(ResourceMediaType.INPUT_CONTROL_XML));
        multiPart.bodyPart(bodyPartMock);
        bodyPartMock = mock(FormDataBodyPart.class);
        final InputStream expectedPartInputStream = mock(InputStream.class);
        when(bodyPartMock.getEntityAs(InputStream.class)).thenReturn(expectedPartInputStream);
        final String expectedPartName = "testPartName";
        when(bodyPartMock.getName()).thenReturn(expectedPartName);
        multiPart.bodyPart(bodyPartMock);
        when(resourceConverterProvider.getClientTypeClass(ResourceMediaType.INPUT_CONTROL_CLIENT_TYPE)).thenReturn((Class) ClientInputControl.class);
        final ToServerConverter toServerConverter = mock(ToServerConverter.class);
        final ArgumentCaptor<ToServerConversionOptions> toServerConversionOptionsArgumentCaptor = ArgumentCaptor.forClass(ToServerConversionOptions.class);
        when(toServerConverter.toServer(eq(clientRes), toServerConversionOptionsArgumentCaptor.capture())).thenReturn(res);
        when(resourceConverterProvider.getToServerConverter(clientRes)).thenReturn((ToServerConverter) toServerConverter);
        when(repositoryService.createResource(res, uri, Boolean.FALSE, false)).thenReturn(res);
        final ClientResource resource = service.createResourceViaForm(multiPart, uri, Boolean.FALSE, null, false);
        assertSame(resource, clientRes);
        assertNotNull(toServerConversionOptionsArgumentCaptor.getValue());
        Map<String, InputStream> parts = toServerConversionOptionsArgumentCaptor.getValue().getAttachments();
        assertNotNull(parts);
        assertTrue(parts.size() == 1);
        final Map.Entry<String, InputStream> entry = parts.entrySet().iterator().next();
        assertEquals(entry.getKey(), expectedPartName);
        assertSame(entry.getValue(), expectedPartInputStream);
    }

    @Test(groups = "CREATE_RESOURCE_MULTIPART", expectedExceptions = IllegalParameterValueException.class)
    public void createResourceViaForm_invalidResourceMediaType() throws RemoteException {
        final FormDataMultiPart multiPart = new FormDataMultiPart();
        final FormDataBodyPart bodyPartMock = mock(FormDataBodyPart.class);
        when(bodyPartMock.getEntityAs(ClientResource.class)).thenReturn(clientRes);
        when(bodyPartMock.getName()).thenReturn("resource");
        when(bodyPartMock.getMediaType()).thenReturn(MediaType.WILDCARD_TYPE);
        multiPart.bodyPart(bodyPartMock);
        service.createResourceViaForm(multiPart, uri, Boolean.FALSE, null, false);
    }

    @Test(groups = "CREATE_FILE")
    public void extractType_mimeType_caseInsensitive(){
        assertEquals(service.extractType(ClientFile.FileType.accessGrantSchema.getMimeType().toUpperCase(), null),
                ClientFile.FileType.accessGrantSchema.toString());
        assertEquals(service.extractType(ClientFile.FileType.accessGrantSchema.getMimeType().toLowerCase(), null),
                ClientFile.FileType.accessGrantSchema.toString());

    }

    @Test(groups = "CREATE_FILE")
    public void testCreateFile() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);

        Response response = service.defaultPostHandler(stream, Folder.SEPARATOR + uri, null, "attachment; filename="+name,
                description, type, "",true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        verify(repositoryService).createFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF,  true, false);
    }

    @Test(groups = "CREATE_FILE", expectedExceptions = {IllegalParameterValueException.class})
    public void testCreateFile_no_disposition() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri ,name,name,description,type,true, false)).thenReturn(res);

        Response response = service.defaultPostHandler(stream, uri, null, null, type, description,"", true, true, null, false);
    }

    @Test(groups = "CREATE_FILE", expectedExceptions = {IllegalParameterValueException.class})
    public void testCreateFile_illegal_disposition() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);

        Response response = service.defaultPostHandler(stream, uri, null, type, type, description,"", true, true, null, false);
    }

    @Test(groups = "CREATE_FILE", expectedExceptions = {IllegalParameterValueException.class})
    public void testCreateFile_no_file_name() throws Exception {
        String name = "", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);

        Response response = service.defaultPostHandler(stream, uri, null, "attachment; filename="+name, type,"",
                description, true, true, null, false);
    }

    @Test(groups = "CREATE_FILE", expectedExceptions = {IllegalParameterValueException.class})
    public void testCreateFile_extracts_type_from_name() throws Exception {
        String name = "", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);

        Response response = service.defaultPostHandler(stream, uri, null, "attachment; filename="+name, type,"",
                description, true, true, null, false);
    }

    @Test(groups = "CREATE_FILE")
    public void testCreateFile_simple_type() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);

        Response response = service.defaultPostHandler(stream, Folder.SEPARATOR + uri, null, "attachment; filename="+name,
                description, type, null, true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        verify(repositoryService).createFileResource(stream, Folder.SEPARATOR + uri, name,  name,description, ContentResource.TYPE_PDF,  true, false);
    }

    @Test(groups = "CREATE_FILE")
    public void testCreateFile_wildcard_type() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name,  name,description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);

        Response response = service.defaultPostHandler(stream, Folder.SEPARATOR + uri, null, "attachment; filename="+name,
                description, type, null, true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        verify(repositoryService).createFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF,  true, false);
    }

    @Test(groups = "CREATE_FILE")
    public void testCreateFile_extension() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name,  name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);
        when(contentTypeMapping.containsKey("pdf")).thenReturn(true);

        Response response = service.defaultPostHandler(stream, Folder.SEPARATOR + uri, null, "attachment; filename="+name,
                description, "",type, true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        verify(repositoryService).createFileResource(stream, Folder.SEPARATOR + uri, name,  name, description, ContentResource.TYPE_PDF,  true, false);
    }

    @Test(groups = "CREATE_FILE")
    public void testCreateFile_multipart() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name,  name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);

        final ClientResource clientResource = service.createFileViaForm(stream, Folder.SEPARATOR + uri, name, description, type, true, false);
        assertSame(clientResource, clientRes);
        verify(repositoryService).createFileResource(stream, Folder.SEPARATOR + uri, name,  name, description, ContentResource.TYPE_PDF,  true, false);

    }

    @Test(groups = "CREATE_FILE", expectedExceptions = {MandatoryParameterNotFoundException.class})
    public void testCreateFile_multipart_empty_stream() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);
        final ClientResource clientResource = service.createFileViaForm(null, uri, name, description, type, true, false);
        assertSame(clientRes, clientResource);
    }

    @Test(groups = "CREATE_FILE", expectedExceptions = {MandatoryParameterNotFoundException.class})
    public void testCreateFile_multipart_empty_label() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);

        final ClientResource clientResource = service.createFileViaForm(stream, uri, null, description, type, true, false);
        assertSame(clientRes, clientResource);
    }

    @Test(groups = "CREATE_FILE", expectedExceptions = {MandatoryParameterNotFoundException.class})
    public void testCreateFile_multipart_empty_uri() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);

        final ClientResource clientResource = service.createFileViaForm(stream, null, name, description, type, true, false);
        assertSame(clientRes, clientResource);
    }

    @Test(groups = "CREATE_FILE", expectedExceptions = {MandatoryParameterNotFoundException.class})
    public void testCreateFile_multipart_empty_type() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);

        final ClientResource clientResource = service.createFileViaForm(stream, uri, name, description, null, true, false);
        assertSame(clientRes, clientResource);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.updateFileResource(stream, Folder.SEPARATOR + uri, name,name, description, ContentResource.TYPE_PDF, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.defaultPutHandler(stream, Folder.SEPARATOR + uri + Folder.SEPARATOR + name, null,
                null, description, type,null, true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(repositoryService).updateFileResource(stream, Folder.SEPARATOR + uri, name, name,description, ContentResource.TYPE_PDF, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_create() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name,  name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);

        Response response = service.defaultPutHandler(stream, Folder.SEPARATOR + uri + Folder.SEPARATOR + name, null,
                null, description, type,null, true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        verify(repositoryService).createFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF,  true, false);
    }

    @Test(groups = "UPDATE_FILE", expectedExceptions = {IllegalParameterValueException.class})
    public void testUpdateFile_extracts_type_from_name() throws Exception {
        String name = "", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.defaultPutHandler(stream, uri + Folder.SEPARATOR + name, null,  null, description,
                type,null,true, true, null, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_simple_type() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name,  name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.defaultPutHandler(stream,Folder.SEPARATOR +  uri + Folder.SEPARATOR + name, null,
                null, description, type, null,true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(repositoryService).updateFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_wildcard_type() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name,  name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.defaultPutHandler(stream, Folder.SEPARATOR + uri + Folder.SEPARATOR + name, null,
                null, description, type,null, true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(repositoryService).updateFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_extension() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name,  name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);
        when(contentTypeMapping.containsKey("pdf")).thenReturn(true);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.defaultPutHandler(stream, Folder.SEPARATOR + uri + Folder.SEPARATOR + name, null,
                null, description, type,null, true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(repositoryService).updateFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_root() throws Exception {
        String name = "test.pdf", description = "test", type = "application/pdf; charset=UTF-8";
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR, name,  name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);
        when(contentTypeMapping.containsKey("pdf")).thenReturn(true);
        when(repositoryService.getResource(Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.defaultPutHandler(stream, Folder.SEPARATOR + name, null, null, description, type,
                null,true, true, null, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(repositoryService).updateFileResource(stream, Folder.SEPARATOR, name, name, description, ContentResource.TYPE_PDF, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_multipart() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.updateFileViaForm(stream, uri + Folder.SEPARATOR + name, name,  description, type, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(repositoryService).updateFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_multipart_root() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR, name,  name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.updateFileViaForm(stream, name, name,  description, type, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(repositoryService).updateFileResource(stream, Folder.SEPARATOR, name, name, description, ContentResource.TYPE_PDF, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_multipart_create() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF,  true, false)).thenReturn(res);

        Response response = service.updateFileViaForm(stream, uri + Folder.SEPARATOR + name, name,  description, type, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
        verify(repositoryService).createFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF,  true, false);
    }

    @Test(groups = "UPDATE_FILE", expectedExceptions = {MandatoryParameterNotFoundException.class})
    public void testUpdateFile_multipart_empty_stream() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.updateFileViaForm(null, uri + Folder.SEPARATOR + name, name,  description, type, false);
    }

    @Test(groups = "UPDATE_FILE")
    public void testUpdateFile_multipart_empty_label() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.updateFileViaForm(stream, uri + Folder.SEPARATOR + name, null,  description, type, false);

        assertNotNull(response);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(repositoryService).updateFileResource(stream, Folder.SEPARATOR + uri, name, name, description, ContentResource.TYPE_PDF, false);
    }

    @Test(groups = "UPDATE_FILE", expectedExceptions = {MandatoryParameterNotFoundException.class})
    public void testUpdateFile_multipart_empty_uri() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.updateFileViaForm(stream, null, name,  description, type, false);
    }

    @Test(groups = "UPDATE_FILE", expectedExceptions = {MandatoryParameterNotFoundException.class})
    public void testUpdateFile_multipart_empty_type() throws Exception {
        String name = "test.pdf", description = "test", type = ContentResource.TYPE_PDF;
        InputStream stream = new ByteArrayInputStream(new byte[5]);

        when(repositoryService.createFileResource(stream,uri,name,name,description,type,true, false)).thenReturn(res);
        when(repositoryService.getResource(Folder.SEPARATOR + uri + Folder.SEPARATOR + name)).thenReturn(res);

        Response response = service.updateFileViaForm(stream, uri, name,  description, null, false);
    }

    @Test
    public void toResponse_hasNoData(){
        final Response response = service.toResponse(new FileResourceData((byte[]) null), null, null);
        assertEquals(response.getStatus(), 204);
    }
}
