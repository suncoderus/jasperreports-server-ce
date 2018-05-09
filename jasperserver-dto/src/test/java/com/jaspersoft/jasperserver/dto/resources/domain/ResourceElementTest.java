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
public class ResourceElementTest {

    public static final String ELEMENT_NAME = "name";
    public static final String SOURCE_NAME = "SourceName";
    ResourceElement<Builder> sourceElement;
    ResourceElement<Builder> clonedElement;

    private static class Builder extends ResourceElement<Builder>{}

    @Before
    public void setUp() {

        sourceElement = new Builder()
                .setName(ELEMENT_NAME)
                .setSourceName(SOURCE_NAME);
    }

    @Test
    public void testCloningConstructor() {

        clonedElement = new ResourceElement<Builder>(sourceElement);

        assertTrue(clonedElement.equals(sourceElement));
        assertFalse(sourceElement == clonedElement);
        assertNotNull(clonedElement.getName());
        assertEquals(ELEMENT_NAME, clonedElement.getName());
        assertNotNull(clonedElement.getSourceName());
        assertEquals(SOURCE_NAME, clonedElement.getSourceName());
    }


}