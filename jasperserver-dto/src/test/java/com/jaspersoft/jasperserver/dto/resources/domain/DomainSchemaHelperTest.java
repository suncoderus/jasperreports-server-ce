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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class DomainSchemaHelperTest {


    @Test
    public void getDataIslandPathFromGroup_recursionAndMockedGetDataIslandIdFromElement_correctResult(){
        final String expectedId = "expected.id";
        final ArrayList<ResourceElement> clientResources = new ArrayList<ResourceElement>();
        final PresentationSingleElement singleElement = new PresentationSingleElement().setResourcePath(expectedId + ".constantA");
        final PresentationSingleElement constant1 = new PresentationSingleElement().setResourcePath("constants1.constantB");
        final PresentationSingleElement constant2 = new PresentationSingleElement().setResourcePath("constants2.constantC");
        final PresentationGroupElement group = new PresentationGroupElement();
        group.setElements(new ArrayList<PresentationElement>(){{
            add(new PresentationGroupElement().setElements((List) Arrays.asList(
                    new PresentationSingleElement(),
                    constant1,
                    singleElement,
                    constant2,
                    new PresentationSingleElement())));
        }});
        final String result = DomainSchemaHelper.getDataIslandPathFromGroup(group, clientResources, Arrays.asList("constants2", "constants1"));
        assertEquals(result, expectedId);
    }

    @Test
    public void getDataIslandPathFromGroup_constantsOnly_constantResourcePath(){
        final PresentationGroupElement group = new PresentationGroupElement();
        final String expectedId = "constants";
        group.setElements(new ArrayList<PresentationElement>(){{
            add(new PresentationGroupElement().setElements((List)Arrays.asList(
                    new PresentationSingleElement().setResourcePath(expectedId + ".constantA"),
                    new PresentationSingleElement().setResourcePath(expectedId + ".constantB"),
                    new PresentationSingleElement().setResourcePath(expectedId + ".constantC"),
                    new PresentationSingleElement().setResourcePath(expectedId + ".constantD"))));
        }});

        final String result = DomainSchemaHelper.getDataIslandPathFromGroup(group, new ArrayList<ResourceElement>(),
                Arrays.asList("constants"));

        assertEquals(result, expectedId);
    }

    @Test
    public void getDataIslandPathFromElement_elementRefersJoinGroup_joinGroupPath(){
        final String joinGroupName = "someJoinGroup";
        final ArrayList<ResourceElement> clientResources = new ArrayList<ResourceElement>() {{
            add(new JoinResourceGroupElement().setName(joinGroupName));
        }};
        final PresentationSingleElement singleElement = new PresentationSingleElement()
                .setResourcePath(joinGroupName + ".some_table.some_column");
        final String result = DomainSchemaHelper.getDataIslandPathFromElement(singleElement, clientResources);
        assertEquals(result, joinGroupName);
    }

    @Test
    public void getDataIslandPathFromElement_elementRefersNonJoinedElement_substringOfResourcePath(){
        final String dataSourceGroupName = "dataSourceGroup";
        final ArrayList<ResourceElement> clientResources = new ArrayList<ResourceElement>() {{
            add(new ResourceGroupElement().setName(dataSourceGroupName));
        }};
        final String resourcePath = dataSourceGroupName + ".some_schema.some_table.some_column";
        final PresentationSingleElement singleElement = new PresentationSingleElement()
                .setResourcePath(resourcePath);
        final String result = DomainSchemaHelper.getDataIslandPathFromElement(singleElement, clientResources);
        assertEquals(result, resourcePath.substring(0, resourcePath.lastIndexOf(".")));
    }
}
