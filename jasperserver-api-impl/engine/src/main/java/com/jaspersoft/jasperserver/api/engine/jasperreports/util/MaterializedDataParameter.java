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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.Serializable;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class MaterializedDataParameter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Object parameterValue;
	private final Object effectiveValue;

	public MaterializedDataParameter(Object parameterValue, Object effectiveValue) {
		this.parameterValue = parameterValue;
		this.effectiveValue = effectiveValue;
	}

	@Override
	public int hashCode() {
		return effectiveValue == null ? 0 : effectiveValue.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MaterializedDataParameter)) {
			return false;
		}
		
		Object otherEffectiveValue = ((MaterializedDataParameter) obj).effectiveValue;
		return effectiveValue == null ? otherEffectiveValue == null 
				: (otherEffectiveValue != null && effectiveValue.equals(otherEffectiveValue));
	}

	public Object getParameterValue() {
		return parameterValue;
	}

	public Object getEffectiveValue() {
		return effectiveValue;
	}

}
