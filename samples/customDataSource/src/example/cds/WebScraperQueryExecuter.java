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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;

/**
 * @author bob
 *
 */
public class WebScraperQueryExecuter extends JRAbstractQueryExecuter {

	private String path;
	private String url;

	/**
	 * @param dataset
	 * @param parameters
	 */
	public WebScraperQueryExecuter(JRDataset dataset, Map parameters) {
		super(dataset, parameters);
		parseQuery();
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuter#cancelQuery()
	 */
	public boolean cancelQuery() throws JRException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuter#close()
	 */
	public void close() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.query.JRQueryExecuter#createDatasource()
	 */
	public JRDataSource createDatasource() throws JRException {
		String[] qarray = getQueryString().trim().split(" ");
		url = qarray[0];
		if (qarray.length > 1) {
			path = qarray[1];
		}
		return new WebScraperDataSource(url, path, new HashMap());
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.query.JRAbstractQueryExecuter#getParameterReplacement(java.lang.String)
	 */
	protected String getParameterReplacement(String parameterName) {
		// TODO Auto-generated method stub
		return null;
	}

}
