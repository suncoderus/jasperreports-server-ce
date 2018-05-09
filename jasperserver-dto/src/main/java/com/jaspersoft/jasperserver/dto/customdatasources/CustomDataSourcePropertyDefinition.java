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
package com.jaspersoft.jasperserver.dto.customdatasources;

import com.jaspersoft.jasperserver.dto.resources.ClientProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "propertyDefinition")
public class CustomDataSourcePropertyDefinition {
    private String name;
    private String label;
    private String defaultValue;
    private List<ClientProperty> properties;

    public CustomDataSourcePropertyDefinition() {
    }

    public CustomDataSourcePropertyDefinition(CustomDataSourcePropertyDefinition source) {
        this.name = source.getName();
        this.defaultValue = source.getDefaultValue();
        this.label = source.getLabel();
        List<ClientProperty> sourceProperties = source.getProperties();
        if (sourceProperties != null) {
            this.properties = new ArrayList<ClientProperty>();
            for(ClientProperty property : sourceProperties){
                this.properties.add(new ClientProperty(property));
            }
        }
    }
    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    public List<ClientProperty> getProperties() {
        return properties;
    }

    public CustomDataSourcePropertyDefinition setProperties(List<ClientProperty> properties) {
        this.properties = properties;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public CustomDataSourcePropertyDefinition setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomDataSourcePropertyDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public CustomDataSourcePropertyDefinition setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomDataSourcePropertyDefinition that = (CustomDataSourcePropertyDefinition) o;

        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CustomDataSourcePropertyDefinition{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                "} " + super.toString();
    }
}
