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
public class PresentationElementTest {


    public static final String ELEMENT_NAME = "name";
    public static final String RESOURCE_PATH = "ResourcePath";
    public static final String LABEL = "Label";
    public static final String LABEL_ID = "LabelId";
    public static final String DESCRIPTION = "Description";
    public static final String DESCRIPTION_ID = "DescriptionId";

    PresentationElement sourceElement;
    PresentationElement clonedElement;

    private static class Builder extends PresentationElement<Builder>{}

    @Before
    public void setUp() {
        sourceElement = new Builder()
                .setName(ELEMENT_NAME)
                .setLabel(LABEL)
                .setLabelId(LABEL_ID)
                .setDescription(DESCRIPTION)
                .setDescriptionId(DESCRIPTION_ID);
    }

    @Test
    public void testCloningConstructor() {

        clonedElement = new PresentationElement(sourceElement);

        assertTrue(clonedElement.equals(sourceElement));
        assertFalse(sourceElement == clonedElement);
        assertNotNull(clonedElement.getName());
        assertEquals(ELEMENT_NAME, clonedElement.getName());
        assertNotNull(clonedElement.getLabel());
        assertEquals(LABEL, clonedElement.getLabel());
        assertNotNull(clonedElement.getLabelId());
        assertEquals(LABEL_ID, clonedElement.getLabelId());
        assertNotNull(clonedElement.getDescription());
        assertEquals(DESCRIPTION, clonedElement.getDescription());
        assertNotNull(clonedElement.getDescriptionId());
        assertEquals(DESCRIPTION_ID, clonedElement.getDescriptionId());
    }


}