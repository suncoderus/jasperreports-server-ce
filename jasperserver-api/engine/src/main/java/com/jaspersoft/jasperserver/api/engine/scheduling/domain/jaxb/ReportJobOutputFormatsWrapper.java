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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * This class is used for serialization because of no ability to use @XmlElementWrapper together with @XmlJavaTypeAdapter.
 * See http://java.net/jira/browse/JAXB-787
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "outputFormats")
public class ReportJobOutputFormatsWrapper {

    private Set<String> formats;
    @XmlElement(name = "outputFormat")
    public Set<String> getFormats() {
        return formats;
    }

    public void setFormats(Set<String> formats) {
        this.formats = formats;
    }

}
