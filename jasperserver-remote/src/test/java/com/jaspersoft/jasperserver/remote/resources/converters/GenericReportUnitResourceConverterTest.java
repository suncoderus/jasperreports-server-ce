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

import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.ReportUnitImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableInputControl;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableQuery;
import com.jaspersoft.jasperserver.dto.resources.ClientReportUnit;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class GenericReportUnitResourceConverterTest {
    @InjectMocks
    private GenericReportUnitResourceConverter converter = new GenericReportUnitResourceConverter();
    @Mock
    private ResourceReferenceConverterProvider resourceReferenceConverterProvider;
    @Mock
    private ResourceReferenceConverter<ClientReferenceableQuery> queryResourceReferenceConverter;
    @Mock
    private ResourceReferenceConverter<ClientReferenceableInputControl> inputControlResourceReferenceConverter;
    @Mock
    private ResourceReferenceConverter<ClientReferenceableFile> fileResourceReferenceConverter;
    @Mock
    private ResourceFactory objectFactory;

    private ArgumentCaptor<ClientReferenceRestriction> restrictionArgumentCaptor = ArgumentCaptor.forClass(ClientReferenceRestriction.class);

    private ToClientConversionOptions options;

    @BeforeClass
    public void initConverter() {
        MockitoAnnotations.initMocks(this);
        when(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableQuery.class)).thenReturn(queryResourceReferenceConverter);
        when(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableInputControl.class)).thenReturn(inputControlResourceReferenceConverter);
        when(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class)).thenReturn(fileResourceReferenceConverter);
    }

    @BeforeMethod
    public void resetProviderMock() {
        reset(queryResourceReferenceConverter, fileResourceReferenceConverter, inputControlResourceReferenceConverter);
        when(fileResourceReferenceConverter.addReferenceRestriction(any(ClientReferenceRestriction.class))).thenReturn(fileResourceReferenceConverter);
        options = ToClientConversionOptions.getDefault();
    }

    @Test
    public void setDataSourceToResource() {
        final ReportUnit reportUnit = new ReportUnitImpl();
        final ResourceReference resourceReference = new ResourceReference("/test/resource/uri");
        converter.setDataSourceToResource(resourceReference, reportUnit);
        assertSame(reportUnit.getDataSource(), resourceReference);
    }

    @Test
    public void getDataSourceFromResource() {
        final ReportUnit reportUnit = new ReportUnitImpl();
        final ResourceReference resourceReference = new ResourceReference("/test/resource/uri");
        reportUnit.setDataSource(resourceReference);
        final ResourceReference result = converter.getDataSourceFromResource(reportUnit);
        assertSame(result, resourceReference);
    }

    @Test
    public void resourceSpecificFieldsToServer_simpleFields() throws Exception {
        final ClientReportUnit clientObject = new ClientReportUnit();
        final ReportUnit serverObject = new ReportUnitImpl();
        final boolean alwaysPromptControls = true;
        final Long dataSnapshotId = Long.valueOf(12345);
        final String inputControlRenderingView = "testInputControlRenderingView";
        final String reportRenderingView = "testReportRenderingView";
        clientObject.setAlwaysPromptControls(alwaysPromptControls);
        clientObject.setControlsLayout(ClientReportUnit.ControlsLayoutType.separatePage);
        clientObject.setInputControlRenderingView(inputControlRenderingView);
        clientObject.setReportRenderingView(reportRenderingView);
        final ReportUnit result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertEquals(result.isAlwaysPromptControls(), alwaysPromptControls);
        assertEquals(result.getControlsLayout(), ReportUnit.LAYOUT_SEPARATE_PAGE);
        assertEquals(result.getInputControlRenderingView(), inputControlRenderingView);
        assertEquals(result.getReportRenderingView(), reportRenderingView);
    }

    @Test
    public void resourceSpecificFieldsToClient_simpleFields() throws Exception {
        final ClientReportUnit clientObject = new ClientReportUnit();
        final ReportUnit serverObject = new ReportUnitImpl();
        final boolean alwaysPromptControls = true;
        final Long dataSnapshotId = Long.valueOf(12345);
        final String inputControlRenderingView = "testInputControlRenderingView";
        final String reportRenderingView = "testReportRenderingView";
        serverObject.setAlwaysPromptControls(alwaysPromptControls);
        serverObject.setControlsLayout(ReportUnit.LAYOUT_SEPARATE_PAGE);
        serverObject.setDataSnapshotId(dataSnapshotId);
        serverObject.setInputControlRenderingView(inputControlRenderingView);
        serverObject.setReportRenderingView(reportRenderingView);
        final ClientReportUnit result = (ClientReportUnit) converter.resourceSpecificFieldsToClient(clientObject, serverObject, options);
        assertSame(result, clientObject);
        assertEquals(result.isAlwaysPromptControls(), alwaysPromptControls);
        assertEquals(result.getControlsLayout(), ClientReportUnit.ControlsLayoutType.separatePage);
        assertEquals(result.getInputControlRenderingView(), inputControlRenderingView);
        assertEquals(result.getReportRenderingView(), reportRenderingView);
    }

    @Test
    public void resourceSpecificFieldsToClient_queryReference() {
        final ClientReportUnit clientObject = new ClientReportUnit();
        final ReportUnit serverObject = new ReportUnitImpl();
        final String queryReferenceUri = "/query/reference/uri";
        final ResourceReference queryReference = new ResourceReference(queryReferenceUri);
        final ClientReference expectedClientReference = new ClientReference(queryReferenceUri);
        when(queryResourceReferenceConverter.toClient(queryReference, options)).thenReturn(expectedClientReference);
        serverObject.setQuery(queryReference);
        final ClientReportUnit result = (ClientReportUnit) converter.resourceSpecificFieldsToClient(clientObject, serverObject, options);
        assertSame(result, clientObject);
        final ClientReferenceableQuery query = result.getQuery();
        assertSame(query, expectedClientReference);
        assertEquals(query.getUri(), queryReferenceUri);
    }

    @Test
    public void resourceSpecificFieldsToServer_queryReference() throws Exception {
        final ClientReportUnit clientObject = new ClientReportUnit();
        final ReportUnit serverObject = new ReportUnitImpl();
        final String queryReferenceUri = "/query/reference/uri";
        final ResourceReference queryReference = new ResourceReference(queryReferenceUri);
        final ClientReference clientReference = new ClientReference(queryReferenceUri);
        clientObject.setQuery(clientReference);
        when(queryResourceReferenceConverter.toServer(eq(clientReference),any(ResourceReference.class), any(ToServerConversionOptions.class))).thenReturn(queryReference);
        final ReportUnit result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertSame(result.getQuery(), queryReference);
    }

    @Test
    public void resourceSpecificFieldsToClient_jrxmlReference() throws Exception {
        final ClientReportUnit clientObject = new ClientReportUnit();
        final ReportUnit serverObject = new ReportUnitImpl();
        final String fileReferenceUri = "/jrxml/reference/uri";
        final ResourceReference fileReference = new ResourceReference(fileReferenceUri);
        final ClientReference expectedClientReference = new ClientReference(fileReferenceUri);
        when(fileResourceReferenceConverter.toClient(fileReference, options)).thenReturn(expectedClientReference);
        serverObject.setMainReport(fileReference);
        final ClientReportUnit result = (ClientReportUnit) converter.resourceSpecificFieldsToClient(clientObject, serverObject, options);
        assertSame(result, clientObject);
        final ClientReferenceableFile file = result.getJrxml();
        assertSame(file, expectedClientReference);
        assertEquals(file.getUri(), fileReferenceUri);
    }

    @Test
    public void resourceSpecificFieldsToServer_jrxmlReference() throws Exception {
        final ClientReportUnit clientObject = new ClientReportUnit();
        final ReportUnit serverObject = new ReportUnitImpl();
        final String fileReferenceUri = "/jrxml/reference/uri";
        final ResourceReference fileReference = new ResourceReference(fileReferenceUri);
        final ClientReference clientReference = new ClientReference(fileReferenceUri);
        when(fileResourceReferenceConverter.toServer(eq(clientReference),any(ResourceReference.class), any(ToServerConversionOptions.class))).thenReturn(fileReference);
        clientObject.setJrxml(clientReference);
        final ReportUnit result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        final ResourceReference resultReference = result.getMainReport();
        assertSame(resultReference, fileReference);
        // check for additional restriction is added. References on JRXML files only allowed
        verify(fileResourceReferenceConverter).addReferenceRestriction(restrictionArgumentCaptor.capture());
        final List<ClientReferenceRestriction> restrictions = restrictionArgumentCaptor.getAllValues();
        assertNotNull(restrictions);
        assertEquals(restrictions.size(), 1);
        final ClientReferenceRestriction restriction = restrictions.get(0);
        assertTrue(restriction instanceof ResourceReferenceConverter.FileTypeRestriction);
        final ClientFile fileToTest = new ClientFile();
        fileToTest.setType(ClientFile.FileType.jrxml);
        // no exception should be here. It means restriction is set to accept JRXML
        restriction.validateReference(fileToTest);
    }

    @Test
    public void resourceSpecificFieldsToServer_inputControls() throws Exception {
        final ClientReportUnit clientObject = new ClientReportUnit();
        final ReportUnit serverObject = new ReportUnitImpl();
        final List<ClientReferenceableInputControl> inputControls = new ArrayList<ClientReferenceableInputControl>();
        inputControls.add(new ClientReference());
        inputControls.add(new ClientReference());
        inputControls.add(new ClientReference());
        inputControls.add(new ClientReference());
        inputControls.add(new ClientReference());
        clientObject.setInputControls(inputControls);
        final ResourceReference expectedReference = new ResourceReference("");
        when(inputControlResourceReferenceConverter.toServer(any(ClientReference.class), any(ResourceReference.class),any(ToServerConversionOptions.class))).thenReturn(expectedReference);
        final ReportUnit result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertNotNull(result);
        final List<ResourceReference> serverInputControls = result.getInputControls();
        assertNotNull(serverInputControls);
        assertEquals(serverInputControls.size(), inputControls.size());
        for (ResourceReference reference : serverInputControls) {
            assertSame(reference, expectedReference);
        }
        ArgumentCaptor<ClientReference> clientReferenceArgumentCaptor = ArgumentCaptor.forClass(ClientReference.class);
        verify(inputControlResourceReferenceConverter, times(inputControls.size())).toServer(clientReferenceArgumentCaptor.capture(), any(ResourceReference.class), any(ToServerConversionOptions.class));
        final List<ClientReference> allValues = clientReferenceArgumentCaptor.getAllValues();
        assertNotNull(allValues);
        assertEquals(allValues.size(), inputControls.size());
        assertTrue(allValues.containsAll(inputControls));
    }

    @Test
    public void resourceSpecificFieldsToClient_inputControls() throws Exception {
        final ClientReportUnit clientObject = new ClientReportUnit();
        final ReportUnit serverObject = new ReportUnitImpl();
        final List<ResourceReference> serverInputControlReferences = new ArrayList<ResourceReference>();
        serverInputControlReferences.add(new ResourceReference(""));
        serverInputControlReferences.add(new ResourceReference(""));
        serverInputControlReferences.add(new ResourceReference(""));
        serverInputControlReferences.add(new ResourceReference(""));
        serverInputControlReferences.add(new ResourceReference(""));
        serverObject.setInputControls(serverInputControlReferences);
        final ClientReference expectedReference = new ClientReference();
        when(inputControlResourceReferenceConverter.toClient(any(ResourceReference.class), eq(options))).thenReturn(expectedReference);
        final ClientReportUnit result = (ClientReportUnit) converter.resourceSpecificFieldsToClient(clientObject, serverObject, options);
        assertNotNull(result);
        final List<ClientReferenceableInputControl> clientInputControls = result.getInputControls();
        assertNotNull(clientInputControls);
        assertEquals(clientInputControls.size(), serverInputControlReferences.size());
        for (ClientReferenceableInputControl inputControlReference : clientInputControls) {
            assertSame(inputControlReference, expectedReference);
        }
        ArgumentCaptor<ResourceReference> resourceReferenceArgumentCaptor = ArgumentCaptor.forClass(ResourceReference.class);
        verify(inputControlResourceReferenceConverter, times(serverInputControlReferences.size()))
                .toClient(resourceReferenceArgumentCaptor.capture(), eq(options));
        final List<ResourceReference> resourceReferences = resourceReferenceArgumentCaptor.getAllValues();
        assertNotNull(resourceReferences);
        assertEquals(resourceReferences.size(), serverInputControlReferences.size());
        assertTrue(resourceReferences.containsAll(serverInputControlReferences));
    }

    @Test
    public void resourceSpecificFieldsToServer_convertResourcesToServerIsCalled() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final GenericReportUnitResourceConverter converterPartialMock = mock(GenericReportUnitResourceConverter.class);
        converterPartialMock.resourceReferenceConverterProvider = resourceReferenceConverterProvider;
        final ClientReportUnit clientObject = new ClientReportUnit();
        Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        List<ResourceReference> serverResources = new ArrayList<ResourceReference>();
        clientObject.setFiles(clientResources);
        final ReportUnit serverObject = new ReportUnitImpl();
        when(converterPartialMock.resourceSpecificFieldsToServer(clientObject, serverObject, null)).thenCallRealMethod();
        when(converterPartialMock.convertResourcesToServer(clientResources, serverResources, null)).thenReturn(serverResources);
        final ReportUnit result = converterPartialMock.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertSame(result.getResources(), serverResources);
    }

    @Test
    public void resourceSpecificFieldsToClient_convertResourcesToClientIsCalled() throws IllegalParameterValueException {
        final GenericReportUnitResourceConverter converterPartialMock = mock(GenericReportUnitResourceConverter.class);
        converterPartialMock.resourceReferenceConverterProvider = resourceReferenceConverterProvider;
        final ClientReportUnit clientObject = new ClientReportUnit();
        Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        List<ResourceReference> serverResources = new ArrayList<ResourceReference>();
        final ReportUnit serverObject = new ReportUnitImpl();
        serverObject.setResources(serverResources);
        when(converterPartialMock.resourceSpecificFieldsToClient(clientObject, serverObject, options)).thenCallRealMethod();
        when(converterPartialMock.convertResourcesToClient(serverResources, options)).thenReturn(clientResources);
        final ClientReportUnit result = (ClientReportUnit) converterPartialMock.resourceSpecificFieldsToClient(clientObject, serverObject, options);
        assertSame(result, clientObject);
        assertSame(result.getFiles(), clientResources);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void convertResourcesToClient_localResourceOfWrongType_exception() {
        final ArrayList<ResourceReference> serverResources = new ArrayList<ResourceReference>();
        ResourceReference reference = new ResourceReference(new QueryImpl());
        serverResources.add(reference);
        converter.convertResourcesToClient(serverResources, options);
    }

    @Test
    public void convertResourcesToClient_localFile() {
        final List<ResourceReference> serverResources = new ArrayList<ResourceReference>();
        final FileResource localFile = new FileResourceImpl();
        final ClientFile expectedClientFile = new ClientFile();
        when(fileResourceReferenceConverter.toClient(any(ResourceReference.class), eq(options))).thenReturn(expectedClientFile);
        final String expectedFileName = "testFileName";
        localFile.setName(expectedFileName);
        ResourceReference localResourceReference = new ResourceReference(localFile);
        serverResources.add(localResourceReference);
        final Map<String, ClientReferenceableFile> clientReportUnitResources = converter.convertResourcesToClient(serverResources, options);
        assertNotNull(clientReportUnitResources);
        assertEquals(clientReportUnitResources.size(), 1);
        assertSame(clientReportUnitResources.get(expectedFileName), expectedClientFile);
    }

    @Test
    public void convertResourcesToClient_localFileReference() {
        final List<ResourceReference> serverResources = new ArrayList<ResourceReference>();
        final FileResourceImpl localResource = new FileResourceImpl();
        final String referenceURI = "/test/resource/reference";
        localResource.setReferenceURI(referenceURI);
        final String expectedResourceName = "expectedResourceName";
        localResource.setName(expectedResourceName);
        serverResources.add(new ResourceReference(localResource));
        final ClientReference expectedClientReference = new ClientReference();
        when(fileResourceReferenceConverter.toClient(any(ResourceReference.class), any(ToClientConversionOptions.class))).thenReturn(expectedClientReference);
        final Map<String, ClientReferenceableFile> result = converter.convertResourcesToClient(serverResources, options);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertSame(result.get(expectedResourceName), expectedClientReference);
        final ArgumentCaptor<ResourceReference> resourceReferenceArgumentCaptor = ArgumentCaptor.forClass(ResourceReference.class);
        verify(fileResourceReferenceConverter, times(1)).toClient(resourceReferenceArgumentCaptor.capture(), eq(options));
        final ResourceReference value = resourceReferenceArgumentCaptor.getValue();
        assertNotNull(value);
        assertEquals(value.getReferenceURI(), referenceURI);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void getResourceNameForReference_invalidUri(){
        converter.getResourceNameForReference(new ResourceReference("invalidName"));
    }

    @Test()
    public void getResourceNameForReference(){
        final String expectedResourceName = "testName";
        final String resourceNameForReference = converter.getResourceNameForReference(new ResourceReference("/test/uri/prefix/" + expectedResourceName));
        assertEquals(resourceNameForReference, expectedResourceName);
    }

    @Test
    public void convertResourcesToClient_resourceReference() {
        final List<ResourceReference> serverResources = new ArrayList<ResourceReference>();
        final String expectedResourceName = "testName";
        final String referenceURI = "/test/reference/uri/" + expectedResourceName;
        final ClientReference expectedClientReference = new ClientReference();
        final ResourceReference serverReference = new ResourceReference(referenceURI);
        serverResources.add(serverReference);
        when(fileResourceReferenceConverter.toClient(serverReference, options)).thenReturn(expectedClientReference);
        final Map<String, ClientReferenceableFile> result = converter.convertResourcesToClient(serverResources, options);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertSame(result.get(expectedResourceName), expectedClientReference);
        // check for reference with invalid URI
        final String invalidUri = "invalidUri";
        serverReference.setReference(invalidUri);
        IllegalStateException exception = null;
        try {
            converter.convertResourcesToClient(serverResources, options);
        } catch (IllegalStateException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    @Test
    public void convertResourcesToClient_nullProducesNull() {
        assertNull(converter.convertResourcesToClient(null, options));
    }

    @Test
    public void convertResourcesToClient_emptyProducesNull() {
        assertNull(converter.convertResourcesToClient(new ArrayList<ResourceReference>(), options));
    }

    @Test
    public void convertResourcesToServer_nullsProducesNull() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        assertNull(converter.convertResourcesToServer(null, null, null));
    }

    @Test
    public void convertResourcesToServer_nullAndEmptyListProducesSameEmptyList() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final ArrayList<ResourceReference> expectedServerReferences = new ArrayList<ResourceReference>();
        final List<ResourceReference> result = converter.convertResourcesToServer(null, expectedServerReferences, null);
        assertSame(result, expectedServerReferences);
        assertTrue(result.isEmpty());
    }

    @Test
    public void convertResourcesToServer_nullAndNonEmptyListProducesEmptyList() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final ArrayList<ResourceReference> expectedServerReferences = new ArrayList<ResourceReference>();
        expectedServerReferences.add(new ResourceReference(""));
        expectedServerReferences.add(new ResourceReference(""));
        expectedServerReferences.add(new ResourceReference(""));
        final List<ResourceReference> result = converter.convertResourcesToServer(null, expectedServerReferences, null);
        assertSame(result, expectedServerReferences);
        assertTrue(result.isEmpty());
    }

    @Test
    public void convertResourcesToServer_EmptyMapAndNonEmptyListProducesEmptyList() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final ArrayList<ResourceReference> expectedServerReferences = new ArrayList<ResourceReference>();
        expectedServerReferences.add(new ResourceReference(""));
        expectedServerReferences.add(new ResourceReference(""));
        expectedServerReferences.add(new ResourceReference(""));
        final List<ResourceReference> result = converter.convertResourcesToServer(new HashMap<String, ClientReferenceableFile>(), expectedServerReferences, null);
        assertSame(result, expectedServerReferences);
        assertTrue(result.isEmpty());
    }

    @Test
    public void convertResourcesToServer_nonEmptyMapAndNullProducesNonEmptyList() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        final String expectedName = "expectedName";
        final ClientFile clientFile = new ClientFile();
        clientResources.put(expectedName, clientFile);
        final FileResource serverFileResource = new FileResourceImpl();
        final ResourceReference expectedReference = new ResourceReference(serverFileResource);
        when(fileResourceReferenceConverter.toServer(clientFile, null, null)).thenReturn(expectedReference);
        final List<ResourceReference> result = converter.convertResourcesToServer(clientResources, null, null);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertSame(result.get(0), expectedReference);
    }

    @Test
    public void convertResourcesToServer_localFile_referenceToAnotherFile_localFileRemoved()
            throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        final Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        final String expectedName = "expectedName";
        final String newReferenceUri = "/new/file/reference";
        final ClientReference clientFile = new ClientReference(newReferenceUri);
        clientResources.put(expectedName, clientFile);
        final FileResource serverFileResource = new FileResourceImpl();
        serverFileResource.setURIString("/old/file/resource/uri/to/replace/" + expectedName);
        List<ResourceReference> resourceReferences = new ArrayList<ResourceReference>();
        resourceReferences.add(new ResourceReference(serverFileResource));
        when(objectFactory.newResource(null, FileResource.class)).thenReturn(new FileResourceImpl());
        final List<ResourceReference> result = converter.convertResourcesToServer(clientResources, resourceReferences, null);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        final ResourceReference resourceReference = result.get(0);
        assertTrue(resourceReference.isLocal() && resourceReference.getLocalResource() instanceof FileResource);
        FileResource fileResource = (FileResource) resourceReference.getLocalResource();
        assertTrue(fileResource.isReference());
        assertEquals(fileResource.getReferenceURI(), newReferenceUri);
    }

    @Test
    public void convertResourcesToServer_localFile_create() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        final String expectedResourceName = "testResourceName";
        final ClientFile clientFile = new ClientFile();
        clientFile.setType(ClientFile.FileType.css);
        clientResources.put(expectedResourceName, clientFile);
        final FileResourceImpl serverLocalResource = new FileResourceImpl();
        serverLocalResource.setName("resourceNameToOverride");
        when(fileResourceReferenceConverter.toServer(eq(clientFile), any(ResourceReference.class), any(ToServerConversionOptions.class))).thenReturn(new ResourceReference(serverLocalResource));
        final List<ResourceReference> serverReferences = converter.convertResourcesToServer(clientResources, null, null);
        assertNotNull(serverReferences);
        assertEquals(serverReferences.size(), 1);
        final ResourceReference reference = serverReferences.get(0);
        assertNotNull(reference);
        assertTrue(reference.isLocal());
        assertSame(reference.getLocalResource(), serverLocalResource);
        assertEquals(reference.getLocalResource().getName(), expectedResourceName);
    }

    @Test
    public void convertResourcesToServer_localFile_update() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        final String expectedResourceName = "testResourceName";
        final ClientFile clientFile = new ClientFile();
        clientFile.setType(ClientFile.FileType.css);
        clientResources.put(expectedResourceName, clientFile);
        final FileResourceImpl serverLocalResource = new FileResourceImpl();
        serverLocalResource.setName(expectedResourceName);
        final List<ResourceReference> serverReferencesToUpdate = new ArrayList<ResourceReference>();
        final ResourceReference serverReferenceToUpdate = new ResourceReference(serverLocalResource);
        serverReferencesToUpdate.add(serverReferenceToUpdate);
        when(fileResourceReferenceConverter.toServer(clientFile, serverReferenceToUpdate, null)).thenReturn(serverReferenceToUpdate);
        final List<ResourceReference> serverReferences = converter.convertResourcesToServer(clientResources, serverReferencesToUpdate, null);
        assertNotNull(serverReferences);
        assertEquals(serverReferences.size(), 1);
        final ResourceReference reference = serverReferences.get(0);
        assertSame(reference, serverReferenceToUpdate);
        assertTrue(reference.isLocal());
        assertSame(reference.getLocalResource(), serverLocalResource);
    }

    @Test
    public void convertResourcesToServer_reference_createLocalFileReference() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        final String expectedResourceName = "expectedResourceName";
        final String expectedReferenceUri = "/test/reference/uri";
        final ClientReference expectedClientReference = new ClientReference(expectedReferenceUri);
        clientResources.put(expectedResourceName, expectedClientReference);
        when(objectFactory.newResource(null, FileResource.class)).thenReturn(new FileResourceImpl());
        final List<ResourceReference> result = converter.convertResourcesToServer(clientResources, null, null);
        // file resource converter is called to ensure, that reference points to a valid FileResource
        final ArgumentCaptor<ClientReferenceableFile> clientReferenceableFileArgumentCaptor = ArgumentCaptor.forClass(ClientReferenceableFile.class);
        verify(fileResourceReferenceConverter).toServer(clientReferenceableFileArgumentCaptor.capture(), any(ToServerConversionOptions.class));
        final ClientReferenceableFile clientReferenceableFile = clientReferenceableFileArgumentCaptor.getValue();
        assertSame(clientReferenceableFile, expectedClientReference);
        assertEquals(clientReferenceableFile.getUri(), expectedReferenceUri);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        final ResourceReference reference = result.get(0);
        assertNotNull(reference);
        assertTrue(reference.isLocal());
        assertTrue(reference.getLocalResource() instanceof FileResource);
        final FileResource fileResource = (FileResource) reference.getLocalResource();
        assertTrue(fileResource.isReference());
        assertEquals(fileResource.getReferenceURI(), expectedReferenceUri);
        assertEquals(fileResource.getName(), expectedResourceName);
        assertEquals(fileResource.getLabel(), expectedResourceName);
    }

    @Test
    public void convertResourcesToServer_localResourceWrongType_update_newFileReferenceIsCreated() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        final String expectedResourceName = "expectedResourceName";
        final String expectedReferenceUri = "/test/reference/uri";
        final ClientReference expectedClientReference = new ClientReference(expectedReferenceUri);
        clientResources.put(expectedResourceName, expectedClientReference);
        when(objectFactory.newResource(null, FileResource.class)).thenReturn(new FileResourceImpl());
        final List<ResourceReference> serverReferencesToUpdate = new ArrayList<ResourceReference>();
        final QueryImpl localResourceOfWrongType = new QueryImpl();
        localResourceOfWrongType.setName(expectedResourceName);
        final ResourceReference wrongLocalResourceReference = new ResourceReference(localResourceOfWrongType);
        serverReferencesToUpdate.add(wrongLocalResourceReference);
        final List<ResourceReference> result = converter.convertResourcesToServer(clientResources, serverReferencesToUpdate, null);
        // file resource converter is called to ensure, that reference points to a valid FileResource
        final ArgumentCaptor<ClientReferenceableFile> clientReferenceableFileArgumentCaptor = ArgumentCaptor.forClass(ClientReferenceableFile.class);
        verify(fileResourceReferenceConverter).toServer(clientReferenceableFileArgumentCaptor.capture(),any(ToServerConversionOptions.class));
        final ClientReferenceableFile clientReferenceableFile = clientReferenceableFileArgumentCaptor.getValue();
        assertSame(clientReferenceableFile, expectedClientReference);
        assertEquals(clientReferenceableFile.getUri(), expectedReferenceUri);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        final ResourceReference reference = result.get(0);
        assertNotNull(reference);
        assertNotSame(reference, wrongLocalResourceReference);
        assertTrue(reference.isLocal());
        assertTrue(reference.getLocalResource() instanceof FileResource);
        final FileResource fileResource = (FileResource) reference.getLocalResource();
        assertTrue(fileResource.isReference());
        assertEquals(fileResource.getReferenceURI(), expectedReferenceUri);
    }

    @Test
    public void convertResourcesToServer_localFileReference_update() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        final String expectedResourceName = "expectedResourceName";
        final String expectedReferenceUri = "/test/reference/uri";
        final ClientReference expectedClientReference = new ClientReference(expectedReferenceUri);
        clientResources.put(expectedResourceName, expectedClientReference);
        final List<ResourceReference> serverReferencesToUpdate = new ArrayList<ResourceReference>();
        final FileResourceImpl serverFileReference = new FileResourceImpl();
        serverFileReference.setName(expectedResourceName);
        serverFileReference.setReferenceURI("/test/uri/to/be/changed");
        final ResourceReference expectedServerReference = new ResourceReference(serverFileReference);
        serverReferencesToUpdate.add(expectedServerReference);
        final List<ResourceReference> result = converter.convertResourcesToServer(clientResources, serverReferencesToUpdate, null);
        // file resource converter is called to ensure, that reference points to a valid FileResource
        final ArgumentCaptor<ClientReferenceableFile> clientReferenceableFileArgumentCaptor = ArgumentCaptor.forClass(ClientReferenceableFile.class);
        verify(fileResourceReferenceConverter).toServer(clientReferenceableFileArgumentCaptor.capture(), any(ToServerConversionOptions.class));
        final ClientReferenceableFile clientReferenceableFile = clientReferenceableFileArgumentCaptor.getValue();
        assertSame(clientReferenceableFile, expectedClientReference);
        assertEquals(clientReferenceableFile.getUri(), expectedReferenceUri);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        final ResourceReference reference = result.get(0);
        assertSame(reference, expectedServerReference);
        assertTrue(reference.isLocal());
        assertSame(reference.getLocalResource(), serverFileReference);
        final FileResource resultLocalFile = (FileResource) reference.getLocalResource();
        assertTrue(resultLocalFile.isReference());
        assertEquals(resultLocalFile.getReferenceURI(), expectedReferenceUri);
    }

    @Test
    public void convertResourcesToServer_resourceReference_update() throws IllegalParameterValueException, MandatoryParameterNotFoundException {
        final Map<String, ClientReferenceableFile> clientResources = new HashMap<String, ClientReferenceableFile>();
        final String expectedResourceName = "expectedResourceName";
        final String expectedReferenceUri = "/test/reference/uri";
        final ClientReference clientReference = new ClientReference(expectedReferenceUri);
        clientResources.put(expectedResourceName, clientReference);
        final ArrayList<ResourceReference> serverResources = new ArrayList<ResourceReference>();
        final ResourceReference expectedServerReference = new ResourceReference("/test/reference/to/be/changed/" + expectedResourceName);
        serverResources.add(expectedServerReference);
        when(fileResourceReferenceConverter.toServer(clientReference, expectedServerReference, null)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object[] arguments = invocation.getArguments();
                ClientReference clientObject = (ClientReference) arguments[0];
                ResourceReference serverObject = (ResourceReference) arguments[1];
                serverObject.setReference(clientObject.getUri());
                return serverObject;
            }
        });
        final List<ResourceReference> result = converter.convertResourcesToServer(clientResources, serverResources, null);
        assertNotNull(result);
        assertEquals(result.size(), 1);
        final ResourceReference reference = result.get(0);
        assertSame(reference, expectedServerReference);
        assertFalse(reference.isLocal());
        assertEquals(reference.getReferenceURI(), expectedReferenceUri);
    }

    @Test
    public void getServerResourcesAsMap_nullProducesEmptyMap(){
        final Map<String, ResourceReference> serverResourcesAsMap = converter.getServerResourcesAsMap(null);
        assertNotNull(serverResourcesAsMap);
        assertTrue(serverResourcesAsMap.isEmpty());
    }

    @Test
    public void getServerResourcesAsMap_emptyListProducesEmptyMap(){
        final Map<String, ResourceReference> serverResourcesAsMap = converter.getServerResourcesAsMap(new ArrayList<ResourceReference>());
        assertNotNull(serverResourcesAsMap);
        assertTrue(serverResourcesAsMap.isEmpty());
    }

    @Test
    public void getServerResourcesAsMap(){
        final ArrayList<ResourceReference> list = new ArrayList<ResourceReference>();
        final String expectedReferenceName = "expectedReferenceName";
        final String expectedReferenceUri = "/test/resource/" + expectedReferenceName;
        final ResourceReference expectedReference = new ResourceReference(expectedReferenceUri);
        list.add(expectedReference);
        final FileResource localFile = new FileResourceImpl();
        final String expectedLocalFileName = "expectedLocalFileName";
        localFile.setName(expectedLocalFileName);
        final ResourceReference expectedRefereceWithLocalResource = new ResourceReference(localFile);
        list.add(expectedRefereceWithLocalResource);
        final FileResourceImpl expectedLocalFileReference = new FileResourceImpl();
        final String expectedLocalFileReferenceName = "expectedLocalFileReferenceName";
        expectedLocalFileReference.setName(expectedLocalFileReferenceName);
        final ResourceReference expectedReferenceWithFileReference = new ResourceReference(expectedLocalFileReference);
        list.add(expectedReferenceWithFileReference);
        final Map<String, ResourceReference> serverResourcesAsMap = converter.getServerResourcesAsMap(list);
        assertNotNull(serverResourcesAsMap);
        assertEquals(serverResourcesAsMap.size(), list.size());
        for(String currentResourceName : serverResourcesAsMap.keySet()){
            final ResourceReference resourceReference = serverResourcesAsMap.get(currentResourceName);
            if(expectedReferenceName.equals(currentResourceName)){
                assertSame(resourceReference, expectedReference);
                assertEquals(resourceReference.getReferenceURI(), expectedReferenceUri);
            } else if(expectedLocalFileName.equals(currentResourceName)){
                assertSame(resourceReference, expectedRefereceWithLocalResource);
                assertSame(resourceReference.getLocalResource(), localFile);
            } else if(expectedLocalFileReferenceName.equals(currentResourceName)){
                assertSame(resourceReference, expectedReferenceWithFileReference);
                assertEquals(resourceReference.getLocalResource(), expectedLocalFileReference);
            } else{
                // no other names are allowed
                assertTrue(false);
            }
        }
    }
}

