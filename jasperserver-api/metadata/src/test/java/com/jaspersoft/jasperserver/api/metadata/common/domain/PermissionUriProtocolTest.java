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

package com.jaspersoft.jasperserver.api.metadata.common.domain;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for PermissionURIProtocol class
 *
 * @author ohavavka
 */
public class PermissionUriProtocolTest {
    @Test
    public void getParentUriTest() throws Exception {
        assertEquals(null,PermissionUriProtocol.RESOURCE.getParentUri("/"));
        assertEquals("/",PermissionUriProtocol.RESOURCE.getParentUri("/test"));
        assertEquals("/organization/organization_1",PermissionUriProtocol.RESOURCE.getParentUri("//organization/organization_1/test"));
        assertEquals("/organization/organization_1",PermissionUriProtocol.RESOURCE.getParentUri("/organization/organization_1//test"));

    }

    @Test
    public void removePrefixTest() {
        assertEquals("repo:",PermissionUriProtocol.RESOURCE.getProtocolPrefix());
        assertEquals("attr:",PermissionUriProtocol.ATTRIBUTE.getProtocolPrefix());

    }

}