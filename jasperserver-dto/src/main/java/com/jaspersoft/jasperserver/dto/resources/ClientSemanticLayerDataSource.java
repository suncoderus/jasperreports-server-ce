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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>SemanticLayerDataSource belongs to PRO codebase, but ClientSemanticLayerDataSource should be placed to CE because of usage in AbstractClientDataSourceHolder</p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = ResourceMediaType.SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE)
public class ClientSemanticLayerDataSource extends BaseSemanticLayerDataSource<ClientSemanticLayerDataSource, ClientReferenceableFile> {
    private ClientReferenceableFile schema;

    public ClientSemanticLayerDataSource(ClientSemanticLayerDataSource other) {
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

    public ClientSemanticLayerDataSource() {
    }


    @XmlElements({
            @XmlElement(name = "schemaFileReference", type = ClientReference.class),
            @XmlElement(name = "schemaFile", type = ClientFile.class)
    })
    public ClientReferenceableFile getSchema() {
        return schema;
    }

    public ClientSemanticLayerDataSource setSchema(ClientReferenceableFile schema) {
        this.schema = schema;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientSemanticLayerDataSource)) return false;
        if (!super.equals(o)) return false;

        ClientSemanticLayerDataSource that = (ClientSemanticLayerDataSource) o;

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
        return "ClientSemanticLayerDataSource{" +
                "schema=" + schema +
                "} " + super.toString();
    }
}
