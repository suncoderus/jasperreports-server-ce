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

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * This class is abstract. Use ResourceGroupElement.Builder if you need to instantiate group itself.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public abstract class AbstractResourceGroupElement<T extends AbstractResourceGroupElement<T>> extends ResourceElement<T> {
    @Valid
    private List<SchemaElement> elements;
    @Valid
    private ClientExpressionContainer filterExpression;

    public AbstractResourceGroupElement(){}
    public AbstractResourceGroupElement(AbstractResourceGroupElement<T> source) {
        super(source);
        final ClientExpressionContainer sourceFilterExpression = source.getFilterExpression();
        if(sourceFilterExpression != null){
            filterExpression = new ClientExpressionContainer(sourceFilterExpression);
        }
        final List<SchemaElement> sourceElements = source.getElements();
        if(sourceElements != null){
            List<SchemaElement> clonedElements = new ArrayList<SchemaElement>(sourceElements.size());
            for (SchemaElement sourceElement : sourceElements) {
                clonedElements.add(sourceElement.deepClone());
            }
            elements = clonedElements;
        }
    }
    @XmlElementWrapper(name = "elements")
    @XmlElements({
            @XmlElement(name = "reference", type = ReferenceElement.class),
            @XmlElement(name = "group", type = ResourceGroupElement.class),
            @XmlElement(name = "queryGroup", type = QueryResourceGroupElement.class),
            // here different types of resources are marked as 'element'. It's not by mistake.
            // ResourceMetadataSingleElement is used in metadata service only. It doesn't come from the client. So, no conflict here.
            @XmlElement(name = "element", type = ResourceSingleElement.class),
            @XmlElement(name = "element", type = ResourceMetadataSingleElement.class)
    })

    public List<SchemaElement> getElements() {
        return elements;
    }

    public T setElements(List<SchemaElement> elements) {
        this.elements = elements;
        return (T) this;
    }

    public ClientExpressionContainer getFilterExpression() {
        return filterExpression;
    }

    public T setFilterExpression(ClientExpressionContainer filterExpression) {
        this.filterExpression = filterExpression;
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractResourceGroupElement)) return false;
        if (!super.equals(o)) return false;

        AbstractResourceGroupElement that = (AbstractResourceGroupElement) o;

        if (elements != null ? !elements.equals(that.elements) : that.elements != null) return false;
        if (filterExpression != null ? !filterExpression.equals(that.filterExpression) : that.filterExpression != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (elements != null ? elements.hashCode() : 0);
        result = 31 * result + (filterExpression != null ? filterExpression.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AbstractResourceGroupElement{" +
                "elements=" + elements +
                ", filterExpression='" + filterExpression + '\'' +
                "} " + super.toString();
    }
}
