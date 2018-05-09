/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package example.cds;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactory;

/**
 * @author bob
 *
 */
public class WebScraperQueryExecuterFactory implements JRQueryExecuterFactory {

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuterFactory#createQueryExecuter(net.sf.jasperreports.engine.JRDataset, java.util.Map)
	 */
	public JRQueryExecuter createQueryExecuter(JRDataset dataset, Map parameters)
			throws JRException {
		// TODO Auto-generated method stub
		return new WebScraperQueryExecuter(dataset, parameters);
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuterFactory#getBuiltinParameters()
	 */
	public Object[] getBuiltinParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuterFactory#supportsQueryParameterType(java.lang.String)
	 */
	public boolean supportsQueryParameterType(String className) {
		// TODO Auto-generated method stub
		return true;
	}

}
