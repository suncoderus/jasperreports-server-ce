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
public class PresentationElement<T extends PresentationElement<T>> extends SchemaElement<T> {
    private String label;
    private String labelId;
    private String description;
    private String descriptionId;

    public PresentationElement() {
    }

    public PresentationElement(PresentationElement source) {
        super(source);
        description = source.getDescription();
        descriptionId = source.getDescriptionId();
        label = source.getLabel();
        labelId = source.getLabelId();
    }

    public String getLabelId() {
        return labelId;
    }

    public T setLabelId(String labelId) {
        this.labelId = labelId;
        return (T) this;
    }

    public String getDescriptionId() {
        return descriptionId;
    }

    public T setDescriptionId(String descriptionId) {
        this.descriptionId = descriptionId;
        return (T) this;
    }

    public String getLabel() {
        return label;
    }

    public T setLabel(String label) {
        this.label = label;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public T setDescription(String description) {
        this.description = description;
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PresentationElement)) return false;
        if (!super.equals(o)) return false;

        PresentationElement that = (PresentationElement) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (descriptionId != null ? !descriptionId.equals(that.descriptionId) : that.descriptionId != null)
            return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (labelId != null ? !labelId.equals(that.labelId) : that.labelId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (labelId != null ? labelId.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (descriptionId != null ? descriptionId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PresentationElement{" +
                "label='" + label + '\'' +
                ", labelId='" + labelId + '\'' +
                ", description='" + description + '\'' +
                ", descriptionId='" + descriptionId + '\'' +
                "} " + super.toString();
    }
}
