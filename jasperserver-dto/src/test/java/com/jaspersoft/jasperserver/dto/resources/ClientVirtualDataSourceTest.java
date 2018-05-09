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

package com.jaspersoft.jasperserver.dto.resources;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ClientVirtualDataSourceTest {
    final ClientVirtualDataSource dataSource1 = new ClientVirtualDataSource(), dataSource2 = new ClientVirtualDataSource();

    @Before
    public void setUp() throws Exception {
        dataSource1.setSubDataSources(Arrays.asList(new ClientSubDataSourceReference().setId("a").setUri("/a"), new ClientSubDataSourceReference().setId("b").setUri("/b")));
        dataSource2.setSubDataSources(Arrays.asList(new ClientSubDataSourceReference().setId("b").setUri("/b"), new ClientSubDataSourceReference().setId("a").setUri("/a")));
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(dataSource1.equals(dataSource2));
        assertTrue(dataSource2.equals(dataSource1));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(dataSource1.hashCode(), dataSource2.hashCode());
    }
}
