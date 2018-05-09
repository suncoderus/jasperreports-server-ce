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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

/**
 * @author askorodumov
 * @version $Id$
 */
@XmlRootElement(name = ResourceMediaType.PROPERTIES_FILE_CLIENT_TYPE)
public class ClientPropertiesFile extends ClientResource<ClientPropertiesFile> {
    private LinkedList<ClientProperty> properties;

    public ClientPropertiesFile() {
    }

    public ClientPropertiesFile(ClientPropertiesFile other) {
        super(other);

        if (other.properties != null) {
            this.properties = new LinkedList<ClientProperty>(other.properties);
        }
    }

    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    public LinkedList<ClientProperty> getProperties() {
        return properties;
    }

    public void setProperties(LinkedList<ClientProperty> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientPropertiesFile)) return false;
        if (!super.equals(o)) return false;

        ClientPropertiesFile that = (ClientPropertiesFile) o;

        return !(properties != null ? !properties.equals(that.properties) : that.properties != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientPropertiesFile{" + super.toString() + "}";
    }
}
