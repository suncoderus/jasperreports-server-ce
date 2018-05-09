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

package com.jaspersoft.jasperserver.dto.job.wrappers;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is needed because of bug in JAXB.
 * XmlElementWrapper annotation doesn't support @XmlJavaTypeAdapter
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement
public class ClientAddressesListWrapper implements DeepCloneable<ClientAddressesListWrapper> {

    private List<String> addresses;

    public ClientAddressesListWrapper() {
    }

    public ClientAddressesListWrapper(List<String> otherAddresses) {
        this.addresses = new ArrayList<String>();
        for (String address : otherAddresses) {
            this.addresses.add(address);
        }
    }

    public ClientAddressesListWrapper(ClientAddressesListWrapper other) {
        this(other.getAddresses());
    }

    @XmlElement(name = "address")
    public List<String> getAddresses() {
        return addresses;
    }

    public ClientAddressesListWrapper setAddresses(List<String> addresses) {
        this.addresses = addresses;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientAddressesListWrapper)) return false;

        ClientAddressesListWrapper that = (ClientAddressesListWrapper) o;

        return !(getAddresses() != null ? !getAddresses().equals(that.getAddresses()) : that.getAddresses() != null);

    }

    @Override
    public int hashCode() {
        return getAddresses() != null ? getAddresses().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientAddressesListWrapper{" +
                "addresses=" + addresses +
                '}';
    }

    @Override
    public ClientAddressesListWrapper deepClone() {
        return new ClientAddressesListWrapper(this);
    }
}
