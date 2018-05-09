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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.ReportSchedulingInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;

/**
 * Created with IntelliJ IDEA.
 * User: Zakhar.Tomchenco
 * Date: 7/25/12
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportSchedulingInternalServiceMock implements ReportSchedulingInternalService {

    public void removeReportUnitJobs(String reportUnitURI) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateReportUnitURI(String oldURI, String newURI) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ReportJob saveJob(ExecutionContext context, ReportJob job) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
