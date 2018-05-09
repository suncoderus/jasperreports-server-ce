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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListenerRegistry;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoFolder;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class HibernateResourceDeleteListener implements HibernateDeleteListener, RepositoryEventListenerRegistry {
	
	private ResourceFactory persistentClassMappings;
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
	
	public void registerListener(RepositoryEventListener listener) {
		ensureListeners();
		this.listeners.add(listener);
	}
	
	public void deregisterListener(RepositoryEventListener listener) {
		ensureListeners();
		this.listeners.remove(listener);
	}

	public ResourceFactory getPersistentClassMappings() {
		return persistentClassMappings;
	}

	public void setPersistentClassMappings(ResourceFactory persistentClassMappings) {
		this.persistentClassMappings = persistentClassMappings;
	}

	public void onDelete(Object o) {
		if (o instanceof RepoResource) {
			fireListeners((RepoResource) o);
		} else if (o instanceof RepoFolder) {
			fireListeners((RepoFolder) o);
		}
	}

	protected void fireListeners(RepoResource resource) {
		if (listeners != null && !listeners.isEmpty()) {
			Class resourceItf = persistentClassMappings.getInterface(resource.getClass());

			//FIXME null Itf for RU datasources
			if (resourceItf != null) {
				String resourceURI = resource.getResourceURI();
				for (Iterator it = listeners.iterator(); it.hasNext();) {
					RepositoryEventListener listener = (RepositoryEventListener) it.next();
					fireListener(listener, resourceItf, resourceURI);
				}
			}
		}
	}

	protected void fireListener(RepositoryEventListener listener, Class resourceItf, String resourceURI) {
		listener.onResourceDelete(resourceItf, resourceURI);
	}

	protected void fireListeners(RepoFolder folder) {
		if (listeners != null && !listeners.isEmpty()) {
			String folderURI = folder.getResourceURI();
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				RepositoryEventListener listener = (RepositoryEventListener) it.next();
				fireListener(listener, folderURI);
			}
		}
	}

	protected void fireListener(RepositoryEventListener listener, String folderURI) {
		listener.onFolderDelete(folderURI);
	}

}
