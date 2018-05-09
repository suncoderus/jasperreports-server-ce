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
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 * @version $Id$
 */
@XmlRootElement(name = "calendarNameList")
public class ClientCalendarNameListWrapper implements DeepCloneable<ClientCalendarNameListWrapper> {
    private List<String> calendarNames;

    public ClientCalendarNameListWrapper() {
    }


    public ClientCalendarNameListWrapper(List<String> calendarNames) {
        this.calendarNames = new LinkedList<String>();
        for (String calendarName : calendarNames) {
            this.calendarNames.add(calendarName);
        }
    }

    public ClientCalendarNameListWrapper(ClientCalendarNameListWrapper other) {
        this(other.getCalendarNames());
    }


    @XmlElement(name = "calendarName")
    public List<String> getCalendarNames() {
        return calendarNames;
    }

    public void setCalendarNames(List<String> calendarNames) {
        this.calendarNames = calendarNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientCalendarNameListWrapper)) return false;

        ClientCalendarNameListWrapper that = (ClientCalendarNameListWrapper) o;

        return !(getCalendarNames() != null ? !getCalendarNames().equals(that.getCalendarNames()) : that.getCalendarNames() != null);

    }

    @Override
    public int hashCode() {
        return getCalendarNames() != null ? getCalendarNames().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientCalendarNameListWrapper{" +
                "calendarNames=" + calendarNames +
                '}';
    }

    @Override
    public ClientCalendarNameListWrapper deepClone() {
        return new ClientCalendarNameListWrapper(this);
    }
}
