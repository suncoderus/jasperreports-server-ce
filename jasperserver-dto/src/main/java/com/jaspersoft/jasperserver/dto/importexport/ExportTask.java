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
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * @author: Zakhar.Tomchenco
 */
@XmlRootElement(name = "export")
public class ExportTask {
    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    private List<String> exportParams;

    @XmlElementWrapper(name = "uris")
    @XmlElement(name = "uri")
    private List<String> urisOfResources;

    @XmlTransient
    private List<String> urisOfScheduledJobs;

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<String> rolesToExport;

    @XmlElementWrapper(name = "users")
    @XmlElement(name = "user")
    private List<String> usersToExport;

    @XmlElementWrapper(name = "resourceTypes")
    @XmlElement(name = "resourceType")
    private List<String> resourceTypes;

    @XmlElement(name = "organization")
    private String organization;

    public ExportTask() {
    }

    public ExportTask(ExportTask other) {
        this.exportParams = (other.exportParams != null) ? new ArrayList<String>(other.exportParams) : null;
        this.urisOfResources = (other.urisOfResources != null) ? new ArrayList<String>(other.urisOfResources) : null;
        this.urisOfScheduledJobs = (other.urisOfScheduledJobs != null) ? new ArrayList<String>(other.urisOfScheduledJobs) : null;
        this.rolesToExport = (other.rolesToExport != null) ? new ArrayList<String>(other.rolesToExport) : null;
        this.usersToExport = (other.usersToExport != null) ? new ArrayList<String>(other.usersToExport) : null;
        this.resourceTypes = (other.resourceTypes != null) ? new ArrayList<String>(other.resourceTypes) : null;
        this.organization = other.getOrganization();
    }

    public List<String> getParameters() {
        return exportParams;
    }

    public ExportTask setParameters(List<String> exportParams) {
        this.exportParams = exportParams;
        return this;
    }

    public List<String> getUris() {
        return urisOfResources;
    }

    public ExportTask setUris(List<String> urisOfResources) {
        this.urisOfResources = urisOfResources;
        return this;
    }

    public List<String> getScheduledJobs() {
        return urisOfScheduledJobs;
    }

    public ExportTask setScheduledJobs(List<String> urisOfScheduledJobs) {
        this.urisOfScheduledJobs = urisOfScheduledJobs;
        return this;
    }

    public List<String> getRoles() {
        return rolesToExport;
    }

    public ExportTask setRoles(List<String> rolesToExport) {
        this.rolesToExport = rolesToExport;
        return this;
    }

    public List<String> getUsers() {
        return usersToExport;
    }

    public ExportTask setUsers(List<String> usersToExport) {
        this.usersToExport = usersToExport;
        return this;
    }

    public String getOrganization() {
        return organization;
    }

    public ExportTask setOrganization(String organization) {
        this.organization = organization;
        return this;
    }

    public List<String> getResourceTypes() {
        return resourceTypes;
    }

    public ExportTask setResourceTypes(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExportTask)) return false;

        ExportTask that = (ExportTask) o;

        if (exportParams != null ? !exportParams.equals(that.exportParams) : that.exportParams != null) return false;
        if (urisOfResources != null ? !urisOfResources.equals(that.urisOfResources) : that.urisOfResources != null)
            return false;
        if (urisOfScheduledJobs != null ? !urisOfScheduledJobs.equals(that.urisOfScheduledJobs) : that.urisOfScheduledJobs != null)
            return false;
        if (rolesToExport != null ? !rolesToExport.equals(that.rolesToExport) : that.rolesToExport != null)
            return false;
        if (usersToExport != null ? !usersToExport.equals(that.usersToExport) : that.usersToExport != null)
            return false;
        if (getResourceTypes() != null ? !getResourceTypes().equals(that.getResourceTypes()) : that.getResourceTypes() != null)
            return false;
        return !(getOrganization() != null ? !getOrganization().equals(that.getOrganization()) : that.getOrganization() != null);

    }

    @Override
    public int hashCode() {
        int result = exportParams != null ? exportParams.hashCode() : 0;
        result = 31 * result + (urisOfResources != null ? urisOfResources.hashCode() : 0);
        result = 31 * result + (urisOfScheduledJobs != null ? urisOfScheduledJobs.hashCode() : 0);
        result = 31 * result + (rolesToExport != null ? rolesToExport.hashCode() : 0);
        result = 31 * result + (usersToExport != null ? usersToExport.hashCode() : 0);
        result = 31 * result + (getResourceTypes() != null ? getResourceTypes().hashCode() : 0);
        result = 31 * result + (getOrganization() != null ? getOrganization().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExportTask{" +
                "exportParams=" + exportParams +
                ", urisOfResources=" + urisOfResources +
                ", urisOfScheduledJobs=" + urisOfScheduledJobs +
                ", rolesToExport=" + rolesToExport +
                ", usersToExport=" + usersToExport +
                ", resourceTypes=" + resourceTypes +
                ", organization='" + organization + '\'' +
                '}';
    }
}
