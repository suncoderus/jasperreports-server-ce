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
package com.jaspersoft.jasperserver.dto.authority.hypermedia;

import com.jaspersoft.jasperserver.dto.authority.ClientAttribute;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p></p>
 *
 * @author Volodya Sabadosh
 * @version $Id$
 */
@XmlRootElement(name = "attribute")
public class HypermediaAttribute extends ClientAttribute {
    private HypermediaAttributeEmbeddedContainer embedded;
    private HypermediaAttributeLinks links;

    public HypermediaAttribute(ClientAttribute other) {
        super(other);
    }

    public HypermediaAttribute() {
    }

    public HypermediaAttribute(HypermediaAttribute other) {
        super(other);
        if (other.getEmbedded() != null) {
            this.setEmbedded(new HypermediaAttributeEmbeddedContainer(other.getEmbedded()));
        }
        if (other.getLinks() != null) {
            this.setLinks(new HypermediaAttributeLinks(other.getLinks()));
        }
    }

    @XmlElement(name = "_embedded")
    public HypermediaAttributeEmbeddedContainer getEmbedded() {
        return embedded;
    }

    public HypermediaAttribute setEmbedded(HypermediaAttributeEmbeddedContainer embedded) {
        this.embedded = embedded;
        return this;
    }

    @XmlElement(name = "_links")
    public HypermediaAttributeLinks getLinks() {
        return links;
    }

    public HypermediaAttribute setLinks(HypermediaAttributeLinks links) {
        this.links = links;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HypermediaAttribute)) return false;
        if (!super.equals(o)) return false;

        HypermediaAttribute attribute = (HypermediaAttribute) o;

        if (embedded != null ? !embedded.equals(attribute.embedded) : attribute.embedded != null) return false;
        return !(links != null ? !links.equals(attribute.links) : attribute.links != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (embedded != null ? embedded.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "embedded=" + embedded +
                ", links=" + links +
                "}" + super.toString();
    }
}
