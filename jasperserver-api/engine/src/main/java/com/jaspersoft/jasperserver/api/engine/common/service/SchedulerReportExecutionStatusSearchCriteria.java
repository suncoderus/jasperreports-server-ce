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
package com.jaspersoft.jasperserver.api.engine.common.service;


import com.jaspersoft.jasperserver.api.JasperServerAPI;

import java.util.Date;

/**
 * criteria object to use for filtering report job service
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 * @since 4.7
 * @see com.jaspersoft.jasperserver.api.engine.common.service.EngineService#getSchedulerReportExecutionStatusList(SchedulerReportExecutionStatusSearchCriteria);
 */
@JasperServerAPI
public class SchedulerReportExecutionStatusSearchCriteria extends ReportExecutionStatusSearchCriteria {

    private Long jobID;
    private String jobLabel;
    private String userName;
    private Date fireTimeFrom;
    private Date fireTimeTo;

    public SchedulerReportExecutionStatusSearchCriteria() {};

    public Date getFireTimeFrom() {
        return fireTimeFrom;
    }

    public void setFireTimeFrom(Date fireTimeFrom) {
        this.fireTimeFrom = fireTimeFrom;
    }

    public Date getFireTimeTo() {
        return fireTimeTo;
    }

    public void setFireTimeTo(Date fireTimeTo) {
        this.fireTimeTo = fireTimeTo;
    }

    public String getJobLabel() {
        return jobLabel;
    }

    public void setJobLabel(String jobLabel) {
        this.jobLabel = jobLabel;
    }

    public Long getJobID() {
        return jobID;
    }

    public void setJobID(Long jobID) {
        this.jobID = jobID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
