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
package com.jaspersoft.jasperserver.api.metadata.data.cache;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class DefaultDataSnapshotPersistentMetadata implements
		DataSnapshotPersistentMetadata {

	private DataCacheSnapshotMetadata metadata;
	private int version;
	private long contentsId;
	
	public DataCacheSnapshotMetadata getSnapshotMetadata() {
		return metadata;
	}

	public void setSnapshotMetadata(DataCacheSnapshotMetadata metadata) {
		this.metadata = metadata;
	}

	public long getContentsId() {
		return contentsId;
	}

	public void setContentsId(long contentsId) {
		this.contentsId = contentsId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
