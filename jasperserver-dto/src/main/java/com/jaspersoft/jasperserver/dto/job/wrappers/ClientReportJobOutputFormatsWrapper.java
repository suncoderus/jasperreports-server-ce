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
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used for serialization because of no ability inFolder use @XmlElementWrapper together with @XmlJavaTypeAdapter.
 * See http://java.net/jira/browse/JAXB-787
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "outputFormats")
public class ClientReportJobOutputFormatsWrapper implements DeepCloneable<ClientReportJobOutputFormatsWrapper> {

    public ClientReportJobOutputFormatsWrapper() {
    }

    public ClientReportJobOutputFormatsWrapper(Set<String> formats) {
        this.formats = new LinkedHashSet<String>();
        for (String format : formats) {
            this.formats.add(format);
        }
    }

    public ClientReportJobOutputFormatsWrapper(ClientReportJobOutputFormatsWrapper other) {
        this(other.getFormats());
    }


    private Set<String> formats;

    @XmlElement(name = "outputFormat")
    public Set<String> getFormats() {
        return formats;
    }

    public ClientReportJobOutputFormatsWrapper setFormats(Set<String> formats) {
        this.formats = formats;
        return this;
    }

    @Override
    public String toString() {
        return "ClientReportJobOutputFormatsWrapper{" +
                "formats=" + formats +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientReportJobOutputFormatsWrapper)) return false;

        ClientReportJobOutputFormatsWrapper that = (ClientReportJobOutputFormatsWrapper) o;

        return !(getFormats() != null ? !getFormats().equals(that.getFormats()) : that.getFormats() != null);

    }

    @Override
    public int hashCode() {
        return getFormats() != null ? getFormats().hashCode() : 0;
    }

    @Override
    public ClientReportJobOutputFormatsWrapper deepClone() {
        return new ClientReportJobOutputFormatsWrapper(this);
    }
}
