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
package com.jaspersoft.jasperserver.dto.reports.options;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement
public class ReportOptionsSummary {
    private String uri;
    private String id;
    private String label;

    public ReportOptionsSummary() { }

    public ReportOptionsSummary(ReportOptionsSummary other) {
        this.uri = other.uri;
        this.id = other.id;
        this.label = other.label;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportOptionsSummary)) return false;

        ReportOptionsSummary that = (ReportOptionsSummary) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ReportOptionsSummary{" +
                "uri='" + uri + '\'' +
                ", id='" + id + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

}
