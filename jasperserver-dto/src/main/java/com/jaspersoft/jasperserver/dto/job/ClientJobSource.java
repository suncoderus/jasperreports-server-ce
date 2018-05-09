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

package com.jaspersoft.jasperserver.dto.job;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import com.jaspersoft.jasperserver.dto.job.adapters.ReportJobSourceParametersXmlAdapter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
@XmlRootElement(name = "source")
public class ClientJobSource implements DeepCloneable<ClientJobSource> {

    private String reportUnitURI;
    private Map<String, String[]> parameters;
    private Integer referenceHeight, referenceWidth;

    public ClientJobSource() {
    }

    public ClientJobSource(ClientJobSource other) {
        this.reportUnitURI = other.reportUnitURI;
        this.referenceHeight = other.referenceHeight;
        this.referenceWidth = other.referenceWidth;

        if (other.parameters != null) {
            this.parameters = new LinkedHashMap<String, String[]>();
            for (Map.Entry<String, String[]> entry : other.parameters.entrySet()) {
                this.parameters.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public String getReportUnitURI() {
        return reportUnitURI;
    }

    public ClientJobSource setReportUnitURI(String reportUnitURI) {
        this.reportUnitURI = reportUnitURI;
        return this;
    }

    public Integer getReferenceHeight() {
        return referenceHeight;
    }

    public ClientJobSource setReferenceHeight(Integer referenceHeight) {
        this.referenceHeight = referenceHeight;
        return this;
    }

    public Integer getReferenceWidth() {
        return referenceWidth;
    }

    public ClientJobSource setReferenceWidth(Integer referenceWidth) {
        this.referenceWidth = referenceWidth;
        return this;
    }

    @XmlJavaTypeAdapter(ReportJobSourceParametersXmlAdapter.class)
    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public ClientJobSource setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientJobSource)) return false;

        ClientJobSource source = (ClientJobSource) o;

        if (parameters != null && source.parameters != null) {
            if (parameters.size() != source.parameters.size()) return false;
            if (!(parameters.keySet().containsAll(source.parameters.keySet()) && source.parameters.keySet().containsAll(parameters.keySet()))) return false;
            for (String key : parameters.keySet()) {
                String[] values = parameters.get(key);
                String[] sourceValues = source.parameters.get(key);
                if (!Arrays.equals(values, sourceValues)) return false;
            }
        } else {
            if ((parameters != null & source.parameters == null) || (parameters == null & source.parameters != null))
                return false;
        }
        if (reportUnitURI != null ? !reportUnitURI.equals(source.reportUnitURI) : source.reportUnitURI != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = reportUnitURI != null ? reportUnitURI.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobSource{" +
                "reportUnitURI='" + reportUnitURI + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    @Override
    public ClientJobSource deepClone() {
        return new ClientJobSource(this);
    }
}
