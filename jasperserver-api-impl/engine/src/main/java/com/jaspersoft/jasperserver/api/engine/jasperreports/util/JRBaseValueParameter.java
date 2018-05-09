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

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRValueParameter;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JRBaseValueParameter extends JRParameterDecorator implements JRValueParameter
{
	private Object value;

	public JRBaseValueParameter(JRParameter parameter, Object value)
	{
		super(parameter);
		
		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value) 
	{
		this.value = value;
	}
}
