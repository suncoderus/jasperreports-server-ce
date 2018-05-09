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

package com.jaspersoft.jasperserver.war;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tonbeller.jpivot.core.Model;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.OlapModelDecorator;
import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.jpivot.mondrian.MondrianDrillThroughTableModel;
import com.tonbeller.wcf.table.EditableTableComponent;


/**
 * @author sbirney
 * @revision $Id: CSVServlet.java 8441 2007-05-30 18:55:04Z sbirney $
 */

public class CSVServlet extends HttpServlet {

    public void service(HttpServletRequest req,
			HttpServletResponse resp)
	throws ServletException
    {
	try {
	    resp.setContentType(MIME_TYPE);
        resp.setHeader("Pragma", "");
        resp.setHeader("Cache-Control", "no-store");
	    //resp.setContentType(HTML_TYPE); // for testing
	    PrintWriter out = resp.getWriter();
	    printQuery( getDrillThroughSQL(req), getConnection(req), out );
	} catch (Exception e) {
	    e.printStackTrace();
	    log.error(e);
	}
    }

    private static final Log log = LogFactory.getLog(CSVServlet.class);

    protected static final String MIME_TYPE = "text/comma-separated-values";
    protected static final String HTML_TYPE = "text/html";
    protected static final String SEP = ",";
    protected static final String NULL_VALUE = "";
    protected static final String NEWLINE = "\r\n";

    private MondrianDrillThroughTableModel
	getDrillThroughModel(HttpServletRequest req)
    {
	HttpSession session = req.getSession();
	OlapModel olapModel = (OlapModel)session.getAttribute("olapModel");
	Model mdl = ((OlapModelDecorator) olapModel).getRootModel();
	String currentView = (String) session.getAttribute("currentView");
	// only MondrianModel supports Drillthru
	if (mdl instanceof MondrianModel) {
	    try {
		if (currentView != null) {
		    EditableTableComponent et = (EditableTableComponent) session
			.getAttribute(currentView + ".drillthroughtable");
		    if (et != null) {
			return (MondrianDrillThroughTableModel) et.getModel();
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
		log.error(e.getStackTrace());
	    }
	}
	return null;
    }

    private Connection getConnection(HttpServletRequest req)
	throws SQLException
    {
	MondrianDrillThroughTableModel model = getDrillThroughModel(req);
	if (model.getDataSourceName() == null) {
	    return DriverManager.getConnection(model.getJdbcUrl(),
					       model.getJdbcUser(),
					       model.getJdbcPassword());
	} else {
	    return getDataSource(req).getConnection();
	}
    }

    private DataSource getDataSource(HttpServletRequest req) {
	MondrianDrillThroughTableModel model = getDrillThroughModel(req);
	String dataSourceName = model.getDataSourceName();
	try {
	    return (DataSource) getJndiContext().lookup(dataSourceName);
	} catch (NamingException e) {
	    e.printStackTrace();
	    log.error(e);
	}
	return null;
    }

    private Context jndiContext;
    private Context getJndiContext() throws NamingException {
	if (jndiContext == null) {
	    jndiContext = new InitialContext();
	}
	return jndiContext;
    }

    /*
      private Connection getConnectionFromOlapUnit(HttpServletRequest req)
      throws SQLException
      {
      HttpSession session = context.getRequest().getSession();
      OlapUnit olapUnit = (OlapUnit) session.getAttribute("olapUnit");

      }
    */

    private String getDrillThroughSQL(HttpServletRequest req)
    {
	MondrianDrillThroughTableModel model = getDrillThroughModel(req);
	return model.getSql();
    }

    private void printQuery(String sqlQuery,
			    Connection conn,
			    PrintWriter out)
    {
	log.info("drill-through SQL = " + sqlQuery);
	try {
	    Statement s = conn.createStatement();
	    ResultSet rs = s.executeQuery(sqlQuery);
	    printCSV(rs, out);
	    rs.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    log.error(e);
	} finally {
	    try {
		conn.close();
	    } catch (SQLException sqle) {
		sqle.printStackTrace();
		log.error(sqle);
	    }
	}
    }

    private void printCSV(ResultSet rs,
			  PrintWriter out)
	throws Exception
    {
	ResultSetMetaData md = rs.getMetaData();
	int numCols = md.getColumnCount();

	// print column headers
	for (int i=1; i<numCols; i++) {
	    out.write(quoteString(md.getColumnName(i)));
	    out.write(SEP);
	}
	out.write(quoteString(md.getColumnName(numCols)));
	out.write(NEWLINE);

	// print row data
	while (rs.next()) {
	    for (int i=1; i<numCols; i++) {
		out.write(quoteString("" + rs.getObject(i)));
		out.write(SEP);
	    }
	    out.write(quoteString("" + rs.getObject(numCols)));
	    out.write(NEWLINE);
	}
    }

    private String quoteString(String s) {
	s = s.replaceAll("\"", "\"\"");
	s = "\"" + s + "\"";
	return s;
    }

}
