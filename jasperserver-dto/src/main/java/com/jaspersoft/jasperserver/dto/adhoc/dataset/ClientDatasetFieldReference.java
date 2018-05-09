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
package com.jaspersoft.jasperserver.dto.adhoc.dataset;

/**
 * @author Vasyl Spachynskyi
 * @version $Id: Id $
 * @since 08.07.2016
 */
public class ClientDatasetFieldReference {
    String reference;
    String type;

    public ClientDatasetFieldReference() {
    }

    public ClientDatasetFieldReference(ClientDatasetFieldReference ref) {
        reference = ref.getReference();
        type = ref.getType();
    }

    public String getReference() {
        return reference;
    }

    public ClientDatasetFieldReference setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getType() {
        return type;
    }

    public ClientDatasetFieldReference setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDatasetFieldReference that = (ClientDatasetFieldReference) o;

        if (reference != null ? !reference.equals(that.reference) : that.reference != null) return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = reference != null ? reference.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientDatasetFieldReference{" +
                "reference='" + reference + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
