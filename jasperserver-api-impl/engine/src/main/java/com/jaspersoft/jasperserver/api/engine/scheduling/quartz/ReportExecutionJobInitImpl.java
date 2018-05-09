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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionException;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id$
 */
public class ReportExecutionJobInitImpl implements ReportExecutionJobInit {

    private static final Log log = LogFactory.getLog(ReportExecutionJobInitImpl.class);

    /*
     * users can plug in their custom codes and modify the report job before exporting take action
     */
    public ReportJob initJob(Job job, ReportJob jobDetails)  throws JobExecutionException {
		/*
		 * plugin last minute customer's logic before starting repeort job execution
		 */

		if(log.isDebugEnabled()){
	        	log.debug("**** ReportExecutionJobInitImpl.initJob ********: " + 
        			"  job =" + job.toString() +
        			", job id=" +  jobDetails.getId() + 
        			", job creation date=" + jobDetails.getCreationDate() +
        			", version=" + jobDetails.getVersion() + 
        			", trigger id=" + jobDetails.getTrigger().getId() + 
        			", start date=" + jobDetails.getTrigger().getStartDate());
        	}
		return jobDetails;
	}
}
