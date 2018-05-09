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
import javax.xml.bind.annotation.XmlElements;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public abstract class AbstractClientMondrianConnection <BuilderType extends AbstractClientMondrianConnection<BuilderType>>
        extends AbstractClientDataSourceHolder<BuilderType>
        implements ClientReferenceableMondrianConnection, ClientReferenciableOlapConnection, ClientReferenceableDataSource {
    private ClientReferenceableFile schema;

    public AbstractClientMondrianConnection(AbstractClientMondrianConnection other) {
        super(other);
        ClientReferenceableFile srcSchema = other.getSchema();

        if (srcSchema != null) {
            if (srcSchema instanceof ClientReference){
                schema = new ClientReference((ClientReference) srcSchema);
            } else if (srcSchema instanceof ClientFile){
                schema = new ClientFile((ClientFile) srcSchema);
            }
        }
    }

    public AbstractClientMondrianConnection() {
    }

    @XmlElements({
            @XmlElement(name = "schemaReference", type = ClientReference.class),
            @XmlElement(name = "schema", type = ClientFile.class)
    })
    public ClientReferenceableFile getSchema() {
        return schema;
    }

    // unchecked cast to BuilderType safety assured by the rule of usage BuilderType generic parameter.
    @SuppressWarnings("unchecked")
    public BuilderType setSchema(ClientReferenceableFile schema) {
        this.schema = schema;
        return (BuilderType) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AbstractClientMondrianConnection that = (AbstractClientMondrianConnection) o;

        if (schema != null ? !schema.equals(that.schema) : that.schema != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "schema=" + schema +
                ", version=" + getVersion() +
                ", permissionMask=" + getPermissionMask() +
                ", uri='" + getUri() + '\'' +
                ", label='" + getLabel() + '\'' +
                '}';
    }
}
