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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class MapNameMethodAttributes implements ReattemptAttributes {

	private Map methodAttributes;
	
	public ReattemptMethodAttributes getMethodAttributes(Method method) {
		String name = method.getName();
		return (ReattemptMethodAttributes) methodAttributes.get(name);
	}
	
	public Map getMethodAttributes() {
		return methodAttributes;
	}

	public void setMethodAttributes(Map methodAttributes) {
		this.methodAttributes = methodAttributes;
	}

}
