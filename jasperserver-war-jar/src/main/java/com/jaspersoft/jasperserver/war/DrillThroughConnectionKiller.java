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
import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author swood
 */
public class DrillThroughConnectionKiller implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent arg0) {
        // Not needed
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();

        // destroy the drill through JDBC connections
        for ( Enumeration coll = session.getAttributeNames(); coll.hasMoreElements(); ) {
            try {
                String attName = (String) coll.nextElement();
                Object att = session.getAttribute(attName);
                OlapModelController.clearDrillThroughConnection(attName, att);
            } catch (Exception ex) {
                // don't care
            }
        }
    }

}
