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

package com.jaspersoft.jasperserver.api.engine.jasperreports.common;

import java.io.Serializable;

/**
 * @author sanda zaharia
 * @version $Id$
 */
public class CsvExportParametersBean extends AbstractExportParameters {
	
	public static final String PROPERTY_CSV_PAGINATED = "com.jaspersoft.jrs.export.csv.paginated";

	private String fieldDelimiter;

	/**
	 * @return Returns the fieldDelimiter.
	 */
	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	/**
	 * @param fieldDelimiter The fieldDelimiter to set.
	 */
	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}
	
	public Object getObject(){
		return this;
	}
	
	public void setPropertyValues(Object object){
		if(object instanceof CsvExportParametersBean){
			this.setFieldDelimiter(((CsvExportParametersBean)object).getFieldDelimiter());
		}
	}
	
}
