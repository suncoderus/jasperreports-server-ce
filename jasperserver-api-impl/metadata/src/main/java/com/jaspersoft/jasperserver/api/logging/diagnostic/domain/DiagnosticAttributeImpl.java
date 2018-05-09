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
package com.jaspersoft.jasperserver.api.logging.diagnostic.domain;

/**
 * @author vsabadosh
 */
public class DiagnosticAttributeImpl implements DiagnosticAttribute {

    private String attributeName;

    private String attributeType;

    private String attributeDescription;

    public DiagnosticAttributeImpl(String attributeName, String attributeType, String attributeDescription) {
        setAttributeName(attributeName);
        setAttributeType(attributeType);
        setAttributeDescription(attributeDescription);
    }

    @Override
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public void setAttributeDescription(String attributeDescription) {
        this.attributeDescription =attributeDescription;
    }

    @Override
    public void setAttributeType(String attributeType) {
        this.attributeType =attributeType;
    }

    @Override
    public String getAttributeName() {
        return this.attributeName;
    }

    @Override
    public String getAttributeType() {
        return this.attributeType;
    }


    @Override
    public String getAttributeDescription() {
        return this.attributeDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiagnosticAttributeImpl that = (DiagnosticAttributeImpl) o;

        if (attributeName != null ? !attributeName.equals(that.attributeName) : that.attributeName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return attributeName != null ? attributeName.hashCode() : 0;
    }

}
