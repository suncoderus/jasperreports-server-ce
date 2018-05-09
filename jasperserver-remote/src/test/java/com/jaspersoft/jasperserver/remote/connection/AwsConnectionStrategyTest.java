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
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.AwsDataSourceService;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.AwsReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.AwsReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientAwsDataSource;
import com.jaspersoft.jasperserver.remote.resources.converters.AwsDataSourceResourceConverter;
import com.jaspersoft.jasperserver.remote.resources.converters.ToServerConversionOptions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class AwsConnectionStrategyTest {
    private static ClientAwsDataSource AWS_DATA_SOURCE_TEMPLATE = new ClientAwsDataSource()
            .setConnectionUrl("someConnectionUrl").setUsername("someUsername").setPassword("somePassword")
            .setDriverClass("someDriverClass").setUri("someUri").setSecretKey("someSecretKey");
    @InjectMocks
    private AwsContextStrategy strategy = new AwsContextStrategy();
    @Mock
    private MessageSource messageSource;
    @Mock
    private RepositoryService repository;
    @Mock
    private AwsReportDataSourceServiceFactory awsDataSourceFactory;
    @Mock
    private AwsDataSourceResourceConverter awsDataSourceResourceConverter;
    @Mock
    private AwsDataSourceService awsDataSourceService;
    @Mock
    private SecureExceptionHandler secureExceptionHandlerMock;

    private ClientAwsDataSource awsDataSource;
    private AwsReportDataSourceImpl awsReportDataSource;


    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void refresh() throws Exception {
        reset(messageSource, repository, awsDataSourceFactory, awsDataSourceService, repository,
                awsDataSourceResourceConverter);
        awsDataSource = new ClientAwsDataSource(AWS_DATA_SOURCE_TEMPLATE);

        doReturn(true).when(awsDataSourceService).testConnection();
        awsReportDataSource = new AwsReportDataSourceImpl();
        doReturn(awsReportDataSource).when(awsDataSourceResourceConverter)
                .toServer(same(awsDataSource), eq(ToServerConversionOptions.getDefault().setSuppressValidation(true)));
        doReturn(awsDataSourceService).when(awsDataSourceFactory).createService(awsReportDataSource);
        when(secureExceptionHandlerMock.handleException(isA(Throwable.class), isA(ErrorDescriptor.class))).thenReturn(new ErrorDescriptor().setMessage("test"));
    }

    @Test
    public void createConnection_withPasswordAndSecretKey_success() throws Exception {
        final ClientAwsDataSource result = strategy.createContext(awsDataSource, null);
        assertSame(result, awsDataSource);
    }

    @Test
    public void createConnection_withSubstitutionOfPasswordAndSecretKey_success() throws Exception {
        final String passwordSubstitution = "passwordSubstitution";
        doReturn(passwordSubstitution).when(messageSource).getMessage(eq("input.password.substitution"),
                isNull(Object[].class), any(Locale.class));
        awsDataSource.setPassword(passwordSubstitution).setSecretKey(passwordSubstitution);
        awsReportDataSource.setPassword("realPassword");
        awsReportDataSource.setAWSSecretKey("realSecretKey");
        doReturn(awsReportDataSource).when(repository).getResource(null, awsDataSource.getUri());
        final ClientAwsDataSource result = strategy.createContext(awsDataSource, null);
        assertSame(result, awsDataSource);
        assertEquals(result.getPassword(), passwordSubstitution);
        assertEquals(result.getSecretKey(), passwordSubstitution);
    }

    @Test
    public void createConnection_withNullPasswordAndSecretKey_success() throws Exception {
        awsDataSource.setPassword(null).setSecretKey(null);
        awsReportDataSource.setPassword("realPassword");
        awsReportDataSource.setAWSSecretKey("realSecretKey");
        doReturn(awsReportDataSource).when(repository).getResource(null, awsDataSource.getUri());
        final ClientAwsDataSource result = strategy.createContext(awsDataSource, null);
        assertSame(result, awsDataSource);
        assertNull(result.getPassword());
        assertNull(result.getSecretKey());
    }


    @Test(expectedExceptions = ContextCreationFailedException.class)
    public void createConnection_createConnectionReturnsNull_exception() throws Exception{
        when(awsDataSourceService.testConnection()).thenReturn(false);
        strategy.createContext(awsDataSource, null);
    }

    @Test(expectedExceptions = ContextCreationFailedException.class)
    public void createConnection_createConnectionThrowsException_exception() throws Exception{
        doThrow(new RuntimeException()).when(awsDataSourceService).testConnection();
        strategy.createContext(awsDataSource, null);
    }


}
