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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class QueryResourceGroupElementTest {

    public static final String ELEMENT_NAME = "name";
    public static final ClientExpressionContainer FILTER_EXPRESSION = new ClientExpressionContainer().setString("FilterExpression");
    public static final String QUERY = "query";
    public static final String SOURCE_NAME = "SourceName";
    QueryResourceGroupElement sourceElement;
    QueryResourceGroupElement clonedElement;

    @Before
    public void setUp() {
        sourceElement = new QueryResourceGroupElement()
                .setName(ELEMENT_NAME)
                .setElements(new ArrayList<SchemaElement>())
                .setFilterExpression(FILTER_EXPRESSION)
                .setQuery(QUERY)
                .setSourceName(SOURCE_NAME);

    }

    @Test
    public void testCloningConstructor() {

        clonedElement = new QueryResourceGroupElement(sourceElement);

        assertTrue(clonedElement.equals(sourceElement));
        assertFalse(sourceElement == clonedElement);
        assertFalse(sourceElement.getElements() == clonedElement.getElements());
        assertNotNull(clonedElement.getName());
        assertEquals(ELEMENT_NAME, clonedElement.getName());
        assertNotNull(clonedElement.getFilterExpression());
        assertEquals(FILTER_EXPRESSION, clonedElement.getFilterExpression());
        assertNotNull(clonedElement.getQuery());
        assertEquals(QUERY, clonedElement.getQuery());
        assertNotNull(clonedElement.getSourceName());
        assertEquals(SOURCE_NAME, clonedElement.getSourceName());

    }
}