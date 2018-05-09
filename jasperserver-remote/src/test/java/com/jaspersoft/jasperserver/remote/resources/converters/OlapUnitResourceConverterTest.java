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
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.OlapUnitImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientOlapUnit;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenciableOlapConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * @author vsabadosh
 * @version $Id$
 */
public class OlapUnitResourceConverterTest {
    @InjectMocks
    private OlapUnitResourceConverter converter = new OlapUnitResourceConverter();
    @Mock
    private ResourceReferenceConverterProvider resourceReferenceConverterProvider;
    @Mock
    private ClientReferenciableOlapConnection clientConnection;
    @Mock
    private ResourceReference serverConnection;
    @Mock
    private ToServerConversionOptions toServerConversionOptions;
    @Mock
    private ToClientConversionOptions toClientConversionOptions;
    private final String EXPECTED_MDX_QUERY = "testMdxQuery";

    @BeforeClass
    public void init() throws MandatoryParameterNotFoundException, IllegalParameterValueException {
        MockitoAnnotations.initMocks(this);
        final ResourceReferenceConverter<ClientReferenciableOlapConnection> olapConnectionReferencesConverter = (ResourceReferenceConverter<ClientReferenciableOlapConnection>) mock(ResourceReferenceConverter.class);
        when(resourceReferenceConverterProvider.getConverterForType(ClientReferenciableOlapConnection.class)).thenReturn(olapConnectionReferencesConverter);
        when(olapConnectionReferencesConverter.toServer(clientConnection, null, toServerConversionOptions)).thenReturn(serverConnection);
        when(olapConnectionReferencesConverter.toClient(serverConnection, toClientConversionOptions)).thenReturn(clientConnection);
    }

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientOlapUnit.class));
        assertEquals(converter.getServerResourceType(), OlapUnit.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        ClientOlapUnit clientObject = new ClientOlapUnit();
        OlapUnit serverObject = new OlapUnitImpl();
        clientObject.setMdxQuery(EXPECTED_MDX_QUERY);
        clientObject.setOlapConnection(clientConnection);
        final OlapUnit result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, toServerConversionOptions);
        assertSame(result, serverObject);
        assertEquals(result.getMdxQuery(), EXPECTED_MDX_QUERY);
        assertSame(result.getOlapClientConnection(), serverConnection);
    }

    @Test
    public void resourceSpecificFieldsToClient(){
        ClientOlapUnit clientObject = new ClientOlapUnit();
        OlapUnit serverObject = new OlapUnitImpl();
        serverObject.setMdxQuery(EXPECTED_MDX_QUERY);
        serverObject.setOlapClientConnection(serverConnection);
        final ClientOlapUnit result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, toClientConversionOptions);
        assertSame(result, clientObject);
        assertEquals(result.getMdxQuery(), EXPECTED_MDX_QUERY);
        assertSame(result.getOlapConnection(), clientConnection);
    }

}
