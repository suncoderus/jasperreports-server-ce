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
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSourceModel;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The source of a report job, consisting of a report to execute and a set of
 * report input values.
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 * @see ReportJob#getSource()
 */
@JasperServerAPI
public class ReportJobSource implements Serializable {
	public static final String REFERENCE_WIDTH_PARAMETER_NAME = "REFERENCE_WIDTH_PARAMETER_NAME";
	public static final String REFERENCE_HEIGHT_PARAMETER_NAME = "REFERENCE_HEIGHT_PARAMETER_NAME";

	private static final long serialVersionUID = 1L;

	private String reportUnitURI;
	private Map<String, Object> parameters;

	/**
	 * Creates an empty job source.
	 */
	public ReportJobSource() {
	}

	/**
	 * Returns the repository URI/path of the report that the job will execute.
	 *
	 * @return the report that the job will execute
	 */
	public String getReportUnitURI() {
		return reportUnitURI;
	}

	/**
	 * Defines the report which should be executed by the job.
	 *
	 * @param reportUnitURI the repository URI/path of the report that the job
	 * should execute
	 */
	public void setReportUnitURI(String reportUnitURI) {
		this.reportUnitURI = reportUnitURI;
	}

	/**
	 * Returns the set of input values which will be used when running the
	 * job report.
	 *
	 * <p>
	 * The input values are associated to report input controls.
	 * </p>
	 *
	 * @return the input values used for the report, indexed by input control
	 * names
	 * @see ReportUnit#getInputControls()
     * @deprecated use ReportJobSource.getParameters() instead
	 */
	public Map getParametersMap() {
		return getParameters();
	}

    /**
     * Returns the set of input values which will be used when running the
     * job report.
     *
     * <p>
     * The input values are associated to report input controls.
     * </p>
     *
     * @return the input values used for the report, indexed by input control
     * names
     * @see ReportUnit#getInputControls()
     * @since 4.7
     */
    public Map<String, Object> getParameters(){
        return parameters;
    }

	/**
	 * Sets the set of input values to be used when running the job report.
	 *
	 * <p>
	 * The values are passed in a map indexed by report input control/parameter
	 * names.
	 * </p>
	 *
	 * @param parameters the report input values
     * @deprecated use ReportJobSource.addParameters() instead
	 */
	public void setParametersMap(Map parameters) {
		setParameters(parameters);
	}

    /**
     * Sets the set of input values to be used when running the job report.
     *
     * <p>
     * The values are passed in a map indexed by report input control/parameter
     * names.
     * </p>
     *
     * @param parameters the report input values
     * @since 4.7
     */
    public void setParameters(Map<String, Object> parameters){
		boolean restore = this.parameters != null;
		Integer width = null, height = null;

		if (restore){
			width = getReferenceWidth();
			height = getReferenceHeight();
		}

        this.parameters = parameters;

		if (restore){
			setReferenceWidth(width);
			setReferenceHeight(height);
		}
    }

	/**
	 * Returns desired width of visualization
	 *
	 * @return the width
	 * @since 6.2 +
	 */

	public Integer getReferenceWidth() {
		if (parameters != null){
			return (Integer) parameters.get(REFERENCE_WIDTH_PARAMETER_NAME);
		}

		return null;
	}

	/**
	 * Sets width of visualizations if required
	 *
	 *
	 * @param width reference width of visualization in pixels
	 * @since 6.2 +
	 */
	public void setReferenceWidth(Integer width) {
		if (parameters == null){
			parameters = new HashMap<String, Object>();
		}

		if (width == null){
			parameters.remove(REFERENCE_WIDTH_PARAMETER_NAME);
		} else {
			parameters.put(REFERENCE_WIDTH_PARAMETER_NAME, width);
		}
	}

	/**
	 * Returns desired height of visualization
	 *
	 * @return the height
	 * @since 6.2 +
	 *
	 */
	public Integer getReferenceHeight() {
		if (parameters != null){
			return (Integer) parameters.get(REFERENCE_HEIGHT_PARAMETER_NAME);
		}

		return null;
	}

	/**
	 * Sets height of visualizations if required
	 *
	 *
	 * @param height reference height of visualization in pixels
	 * @since 6.2 +
	 */
	public void setReferenceHeight(Integer height) {
		if (parameters == null){
			parameters = new HashMap<String, Object>();
		}

		if (height == null){
			parameters.remove(REFERENCE_HEIGHT_PARAMETER_NAME);
		} else {
			parameters.put(REFERENCE_HEIGHT_PARAMETER_NAME, height);
		}
	}

	/**
   * Convenience constructor that returns a distinct copy of the input ReportJobSource.
   * All of the copy's Object members are themselves copies as well.
   *
   * We're deliberately avoiding using clone()
   */
  public ReportJobSource(ReportJobSource jobSource) {
      this.setReportUnitURI(jobSource.getReportUnitURI());

      // Here we assume that copying any Map Object references is safe
      //   That it is OK if any of these are shared.
      if (jobSource.getParametersMap() != null) {
          this.setParametersMap(new HashMap(jobSource.getParametersMap()));
      }
  }

	public void copyFromModel(ReportJobSourceModel sourceModel) {
		if (sourceModel.isReportUnitURIModified()) setReportUnitURI(sourceModel.getReportUnitURI());
		if (sourceModel.isParametersMapModified()) {
			HashMap<String, Object> copiedMap = new HashMap<String, Object>();
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				copiedMap.put(entry.getKey(), entry.getValue());
}
			setParametersMap(copiedMap);
		};
	}
}
