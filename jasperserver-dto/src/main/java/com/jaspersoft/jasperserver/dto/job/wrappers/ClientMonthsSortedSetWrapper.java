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
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is needed because of bug in JAXB.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 * @XmlElementWrapper doesn't support @XmlJavaTypeAdapter
 */
@XmlRootElement(name = "months")
public class ClientMonthsSortedSetWrapper implements DeepCloneable<ClientMonthsSortedSetWrapper> {

    private SortedSet<String> mongths;

    public ClientMonthsSortedSetWrapper() {
    }

    public ClientMonthsSortedSetWrapper(SortedSet<String> mongths) {
        this.mongths = new TreeSet<String>();
        for (String mongth : mongths) {
            this.mongths.add(mongth);
        }
    }

    public ClientMonthsSortedSetWrapper(ClientMonthsSortedSetWrapper other) {
        this(other.getMongths());
    }

    @XmlElement(name = "month")
    public SortedSet<String> getMongths() {
        return mongths;
    }

    public ClientMonthsSortedSetWrapper setMongths(SortedSet<String> mongths) {
        this.mongths = mongths;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientMonthsSortedSetWrapper)) return false;

        ClientMonthsSortedSetWrapper that = (ClientMonthsSortedSetWrapper) o;

        return !(getMongths() != null ? !getMongths().equals(that.getMongths()) : that.getMongths() != null);

    }

    @Override
    public int hashCode() {
        return getMongths() != null ? getMongths().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientMonthsSortedSetWrapper{" +
                "mongths=" + mongths +
                '}';
    }

    @Override
    public ClientMonthsSortedSetWrapper deepClone() {
        return new ClientMonthsSortedSetWrapper(this);
    }
}
