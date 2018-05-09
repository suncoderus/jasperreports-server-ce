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
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class DomainSchemaHelper {
    public static <T extends SchemaElement> T findResourceElement(String resourcePath, List<ResourceElement> resources){
        SchemaElement result = null;
        List<? extends SchemaElement> currentElements = resources;
        if(resources != null && resourcePath != null){
            final String[] pathTokens = resourcePath.split("\\.");
            for(int i = 0; i < pathTokens.length; i++){
                String name = pathTokens[i];
                SchemaElement currentResult = null;
                for(SchemaElement element : currentElements){
                    if(name.equals(element.getName())){
                        currentResult = element;
                        break;
                    }
                }
                if(i == pathTokens.length - 1){
                    result = currentResult;
                } else if(currentResult instanceof AbstractResourceGroupElement){
                    currentElements = ((AbstractResourceGroupElement) currentResult).getElements();
                } else if(currentResult instanceof ConstantsResourceGroupElement){
                    currentElements = ((ConstantsResourceGroupElement) currentResult).getElements();
                } else if(currentResult instanceof ReferenceElement){
                    SchemaElement referenced = findResourceElement(((ReferenceElement) currentResult).getReferencePath(), resources);
                    if(referenced != null && referenced instanceof AbstractResourceGroupElement){
                        currentElements = ((AbstractResourceGroupElement) referenced).getElements();
                    }
                } else {
                    // not found
                    break;
                }
            }
        }
        return (T)result;
    }

    public static String getDataIslandPathFromElement(PresentationSingleElement element, List<ResourceElement> clientResources){
        // data island ID is based on particular item resource path.
        String result = null;
        if(element != null && element.getResourcePath() != null){
            final String resourcePath = element.getResourcePath();
            final String firstLevelResourcePath = resourcePath.substring(0, resourcePath.indexOf("."));
            if(findResourceElement(firstLevelResourcePath, clientResources) instanceof JoinResourceGroupElement){
                // this is an item from join tree. Use join group path as data island ID then
                result = firstLevelResourcePath;
            } else {
                // it's an item from not joined table. Use table path as data island ID in this case.
                result = resourcePath.substring(0, resourcePath.lastIndexOf("."));
            }
        }
        return result;
    }

    public static String getDataIslandPathFromGroup(PresentationGroupElement group, List<ResourceElement> clientResources,
            List<String> constantGroupNames){
        // Data island ID is taken from resource path, but PresentationGroupElement doesn't have resourcePath.
        // Let's iterate over elements tree to find any PresentationSingleElement and take data island ID from it.
        String result = null;
        if(group != null){
            final List<PresentationElement> elements = group.getElements();
            for (PresentationElement element : elements) {
                if(element instanceof PresentationGroupElement){
                    // it's group. Proceed with recursive call
                    result = getDataIslandPathFromGroup((PresentationGroupElement) element, clientResources,
                            constantGroupNames);
                } else if(element instanceof PresentationSingleElement){
                    // it's single element. Try to take data island ID from it
                    final String dataIslandPathCandidate =
                            getDataIslandPathFromElement((PresentationSingleElement) element, clientResources);
                    // if current element points to constant, then we have to ignore it, because constants may
                    // participate in multiple data islands without being joined
                    if(!constantGroupNames.contains(dataIslandPathCandidate)) {
                        // it's not constant, let's use it as a result
                        result = dataIslandPathCandidate;
                    }
                }
                if(result != null){
                    // data island ID is found. Stop iterating.
                    break;
                }
            }
        }
        if(result == null && !constantGroupNames.isEmpty()){
            // it may be a data island with constants only. Let's try to find it's path by calling same method,
            // but without constant groups collection. In this case reference to constants groups
            // will not be ignored
            result = getDataIslandPathFromGroup(group, clientResources, new ArrayList<String>());
        }
        return result;
    }
}
