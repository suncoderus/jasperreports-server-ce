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
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.search.SearchCriteriaFactory;
import com.jaspersoft.jasperserver.dto.resources.ClientInputControl;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.remote.exception.*;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import com.jaspersoft.jasperserver.remote.resources.converters.DataTypeResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ResourceConverterProvider;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConverter;
import com.jaspersoft.jasperserver.remote.resources.operation.CopyMoveOperationStrategy;
import com.jaspersoft.jasperserver.war.common.ConfigurationBean;
import org.mockito.*;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.springframework.security.authentication.BadCredentialsException;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.same;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class SingleRepositoryServiceImplTest {
    @InjectMocks
    private SingleRepositoryServiceImpl service;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private Set<String> fileResourceTypes;
    @Mock
    private UriHardModifyProtectionChecker uriHardModifyProtectionChecker;
    @Mock
    private ConfigurationBean configurationBean;
    @Mock
    private ResourceConverterProvider resourceConverterProvider;
    @Mock
    private ToServerConverter toServerConverter;
    @Mock
    private ToClientConverter toClientConverter;
    @Mock
    private SearchCriteriaFactory searchCriteriaFactory;
    @Spy
    protected Map<String, CopyMoveOperationStrategy> copyMoveStrategies = new HashMap<String, CopyMoveOperationStrategy>();


    private ClientInputControl clientObject;

    final private String uri = "/test";
    final private String uri2 = "/test/r";
    final private String orgUri = "/organizations";
    final private Folder folder = new FolderImpl();
    final private Folder rootFolder = new FolderImpl();
    final private Resource resource = new QueryImpl();
    final private Resource resource2 = new QueryImpl();

    @BeforeClass
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = Mockito.spy(service);
        when(configurationBean.getResourceIdNotSupportedSymbols()).thenReturn("\\s");
        when(configurationBean.getOrganizationsFolderUri()).thenReturn(orgUri);
    }

    @BeforeMethod
    public void cleanUp(){
        Mockito.reset(service, repositoryService, uriHardModifyProtectionChecker, toClientConverter, toServerConverter);
        folder.setURIString("/folder");
        folder.setLabel("l");

        rootFolder.setURIString("/");

        resource.setURIString("/test/folder");
        resource.setLabel("Folder");
        resource2.setURIString(uri2 + "/" + resource.getName());
        if(clientObject != null) {
            clientObject.setVersion(Resource.VERSION_NEW);
        }
    }

    @BeforeGroups(groups = "POST")
    public void makePartialPost() throws Exception{
        doReturn(resource).when(service).getResource(anyString());
    }

    @BeforeGroups(groups = "SAVE_OR_UPDATE")
    public void prepareSaveOrUpdate() throws Exception{
        clientObject = new ClientInputControl().setUri(uri);

        when(resourceConverterProvider.getToServerConverter(clientObject)).thenReturn(toServerConverter);
        when(resourceConverterProvider.getToClientConverter(any(DataType.class))).thenReturn((ToClientConverter)new DataTypeResourceConverter());
        when(toServerConverter.getServerResourceType()).thenReturn(InputControl.class.getName());
    }

    @BeforeGroups(groups = "CREATE_FILE")
    public void makePartialCreateFile() throws Exception{
        doAnswer(new ReturnsArgumentAt(0)).when(service).createResource(any(Resource.class), anyString(), anyBoolean(), eq(false));
    }

    @BeforeGroups(groups = "DRY_RUN")
    public void prepareDryRun(){
        clientObject = new ClientInputControl().setUri(uri);
    }

    @Test(groups = "DRY_RUN")
    public void createResource_dryRunTrue_noSaveMethodsCall() throws Exception {
        final Resource result = service.createResource(this.resource, "/some/folder", true, true);
        assertSame(result, this.resource);
        verify(service, never()).ensureFolderUri(anyString());
        verify(repositoryService, never()).saveResource(any(ExecutionContext.class), any(Resource.class));
        verify(service, never()).getResource(anyString());
    }

    @Test(groups = "DRY_RUN")
    public void saveOrUpdate_dryRunIsTrue_anotherResourceType_owerwriteIsTrue_noDelete(){
        doReturn(resource).when(service).getResource(anyString());
        doReturn(null).when(resourceConverterProvider).getToClientConverter(anyString(), anyString());
        doReturn(toServerConverter).when(resourceConverterProvider).getToServerConverter(clientObject);
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(resource);
        final ClientJndiJdbcDataSource expectedResult = new ClientJndiJdbcDataSource();
        doReturn(expectedResult).when(toClientConverter).toClient(same(resource), any(ToClientConversionOptions.class));
        doReturn(resource).when(toServerConverter).toServer(same(clientObject), isNull(Resource.class), any(ToServerConversionOptions.class));
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, true);
        assertSame(result, expectedResult);
        verify(service, never()).deleteResource(anyString());
    }

    @Test(groups = "DRY_RUN")
    public void saveOrUpdate_dryRunIsTrue_versionNotMutch_owerwriteIsTrue_noDelete(){
        doReturn(resource).when(service).getResource(anyString());
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(anyString(), anyString());
        doReturn(toServerConverter).when(resourceConverterProvider).getToServerConverter(clientObject);
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(resource);
        resource.setVersion(10);
        clientObject.setVersion(9);
        final ClientJndiJdbcDataSource expectedResult = new ClientJndiJdbcDataSource();
        doReturn(expectedResult).when(toClientConverter).toClient(same(resource), any(ToClientConversionOptions.class));
        doReturn(resource).when(toServerConverter).toServer(same(clientObject), isNull(Resource.class), any(ToServerConversionOptions.class));
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, true);
        assertSame(result, expectedResult);
        verify(service, never()).deleteResource(anyString());
    }

    @Test(groups = "DELETE", expectedExceptions = {AccessDeniedException.class})
    public void testDeleteResource_modifyProtected() throws Exception{
        final String testUri = "testUri";
        when(uriHardModifyProtectionChecker.isHardModifyProtected(testUri)).thenReturn(true);

        service.deleteResource(testUri);

        Mockito.verify(repositoryService).deleteResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(groups = "SAVE_OR_UPDATE", expectedExceptions = ResourceAlreadyExistsException.class)
    public void saveOrUpdate_anotherResource_noOverwrite_exception() throws Exception {
        reset(service);
        final DataTypeImpl serverObject = new DataTypeImpl();
        doReturn(serverObject).when(service).getResource(uri);
        service.saveOrUpdate(clientObject, false, true, null, false);
    }

    @Test(groups = "SAVE_OR_UPDATE", expectedExceptions = VersionNotMatchException.class)
    public void saveOrUpdate_anotherResource_noOverwrite_exception_version() throws Exception {
        reset(service);

        final InputControl serverObject = new InputControlImpl();
        serverObject.setVersion(5);

        doReturn(serverObject).when(service).getResource(uri);
        doReturn(toClientConverter).when(resourceConverterProvider).getToClientConverter(serverObject.getResourceType(),
                ClientTypeHelper.extractClientType(clientObject.getClass()));

        service.saveOrUpdate(clientObject, false, true, null, false);
    }

    @Test(groups = "SAVE_OR_UPDATE")
    public void saveOrUpdate_anotherResource_overwrite() throws Exception {
        reset(service);
        final DataTypeImpl serverObject = new DataTypeImpl();
        serverObject.setURIString(uri);
        doReturn(serverObject).when(service).getResource(uri);
        doNothing().when(service).deleteResource(uri);
        ArgumentCaptor<ToServerConversionOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(ToServerConversionOptions.class);
        when(toServerConverter.toServer(same(clientObject), isNull(), optionsArgumentCaptor.capture())).thenReturn(serverObject);
        doReturn(serverObject).when(service).createResource(serverObject, serverObject.getParentPath(), true, false);
        final ToClientConverter toClientConverter = mock(ToClientConverter.class);
        when(toClientConverter.getClientResourceType()).thenReturn(ResourceMediaType.DATA_TYPE_CLIENT_TYPE);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn(toClientConverter);
        when(toClientConverter.toClient(same(serverObject), any(ToClientConversionOptions.class))).thenReturn(clientObject);
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, false);
        verify(service).deleteResource(uri);
        assertSame(result, clientObject);
        final ToServerConversionOptions usedOptions = optionsArgumentCaptor.getValue();
        assertNotNull(usedOptions);
        assertEquals(usedOptions.getOwnersUri(), uri);
    }

    @Test(groups = "SAVE_OR_UPDATE")
    public void saveOrUpdate_existingResource_updated() throws Exception {
        reset(service);
        final InputControl serverObject = new InputControlImpl();
        serverObject.setURIString(uri);
        serverObject.setVersion(1);
        clientObject.setVersion(1);
        doReturn(serverObject).when(service).getResource(uri);
        ArgumentCaptor<ToServerConversionOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(ToServerConversionOptions.class);
        when(toServerConverter.toServer(same(clientObject), same(serverObject), optionsArgumentCaptor.capture())).thenReturn(serverObject);
        doReturn(serverObject).when(service).updateResource(serverObject, false);
        final ToClientConverter toClientConverter = mock(ToClientConverter.class);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn(toClientConverter);
        when(toClientConverter.toClient(same(serverObject), any(ToClientConversionOptions.class))).thenReturn(clientObject);
        when(toClientConverter.getClientResourceType()).thenReturn(ResourceMediaType.INPUT_CONTROL_CLIENT_TYPE);
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, false);
        verify(service, never()).deleteResource(any(String.class));
        assertSame(result, clientObject);
        final ToServerConversionOptions usedOptions = optionsArgumentCaptor.getValue();
        assertNotNull(usedOptions);
        assertEquals(usedOptions.getOwnersUri(), uri);
    }

    @Test(groups = "SAVE_OR_UPDATE")
    public void saveOrUpdate_doesntExist_created() throws Exception {
        reset(service);
        final InputControl serverObject = new InputControlImpl();
        serverObject.setURIString(uri);
        doReturn(null).when(service).getResource(uri);
        ArgumentCaptor<ToServerConversionOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(ToServerConversionOptions.class);
        when(toServerConverter.toServer(same(clientObject), isNull(), optionsArgumentCaptor.capture())).thenReturn(serverObject);
        doReturn(serverObject).when(service).createResource(serverObject, serverObject.getParentPath(), true, false);
        final ToClientConverter toClientConverter = mock(ToClientConverter.class);
        when(resourceConverterProvider.getToClientConverter(serverObject)).thenReturn(toClientConverter);
        when(toClientConverter.toClient(same(serverObject), any(ToClientConversionOptions.class))).thenReturn(clientObject);
        final ClientResource result = service.saveOrUpdate(clientObject, true, true, null, false);
        verify(service, never()).deleteResource(any(String.class));
        verify(service, never()).updateResource(any(Resource.class), eq(true));
        assertSame(result, clientObject);
        final ToServerConversionOptions usedOptions = optionsArgumentCaptor.getValue();
        assertNotNull(usedOptions);
        assertEquals(usedOptions.getOwnersUri(), uri);
    }

    @Test(groups = "DELETE", expectedExceptions = {IllegalParameterValueException.class})
    public void testDeleteResource_Null() throws Exception{
        service.deleteResource(null);
    }

    @Test(groups = "DELETE", expectedExceptions = {IllegalParameterValueException.class})
    public void testDeleteResource_Empty() throws Exception{
        service.deleteResource("");
    }

    @Test(groups = "DELETE")
    public void testDeleteResource_Resource() throws Exception{
        Mockito.when(repositoryService.getResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri))).thenReturn(resource);

        service.deleteResource(uri);

        Mockito.verify(repositoryService).deleteResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(groups = "DELETE")
    public void testDeleteResource_Folder() throws Exception{
        Mockito.when(repositoryService.getResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(Mockito.any(ExecutionContext.class), Mockito.eq(uri))).thenReturn(folder);

        service.deleteResource(uri);

        Mockito.verify(repositoryService).deleteFolder(Mockito.any(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(groups = "DELETE", expectedExceptions = {AccessDeniedException.class})
    public void testDeleteResource_Forbidden() throws Exception{
        Mockito.when(repositoryService.getResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(Mockito.any(ExecutionContext.class), Mockito.eq(uri))).thenReturn(folder);
        Mockito.doThrow(new org.springframework.security.access.AccessDeniedException("NO")).when(repositoryService).deleteFolder(Mockito.any(ExecutionContext.class), Mockito.eq(uri));

        service.deleteResource(uri);
    }

    @Test(groups = "DELETE", expectedExceptions = {AccessDeniedException.class})
    public void testDeleteResource_HasDependentResources() throws Exception{
        List<ResourceLookup> dependencies = new LinkedList<ResourceLookup>();
        dependencies.add(new ResourceLookupImpl());
        dependencies.get(0).setURIString(uri);

        Mockito.when(repositoryService.getResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri))).thenReturn(resource);
        Mockito.when(repositoryService.getDependentResources(Mockito.any(ExecutionContext.class), Mockito.anyString(), Mockito.same(searchCriteriaFactory), anyInt(), anyInt())).thenReturn(dependencies);

        service.deleteResource(uri);
    }

    @Test(groups = "DELETE")
    public void testDeleteResource_HasDependentTempResources() throws Exception{
        List<ResourceLookup> dependencies = new LinkedList<ResourceLookup>();
        dependencies.add(new ResourceLookupImpl());
        dependencies.get(0).setURIString("/temp/uri");

        Mockito.when(repositoryService.getResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri))).thenReturn(resource);
        Mockito.when(repositoryService.getDependentResources(Mockito.any(ExecutionContext.class), Mockito.anyString(), Mockito.same(searchCriteriaFactory), anyInt(), anyInt())).thenReturn(dependencies);

        service.deleteResource(uri);

        Mockito.verify(repositoryService).deleteResource(Mockito.any(ExecutionContext.class), Mockito.eq(uri));
    }

    @Test(groups = "POST")
    public void testCreateResource_created() throws Exception{
        Mockito.doReturn(true).when(repositoryService).folderExists(any(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(Mockito.any(ExecutionContext.class), Mockito.eq(resource.getURIString()))).thenReturn(folder);

        Resource response = service.createResource(resource, uri, false, false);

        Mockito.verify(repositoryService).saveResource(any(ExecutionContext.class), any(Resource.class));
    }

    @Test(groups = "POST", expectedExceptions = {ResourceAlreadyExistsException.class})
    public void testCreateResource_oneOfGeneratedIsResource() throws Exception {
        Mockito.when(repositoryService.getResource(Mockito.any(ExecutionContext.class), anyString())).thenReturn(resource);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq("/"))).thenReturn(folder);

        Resource response = service.createResource(resource, uri, true, false);

        assertEquals(response.getName(), resource.getLabel().concat("_1"));
    }

    @Test(groups = "POST")
    public void testCreateResource_generate() throws Exception{
        resource.setName("");
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);
        doReturn(resource).when(service).getResource(anyString());

        Resource response = service.createResource(resource, uri, true, false);

        assertEquals(response.getName(), resource.getLabel());
    }

    @Test(groups = "POST")
    public void testCreateResource_generate_stripSpaces() throws Exception{
        resource.setName("");
        resource.setLabel(" test label ");
        doReturn(resource).when(service).getResource(anyString());
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);

        Resource response = service.createResource(resource, uri, true, false);

        assertEquals(response.getName(), "_test_label_");
    }

    @Test(groups = "POST")
    public void testCreateResource_generateNameIfResourceExists() throws Exception{
        resource.setName("");
        doReturn(resource).when(service).getResource(anyString());
        doReturn("generatedName").when(service).generateName(uri, resource.getLabel());

        Resource response = service.createResource(resource, uri, true, true);

        assertEquals(response.getName(), "generatedName");
    }

    @Test(groups = "POST")
    public void generateName_resourceExistsTwice() throws Exception{
        doReturn(new InputControlImpl()).when(repositoryService).getResource(null, "/test/label");
        doReturn(new InputControlImpl()).when(repositoryService).getResource(null, "/test/label_1");

        final String result = service.generateName("/test", "label");

        assertEquals(result, "label_2");
    }

    @Test(groups = "POST", expectedExceptions = {FolderNotFoundException.class})
    public void testCreateResource_failIfFolderNotExists() throws Exception{
        Mockito.doReturn(true).when(repositoryService).resourceExists(any(ExecutionContext.class), anyString());
        Mockito.doReturn(false).when(repositoryService).folderExists(any(ExecutionContext.class), anyString());

        Resource response = service.createResource(resource, uri, false, false);
    }

    @Test(groups = "POST")
    public void testCreateResource_createNotExistingFolder() throws Exception{
        Mockito.doReturn(false).when(repositoryService).resourceExists(any(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);

        Resource response = service.createResource(resource, uri, true, false);

        Mockito.verify(repositoryService).saveFolder(any(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST", expectedExceptions = {FolderAlreadyExistsException.class})
    public void testCreateResource_createNotExistingFolderUriExist() throws Exception{
        folder.setURIString(uri.toUpperCase());

        Mockito.doReturn(false).when(repositoryService).resourceExists(any(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(uri))).thenReturn(folder);

        Resource response = service.createResource(resource, uri, true, false);

        Mockito.verify(repositoryService).saveFolder(any(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST")
    public void testCreateResource_createNotExistingFolders() throws Exception{
        Mockito.doReturn(false).when(repositoryService).resourceExists(any(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);

        resource.setURIString(resource.getURIString() + "/gfyu");
        Resource response = service.createResource(resource, resource.getURIString(), true, false);

        Mockito.verify(repositoryService, times(3)).saveFolder(any(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST", expectedExceptions = {FolderAlreadyExistsException.class})
    public void testCreateResource_createNotExistingFoldersUriExist() throws Exception {
        folder.setURIString(uri.toUpperCase());

        Mockito.doReturn(false).when(repositoryService).resourceExists(any(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq("/"))).thenReturn(rootFolder);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(uri))).thenReturn(folder);

        resource.setURIString(resource.getURIString() + "/gfyu");
        Resource response = service.createResource(resource, resource.getURIString(), true, false);

        Mockito.verify(repositoryService, times(3)).saveFolder(any(ExecutionContext.class), any(Folder.class));
    }

    // TODO: SSU
    @Test(groups = "POST", expectedExceptions = {AccessDeniedException.class},enabled = false)
    public void testCreateResource_createNotExistingFoldersWithoutPermission() throws Exception{
        Mockito.doReturn(false).when(repositoryService).resourceExists(any(ExecutionContext.class), anyString());
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq("/"))).thenThrow(BadCredentialsException.class);

        resource.setURIString(resource.getURIString() + "/gfyu");
        Resource response = service.createResource(resource, resource.getURIString(), true, false);

        Mockito.verify(repositoryService, times(2)).saveFolder(any(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST")
    public void testCreateResource_createsFolder() throws Exception {
        Mockito.when(repositoryService.folderExists(any(ExecutionContext.class), anyString())).thenReturn(true);

        Resource response = service.createResource(folder, uri, false, false);

        Mockito.verify(repositoryService).saveFolder(any(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "POST")
    public void testCreateResource_forbiddenFolder_folder() throws Exception {
        Mockito.when(repositoryService.folderExists(any(ExecutionContext.class), anyString())).thenReturn(true);
        Mockito.when(uriHardModifyProtectionChecker.isHardModifyProtected(uri)).thenReturn(true);

        Resource response = service.createResource(folder, uri, false, false);

        Mockito.verify(repositoryService).saveFolder(any(ExecutionContext.class), any(Folder.class));
    }

    @Test(groups = "PUT")
    public void testUpdateResource() throws Exception{
        service.updateResource(resource, false);

        verify(repositoryService).saveResource(any(ExecutionContext.class), eq(resource));
    }

    @Test(groups = "PUT")
    public void testUpdateResource_folder() throws Exception{
        service.updateResource(folder, false);

        verify(repositoryService).saveFolder(any(ExecutionContext.class), eq(folder));
    }

    @Test(groups = "PUT")
    public void testUpdateResource_composite_references() throws Exception{
        InputControl inputControl = new InputControlImpl();
        inputControl.setQuery(new ResourceReference(resource.getURIString()));
        inputControl.setURIString(uri);

        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri))).thenReturn(inputControl);

        service.updateResource(inputControl, false);

        assertEquals(inputControl.getQuery().getTargetURI(), resource.getURIString());
        assertFalse(inputControl.getQuery().isLocal());
    }

    @Test(groups = "COPY")
    public void testCopyResource_copy() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);

        service.copyResource(resource.getURIString(), folder.getURIString(), false, false, null);

        verify(repositoryService).copyRenameResource(any(ExecutionContext.class), eq(resource.getURIString()),
                eq(folder.getURIString() + Folder.SEPARATOR + resource.getName()), isNull(String.class));
    }

    @Test(groups = "COPY")
    public void testCopyResource_copy_rename() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);
        final String newName = "newName";
        service.copyResource(resource.getURIString(), folder.getURIString(), false, false, newName);

        verify(repositoryService).copyRenameResource(any(ExecutionContext.class), eq(resource.getURIString()),
                eq(folder.getURIString() + Folder.SEPARATOR + newName), eq(newName));
    }

    @Test(groups = "COPY", expectedExceptions = {IllegalParameterValueException.class})
    public void testCopyResource_sourceNotSet() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri))).thenReturn(resource);
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri2))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), anyString())).thenReturn(folder);

        service.copyResource(null, uri2, false, false, null);
    }

    @Test(groups = "COPY", expectedExceptions = {ResourceNotFoundException.class})
    public void testCopyResource_copyNotExists() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri2))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), anyString())).thenReturn(null);

        service.copyResource(uri, uri2, false, false, null);
    }

    @Test(groups = "COPY", expectedExceptions = {ResourceAlreadyExistsException.class})
    public void testCopyResource_copyDestinationAlreadyExists() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(folder.getURIString() + Folder.SEPARATOR + resource.getName()))).thenReturn(resource2);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);

        service.copyResource(resource.getURIString(), folder.getURIString(), false, false, null);
    }

    @Test(groups = "COPY")
    public void testCopyResource_copyDestinationAlreadyExistsOverwrite() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(folder.getURIString() + Folder.SEPARATOR + resource.getName()))).thenReturn(resource2);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);

        service.copyResource(resource.getURIString(), folder.getURIString(), false, true, null);

        verify(repositoryService).deleteResource(any(ExecutionContext.class), eq(folder.getURIString() + Folder.SEPARATOR + resource.getName()));
    }

    @Test(groups = "COPY")
    public void testCopyResource_copyFolder() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(uri))).thenReturn(folder);

        final String newName = "newName";
        service.copyResource(folder.getURIString(), uri, false, false, newName);

        Mockito.verify(repositoryService).copyRenameFolder(any(ExecutionContext.class), eq(folder.getURIString()),
                eq(folder.getURIString() + Folder.SEPARATOR + newName), eq(newName));
    }

    @Test(groups = "COPY", expectedExceptions = {AccessDeniedException.class})
    public void testCopyResource_copyForbiddenFolder() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri2))).thenReturn(folder);
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), anyString())).thenReturn(folder);
        Mockito.when(uriHardModifyProtectionChecker.isHardModifyProtected(uri2)).thenReturn(true);

        service.copyResource(uri2, uri, false, false, null);
    }

    @Test(groups = "MOVE", dependsOnGroups = {"COPY"})
    public void testMoveResource_move() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(resource.getURIString()))).thenReturn(resource);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);

        service.moveResource(resource.getURIString(), folder.getURIString(), false, false, null);

        verify(repositoryService).moveResource(any(ExecutionContext.class), eq(resource.getURIString()), eq(folder.getURIString()));
    }

    @Test(groups = "MOVE", dependsOnGroups = {"COPY"})
    public void testMoveResource_moveFolder() throws Exception{
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(folder.getURIString()))).thenReturn(folder);
        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(uri))).thenReturn(null);
        Mockito.when(repositoryService.getFolder(any(ExecutionContext.class), eq(uri))).thenReturn(folder);

        service.moveResource(folder.getURIString(), uri, false, false, null);

        Mockito.verify(repositoryService).moveFolder(any(ExecutionContext.class), eq(folder.getURIString()), eq(folder.getURIString()));
    }

    @Test(groups = "GET")
    public void testGetFileResourceData_file() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        FileResource file = new FileResourceImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResourceData(any(ExecutionContext.class), anyString())).thenReturn(data);
        Mockito.when(repositoryService.getContentResourceData(any(ExecutionContext.class), anyString())).thenReturn(data);

        assertSame(service.getFileResourceData(file), data);
        verify(repositoryService, only()).getResourceData(any(ExecutionContext.class), eq(file.getURIString()));
        verify(repositoryService, never()).getContentResourceData(any(ExecutionContext.class), anyString());
    }

    @Test(groups = "GET")
    public void testGetFileResourceData_contentResource() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        ContentResource file = new ContentResourceImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResourceData(any(ExecutionContext.class), anyString())).thenReturn(data);
        Mockito.when(repositoryService.getContentResourceData(any(ExecutionContext.class), anyString())).thenReturn(data);

        assertSame(service.getFileResourceData(file), data);
        verify(repositoryService, never()).getResourceData(any(ExecutionContext.class), anyString());
        verify(repositoryService, only()).getContentResourceData(any(ExecutionContext.class), eq(file.getURIString()));
    }

    @Test(groups = "GET", expectedExceptions = {IllegalStateException.class})
    public void testGetFileResourceData_not_a_file() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        Resource file = new ResourceLookupImpl();
        file.setURIString("/test/index.html");

        service.getFileResourceData(file);
    }

    @Test(groups = "GET")
    public void testGetFileResourceData_file_uri() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        FileResource file = new FileResourceImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), anyString())).thenReturn(file);
        Mockito.when(repositoryService.getResourceData(any(ExecutionContext.class), anyString())).thenReturn(data);
        Mockito.when(repositoryService.getContentResourceData(any(ExecutionContext.class), anyString())).thenReturn(data);

        assertSame(service.getFileResourceData(file.getURIString()), data);
    }

    @Test(groups = "GET")
    public void testGetFileResourceData_contentResource_uri() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        ContentResource file = new ContentResourceImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), anyString())).thenReturn(file);
        Mockito.when(repositoryService.getResourceData(any(ExecutionContext.class), anyString())).thenReturn(data);
        Mockito.when(repositoryService.getContentResourceData(any(ExecutionContext.class), anyString())).thenReturn(data);

        assertSame(service.getFileResourceData(file.getURIString()), data);
    }

    @Test(groups = "GET", expectedExceptions = {IllegalStateException.class})
    public void testGetFileResourceData_not_a_file_uri() throws Exception {
         Resource file = new ResourceLookupImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(file.getURIString()))).thenReturn(file);

        service.getFileResourceData(file.getURIString());
    }

    @Test(groups = "GET", expectedExceptions = {IllegalStateException.class})
    public void testGetFileResourceData_illegal_uri() throws Exception {
        FileResourceData data = new FileResourceData(new byte[10]);
        Resource file = new ResourceLookupImpl();
        file.setURIString("/test/index.html");

        Mockito.when(repositoryService.getResource(any(ExecutionContext.class), eq(file.getURIString()))).thenReturn(null);

        service.getFileResourceData(file.getURIString());
    }

    @Test(groups = "CREATE_FILE", dependsOnGroups = {"POST"})
    public void testCreateFileResource_file() throws Exception {
        byte[] arr = new byte[10];
        for (byte i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        ByteArrayInputStream baos = new ByteArrayInputStream(arr);
        String name = "name";
        String description = "description";

        when(fileResourceTypes.contains(anyString())).thenReturn(true);
        doAnswer(new ReturnsArgumentAt(0)).when(service).createResource(any(Resource.class), anyString(), anyBoolean(), anyBoolean());

        Resource response = service.createFileResource(baos, uri, name, name, description, null, false, false);
        if (response instanceof FileResource){
            assertEquals(response.getLabel(), name);
            assertEquals(response.getDescription(), description);

            byte[] fileData = ((FileResource)response).getData();
            assertEquals(fileData.length, arr.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(fileData[i], arr[i]);
            }
        } else {
            fail("Expected FileResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "CREATE_FILE", dependsOnGroups = {"POST"})
    public void testCreateFileResource_contentResource() throws Exception {
        byte[] arr = new byte[10];
        for (byte i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        ByteArrayInputStream baos = new ByteArrayInputStream(arr);
        String name = "name";
        String description = "description";

        when(fileResourceTypes.contains(anyString())).thenReturn(false);
        doAnswer(new ReturnsArgumentAt(0)).when(service).createResource(any(Resource.class), anyString(), anyBoolean(), anyBoolean());

        Resource response = service.createFileResource(baos, uri, name, name, description, null, false, false);
        if (response instanceof ContentResource){
            assertEquals(response.getLabel(), name);
            assertEquals(response.getDescription(), description);

            byte[] fileData = ((ContentResource)response).getData();
            assertEquals(fileData.length, arr.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(fileData[i], arr[i]);
            }
        } else {
            fail("Expected ContentResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "UPDATE_FILE", dependsOnGroups = {"PUT"})
    public void testUpdateFileResource_file() throws Exception {
        byte[] arr = new byte[10];
        for (byte i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        ByteArrayInputStream baos = new ByteArrayInputStream(arr);
        String name = "name";
        String label = "label";
        String description = "description";
        FileResource contentResource = new FileResourceImpl();

        contentResource.setName(name);
        contentResource.setParentFolder(uri);

        when(fileResourceTypes.contains(anyString())).thenReturn(false);
        when(repositoryService.getResource(any(ExecutionContext.class), eq(uri + Folder.SEPARATOR + name))).thenReturn(contentResource);

        Resource response = service.updateFileResource(baos, uri, name, label, description, null, false);
        if (response instanceof FileResource){
            assertEquals(response.getLabel(), label);
            assertEquals(response.getDescription(), description);

            byte[] fileData = ((FileResource)response).getData();
            assertEquals(fileData.length, arr.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(fileData[i], arr[i]);
            }
        } else {
            fail("Expected ContentResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "UPDATE_FILE", dependsOnGroups = {"PUT"})
    public void testUpdateFileResource_file_no_label() throws Exception {
         ByteArrayInputStream baos = new ByteArrayInputStream(new byte[10]);
        String name = "name";
        String description = "description";
        FileResource contentResource = new FileResourceImpl();

        contentResource.setName(name);
        contentResource.setParentFolder(uri);

        when(fileResourceTypes.contains(anyString())).thenReturn(false);
        when(repositoryService.getResource(any(ExecutionContext.class), eq(uri + Folder.SEPARATOR + name))).thenReturn(contentResource);

        Resource response = service.updateFileResource(baos, uri, name, name, description, null, false);
        if (response instanceof FileResource){
            assertEquals(response.getLabel(), name);
            assertEquals(response.getDescription(), description);
        } else {
            fail("Expected ContentResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "UPDATE_FILE", dependsOnGroups = {"PUT"})
    public void testUpdateFileResource_contentResource() throws Exception {
        byte[] arr = new byte[10];
        for (byte i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        ByteArrayInputStream baos = new ByteArrayInputStream(arr);
        String name = "name";
        String label = "label";
        String description = "description";
        ContentResource contentResource = new ContentResourceImpl();

        contentResource.setName(name);
        contentResource.setParentFolder(uri);

        when(fileResourceTypes.contains(anyString())).thenReturn(false);
        when(repositoryService.getResource(any(ExecutionContext.class), eq(uri + Folder.SEPARATOR + name))).thenReturn(contentResource);

        Resource response = service.updateFileResource(baos, uri, name, label, description, null, false);
        if (response instanceof ContentResource){
            assertEquals(response.getLabel(), label);
            assertEquals(response.getDescription(), description);

            byte[] fileData = ((ContentResource)response).getData();
            assertEquals(fileData.length, arr.length);
            for (int i = 0; i < arr.length; i++) {
                assertEquals(fileData[i], arr[i]);
            }
        } else {
            fail("Expected ContentResource, got "+response.getClass().getSimpleName());
        }
    }

    @Test(groups = "UPDATE_FILE", dependsOnGroups = {"PUT"}, expectedExceptions = NotAFileException.class)
    public void testUpdateFileResource_not_a_file() throws Exception {
        ByteArrayInputStream baos = new ByteArrayInputStream(new byte[5]);
        String name = "name";
        String label = "label";
        String description = "description";

        when(repositoryService.getResource(any(ExecutionContext.class), eq(uri + Folder.SEPARATOR + name))).thenReturn(resource);

        Resource response = service.updateFileResource(baos, uri, name, label, description, null, false);

    }
}
