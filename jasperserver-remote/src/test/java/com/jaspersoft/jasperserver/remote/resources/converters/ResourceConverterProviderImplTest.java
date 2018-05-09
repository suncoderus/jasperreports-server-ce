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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.ToClientConversionOptions;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ResourceConverterProviderImplTest {
    @InjectMocks
    private ResourceConverterProviderImpl provider;
    @Mock
    private ApplicationContext context;
    @Mock
    private BinaryDataResourceConverter binaryDataResourceConverter;
    private LookupResourceConverter lookupResourceConverter;
    private TestFolderConverter testFolderConverter;

    private final String type = ClientTypeHelper.extractClientType(ClientFile.class);

    @BeforeMethod
    public void init(){
        provider = new ResourceConverterProviderImpl();
        lookupResourceConverter = new LookupResourceConverter();
        testFolderConverter = new TestFolderConverter();
        MockitoAnnotations.initMocks(this);
        Map<String, ResourceConverter> converterMap = new HashMap<String, ResourceConverter>();
        converterMap.put("lookupConverter", lookupResourceConverter);
        converterMap.put("testFolderConverter", testFolderConverter);
        when(context.getBeansOfType(ResourceConverter.class)).thenReturn(converterMap);
        when(binaryDataResourceConverter.getClientResourceType()).thenReturn(type);
    }

    @Test
    public void getClientTypeClass() throws IllegalParameterValueException {
        ResourceConverterProviderImpl converterProvider = mock(ResourceConverterProviderImpl.class);
        final String testType = "testType";
        when(converterProvider.getClientTypeClass(testType)).thenCallRealMethod();
        final ResourceConverter resourceConverter = mock(ResourceConverter.class);
        when(converterProvider.getToServerConverter(testType)).thenReturn((ToServerConverter) resourceConverter);
        when(resourceConverter.getClientTypeClass()).thenReturn(String.class);
        assertSame(converterProvider.getClientTypeClass(testType), String.class);
    }

    @Test
    public void getConverters(){
        final List<ResourceConverter> converters = (List)provider.getConverters();
        assertNotNull(converters);
        assertTrue(converters.size() == 2);
        if(converters.get(0) instanceof LookupResourceConverter){
            assertTrue(converters.get(1) instanceof TestFolderConverter);
        } else if(converters.get(0) instanceof TestFolderConverter){
            assertTrue(converters.get(1) instanceof LookupResourceConverter);
        } else {
            // unknown converter type is present
            assertTrue(false);
        }
    }

    @Test
    public void prepareConverters_getToClientConverter_string() throws Exception {
        assertSame(provider.getToClientConverter(ResourceLookup.class.getName()), lookupResourceConverter);
        assertSame(provider.getToClientConverter(Folder.class.getName()), testFolderConverter);
        IllegalParameterValueException exception = null;
        try{
            provider.getToClientConverter("unknownType");
        }catch (IllegalParameterValueException ex){
            exception = ex;
        }
        assertNotNull(exception);
    }

    @Test
    public void prepareConverters_getToClientConverter_object() throws Exception {
        assertSame(provider.getToClientConverter(new ResourceLookupImpl()), lookupResourceConverter);
        assertSame(provider.getToClientConverter(new FolderImpl()), testFolderConverter);
    }

    @Test
    public void prepareConverters_getToServerConverter_string() throws Exception {
        assertSame(provider.getToServerConverter(ClientTypeHelper.extractClientType(ClientResourceLookup.class)), lookupResourceConverter);
        assertSame(provider.getToServerConverter(ClientTypeHelper.extractClientType(TestClientFolder.class)), testFolderConverter);
        IllegalParameterValueException exception = null;
        try{
            provider.getToServerConverter("unknownType");
        }catch (IllegalParameterValueException ex){
            exception = ex;
        }
        assertNotNull(exception);
    }

    @Test
    public void prepareConverters_getToServerConverter_object() throws Exception {
        assertSame(provider.getToServerConverter(new ClientResourceLookup()), lookupResourceConverter);
    }

    @Test
    public void prepareConverters_getToServerConverter_file() throws Exception {
        assertSame(provider.getToServerConverter(new ClientFile()), binaryDataResourceConverter);
    }

    private class TestClientFolder extends ClientResource<TestClientFolder> {}
    private class TestFolderConverter extends ResourceConverterImpl<Folder, TestClientFolder> {
        @Override
        protected Folder resourceSpecificFieldsToServer(TestClientFolder clientObject, Folder resultToUpdate, ToServerConversionOptions options) throws IllegalParameterValueException, MandatoryParameterNotFoundException {
            return resultToUpdate;
        }

        @Override
        protected TestClientFolder resourceSpecificFieldsToClient(TestClientFolder client, Folder serverObject, ToClientConversionOptions options) {
            return client;
        }
    }
}
