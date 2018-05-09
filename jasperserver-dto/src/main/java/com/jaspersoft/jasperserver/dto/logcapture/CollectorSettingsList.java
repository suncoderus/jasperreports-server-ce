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
package com.jaspersoft.jasperserver.dto.logcapture;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Yakiv Tymoshenko
 * @version $Id: Id $
 * @since 06.10.14
 */
@XmlRootElement
@SuppressWarnings("unused")
public class CollectorSettingsList {
    List<CollectorSettings> collectorSettingsList;

    public CollectorSettingsList() {
    }

    public CollectorSettingsList(CollectorSettingsList other) {
        this(other.getCollectorSettingsList());
    }


    public CollectorSettingsList(List<CollectorSettings> collectorSettingsList) {
        if (collectorSettingsList != null) {
            List<CollectorSettings> clonedCollectorSettingsList = new ArrayList<CollectorSettings>(collectorSettingsList.size());
            for (CollectorSettings collectorSettings : collectorSettingsList) {
                clonedCollectorSettingsList.add(new CollectorSettings(collectorSettings));
            }
            this.collectorSettingsList = clonedCollectorSettingsList;
        }
    }

    @XmlElementWrapper(name = "CollectorSettingsList")
    @XmlElement(name = "CollectorSettings")
    public List<CollectorSettings> getCollectorSettingsList() {
        return collectorSettingsList;
    }

    public CollectorSettingsList setCollectorSettingsList(List<CollectorSettings> collectorSettingsList) {
        this.collectorSettingsList = collectorSettingsList;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectorSettingsList)) return false;

        CollectorSettingsList that = (CollectorSettingsList) o;

        return !(getCollectorSettingsList() != null ? !getCollectorSettingsList().equals(that.getCollectorSettingsList()) : that.getCollectorSettingsList() != null);

    }

    @Override
    public int hashCode() {
        return getCollectorSettingsList() != null ? getCollectorSettingsList().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CollectorSettingsList{" +
                "collectorSettingsList=" + collectorSettingsList +
                '}';
    }
}
