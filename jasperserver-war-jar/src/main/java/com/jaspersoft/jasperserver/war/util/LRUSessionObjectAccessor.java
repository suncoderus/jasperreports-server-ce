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

package com.jaspersoft.jasperserver.war.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.WebUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class LRUSessionObjectAccessor implements SessionObjectSerieAccessor {

	protected static final Log log = LogFactory.getLog(LRUSessionObjectAccessor.class);
	
	private String listSessionName;
	private int maxSize;
	private SessionObjectSeriesListener listener;
	
	private AtomicInteger idCounter = new AtomicInteger();

	protected static class ObjectSerie extends LinkedHashMap implements SessionObjectSeries, 
			HttpSessionBindingListener, HttpSessionActivationListener {
		
		private static final long serialVersionUID = 1L;
		
		private final int maxSize;
		private final SessionObjectSeriesListener listener;

		public ObjectSerie(int maxSize, SessionObjectSeriesListener listener) {
			super();
			this.maxSize = maxSize;
			this.listener = listener;
		}

		protected boolean removeEldestEntry(Entry entry) {
			boolean remove = size() > maxSize;
			
			if (remove && log.isDebugEnabled()) {
				log.debug("Automatically removing object with name " + entry.getKey());
			}
			
			return remove;
		}

		public void valueBound(HttpSessionBindingEvent event) {
			if (listener != null) {
				listener.objectSeriesBound(event, this);
			}
		}

		public void valueUnbound(HttpSessionBindingEvent event) {
			if (listener != null) {
				listener.objectSeriesUnbound(event, this);
			}
		}

		public void sessionDidActivate(HttpSessionEvent se) {
			if (listener != null) {
				listener.objectSeriesDidActivate(se, this);
			}
		}

		public void sessionWillPassivate(HttpSessionEvent se) {
			if (listener != null) {
				listener.objectSeriesWillPassivate(se, this);
			}
		}

		public List getValues() {
			Collection values = values();
			return new ArrayList(values);
		}
	}
	
	public String putObject(HttpServletRequest request, Object object) {
		ObjectSerie objectSerie = getObjectSerie(request);
		String name = createName(object);
		
		if (log.isDebugEnabled()) {
			log.debug(listSessionName + " putting object " + object + " with name " + name);
		}

		synchronized (objectSerie) {
			objectSerie.put(name, object);
		}

		return name;
	}

	public Object getObject(HttpServletRequest request, String name) {
		ObjectSerie objectSerie = getObjectSerie(request);
		synchronized (objectSerie) {
			Object object = objectSerie.get(name);
			return object;
		}
	}

	public Object removeObject(HttpServletRequest request, String name) {
		if (log.isDebugEnabled()) {
			log.debug(listSessionName + " removing object with name " + name);
		}
		
		ObjectSerie objectSerie = getObjectSerie(request);
		synchronized (objectSerie) {
			return objectSerie.remove(name);
		}
	}

	public SessionObjectSeries getSeries(HttpSession session) {
		return getObjectSerie(session);
	}
	
	protected ObjectSerie getObjectSerie(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return getObjectSerie(session);
	}

	protected ObjectSerie getObjectSerie(HttpSession session) {
		Object mutex = WebUtils.getSessionMutex(session);
		ObjectSerie serie;
		boolean created = false;
		synchronized (mutex) {
			serie = (ObjectSerie) session.getAttribute(getListSessionName());
			if (serie == null) {
				created = true;
				serie = new ObjectSerie(getMaxSize(), getListener());
				session.setAttribute(getListSessionName(), serie);
			}
		}
		
		if (created && log.isDebugEnabled()) {
			log.debug(listSessionName + " created object serie " + serie + " for session " + session.getId());
		}
		
		return serie;
	}
	
	protected String createName(Object object) {
		return System.identityHashCode(object) + "_" + System.currentTimeMillis()
				+ "_" + idCounter.getAndIncrement();
	}

	public String getListSessionName() {
		return listSessionName;
	}

	public void setListSessionName(String listSessionName) {
		this.listSessionName = listSessionName;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public SessionObjectSeriesListener getListener() {
		return listener;
	}

	public void setListener(SessionObjectSeriesListener listener) {
		this.listener = listener;
	}

}
