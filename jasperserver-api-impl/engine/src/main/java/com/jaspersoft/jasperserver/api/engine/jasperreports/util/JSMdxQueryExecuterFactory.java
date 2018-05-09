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

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JREmptyQueryExecuter;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.olap.JRMdxQueryExecuterFactory;
import net.sf.jasperreports.olap.JRMondrianQueryExecuter;
import net.sf.jasperreports.olap.JRMondrianQueryExecuterFactory;
import net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JSMdxQueryExecuterFactory extends JRMdxQueryExecuterFactory {
	
	private static final Log log = LogFactory.getLog(JSMdxQueryExecuterFactory.class);

	@Override
	public JRQueryExecuter createQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset, Map<String, ? extends JRValueParameter> parameters)
			throws JRException {
		JRQueryExecuter queryExecuter;
		if (getParameterValue(parameters, 
				JRMondrianQueryExecuterFactory.PARAMETER_MONDRIAN_CONNECTION) != null) {
			queryExecuter = new JRMondrianQueryExecuter(jasperReportsContext, dataset, parameters);
		}
		else if (getParameterValue(parameters, 
				JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL) != null) {
			//creating a JS XMLA query executer
			queryExecuter = new JSXmlaQueryExecuter(jasperReportsContext, dataset, parameters);
		}
		else {
			log.warn("No Mondrian connection or XMLA URL set for MDX query");
			queryExecuter = new JREmptyQueryExecuter();
		}
		return queryExecuter;
	}

}
