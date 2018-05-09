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

package com.jaspersoft.jasperserver.api.common.error.handling;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import junit.framework.Assert;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.UUID;

/**
 * Unit tests for {@link SecureExceptionHandlerImpl} class.
 *
 * @author dlitvak
 * @version $Id$
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        LogManager.class,
        SecureExceptionHandlerImpl.class,
        UUID.class
})
public class SecureExceptionHandlerImplTest {

    @Mock
    private Logger loggerMock;
    @Mock
    private MessageSource messageSourceMock;
    @Mock
    private ExceptionOutputManager outputManagerMock;

    public static final String SERVER_ERROR_MESSAGE = "RevenueDetailReport.pdf " +
            "は、リポジトリには保存されませんでした。エラーにより処理が中断されました。";
    public static final String EXPECTED_UNIQUE_IDENTIFIER = "00000000-0000-0001-0000-000000000002";
    private SecureExceptionHandlerImpl exceptionHandler;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(LogManager.class, UUID.class);

        PowerMockito.when(LogManager.getLogger(ExceptionOutputManagerImpl.class)).thenReturn(loggerMock);
        PowerMockito.when(UUID.randomUUID()).thenReturn(new UUID(1L, 2L));

        exceptionHandler = new SecureExceptionHandlerImpl();
        exceptionHandler.setExceptionOutputManager(outputManagerMock);
        exceptionHandler.setMessageSource(messageSourceMock);
    }

    @Test
    public void shouldReturnProperErrorDescriptorForJasperadminUserWhenErrorOccurred() {
        // given
        final Exception exception =
                new JSException("jsexception.report.resource.already.exists.no.overwrite", new JRException("1"));

        Mockito.doReturn(true).when(outputManagerMock).isExceptionMessageAllowed();
        Mockito.doReturn(true).when(outputManagerMock).isStackTraceAllowed();
        Mockito.doReturn(false).when(outputManagerMock).isUIDOutputOn();

        // when
        ErrorDescriptor descriptor = exceptionHandler.handleException(exception,
                new ErrorDescriptor().setMessage(SERVER_ERROR_MESSAGE), Locale.JAPAN);

        // then
        Assert.assertEquals(descriptor.getMessage(), SERVER_ERROR_MESSAGE);
        String[] params = descriptor.getParameters();
        Assert.assertSame(params.length, 1);
    }


    @Test
    public void shouldReturnProperErrorDescriptorForJoeuserWhenErrorOccurred() {
        // given
        final Exception exception =
                new JSException("jsexception.report.resource.already.exists.no.overwrite", new JRException("1"));

        Mockito.doReturn(true).when(outputManagerMock).isExceptionMessageAllowed();
        Mockito.doReturn(false).when(outputManagerMock).isStackTraceAllowed();
        Mockito.doReturn(true).when(outputManagerMock).isUIDOutputOn();

        Mockito.doReturn("Error UID").when(messageSourceMock).getMessage(
                Matchers.eq("error.uid.message"),
                Matchers.any(Object[].class),
                Matchers.any(Locale.class)
        );

        // when
        ErrorDescriptor retrievedDescriptor = exceptionHandler.handleException(exception,
                new ErrorDescriptor().setMessage(SERVER_ERROR_MESSAGE), Locale.JAPAN);

        // then
        Assert.assertEquals(retrievedDescriptor.getMessage(), SERVER_ERROR_MESSAGE +
                " (Error UID: 00000000-0000-0001-0000-000000000002)");
        Assert.assertEquals(retrievedDescriptor.getErrorUid(), EXPECTED_UNIQUE_IDENTIFIER);
    }

    @After
    public void after() {
        Mockito.reset(loggerMock, messageSourceMock, outputManagerMock);
    }
}
