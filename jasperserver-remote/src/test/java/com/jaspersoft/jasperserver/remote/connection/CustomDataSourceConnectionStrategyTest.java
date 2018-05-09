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
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.common.error.handling.SecureExceptionHandler;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.CustomReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.CustomReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.CustomReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.CustomReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientCustomDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.remote.resources.converters.CustomDataSourceResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import net.sf.jasperreports.engine.JRException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class CustomDataSourceConnectionStrategyTest {
    @InjectMocks
    private CustomDataSourceContextStrategy strategy = new CustomDataSourceContextStrategy();
    @Mock
    private Set<String> propertiesToIgnore;
    @Mock
    private CustomReportDataSourceServiceFactory customDataSourceFactory;
    @Mock
    private CustomDataSourceResourceConverter customDataSourceResourceConverter;
    @Mock
    private RepositoryService repository;
    @Mock
    private CustomReportDataSourceService customReportDataSourceService;
    @Mock
    private SecureExceptionHandler secureExceptionHandlerMock;

    private ClientCustomDataSource customDataSource;
    private CustomReportDataSource serverCustomReportDataSource;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void refresh(){
        reset(propertiesToIgnore, customDataSourceFactory, customDataSourceResourceConverter, repository, customReportDataSourceService);
        customDataSource = new ClientCustomDataSource().setProperties(new ArrayList<ClientProperty>());
        serverCustomReportDataSource = new CustomReportDataSourceImpl();
        serverCustomReportDataSource.setURIString("/some/resource/uri");
        serverCustomReportDataSource.setPropertyMap(new HashMap());
        when(customDataSourceResourceConverter.toServer(same(customDataSource), any(ToServerConversionOptions.class)))
                .thenReturn(serverCustomReportDataSource);
        when(customDataSourceFactory.createService(serverCustomReportDataSource)).thenReturn(customReportDataSourceService);
        when(secureExceptionHandlerMock.handleException(isA(Throwable.class), isA(ErrorDescriptor.class))).thenReturn(new ErrorDescriptor().setMessage("test"));
    }

    @Test
    public void secureGetConnection_ignorablePropertiesAreRemoved(){
        final String propertyToSecure = "propertyToSecure";
        when(propertiesToIgnore.contains(propertyToSecure)).thenReturn(true);
        final ClientProperty clientPropertyToRemove = new ClientProperty(propertyToSecure, null);
        customDataSource.getProperties().add(clientPropertyToRemove);
        final ClientCustomDataSource result = strategy.getContextForClient(customDataSource, null);
        assertNotNull(result);
        // original object isn't changed
        assertTrue(customDataSource.getProperties().contains(clientPropertyToRemove));
        final List<ClientProperty> properties = result.getProperties();
        if(properties != null){
            for (ClientProperty property : properties){
                assertNotEquals(property.getKey(), propertyToSecure);
            }
        }
    }

    @Test
    public void createConnection_noPasswordProperty_success() throws Exception {
        doReturn(true).when(customReportDataSourceService).testConnection();
        final ClientCustomDataSource connection = strategy.createContext(customDataSource, null);
        assertSame(connection, customDataSource);
    }

    @Test
    public void createConnection_noTestConnection_success() throws JRException {
        reset(customDataSourceFactory);
        when(customDataSourceFactory.createService(serverCustomReportDataSource)).thenReturn(mock(ReportDataSourceService.class));
        final ClientCustomDataSource connection = strategy.createContext(customDataSource, null);
        assertSame(connection, customDataSource);
    }

    @Test(expectedExceptions = ContextCreationFailedException.class)
    public void createConnection_testConnectionReturnsFalse_failure() throws Exception {
        doReturn(false).when(customReportDataSourceService).testConnection();
        final ClientCustomDataSource connection = strategy.createContext(customDataSource, null);
        assertSame(connection, customDataSource);
    }

    @Test(expectedExceptions = ContextCreationFailedException.class)
    public void createConnection_testConnectionThrowsException_failure() throws Exception {
        doThrow(new RuntimeException()).when(customReportDataSourceService).testConnection();
        final ClientCustomDataSource connection = strategy.createContext(customDataSource, null);
        assertSame(connection, customDataSource);
    }

    @Test
    public void createConnection_passwordIsNull_success() throws Exception {
        doReturn(true).when(customReportDataSourceService).testConnection();
        serverCustomReportDataSource.getPropertyMap().put("password", null);
        final CustomReportDataSourceImpl dsFromRepository = new CustomReportDataSourceImpl();
        dsFromRepository.setPropertyMap(new HashMap());
        final String expectedPassword = "expectedPassword";
        dsFromRepository.getPropertyMap().put("password", expectedPassword);
        customDataSource.setUri(serverCustomReportDataSource.getURIString());
        when(repository.getResource(null, serverCustomReportDataSource.getURIString())).thenReturn(dsFromRepository);
        reset(customDataSourceFactory);
        ArgumentCaptor<CustomReportDataSource> argumentCaptor = ArgumentCaptor.forClass(CustomReportDataSource.class);
        doReturn(customReportDataSourceService).when(customDataSourceFactory).createService(argumentCaptor.capture());
        final ClientCustomDataSource connection = strategy.createContext(customDataSource, null);
        assertSame(connection, customDataSource);
        final CustomReportDataSource reportDataSource = argumentCaptor.getValue();
        assertNotNull(reportDataSource);
        assertNotNull(reportDataSource.getPropertyMap());
        assertEquals(reportDataSource.getPropertyMap().get("password"), expectedPassword);
    }


}
