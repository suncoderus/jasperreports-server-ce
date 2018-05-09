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

import java.util.Map;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory;
import net.sf.jasperreports.engine.query.JRQueryExecuter;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 * @version $Id
 */
public class JRTimezoneJdbcQueryExecuterFactory extends JRJdbcQueryExecuterFactory
{

	public static final String PARAMETER_TIMEZONE = "DATABASE_TIMEZONE";

	private final static Object[] BUILTIN_PARAMETERS = {PARAMETER_TIMEZONE,  TimeZone.class};

	public Object[] getBuiltinParameters()
	{
		return BUILTIN_PARAMETERS;
	}

	public JRQueryExecuter createQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset, Map<String, ? extends JRValueParameter> parameters) throws JRException
	{
		return new JRTimezoneJdbcQueryExecuter(jasperReportsContext, dataset, parameters);
	}

}
