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

package com.jaspersoft.jasperserver.search.common;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
public class SchedulingChecker {

    private ReportSchedulingService schedulingService;

    private Set<String> types;

    public boolean isScheduled(ExecutionContext context, Resource resource) {
        if (this.getTypes().isEmpty() || this.getTypes().contains(resource.getResourceType())) {
            List jobs = schedulingService.getScheduledJobSummaries(context, resource.getURIString());

            return jobs.size() > 0;
        } else {
            return false;
        }
    }

    public Set<String> getTypes() {
        if (this.types == null) {
            this.types = new HashSet<String>();
        }

        return types;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }

    public void setSchedulingService(ReportSchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }
}
