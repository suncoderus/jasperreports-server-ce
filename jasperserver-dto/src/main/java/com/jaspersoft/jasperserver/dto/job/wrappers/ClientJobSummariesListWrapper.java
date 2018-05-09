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
import com.jaspersoft.jasperserver.dto.job.ClientJobSummary;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "jobs")
public class ClientJobSummariesListWrapper implements DeepCloneable<ClientJobSummariesListWrapper>{
    private List<ClientJobSummary> jobsummary;

    public ClientJobSummariesListWrapper(){}

    public ClientJobSummariesListWrapper(List<ClientJobSummary> jobSummaries){
        jobsummary = new ArrayList<ClientJobSummary>(jobSummaries.size());
        for (ClientJobSummary r : jobSummaries){
            jobsummary.add(r);
        }
    }

    public ClientJobSummariesListWrapper(ClientJobSummariesListWrapper other){
        this(other.getJobsummary());
    }

    @XmlElement(name = "jobsummary")
    public List<ClientJobSummary> getJobsummary() {
        return jobsummary;
    }

    public void setJobsummary(List<ClientJobSummary> jobSummaries) {
        this.jobsummary = jobSummaries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientJobSummariesListWrapper that = (ClientJobSummariesListWrapper) o;
        if (jobsummary != null ? !jobsummary.equals(that.jobsummary) : that.jobsummary != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return jobsummary != null ? jobsummary.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientJobSummariesListWrapper{" +
                "jobsummary=" + jobsummary +
                '}';
    }

    @Override
    public ClientJobSummariesListWrapper deepClone() {
        return new ClientJobSummariesListWrapper(this);
    }
}
