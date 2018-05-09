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

package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import org.quartz.JobExecutionException;
import com.jaspersoft.jasperserver.api.JasperServerAPI;
import java.util.List;

/**
 * user can easily customize file saving feature by writing custom codes to implement ReportExecutionJobFileSaving interface
 * then plug it in applicationContext-report-scheduling.xml
 * (including saving to repository, local, and ftp server
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
@JasperServerAPI
public interface ReportExecutionJobFileSaving {

    /*
     * users can plug in their custom codes to override the save feature
     */
    public void save(ReportExecutionJob job, ReportOutput output, boolean useFolderHierarchy, ReportJob jobDetails);

}
