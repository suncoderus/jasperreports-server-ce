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
@XmlRootElement(name = "definition")
public class ClientCustomDataSourceDefinition {
    private String name;
    private List<String> queryTypes;
    private List<CustomDataSourcePropertyDefinition> propertyDefinitions;
    private Boolean testable;
    public ClientCustomDataSourceDefinition(){
    }
    public ClientCustomDataSourceDefinition(ClientCustomDataSourceDefinition source){
        this.name = source.getName();
        this.queryTypes = new ArrayList<String>(source.getQueryTypes());
        final List<CustomDataSourcePropertyDefinition> sourcePropertyDefinitions = source.getPropertyDefinitions();
        if(sourcePropertyDefinitions != null && !sourcePropertyDefinitions.isEmpty()){
            this.propertyDefinitions = new ArrayList<CustomDataSourcePropertyDefinition>();
            for(CustomDataSourcePropertyDefinition currentDefinition : sourcePropertyDefinitions){
                this.propertyDefinitions.add(new CustomDataSourcePropertyDefinition(currentDefinition));
            }
        }
        if(source.getTestable() != null){
            this.testable = source.getTestable();
        }
    }

    public Boolean getTestable() {
        return testable;
    }

    public ClientCustomDataSourceDefinition setTestable(Boolean testable) {
        this.testable = testable;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClientCustomDataSourceDefinition setName(String name) {
        this.name = name;
        return this;
    }

    @XmlElementWrapper(name = "queryTypes")
    @XmlElement(name = "queryType")
    public List<String> getQueryTypes() {
        return queryTypes;
    }

    public ClientCustomDataSourceDefinition setQueryTypes(List<String> queryTypes) {
        this.queryTypes = queryTypes;
        return this;
    }

    @XmlElementWrapper(name = "propertyDefinitions")
    @XmlElement(name = "propertyDefinition")
    public List<CustomDataSourcePropertyDefinition> getPropertyDefinitions() {
        return propertyDefinitions;
    }

    public ClientCustomDataSourceDefinition setPropertyDefinitions(List<CustomDataSourcePropertyDefinition> propertyDefinitions) {
        this.propertyDefinitions = propertyDefinitions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientCustomDataSourceDefinition that = (ClientCustomDataSourceDefinition) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (propertyDefinitions != null ? !propertyDefinitions.equals(that.propertyDefinitions) : that.propertyDefinitions != null)
            return false;
        if (queryTypes != null ? !queryTypes.equals(that.queryTypes) : that.queryTypes != null) return false;
        if (testable != null ? !testable.equals(that.testable) : that.testable != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (queryTypes != null ? queryTypes.hashCode() : 0);
        result = 31 * result + (propertyDefinitions != null ? propertyDefinitions.hashCode() : 0);
        result = 31 * result + (testable != null ? testable.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientCustomDataSourceDefinition{" +
                "name='" + name + '\'' +
                ", queryTypes=" + queryTypes +
                ", propertyDefinitions=" + propertyDefinitions +
                ", testable=" + testable +
                '}';
    }
}
