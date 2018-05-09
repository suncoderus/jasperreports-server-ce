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

package com.jaspersoft.jasperserver.export.modules.common;

import org.exolab.castor.types.AnyNode;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ReportParameterValueBean {
	
	public static final String VALUE_TYPE_SINGLE = "single";

	private Object name;
	private Object[] values;
	private String valueType;

	public ReportParameterValueBean() {
	}
	
	public ReportParameterValueBean(String name, Object[] values) {
		this(name, values, null);
	}
	
	public ReportParameterValueBean(String name, Object[] values, String valueType) {
		this.name = name;
		this.values = values;
		this.valueType = valueType;
	}
	
	public Object getName() {
		return name;
	}
	
	public void setName(Object name) {
		this.name = name;
	}
	
	public Object[] getValues() {
        return values;
	}
	
	public void setValues(Object[] values) {
		//workaround for http://jira.codehaus.org/browse/CASTOR-1887
        //and JRS Bugzilla bug 30523
		if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				if (value instanceof AnyNode) {
					AnyNode node = (AnyNode) value;
					Object dateValue = DefaultReportParametersTranslator.parseDateNode(node);
					if (dateValue != null) {
						values[i] = dateValue;
					}
				}
			}
		}

		this.values = values;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	
	public boolean isSingleValue() {
		return valueType != null && valueType.equals(VALUE_TYPE_SINGLE);
	}
}
