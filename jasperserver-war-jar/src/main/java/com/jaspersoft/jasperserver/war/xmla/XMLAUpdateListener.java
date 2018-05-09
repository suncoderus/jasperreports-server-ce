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

package com.jaspersoft.jasperserver.war.xmla;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.jaspersoft.jasperserver.api.metadata.olap.service.UpdatableXMLAContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.olap.domain.MondrianXMLADefinition;

/**
 * @author asokolnikov
 * @version $Id$
 */
public class XMLAUpdateListener {

	private Set registeredListeners = new LinkedHashSet();
	
	private static final Log log = LogFactory.getLog(XMLAUpdateListener.class);
	
	public void registerListener(UpdatableXMLAContainer updatableXMLAContainer) {
		if (updatableXMLAContainer != null) {
			registeredListeners.add(updatableXMLAContainer);
		}
	}
	
	public boolean unregisterListener(UpdatableXMLAContainer updatableXMLAContainer) {
		if (updatableXMLAContainer != null) {
			return registeredListeners.remove(updatableXMLAContainer);
		}
		return false;
	}
	
	public void notifyListeners(MondrianXMLADefinition oldDef, MondrianXMLADefinition newDef) {
		for (Iterator iter = registeredListeners.iterator(); iter.hasNext(); ) {
			UpdatableXMLAContainer updatableXMLAContainer = (UpdatableXMLAContainer) iter.next();
			try {
				updatableXMLAContainer.updateXMLAConnection(oldDef, newDef);
			} catch (Exception ex) {
				log.error("Cannot update XMLA Connection!", ex);
			}
		}
	}
	
}
