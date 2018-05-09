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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.RepositoryContextManager;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.util.LocalLockManager;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockHandle;
import com.jaspersoft.jasperserver.api.metadata.common.util.LockManager;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class RepositoryCacheMap {

	private static final Log log = LogFactory.getLog(RepositoryCacheMap.class); 
	
	public static interface ObjectCache {
		boolean isValid(Object o);
		Object create(ExecutionContext context, FileResource res);
		void release(Object o);
	}

	protected static class VersionObject {
		public final Object o;
		public final String referenceURI;
		public final int version;

		public VersionObject(Object o, int version) {
			this.o = o;
			this.referenceURI = null;
			this.version = version;
		}

		public VersionObject(String referenceURI, int version) {
			this.o = null;
			this.referenceURI = referenceURI;
			this.version = version;
		}
		
		public boolean isReference() {
			return referenceURI != null;
		}
	}
	
	public static class CacheObject {
		private final Object o;
		private final boolean cached;
		
		public CacheObject(Object o, boolean cached) {
			this.o = o;
			this.cached = cached;
		}
		
		public Object getObject() {
			return o;
		}
		
		public boolean isCached() {
			return cached;
		}
	}

	private final RepositoryService repository;
	private final RepositoryContextManager repositoryContextManager;
	private final ObjectCache objectCache;
	private final Map map;
	
	// a lock manager to prevent race conditions
	private LockManager lockManager;
	
	public RepositoryCacheMap(RepositoryService repository, 
			RepositoryContextManager repositoryContextManager, 
			ObjectCache objectCache) {
		this.repository = repository;
		this.repositoryContextManager = repositoryContextManager;
		this.objectCache = objectCache;
		map = Collections.synchronizedMap(new HashMap());
		
		lockManager = new LocalLockManager();
	}

	public CacheObject cache(ExecutionContext context, FileResource res, boolean cacheFirst) {
		String resourcePathKey = getResourcePathKey(res);

		LockHandle lock = lockManager.lock(getLockName(), resourcePathKey);
		try {
			VersionObject val = null;
			if (cacheFirst) {
				if (log.isDebugEnabled()) {
					log.debug("Searching cache for " + resourcePathKey + ", version " + res.getVersion());
				}
				
				val = (VersionObject) map.get(resourcePathKey);

				if (log.isDebugEnabled()) {
					if (val == null) {
						log.debug("Cache doesn't contain " + resourcePathKey);
					} else {
						log.debug("Found in cache " + resourcePathKey + " with version " + val.version);
					}
				}
			}
			
			Object ret;
			boolean cached = true;
			if (val != null && val.version >= res.getVersion() && (val.isReference() || objectCache.isValid(val.o))) {
				ret = val.o;
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Creating cached object for " + resourcePathKey + ", version " + res.getVersion());
				}
				
				if (res.isReference()) {
					FileResource ref = (FileResource) repository.getResource(context, res.getReferenceURI());
					CacheObject refCache = cache(context, ref, true);
					ret = refCache.getObject();
					if (cacheFirst) {
						VersionObject value = new VersionObject(res.getReferenceURI(), res.getVersion());
						put(resourcePathKey, value);
					}
				} else {
					ret = objectCache.create(context, res);
					if (cacheFirst) {
						VersionObject value = new VersionObject(ret, res.getVersion());
						put(resourcePathKey, value);
					} else {
						cached = false;
					}
				}
			}

			return new CacheObject(ret, cached);
		} finally {
			lockManager.unlock(lock);
		}
	}

	private String getLockName() {
		return "RepositoryCacheMap_" + System.identityHashCode(this);
	}

	protected String getResourcePathKey(FileResource res) {
		String resourceUri = res.getURIString();
		String resourcePathKey = repositoryContextManager.getRepositoryPathKey(
				resourceUri);
		return resourcePathKey;
	}

	public void remove(String resourceURI) {
		if (log.isDebugEnabled()) {
			log.debug("Removing from cache " + resourceURI);
		}
		
		map.remove(resourceURI);
	}
	
	protected void put(String pathKey, VersionObject value) {
		VersionObject old = (VersionObject) map.put(pathKey, value);
		if (old != null && !old.isReference()) {
			// TODO the old object can be still in use
			objectCache.release(old.o);
		}
	}
	
	public void release() {
		for (Iterator it = map.values().iterator(); it.hasNext();) {
			VersionObject val = (VersionObject) it.next();
			if (!val.isReference()) {
				objectCache.release(val.o);
			}
		}
	}

	protected void finalize() throws Throwable {
		release();
	}
}
