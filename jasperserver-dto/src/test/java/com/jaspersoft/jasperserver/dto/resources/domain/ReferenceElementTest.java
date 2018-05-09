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
public class ReferenceElementTest {

    public static final String ELEMENT_NAME = "name";
    public static final String REFERENCE_PATH = "ReferencePath";
    ReferenceElement sourceElement;
    ReferenceElement clonedElement;

    @Before
    public void setUp() {
        sourceElement = new ReferenceElement()
                .setName(ELEMENT_NAME)
                .setReferencePath(REFERENCE_PATH);
    }

    @Test
    public void testCloningConstructor() {

        clonedElement = new ReferenceElement(sourceElement);

        assertTrue(clonedElement.equals(sourceElement));
        assertFalse(sourceElement == clonedElement);
        assertNotNull(clonedElement.getName());
        assertEquals(ELEMENT_NAME, clonedElement.getName());
        assertNotNull(clonedElement.getReferencePath());
        assertEquals(REFERENCE_PATH, clonedElement.getReferencePath());
    }

}