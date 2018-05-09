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


import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

/**
 * user can easily customize sending alert feature by writing custom codes to implement ReportExecutionJobAlert interface
 * then plug it in applicationContext-report-scheduling.xml
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
@JasperServerAPI
public interface ReportExecutionJobAlert {

    /*
     * users can plug in their custom codes to override the alert feature
     */
    public void sendAlertMail(Job job,
                              ReportJob jobDetails,
                              List<ErrorDescriptor> exceptions,
                              JavaMailSender mailSender,
                              String fromAddress,
                              String[] toAddresses,
                              String characterEncoding) throws JobExecutionException;

}
