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

package com.jaspersoft.jasperserver.dto.resources.domain;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class ConstantsResourceGroupElementTest {

    public static final String SOURCE_NAME = "SourceName";
    public static final String ELEMENT_NAME = "name";
    ConstantsResourceGroupElement sourceElement;
    ConstantsResourceGroupElement clonedElement;

    @Before
    public void setUp() {

        sourceElement = new ConstantsResourceGroupElement()
                .setSourceName(SOURCE_NAME)
                .setName(ELEMENT_NAME)
                .setElements(new ArrayList<ResourceSingleElement>());
    }

    @Test
    public void testCloningConstructor() {

        clonedElement = new ConstantsResourceGroupElement(sourceElement);

        assertTrue(clonedElement.equals(sourceElement));
        assertFalse(sourceElement == clonedElement);
        assertFalse(sourceElement.getElements() == clonedElement.getElements());
        assertNotNull(clonedElement.getName());
        assertEquals(ELEMENT_NAME, clonedElement.getName());
        assertNotNull(clonedElement.getSourceName());
        assertEquals(SOURCE_NAME, clonedElement.getSourceName());

    }
}