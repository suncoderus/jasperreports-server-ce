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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.engine.EntityEntry;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.EventSource;
import org.hibernate.event.def.DefaultDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class HibernateCompositeDeleteListener extends DefaultDeleteEventListener {
	
	private List listeners;

	public List getListeners() {
		return listeners;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}
	
	protected void cascadeBeforeDelete(EventSource session, EntityPersister persister, Object entity, EntityEntry entityEntry, Set transientEntities) throws HibernateException {
		super.cascadeBeforeDelete(session, persister, entity, entityEntry, transientEntities);
		
		fireBeforeListeners(new DeleteEvent(entity, session));
	}

	protected void fireBeforeListeners(DeleteEvent event) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				Object listener = it.next();
				if (listener instanceof HibernateBeforeDeleteListener) {
					fireListener((HibernateBeforeDeleteListener) listener, event);
				}
			}
		}
	}

	protected void fireListener(HibernateBeforeDeleteListener listener, DeleteEvent event) {
		listener.beforeDelete(event);
	}

}
