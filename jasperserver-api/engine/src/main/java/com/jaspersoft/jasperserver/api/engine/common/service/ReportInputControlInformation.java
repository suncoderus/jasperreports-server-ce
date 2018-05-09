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

import net.sf.jasperreports.engine.JRParameter;

/**
 * Information provided for input controls used to set parameter values for a report.
 * This information comes from the JRXML parameter definitions and the JasperServer
 * persisted input control objects.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
@JasperServerAPI
public interface ReportInputControlInformation {

	/**
	 * Gets a text string to be displayed with the input control
	 * @return label value
	 */
	String getPromptLabel();

	/**
	 * Gets a text string to be used as an input control description
	 * @return description value
	 */
	String getDescription();

    /**
     * Gets string name of the JRParameter.
     * @return parameter name
     */
    String getParameterName();

	/**
	 * Gets the Java class of the parameter corresponding to the input control
	 * @return 
	 */
	Class getValueType();
	
	/**
	 * Gets the Java class of the objects contained in a parameter value which has a collection type such as java.util.List or java.util.Set
	 * 
	 * @return the input control nested value type
	 * @see JRParameter#getNestedType()
	 */
	Class getNestedType();
	
	/**
	 * Gets the default value of the parameter
	 * @return default value
	 */
	
	Object getDefaultValue();
	
	void setDefaultValue(Object value);

    /**
     * Gets strings to be displayed with input control list of values,
     * if no list of values then returns null
     * @return values information
     */
    ReportInputControlValuesInformation getReportInputControlValuesInformation();
	
}
