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

package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;

import static org.junit.Assert.assertEquals;

/**
 * Tests ResourceLookupImpl class implementation.
 *
 * @author Yuriy Plakosh
 */
public class ResourceLookupImplTest extends UnitilsJUnit4 {
    @TestedObject
    private ResourceLookupImpl resourceLookup;

    @Test
    public void setURIString() {
        resourceLookup.setURIString("/organization/test_folder/test_resource1");
        assertEquals("Wrong parent folder URI", "/organization/test_folder", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource1", resourceLookup.getName());

        resourceLookup.setURIString("/organization/test_resource2");
        assertEquals("Wrong parent folder URI", "/organization", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource2", resourceLookup.getName());

        resourceLookup.setURIString("/test_resource3");
        assertEquals("Wrong parent folder URI", "/", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource3", resourceLookup.getName());

        resourceLookup.setURIString("test_resource3");
        assertEquals("Wrong parent folder URI", null, resourceLookup.getParentFolder());
        assertEquals("Wrong name", null, resourceLookup.getName());

        //noinspection NullableProblems
        resourceLookup.setURIString(null);
        assertEquals("Wrong parent folder URI", null, resourceLookup.getParentFolder());
        assertEquals("Wrong name", null, resourceLookup.getName());
    }
}
