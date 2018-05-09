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

import com.jaspersoft.jasperserver.war.control.OlapModelController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 *
 * @author swood
 */
public class OlapSessionKiller implements HttpSessionListener {
    private static final Logger log = Logger.getLogger(OlapSessionKiller.class);

    public void sessionCreated(HttpSessionEvent arg0) {
        // Not needed
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();

        // destroy the Olap connections
        for ( Enumeration coll = session.getAttributeNames(); coll.hasMoreElements(); ) {
            try {
                String attName = (String) coll.nextElement();
                Object att = session.getAttribute(attName);
                OlapModelController.clearDrillThroughConnection(attName, att);
            } catch (Exception ex) {
                // don't care
            }
        }

        if (session.getAttribute("olapModels") != null) {

            Collection currentUnits = new ArrayList(((Map) session.getAttribute("olapModels")).keySet());

            for (Object o : currentUnits) {
                String unitUri = (String) o;

                try {
                    OlapModelController.clearOlapSession(session, unitUri);
                } catch (Exception ex) {
                    // don't care
                }
            }

            session.removeAttribute("olapModels");
            log.debug("Removed olapModels");
        }

        if (session.getAttribute("drillthrough") != null) {
            session.removeAttribute("drillthrough");
        }

        if (session.getAttribute("drillThroughTableModel") != null) {
            session.removeAttribute("drillThroughTableModel");
        }

        if (session.getAttribute("inDrillthrough") != null) {
            session.removeAttribute("inDrillthrough");
        }

        if (session.getAttribute("currentView") != null) {
            session.removeAttribute("currentView");
        }

        if (session.getAttribute("olapUnit") != null) {
            session.removeAttribute("olapUnit");
        }

        if (log.isDebugEnabled()) {
            log.debug("Remaining session attributes");
            Enumeration attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attribute = (String) attributeNames.nextElement();
                log.debug("\t" + attribute);
            }
        }
    }

}
