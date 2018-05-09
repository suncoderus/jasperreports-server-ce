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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.MondrianConnectionImpl;
import com.jaspersoft.jasperserver.dto.resources.AbstractClientMondrianConnection;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientMondrianConnection;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class GenericMondrianConnectionResourceConverterTest {
    @InjectMocks
    private GenericMondrianConnectionResourceConverter converter = new GenericMondrianConnectionResourceConverter();
    @Mock
    private ResourceReferenceConverterProvider resourceReferenceConverterProvider;
    @Mock
    private ResourceReferenceConverter<ClientReferenceableFile> fileResourceReferenceConverter;
    private ArgumentCaptor<ClientReferenceRestriction> restrictionArgumentCaptor = ArgumentCaptor.forClass(ClientReferenceRestriction.class);

    @BeforeClass
    public void initConverter() {
        MockitoAnnotations.initMocks(this);
        when(resourceReferenceConverterProvider.getConverterForType(ClientReferenceableFile.class)).thenReturn(fileResourceReferenceConverter);
        when(fileResourceReferenceConverter.addReferenceRestriction(any(ClientReferenceRestriction.class))).thenReturn(fileResourceReferenceConverter);
    }

    @Test
    public void resourceSpecificFieldsToClient() throws Exception {
        final ClientMondrianConnection clientObject = new ClientMondrianConnection();
        final MondrianConnection serverObject = new MondrianConnectionImpl();
        final String fileReferenceUri = "/schema/reference/uri";
        final ResourceReference fileReference = new ResourceReference(fileReferenceUri);
        final ClientReference expectedClientReference = new ClientReference(fileReferenceUri);
        final ToClientConversionOptions options = ToClientConversionOptions.getDefault();
        when(fileResourceReferenceConverter.toClient(fileReference, options)).thenReturn(expectedClientReference);
        serverObject.setSchema(fileReference);
        final AbstractClientMondrianConnection result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, options);
        assertSame(result, clientObject);
        final ClientReferenceableFile file = result.getSchema();
        assertSame(file, expectedClientReference);
        assertEquals(file.getUri(), fileReferenceUri);
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        final ClientMondrianConnection clientObject = new ClientMondrianConnection();
        final MondrianConnection serverObject = new MondrianConnectionImpl();
        final String fileReferenceUri = "/schema/reference/uri";
        final ResourceReference fileReference = new ResourceReference(fileReferenceUri);
        final ClientReference clientReference = new ClientReference(fileReferenceUri);
        final ToServerConversionOptions options = ToServerConversionOptions.getDefault();
        when(fileResourceReferenceConverter.toServer(eq(clientReference), any(ResourceReference.class), any(ToServerConversionOptions.class))).thenReturn(fileReference);
        clientObject.setSchema(clientReference);
        final MondrianConnection result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, options);
        assertSame(result, serverObject);
        final ResourceReference resultReference = result.getSchema();
        assertSame(resultReference, fileReference);
        // check for additional restriction is added. References on XML files only allowed
        verify(fileResourceReferenceConverter).addReferenceRestriction(restrictionArgumentCaptor.capture());
        final List<ClientReferenceRestriction> restrictions = restrictionArgumentCaptor.getAllValues();
        assertNotNull(restrictions);
        assertEquals(restrictions.size(), 1);
        final ClientReferenceRestriction restriction = restrictions.get(0);
        assertTrue(restriction instanceof ResourceReferenceConverter.FileTypeRestriction);
        final ClientFile fileToTest = new ClientFile();
        fileToTest.setType(ClientFile.FileType.olapMondrianSchema);
        // no exception should be here. It means restriction is set to accept XML
        restriction.validateReference(fileToTest);
    }



}
