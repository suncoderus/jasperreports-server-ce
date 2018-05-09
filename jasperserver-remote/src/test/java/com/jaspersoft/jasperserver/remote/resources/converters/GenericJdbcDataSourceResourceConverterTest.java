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

import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.JdbcReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.JdbcReportDataSourceImpl;
import com.jaspersoft.jasperserver.dto.resources.AbstractClientJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientJdbcDataSource;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class GenericJdbcDataSourceResourceConverterTest {
    private GenericJdbcDataSourceResourceConverter converter = new GenericJdbcDataSourceResourceConverter();

    @Test
    public void resourceSpecificFieldsToServer() throws Exception {
        final String expectedDriverClass = "testDriverClass";
        final String expectedPassword = "testPassword";
        final String expectedUsername = "testUsername";
        final String expectedConnectionUrl = "testConnectionUrl";
        final String expectedTimezone = "North Pole";
        final ClientJdbcDataSource clientObject = new ClientJdbcDataSource();
        clientObject.setConnectionUrl(expectedConnectionUrl);
        clientObject.setDriverClass(expectedDriverClass);
        clientObject.setPassword(expectedPassword);
        clientObject.setUsername(expectedUsername);
        clientObject.setTimezone(expectedTimezone);
        final JdbcReportDataSource serverObject = new JdbcReportDataSourceImpl();
        final JdbcReportDataSource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertEquals(result.getConnectionUrl(), expectedConnectionUrl);
        assertEquals(result.getDriverClass(), expectedDriverClass);
        assertEquals(result.getPassword(), expectedPassword);
        assertEquals(result.getUsername(), expectedUsername);
        assertEquals(result.getTimezone(), expectedTimezone);
    }

    @Test
    public void resourceSpecificFieldsToServer_passwordNotSet() throws Exception {
        final String expectedDriverClass = "testDriverClass";
        final String expectedPassword = "testPassword";
        final String expectedUsername = "testUsername";
        final String expectedConnectionUrl = "testConnectionUrl";
        final ClientJdbcDataSource clientObject = new ClientJdbcDataSource();
        clientObject.setConnectionUrl(expectedConnectionUrl);
        clientObject.setDriverClass(expectedDriverClass);
        clientObject.setPassword(null);
        clientObject.setUsername(expectedUsername);
        final JdbcReportDataSource serverObject = new JdbcReportDataSourceImpl();
        serverObject.setPassword(expectedPassword);
        final JdbcReportDataSource result = converter.resourceSpecificFieldsToServer(clientObject, serverObject, null);
        assertSame(result, serverObject);
        assertEquals(result.getConnectionUrl(), expectedConnectionUrl);
        assertEquals(result.getDriverClass(), expectedDriverClass);
        assertEquals(result.getPassword(), expectedPassword);
        assertEquals(result.getUsername(), expectedUsername);
    }

    @Test
    public void resourceSpecificFieldsToClient(){
        final String expectedDriverClass = "testDriverClass";
        final String expectedPassword = "testPassword";
        final String expectedUsername = "testUsername";
        final String expectedConnectionUrl = "testConnectionUrl";
        final String expectedTimezone = "North Pole";
        final ClientJdbcDataSource clientObject = new ClientJdbcDataSource();
        final JdbcReportDataSource serverObject = new JdbcReportDataSourceImpl();
        serverObject.setConnectionUrl(expectedConnectionUrl);
        serverObject.setDriverClass(expectedDriverClass);
        serverObject.setPassword(expectedPassword);
        serverObject.setUsername(expectedUsername);
        serverObject.setTimezone(expectedTimezone);
        final AbstractClientJdbcDataSource result = converter.resourceSpecificFieldsToClient(clientObject, serverObject, null);
        assertSame(result, clientObject);
        assertEquals(result.getConnectionUrl(), expectedConnectionUrl);
        assertEquals(result.getDriverClass(), expectedDriverClass);
        assertNull(result.getPassword());
        assertEquals(result.getUsername(), expectedUsername);
        assertEquals(result.getTimezone(), expectedTimezone);
    }
}
