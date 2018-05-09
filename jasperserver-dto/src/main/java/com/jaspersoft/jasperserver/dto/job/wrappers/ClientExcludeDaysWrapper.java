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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * This class is needed because of bug in JAXB.
 * XmlElementWrapper annotation doesn't support @XmlJavaTypeAdapter
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "excludeDays")
public class ClientExcludeDaysWrapper implements DeepCloneable<ClientExcludeDaysWrapper> {

    private List<String> excludeDays;

    public ClientExcludeDaysWrapper() {
    }

    public ClientExcludeDaysWrapper(List<String> excludeDays) {
        this.excludeDays = new LinkedList<String>();
        for (String excludeDay : excludeDays) {
            this.excludeDays.add(excludeDay);
        }
    }

    public ClientExcludeDaysWrapper(ClientExcludeDaysWrapper other) {
        this(other.getExcludeDays());
    }

    @XmlElement(name = "excludeDay")
    public List<String> getExcludeDays() {
        return excludeDays;
    }

    public ClientExcludeDaysWrapper setExcludeDays(List<String> excludeDays) {
        this.excludeDays = excludeDays;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientExcludeDaysWrapper that = (ClientExcludeDaysWrapper) o;

        if (excludeDays != null ? !excludeDays.equals(that.excludeDays) : that.excludeDays != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return excludeDays != null ? excludeDays.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientExcludeDaysWrapper{" +
                "excludeDays=" + excludeDays +
                '}';
    }

    @Override
    public ClientExcludeDaysWrapper deepClone() {
        return new ClientExcludeDaysWrapper(this);
    }
}
