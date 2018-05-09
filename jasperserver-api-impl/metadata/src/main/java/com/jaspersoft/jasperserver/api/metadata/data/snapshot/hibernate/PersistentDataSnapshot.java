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
package com.jaspersoft.jasperserver.api.metadata.data.snapshot.hibernate;

import java.util.Date;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class PersistentDataSnapshot {

	private static final int VERSION_NEW = -1;
	
	private long id;
	private int version;
    private Date snapshotDate;
    private long contentsId;
    private Map<String, Object> dataParameters;
    
    public PersistentDataSnapshot() {
    	this.version = VERSION_NEW;
    }
    
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public Date getSnapshotDate() {
		return snapshotDate;
	}
	
	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

	public Map<String, Object> getDataParameters() {
		return dataParameters;
	}

	public void setDataParameters(Map<String, Object> dataParameters) {
		this.dataParameters = dataParameters;
	}

	public long getContentsId() {
		return contentsId;
	}

	public void setContentsId(long contentsId) {
		this.contentsId = contentsId;
	}
	
}
