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

import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshot;
import com.jaspersoft.jasperserver.api.metadata.data.cache.DataCacheSnapshotMetadata;

import net.sf.jasperreports.data.cache.DataSnapshot;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DefaultDataCacheSnapshot implements DataCacheSnapshot {

	private final DataSnapshot snapshot;
	private final DataCacheSnapshotMetadata metadata;
	
	public DefaultDataCacheSnapshot(DataSnapshot snapshot, DataCacheSnapshotMetadata metadata) {
		this.snapshot = snapshot;
		this.metadata = metadata;
	}

	public DataSnapshot getSnapshot() {
		return snapshot;
	}

	public DataCacheSnapshotMetadata getMetadata() {
		return metadata;
	}

	public boolean isSaved() {
		return false;
	}

}
