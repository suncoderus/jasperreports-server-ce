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


import java.util.ArrayList;
import java.util.List;


/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class SchemaTest {

    private Schema schema1;
    private Schema schema2;
    private Schema schema3;

    @Before
    public void setUp() throws Exception {
        // init Presentation field

        // init single elements
        PresentationElement<PresentationSingleElement> presentationSingleElement1 = new PresentationSingleElement()
                .setName("presentationSingleElement1");
        PresentationElement<PresentationSingleElement> presentationSingleElement2 = new PresentationSingleElement().
                setName("presentationSingleElement2");
        PresentationElement<PresentationSingleElement> presentationSingleElement3 = new PresentationSingleElement()
                .setName("presentationSingleElement3");
        PresentationElement<PresentationSingleElement> presentationSingleElement4 = new PresentationSingleElement()
                .setName("presentationSingleElement4");

        List<PresentationElement> presentationSingleElements1 = new ArrayList<PresentationElement>();
        List<PresentationElement> presentationSingleElements2 = new ArrayList<PresentationElement>();

        presentationSingleElements1.add(presentationSingleElement1);
        presentationSingleElements1.add(presentationSingleElement2);
        presentationSingleElements2.add(presentationSingleElement3);
        presentationSingleElements2.add(presentationSingleElement4);

        // init presentation group elements and set their fields  with lists of single elements
        PresentationGroupElement presentationGroupElement2 = new PresentationGroupElement();
        PresentationGroupElement presentationGroupElement3 = new PresentationGroupElement();
        PresentationGroupElement presentationGroupElement1 = new PresentationGroupElement();
        PresentationGroupElement presentationGroupElement4 = new PresentationGroupElement();

        presentationGroupElement1.setElements(presentationSingleElements1);
        presentationGroupElement2.setElements(presentationSingleElements2);
        presentationGroupElement3.setElements(presentationSingleElements1);
        presentationGroupElement4.setElements(presentationSingleElements2);

        // add group presentation elements to two lists with different order of elements
        // to get positive and negative results
        List<PresentationGroupElement> sourcePresentation1 = new ArrayList<PresentationGroupElement>();
        List<PresentationGroupElement> sourcePresentation2 = new ArrayList<PresentationGroupElement>();

        sourcePresentation1.add(presentationGroupElement1);
        sourcePresentation1.add(presentationGroupElement2);
        sourcePresentation1.add(presentationGroupElement3);
        sourcePresentation1.add(presentationGroupElement4);

        sourcePresentation2.add(presentationGroupElement4);
        sourcePresentation2.add(presentationGroupElement2);
        sourcePresentation2.add(presentationGroupElement1);
        sourcePresentation2.add(presentationGroupElement3);

        // init Resources field

        // init single ReferenceElement
        ReferenceElement referenceElement1 = new ReferenceElement().setName("referenceElement1");
        ReferenceElement referenceElement2 = new ReferenceElement().setName("referenceElement2");
        ReferenceElement referenceElement3 = new ReferenceElement().setName("referenceElement3");
        ReferenceElement referenceElement4 = new ReferenceElement().setName("referenceElement4");
        ReferenceElement referenceElement5 = new ReferenceElement().setName("referenceElement5");

        List<SchemaElement> referenceElements = new ArrayList<SchemaElement>();
        referenceElements.add(referenceElement1);
        referenceElements.add(referenceElement2);
        referenceElements.add(referenceElement3);
        referenceElements.add(referenceElement4);
        referenceElements.add(referenceElement5);

        // init JoinResourceGroupElement and set its name and elements with list of single elements
        JoinResourceGroupElement joinResourceGroupElement1 = new JoinResourceGroupElement();
        joinResourceGroupElement1.setElements(referenceElements).setName("joinResourceGroupElement1");

        // init single ResourceMetadataSingleElements
        ResourceMetadataSingleElement resourceSingleElement1 = new ResourceMetadataSingleElement().setName("resourceSingleElement1");
        ResourceMetadataSingleElement resourceSingleElement2 = new ResourceMetadataSingleElement().setName("resourceSingleElement2");
        ResourceMetadataSingleElement resourceSingleElement3 = new ResourceMetadataSingleElement().setName("resourceSingleElement3");
        ResourceMetadataSingleElement resourceSingleElement4 = new ResourceMetadataSingleElement().setName("resourceSingleElement4");
        ResourceMetadataSingleElement resourceSingleElement5 = new ResourceMetadataSingleElement().setName("resourceSingleElement5");

        List<SchemaElement> resourceMetadataSingleElements1 = new ArrayList<SchemaElement>();
        resourceMetadataSingleElements1.add(resourceSingleElement1);
        resourceMetadataSingleElements1.add(resourceSingleElement2);

        List<SchemaElement> resourceMetadataSingleElements2 = new ArrayList<SchemaElement>();
        resourceMetadataSingleElements2.add(resourceSingleElement3);
        resourceMetadataSingleElements2.add(resourceSingleElement4);
        resourceMetadataSingleElements2.add(resourceSingleElement5);

        // init resource group elements and set their fields with lists of single elements
        AbstractResourceGroupElement resourceGroupElement1 = new ResourceGroupElement()
                .setElements(resourceMetadataSingleElements1)
                .setName("resourceGroupElement1");
        AbstractResourceGroupElement resourceGroupElement2 = new ResourceGroupElement()
                .setElements(resourceMetadataSingleElements2)
                .setName("resourceGroupElement2");

        List<SchemaElement> resourceGroupElements1 = new ArrayList<SchemaElement>();
        resourceGroupElements1.add(resourceGroupElement1);
        List<SchemaElement> resourceGroupElements2 = new ArrayList<SchemaElement>();
        resourceGroupElements2.add(resourceGroupElement2);

        // init resource group elements of higher level
        AbstractResourceGroupElement resourceGroupElement3 = new ResourceGroupElement()
                .setElements(resourceGroupElements1)
                .setName("resourceGroupElement3");
        AbstractResourceGroupElement resourceGroupElement4 = new ResourceGroupElement()
                .setElements(resourceGroupElements2)
                .setName("resourceGroupElement4");

        // add group resources elements to two lists with different order of elements for different instances of Schema
        List<ResourceElement> sourceResources1 = new ArrayList<ResourceElement>();
        sourceResources1.add(joinResourceGroupElement1);
        sourceResources1.add(resourceGroupElement3);
        sourceResources1.add(resourceGroupElement4);


        List<ResourceElement> sourceResources2 = new ArrayList<ResourceElement>();
        sourceResources2.add(resourceGroupElement3);
        sourceResources2.add(joinResourceGroupElement1);
        sourceResources2.add(resourceGroupElement4);

        // init schema instance
        schema1 = new Schema();
        schema1.setPresentation(sourcePresentation1);
        schema1.setResources(sourceResources1);

        // init schema instance and set Resource with different order of resources elements
        schema2 = new Schema();
        schema2.setPresentation(sourcePresentation1);
        schema2.setResources(sourceResources2);

        // init schema instance and set Resource with different order of presentation elements
        schema3 = new Schema();
        schema3.setPresentation(sourcePresentation2);
        schema3.setResources(sourceResources1);
    }

    @Test
    public void testEquals() {
        assertTrue(schema1.equals(schema2));
        assertFalse(schema1.equals(schema3));

    }

    @Test
    public void testHashCode() {
        assertEquals(schema1.hashCode(), schema2.hashCode());
        assertFalse(schema1.hashCode() == schema3.hashCode());
    }
}