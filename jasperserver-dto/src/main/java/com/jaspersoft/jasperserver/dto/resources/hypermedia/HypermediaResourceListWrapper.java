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

package com.jaspersoft.jasperserver.dto.resources.hypermedia;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@XmlRootElement(name = "resources")
public class HypermediaResourceListWrapper {
    private List<HypermediaResourceLookup> resourceLookups;
    private HypermediaResourceLookupLinks links;

    public HypermediaResourceListWrapper(){}

    public HypermediaResourceListWrapper(List<HypermediaResourceLookup> resourceLookups){
        this.resourceLookups = resourceLookups;
    }

    public HypermediaResourceListWrapper(HypermediaResourceListWrapper other) {
        final List<HypermediaResourceLookup> srcResourceLookups = other.getResourceLookups();
        if(srcResourceLookups != null){
            resourceLookups = new ArrayList<HypermediaResourceLookup>(other.getResourceLookups().size());
            for(HypermediaResourceLookup lookup : srcResourceLookups){
                resourceLookups.add(new HypermediaResourceLookup(lookup));
            }
        }
    }

    @XmlElement(name = "resourceLookup")
    public List<HypermediaResourceLookup> getResourceLookups() {
        return resourceLookups;
    }

    public HypermediaResourceListWrapper setResourceLookups(List<HypermediaResourceLookup> resourceLookups) {
        this.resourceLookups = resourceLookups;
        return this;
    }

    @XmlElement(name = "_links")
    public HypermediaResourceLookupLinks getLinks() {
        return links;
    }

    public void setLinks(HypermediaResourceLookupLinks links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HypermediaResourceListWrapper that = (HypermediaResourceListWrapper) o;

        if (resourceLookups != null ? !resourceLookups.equals(that.resourceLookups) : that.resourceLookups != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return resourceLookups != null ? resourceLookups.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "HypermediaResourceListWrapper{" +
                "resourceLookups=" + resourceLookups +

                '}';
    }
}
