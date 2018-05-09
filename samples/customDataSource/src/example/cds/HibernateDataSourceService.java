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

import net.sf.jasperreports.engine.query.JRHibernateQueryExecuterFactory;

import org.hibernate.Session;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;

/**
 * this is an implementation of a hibernate data source that can use Hibernate session factories 
 * defined in the Spring application context. It uses the HibernateSessionFactoryFinder to look them up.
 * @author bob
 *
 */
public class HibernateDataSourceService implements ReportDataSourceService {
	private HibernateSessionFactoryFinder sessionFactoryFinder;
	private String sessionFactoryName;
    private Session session;

    public void closeConnection() {
    	if (session != null) {
    		session.close();
    	}
    }

    public void setReportParameterValues(Map parameters) {
    	session = sessionFactoryFinder.getSession(sessionFactoryName);
        session.beginTransaction();
        parameters.put(JRHibernateQueryExecuterFactory.PARAMETER_HIBERNATE_SESSION , session);
    }

	public HibernateSessionFactoryFinder getSessionFactoryFinder() {
		return sessionFactoryFinder;
	}

	public void setSessionFactoryFinder(HibernateSessionFactoryFinder sessionFactoryFinder) {
		this.sessionFactoryFinder = sessionFactoryFinder;
	}

	public String getSessionFactoryName() {
		return sessionFactoryName;
	}

	public void setSessionFactoryName(String sessionFactoryName) {
		this.sessionFactoryName = sessionFactoryName;
	}
}
