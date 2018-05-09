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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JndiJdbcDataSourceService extends BaseJdbcDataSource {
	
	private static final Log log = LogFactory.getLog(JndiJdbcDataSourceService.class);

	private final String jndiName;
	
	public JndiJdbcDataSourceService(String jndiName) {
		this.jndiName = jndiName;
	}

    public boolean testConnection() throws Exception {
        Context ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/" + jndiName);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return conn != null;
        } finally {
            if(conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Couldn't disconnect JNDI connection", e);
                }
        }
    }

	protected Connection createConnection() {

		try
		{
            Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + jndiName);
            Connection c = ds.getConnection();
            if (log.isDebugEnabled()) {
                log.debug("CreateConnection successful at com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JndiJdbcDataSourceService.createConnection");
            }
                return c;
            }
		catch (NamingException e)
		{
			try {
                //Added as short time solution due of http://bugzilla.jaspersoft.com/show_bug.cgi?id=26570.
                //The main problem - this code executes in separate tread (non http).
                //Jboss 7 support team recommend that you use the non-component environment namespace for such situations.
                Context ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup(jndiName);
                Connection c = ds.getConnection();
                if (log.isDebugEnabled()) {
                    log.debug("CreateConnection successful at com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.JndiJdbcDataSourceService.createConnection");
                }
                return c;

            }  catch (NamingException ex) {
                if (log.isDebugEnabled())
                    log.debug(e, e);
                throw new JSExceptionWrapper(e);
            } catch (SQLException ex) {
                if (log.isDebugEnabled())
                    log.debug(e, e);
                throw new JSExceptionWrapper(e);                
            }

		}
		catch (SQLException e)
		{
			if (log.isDebugEnabled())
				log.debug(e, e);
			throw new JSExceptionWrapper(e);
		}
	}

}
