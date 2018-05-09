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
package com.jaspersoft.jasperserver.remote.services.impl;

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
public class ExcludeDaysWrapper {

    private List<String> excludeDays;

    public ExcludeDaysWrapper(){}

    public ExcludeDaysWrapper(List<String> excludeDays){
        this.excludeDays = excludeDays;
    }
    @XmlElement(name = "excludeDay")
    public List<String> getExcludeDays() {
        return excludeDays;
    }

    public void setExcludeDays(List<String> excludeDays) {
        this.excludeDays = excludeDays;
    }
}
