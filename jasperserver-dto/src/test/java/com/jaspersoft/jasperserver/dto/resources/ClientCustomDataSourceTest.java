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
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ClientCustomDataSourceTest {
    final ClientCustomDataSource dataSource1 = new ClientCustomDataSource(), dataSource2 = new ClientCustomDataSource();
    final ClientProperty property1 = new ClientProperty("a", "b"), property2 = new ClientProperty("c", "d");

    @Before
    public void init(){
        dataSource1.setProperties(Arrays.asList(property1, property2));
        dataSource2.setProperties(Arrays.asList(property2, property1));
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

    @Test
    public void cloeableConstuctorClonesResources(){
        final ClientCustomDataSource source = new ClientCustomDataSource();
        final HashMap<String, ClientReferenceableFile> resources = new HashMap<String, ClientReferenceableFile>();
        resources.put("resource1", new ClientReference("/reference1"));
        ClientFile file = new ClientFile();
        file.setType(ClientFile.FileType.css);
        resources.put("resource2", file);
        resources.put("resource3", new ClientReference("/reference2"));
        file = new ClientFile();
        file.setType(ClientFile.FileType.csv);
        resources.put("resource4", file);
        source.setResources(resources);
        final Map<String, ClientReferenceableFile> resourcesClone = new ClientCustomDataSource(source).getResources();
        assertNotNull(resourcesClone);
        assertNotSame(resourcesClone, resources);
        assertEquals(resourcesClone.size(), resources.size());
        for(String key : resourcesClone.keySet()){
            final ClientReferenceableFile clone = resourcesClone.get(key);
            final ClientReferenceableFile original = resources.get(key);
            assertNotNull(clone);
            assertNotNull(original);
            assertTrue(clone.equals(original));
            assertNotSame(clone, original);
        }
    }
}
