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

package com.jaspersoft.jasperserver.dto.authority.hypermedia;

import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author askorodumov
 * @version $Id$
 */
public class HypermediaAttributeEmbeddedContainerTest {

    @Before
    public void init() {
        createDifferentEmbeddedContainers();
    }

    @Test
    public void equals_emptyIdentical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = new HypermediaAttributeEmbeddedContainer();
        HypermediaAttributeEmbeddedContainer embeddedContainer2 = new HypermediaAttributeEmbeddedContainer();

        assertTrue(embeddedContainer1.equals(embeddedContainer2));
        assertTrue(embeddedContainer2.equals(embeddedContainer1));
    }

    @Test
    public void hashCode_emptyIdentical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = new HypermediaAttributeEmbeddedContainer();
        HypermediaAttributeEmbeddedContainer embeddedContainer2 = new HypermediaAttributeEmbeddedContainer();

        assertEquals(embeddedContainer1.hashCode(), embeddedContainer2.hashCode());
    }

    @Test
    public void toString_emptyIdentical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = new HypermediaAttributeEmbeddedContainer();
        HypermediaAttributeEmbeddedContainer embeddedContainer2 = new HypermediaAttributeEmbeddedContainer();

        assertEquals(embeddedContainer1.toString(), embeddedContainer2.toString());
    }

    @Test
    public void equals_identical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = createEmbeddedContainer("uri1", "recipient1", 1);
        HypermediaAttributeEmbeddedContainer embeddedContainer2 = new HypermediaAttributeEmbeddedContainer(embeddedContainer1);

        assertTrue(embeddedContainer1.equals(embeddedContainer2));
        assertTrue(embeddedContainer2.equals(embeddedContainer1));
    }

    @Test
    public void hashCode_identical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = createEmbeddedContainer("uri1", "recipient1", 1);
        HypermediaAttributeEmbeddedContainer embeddedContainer2 = new HypermediaAttributeEmbeddedContainer(embeddedContainer1);

        assertEquals(embeddedContainer1.hashCode(), embeddedContainer2.hashCode());
    }

    @Test
    public void toString_identical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = createEmbeddedContainer("uri1", "recipient1", 1);
        HypermediaAttributeEmbeddedContainer embeddedContainer2 = new HypermediaAttributeEmbeddedContainer(embeddedContainer1);

        assertEquals(embeddedContainer1.toString(), embeddedContainer2.toString());
    }

    @Test
    public void equals_notIdentical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = createEmbeddedContainer("uri1", "recipient1", 1);

        for (HypermediaAttributeEmbeddedContainer embeddedContainer2 : differentEmbeddedContainers) {
            assertFalse(embeddedContainer1.equals(embeddedContainer2));
            assertFalse(embeddedContainer2.equals(embeddedContainer1));
        }
    }

    @Test
    public void hashCode_notIdentical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = createEmbeddedContainer("uri1", "recipient1", 1);

        for (HypermediaAttributeEmbeddedContainer embeddedContainer2 : differentEmbeddedContainers) {
            assertNotEquals(embeddedContainer1.hashCode(), embeddedContainer2.hashCode());
        }
    }

    @Test
    public void toString_notIdentical_success() {
        HypermediaAttributeEmbeddedContainer embeddedContainer1 = createEmbeddedContainer("uri1", "recipient1", 1);

        for (HypermediaAttributeEmbeddedContainer embeddedContainer2 : differentEmbeddedContainers) {
            assertNotEquals(embeddedContainer1.toString(), embeddedContainer2.toString());
        }
    }

    private static AtomicInteger counter = new AtomicInteger(0);

    public static HypermediaAttributeEmbeddedContainer createEmbeddedContainer(String uri, String recipient, int mask) {
        HypermediaAttributeEmbeddedContainer embedded = new HypermediaAttributeEmbeddedContainer();

        List<RepositoryPermission> permissionList;
        int listType = HypermediaAttributeEmbeddedContainerTest.counter.incrementAndGet() % 5;

        // try different types of the java.util.List interface
        switch (listType) {
            case 0:
                permissionList = new ArrayList<RepositoryPermission>();
                permissionList.add(new RepositoryPermission(uri, recipient, mask));
                break;
            case 1:
                permissionList = new LinkedList<RepositoryPermission>();
                permissionList.add(new RepositoryPermission(uri, recipient, mask));
                break;
            case 2:
                permissionList = new CopyOnWriteArrayList<RepositoryPermission>();
                permissionList.add(new RepositoryPermission(uri, recipient, mask));
                break;
            case 3:
                permissionList = Arrays.asList(new RepositoryPermission(uri, recipient, mask));
                break;
            default:
                permissionList = Collections.singletonList(new RepositoryPermission(uri, recipient, mask));
                break;
        }

        embedded.setRepositoryPermissions(permissionList);
        return embedded;
    }

    private List<HypermediaAttributeEmbeddedContainer>
            differentEmbeddedContainers = new ArrayList<HypermediaAttributeEmbeddedContainer>();

    private void createDifferentEmbeddedContainers() {
        differentEmbeddedContainers.add(createEmbeddedContainer("uri2", "recipient1", 1));
        differentEmbeddedContainers.add(createEmbeddedContainer("uri1", "recipient2", 1));
        differentEmbeddedContainers.add(createEmbeddedContainer("uri1", "recipient1", 2));
        differentEmbeddedContainers.add(createEmbeddedContainer(null, "recipient1", 1));
        differentEmbeddedContainers.add(createEmbeddedContainer("uri1", null, 1));
        differentEmbeddedContainers.add(createEmbeddedContainer(null, null, 1));
    }
}
