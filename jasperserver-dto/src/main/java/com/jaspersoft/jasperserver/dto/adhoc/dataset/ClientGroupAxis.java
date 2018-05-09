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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @author Vasyl Spachynskyi
* @version $Id$
* @since 05.04.2016
*/
@XmlRootElement
public class ClientGroupAxis {
    private List<String[]> level = new ArrayList<String[]>();

    public ClientGroupAxis() {}

    public ClientGroupAxis(ClientGroupAxis groupAxis) {
        for (String[] groupLevel : groupAxis.getLevel()) {
            level.add(Arrays.copyOf(groupLevel, groupLevel.length));
        }
    }


    @XmlElement(name = "level")
    public List<String[]> getLevel() {
        return level;
    }

    public ClientGroupAxis setLevel(List<String[]> columns) {
        this.level = columns;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientGroupAxis that = (ClientGroupAxis) o;

        if (level == that.level) return true;
        for (int i = 0; i < level.size(); i++) {
            if (!Arrays.equals(level.get(i), that.getLevel().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return level != null ? level.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientGroupAxis{" +
                "columns=" + level +
                '}';
    }

}