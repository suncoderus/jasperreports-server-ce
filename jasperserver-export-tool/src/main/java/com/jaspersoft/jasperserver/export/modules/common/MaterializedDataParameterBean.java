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

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class MaterializedDataParameterBean {
	
	private Object parameterValue;
	private Object effectiveValue;

	public MaterializedDataParameterBean() {
	}

	public MaterializedDataParameterBean(Object parameterValue, Object effectiveValue) {
		this.parameterValue = parameterValue;
		this.effectiveValue = effectiveValue;
	}

	public Object getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(Object parameterValue) {
		this.parameterValue = parameterValue;
	}

	public Object getEffectiveValue() {
		return effectiveValue;
	}

	public void setEffectiveValue(Object effectiveValue) {
		this.effectiveValue = effectiveValue;
	}

}
