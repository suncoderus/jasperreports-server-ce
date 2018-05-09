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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConverter;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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
public class LookupResourceConverterTest {
    @InjectMocks
    private LookupResourceConverter converter = new LookupResourceConverter();
    @Mock
    private ResourceConverterProvider resourceConverterProvider;

    private LookupResourceConverter converterMock;

    @BeforeClass
    public void initClass() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void initMock() {
        converterMock = mock(LookupResourceConverter.class);
    }

    @Test
    public void toServer_notSupported() {
        IllegalStateException exception = null;
        try {
            converter.toServer(new ClientResourceLookup(), null);
        } catch (IllegalStateException ex) {
            exception = ex;
        }
        assertNotNull(exception);
        exception = null;
        try {
            converter.toServer(new ClientResourceLookup(), new ResourceLookupImpl(), null);
        } catch (IllegalStateException ex) {
            exception = ex;
        }
        assertNotNull(exception);
    }

    @Test
    public void resourceSpecificFieldsToClient() {
        final ClientResourceLookup expectedResult = new ClientResourceLookup();
        final ResourceLookupImpl serverObject = new ResourceLookupImpl();
        serverObject.setURIString("/uri/string/to/avoid/exception");
        final String serverResourceType = "serverResourceType";
        final String clientResourceType = "clientResourceType";
        serverObject.setResourceType(serverResourceType);
        final ToClientConversionOptions options = ToClientConversionOptions.getDefault();
        when(converterMock.resourceSpecificFieldsToClient(expectedResult, serverObject, options)).thenCallRealMethod();
        when(converterMock.toClientResourceType(serverResourceType)).thenReturn(clientResourceType);
        final ClientResourceLookup result = converterMock.resourceSpecificFieldsToClient(expectedResult, serverObject, options);
        assertSame(result, expectedResult);
        assertEquals(result.getResourceType(), clientResourceType);
    }

    @Test
    public void toClientResourceType() {
        final String serverResourceType = "serverResourceType";
        final String clientResourceType = "clientResourceType";
        final ToClientConverter<Resource, ClientResource, ToClientConversionOptions> toClientConverter = new ToClientConverter<Resource, ClientResource, ToClientConversionOptions>() {
            @Override
            public ClientResource toClient(Resource serverObject, ToClientConversionOptions options) {
                return null;
            }

            @Override
            public String getClientResourceType() {
                return clientResourceType;
            }
        };
        reset(resourceConverterProvider);
        when(resourceConverterProvider.getToClientConverter(serverResourceType)).thenReturn((ToClientConverter) toClientConverter);
        assertEquals(converter.toClientResourceType(serverResourceType), clientResourceType);
    }

    @Test
    public void toClientResourceType_unknownType() {
        final String serverResourceType = "serverResourceType";
        final String clientResourceType = "unknown";
        reset(resourceConverterProvider);
        when(resourceConverterProvider.getToClientConverter(serverResourceType)).thenThrow(IllegalParameterValueException.class);
        assertEquals(converter.toClientResourceType(serverResourceType), clientResourceType);
    }


}
