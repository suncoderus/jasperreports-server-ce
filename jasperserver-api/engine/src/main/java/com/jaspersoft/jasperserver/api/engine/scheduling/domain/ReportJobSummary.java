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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import java.io.Serializable;

/**
 * Report job summary information returned by the scheduler service when
 * listing report jobs.
 * 
 * <p>
 * The job summary information also includes job execution status determined
 * at the moment the jobs are listed.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @see ReportSchedulingService#getScheduledJobs(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, String)
 * @see ReportSchedulingService#getScheduledJobs(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext)
 * @see ReportJobRuntimeInformation
 * @since 1.0
 */
@JasperServerAPI
public class ReportJobSummary implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private int version;
	private String reportUnitURI;
	private String username;
	private String label;
	private String description;
	private String reportLabel;

	private ReportJobRuntimeInformation runtimeInformation;

	/**
	 * Creates an empty object.
	 */
	public ReportJobSummary() {
	}
	
	/**
	 * Returns the ID of the job for which this summary has been created.
	 * 
	 * @return the job ID
	 * @see ReportJob#getId()
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Sets the ID of the job to which this summary is associated.
	 * 
	 * @param id the job ID
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Returns the job label.
	 * 
	 * @return the job label
	 * @see ReportJob#getLabel()
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the job label.
	 * 
	 * @param label the label of the job
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Returns the persisted version of the job.
	 * 
	 * @return the job version
	 * @see ReportJob#getVersion()
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Sets the job version.
	 * 
	 * @param version the version of the job
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Returns the job execution status at the moment the job was listed
	 * by the scheduling service.
	 * 
	 * @return the job execution status
	 */
	public ReportJobRuntimeInformation getRuntimeInformation() {
		return runtimeInformation;
	}

	/**
	 * Sets the job execution status.
	 * 
	 * @param runtimeInformation the execution status of the job
	 */
	public void setRuntimeInformation(ReportJobRuntimeInformation runtimeInformation) {
		this.runtimeInformation = runtimeInformation;
	}

	/**
	 * Returns the username of the job owner.
	 * 
	 * @return the job owner
	 * @see ReportJob#getUsername()
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the job owner.
	 * 
	 * @param username the username of the job owner
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the report that the job executes.
	 * 
	 * @return the repository URI/path of the job report
	 * @see ReportJobSource#getReportUnitURI()
	 */
	public String getReportUnitURI() {
		return reportUnitURI;
	}

	/**
	 * Sets the report that the job executes.
	 * 
	 * @param reportUnitURI the repository URI/path of the job report
	 */
	public void setReportUnitURI(String reportUnitURI) {
		this.reportUnitURI = reportUnitURI;
	}

	/**
	 * Returns the job description.
	 *
	 * @return the job description
	 * @see ReportJob#getDescription()
	 */
	public String getDescription() {
		return description;
	}

   /**
	 * get the label of the scheduled report (as opposed to the job itself)
	 * @return
	 */
	public String getReportLabel() {
		return reportLabel;
	}

	/**
	 * Sets the job description.
	 *
	 * @param description the description of the job
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void setReportLabel(String reportLabel) {
		this.reportLabel = reportLabel;
	}

}
