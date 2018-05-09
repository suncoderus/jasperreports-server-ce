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

import java.util.ArrayList;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttributeEmbeddedContainerTest.createEmbeddedContainer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author askorodumov
 * @version $Id$
 */
public class HypermediaAttributeTest {
    @Before
    public void init() {
        createDifferentAttributes();
    }

    @Test
    public void equals_emptyIdentical_success() {
        HypermediaAttribute attribute1 = new HypermediaAttribute();
        HypermediaAttribute attribute2 = new HypermediaAttribute();

        assertTrue(attribute1.equals(attribute2));
        assertTrue(attribute2.equals(attribute1));
    }

    @Test
    public void hashCode_emptyIdentical_success() {
        HypermediaAttribute attribute1 = new HypermediaAttribute();
        HypermediaAttribute attribute2 = new HypermediaAttribute();

        assertEquals(attribute1.hashCode(), attribute2.hashCode());
    }

    @Test
    public void toString_emptyIdentical_success() {
        HypermediaAttribute attribute1 = new HypermediaAttribute();
        HypermediaAttribute attribute2 = new HypermediaAttribute();

        assertEquals(attribute1.toString(), attribute2.toString());
    }

    @Test
    public void equals_identical_success() {
        HypermediaAttribute attribute1 = createGenericAttribute();
        HypermediaAttribute attribute2 = new HypermediaAttribute(attribute1);

        assertTrue(attribute1.equals(attribute2));
        assertTrue(attribute2.equals(attribute1));
    }

    @Test
    public void hashCode_identical_success() {
        HypermediaAttribute attribute1 = createGenericAttribute();
        HypermediaAttribute attribute2 = new HypermediaAttribute(attribute1);

        assertEquals(attribute1.hashCode(), attribute2.hashCode());
    }

    @Test
    public void toString_identical_success() {
        HypermediaAttribute attribute1 = createGenericAttribute();
        HypermediaAttribute attribute2 = new HypermediaAttribute(attribute1);

        assertEquals(attribute1.toString(), attribute2.toString());
    }

    @Test
    public void equals_notIdentical_success() {
        HypermediaAttribute genericAttribute = createGenericAttribute();

        for (HypermediaAttribute attribute : differentAttributes) {
            assertFalse(genericAttribute.equals(attribute));
            assertFalse(attribute.equals(genericAttribute));
        }
    }

    @Test
    public void hashCode_notIdentical_success() {
        HypermediaAttribute genericAttribute = createGenericAttribute();

        for (HypermediaAttribute attribute : differentAttributes) {
            assertNotEquals(genericAttribute.hashCode(), attribute.hashCode());
        }
    }

    @Test
    public void toString_notIdentical_success() {
        HypermediaAttribute genericAttribute = createGenericAttribute();

        for (HypermediaAttribute attribute : differentAttributes) {
            assertNotEquals(genericAttribute.toString(), attribute.toString());
        }
    }

    private HypermediaAttribute createGenericAttribute() {
        HypermediaAttribute attribute = new HypermediaAttribute();
        attribute.setName("attr1");
        attribute.setValue("attr1-value");
        attribute.setLinks(createLinks("http://link.to/some/resource"));
        attribute.setPermissionMask(32);
        attribute.setEmbedded(createEmbeddedContainer("uri", "recipient", 32));
        attribute.setSecure(true);
        attribute.setInherited(true);
        attribute.setDescription("attr1 desc.");
        attribute.setHolder("tenant:/org1");

        return attribute;
    }

    private List<HypermediaAttribute> differentAttributes = new ArrayList<HypermediaAttribute>();

    private void createDifferentAttributes() {
        HypermediaAttribute genericAttribute = createGenericAttribute();

        HypermediaAttribute attribute = new HypermediaAttribute(createGenericAttribute());
        attribute.setName("attr2");
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setValue("attr2-value");
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setLinks(createLinks("http://link.to/another/resource"));
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setLinks(createLinks(null));
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setLinks(null);
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setPermissionMask(1);
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setEmbedded(createEmbeddedContainer("uri2", "recipient", 32));
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setEmbedded(createEmbeddedContainer(null, "recipient", 32));
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setEmbedded(createEmbeddedContainer("uri", "recipient2", 32));
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setEmbedded(createEmbeddedContainer("uri", null, 32));
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setEmbedded(createEmbeddedContainer("uri", "recipient", 1));
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setEmbedded(null);
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setSecure(false);
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setInherited(false);
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setDescription("attr2 desc.");
        differentAttributes.add(attribute);

        attribute = new HypermediaAttribute(genericAttribute);
        attribute.setHolder("tenant:/org2");
        differentAttributes.add(attribute);
    }

    private HypermediaAttributeLinks createLinks(String link) {
        HypermediaAttributeLinks links = new HypermediaAttributeLinks();
        Link permission = new Link();
        permission.setHref(link);
        links.setPermission(permission);
        return links;
    }
}
