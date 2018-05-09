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

import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.XMLAConnectionImpl;
import com.jaspersoft.jasperserver.dto.resources.ClientXmlaConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.resources.ClientTypeHelper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class XmlaConnectionResourceConverterTest {
    private XmlaConnectionResourceConverter converter = new XmlaConnectionResourceConverter();
    

    @Test
    public void correctClientServerResourceType(){
        assertEquals(converter.getClientResourceType(), ClientTypeHelper.extractClientType(ClientXmlaConnection.class));
        assertEquals(converter.getServerResourceType(), XMLAConnection.class.getName());
    }

    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        final String expectedUrl = "testUri";
        final String expectedDataSource = "testDataSource";
        final String expectedCatalog = "testCatalog";
        final String expectedUsername = "testUsername";
        final String expectedPassword = "testPassword";
        ClientXmlaConnection clientObject = new ClientXmlaConnection();
        XMLAConnection serverObject = new XMLAConnectionImpl();
        clientObject.setUrl(expectedUrl);
        clientObject.setCatalog(expectedCatalog);
        clientObject.setDataSource(expectedDataSource);
        clientObject.setPassword(expectedPassword);
        clientObject.setUsername(expectedUsername);
        final XMLAConnection result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertEquals(result.getURI(), expectedUrl);
        assertEquals(result.getCatalog(), expectedCatalog);
        assertEquals(result.getDataSource(), expectedDataSource);
        assertEquals(result.getPassword(), expectedPassword);
        assertEquals(result.getUsername(), expectedUsername);
    }

    @Test
    public void resourceSpecificFieldsToServer_nullPasswordDoesNotChange() throws IllegalParameterValueException {
        final String expectedPassword = "testPassword";
        ClientXmlaConnection clientObject = new ClientXmlaConnection();
        XMLAConnection serverObject = new XMLAConnectionImpl();
        serverObject.setPassword(expectedPassword);
        final XMLAConnection result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertEquals(result.getPassword(), expectedPassword);
    }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedUrl = "testUrl";
        final String expectedDataSource = "testDataSource";
        final String expectedCatalog = "testCatalog";
        final String expectedUsername = "testUsername";
        final String expectedPassword = "testPassword";
        ClientXmlaConnection clientObject = new ClientXmlaConnection();
        XMLAConnection serverObject = new XMLAConnectionImpl();
        serverObject.setURI(expectedUrl);
        serverObject.setCatalog(expectedCatalog);
        serverObject.setDataSource(expectedDataSource);
        serverObject.setPassword(expectedPassword);
        serverObject.setUsername(expectedUsername);
        final ClientXmlaConnection result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        assertEquals(result.getUrl(), expectedUrl);
        assertEquals(result.getCatalog(), expectedCatalog);
        assertEquals(result.getDataSource(), expectedDataSource);
        // password is hidden
        assertNull(result.getPassword());
        assertEquals(result.getUsername(), expectedUsername);
    }
}
