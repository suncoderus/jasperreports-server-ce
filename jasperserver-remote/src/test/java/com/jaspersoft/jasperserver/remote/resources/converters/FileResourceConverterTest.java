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
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class FileResourceConverterTest {
    @InjectMocks
    private FileResourceConverter converter = new FileResourceConverter();
    @Mock
    private RepositoryService repositoryService;

    private byte[] data = new byte[]{1,2,3,4,5,6,7,8,9,0};
    private String encoded = DatatypeConverter.printBase64Binary(data);


    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientFile.class));
        assertEquals(converter.getServerResourceType(), FileResource.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception{
        final String expectedFileType = FileResource.TYPE_JRXML;
        final ClientFile clientObject = new ClientFile();
        final FileResource serverObject = new FileResourceImpl();
        clientObject.setType(ClientFile.FileType.jrxml);
        clientObject.setContent(encoded);
        final FileResource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertNotNull(result);
        assertEquals(result.getFileType(), expectedFileType);
        assertEquals(result.getData(), data);
    }

    @Test
    public void resourceSpecificFieldsToServer_reference_toFullResource() throws Exception{
        final String expectedFileType = FileResource.TYPE_JRXML;
        final ClientFile clientObject = new ClientFile();
        final FileResource serverObject = new FileResourceImpl(), referenced = new FileResourceImpl();

        serverObject.setReferenceURI("aff");
        referenced.setFileType(expectedFileType);
        clientObject.setType(ClientFile.FileType.jrxml);
        clientObject.setContent(encoded);

        when(repositoryService.getResource(any(ExecutionContext.class), anyString())).thenReturn(referenced);

        final FileResource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);

        assertNotNull(result);
        assertNotNull(result.getFileType());
        assertEquals(result.getData(), data);
    }

    @Test(expectedExceptions = {IllegalParameterValueException.class})
    public void resourceSpecificFieldsToServer_reference_TypeDifferent() throws Exception{
        final ClientFile clientObject = new ClientFile();
        final FileResource serverObject = new FileResourceImpl(), referenced = new FileResourceImpl();

        serverObject.setReferenceURI("aff");
        referenced.setFileType(FileResource.TYPE_ACCESS_GRANT_SCHEMA);
        clientObject.setType(ClientFile.FileType.jrxml);
        clientObject.setContent(encoded);

        when(repositoryService.getResource(any(ExecutionContext.class), anyString())).thenReturn(referenced);

        final FileResource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
    }

    @Test(expectedExceptions = {IllegalParameterValueException.class})
    public void resourceSpecificFieldsToServer_TypeDifferent() throws Exception{
        final ClientFile clientObject = new ClientFile();
        final FileResource serverObject = new FileResourceImpl();

        serverObject.setFileType(FileResource.TYPE_ACCESS_GRANT_SCHEMA);
        clientObject.setType(ClientFile.FileType.jrxml);
        clientObject.setContent(encoded);

        final FileResource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
    }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedFileType = FileResource.TYPE_JRXML;
        final ClientFile clientObject = new ClientFile();
        final FileResource serverObject = new FileResourceImpl();
        serverObject.setFileType(expectedFileType);
        serverObject.setData(data);
        final ClientFile result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertNotNull(result);
        assertEquals(result.getType().name(), expectedFileType);
        assertEquals(result.getContent(), null);
    }

    @Test
    public void resourceSpecificFieldsToClient_reference(){
        final String expectedFileType = FileResource.TYPE_JRXML;
        final String referencedUri = "/test";
        final ClientFile clientObject = new ClientFile();
        final FileResource serverObject = new FileResourceImpl(), referenced = new FileResourceImpl();

        referenced.setFileType(expectedFileType);
        serverObject.setData(data);
        serverObject.setReferenceURI(referencedUri);

        when(repositoryService.getResource(any(ExecutionContext.class), eq(referencedUri))).thenReturn(referenced);

        final ClientFile result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);

        assertNotNull(result);
        assertEquals(result.getType().name(), expectedFileType);
    }

    @Test
    public void resourceSpecificFieldsToClient_unsupportedFileTypeBecomesUnspecified(){
        final FileResourceImpl serverObject = new FileResourceImpl();
        serverObject.setFileType("someNotSupportedFileType");
        final ClientFile clientFile = converter.resourceSpecificFieldsToClient(new ClientFile(), serverObject, null);
        assertSame(clientFile.getType(), ClientFile.FileType.unspecified);
    }
}
