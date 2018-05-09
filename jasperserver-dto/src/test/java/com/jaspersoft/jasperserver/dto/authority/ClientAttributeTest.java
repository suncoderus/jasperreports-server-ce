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

package com.jaspersoft.jasperserver.dto.authority;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author askorodumov
 * @version $Id$
 */
public class ClientAttributeTest {
    @Before
    public void init() {
        createDifferentAttributes();
    }

    @Test
    public void equals_emptyIdentical_success() {
        ClientAttribute attribute1 = new ClientAttribute();
        ClientAttribute attribute2 = new ClientAttribute();

        assertTrue(attribute1.equals(attribute2));
        assertTrue(attribute2.equals(attribute1));
    }

    @Test
    public void hashCode_emptyIdentical_success() {
        ClientAttribute attribute1 = new ClientAttribute();
        ClientAttribute attribute2 = new ClientAttribute();

        assertEquals(attribute1.hashCode(), attribute2.hashCode());
    }

    @Test
    public void toString_emptyIdentical_success() {
        ClientAttribute attribute1 = new ClientAttribute();
        ClientAttribute attribute2 = new ClientAttribute();

        assertEquals(attribute1.toString(), attribute2.toString());
    }

    @Test
    public void equals_identical_success() {
        ClientAttribute attribute1 = createGenericAttribute();
        ClientAttribute attribute2 = new ClientAttribute(attribute1);

        assertTrue(attribute1.equals(attribute2));
        assertTrue(attribute2.equals(attribute1));
    }

    @Test
    public void hashCode_identical_success() {
        ClientAttribute attribute1 = createGenericAttribute();
        ClientAttribute attribute2 = new ClientAttribute(attribute1);

        assertEquals(attribute1.hashCode(), attribute2.hashCode());
    }

    @Test
    public void toString_identical_success() {
        ClientAttribute attribute1 = createGenericAttribute();
        ClientAttribute attribute2 = new ClientAttribute(attribute1);

        assertEquals(attribute1.toString(), attribute2.toString());
    }

    @Test
    public void equals_notIdentical_success() {
        ClientAttribute genericAttribute = createGenericAttribute();

        for (ClientAttribute attribute : differentAttributes) {
            assertFalse(genericAttribute.equals(attribute));
            assertFalse(attribute.equals(genericAttribute));
        }
    }

    @Test
    public void hashCode_notIdentical_success() {
        ClientAttribute genericAttribute = createGenericAttribute();

        for (ClientAttribute attribute : differentAttributes) {
            assertNotEquals(genericAttribute.hashCode(), attribute.hashCode());
        }
    }

    @Test
    public void toString_notIdentical_success() {
        ClientAttribute genericAttribute = createGenericAttribute();

        for (ClientAttribute attribute : differentAttributes) {
            assertNotEquals(genericAttribute.toString(), attribute.toString());
        }
    }

    private ClientAttribute createGenericAttribute() {
        ClientAttribute attribute = new ClientAttribute();
        attribute.setName("attr1");
        attribute.setValue("attr1-value");
        attribute.setPermissionMask(32);
        attribute.setSecure(true);
        attribute.setInherited(true);
        attribute.setDescription("attr1 desc.");
        attribute.setHolder("tenant:/org1");

        return attribute;
    }

    private List<ClientAttribute> differentAttributes = new ArrayList<ClientAttribute>();

    private void createDifferentAttributes() {
        ClientAttribute genericAttribute = createGenericAttribute();

        differentAttributes.add(new ClientAttribute(genericAttribute).setName("attr2"));
        differentAttributes.add(new ClientAttribute(genericAttribute).setName(null));
        differentAttributes.add(new ClientAttribute(genericAttribute).setValue("attr2-value"));
        differentAttributes.add(new ClientAttribute(genericAttribute).setValue(null));
        differentAttributes.add(new ClientAttribute(genericAttribute).setPermissionMask(1));
        differentAttributes.add(new ClientAttribute(genericAttribute).setSecure(false));
        differentAttributes.add(new ClientAttribute(genericAttribute).setInherited(false));
        differentAttributes.add(new ClientAttribute(genericAttribute).setDescription("attr2 desc."));
        differentAttributes.add(new ClientAttribute(genericAttribute).setDescription(null));
        differentAttributes.add(new ClientAttribute(genericAttribute).setHolder("tenant:/org2"));
        differentAttributes.add(new ClientAttribute(genericAttribute).setHolder(null));
    }
}
