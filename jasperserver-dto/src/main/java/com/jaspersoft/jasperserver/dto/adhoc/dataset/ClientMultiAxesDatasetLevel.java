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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vasyl Spachynskyi
 * @version $Id$
 * @since 08.04.2016
 */
public abstract class ClientMultiAxesDatasetLevel {

    private List<String> members = new ArrayList<String>();

    public ClientMultiAxesDatasetLevel() {
    }

    public ClientMultiAxesDatasetLevel(ClientMultiAxesDatasetLevel clientMultiAxesDatasetLevel) {
        this.members = new ArrayList<String>(clientMultiAxesDatasetLevel.getMembers());
    }

    @XmlElementWrapper(name = "members")
    @XmlElement(name = "member")
    public List<String> getMembers() {
        return members;
    }

    public ClientMultiAxesDatasetLevel setMembers(List<String> members) {
        this.members = members;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientMultiAxesDatasetLevel that = (ClientMultiAxesDatasetLevel) o;

        return !(members != null ? !members.equals(that.members) : that.members != null);
    }

    @Override
    public int hashCode() {
        return members != null ? members.hashCode() : 0;
    }

}
