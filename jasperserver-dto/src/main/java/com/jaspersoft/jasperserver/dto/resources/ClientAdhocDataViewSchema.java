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
package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.resources.domain.AbstractResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ConstantsResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id: $
 */
public class ClientAdhocDataViewSchema {
    private List<ResourceElement> resources;
    private List<PresentationElement> presentation;

    public ClientAdhocDataViewSchema(){}

    public ClientAdhocDataViewSchema(ClientAdhocDataViewSchema source){
        final List<? extends PresentationElement> sourcePresentation = source.getPresentation();
        if(sourcePresentation != null) presentation = new ArrayList<PresentationElement>(sourcePresentation);
        final List<ResourceElement> sourceResources = source.getResources();
        if (sourceResources != null) resources = new ArrayList<ResourceElement>(sourceResources);
    }
    @XmlElementWrapper(name = "resources")
    @XmlElements({
            @XmlElement(name = "group", type = ResourceGroupElement.class),
            @XmlElement(name = "element", type = ResourceSingleElement.class),
    })
    public List<ResourceElement> getResources() {
        return resources;
    }

    public ClientAdhocDataViewSchema setResources(List<ResourceElement> resources) {
        this.resources = resources;
        return this;
    }

    @XmlElementWrapper(name="presentation")
    @XmlElements({
            @XmlElement(name = "group", type = PresentationGroupElement.class),
            @XmlElement(name = "element", type = PresentationSingleElement.class)
    })
    public List<PresentationElement> getPresentation() {
        return presentation;
    }

    public ClientAdhocDataViewSchema setPresentation(List<PresentationElement> presentation) {
        this.presentation = presentation;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientAdhocDataViewSchema)) return false;

        ClientAdhocDataViewSchema schema = (ClientAdhocDataViewSchema) o;

        if (presentation != null ? !presentation.equals(schema.presentation) : schema.presentation != null)
            return false;
        if (resources != null  && schema.resources != null && !equalGroupElements(resources, schema.resources)) return false;

        return true;
    }

    private boolean equalGroupElements(List<? extends ResourceElement> groupElements1, List<? extends ResourceElement> groupElements2) {
        if(groupElements1 == null && groupElements2 == null) return true;
        if(groupElements1 == null || groupElements2 == null) return false;
        if (groupElements1.size() != groupElements2.size())  return false;

        if (!(groupElements1.get(0) instanceof AbstractResourceGroupElement)) {
            return groupElements1.containsAll(groupElements2);
        }

        Comparator<ResourceElement> comparator = new Comparator<ResourceElement>() {
            public int compare(ResourceElement elem1, ResourceElement elem2) {
                return elem1.getName().compareTo(elem2.getName());
            }
        };
        Collections.sort(groupElements1, comparator);
        Collections.sort(groupElements2, comparator);
        boolean result = false;
        for (int i = 0; i < groupElements1.size(); i++) {
            final ResourceElement resourceElement1 = groupElements1.get(i);
            final ResourceElement resourceElement2 = groupElements2.get(i);
            if(resourceElement1.getName().equals(resourceElement2.getName())
                    && resourceElement1.getClass() == resourceElement2.getClass()) {
                if(resourceElement1 instanceof AbstractResourceGroupElement) {
                    result = equalGroupElements(((AbstractResourceGroupElement) resourceElement1).getElements(),
                            ((AbstractResourceGroupElement) resourceElement2).getElements());
                } else if (resourceElement1 instanceof ConstantsResourceGroupElement) {
                    result =  equalGroupElements(((ConstantsResourceGroupElement) resourceElement1).getElements(),
                            ((ConstantsResourceGroupElement) resourceElement2).getElements());
                }
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = resources != null ? sortResourceGroupElement(resources).hashCode() : 0;
        result = 31 * result + (presentation != null ? presentation.hashCode() : 0);
        return result;
    }

    private List<? extends SchemaElement> sortResourceGroupElement(List<? extends SchemaElement> list) {
        Comparator<SchemaElement> comparator = new Comparator<SchemaElement>() {
            public int compare(SchemaElement elem1, SchemaElement elem2) {
                return elem1.getName().compareTo(elem2.getName());
            }
        };
        Collections.sort(list, comparator);

        if (list.get(0) instanceof AbstractResourceGroupElement) {
            List<AbstractResourceGroupElement> castedList = (List<AbstractResourceGroupElement>) list;
            for (AbstractResourceGroupElement element : castedList) {
                sortResourceGroupElement(element.getElements());
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "Schema{" +
                "resources=" + resources +
                ", presentation=" + presentation +
                '}';
    }
}

