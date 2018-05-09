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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.jasperreports.data.cache.DataSnapshot;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.jasperreports.service.DataSnapshotCachingService;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataSnapshotPersistentMetadata;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataSnapshotSavedId;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DefaultDataSnapshotPersistentMetadata;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DataSnapshotEhcacheService implements DataSnapshotCachingService {

	private Ehcache metadataCache;
	private Ehcache contentsCache;

	public DataSnapshotPersistentMetadata getSnapshotMetadata(ExecutionContext context, long snapshotId) {
		Element element = metadataCache.get(snapshotId);
		if (element == null) {
			return null;
		}
		
		DataSnapshotPersistentMetadata metadata = (DataSnapshotPersistentMetadata) element.getObjectValue();
		return metadata;
	}

	public void putSnapshotMetadata(ExecutionContext context, 
			long snapshotId, DataSnapshotPersistentMetadata metadata) {
		Element element = new Element(snapshotId, metadata);
		metadataCache.put(element);		
	}

	public DataSnapshot getSnapshotContents(ExecutionContext context, long contentsId) {
		Element element = contentsCache.get(contentsId);
		if (element == null) {
			return null;
		}
		
		DataSnapshot snapshot = (DataSnapshot) element.getObjectValue();
		return snapshot;
	}

	public void putSnapshotContents(ExecutionContext context, 
			long contentsId, DataSnapshot dataSnapshot) {
		Element element = new Element(contentsId, dataSnapshot);
		contentsCache.put(element);		
	}

	public void put(ExecutionContext context, DataSnapshotSavedId savedId, DataCacheSnapshot snapshot) {
		// put the metadata
		DefaultDataSnapshotPersistentMetadata persistentMetadata = new DefaultDataSnapshotPersistentMetadata();
		persistentMetadata.setSnapshotMetadata(snapshot.getMetadata());
		persistentMetadata.setVersion(savedId.getVersion());
		persistentMetadata.setContentsId(savedId.getContentsId());
		putSnapshotMetadata(context, savedId.getSnapshotId(), persistentMetadata);
		
		// put the contents
		putSnapshotContents(context, savedId.getContentsId(), snapshot.getSnapshot());
	}

	public void invalidateSnapshot(ExecutionContext context, long snapshotId) {
		Element element = metadataCache.get(snapshotId);
		if (element == null) {
			// nothing to do
			return;
		}
		
		DataSnapshotPersistentMetadata metadata = (DataSnapshotPersistentMetadata) element.getObjectValue();
		// remove from the two caches
		metadataCache.remove(snapshotId);
		contentsCache.remove(metadata.getContentsId());
	}

	public Ehcache getMetadataCache() {
		return metadataCache;
	}

	public void setMetadataCache(Ehcache metadataCache) {
		this.metadataCache = metadataCache;
	}

	public Ehcache getContentsCache() {
		return contentsCache;
	}

	public void setContentsCache(Ehcache contentsCache) {
		this.contentsCache = contentsCache;
	}

}
