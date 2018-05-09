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
package com.jaspersoft.jasperserver.jaxrs.connection;

import com.jaspersoft.jasperserver.remote.connection.ContextsManager;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class ConnectionsJaxrsServiceTest {
    @InjectMocks
    private ConnectionsJaxrsService service = new ConnectionsJaxrsService();
    @Mock
    private Providers providers;
    @Mock
    private HttpHeaders httpHeaders;
    @Mock
    private HttpServletRequest request;
    @Mock
    private ContextsManager contextsManager;
    private ConnectionsJaxrsService spyService;
    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
        spyService = spy(service);
    }

    @BeforeMethod
    public void resetMocks(){
        reset(providers, httpHeaders, request, contextsManager, spyService);
    }

    @Test
    public void getConnectionClass(){
        final String connectionType = "lfc";
        final String type = "application/connections." + connectionType + "+json";
        Class expectedClass = Number.class;
        when(contextsManager.getConnectionDescriptionClass(connectionType)).thenReturn(expectedClass);
        final Class<?> result = service.getConnectionClass(MediaType.valueOf(type));
        assertSame(result, expectedClass);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void getConnectionClass_notExistentType_exception(){
        service.getConnectionClass(MediaType.valueOf("application/connections.notExistentType+xml"));
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void getConnectionClass_nullMediaType_exception(){
        service.getConnectionClass(null);
    }

    @Test
    public void createConnection_establish()throws Exception{
        final MediaType connectionType = MediaType.valueOf("application/connections.type+json");
        final Class<Number> connectionClass = Number.class;
        doReturn(connectionClass).when(spyService).getConnectionClass(connectionType);
        final InputStream streamMock = mock(InputStream.class);
        final Object expectedConnectionObject = new Object();
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, streamMock, connectionType);
        final UUID expectedUuid = UUID.randomUUID();
        when(contextsManager.createConnection(expectedConnectionObject)).thenReturn(expectedUuid);
        final String someRequestUrl = "someRequestUrl";
        when(request.getRequestURL()).thenReturn(new StringBuffer(someRequestUrl));
        final Response response = spyService.createConnection(streamMock, connectionType, null, request);
        assertEquals(response.getMetadata().getFirst(HttpHeaders.LOCATION).toString(), someRequestUrl + "/" + expectedUuid);
    }

    @Test
    public void createConnection_metadata()throws Exception{
        final MediaType connectionType = MediaType.valueOf("application/connections.type+json");
        final MediaType metadataType = MediaType.valueOf("application/connections.type.metadata+json");
        final Class<Number> connectionClass = Number.class;
        doReturn(connectionClass).when(spyService).getConnectionClass(connectionType);
        doReturn(connectionClass).when(spyService).getConnectionClass(metadataType);
        final InputStream streamMock = mock(InputStream.class);
        final Object expectedConnectionObject = new Object();
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, streamMock, connectionType);
        final UUID expectedUuid = UUID.randomUUID();
        when(contextsManager.isMetadataSupported(expectedConnectionObject, "connections.type.metadata")).thenReturn(true);
        when(contextsManager.createConnection(expectedConnectionObject)).thenReturn(expectedUuid);
        when(contextsManager.getConnectionMetadata(expectedUuid, new HashMap<String, String[]>())).thenReturn(expectedConnectionObject);
        final String someRequestUrl = "someRequestUrl";
        when(request.getRequestURL()).thenReturn(new StringBuffer(someRequestUrl));
        final Response response = spyService.createConnection(streamMock, connectionType, metadataType, request);
        assertEquals(response.getMetadata().getFirst(HttpHeaders.LOCATION).toString(), someRequestUrl + "/" + expectedUuid + "/metadata");
        assertSame(response.getEntity(), expectedConnectionObject);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void createConnection_metadata_notSupported()throws Exception{
        final MediaType connectionType = MediaType.valueOf("application/connections.type+json");
        final MediaType metadataType = MediaType.valueOf("application/connections.type.metadata+json");
        final Class<Number> connectionClass = Number.class;
        doReturn(connectionClass).when(spyService).getConnectionClass(connectionType);
        doReturn(connectionClass).when(spyService).getConnectionClass(metadataType);
        final InputStream streamMock = mock(InputStream.class);
        final Object expectedConnectionObject = new Object();
        doReturn(expectedConnectionObject).when(spyService).parseEntity(connectionClass, streamMock, connectionType);
        when(contextsManager.isMetadataSupported(expectedConnectionObject, "connections.type.metadata")).thenReturn(false);
        spyService.createConnection(streamMock, connectionType, metadataType, request);
    }

}
