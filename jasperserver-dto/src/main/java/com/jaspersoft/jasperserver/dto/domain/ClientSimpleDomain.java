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
package com.jaspersoft.jasperserver.dto.domain;

import com.jaspersoft.jasperserver.dto.connection.metadata.TableMetadata;
import com.jaspersoft.jasperserver.dto.resources.AbstractClientDataSourceHolder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "simpleDomain")
public class ClientSimpleDomain extends AbstractClientDataSourceHolder<ClientSimpleDomain>{
    private TableMetadata metadata;

    public ClientSimpleDomain(){}

    public ClientSimpleDomain(ClientSimpleDomain source){
        super(source);
        final TableMetadata sourceMetadata = source.getMetadata();
        metadata = sourceMetadata != null ? new TableMetadata(sourceMetadata) : null;
    }

    public TableMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(TableMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ClientSimpleDomain that = (ClientSimpleDomain) o;

        if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientSimpleDomain{" +
                "metadata=" + metadata +
                "} " + super.toString();
    }
}
