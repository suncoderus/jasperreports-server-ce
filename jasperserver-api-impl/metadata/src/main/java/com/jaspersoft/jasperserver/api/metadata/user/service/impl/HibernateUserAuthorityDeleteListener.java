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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoRole;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoUser;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityEventListener;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityEventListenerRegistry;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class HibernateUserAuthorityDeleteListener implements HibernateDeleteListener, UserAuthorityEventListenerRegistry {
	
	private List listeners;

	public List getListeners() {
		return listeners;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}

	protected void ensureListeners() {
		if (listeners == null) {
			listeners = new ArrayList();
		}
	}

	public void registerListener(UserAuthorityEventListener listener) {
		ensureListeners();
		listeners.add(listener);
	}

	public void deregisterListener(UserAuthorityEventListener listener) {
		ensureListeners();
		listeners.remove(listener);
	}

	public void onDelete(Object o) {
		if (o instanceof RepoUser) {
			RepoUser user = (RepoUser) o;
			final String username = user.getUsername();
			fireListeners(new ListenerVisitor() {
				public void visit(UserAuthorityEventListener listener) {
					listener.onUserDelete(username);
				}
			});
		} else if (o instanceof RepoRole) {
			RepoRole role = (RepoRole) o;
			final String roleName = role.getRoleName();
			fireListeners(new ListenerVisitor() {
				public void visit(UserAuthorityEventListener listener) {
					listener.onRoleDelete(roleName);
				}
			});
		}
	}

	protected static interface ListenerVisitor {
		void visit(UserAuthorityEventListener listener);
	}
	
	protected void fireListeners(ListenerVisitor visitor) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				UserAuthorityEventListener listener = (UserAuthorityEventListener) it.next();
				visitor.visit(listener);
			}
		}
	}

}
