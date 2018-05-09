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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
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
public class ResourceMetadataSingleElementTest {

    public static final String SOURCE_NAME = "SourceName";
    public static final String ELEMENT_NAME = "name";
    public static final String TYPE = "Type";
    public static final ClientExpressionContainer EXPRESSION = new ClientExpressionContainer().setString("Expression");
    public static final String REFERENCE = "Reference";
    ResourceMetadataSingleElement sourceElement;
    ResourceMetadataSingleElement clonedElement;

    @Before
    public void setUp() {

        sourceElement = new ResourceMetadataSingleElement()
                .setType(TYPE)
                .setExpression(EXPRESSION)
                .setSourceName(SOURCE_NAME)
                .setName(ELEMENT_NAME)
                .setIsIdentifier(true)
                .setReferenceTo(REFERENCE);
    }

    @Test
    public void testCloningConstructor() {

        clonedElement = new ResourceMetadataSingleElement(sourceElement);

        assertTrue(clonedElement.equals(sourceElement));
        assertFalse(sourceElement == clonedElement);
        assertNotNull(clonedElement.getName());
        assertEquals(ELEMENT_NAME, clonedElement.getName());
        assertNotNull(clonedElement.getSourceName());
        assertEquals(SOURCE_NAME, clonedElement.getSourceName());
        assertNotNull(clonedElement.getExpression());
        assertEquals(EXPRESSION, clonedElement.getExpression());
        assertNotNull(clonedElement.getType());
        assertEquals(TYPE, clonedElement.getType());
        assertNotNull(clonedElement.getIsIdentifier());
        assertEquals(Boolean.TRUE, clonedElement.getIsIdentifier());
        assertNotNull(clonedElement.getReferenceTo());
        assertEquals(REFERENCE, clonedElement.getReferenceTo());

    }
}