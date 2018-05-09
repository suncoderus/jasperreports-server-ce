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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.representation;

import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.HypermediaRepresentation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
@XmlRootElement(name = "resource")
public class ResourceLookupRepresentation extends HypermediaRepresentation{

    private ClientResourceLookup resourceLookup;

    public ResourceLookupRepresentation(ClientResourceLookup resourceLookup) {
        this.resourceLookup = resourceLookup;
    }

    public String getResourceType() {
        return resourceLookup.getResourceType();
    }

    public Integer getVersion() {
        return resourceLookup.getVersion();
    }

    public Integer getPermissionMask() {
        return resourceLookup.getPermissionMask();
    }

    public String getCreationDate() {
        return resourceLookup.getCreationDate();
    }

    public String getUpdateDate() {
        return resourceLookup.getUpdateDate();
    }

    public String getLabel() {
        return resourceLookup.getLabel();
    }

    public String getDescription() {
        return resourceLookup.getDescription();
    }

    public String getUri() {
        return resourceLookup.getUri();
    }

    @XmlTransient
    public ClientResourceLookup getBody(){
        return  resourceLookup;
    }

    @Override
    public int hashCode() {
        return (new HashCodeBuilder()
                .append(this.getUri())
                .append(this.getVersion())
                .append(this.getDescription())
                .append(this.getLabel())
                .append(this.getUpdateDate())
                .append(this.getCreationDate())
                .append(this.getEmbedded().hashCode())
                .append(this.getLinks().hashCode())
        ).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof ResourceLookupRepresentation)) {
            return false;
        }
        ResourceLookupRepresentation that = (ResourceLookupRepresentation)obj;
        return new EqualsBuilder()
                .append(this.getUri(), that.getUri())
                .append(this.getVersion(), that.getVersion())
                .append(this.getDescription(), that.getDescription())
                .append(this.getLabel(), that.getLabel())
                .append(this.getUpdateDate(), that.getUpdateDate())
                .append(this.getCreationDate(), that.getCreationDate())
                .append(this.getEmbedded(), that.getEmbedded())
                .append(this.getLinks(), that.getLinks())
                .isEquals();
    }

}
