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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author askorodumov
 * @version $Id$
 */
public class HypermediaAttributeLinksTest {

    @Test
    public void equals_emptyIdentical_success() {
        HypermediaAttributeLinks attributeLinks1 = new HypermediaAttributeLinks();
        HypermediaAttributeLinks attributeLinks2 = new HypermediaAttributeLinks();

        assertTrue(attributeLinks1.equals(attributeLinks2));
        assertTrue(attributeLinks2.equals(attributeLinks1));
    }

    @Test
    public void hashCode_emptyIdentical_success() {
        HypermediaAttributeLinks attributeLinks1 = new HypermediaAttributeLinks();
        HypermediaAttributeLinks attributeLinks2 = new HypermediaAttributeLinks();

        assertEquals(attributeLinks1.hashCode(), attributeLinks2.hashCode());
    }

    @Test
    public void toString_emptyIdentical_success() {
        HypermediaAttributeLinks attributeLinks1 = new HypermediaAttributeLinks();
        HypermediaAttributeLinks attributeLinks2 = new HypermediaAttributeLinks();

        assertEquals(attributeLinks1.toString(), attributeLinks2.toString());
    }

    @Test
    public void equals_identical_success() {
        HypermediaAttributeLinks attributeLinks1 = createAttributeLinks("href1");
        HypermediaAttributeLinks attributeLinks2 = new HypermediaAttributeLinks(attributeLinks1);

        assertTrue(attributeLinks1.equals(attributeLinks2));
        assertTrue(attributeLinks2.equals(attributeLinks1));
    }

    @Test
    public void hashCode_identical_success() {
        HypermediaAttributeLinks attributeLinks1 = createAttributeLinks("href1");
        HypermediaAttributeLinks attributeLinks2 = new HypermediaAttributeLinks(attributeLinks1);

        assertEquals(attributeLinks1.hashCode(), attributeLinks2.hashCode());
    }

    @Test
    public void toString_identical_success() {
        HypermediaAttributeLinks attributeLinks1 = createAttributeLinks("href1");
        HypermediaAttributeLinks attributeLinks2 = new HypermediaAttributeLinks(attributeLinks1);

        assertEquals(attributeLinks1.toString(), attributeLinks2.toString());
    }

    @Test
    public void equals_notIdentical_success() {
        HypermediaAttributeLinks attributeLinks1 = createAttributeLinks("href1");
        HypermediaAttributeLinks attributeLinks2 = createAttributeLinks("href2");
        HypermediaAttributeLinks attributeLinks3 = createAttributeLinks(null);

        assertFalse(attributeLinks1.equals(attributeLinks2));
        assertFalse(attributeLinks2.equals(attributeLinks1));

        assertFalse(attributeLinks1.equals(attributeLinks3));
        assertFalse(attributeLinks3.equals(attributeLinks1));
    }

    @Test
    public void hashCode_notIdentical_success() {
        HypermediaAttributeLinks attributeLinks1 = createAttributeLinks("href1");
        HypermediaAttributeLinks attributeLinks2 = createAttributeLinks("href2");
        HypermediaAttributeLinks attributeLinks3 = createAttributeLinks(null);

        assertNotEquals(attributeLinks1.hashCode(), attributeLinks2.hashCode());
        assertNotEquals(attributeLinks1.hashCode(), attributeLinks3.hashCode());
    }

    @Test
    public void toString_notIdentical_success() {
        HypermediaAttributeLinks attributeLinks1 = createAttributeLinks("href1");
        HypermediaAttributeLinks attributeLinks2 = createAttributeLinks("href2");
        HypermediaAttributeLinks attributeLinks3 = createAttributeLinks(null);

        assertNotEquals(attributeLinks1.toString(), attributeLinks2.toString());
        assertNotEquals(attributeLinks1.toString(), attributeLinks3.toString());
    }

    private HypermediaAttributeLinks createAttributeLinks(String href) {
        HypermediaAttributeLinks links = new HypermediaAttributeLinks();
        Link link = new Link();
        link.setHref(href);
        links.setPermission(link);
        return links;
    }
}
