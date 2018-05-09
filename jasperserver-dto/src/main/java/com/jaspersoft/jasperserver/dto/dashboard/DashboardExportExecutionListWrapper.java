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

package com.jaspersoft.jasperserver.dto.dashboard;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * <p>DTO for dashboard executions.</p>
 *
 * @author Zakhar Tomchenko
 * @version $Id: $
 */
@XmlRootElement(name = "dashboardExportExecutions")
public class DashboardExportExecutionListWrapper {
    private List<DashboardExportExecution> executions;

    @XmlElement(name = "dashboardExportExecution")
    public List<DashboardExportExecution> getExecutions() {
        return executions;
    }

    public DashboardExportExecutionListWrapper setExecutions(List<DashboardExportExecution> executions) {
        this.executions = executions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashboardExportExecutionListWrapper that = (DashboardExportExecutionListWrapper) o;

        return !(executions != null ? !executions.equals(that.executions) : that.executions != null);

    }

    @Override
    public int hashCode() {
        return executions != null ? executions.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DashboardReportExecutionListWrapper{" +
                "executions=" + executions +
                '}';
    }
}

