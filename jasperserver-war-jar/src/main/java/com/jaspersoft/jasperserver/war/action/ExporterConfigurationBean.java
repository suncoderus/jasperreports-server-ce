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

package com.jaspersoft.jasperserver.war.action;

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.engine.jasperreports.common.ExportParameters;



/**
 * @author sanda zaharia
 * @version $Id$
 */
public class ExporterConfigurationBean implements Serializable{
	
	private String descriptionKey;
	private String iconSrc;
	private String parameterDialogName;
	private ExportParameters exportParameters;
	private AbstractReportExporter currentExporter;
	/**
	 * @return Returns the iconSrc.
	 */
	public String getIconSrc() {
		return iconSrc;
	}
	/**
	 * @param iconSrc The iconSrc to set.
	 */
	public void setIconSrc(String iconSrc) {
		this.iconSrc = iconSrc;
	}
	/**
	 * @return Returns the parameterDialogName.
	 */
	public String getParameterDialogName() {
		return parameterDialogName;
	}
	/**
	 * @param parameterDialogName The parameterDialogName to set.
	 */
	public void setParameterDialogName(String parameterDialogName) {
		this.parameterDialogName = parameterDialogName;
	}
	/**
	 * @return Returns the descriptionKey.
	 */
	public String getDescriptionKey() {
		return descriptionKey;
	}
	/**
	 * @param descriptionKey The descriptionKey to set.
	 */
	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	/**
	 * @return Returns the exportParameters.
	 */
	public ExportParameters getExportParameters() {
		return exportParameters;
	}
	/**
	 * @param exportParameters The exportParameters to set.
	 */
	public void setExportParameters(ExportParameters exportParameters) {
		this.exportParameters = exportParameters;
	}
	/**
	 * @return Returns the currentExporter.
	 */
	public AbstractReportExporter getCurrentExporter() {
		return currentExporter;
	}
	/**
	 * @param currentExporter The currentExporter to set.
	 */
	public void setCurrentExporter(AbstractReportExporter currentExporter) {
		this.currentExporter = currentExporter;
	}
}
