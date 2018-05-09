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

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ResourceMetadataSingleElement extends AbstractResourceSingleElement<ResourceMetadataSingleElement> {
    private Boolean isIdentifier;
    private String referenceTo;

    public ResourceMetadataSingleElement(){}

    public ResourceMetadataSingleElement(ResourceMetadataSingleElement source){
        super(source);
        isIdentifier = source.getIsIdentifier();
        referenceTo = source.getReferenceTo();
    }

    public Boolean getIsIdentifier() {
        return isIdentifier;
    }

    public ResourceMetadataSingleElement setIsIdentifier(Boolean isIdentifier) {
        this.isIdentifier = isIdentifier;
        return this;
    }

    public String getReferenceTo() {
        return referenceTo;
    }

    public ResourceMetadataSingleElement setReferenceTo(String referenceTo) {
        this.referenceTo = referenceTo;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceMetadataSingleElement)) return false;
        if (!super.equals(o)) return false;

        ResourceMetadataSingleElement that = (ResourceMetadataSingleElement) o;

        if (isIdentifier != null ? !isIdentifier.equals(that.isIdentifier) : that.isIdentifier != null) return false;
        if (referenceTo != null ? !referenceTo.equals(that.referenceTo) : that.referenceTo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isIdentifier != null ? isIdentifier.hashCode() : 0);
        result = 31 * result + (referenceTo != null ? referenceTo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResourceMetadataSingleElement{" +
                "isIdentifier=" + isIdentifier +
                ", referenceTo='" + referenceTo + '\'' +
                "} " + super.toString();
    }
}
