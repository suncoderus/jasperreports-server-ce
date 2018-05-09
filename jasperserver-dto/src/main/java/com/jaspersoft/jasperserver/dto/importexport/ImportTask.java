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
package com.jaspersoft.jasperserver.dto.importexport;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Import Task Dto
 * @author askorodumov
 * @version $Id: ImportTaskDto.java 58382 2015-10-07 11:36:07Z vzavadsk $
 */
@XmlRootElement(name = "import")
public class ImportTask {
    @XmlElement(name = "organization")
    private String organization;

    @XmlElement(name = "brokenDependencies")
    private String brokenDependencies;

    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<String> parameters;

    public ImportTask() {
    }


    public ImportTask(ImportTask other) {
        this.organization = other.getOrganization();
        this.brokenDependencies = other.getBrokenDependencies();
        this.parameters = (other.getParameters() != null) ? new ArrayList<String>(other.getParameters()) : null;
    }

    public String getOrganization() {
        return organization;
    }

    public ImportTask setOrganization(String organization) {
        this.organization = organization;
        return this;
    }

    public String getBrokenDependencies() {
        return brokenDependencies;
    }

    public ImportTask setBrokenDependencies(String brokenDependencies) {
        this.brokenDependencies = brokenDependencies;
        return this;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public ImportTask setParameters(List<String> parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportTask)) return false;

        ImportTask that = (ImportTask) o;

        if (getOrganization() != null ? !getOrganization().equals(that.getOrganization()) : that.getOrganization() != null)
            return false;
        if (getBrokenDependencies() != null ? !getBrokenDependencies().equals(that.getBrokenDependencies()) : that.getBrokenDependencies() != null)
            return false;
        return !(getParameters() != null ? !getParameters().equals(that.getParameters()) : that.getParameters() != null);

    }

    @Override
    public int hashCode() {
        int result = getOrganization() != null ? getOrganization().hashCode() : 0;
        result = 31 * result + (getBrokenDependencies() != null ? getBrokenDependencies().hashCode() : 0);
        result = 31 * result + (getParameters() != null ? getParameters().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImportTask{" +
                "organization='" + organization + '\'' +
                ", brokenDependencies='" + brokenDependencies + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
