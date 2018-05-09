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

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lucian Chirita
 *
 */
public class HibernateCompositeInterceptor extends EmptyInterceptor {
	
	private List listeners;

	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		fireDeleteListeners(entity);
	}
	
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		fireSaveListeners(entity);
		return false;
	}


    @Override
    public String onPrepareStatement(String sql) {
        // See RepoResourceItemBase.hbm.xml
        // RepoResourceItemBase uses union subclass strategy to combine resources and folders from JIResource and JIResourceFolder
        // Fake tables required to set composite id on existing database schema (composite ids required for identity-based
        // databases, such as MySQL in order to distinguish resources and folders with same id)
        // The RepoResourceItemBase.hbm.xml file is excluded from DDL generation.
        return super.onPrepareStatement(sql)
                .replace("FakeJIResource", "JIResource")
                .replace("FakeJIResourceFolder", "JIResourceFolder");
    }

    protected void fireDeleteListeners(Object deleted) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				Object listener = it.next();
				if (listener instanceof HibernateDeleteListener) {
					fireListener((HibernateDeleteListener) listener, deleted);
				}
			}
		}
	}

	protected void fireSaveListeners(Object saved) {
		if (listeners != null && !listeners.isEmpty()) {
			for (Iterator it = listeners.iterator(); it.hasNext();) {
				Object listener = it.next();
				if (listener instanceof HibernateSaveListener) {
					fireListener((HibernateSaveListener) listener, saved);
				}
			}
		}
	}

	protected void fireListener(HibernateDeleteListener listener, Object deleted) {
		listener.onDelete(deleted);
	}

	protected void fireListener(HibernateSaveListener listener, Object saved) {
		listener.onSave(saved);
	}

	public List getListeners() {
		return listeners;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}

}
